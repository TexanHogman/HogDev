package com.hogdev.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.IndexColorModel;
import java.awt.image.Kernel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * JImager is a simple utility designed to create thumbnails from jpg gif and
 * bmp files. The format of the thumbnail will be a jpg file. Please make any
 * changes desired to this source but keep me informed so I can benefit from
 * them Thanks Hogge rhogge@austin.rr.com 2001-01-19.
 */
public class JImager
{
	int iSize = -1;

	int iWidth = -1;

	int iHeight = -1;

	float flQuality = 1.0f;

	public JImager()
	{
	}

	/**
	 * 
	 * 
	 * @param i
	 */
	public void setSize(int i)
	{
		this.iSize = i;
	}

	/**
	 * 
	 * 
	 * @param i
	 */
	public void setHeight(int i)
	{
		this.iHeight = i;
	}

	/**
	 * 
	 * 
	 * @param i
	 */
	public void setWidth(int i)
	{
		this.iWidth = i;
	}

	/**
	 * 
	 * 
	 * @param f
	 */
	public void setQuality(float f)
	{
		this.flQuality = f;
	}

	public void doResize(InputStream is, OutputStream os) throws IOException
	{
		JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(is);
		BufferedImage imgSrc = decoder.decodeAsBufferedImage();

		int iW = imgSrc.getWidth(null);
		int iH = imgSrc.getHeight(null);

		int iNewW = -1;
		int iNewH = -1;

		// reduce the image here!!! based upon options
		if (iSize != -1)
		{
			if (iW > iH && iW != iSize)
			{
				iNewH = (iH * iSize) / iW;
				iNewW = iSize;
			} else if (iH > iW && iH != iSize)
			{
				iNewW = (iW * iSize) / iH;
				iNewH = iSize;
			} else if (iH == iW && iH != iSize)
			{
				iNewW = iNewH = iSize;
			} else
			{
				// must already be the correct size
			}
		} else
		{
			iNewW = iWidth;
			iNewH = iHeight;
		}

		BufferedImage biNew;
		if (iNewW != -1 && iNewH != -1)
		{
			biNew = new BufferedImage(iNewW, iNewH, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2dNew = biNew.createGraphics();
			g2dNew.clearRect(0, 0, iNewW, iNewH);
			g2dNew.drawImage(imgSrc, 0, 0, iNewW, iNewH, 0, 0,
					imgSrc.getWidth(null), imgSrc.getHeight(null), null);
		} else
		{
			biNew = imgSrc;
		}

		// the encoder seems to have problems if the outputstream becomes
		// invalid (like a servlet outputstream will
		// if the user cancels, so I will give it a byte array os then shove its
		// data to the passed in os.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(baos);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(biNew);
		param.setQuality(flQuality, false);
		encoder.setJPEGEncodeParam(param);

		encoder.encode(biNew);

		os.write(baos.toByteArray());
		baos.close();
	}

	public static void doCrop(InputStream is, OutputStream os, Rectangle rect)
			throws IOException
	{
		JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(is);
		BufferedImage imgSrc = decoder.decodeAsBufferedImage();

		BufferedImage biNew = doCrop(imgSrc, rect);

		// the encoder seems to have problems if the outputstream becomes
		// invalid (like a servlet outputstream will
		// if the user cancels, so I will give it a byte array os then shove its
		// data to the passed in os.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(baos);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(biNew);
		param.setQuality(1.0f, false);
		encoder.setJPEGEncodeParam(param);

		encoder.encode(biNew);

		os.write(baos.toByteArray());
		baos.close();
	}

	public static BufferedImage doCrop(BufferedImage imgSrc, Rectangle rect)
			throws IOException
	{
		return imgSrc.getSubimage(rect.x, rect.y, rect.width, rect.height);
	}

	public static BufferedImage doNegative(BufferedImage img)
	{
		Color col;
		for (int x = 0; x < img.getWidth(); x++)
		{ // width
			for (int y = 0; y < img.getHeight(); y++)
			{ // height

				int RGBA = img.getRGB(x, y); // gets RGBA data for the specific
												// pixel

				col = new Color(RGBA, true); // get the color data of the
												// specific pixel

				col = new Color(Math.abs(col.getRed() - 255), Math.abs(col
						.getGreen() - 255), Math.abs(col.getBlue() - 255)); // Swaps
																			// values
				// i.e. 255, 255, 255 (white)
				// becomes 0, 0, 0 (black)

				img.setRGB(x, y, col.getRGB()); // set the pixel to the altered
												// colors
			}
		}
		return img;
	}

	public static BufferedImage doBlur(BufferedImage img)
	{
		Kernel kernel = new Kernel(3, 3, new float[] { 1f / 9f, 1f / 9f,
				1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f });

		BufferedImageOp op = new ConvolveOp(kernel);
		return op.filter(img, null);
	}

	public static BufferedImage doSharpen(BufferedImage img)
	{
		Kernel kernel = new Kernel(3, 3, new float[] { -1, -1, -1, -1, 9, -1,
				-1, -1, -1 });

		BufferedImageOp op = new ConvolveOp(kernel);
		return op.filter(img, null);
	}

	public static BufferedImage doGrayscale(BufferedImage img)
	{
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		return op.filter(img, null);
	}

	public static BufferedImage doConvert1(BufferedImage src)
	{
		IndexColorModel icm = new IndexColorModel(1, 2, new byte[] { (byte) 0,
				(byte) 0xFF }, new byte[] { (byte) 0, (byte) 0xFF },
				new byte[] { (byte) 0, (byte) 0xFF });

		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(),
				BufferedImage.TYPE_BYTE_BINARY, icm);

		ColorConvertOp cco = new ColorConvertOp(src.getColorModel()
				.getColorSpace(), dest.getColorModel().getColorSpace(), null);

		cco.filter(src, dest);

		return dest;
	}

	public static void main(String args[])
	{
		try
		{
			long start = System.currentTimeMillis();
			BufferedImage img = ImageIO
					.read(new File(
							"C:\\Users\\rhogge\\Dropbox\\RaceControl\\raceimage-orig.jpg"));
			System.out.println(System.currentTimeMillis() - start);
			img = doCrop(img, new Rectangle(3, 74, 1004, 651));
			System.out.println(System.currentTimeMillis() - start);
			img = doNegative(img);
			System.out.println(System.currentTimeMillis() - start);
//			img = doConvert1(img);
			ImageIO.write(
					img,
					"jpg",
					new File(
							"C:\\Users\\rhogge\\Dropbox\\RaceControl\\raceimage-processed2.jpg"));
			System.out.println(System.currentTimeMillis() - start);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
