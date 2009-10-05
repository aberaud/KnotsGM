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

import java.util.ListIterator;

public class KnotString extends KnotStringBase
{
	private static final long serialVersionUID = 2316664632785821046L;
	
	public KnotString()
	{
		super();
		setClosed(true);
	}
	
	public KnotString(Chain c)
	{
		super();
		setClosed(c.isClosed());
		for (ChainPoint point : c) addLast(new ChainPoint(point.getPoint(), this));
		
		setClosed(true);
		try
		{
			init();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean removePart(ChainPoint start, ChainPoint end)
	{
		if(start.getChain() != this || end.getChain() != this) return false;
		ChainPoint current;
		for(current = start; current != end; current = current.next())
			if(current.getIntersection() != null) removeIntersection(current.getIntersection());
		
		ListIterator<ChainPoint> iterator = listIterator();
		while(current != start) current = iterator.next();
		
		while(current != end)
		{
			iterator.remove();
			current = iterator.next();
		}
		
		return true;
	}
	
}
