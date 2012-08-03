package com.tuqianyi.action.admin;

import java.util.List;

import com.taobao.api.ApiException;
import com.taobao.api.domain.ArticleBizOrder;
import com.taobao.api.response.VasOrderSearchResponse;
import com.tuqianyi.action.ActionBase;
import com.tuqianyi.taobao.TaobaoProxy;

public class OrdersAction extends ActionBase{

	private String nick;
	private List<ArticleBizOrder> orders;
	
	public String execute()
	{
		_log.info("nnnnnnnnnnick: " + nick);
		try {
			VasOrderSearchResponse rsp = TaobaoProxy.getOrders(nick, ARTICLE_CODE);
			if (rsp.isSuccess())
			{
				orders = rsp.getArticleBizOrders();
			}
			else
			{
				error(rsp);
			}
		} catch (ApiException e) {
			error(e);
		}
		return SUCCESS;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getNick() {
		return nick;
	}

	public void setOrders(List<ArticleBizOrder> orders) {
		this.orders = orders;
	}

	public List<ArticleBizOrder> getOrders() {
		return orders;
	}
}
