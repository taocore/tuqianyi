package com.tuqianyi.action;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;

import com.taobao.api.ApiException;
import com.taobao.api.response.ItemGetResponse;
import com.tuqianyi.db.DBUtils;
import com.tuqianyi.db.Dao;
import com.tuqianyi.model.Item;
import com.tuqianyi.taobao.TaobaoProxy;

public class MergingAction extends ActionBase {

	private String numIids;
	private com.tuqianyi.model.Item item;
	
	public String execute()
	{
		_log.info("numIids: " + numIids);
		if (numIids != null)
		{
			String[] iids = StringUtils.split(numIids, ',');
			if (iids.length == 1)
			{
				Connection conn = null;
				try {
					long id = Long.parseLong(iids[0]);
					conn = DBUtils.getConnection();
					item = Dao.INSTANCE.getMergedItem(id, conn);
					if (item == null)
					{
						ItemGetResponse rsp = TaobaoProxy.getItem(getSessionId(), id);
						if (rsp.isSuccess())
						{
							item = new com.tuqianyi.model.Item(rsp.getItem());
						}
						else
						{
							error(rsp);
						}
					}
				} catch (NumberFormatException e) {
					error(e);
				} catch (ApiException e) {
					error(e);
				} catch (NamingException e) {
					error(e);
				} catch (SQLException e) {
					error(e);
				} catch (Exception e) {
					error(e);
				}
				finally
				{
					DBUtils.close(conn, null, null);
				}
			}
		}
		return SUCCESS;
	}

	public void setNumIids(String numIids) {
		this.numIids = numIids;
	}

	public String getNumIids() {
		return numIids;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Item getItem() {
		return item;
	}
}
