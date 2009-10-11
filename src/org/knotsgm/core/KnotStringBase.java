/* 
 * Copyright (C) 2008-2009 Adrien Béraud <adrienberaud@gmail.com>
 * 
 * This file is a part of KnotsGM, The Knots Graphical Manipulator
 * 
 * KnotsGM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.knotsgm.core;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

import org.knotsgm.ui.swing.KnotsGM;

public abstract class KnotStringBase extends Chain
{
	private static final long serialVersionUID = -5843303869472684919L;
	
	protected Vector<Intersection> intersections;
	protected Vector<Intersection> own_intersections;
	protected Vector<Intersection> others_intersections;
	
	protected Rectangle bounds;
	
	public KnotStringBase()
	{
		intersections = new Vector<Intersection>();
		own_intersections = new Vector<Intersection>();
		others_intersections = new Vector<Intersection>();
	}
	
	public void updateIntersections()
	{
		for(Intersection inter : own_intersections) inter.updateNearIntersections();
		
		try
		{
			for(Intersection inter : others_intersections) inter.updateNearIntersections(this);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Compute intersections with another string and add them to both strings.
	 * @param string the other string
	 * @return An list of detected intersections
	 */
	public Vector<Intersection> setIntersections(KnotStringBase string)
	{
		if(string == this)
			return setIntersections();
		
		Vector<Intersection> newi = Chain.setIntersections(this, string);
		
		KnotsGM.debugMessage("Found " + newi.size() + " intersections." , 2);
		
		getIntersections(newi);
		string.getIntersections(newi);
		
		
		
		return newi;
	}
	
	public void refreshIntersections()
	{
		for(ChainPoint point : this)
		{
			Intersection inters = point.getIntersection();
			if(inters == null || intersections.contains(inters)) continue;
			//System.out.println("fgdghhgfgf");
			intersections.add(inters);
			if(inters.getChain1() == inters.getChain2()) own_intersections.add(inters);
			else others_intersections.add(inters);
		}
	}
	
	/**
	 * Compute intersections of the string with itself and add them to the string
	 * @return An list of detected intersections
	 */
	public Vector<Intersection> setIntersections()
	{
		Vector<Intersection> inters = findIntersections();
		own_intersections.addAll(inters);
		intersections.addAll(inters);
		return inters;
	}
	
	private void getIntersections(Vector<Intersection> newi)
	{
		intersections.addAll(newi);
		others_intersections.addAll(newi);
	}
	
	public int getIntersectionsNumber()
	{
		return intersections.size();
	}
	
	public Dimension getDimension()
	{
		if(bounds == null) refreshDimensions();
		return bounds.getSize();
	}
	
	public Rectangle getBounds()
	{
		if(bounds == null) refreshDimensions();
		return (Rectangle)bounds.clone();
	}
	
	public void refreshDimensions()
	{
		Rectangle rect = new Rectangle(0, 0, -1, -1);
		for (ChainPoint i : this)
			rect.add(new Point((int)i.x(), (int)i.y()));
		bounds = rect;
	}
	
	@Override
	public void subtract(KnotPoint p)
	{
		if(p.equals(new KnotPoint(0., 0.))) return;
		super.subtract(p);
		refreshDimensions();
	}
	
	/**
	 * Completely remove an intersection form the string. The intersection may stay linked to other strings.
	 * If the intersection doesn't belong to the string, this will have no effect.
	 * @param intersection The intersection to remove
	 */
	public void removeIntersection(Intersection intersection)
	{
		if (!intersections.contains(intersection))
			return;
		
		for (ChainPoint point : this)
		{
			if (point.getNearIntersection() == intersection)
				point.setNearIntersection(null, null);
			if (point.getIntersection() == intersection)
				point.setIntersection(null);
		}
		
		intersections.remove(intersection);
		own_intersections.remove(intersection);
		others_intersections.remove(intersection);
	}
	
	@Override
	public void dispose()
	{
		for(Intersection inter : own_intersections) inter.dispose();
		for(Intersection inter : others_intersections) 
		{
			KnotStringBase other = (KnotStringBase) inter.getOtherChain(this);
			other.removeIntersection(inter);
			for (ChainPoint point : this)
			{
				if (point.getNearIntersection() == inter)
					point.setNearIntersection(null, null);
				if (point.getIntersection() == inter)
					point.setIntersection(null);
			}
			inter.dispose();
		}
		intersections.clear();
		own_intersections.clear();
		others_intersections.clear();
		
		own_intersections = null;
		others_intersections = null;
		intersections = null;
		bounds = null;
		
		super.dispose();
	}
}
