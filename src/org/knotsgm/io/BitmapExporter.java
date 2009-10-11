package org.knotsgm.io;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.knotsgm.core.Knot;
import org.knotsgm.ui.swing.KnotGraphic;

public abstract class BitmapExporter implements KnotExporter, BitmapImageExporter
{
	
	@Override
	public void exportKnot(KnotGraphic knot, File file) throws FileNotFoundException, IOException
	{
		ImageWriter writer = getImageWriter();
		ImageWriteParam iwp = getImageWriterParams();
		
		FileImageOutputStream output;
		
		output = new FileImageOutputStream(file);
		writer.setOutput(output);
		BufferedImage tamponSauvegarde = new BufferedImage(knot.getWidth(), knot.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = tamponSauvegarde.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, knot.getPreferredSize().width, knot.getPreferredSize().height);
		knot.paint(g); 
		
		IIOImage image = new IIOImage(tamponSauvegarde, null, null);
		writer.write(null, image, iwp);
		writer.dispose();
		output.flush();
		output.close();	
	}
	
	@Override
	public void exportKnot(Knot knot, File file)
	{
		
	}
	
}
