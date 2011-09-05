package com.tuqianyi.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.struts2.util.ServletContextAware;

import com.taobao.api.ApiException;
import com.taobao.api.response.ItemGetResponse;
import com.taobao.api.response.ItemUpdateResponse;
import com.tuqianyi.db.DBUtils;
import com.tuqianyi.db.Dao;
import com.tuqianyi.font.FontProvider;
import com.tuqianyi.image.ImageUtils;
import com.tuqianyi.model.FrameLabel;
import com.tuqianyi.model.ImageLabel;
import com.tuqianyi.model.Item;
import com.tuqianyi.model.Label;
import com.tuqianyi.model.Merge;
import com.tuqianyi.model.TextLabel;
import com.tuqianyi.taobao.TaobaoProxy;

public class MergeAction extends ActionBase implements ServletContextAware{

	private static FontProvider fontProvider;
	private ServletContext context;
	
	public String execute() throws Exception {
		if (fontProvider == null)
		{
			String fontDir = context.getRealPath("/") + "fonts/";
			fontProvider = new FontProvider(fontDir);
		}

		return SUCCESS;
	}
	
	public String merge(List<Item> items, List<Merge> merges)
	{
		String topSession = getSessionId();
		if (!checkItemsCount(items.size()))
		{
			//todo
//			return Constants.OUT_OF_ALLOWED_ITEMS;
		}
		Connection conn = null;
		try
		{
			conn = DBUtils.getConnection();
			for (Item item : items)
			{
				if (item.getStatus() == Item.STATUS_OK)
				{
					continue;
				}
				try {
					Dao.INSTANCE.merging(item, getUser(), item.getPicUrl(), -1L, conn);
				} catch (Exception e) {
					_log.log(Level.SEVERE, "", e);
				}
			}
		
			int count = items.size();
			int processed = 0;
			updateProgress(count, processed);
			for (Item item : items)
			{
				if (item.getStatus() == Item.STATUS_OK)
				{
					processed++;
					updateProgress(count, processed);
					continue;
				}
				merge(item, merges, topSession, conn);
				processed++;
				updateProgress(count, processed);
				System.gc();
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
		return null;
	}
	
	private synchronized void merge(Item item, List<Merge> merges, String topSession, Connection conn)
	{
		try {
			_log.info("merging..." + item.getNumIid());
			BufferedImage image = ImageIO.read(new URL(item.getPicUrl()));
			String rootPath = context.getRealPath("/");
			for (Merge m : merges)
			{
				Label label = m.getLabel();
				if (label instanceof ImageLabel)
				{
					image = mergeImage(image, m, rootPath);
				}
				else if(label instanceof TextLabel)
				{
					image = drawText(item, image, m);
				}
			}
//				FileOutputStream out = new FileOutputStream(rootPath + "result.jpg");
			ByteArrayOutputStream out2 = new ByteArrayOutputStream();
			try
			{
//					ImageUtils.writeImage(image, "jpg", 1, out);
				ImageUtils.writeImage(image, "jpg", 1, out2);
				int size = out2.size();
				if (size > 512000)
				{
					out2 = new ByteArrayOutputStream();
					ImageUtils.writeImage(image, "jpg", 512000F/size, out2);
//						out = new FileOutputStream(rootPath + "result.jpg");
//						ImageUtils.writeImage(image, "jpg", 512000F/size, out);
				}
				_log.info("size: " + out2.size());
				ItemUpdateResponse response = TaobaoProxy.updateMainPic(topSession, item.getNumIid(), out2.toByteArray());
				if (response.isSuccess())
				{
					com.taobao.api.domain.Item updatedItem = response.getItem();
					updatedItem.setPicUrl(getNewPicUrl(topSession, item.getNumIid()));
					Dao.INSTANCE.merged(item.getNumIid(), updatedItem.getPicUrl(), updatedItem.getModified(), Item.STATUS_OK, null, null, conn);
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
						Dao.INSTANCE.merged(item.getNumIid(), null, null, Item.STATUS_FAILED, errorMsg, response.getSubCode(), conn);
					}
				}
			} 
			catch (ApiException e) {
				_log.log(Level.SEVERE, "", e);
//					throw new Exception(Constants.API_EXCEPTION);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "", e);
				try {
					Dao.INSTANCE.merged(item.getNumIid(), null, null, Item.STATUS_FAILED, e.getMessage(), "unknown", conn);
				} catch (Exception e1) {
					_log.log(Level.SEVERE, "", e1);
				}
			}
			finally
			{
//					out.close();
				out2.close();
			}
		} catch (MalformedURLException e) {
			_log.log(Level.SEVERE, "", e);
			try {
				Dao.INSTANCE.merged(item.getNumIid(), null, null, Item.STATUS_FAILED, e.getMessage(), "unknown", conn);
			} catch (Exception e1) {
				_log.log(Level.SEVERE, "", e1);
			}
		} catch (IOException e) {
			_log.log(Level.SEVERE, "", e);
			try {
				Dao.INSTANCE.merged(item.getNumIid(), null, null, Item.STATUS_FAILED, e.getMessage(), "unknown", conn);
			} catch (Exception e1) {
				_log.log(Level.SEVERE, "", e1);
			}
		}
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
	
	private BufferedImage mergeImage(BufferedImage image, Merge m, String rootPath) throws IOException
	{
		ImageLabel label = (ImageLabel)m.getLabel();
		if (label instanceof FrameLabel)
		{
			return addFrame(image, (FrameLabel)label, rootPath);
		}
		BufferedImage labelImage;
		_log.info("label id: " + label.getId());
		if (ImageLabel.isLocal(label.getSrc()))
		{
			_log.info("label file path: " + rootPath + label.getSrc());
			File f = new File(rootPath + label.getSrc());
			labelImage = ImageIO.read(f);
		}
		else
		{
			URL labelUrl = new URL(label.getSrc());
			labelImage = ImageIO.read(labelUrl);
		}
//				Image resizedLabel = new Image(label);
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
		_log.info("new width: " + newWidth + ", new height: " + newHeight);
		_log.info("new x: " + newX + ", new y: " + newY);
//				resizedLabel.resize(newWidth, newHeight);
//				label = resizedLabel.getAsBufferedImage();
		image = ImageUtils.composite(image, labelImage, newX, newY, newWidth, newHeight, label.getOpacity()/100F);
		return image;
	}
	
	private BufferedImage addFrame(BufferedImage image, FrameLabel label, String rootPath) throws IOException
	{
		_log.info("frame path: " + label.getSrc());
		URL labelUrl = new URL(label.getSrc());
		BufferedImage labelImage = ImageIO.read(labelUrl);
		return ImageUtils.composite(image, labelImage, 0, 0, image.getWidth(), image.getHeight(), label.getOpacity()/100F);
	}
	
	private BufferedImage drawText(Item item, BufferedImage image, Merge m)
	{
		TextLabel label = (TextLabel)m.getLabel();
		String text = label.getText();
		if (text == null || text.length() == 0)
		{
			return image;
		}
		float original2PreviewRatio = getOriginal2PreviewRatio(image);
		int newX = Math.round(m.getX() * original2PreviewRatio);
		int newY = Math.round(m.getY() * original2PreviewRatio);
		_log.info("new x: " + newX + ", new y: " + newY);
//				resizedLabel.resize(newWidth, newHeight);
//				label = resizedLabel.getAsBufferedImage();
		int fontSize = Math.round((label.getFontSize() + 4) * original2PreviewRatio);
		Font font = fontProvider.getFont(label.getFont()).deriveFont(Font.BOLD, fontSize);
//		Font font = new Font(label.getFont(), Font.BOLD, label.getFontSize());
		Color color = Color.decode(label.getColor());
		image = ImageUtils.pressText(image, item.parseToken(text), font, 
				color, label.getBackground() == null ? null : Color.decode(label.getBackground()), newX, newY, label.getAngle(), 1F);
		return image;
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

	private boolean checkItemsCount(int processingItemsCount)
	{
		long processedItemsCount;
		try {
			processedItemsCount = Dao.INSTANCE.getMergedItemsCount(getUser(), Item.STATUS_OK);
			String version = getVersion();
			int allowedItemsCount = getAllowedItems(version);
			return (processedItemsCount + processingItemsCount <= allowedItemsCount);
		} catch (Exception e) {
			_log.log(Level.SEVERE, "", e);
		}
		return false;
	}
	
	private int getAllowedItems(String version)
	{
		int allowedItemsCount = 10;
		if ("1".equals(version))
		{
			allowedItemsCount = 300;
		}
		else if ("2".equals(version))
		{
			allowedItemsCount = 800;
		}
		else if ("3".equals(version))
		{
			allowedItemsCount = 2000;
		};
		return allowedItemsCount;
	}

	public void setServletContext(ServletContext context) {
		this.context = context;
	}
}
