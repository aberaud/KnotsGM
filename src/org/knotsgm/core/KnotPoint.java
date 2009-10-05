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

//public class Point extends java.awt.Point implements Serializable
public class KnotPoint extends java.awt.geom.Point2D.Double implements Serializable
{
	private static final long serialVersionUID = 5313566744183135598L;
	
	/*public Point()
	{
		super(0., 0.);
	}*/
	
	public KnotPoint(double x, double y)
	{
		super(x, y);
	}
	
	public KnotPoint(java.awt.Point p)
	{
		super(p.x, p.y);
	}
	
	public KnotPoint(KnotPoint point)
	{
		//this(/);
		super(point.x, point.y);
	}
	
	public void subtract(KnotPoint p)
	{
		x -= p.x;
		y -= p.y;
	}
	
	public void add(KnotPoint p)
	{
		x += p.x;
		y += p.y;
	}
	
	public static double distance(KnotPoint p1, KnotPoint p2)
	{
		return p1.distance(p2);
		//return Math.sqrt((p2.x-p1.x)*(p2.x-p1.x)+(p2.y-p1.y)*(p2.y-p1.y));
	}
	
	public static KnotPoint center(KnotPoint p1, KnotPoint p2)
	{
		return new KnotPoint((p1.x+p2.x)/2.D, (p1.y+p2.y)/2.D);
		//return Math.sqrt((p2.x-p1.x)*(p2.x-p1.x)+(p2.y-p1.y)*(p2.y-p1.y));
	}
	
	@Override
	public String toString()
	{
		return "[Point(" + x + ";" + y + ")]";
	}
	
	public void translate(double dx, double dy)
	{
		x += dx;
		y += dy;
	}
}
