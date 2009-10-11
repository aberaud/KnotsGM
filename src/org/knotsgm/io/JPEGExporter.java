package org.knotsgm.io;

import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

public final class JPEGExporter extends BitmapExporter
{
	public static final String JPEG_EXTENSION = "jpeg";
	
	ImageWriter writer;
	ImageWriteParam iwp;
	
	public JPEGExporter()
	{
		createWriter(.8f);
	}
	
	public JPEGExporter(float quality)
	{
		createWriter(quality);
	}
	
	private void createWriter(float quality)
	{
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("JPEG");
		writer = iter.next();
		iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(quality);
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
