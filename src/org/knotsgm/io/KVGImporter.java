package org.knotsgm.io;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.knotsgm.core.ChainPoint;
import org.knotsgm.core.Intersection;
import org.knotsgm.core.Knot;
import org.knotsgm.core.KnotPoint;
import org.knotsgm.core.KnotString;
import org.knotsgm.core.KnotStringBase;
import org.knotsgm.ui.swing.KnotsGM;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class KVGImporter extends KVGManipulator implements KnotImporter
{
	@Override
	public Knot importKnot(File file) throws Exception
	{
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse (file);
		doc.getDocumentElement().normalize();
		
		if(doc.getDocumentElement().getNodeName() != KVG_TAG) throw new Exception("Document is not a KVG document.");
		KnotsGM.debugMessage("Parsing a KVG document, version " + doc.getDocumentElement().getAttribute("version"), 1);
		
		Knot knot = new Knot();
		HashMap<String, Intersection> interc = new HashMap<String, Intersection>();
		Element knot_node = (Element) doc.getElementsByTagName(KNOT_TAG).item(0);
		NodeList intersections = knot_node.getElementsByTagName(INTERSECTION_TAG);
		NodeList strings = knot_node.getElementsByTagName(STRING_TAG);
		NodeList string_bits;
		
		for(int iid = 0; iid < intersections.getLength(); iid++)
		{
			Element current_i = (Element) intersections.item(iid);
			KnotPoint pos = new KnotPoint(new Double(current_i.getAttribute("x")).doubleValue() , new Double(current_i.getAttribute("y")).doubleValue());
			Intersection newi = new Intersection(pos, current_i.getAttribute("polarity")=="true"?true:false);
			interc.put(current_i.getAttribute("id"), newi);
		}
		
		for(int cn = 0; cn < strings.getLength(); cn++)
		{
			KnotString kstring = new KnotString();
			Element current_string = (Element) strings.item(cn);
			string_bits = current_string.getElementsByTagName(STRINGBIT_TAG);
			
			Element current_bit;
			Element previous_bit = (Element) string_bits.item(string_bits.getLength()-1);
			for(int csb = 0; csb<string_bits.getLength(); csb++)
			{
				current_bit = (Element) string_bits.item(csb);
				
				if(!current_bit.getAttribute("start").equals(previous_bit.getAttribute("end")))
					throw new Exception("KVG mal formé");
				
				String path = current_bit.getAttribute("path");
				//String i1 = current_bit.getAttribute("start");
				System.out.println(path);
				
				double[] pos = stringToList(path);
				ChainPoint p = null;
				for(int i = 0; i<pos.length/4; i++)
				{
					p = new ChainPoint(new KnotPoint(pos[4*i], pos[4*i+1]), kstring);
					p.setDrawingPoint(new KnotPoint(pos[4*i+2], pos[4*i+3]));
					kstring.addLast(p);
				}
				Intersection pintersection = interc.get(current_bit.getAttribute("end"));
				
				if(pintersection.getSegment1() == null)
				{
					pintersection.setSegment1(p);
					pintersection.setChain1(kstring);
				}
				else
				{
					pintersection.setSegment2(p);
					pintersection.setChain2(kstring);
				}
				p.setIntersection(pintersection);
				
				previous_bit = current_bit;
			}
			knot.addRawString(kstring);
		}
		
		for(Intersection i : interc.values())
			i.setupSegments();
		
		for(KnotStringBase s : knot.getStrings())
			s.refreshIntersections();
		
		/*for(Intersection i : interc.values())
			i.setupSegments();*/
		
		knot.refreshIntersections();
		
		return knot;
	}
	
	private double[] stringToList(String string)
	{
		//Vector<Double> vect = new Vector<Double>();
		String[] parts = string.split(" ");
		double[] dparts = new double[parts.length];
		for(int i=0; i<parts.length; i++) dparts[i] = new Double(parts[i]).doubleValue();
		return dparts;
	}
	
}
