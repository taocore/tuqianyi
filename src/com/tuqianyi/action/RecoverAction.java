package com.tuqianyi.action;

import java.net.URL;
import java.sql.Connection;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.taobao.api.ApiException;
import com.taobao.api.response.ItemUpdateResponse;
import com.tuqianyi.db.DBUtils;
import com.tuqianyi.db.Dao;
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
					recover(item, topSession, conn);
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
	
	private static synchronized void recover(Item item, String topSession, Connection conn)
	{
		try {
			if (item.getOldPicUrl() != null)
			{
				_log.info("nick: " + getUser() + " num_iid:" + item.getNumIid() + " old: " + item.getOldPicUrl());
				URL oldUrl = new URL(item.getOldPicUrl());
				byte[] data = IOUtils.toByteArray(oldUrl.openStream());
				ItemUpdateResponse response = TaobaoProxy.updateMainPic(topSession, item.getNumIid(), data);
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
		} catch (ApiException e) {
			_log.log(Level.SEVERE, "", e);
		} catch (Exception e) {
			_log.log(Level.SEVERE, "", e);
		}
	}

	public void setNumIids(String numIids) {
		this.numIids = numIids;
	}

	public String getNumIids() {
		return numIids;
	}
}
