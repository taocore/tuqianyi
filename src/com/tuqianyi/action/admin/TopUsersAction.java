package com.tuqianyi.action.admin;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import com.tuqianyi.action.ActionBase;
import com.tuqianyi.db.Dao;
import com.tuqianyi.model.User;

public class TopUsersAction extends ActionBase{

	static Logger _log = Logger.getLogger(TopUsersAction.class.getName());
	
	private int limit = 27;
	private List<User> users;
	
	public String execute()
	{
		try {
			users = Dao.INSTANCE.getTopUsers(limit);
			return SUCCESS;
		} catch (NamingException e) {
			_log.log(Level.SEVERE, "", e);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "", e);
		}
		return ERROR;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getLimit() {
		return limit;
	}
}
