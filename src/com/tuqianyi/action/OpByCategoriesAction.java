package com.tuqianyi.action;

import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.taobao.api.ApiException;
import com.taobao.api.domain.SellerCat;
import com.taobao.api.response.SellercatsListGetResponse;
import com.tuqianyi.taobao.TaobaoProxy;

public class OpByCategoriesAction extends ActionBase{
	
	private List<SellerCat> categories;
	
	public String execute() throws Exception
	{
		retriveCategories();
		return SUCCESS;
	}

	private void retriveCategories() throws ApiException
	{
		Map<String, Object> session = ActionContext.getContext().getSession();
		String nick = (String)session.get(USER);
		SellercatsListGetResponse catRsp = TaobaoProxy.getSellerCategories(nick);
		setCategories(catRsp.getSellerCats());
	}

	public void setCategories(List<SellerCat> categories) {
		this.categories = categories;
	}

	public List<SellerCat> getCategories() {
		return categories;
	}
}
