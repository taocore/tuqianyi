package com.tuqianyi.taobao;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.time.DateUtils;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.FileItem;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.ArticleUserSubscribe;
import com.taobao.api.domain.User;
import com.taobao.api.request.ItemGetRequest;
import com.taobao.api.request.ItemUpdateListingRequest;
import com.taobao.api.request.ItemUpdateRequest;
import com.taobao.api.request.ItemsInventoryGetRequest;
import com.taobao.api.request.ItemsOnsaleGetRequest;
import com.taobao.api.request.SellercatsListGetRequest;
import com.taobao.api.request.UserGetRequest;
import com.taobao.api.request.VasOrderSearchRequest;
import com.taobao.api.request.VasSubscribeGetRequest;
import com.taobao.api.response.ItemGetResponse;
import com.taobao.api.response.ItemUpdateListingResponse;
import com.taobao.api.response.ItemUpdateResponse;
import com.taobao.api.response.ItemsInventoryGetResponse;
import com.taobao.api.response.ItemsOnsaleGetResponse;
import com.taobao.api.response.SellercatsListGetResponse;
import com.taobao.api.response.UserGetResponse;
import com.taobao.api.response.VasOrderSearchResponse;
import com.taobao.api.response.VasSubscribeGetResponse;
import com.tuqianyi.Constants;

public class TaobaoProxy implements Constants
{
	static Logger _log = Logger.getLogger(TaobaoProxy.class.getName());
	
	public static final String INVALID_PIC_PATH = "isv.invalid-parameter:picPath";
	
	private static final TaobaoClient taobaoClient = new DefaultTaobaoClient(getApiUrl(), getAppKey(), getAppSecret());
	
	private static String getApiUrl()
	{
		return "http://gw.api.taobao.com/router/rest";
	}
	
	private static String getAppKey()
	{
		return APP_KEY;
	}
	
	private static String getAppSecret()
	{
		return SECRET;
	}
	
	public static TaobaoClient getClient()
	{
		return taobaoClient;
	}
	
	public static User getUser(String session) throws ApiException
	{
		UserGetRequest req = new UserGetRequest();
		req.setFields("user_id,uid,nick");
		//req.setNick(nick);
		UserGetResponse rsp = taobaoClient.execute(req, session);
		return rsp.getUser();
	}
	
	public static ItemGetResponse getItem(String session, long numIid) throws ApiException
	{
		ItemGetRequest req = new ItemGetRequest();
		req.setFields("num_iid,title,pic_url,price");
		req.setNumIid(numIid);
		return taobaoClient.execute(req, session);
	}
	
	public static ItemGetResponse getItem(long numIid) throws ApiException
	{
		ItemGetRequest req = new ItemGetRequest();
		req.setFields("num_iid");
		req.setNumIid(numIid);
		return taobaoClient.execute(req);
	}
	
	public static ItemsOnsaleGetResponse getOnSales(String session, long pageNumber, long pageSize, String sellerCids, String keyWord) throws ApiException
	{
		ItemsOnsaleGetRequest req = new ItemsOnsaleGetRequest();
		req.setFields("num_iid,title,pic_url,price");
		if (sellerCids != null)
		{
			req.setSellerCids(sellerCids);
		}
		if (keyWord != null)
		{
			req.setQ(keyWord);
		}
		req.setOrderBy("list_time:desc");
		req.setPageNo(pageNumber);
		req.setPageSize(pageSize);
		return taobaoClient.execute(req, session);
	}
	
	public static ItemsInventoryGetResponse getInventory(String sessionKey, long pageNumber, long pageSize, String banner, String sellerCids, String keyWord) throws ApiException
	{
		ItemsInventoryGetRequest req = new ItemsInventoryGetRequest();
		req.setFields("num_iid,title,pic_url,price,num, list_time, delist_time, nick");
		if (sellerCids != null)
		{
			req.setSellerCids(sellerCids);
		}
		if (keyWord != null)
		{
			req.setQ(keyWord);
		}
		if (banner != null)
		{
			req.setBanner(banner);
		}
		req.setOrderBy("list_time:desc");
		req.setPageNo(pageNumber);//default 1
		req.setPageSize(pageSize);// default 200
		 
		ItemsInventoryGetResponse rsp = taobaoClient.execute(req, sessionKey);
		return rsp;
	}
	
	public static SellercatsListGetResponse getSellerCategories(String nick) throws ApiException
	{
		SellercatsListGetRequest req = new SellercatsListGetRequest();
		req.setNick(nick);

		SellercatsListGetResponse rsp = taobaoClient.execute(req);
		return rsp;
	}
	
	public static ItemUpdateResponse updateMainPic(String sessionKey, long numIid, byte[] data) throws ApiException
	{
		ItemUpdateRequest req = new ItemUpdateRequest();
		req.setNumIid(numIid);
		FileItem image = new FileItem(numIid + ".jpg", data);
		req.setImage(image);
		 
		ItemUpdateResponse rsp = taobaoClient.execute(req, sessionKey);
		return rsp;
	}
	
	public static ItemUpdateResponse updateMainPic(String sessionKey, long numIid, String newUrl) throws ApiException
	{
		ItemUpdateRequest req = new ItemUpdateRequest();
		req.setNumIid(numIid);
		req.setPicPath(newUrl);

		ItemUpdateResponse rsp = taobaoClient.execute(req, sessionKey);
		return rsp;
	}
	
	public static String updateListing(long numIid, long num, String sessionKey) throws ApiException
	{
		TaobaoClient client = new DefaultTaobaoClient(getApiUrl(), getAppKey(), getAppSecret());
		ItemUpdateListingRequest req = new ItemUpdateListingRequest();
		req.setNumIid(numIid);
		req.setNum(num);
		 
		ItemUpdateListingResponse rsp = client.execute(req, sessionKey);
		return rsp.getBody();
	}
	
	public static String getUserInfo(String nick) throws ApiException
	{
		TaobaoClient client = new DefaultTaobaoClient(getApiUrl(), getAppKey(), getAppSecret());
		UserGetRequest req = new UserGetRequest();
		req.setNick(nick);
		req.setFields("alipay_account");
		 
		UserGetResponse rsp = client.execute(req);
		return rsp.getBody();
	}
	
	public static List<ArticleUserSubscribe> getSubscription(String nick, String articleCode) throws ApiException
	{
		TaobaoClient client = new DefaultTaobaoClient(getApiUrl(), getAppKey(), getAppSecret());
		VasSubscribeGetRequest req = new VasSubscribeGetRequest();
		req.setNick(nick);
		req.setArticleCode(articleCode);
		 
		VasSubscribeGetResponse rsp = client.execute(req);
		return rsp.getArticleUserSubscribes();
	}
	
	public static boolean verifyVersion(String appKey, String leaseId, String timestamp, String versionNo, String sign, String appSecret)
    {
		StringBuilder result = new StringBuilder();
		result.append("appkey").append(appKey)
		.append("leaseId").append(leaseId);
		if (timestamp != null)
		{
			result.append("timestamp").append(timestamp);
		}
		result.append("versionNo").append(versionNo);
		result.insert(0, appSecret);
		result.append(appSecret);
		_log.info("sign: " + sign);
		String s = DigestUtils.md5Hex(result.toString());
		_log.info("s: " + s);
		return sign != null && sign.equals(s.toUpperCase());
    }
	
	public static VasOrderSearchResponse getOrders(String nick, String articleCode) throws ApiException
    {
		TaobaoClient client = new DefaultTaobaoClient(getApiUrl(), getAppKey(), getAppSecret());
		VasOrderSearchRequest req = new VasOrderSearchRequest();
		req.setNick(nick);
		req.setArticleCode(articleCode);
		String[] patterns = {"yyyy-MM-dd"};
		Date from = null;
		try {
			from = DateUtils.parseDate("2010-12-01", patterns);
		} catch (ParseException e) {
			_log.log(Level.SEVERE, "", e);
		}
		req.setStartCreated(from);
		return client.execute(req);
    }
}
