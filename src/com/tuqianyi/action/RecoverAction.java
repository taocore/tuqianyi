package com.tuqianyi.action;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.taobao.api.TaobaoResponse;
import com.taobao.api.response.ItemGetResponse;
import com.taobao.api.response.ItemImgUploadResponse;
import com.tuqianyi.db.DBUtils;
import com.tuqianyi.db.Dao;
import com.tuqianyi.image.ImageUtils;
import com.tuqianyi.model.Item;
import com.tuqianyi.service.RecoverService;
import com.tuqianyi.taobao.TaobaoProxy;

public class RecoverAction extends ActionBase{

	private String numIids;
	
	public String execute() throws Exception {
		final String[] ids = StringUtils.split(numIids, ',');
		updateProgress(ids.length, 0);
		Connection conn = null;
		try
		{
			conn = DBUtils.getConnection();
			Dao.INSTANCE.recovering(ids, conn);
			recover(ids);
		}
		catch (Exception e) {
			_log.log(Level.SEVERE, "", e);
		}
		finally
		{
			DBUtils.close(conn, null, null);
		}
		return SUCCESS;
	}
	
	private void recover(String[] ids)
	{
		Map<String, Object> session = getSession();
		for (String id: ids)
		{
			Runnable task = new RecoverTask(id, session);
//			executeInPool(task);
			task.run();
			increaseProgress();
		}
	}
	
	public void setNumIids(String numIids) {
		this.numIids = numIids;
	}

	public String getNumIids() {
		return numIids;
	}
	
	private static class RecoverTask implements Runnable
	{
		private String numIid;
		private Map<String, Object> session;
		
		public RecoverTask(String numIid, Map<String, Object> session)
		{
			this.numIid = numIid;
			this.session = session;
		}

		public void run() {
			Connection conn = null;
			try
			{
				conn = DBUtils.getConnection();
				Item item = Dao.INSTANCE.getMergedItem(Long.parseLong(numIid), conn);
				String topSession = (String)session.get(TOP_SESSION);
				if (item != null && item.isMerged())
				{
					recover(item, topSession, conn);
				}
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "", e);
			}
			finally
			{
				DBUtils.close(conn, null, null);
			}
		}
		
		private void recover(Item item, String topSession, Connection conn) throws Exception
		{
			try
			{
				if (item.getOldPicUrl() != null)
				{
					String newPicMD5 = item.getNewPicMD5();
					if (newPicMD5 != null)
					{
						ItemGetResponse iRsp = TaobaoProxy.getItem(topSession, Long.parseLong(numIid));
						if (iRsp.isSuccess())
						{
							com.taobao.api.domain.Item i = iRsp.getItem();
							String iPicUrl = i.getPicUrl();
							URL iUrl = new URL(iPicUrl);
							byte[] iPicData = IOUtils.toByteArray(iUrl.openStream());
							String md5 = DigestUtils.md5Hex(iPicData);
							if (!md5.equals(newPicMD5))
							{
								_log.info("user changed main pic after merged. ");
								Dao.INSTANCE.unmerged(item.getNumIid(), true, null, conn);
								return;
							}
						}
						else
						{
							error(iRsp);
							return;
						}
					}
					String oldPicUrl = item.getOldPicUrl();
					String user = (String)session.get(USER);
					_log.info("nick: " + user + " num_iid:" + item.getNumIid() + " old: " + oldPicUrl);
					URL oldUrl = new URL(oldPicUrl);
					byte[] oldPicData = IOUtils.toByteArray(oldUrl.openStream());
					_log.info("data.length: " + oldPicData.length);
					TaobaoResponse response = null;
					if (oldPicData.length > 524288)
					{
						_log.info("recovering by picPath...");
						String key = "bao/uploaded/";
						int i = oldPicUrl.indexOf(key);
						String picPath = oldPicUrl.substring(i + key.length());
						_log.info("kongjian.url: " + oldPicUrl);
						response = TaobaoProxy.updateMainPic(topSession, item.getNumIid(), picPath);
						if (!response.isSuccess())
						{
							error(response);
						}
					}
					else
					{
						_log.info("recovering by data...");
						response = RecoverService.updateMainPic(topSession, item.getNumIid(), oldPicData);
						if (!response.isSuccess())
						{
							error(response);
						}
						else
						{
							if (response instanceof ItemImgUploadResponse)
							{
								ItemImgUploadResponse rsp = (ItemImgUploadResponse)response;
								String url = rsp.getItemImg().getUrl();
								Date modified = rsp.getItemImg().getCreated();
								_log.info("recovered...url: " + url + "; modified: " + modified);
							}
						}
					}
					
					if (!response.isSuccess() && oldPicData.length > 500000)
					{
						_log.info("recovering by reduced data...");
						float quality = 0.9F;
						byte[] tmp = null;
						ByteArrayInputStream in = new ByteArrayInputStream(oldPicData);
						BufferedImage image = ImageIO.read(in);
						for (int i = 0; i < 5; i++)
						{
							ByteArrayOutputStream out = new ByteArrayOutputStream();							
							ImageUtils.writeImage(image, "jpg", quality, out);
							tmp = out.toByteArray();
							_log.info("reduced data.length: " + tmp.length);
							if (tmp.length < 500000)
							{
								break;
							}
							quality = quality - 0.1F;
						};
						if (tmp != null)
						{
							response = RecoverService.updateMainPic(topSession, item.getNumIid(), tmp);
						}
					}
					
					if (response.isSuccess())
					{
						Dao.INSTANCE.unmerged(item.getNumIid(), true, null, conn);
					}
					else
					{
						error(response);
						if ("isv.item-is-delete:invalid-numIid-or-iid".equals(response.getSubCode()) || "isv.item-get-service-error:ITEM_NOT_FOUND".equals(response.getSubCode()))
						{
							Dao.INSTANCE.deleteMergedItem(item.getNumIid(), conn);
						}
						else
						{
							String errorMsg = response.getSubMsg() == null ? response.getMsg() : response.getSubMsg();
							Dao.INSTANCE.unmerged(item.getNumIid(), false, errorMsg, conn);
						}
					}
				}
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "", e);
				Dao.INSTANCE.unmerged(item.getNumIid(), false, e.getMessage(), conn);
			}
		}
	}
}
