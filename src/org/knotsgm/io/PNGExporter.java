package org.knotsgm.io;

import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

public class PNGExporter extends BitmapExporter
{
	public static final String PNG_EXTENSION = "png";
	
	ImageWriter writer;
	ImageWriteParam iwp;
	
	public PNGExporter()
	{
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("PNG");
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
