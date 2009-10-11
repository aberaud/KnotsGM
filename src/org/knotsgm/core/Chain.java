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

import java.io.Serializable;
import java.util.ListIterator;
import java.util.Vector;

import org.knotsgm.utils.CircularList;

/**
 * The Chain class is a list of ChainPoint, with functions to manipulate it.
 * The Chain creation process has two parts : filling the chain with points and calling the init() function.
 * After the Chain have been initialised, you can start manipulate it.
 * @author Adrien Béraud
 */
public class Chain extends CircularList<ChainPoint> implements Serializable
{
	
	private static final long serialVersionUID = -196446557047680831L;
	
	private boolean initset = false;
	private boolean closed = false;
	
	public Chain()
	{
	}
	
	/**
	 * Return true if the chain is closed and false if the chain is open (eg. a partial chain).
	 * @return true if the chain is closed and false if the chain is open (eg. a partial chain)
	 */
	public boolean isClosed()
	{
		return closed;
	}
	
	/**
	 * Change the state (closed or open) of the chain by automatically adding or removing a segment at te end of the chain.
	 * @param isclosed true for a closed chain and false for an open chain
	 */
	public void setClosed(boolean isclosed)
	{
		if(closed == isclosed || size()==0) return;
		closed = isclosed;
	}
	
	/**
	 * Compute the drawing point of the chain (for bezier curve).
	 * @throws Exception If the chain have already been initialised or if the chain is too small.
	 */
	public void init() throws Exception
	{
		if(initset) throw new Exception("Trying to init a chain already treated.");
		if(size()<2) throw new Exception("Chain too small to init.");
		
		KnotPoint lastp = KnotPoint.center(getLast().getPoint(), getFirst().getPoint());
		KnotPoint lastd = getFirst().getPoint();
		for(ChainPoint point : this)
		{
			point.setDrawingPoint(point.next().getPoint());
			point.setPoint(KnotPoint.center(point.getPoint(), point.next().getPoint()));
		}
		
		getLast().setDrawingPoint(lastd);
		getLast().setPoint(lastp);
		
		initset = true;
		interpolate();
		interpolate();
		interpolate();
		//interpolate();
		//interpolate();
		//interpolate();
	}
	
	private void interpolate()
	{
		ListIterator<ChainPoint> iterator = listIterator();
		ChainPoint new_point;
		for(ChainPoint point = iterator.next(); iterator.hasNext(); point = iterator.next())
		{
			new_point = new ChainPoint(point.getQuadCenterPoint(), this);
			new_point.setDrawingPoint(KnotPoint.center(point.getDrawingPoint(), point.next().getPoint()));
			point.setDrawingPoint(KnotPoint.center(point.getPoint(), point.getDrawingPoint()));
			iterator.add(new_point);
		}
	}
	
	/**
	 * Compute intersections of the chain with itself.
	 * @return A list of detected intersections.
	 */
	public Vector<Intersection> findIntersections()
	{
		Vector<Intersection> inter = new Vector<Intersection>();
		int i;
		KnotPoint itr;
		Boolean flag = false;
		Intersection newi;
		
		ChainPoint start = get(2), end = getLast().previous(), currentx, currenty;
		for(currentx = getFirst(); currentx != end; currentx=currentx.next())
		{
			for(currenty = start; currenty != getFirst(); currenty = currenty.next())
			{
				itr = ChainPoint.computeIntersection(currentx, currenty);
				
				if(itr != null && (currentx != getFirst() || currenty != getLast()))
				{
					newi = new Intersection(itr, flag, currentx, currenty, this);
					//flag = !flag;					
					inter.add(newi);
				}
			}
			start = start.next();
		}
		
		for(i=0; i<inter.size()-1; i++)
		{
			if(KnotPoint.distance(inter.get(i).point, inter.get(i+1).point)<2)
			{
				inter.remove(i);
				i--;
			}
		}
		return inter;
	}
	
	/**
	 * Compute intersections of the chain with another chain.
	 * @param other The other chain.
	 * @return A list of detected intersections.
	 */
	public Vector<Intersection> findIntersections(Chain other)
	{
		Vector<Intersection> inter = new Vector<Intersection>();
		Boolean flag = false; 
		KnotPoint itr,lap = new KnotPoint(-32, -32);
		Intersection newi;
		
		for(ChainPoint seg1 : this)
		{
			for(ChainPoint seg2 : other)
			{
				itr = ChainPoint.computeIntersection(seg1, seg2);
				
				if(itr != null && !itr.equals(lap))
				{
					newi = new Intersection(itr, flag, seg1, seg2, this, other);
					
					inter.add(newi);
					
					//flag = !flag;
					lap = itr;
				}
			}
		}
		
		return inter;
	}
	
	/**
	 * Compute intersections between two chains.
	 * @param chain1 The first chain
	 * @param chain2 The second chain
	 * @return A list of intersections.
	 */
	public static Vector<Intersection> setIntersections(Chain chain1, Chain chain2)
	{
		return chain1.findIntersections(chain2);
	}
	
	/**
	 * Move all the points of the chain by subtracting the coordinates of a point. 
	 * @param p The vector-point whose coordinates will be subtracted to the coordinates of all the points of the chain.
	 */
	public void subtract(KnotPoint p)
	{
		for(ChainPoint point : this) point.subtract(p);
	}
	
	/**
	 * Release the object from the memory.
	 */
	public void dispose()
	{
		for(ChainPoint point : this) point.dispose();
		clear();
	}
}

