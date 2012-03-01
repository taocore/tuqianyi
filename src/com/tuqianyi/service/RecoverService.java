package com.tuqianyi.service;

import java.util.List;
import java.util.logging.Logger;

import com.taobao.api.ApiException;
import com.taobao.api.FileItem;
import com.taobao.api.TaobaoClient;
import com.taobao.api.TaobaoResponse;
import com.taobao.api.domain.Item;
import com.taobao.api.domain.ItemImg;
import com.taobao.api.request.ItemGetRequest;
import com.taobao.api.request.ItemImgUploadRequest;
import com.taobao.api.response.ItemGetResponse;
import com.tuqianyi.taobao.TaobaoProxy;

public class RecoverService {

	protected static Logger _log = Logger.getLogger(RecoverService.class.getName());
	
	public static TaobaoResponse updateMainPic(String sessionKey, long numIid, byte[] data) throws ApiException
	{
		ItemGetRequest req = new ItemGetRequest();
		req.setNumIid(numIid);
		req.setFields("item_img");
		TaobaoClient taobaoClient = TaobaoProxy.createClient();
		ItemGetResponse rsp = taobaoClient.execute(req);
		if (rsp.isSuccess())
		{
			Item item = rsp.getItem();
			ItemImg mainPic = null;
			List<ItemImg> itemImages = item.getItemImgs();
			for (ItemImg img : itemImages)
			{
				if (img.getPosition() == 0)
				{
					mainPic = img;
					break;
				}
			}
			ItemImgUploadRequest uploadReq = new ItemImgUploadRequest();
			uploadReq.setId(mainPic.getId());
			uploadReq.setNumIid(numIid);
			uploadReq.setIsMajor(true);
			FileItem image = new FileItem(numIid + ".jpg", data);
			uploadReq.setImage(image);
			return taobaoClient.execute(uploadReq, sessionKey);
		}
		return rsp;
	}
}
