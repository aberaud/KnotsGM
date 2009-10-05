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

import java.awt.Point;

/**
 * Representation of a complete knot.
 * The class provides high-level functions to manipulate a knot.
 * @author Adrien Béraud <adrienberaud@gmail.com>
 * @see KnotString
 */
public class Knot extends KnotBase
{
	private static final long serialVersionUID = -8090776165148485644L;
	
	//protected Vector<KnotBit> bits; 
	
	public Knot(Chain startWith)
	{
		/*setBackground(Color.white);
		setOpaque(true);*/
		integerChain(startWith);
	}
	
	public Knot()
	{
		// TODO Auto-generated constructor stub
	}
	
	@Override 
	public Point addString(KnotStringBase string)
	{
		if(!(string instanceof KnotString)) return null;
		return super.addString(string);
	}
	
	/**
	 * Transforms a chain in a KnotString and add it to the Knot.
	 * @param c The chain to add.
	 */
	public void integerChain(Chain c)
	{
		KnotString string;
		
		if(c instanceof KnotString) string = (KnotString) c;
		else string = new KnotString(c);
		
		addString(string);
	}
	
	/**
	 * Transforms the Knot to an equivalent (simplier) one.
	 */
	public Point simplify()
	{
		//while(simplifySomething());
		return simplifySomething();
	}
	
	private Point simplifySomething()
	{
		/*
		for(Intersection inter : intersections)
		{
			System.out.println("Infos sur l'intersection " + inter.getID());
			System.out.println("\tprev1 : " + inter.prev1.getID());
			System.out.println("\tprev2 : " + inter.prev2.getID());
			System.out.println("\tnext1 : " + inter.next1.getID());
			System.out.println("\tnext2 : " + inter.next2.getID());
		}
		 */
		/*
		 * Detect and simplify basic stuff
		 *   ___  __       ______
		 *  /   \/    =>  /           (1)
		 *  \___/\__      \______
		 * 
		 *         and
		 *   ___|__       |   ____
		 *  /   |     =>  |  /        (2)
		 *  \___|__       |  \____ 
		 *      |         |
		 */     
		/*for(Intersection inter : intersections)
		{
			/* (1) */
		/*if(inter.getChain1() == inter.getChain2() && (inter.next1 == inter || inter.next2 == inter))
		{
			System.out.println("Boucle détectée autour de l'intersection " + inter.getID());
			
			KnotString string = (KnotString)inter.getChain1();
			
			if(inter.next1 == inter)
			{
				inter.prev1.setNextIntersection(inter.next2, inter.prev1.getSegment(string));
				inter.next2.setPreviousIntersection(inter.prev1, inter.next2.getSegment(string));
			}
			else
			{
				inter.prev2.setNextIntersection(inter.next1, inter.prev2.getSegment(string));
				inter.next1.setPreviousIntersection(inter.prev2, inter.next1.getSegment(string));
			}
			
			string.removePart(inter.getSegment1(), inter.getSegment2());
			intersections.remove(inter);
			
			for (KnotStringBase cstring : strings)
				cstring.updateIntersections();
			
			return new Point();
		}
		 */
		/* (2) */
		/*if(inter.prev1 == inter.next2 && inter.prev1 != inter && inter.getPolarity(inter.prev1) == inter.prev1.getPolarity(inter))
				System.out.println("Hyper-boucle type 1 détectée autour des intersections " + inter.getID() + " et " + inter.prev1.getID());
			
			if(inter.prev2 == inter.prev1 && inter.prev2 != inter && inter.getPolarity(inter.prev2) != inter.prev2.getPolarity(inter))
				System.out.println("Hyper-boucle type 2 détectée autour des intersections " + inter.getID() + " et " + inter.prev2.getID());
			
			if(inter.next1 == inter.prev2 && inter.next1 != inter && inter.getPolarity(inter.next1) == inter.next1.getPolarity(inter))
				System.out.println("Hyper-boucle type 3 détectée autour des intersections " + inter.getID() + " et " + inter.next1.getID());
			
			if(inter.next2 == inter.next1 && inter.next2 != inter && inter.getPolarity(inter.next2) != inter.next2.getPolarity(inter))
				System.out.println("Hyper-boucle type 4 détectée autour des intersections " + inter.getID() + " et " + inter.next2.getID());*/
		//}
		
		return normalise();
	}
	
	
	
	
	
	
	
	
}
