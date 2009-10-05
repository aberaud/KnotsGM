package org.knotsgm.io;

import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

interface BitmapImageExporter
{
	ImageWriter getImageWriter();
	ImageWriteParam getImageWriterParams();
}
