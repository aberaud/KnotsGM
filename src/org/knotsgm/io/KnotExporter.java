package org.knotsgm.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.knotsgm.ui.swing.KnotGraphic;

public interface KnotExporter
{
	//void exportKnot(Knot knot, File file) throws FileNotFoundException, IOException;
	void exportKnot(KnotGraphic knot, File file) throws FileNotFoundException, IOException;
}
