package com.tuqianyi.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import com.taobao.api.ApiException;
import com.taobao.api.TaobaoResponse;
import com.taobao.api.response.ItemGetResponse;
import com.taobao.api.response.ItemImgUploadResponse;
import com.tuqianyi.Constants;
import com.tuqianyi.action.MergeAction;
import com.tuqianyi.db.DBUtils;
import com.tuqianyi.db.Dao;
import com.tuqianyi.font.FontProvider;
import com.tuqianyi.image.ImageUtils;
import com.tuqianyi.model.ImageLabel;
import com.tuqianyi.model.Item;
import com.tuqianyi.model.Merge;
import com.tuqianyi.model.TextLabel;
import com.tuqianyi.servlet.FontsServlet;
import com.tuqianyi.taobao.TaobaoProxy;

public class MergeTask implements Runnable
{
	static Logger _log = Logger.getLogger(MergeTask.class.getName());
	
	private Item item;
	private ImageLabel frame;
	private List<Merge> merges;
	private Map<String, Object> session;
	
	public MergeTask(Item item, ImageLabel frame, List<Merge> merges, Map<String, Object> session)
	{
		this.item = item;
		this.frame = frame;
		this.merges = merges;
		this.session = session;
	}

	public void run() {
		_log.info("enter merge task");
		Connection conn = null;
		try
		{
			conn = DBUtils.getConnection();
			String topSession = (String)session.get(MergeAction.TOP_SESSION);
			merge(item, frame, merges, topSession, conn);
			String sUserId = (String)session.get(Constants.USER_ID);
			long userId = Long.parseLong(sUserId);
			if (merges != null)
			{
				for (Merge m : merges)
				{
					ImageLabel label = m.getImageLabel();
					if (label != null && label.getId() > 0)
					{
						try {
							Dao.INSTANCE.addRecentLabel(label, userId, conn);
						} catch (Exception e) {
							_log.log(Level.SEVERE, "", e);
						}
					}
				}
			}
			else
			{
				_log.warning("merges is null");
			}
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "", e);
		}
		finally
		{
			DBUtils.close(conn, null, null);
		}
	}
	
	private boolean isAdmin()
	{
		Object admin = session.get("admin");
		if (admin != null)
		{
			return (Boolean) admin;
		}
		return false;
	}
	
	private void merge(Item item, ImageLabel frame, List<Merge> merges, String topSession, Connection conn)
	{
		try {
			_log.info("merging..." + item.getNumIid());
			String picUrl = item.getOldPicUrl();
			if (picUrl == null)
			{
				picUrl = item.getPicUrl();
			}
			if (picUrl == null)
			{
				_log.info("no pic: " + item.getNumIid());
				return;
			}
			BufferedImage image = ImageIO.read(new URL(picUrl));
			String rootPath = FontsServlet.getRootPath();
			if (frame != null)
			{
				image = addFrame(image, rootPath);
			}
			if (merges != null)
			{
				for (Merge m : merges)
				{
					image = mergeImage(image, m, item);
				}
			}
			ByteArrayOutputStream out2 = new ByteArrayOutputStream();
			try
			{

//				if (isAdmin())
//				{
//					ImageUtils.writeHighQualityImage(image, 1, out2);
//				}
//				else
//				{
					ImageUtils.writeImage(image, "jpg", 1, out2);
//				}
				int size = out2.size();
				if (size > 512000)
				{
					out2 = new ByteArrayOutputStream();
//					if (isAdmin())
//					{
//						ImageUtils.writeHighQualityImage(image, 1, out2);
//					}
//					else
//					{
						ImageUtils.writeImage(image, "jpg", 512000F/size, out2);
//					}
				}
				_log.info("size: " + out2.size());
				//ItemUpdateResponse response = TaobaoProxy.updateMainPic(topSession, item.getNumIid(), out2.toByteArray());
				byte[] newPicData = out2.toByteArray();
				TaobaoResponse response = RecoverService.updateMainPic(topSession, item.getNumIid(), newPicData);
				if (response.isSuccess())
				{
//					com.taobao.api.domain.Item updatedItem = response.getItem();
//					updatedItem.setPicUrl(getNewPicUrl(topSession, item.getNumIid()));
//					Dao.INSTANCE.merged(item.getNumIid(), updatedItem.getPicUrl(), updatedItem.getModified(), Item.STATUS_OK, null, null, conn);
					if (response instanceof ItemImgUploadResponse)
					{
						ItemImgUploadResponse rsp = (ItemImgUploadResponse)response;
						String url = rsp.getItemImg().getUrl();
						Date modified = rsp.getItemImg().getCreated();
						URL iUrl = new URL(url);
						byte[] iPicData = IOUtils.toByteArray(iUrl.openStream());
						Dao.INSTANCE.merged(item.getNumIid(), url, DigestUtils.md5Hex(iPicData), modified, Item.STATUS_OK, null, null, conn);
					}
				}
				else
				{
					_log.info(response.getErrorCode() + ": " +response.getMsg() + ", " + response.getSubCode() + ": " + response.getSubMsg());
					String errorMsg = response.getSubMsg() == null ? response.getMsg() : response.getSubMsg();
					if ("isv.item-is-delete:invalid-numIid-or-iid".equals(response.getSubCode()) || "isv.item-get-service-error:ITEM_NOT_FOUND".equals(response.getSubCode()))
					{
						Dao.INSTANCE.deleteMergedItem(item.getNumIid(), conn);
					}
					else
					{
						if ("isv.item-update-service-error:IC_ITEM_PIC_IS_TOO_LARGES".equals(response.getSubCode()))
						{
							errorMsg = "合成后的图像文件大小超过淘宝限制的最大值500K";
						}
						Dao.INSTANCE.merged(item.getNumIid(), null, null, null, Item.STATUS_FAILED, errorMsg, response.getSubCode(), conn);
					}
				}
			} 
			catch (ApiException e) {
				_log.log(Level.SEVERE, "", e);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "", e);
				try {
					Dao.INSTANCE.merged(item.getNumIid(), null, null, null, Item.STATUS_FAILED, e.getMessage(), "unknown", conn);
				} catch (Exception e1) {
					_log.log(Level.SEVERE, "", e1);
				}
			}
			finally
			{
				out2.close();
			}
		} catch (MalformedURLException e) {
			_log.log(Level.SEVERE, "", e);
			try {
				Dao.INSTANCE.merged(item.getNumIid(), null, null, null, Item.STATUS_FAILED, e.getMessage(), "unknown", conn);
			} catch (Exception e1) {
				_log.log(Level.SEVERE, "", e1);
			}
		} catch (IOException e) {
			_log.log(Level.SEVERE, "", e);
			try {
				Dao.INSTANCE.merged(item.getNumIid(), null, null, null, Item.STATUS_FAILED, e.getMessage(), "unknown", conn);
			} catch (Exception e1) {
				_log.log(Level.SEVERE, "", e1);
			}
		}
		catch (Throwable e) {
			_log.log(Level.SEVERE, "", e);
			try {
				Dao.INSTANCE.merged(item.getNumIid(), null, null, null, Item.STATUS_FAILED, e.getMessage(), "unknown", conn);
			} catch (Exception e1) {
				_log.log(Level.SEVERE, "", e1);
			}
		}
	}
	
	private BufferedImage mergeImage(BufferedImage image, Merge m, Item item) throws IOException
	{
		BufferedImage labelImage = createMergeImage(m, item);
		if (labelImage == null)
		{
			_log.warning("No label image was found.");
			return image;
		}

		_log.info("m.width: " + m.getWidth());
		_log.info("m.height: " + m.getHeight());
		float orignal2PreviewRatio = getOriginal2PreviewRatio(image);
		int newWidth = Math.round(m.getWidth() * orignal2PreviewRatio);
		int newHeight = Math.round(m.getHeight() * orignal2PreviewRatio);
		int newX = Math.round(m.getX() * orignal2PreviewRatio);
		int newY = Math.round(m.getY() * orignal2PreviewRatio);
		if (newX + newWidth > image.getWidth())
		{
			newX = image.getWidth() - newWidth;
		}
		if (newY + newHeight > image.getHeight())
		{
			newY = image.getHeight() - newHeight;
		}
		if (newY < 0)
		{
			newY = 0;
		}
		_log.info("new width: " + newWidth + ", new height: " + newHeight);
		_log.info("new x: " + newX + ", new y: " + newY);
		image = ImageUtils.composite(image, labelImage, newX, newY, newWidth, newHeight, m.getOpacity()/100F);
		return image;
	}
	
	private BufferedImage addFrame(BufferedImage image, String rootPath) throws IOException
	{
		File f = new File(rootPath + frame.getSrc());
		_log.info("frame file: " + f.getAbsolutePath());
		BufferedImage frameImage = ImageIO.read(f);
		return ImageUtils.composite(image, frameImage, 0, 0, image.getWidth(), image.getHeight(), 1F);
	}
	
	private String getNewPicUrl(String topSession, long numIid)
	{
		try {
			ItemGetResponse response = TaobaoProxy.getItem(topSession, numIid);
			if (response.isSuccess())
			{
				return response.getItem().getPicUrl();
			}
			else
			{
				_log.info(response.getErrorCode() + ": " +response.getMsg());
				_log.info(response.getSubCode() + ": " + response.getSubMsg());
			}
		} catch (ApiException e) {
			_log.log(Level.SEVERE, "", e);
		}
		return null;
	}
	
	private BufferedImage createMergeImage(Merge m, Item item) throws IOException
	{
		ImageLabel imageLabel = m.getImageLabel();
		TextLabel textLabel = m.getTextLabel();
		if (imageLabel != null)
		{
			if (m.getImage() == null)
			{
				BufferedImage labelImage = null;
				_log.info("label id: " + imageLabel.getId());
				if (ImageLabel.isLocal(imageLabel.getSrc()))
				{
					String root = FontsServlet.getRootPath();
					_log.info("label file path: " + root + imageLabel.getSrc());
					File f = new File(root + imageLabel.getSrc());
					labelImage = ImageIO.read(f);
				}
				else
				{
					URL labelUrl = new URL(imageLabel.getSrc());
					labelImage = ImageIO.read(labelUrl);
				}
				m.setImage(labelImage);
			}
			return m.getImage();
		}
		else if (textLabel != null)
		{
			if (textLabel.hasToken())
			{
				String text = textLabel.getParseText(item.getPrice());
				String color = textLabel.getColor();
				String backColor = textLabel.getBackground();
				return FontProvider.getInstance().createText(text, textLabel.getFont(), color, backColor, textLabel.getStyle(), textLabel.getLine(), textLabel.getBorderWidth(), textLabel.getAngle(), textLabel.isVertical());
			}
			else
			{
				return (BufferedImage)session.get(textLabel.getId());
			}
		}
		return null;
	}
	
	private float getOriginal2PreviewRatio(BufferedImage originalImage)
	{
		if (originalImage.getWidth() >= originalImage.getHeight() && originalImage.getWidth() > 310)
		{
			return 1.0f * originalImage.getWidth() / 310;
		}
		else if (originalImage.getHeight() >= originalImage.getWidth() && originalImage.getHeight() > 310)
		{
			return 1.0f * originalImage.getHeight() / 310;
		}
		return 1;
	}
}