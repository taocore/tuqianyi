package com.tuqianyi.action;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.taobao.api.ApiException;
import com.taobao.api.response.ItemUpdateResponse;
import com.tuqianyi.db.DBUtils;
import com.tuqianyi.db.Dao;
import com.tuqianyi.image.ImageUtils;
import com.tuqianyi.model.Item;
import com.tuqianyi.taobao.TaobaoProxy;

public class RecoverAction extends ActionBase{

	private String numIids;
	
	public String execute() throws Exception {
		_log.info("numIids: " + numIids);
		String[] ids = StringUtils.split(numIids, ',');
		String topSession = getSessionId();
		int processed = 0;
		Connection conn = null;
		try
		{
			conn = DBUtils.getConnection();
			updateProgress(ids.length, processed);
			for (String id: ids)
			{
				Item item = Dao.INSTANCE.getMergedItem(Long.parseLong(id), conn);
				if (item != null && item.isMerged())
				{
					Dao.INSTANCE.recovering(item.getNumIid(), conn);
					try
					{
						recover(item, topSession, conn);
					}
					catch (Exception e)
					{
						_log.log(Level.SEVERE, "", e);
						Dao.INSTANCE.unmerged(item.getNumIid(), false, e.getMessage(), conn);
					}
				}
				updateProgress(ids.length, ++processed);
			}
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
	
	private static void recover(Item item, String topSession, Connection conn) throws Exception
	{
			if (item.getOldPicUrl() != null)
			{
				String oldPicUrl = item.getOldPicUrl();
				_log.info("nick: " + getUser() + " num_iid:" + item.getNumIid() + " old: " + oldPicUrl);
				URL oldUrl = new URL(oldPicUrl);
				byte[] data = IOUtils.toByteArray(oldUrl.openStream());
				_log.info("data.length: " + data.length);
				ItemUpdateResponse response;
				if (data.length > 524288)
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
					response = TaobaoProxy.updateMainPic(topSession, item.getNumIid(), data);
					if (!response.isSuccess())
					{
						error(response);
					}
				}
				
				if (!response.isSuccess() && data.length > 500000)
				{
					_log.info("recovering by reduced data...");
					float quality = 0.9F;
					byte[] tmp = null;
					ByteArrayInputStream in = new ByteArrayInputStream(data);
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
						response = TaobaoProxy.updateMainPic(topSession, item.getNumIid(), tmp);
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

	public void setNumIids(String numIids) {
		this.numIids = numIids;
	}

	public String getNumIids() {
		return numIids;
	}
}
