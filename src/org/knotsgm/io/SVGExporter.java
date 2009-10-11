package org.knotsgm.io;

import java.io.File;

import org.knotsgm.core.Knot;
import org.knotsgm.ui.swing.KnotGraphic;

public final class SVGExporter implements KnotExporter
{
	
	@Override
	public void exportKnot(Knot knot, File file)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void exportKnot(KnotGraphic knot, File file)
	{
		exportKnot(knot.getKnot(), file);
	}
	
	
}
