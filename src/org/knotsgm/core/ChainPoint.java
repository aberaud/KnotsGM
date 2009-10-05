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

import org.knotsgm.utils.CircularListElement;
/**
 * A ChainPoint is a Point associated to a Chain.
 * @author Adrien Béraud
 */
public class ChainPoint extends CircularListElement<ChainPoint>
{
	private static final long serialVersionUID = -8728377437887543952L;
	
	private Chain parentChain;
	private KnotPoint point;
	private KnotPoint drawingPoint;
	private Intersection intersection = null;
	private Intersection nearIntersection = null;
	private ChainPoint nearIntersectionPoint;
	
	/**
	 * Create a ChainPoint from double coordinates.
	 * @param x The x coordinate of the point.
	 * @param y The y coordinate of the point.
	 * @param parent The parent chain.
	 */
	public ChainPoint(double x, double y, Chain parent)
	{
		//super(x, y);
		parentChain = parent;
		point = new KnotPoint(x, y);
	}
	
	/**
	 * Create a ChainPoint from a Point.
	 * @param point The instance of Point to use.
	 * @param parent The parent chain.
	 */
	public ChainPoint(KnotPoint point, Chain parent)
	{
		//super(point);
		parentChain = parent;
		this.point = point;
	}
	
	public void setIntersection(Intersection intersection)
	{
		this.intersection = intersection;
		nearIntersection = intersection;
		setNearIntersectionPoint(this);
	}
	
	public Intersection getIntersection()
	{
		return intersection;
	}
	
	public void setNearIntersection(Intersection intersection, ChainPoint p)
	{
		nearIntersection = intersection;
		setNearIntersectionPoint(p);
	}
	
	public Intersection getNearIntersection()
	{
		return nearIntersection;
	}
	
	public double length()
	{
		return KnotPoint.distance(point, next().point);
	}
	
	public static KnotPoint computeIntersection(ChainPoint seg1, ChainPoint seg2)
	{
		KnotPoint c1 = seg1.point,
		c2 = seg1.next().point,
		c3 = seg2.point,
		c4 = seg2.next().point;
		
		double v = (c1.x-c2.x)*(c3.y-c4.y)-(c3.x-c4.x)*(c1.y-c2.y);
		if(v == 0) return null;
		
		double v1 = c1.x*c2.y-c1.y*c2.x;
		double v2 = c3.x*c4.y-c3.y*c4.x;
		
		double X = (v1*(c3.x-c4.x)-v2*(c1.x-c2.x))/v;
		double Y = (v1*(c3.y-c4.y)-v2*(c1.y-c2.y))/v;
		
		if(Math.min(Math.max(c1.x, c2.x), Math.max(c3.x, c4.x)) < X) return null;
		if(Math.max(Math.min(c1.x, c2.x), Math.min(c3.x, c4.x)) > X) return null;
		if(Math.min(Math.max(c1.y, c2.y), Math.max(c3.y, c4.y)) < Y) return null;
		if(Math.max(Math.min(c1.y, c2.y), Math.min(c3.y, c4.y)) > Y) return null;
		
		return new KnotPoint(X, Y);
	}
	
	public KnotPoint getQuadCenterPoint()
	{
		double nx = 0.25D*point.x + 0.5D*drawingPoint.x + 0.25D*next().point.x;
		double ny = 0.25D*point.y + 0.5D*drawingPoint.y + 0.25D*next().point.y;
		return new KnotPoint(nx, ny);
	}
	
	/**
	 * Get the associated Chain object.
	 * @return the associated Chain object.
	 */
	public Chain getChain()
	{
		return parentChain;
	}
	
	public KnotPoint getPoint()
	{
		return point;
	}
	
	protected void setPoint(KnotPoint p)
	{
		point = p;
	}
	
	public KnotPoint getDrawingPoint()
	{
		return drawingPoint;
	}
	
	public void setDrawingPoint(KnotPoint p)
	{
		drawingPoint = p;
	}
	
	public KnotPoint getNextPoint()
	{
		return next().point;
	}
	
	public void setNearIntersectionPoint(ChainPoint nearIntersectionPoint)
	{
		this.nearIntersectionPoint = nearIntersectionPoint;
	}
	
	public ChainPoint getNearIntersectionPoint()
	{
		return nearIntersectionPoint;
	}
	
	public double x()
	{
		return point.x;
	}
	
	public double y()
	{
		return point.y;
	}
	
	public double segmentX()
	{
		return next().x()-x();
	}
	
	public double segmentY()
	{
		return next().y()-y();
	}		
	
	public void dispose()
	{
		parentChain = null;
		point = null;
		drawingPoint = null;
		intersection = null;
		nearIntersection = null;
		setNearIntersectionPoint(null);
	}
	
	public void subtract(KnotPoint p)
	{
		point.subtract(p);
		drawingPoint.subtract(p);
	}
}
