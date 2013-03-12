package com.tuqianyi.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.im4java.core.CompositeCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.GMOperation;
import org.im4java.core.IM4JavaException;

import com.taobao.api.ApiException;
import com.taobao.api.TaobaoResponse;
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

public class MergeTask2 implements Runnable{
	static Logger _log = Logger.getLogger(MergeTask2.class.getName());
	private static int uid;
	
	private Item item;
	private ImageLabel frame;
	private List<Merge> merges;
	private Map<String, Object> session;
	
	public MergeTask2(Item item, ImageLabel frame, List<Merge> merges, Map<String, Object> session)
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
	
	private String getLocalDir()
	{
		return "/tmp/" + item.getNumIid() + "/";
	}
	
	private String saveImage2Local(Item item) throws MalformedURLException, IOException
	{
		String picUrl = item.getOldPicUrl();
		if (picUrl == null)
		{
			picUrl = item.getPicUrl();
		}
		if (picUrl == null)
		{
			_log.info("no pic: " + item.getNumIid());
			return null;
		}
		String localDir = getLocalDir();
		File f = new File(localDir);
		f.mkdirs();
		String localPath = localDir + uid++ + ".jpg";
		InputStream in = new URL(picUrl).openStream();
		FileOutputStream out = new FileOutputStream(localPath);
		IOUtils.copy(in, out);
		out.close();
		in.close();
		return localPath;
	}
	
	private void merge(Item item, ImageLabel frame, List<Merge> merges, String topSession, Connection conn)
	{
		try {
			_log.info("merging..." + item.getNumIid());
			String localPath = saveImage2Local(item);
			_log.info("image saved to: " + localPath);
			String rootPath = FontsServlet.getRootPath();
			String resultPath = localPath;
			if (frame != null)
			{
				resultPath = addFrame(localPath, rootPath);
			}
			if (merges != null)
			{
				for (Merge m : merges)
				{
					resultPath = mergeImage(resultPath, m, item);
				}
			}
			FileInputStream in = new FileInputStream(resultPath);
			byte[] newPicData = IOUtils.toByteArray(in);
			if (newPicData.length > 512000)
			{
				String tmp = getLocalDir() + uid++ + ".jpg";
				GMOperation op = new GMOperation();
				op.quality(Double.valueOf(100 * 512000F/newPicData.length));
				op.addImage(2);
				ConvertCmd cmd = new ConvertCmd(true);
				cmd.run(op, resultPath, tmp);
				resultPath = tmp;
				in.close();
				in = new FileInputStream(resultPath);
				newPicData = IOUtils.toByteArray(in);
			}
			try
			{
				TaobaoResponse response = RecoverService.updateMainPic(topSession, item.getNumIid(), newPicData);
				if (response.isSuccess())
				{
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
				in.close();
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
		} catch (InterruptedException e) {
			_log.log(Level.SEVERE, "", e);
			try {
				Dao.INSTANCE.merged(item.getNumIid(), null, null, null, Item.STATUS_FAILED, e.getMessage(), "unknown", conn);
			} catch (Exception e1) {
				_log.log(Level.SEVERE, "", e1);
			}
		} catch (IM4JavaException e) {
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
		finally
		{
			try {
				FileUtils.deleteDirectory(new File(getLocalDir()));
			} catch (IOException e) {
				_log.log(Level.SEVERE, "", e);
			}
		}
	}
	
	private String mergeImage(String image, Merge m, Item item) throws IOException, InterruptedException, IM4JavaException
	{
		String labelImage = createMergeImage(m, item);
		if (labelImage == null)
		{
			_log.warning("No label image was found.");
			return image;
		}
		/*
		if (m.getOpacity() < 100)
		{
			String opacityPath = getLocalDir() + uid++ + ".png";
			GMOperation op = new GMOperation();
			op.operator("Opacity", "Assign", new Double(m.getOpacity()/100f));
			op.quality(Double.valueOf(100));
			op.addImage(2);
			ConvertCmd cmd = new ConvertCmd(true);
			cmd.run(op, labelImage, opacityPath);
			labelImage = opacityPath;
		}*/
		
		BufferedImage bi = ImageIO.read(new File(image));
		int width = bi.getWidth();
		int height = bi.getHeight();
		float orignal2PreviewRatio = getOriginal2PreviewRatio(width, height);
		int newWidth = Math.round(m.getWidth() * orignal2PreviewRatio);
		int newHeight = Math.round(m.getHeight() * orignal2PreviewRatio);
		int newX = Math.round(m.getX() * orignal2PreviewRatio);
		int newY = Math.round(m.getY() * orignal2PreviewRatio);
		if (newX + newWidth > width)
		{
			newX = width - newWidth;
		}
		if (newY + newHeight > height)
		{
			newY = height - newHeight;
		}
		if (newY < 0)
		{
			newY = 0;
		}
		_log.info("new width: " + newWidth + ", new height: " + newHeight);
		_log.info("new x: " + newX + ", new y: " + newY);
		String resultPath = getLocalDir() + uid++ + ".jpg";
		GMOperation op = new GMOperation();
		op.geometry(newWidth, newHeight, newX, newY);
		if (m.getOpacity() < 100)
		{
			op.addRawArgs("-dissolve", String.valueOf(m.getOpacity()));
		}
		op.quality(Double.valueOf(100));
		op.addImage(3);
		CompositeCmd cmd = new CompositeCmd(true);
		cmd.run(op, labelImage, image, resultPath);
		return resultPath;
	}
	
	private String addFrame(String imagePath, String rootPath) throws IOException, InterruptedException, IM4JavaException
	{
		BufferedImage bi = ImageIO.read(new File(imagePath));
		int width = bi.getWidth();
		int height = bi.getHeight();
		File f = new File(rootPath + frame.getSrc());
		_log.info("frame file: " + f.getAbsolutePath());
		String framePath = f.getAbsolutePath();
		GMOperation op = new GMOperation();
		op.geometry(width, height, 0, 0);
		op.quality(Double.valueOf(100));
		op.addImage(3);
		CompositeCmd cmd = new CompositeCmd(true);
		String resultPath = getLocalDir() + uid++ + ".jpg";
		cmd.run(op, framePath, imagePath, resultPath);
		return resultPath;
	}
	
	private String createMergeImage(Merge m, Item item) throws IOException
	{
		ImageLabel imageLabel = m.getImageLabel();
		TextLabel textLabel = m.getTextLabel();
		if (imageLabel != null)
		{
			_log.info("label id: " + imageLabel.getId());
			if (ImageLabel.isLocal(imageLabel.getSrc()))
			{
				String root = FontsServlet.getRootPath();
				_log.info("label file path: " + root + imageLabel.getSrc());
				return root + imageLabel.getSrc();
			}
			else
			{
				URL labelUrl = new URL(imageLabel.getSrc());
				String localPath = getLocalDir() + uid++ + ".png";
				InputStream in = labelUrl.openStream();
				FileOutputStream out = new FileOutputStream(localPath);
				IOUtils.copy(in, out);
				out.close();
				in.close();
				return localPath;
			}
		}
		else if (textLabel != null)
		{
			BufferedImage textImage;
			if (textLabel.hasToken())
			{
				String text = textLabel.getParseText(item.getPrice());
				String color = textLabel.getColor();
				String backColor = textLabel.getBackground();
				textImage = FontProvider.getInstance().createText(text, textLabel.getFont(), color, backColor, textLabel.getStyle(), textLabel.getLine(), textLabel.getBorderWidth(), textLabel.getAngle(), textLabel.isVertical());
			}
			else
			{
				textImage = (BufferedImage)session.get(textLabel.getId());
			}
			String localPath = getLocalDir() + uid++ + ".png";
			FileOutputStream out = new FileOutputStream(localPath);
			//ImageUtils.writeImage(textImage, "png", 1, out);
			ImageIO.write(textImage, "png", out);
			return localPath;
		}
		return null;
	}
	
	private float getOriginal2PreviewRatio(int width, int height)
	{
		if (width >= height && width > 310)
		{
			return 1.0f * width / 310;
		}
		else if (height >= width && height > 310)
		{
			return 1.0f * height / 310;
		}
		return 1;
	}
}
