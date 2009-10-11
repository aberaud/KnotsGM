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

import java.awt.Color;
import java.io.Serializable;

import javax.swing.event.EventListenerList;

import org.knotsgm.ui.swing.event.RepaintEvent;
import org.knotsgm.ui.swing.event.RepaintListener;

/**
 * @author Adrien Béraud
 */
public class Style implements Serializable, RepaintListener
{
	private static final long serialVersionUID = -387807632295117697L;
	
	protected EventListenerList listenerList = new EventListenerList();
	protected static EventListenerList stylelistenerList = new EventListenerList();
	
	protected static boolean globalMode = false;
	
	private static final Color BACKGROUND_COLOR = Color.white;
	public static final short DEFAULT_WIDTH = 4;//4;
	public static final Color DEFAULT_COLOR = new Color(0x000000);
	public static final boolean DEFAULT_MODE = false;
	
	public short width = DEFAULT_WIDTH;
	public Color color = DEFAULT_COLOR;
	public boolean mode = DEFAULT_MODE;
	public boolean bold = false;
	
	public boolean followGlobal = true;
	
	public Style()
	{
		stylelistenerList.add(Style.class, this);
	}
	
	public Style(short width, Color color)
	{
		stylelistenerList.add(Style.class, this);
		this.width = width;
		this.color = color;
	}
	
	public Style(short width, int color)
	{
		stylelistenerList.add(Style.class, this);
		this.width = width;
		this.color = new Color(color);
	}
	
	public Style(short width, boolean random, boolean mode)
	{
		stylelistenerList.add(Style.class, this);
		this.width = width;
		this.mode = mode;
		color = random?Style.randomColor(110,200):DEFAULT_COLOR;
	}
	
	public Color getInsideColor()
	{
		return (followGlobal?globalMode:mode)?BACKGROUND_COLOR:color;
	}
	
	public Color getOutsideColor()
	{
		return (followGlobal?globalMode:mode)?color:BACKGROUND_COLOR;
	}
	
	public int getInsideWidth()
	{
		if(bold && !(followGlobal?globalMode:mode)) return width+2;
		return (followGlobal?globalMode:mode)?2*width:width;
	}
	
	public int getOutsideWidth()
	{
		if(bold && (followGlobal?globalMode:mode)) return 3*width+4;
		return (followGlobal?globalMode:mode)?3*width:3*width;
	}
	
	public static void setGlobalMode(boolean b)
	{
		if(globalMode == b) return;
		globalMode = b;
		dispatchStyleNeedRepaint(new RepaintEvent());
	}
	
	/**
	 * Generates a random Color with brightness between min and max
	 * @param min Minimum brightness of the generated color
	 * @param max Maximum brightness of the generated color
	 * @return a random Color with brightness between min and max
	 */
	public static Color randomColor(int min, int max)
	{
		//System.out.println(brightness(255, 0, 0));
		
		if(max > 255) throw new Error("Range error for param max in randomColor");
		if(max<=min) throw new Error("Bad values for min&max in randomColor");
		
		short red, green, blue;
		double brightness;
		
		do
		{
			red = (short)(Math.random()*255);
			green = (short)(Math.random()*255);
			blue = (short)(Math.random()*255);
			brightness = brightness(red, green, blue);
		}
		while((int)brightness > max || (int)brightness < min);
		
		//System.out.println(new Color(red, green, blue).toString());
		//System.out.println((int)brightness + " : " + min + "-" + max);
		
		return new Color(red, green, blue);
	}
	
	//Using HSP color model as described in http://alienryderflex.com/hsp.html
	private static double brightness(double r, double g, double b)
	{
		//return Math.sqrt(r*r + g*g + b*b);
		return Math.sqrt(.241*r*r + .691*g*g + .068*b*b);
	}
	
	//Public events
	public void addRepaintEventListener(RepaintListener listener)
	{
		listenerList.add(RepaintListener.class, listener);
	}
	
	public void removeRepaintEventListener(RepaintListener listener)
	{
		listenerList.remove(RepaintListener.class, listener);
	}
	
	private void dispatchNeedRepaint(RepaintEvent evt)
	{
		Object[] listeners = listenerList.getListenerList();
		for (int i=0; i<listeners.length; i+=2)
		{
			if (listeners[i]==RepaintListener.class)
			{
				((RepaintListener)listeners[i+1]).needRepaint(evt);
			}
		}
	}
	
	//Internal events
	private static void dispatchStyleNeedRepaint(RepaintEvent evt)
	{
		Object[] listeners = stylelistenerList.getListenerList();
		for (int i=0; i<listeners.length; i+=2)
		{
			if (listeners[i]==Style.class)
			{
				((RepaintListener)listeners[i+1]).needRepaint(evt);
			}
		}
	}
	
	public void needRepaint(RepaintEvent evt)
	{
		dispatchNeedRepaint(new RepaintEvent(this));
	}
}
