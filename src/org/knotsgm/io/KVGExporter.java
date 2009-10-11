package org.knotsgm.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.knotsgm.core.ChainPoint;
import org.knotsgm.core.Intersection;
import org.knotsgm.core.Knot;
import org.knotsgm.core.KnotString;
import org.knotsgm.core.KnotStringBase;
import org.knotsgm.ui.swing.KnotGraphic;

public final class KVGExporter extends KVGManipulator  implements KnotExporter
{
	private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	private static final String NL_CHAR = "\n";
	private static final String TAB_CHAR = "\t";
	
	@Override
	public void exportKnot(Knot knot, File file)
	{
		String document_xml = getDocumentXML(knot);
		if(file.exists()) file.delete();
		
		Writer output;
		
		try
		{
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
			
			output.write(document_xml);
			output.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void exportKnot(KnotGraphic knot, File file)
	{
		exportKnot(knot.getKnot(), file);
	}
	
	private String getDocumentXML(Knot knot)
	{
		String document = "";
		document += XML_DECLARATION;
		document += newline(0) + getTag(KVG_TAG + getAttribute("version", KVG_VERSION), false);
		document += getKnotXML(knot, 1);
		document += newline(0) + getTag(KVG_TAG, true);
		document += newline(0) + newline(0);
		return document;
	}
	
	private String getKnotXML(Knot knot, int ind)
	{
		String knotstr = "";
		knotstr += newline(ind) + getTag(KNOT_TAG, false);
		
		for(Intersection inter : knot.getIntersections()) 
			knotstr += getIntersectionXML(inter, ind+1);
		
		knotstr += newline(ind);
		
		for(KnotStringBase string : knot.getStrings())
			knotstr += getStringXML((KnotString)string, ind+1);
		
		knotstr += newline(ind) + getTag(KNOT_TAG, true);
		return knotstr;
	}
	
	private String getIntersectionXML(Intersection intersection, int ind)
	{
		return newline(ind) + getEmptyTag(INTERSECTION_TAG
				+ getAttribute("id", INTERSECTION_NAME+intersection.getID())
				+ getAttribute("x", String.valueOf(intersection.getLocation().x))
				+ getAttribute("y", String.valueOf(intersection.getLocation().y))
				+ getAttribute("polarity", intersection.getPolarity()?"true":"false"));
	}
	
	private String getStringXML(KnotString string, int ind)
	{
		String stringstr = "";
		stringstr += newline(ind) + getTag(STRING_TAG, false);
		
		ChainPoint point = string.getFirst();
		ChainPoint first;
		
		if(string.getIntersectionsNumber() > 0)
			while(point.getIntersection() == null) point = point.next();
		
		first = point;
		
		Intersection start_inter = first.getIntersection();
		Intersection end_inter;
		String coords;
		do
		{
			coords = "";
			do
			{
				coords += String.valueOf(point.getPoint().x) + " " + String.valueOf(point.getPoint().y + " ");
				coords += String.valueOf(point.getDrawingPoint().x) + " " + String.valueOf(point.getDrawingPoint().y + " ");
				point = point.next();
				
			}
			while(point.getIntersection() == null);
			end_inter = point.getIntersection();
			stringstr += newline(ind+1) + getEmptyTag(STRINGBIT_TAG 
					+ getAttribute("start", INTERSECTION_NAME+start_inter.getID())
					+ getAttribute("end", INTERSECTION_NAME+end_inter.getID())
					+ getAttribute("path", coords));
			start_inter = end_inter;
		}
		while(first != point);
		
		stringstr += newline(ind) + getTag(STRING_TAG, true);
		return stringstr;
	}
	
	private String getTag(String name, boolean closing)
	{
		return "<" + (closing?"/":"") + name + ">";
	}
	
	private String getEmptyTag(String name)
	{
		return "<" + name + "/>";
	}
	
	private String getAttribute(String name, String value)
	{
		return " " + name + "=\"" + value + "\"";
	}
	
	private String newline(int num)
	{
		String newline = NL_CHAR;
		for(; num>0; num--) newline += TAB_CHAR;
		return newline;
	}
	
	
}
