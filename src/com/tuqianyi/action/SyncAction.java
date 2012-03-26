package com.tuqianyi.action;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;

import com.taobao.api.response.ItemGetResponse;
import com.tuqianyi.db.DBUtils;
import com.tuqianyi.db.Dao;
import com.tuqianyi.taobao.TaobaoProxy;

public class SyncAction extends ActionBase{
	
	public String execute()
	{
		sync(getUser());
		return SUCCESS;
	}

	private void sync(String user)
	{
		_log.info("syncing");
		String session = getSessionId();
		Connection conn = null;
		try {
			conn = DBUtils.getConnection();
			List<Long> ids = Dao.INSTANCE.getMergedItemIds(user, conn);
			for (long id : ids)
			{
				ItemGetResponse response = TaobaoProxy.getItem(session, id);
				if (!response.isSuccess())
				{
					_log.info(response.getErrorCode() + ": " +response.getMsg() + ", " + response.getSubCode() + ": " + response.getSubMsg());
					if ("isv.item-is-delete:invalid-numIid".equals(response.getSubCode()) || "isv.item-is-delete:invalid-numIid-or-iid".equals(response.getSubCode()) || "isv.item-get-service-error:ITEM_NOT_FOUND".equals(response.getSubCode()))
					{
						_log.info("deleting invalid item: " + id);
						Dao.INSTANCE.deleteMergedItem(id, conn);
					}
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "", e);
		}
		finally
		{
			DBUtils.close(conn, null, null);
		}
	}
}
