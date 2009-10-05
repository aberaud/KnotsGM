package org.knotsgm.io;

import java.io.File;

import org.knotsgm.core.Knot;

public interface KnotExporter
{
	void exportKnot(Knot knot, File file);
}
