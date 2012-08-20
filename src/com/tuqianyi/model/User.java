package com.tuqianyi.model;

import java.util.Date;

public class User extends com.taobao.api.domain.User{

	private Date lastLogin;
	private short level;

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLevel(short level) {
		this.level = level;
	}

	public short getLevel() {
		return level;
	}
	
	public String getLevelImage()
	{
		int a = level / 5 + 1;
		int b = level % 5;
		if (b == 0)
		{
			b = 5;
			--a;
		}
		if (a > 0)
		{
			return "http://a.tbcdn.cn/sys/common/icon/rank/b_" + a + "_" + b + ".gif";
		}
		return null;
	}
}
