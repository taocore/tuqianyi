package com.tuqianyi.action;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;

import com.taobao.api.ApiException;
import com.taobao.api.response.ItemGetResponse;
import com.taobao.api.response.ItemUpdateResponse;
import com.tuqianyi.Constants;
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

public class MergeAction extends ActionBase {

	private String numIids;
	private List<Merge> merges;
	private ImageLabel frame;
	
	public String execute() throws Exception {
		if (numIids != null)
		{
			final String[] iids = StringUtils.split(numIids, ',');
			if (!checkItemsCount(iids.length))
			{
				return Constants.OUT_OF_ALLOWED_ITEMS;
			}
			updateProgress(iids.length, 0);
			Connection conn = null;
			try
			{
				conn = DBUtils.getConnection();
				final List<Item> items = getItems(iids, conn);
				merging(items, conn);
				merge(items, frame, merges);
			}
			catch (Exception e)
			{
				error(e);
			}
			finally
			{
				DBUtils.close(conn, null, null);
			}
		}
		return SUCCESS;
	}
	
	private List<Item> getItems(String[] numIids, Connection conn)
	{
		List<Item> items = new ArrayList<Item>();
		for (String numIid : numIids)
		{
			try {
				Item item = Dao.INSTANCE.getMergedItem(Long.parseLong(numIid), conn);
				if (item == null || item.getPicUrl() == null)
				{
					ItemGetResponse rsp = TaobaoProxy.getItem(getSessionId(), Long.parseLong(numIid));
					if (rsp.isSuccess())
					{
						com.taobao.api.domain.Item i = rsp.getItem();
						item = new Item(i);
					}
					else
					{
						error(rsp);
					}
				}
				if (item != null)
				{
					items.add(item);
				}
			} catch (NumberFormatException e) {
				error(e);
			} catch (ApiException e) {
				error(e);
			} 
			catch (Exception e)
			{
				error(e);
			}
		}
		return items;
	}
	
	private void merging(List<Item> items, final Connection conn)
	{
		for (Item item : items)
		{
			if (item.getStatus() != Item.STATUS_NORMAL)
			{
				continue;
			}
			try {
				Dao.INSTANCE.merging(item, getUser(), item.getPicUrl(), -1L, conn);
			} catch (Exception e) {
				_log.log(Level.SEVERE, "", e);
			}
		}
	}
	
	public String merge(List<Item> items, ImageLabel frame, final List<Merge> merges)
	{
		int count = items.size();
		updateProgress(count, 0);
		for (final Item item : items)
		{
			if (item.getStatus() != Item.STATUS_NORMAL)
			{
				increaseProgress();
				continue;
			}
			Runnable task = new MergeTask(item, frame, merges, getSession());
//			executeInPool(task);
			task.run();
			increaseProgress();
		}
		return null;
	}

	private boolean checkItemsCount(int processingItemsCount)
	{
		long processedItemsCount;
		try {
			processedItemsCount = Dao.INSTANCE.getMergedItemsCount(getUser());
			int allowedItemsCount = getAllowedItems();
			_log.info("allowed items: " + allowedItemsCount);
			return (processedItemsCount + processingItemsCount <= allowedItemsCount);
		} catch (Exception e) {
			_log.log(Level.SEVERE, "", e);
		}
		return false;
	}

	public void setNumIids(String numIids) {
		this.numIids = numIids;
	}

	public String getNumIids() {
		return numIids;
	}

	public void setMerges(List<Merge> merges) {
		this.merges = merges;
	}

	public List<Merge> getMerges() {
		return merges;
	}

	public void setFrame(ImageLabel frame) {
		this.frame = frame;
	}

	public ImageLabel getFrame() {
		return frame;
	}
	
	private static class MergeTask implements Runnable
	{
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
			Connection conn = null;
			try
			{
				conn = DBUtils.getConnection();
				String topSession = (String)session.get(TOP_SESSION);
				merge(item, frame, merges, topSession, conn);
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
		
		private void merge(Item item, ImageLabel frame, List<Merge> merges, String topSession, Connection conn)
		{
			try {
				_log.info("merging..." + item.getNumIid());
				String picUrl = item.getPicUrl();
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
					ImageUtils.writeImage(image, "jpg", 1, out2);
					int size = out2.size();
					if (size > 512000)
					{
						out2 = new ByteArrayOutputStream();
						ImageUtils.writeImage(image, "jpg", 512000F/size, out2);
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
					return FontProvider.getInstance().createText(text, textLabel.getFont(), color, backColor, textLabel.getStyle(), textLabel.getLine(), textLabel.getBorderWidth(), textLabel.getAngle());
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
}
