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
import org.knotsgm.core.Style;
import org.knotsgm.ui.swing.KnotGraphic;
import org.knotsgm.ui.swing.KnotStringGraphic;

public final class KVGExporter extends KVGManipulator implements KnotExporter
{
	private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	private static final String NL_CHAR = "\n";
	private static final String TAB_CHAR = "\t";
	
	@Override
	public void exportKnot(KnotGraphic knotg, File file)
	{
		String document_xml = getDocumentXML(knotg);
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
	
	/*@Override
	public void exportKnot(KnotGraphic knot, File file)
	{
		exportKnot(knot.getKnot(), file);
	}*/
	
	private String getDocumentXML(KnotGraphic knot)
	{
		String document = "";
		document += XML_DECLARATION;
		document += newline(0) + getTag(KVG_TAG + getAttribute("version", KVG_VERSION), false);
		document += newline(1);
		document += getGlobalStyleXML(new Style(), 1);
		document += newline(1);
		document += getKnotXML(knot, 1);
		document += newline(0) + getTag(KVG_TAG, true);
		document += newline(0) + newline(0);
		return document;
	}
	
	private String getKnotXML(KnotGraphic knotg, int ind)
	{
		Knot knot = knotg.getKnot();
		String knotstr = "";
		knotstr += newline(ind) + getTag(KNOT_TAG, false);
		
		for(Intersection inter : knot.getIntersections()) 
			knotstr += getIntersectionXML(inter, ind+1);
		
		knotstr += newline(ind);
		
		for(KnotStringGraphic string : knotg.getStrings())
			knotstr += getStringXML(string, ind+1);
		
		knotstr += newline(ind) + getTag(KNOT_TAG, true);
		return knotstr;
	}
	
	private String getGlobalStyleXML(Style style, int ind)
	{
		String content = "";
		content += newline(ind+1) + "string {";
		content += newline(ind+2) + getCSSProperty("type", style.getMode()?"inside":"outside");
		content += newline(ind+1) + "}";
		
		return newline(ind) + getTag("style" + getAttribute("type", "text/css"), false)+getCDATAProtection(content, ind)+getTag("style", true);
	}
	
	private String getIntersectionXML(Intersection intersection, int ind)
	{
		return newline(ind) + getEmptyTag(INTERSECTION_TAG
				+ getAttribute("id", INTERSECTION_NAME+intersection.getID())
				+ getAttribute("x", String.valueOf(intersection.getLocation().x))
				+ getAttribute("y", String.valueOf(intersection.getLocation().y))
				+ getAttribute("polarity", intersection.getPolarity()?"true":"false"));
	}
	
	private String getStringXML(KnotStringGraphic stringg, int ind)
	{
		KnotString string = stringg.getString();
		String stringstr = "";
		String style_propreties = "";
		Style style = stringg.getStyle();
		if(!style.followGlobal) style_propreties += getCSSProperty("style", style.mode?"inside":"outside");
		style_propreties += getCSSProperty("stroke", "0x"+Integer.toHexString(style.color.getRGB()));
		
		stringstr += newline(ind) + getTag(STRING_TAG + getAttribute("style", style_propreties), false);
		
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
	
	private String getCSSProperty(String name, String value)
	{
		return name + ": " + value + ";";
	}
	
	private String getCDATAProtection(String content, int ind)
	{
		return "<![CDATA[" + content + newline(ind) + "]]>";
	}
	
	private String newline(int num)
	{
		String newline = NL_CHAR;
		for(; num>0; num--) newline += TAB_CHAR;
		return newline;
	}
	
	
}
