package org.knotsgm.io;

import java.io.File;

import org.knotsgm.core.Knot;

public interface KnotImporter
{
	Knot importKnot(File file) throws Exception;
	
}
