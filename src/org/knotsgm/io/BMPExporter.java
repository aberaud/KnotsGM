package org.knotsgm.io;

import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

public class BMPExporter extends BitmapExporter
{
	public static final String BMP_EXTENSION = "bmp";
	
	ImageWriter writer;
	ImageWriteParam iwp;
	
	public BMPExporter()
	{
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("BMP");
		writer = iter.next();
		iwp = writer.getDefaultWriteParam();
	}
	
	@Override
	public ImageWriter getImageWriter()
	{
		return writer;
	}
	
	@Override
	public ImageWriteParam getImageWriterParams()
	{
		return iwp;
	}
}
