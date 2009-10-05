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

/**
 * Representation of a knot without information of completness (the knot can be complete or partial)
 * @author Adrien Béraud <adrienberaud@gmail.com>
 * @see Knot
 * @see KnotStringBase
 */
public abstract class KnotBase
{
	private static final long serialVersionUID = 1234331408262189991L;
	public static final int margin = 20;
	
	protected Vector<KnotStringBase> strings;
	protected Vector<Intersection> intersections;
	
	protected Dimension dimension = new Dimension(0, 0);
	
	public KnotBase()
	{
		strings = new Vector<KnotStringBase>();
		intersections = new Vector<Intersection>();
	}
	
	/**
	 * Add a string to the knot, compute intersection and normalise the coordinates 
	 * (make sure that all coordinates are coerents and positive)
	 * @param string A string to add to the knot, with coordinates expressed from the top-left corner of the knot
	 * @return The difference between the previous knot top-left position and the new knot ones.
	 */
	public Point addString(KnotStringBase string)
	{
		if (strings.indexOf(string) != -1)
			return null;
		
		intersections.addAll(string.setIntersections());
		
		for (KnotStringBase cstring : strings)
			intersections.addAll(cstring.setIntersections(string));
		
		strings.add(string);
		
		for (KnotStringBase cstring : strings)
			cstring.updateIntersections();
		
		return normalise();
	}
	
	public void addRawString(KnotStringBase string)
	{
		if (strings.indexOf(string) != -1) return;
		
		strings.add(string);
		for (Intersection inter : string.intersections)
			if(!intersections.contains(inter)) intersections.add(inter);
	}
	
	public void refreshIntersections()
	{
		for (KnotStringBase string : strings)
			for (Intersection inter : string.intersections)
				if(!intersections.contains(inter)) intersections.add(inter);
	}
	
	/**
	 * Remove a string from the knot. This will have no effect if the string doesn't belong to the knot 
	 * or if it's the last string of the knot.
	 * @param string The string to remove
	 * @return The difference between the previous knot top-left position and the new knot ones.
	 */
	public Point removeString(KnotStringBase string)
	{
		if(!strings.contains(string)) return new Point();
		
		strings.remove(string);
		intersections.removeAll(string.intersections);
		
		for (Intersection inter : string.others_intersections)
			((KnotStringBase)inter.getOtherChain(string)).removeIntersection(inter);
		
		/*for (KnotStringBase cstring : strings)
		{
			
			cstring.others_intersections.removeAll(string.others_intersections);
			cstring.updateIntersections();
		}*/
		
		string.dispose();
		
		return normalise();
	}
	
	protected Point normalise()
	{
		Rectangle old_dims = getBounds();
		Point diff = old_dims.getLocation();
		translate(new org.knotsgm.core.KnotPoint(diff));
		return diff;
	}
	
	public boolean isOptimizedDrawingEnabled()
	{
		return false;
	}
	
	private void addMargins(Rectangle rect)
	{
		rect.x -= margin;
		rect.y -= margin;
		rect.height += 2*margin;
		rect.width += 2*margin;
	}
	
	private void subtract(org.knotsgm.core.KnotPoint point)
	{
		if(point.x == 0.D && point.y == 0.D) return;
		for (KnotStringBase cstring : strings)
		{
			cstring.subtract(point);
		}
		for (Intersection inter : intersections)
		{
			inter.point.subtract(point);
		}
	}
	
	private void translate(org.knotsgm.core.KnotPoint point)
	{
		subtract(point);
	}
	
	/**
	 * Compute the dimensions of the knot. Performance is o(n) where n is the numbers of points in the knot. 
	 * @return The dimensions of the knot
	 */
	public Dimension getDimension()
	{
		Rectangle dims = new Rectangle(0, 0, -1, -1);
		for(KnotStringBase i : strings)
		{
			dims.add(i.getBounds());
		}
		addMargins(dims);
		return dims.getSize();
	}
	
	/**
	 * Return the bounds of the knot, the top-left corner should allways be (0, 0)
	 * but can be different in some special situations.
	 * @return The bounds of the knot
	 * @see #getDimension()
	 */
	public Rectangle getBounds()
	{
		Rectangle dims = new Rectangle(0, 0, -1, -1);
		for(KnotStringBase i : strings)
		{
			dims.add(i.getBounds());
		}
		addMargins(dims);
		return (Rectangle)dims.clone();
	}
	
	/**
	 * Returns a list of the knot's intersections.
	 * @return A list of the knot's intersections.
	 */
	public Vector<Intersection> getIntersections()
	{
		return intersections;
	}
	
	/**
	 * Returns a list of the knot's strings.
	 * @return A list of the knot's strings.
	 */
	public Vector<KnotStringBase> getStrings()
	{
		return strings;
	}
	
	/**
	 * Clear the knot to be easely barbage-collected. The knot become unusable. Any function call on the knot will probably cause an error.
	 */
	public void dispose()
	{
		for(KnotStringBase string : strings) string.dispose();
		strings.clear();
		intersections.clear();
		dimension = null;
	}
	
}
