package com.tuqianyi.model;

import java.util.Date;

public class User extends com.taobao.api.domain.User{

	private Date lastLogin;

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getLastLogin() {
		return lastLogin;
	}
	
}
