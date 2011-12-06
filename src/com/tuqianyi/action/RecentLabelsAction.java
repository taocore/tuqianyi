package com.tuqianyi.action;

import java.sql.Connection;
import java.util.List;

import com.tuqianyi.db.DBUtils;
import com.tuqianyi.db.Dao;
import com.tuqianyi.model.ImageLabel;

public class RecentLabelsAction extends ActionBase{

	private List<ImageLabel> labels;
	
	public String execute() throws Exception
	{
		Connection conn = null;
		try
		{
			conn = DBUtils.getConnection();
			labels = Dao.INSTANCE.getRecentLabels(getUserId(), conn);
		}
		finally
		{
			DBUtils.close(conn, null, null);
		}
		return SUCCESS;
	}

	public void setLabels(List<ImageLabel> labels) {
		this.labels = labels;
	}

	public List<ImageLabel> getLabels() {
		return labels;
	}
}
