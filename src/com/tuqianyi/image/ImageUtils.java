package com.tuqianyi.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

public class ImageUtils {

	/**
	 * 图片水印
	 * 
	 * @param markFile
	 *            水印图片
	 * @param imageFile
	 *            目标图片
	 * @param x
	 *            修正�?默认在中�?
	 * @param y
	 *            修正�?默认在中�?
	 * @param alpha
	 *            透明�?
	 */
	public static BufferedImage pressImage(BufferedImage image,
			BufferedImage watermark, int x, int y, float alpha) {
		int wideth = image.getWidth();
		int height = image.getHeight();
		BufferedImage result = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = result.createGraphics();
		g.drawImage(image, 0, 0, wideth, height, null);
		// 水印文件
		int wideth_biao = watermark.getWidth();
		int height_biao = watermark.getHeight();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
				alpha));
		g.drawImage(watermark, x, y, wideth_biao, height_biao, null);
		// 水印文件结束
		g.dispose();
		return result;
	}

	/**
	 * 文字水印
	 * 
	 * @param text
	 *            水印文字
	 * @param targetImg
	 *            目标图片
	 * @param fontName
	 *            字体名称
	 * @param fontStyle
	 *            字体样式
	 * @param color
	 *            字体颜色
	 * @param fontSize
	 *            字体大小
	 * @param x
	 *            修正�?
	 * @param y
	 *            修正�?
	 * @param alpha
	 *            透明�?
	 */
	public static BufferedImage pressText(BufferedImage image, String text, 
			Font font, Color color, Color backColor, int x, int y, int angle, float alpha) {
			Graphics2D g = image.createGraphics();
			FontMetrics metrics = g.getFontMetrics(font);
			int textLength = metrics.stringWidth(text);
			int textHeight = metrics.getHeight();
			if (x + textLength > image.getWidth())
			{
				x = image.getWidth() - textLength;
			}
			if (y + textHeight > image.getHeight())
			{
				y = image.getHeight() - textHeight;
			}
//			g.drawImage(src, 0, 0, width, height, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
					alpha));
			g.setFont(font);			
			
			System.out.println("color: " + color);
			System.out.println("backcolor: " + backColor);
			if (angle != 0)
			{
				boolean rotated = false;
				if (backColor != null)
				{
					g.setColor(backColor);
					g.translate(x, y + metrics.getAscent());
					g.rotate(angle * Math.PI / 180);
					g.fillRect(0, -metrics.getAscent(), textLength, textHeight);
					rotated = true;
				}
				if (!rotated)
				{
					g.translate(x, y + metrics.getAscent());
					g.rotate(angle * Math.PI / 180);
				}
				g.setColor(color);
				g.drawString(text, 0, 0);
			}
			else
			{
				if (backColor != null)
				{
					g.setColor(backColor);
					g.fillRect(x, y, textLength, textHeight);
				}
				g.setColor(color);
				g.drawString(text, x, y + metrics.getAscent());
			}
			g.dispose();
			return image;
	}

	public static BufferedImage resize(BufferedImage image, int width, int height)
	{
		BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = target.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		return target;
	}
	
	public static BufferedImage resize2(BufferedImage image, int width, int height)
	{
		double rw = (double)width / image.getWidth();
		double rh = (double)height /image.getHeight();
		AffineTransform transform = AffineTransform.getScaleInstance(rw, rh);
		AffineTransformOp op = new AffineTransformOp(transform, null);
		return (BufferedImage)op.filter(image, null);
	}
	
	/**
	 * 缩放
	 * 
	 * @param filePath
	 *            图片路径
	 * @param height
	 *            高度
	 * @param width
	 *            宽度
	 * @param bb
	 *            比例不对时是否需要补�?
	 */
	public static BufferedImage resize(BufferedImage bi, int height, int width, boolean bb) {
		double ratio = 0.0; // 缩放比例
		Image itemp = bi.getScaledInstance(width, height,
				BufferedImage.SCALE_SMOOTH);
		// 计算比例
		if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
			if (bi.getHeight() > bi.getWidth()) {
				ratio = (new Integer(height)).doubleValue()
						/ bi.getHeight();
			} else {
				ratio = (new Integer(width)).doubleValue() / bi.getWidth();
			}
			AffineTransformOp op = new AffineTransformOp(AffineTransform
					.getScaleInstance(ratio, ratio), null);
			itemp = op.filter(bi, null);
		}
		if (bb) {
			BufferedImage image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, width, height);
			if (width == itemp.getWidth(null))
				g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2,
						itemp.getWidth(null), itemp.getHeight(null),
						Color.white, null);
			else
				g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0,
						itemp.getWidth(null), itemp.getHeight(null),
						Color.white, null);
			g.dispose();
			itemp = image;
		}
		return (BufferedImage) itemp;
	}

	public static int getLength(String text) {
		int length = 0;
		for (int i = 0; i < text.length(); i++) {
			if (new String(text.charAt(i) + "").getBytes().length > 1) {
				length += 2;
			} else {
				length += 1;
			}
		}
		return length / 2;
	}
	
	public static BufferedImage composite(BufferedImage backImage, BufferedImage frontImage, 
			int x, int y, int frontWidth, int frontHeight, float alpha) {
		Graphics2D g = backImage.createGraphics();
//		int frontWidth = frontImage.getWidth();
//		int frontHeight = frontImage.getHeight();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
		g.drawImage(frontImage, x, y, frontWidth, frontHeight, null);
		g.dispose();
		return backImage;
	}
	
	/**
	 * Rotates image 90 degrees to the left.
	 */
	public static BufferedImage rotateLeft(BufferedImage bufferedImage) {

		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();

		BufferedImage result = new BufferedImage(height, width,
				BufferedImage.TYPE_INT_BGR);
		Graphics2D g = result.createGraphics();
		result = g.getDeviceConfiguration().createCompatibleImage(100, 100, Transparency.TRANSLUCENT);
		g.dispose();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int rgb = bufferedImage.getRGB(x, y);
				result.setRGB(y, width - x - 1, rgb);
			}
		}
		return result;
	}
	
	/**
	 * Rotates image 90 degrees to the right.
	 */
	public static BufferedImage rotateRight(BufferedImage bufferedImage) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();

		BufferedImage result = new BufferedImage(height, width,	BufferedImage.TYPE_INT_BGR);
		Graphics2D g = result.createGraphics();
		result = g.getDeviceConfiguration().createCompatibleImage(100, 100, Transparency.TRANSLUCENT);
		g.dispose();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int rgb = bufferedImage.getRGB(x, y);
				result.setRGB(height - y - 1, x, rgb);
			}
		}

		return result;
	}
	
	public static void writeImage(BufferedImage image, String format, float quality, OutputStream out) throws IOException
	{
		ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(image);
		Iterator<ImageWriter> iter = ImageIO.getImageWriters(type, format);
		ImageWriter writer = (ImageWriter)iter.next();
		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);
		writer.setOutput(ImageIO.createImageOutputStream(out));
		IIOImage iioImage = new IIOImage(image, null, null);//writer.getDefaultImageMetadata(type, param));
        writer.write(writer.getDefaultImageMetadata(type, param), iioImage, param);
        writer.dispose();
	}
}
