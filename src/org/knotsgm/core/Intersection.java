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

/**
 * Representation of an intersection of two chains,  
 * the base class to manipulate Knots.
 * This class provides functions to abstract 
 * @author Adrien Béraud
 * @see Chain
 */
//public class Intersection implements Serializable, Comparable<Intersection>
public class Intersection implements Serializable
{
	private static int currentID = 0;
	private int ID;
	
	private static final long serialVersionUID = 3598029820133975960L;
	private static final int keyDistance = 8;
	
	private Chain chain1, chain2;
	private Boolean polarity = false;
	//private Boolean sign = false;
	
	private ChainPoint segment1, segment2;
	protected KnotPoint point;
	
	private Intersection prev1, next1;
	private Intersection prev2, next2;
	
	/**
	 * Contructor for an intersection of a chain with itself.
	 * @param itr The location of the intersection
	 * @param polarity The polarity of the intersection
	 */
	public Intersection(KnotPoint itr, Boolean polarity)
	{
		ID = currentID++;
		point = itr;
		this.polarity = polarity;
	}
	
	/**
	 * Contructor for an intersection of a chain with itself.
	 * @param itr The location of the intersection
	 * @param polarity The polarity of the intersection
	 * @param segment1 The first segment.
	 * @param segment2 The second segment segment. Fist and second segments can't be the same.
	 * @param chain1 The chain that contains the first segment.
	 * @param chain2 The chain that contains the second segment. Fist and second segments can be the same.
	 */
	public Intersection(KnotPoint itr, Boolean polarity, ChainPoint segment1, ChainPoint segment2, Chain chain1, Chain chain2)
	{
		ID = currentID++;
		point = itr;
		this.polarity = polarity;
		this.segment1 = segment1;
		this.segment2 = segment2;
		this.chain1 = chain1;
		this.chain2 = chain2;
		setupSegments();
	}
	
	/**
	 * Contructor for an intersection of a chain with itself.
	 * @param itr The location of the intersection
	 * @param polarity The polarity of the intersection
	 * @param segment1 The first segment.
	 * @param segment2 The second segment segment. Fist and second segments can't be the same.
	 * @param chain The chain that contains both segments.
	 */
	public Intersection(KnotPoint itr, Boolean polarity, ChainPoint segment1, ChainPoint segment2, Chain chain)
	{
		ID = currentID++;
		point = itr;
		this.polarity = polarity;
		this.segment1 = segment1;
		this.segment2 = segment2;
		chain1 = chain;
		chain2 = chain;
		setupSegments();
	}
	
	/**
	 * Retuns the ID of the segments, used for save/load and debuging operations. 
	 * @return the ID of the segment.
	 */
	public int getID()
	{
		return ID;
	}
	
	public void setupSegments()
	{
		segment1.setIntersection(this);
		segment2.setIntersection(this);
		
		setupSegmentNI(segment1);
		setupSegmentNI(segment2);
	}
	
	private void setupSegmentNI(ChainPoint startSegment)
	{
		ChainPoint segment = startSegment;
		double dist;
		
		dist = keyDistance - KnotPoint.distance(segment.getPoint(), point);
		while(dist > 0L)
		{
			segment = segment.previous();
			segment.setNearIntersection(this, startSegment);
			dist -= segment.length();
		}
		
		segment = startSegment;
		dist = keyDistance - KnotPoint.distance(segment.getNextPoint(), point);
		while(dist > 0L)
		{
			segment = segment.next();
			segment.setNearIntersection(this, startSegment);
			dist -= segment.length();
		}
	}
	
	/**
	 * Change the polarity of the intersection
	 */
	public void reverse()
	{
		polarity = !polarity;
	}
	
	/**
	 * Returns the polarity of the intersection.
	 * @return The polarity of the intersection.
	 */
	public Boolean getPolarity()
	{
		return polarity;
	}
	
	/**
	 * Get the polarity of the intersection according to the s segment point of view. Usefull for cases when chain1 == chain2. 
	 * @param s A Segment part of the intersection.
	 * @return The polarity of the intersection according to the s segment point of view.
	 * @throws Exception If s is not one of the two chains of the intersection.
	 */
	public Boolean getPolarity(ChainPoint t)
	{
		ChainPoint o;
		if(t == segment1) o = segment2;
		else if(t == segment2) o = segment1;
		//else throw new Exception("call of getPolarity with wrong Chain object");
		else return false;
		
		double exterior_product = t.segmentX()*o.segmentY()-t.segmentY()*o.segmentX();
		if(exterior_product < 0) return !polarity;
		return polarity;
	}
	
	/**
	 * Set the intersection polarity.
	 * @param polarity The new polarity value.
	 */
	public void setPolarity(Boolean polarity)
	{
		this.polarity = polarity;
	}
	
	public void setPolarity(Boolean polarity, Chain c) throws Exception
	{
		if (c == chain1)
			this.polarity = polarity;
		else if(c == chain2)
			this.polarity = !polarity;
		else
			throw new Exception("call of setPolarity with wrong Chain object");
	}
	
	public Chain getChain1()
	{
		return chain1;
	}
	
	public Chain getChain2()
	{
		return chain2;
	}
	
	public void setChain1(Chain chain)
	{
		chain1 = chain;
	}
	
	public void setChain2(Chain chain)
	{
		chain2 = chain;
	}
	
	public Chain getOtherChain(Chain c)
	{
		if (c == chain1)
			return chain2;
		else if(c == chain2)
			return chain1;
		else
			throw new Error("call of getOtherChain with wrong Chain object");
	}
	
	public ChainPoint getSegment1()
	{
		return segment1;
	}
	
	public ChainPoint getSegment2()
	{
		return segment2;
	}
	
	public void setSegment1(ChainPoint seg)
	{
		segment1 = seg;
	}
	
	public void setSegment2(ChainPoint seg)
	{
		segment2 = seg;
	}
	
	public ChainPoint getSegment(Chain c)
	{
		if (c == chain1)
			return segment1;
		else if(c == chain2)
			return segment2;
		else
			throw new Error("call of getSegment with wrong Chain object");
	}
	
	protected Intersection getPreviousIntersection(ChainPoint seg)
	{
		if (seg == segment1)
			return next1;
		else if(seg == segment2)
			return next2;
		else
			throw new Error("call of getPreviousIntersection with wrong Chain object");
	}
	
	protected Intersection getNextIntersection(ChainPoint seg)
	{
		if (seg == segment1)
			return prev1;
		else if(seg == segment2)
			return prev2;
		else
			throw new Error("call of getNextIntersection with wrong Chain object");
	}
	
	protected void updateNearIntersections(ChainPoint p)
	{
		if(p == segment1)
		{
			prev1 = getNearIntersection(segment1, false);
			next1 = getNearIntersection(segment1, true);
		}
		else if(p == segment2)
		{
			prev2 = getNearIntersection(segment2, false);
			next2 = getNearIntersection(segment2, true);
		}
	}
	
	protected void updateNearIntersections(Chain c) throws Exception
	{
		if(chain1 == chain2) throw new Exception("No");
		if(c == chain1)
		{
			prev1 = getNearIntersection(segment1, false);
			next1 = getNearIntersection(segment1, true);
		}
		else if(c == chain2)
		{
			prev2 = getNearIntersection(segment2, false);
			next2 = getNearIntersection(segment2, true);
		}
	}
	
	protected void updateNearIntersections()
	{
		prev1 = getNearIntersection(segment1, false);
		next1 = getNearIntersection(segment1, true);
		prev2 = getNearIntersection(segment2, false);
		next2 = getNearIntersection(segment2, true);	
	}
	
	private Intersection getNearIntersection(ChainPoint seg, Boolean next)
	{
		while(true)
		{
			seg = next?seg.next():seg.previous();
			if(seg.getIntersection() != null) return seg.getIntersection();
		}
	}
	
	/**
	 * compare 2 intersections by comparing segment1 position 
	 */
	/*public int compareTo(Intersection inter)
	{
		return segment1.compareTo(inter.segment1);
		//if()
	}*/
	
	@Override
	public String toString()
	{
		return "[Intersection : " + point + "; " + segment1 + "; " + segment2 + "]";
	}
	
	public KnotPoint getLocation()
	{
		return point;
	}
	
	void dispose()
	{
		chain1 = null;
		chain2 = null;
		segment1 = null;
		segment2 = null;
		point = null;
		prev1 = null;
		next1 = null;
		prev2 = null;
		next2 = null;
	}
}
