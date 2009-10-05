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

package org.knotsgm.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import org.knotsgm.core.Chain;
import org.knotsgm.core.Knot;
import org.knotsgm.core.KnotPoint;
import org.knotsgm.core.KnotString;
import org.knotsgm.ui.KnotDrawingOptions;
import org.knotsgm.ui.swing.event.KnotManagerEvent;
import org.knotsgm.ui.swing.event.KnotManagerEventListener;
import org.knotsgm.ui.swing.event.RepaintEvent;
import org.knotsgm.ui.swing.event.RepaintListener;

public class KnotManager extends JPanel implements RepaintListener
{
	private static final long serialVersionUID = -1843041837881565792L;
	
	protected EventListenerList listenerList = new EventListenerList();
	
	private DrawingArea drawing_area;
	
	private final Dimension min_size = new Dimension(200, 200);
	private final Dimension pref_size = new Dimension(600, 600);
	
	private Vector<KnotGraphic> knots = new Vector<KnotGraphic>();
	private KnotGraphic current_knot;
	
	//Vector<IntersectionChanger> interchangers;
	//Vector<KnotStringGraphic> stringselectors;
	
	public KnotManager()
	{
		setLayout(null);
		setOpaque(true);
		setBackground(Color.white);
		
		drawing_area = new DrawingArea(this);
		
		//interchangers = new Vector<IntersectionChanger>();
		//stringselectors = new Vector<KnotStringGraphic>();
	}
	
	public Vector<KnotGraphic> getKnots()
	{
		return knots;
	}
	
	//public void addKnot(Knot newknot)
	public void addKnotFrom(Chain chain)
	{
		KnotsGM.debugMessage("Nouveau noeud", 1);
		
		KnotString string = new KnotString(chain);
		
		Point position = string.getBounds().getLocation();
		string.subtract(new org.knotsgm.core.KnotPoint(position));
		
		KnotGraphic knot_graphic = new KnotGraphic(string);
		//TODO cleanup margins stuff
		position.x -= 20;
		position.y -= 20;
		knot_graphic.setLocation(position);
		
		knots.add(knot_graphic);
		add(knot_graphic);
		knot_graphic.addRepaintEventListener(this);
		setComponentZOrder(knot_graphic, 0);
		current_knot = knot_graphic;
		
		//validate();
		repaint();
	}
	
	public void addKnot(Knot newknot)
	{
		KnotsGM.debugMessage("Nouveau noeud", 1);
		KnotGraphic knot_graphic = new KnotGraphic(newknot);
		knot_graphic.setLocation(new Point(0, 0));
		
		knots.add(knot_graphic);
		add(knot_graphic);
		knot_graphic.addRepaintEventListener(this);
		setComponentZOrder(knot_graphic, 0);
		current_knot = knot_graphic;
		repaint();
	}
	
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		drawing_area.setBounds(0, 0, getWidth(), getHeight());
		//background.setBounds(0, 0, getWidth(), getHeight());
		//background.repaint();
	}
	
	@Override
	public Dimension getMinimumSize()
	{
		return min_size;
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return pref_size;
	}
	
	public void stopDrawing()
	{
		remove(drawing_area);
	}
	
	public void drawingFinished(Chain chain)
	{
		stopDrawing();
		if(current_knot == null)
		{
			addKnotFrom(chain);
		}
		else
		{
			KnotString news = new KnotString(chain);
			news.subtract(new KnotPoint(current_knot.getLocation()));
			current_knot.addString(news);
		}
		dispatchStringDrawn(new KnotManagerEvent(this));
		startChangeIntersections();
	}
	
	@Override
	public boolean isOptimizedDrawingEnabled()
	{
		return false;
	}
	
	public void drawKnotString()
	{
		stopChangeIntersections();
		dispatchDrawingString(new KnotManagerEvent(this));
		add(drawing_area);
		setComponentZOrder(drawing_area, 0);
	}
	
	
	public void clear()
	{
		for (KnotGraphic cknot : knots)
		{
			cknot.getKnot().dispose();
			remove(cknot);
		}
		knots = new Vector<KnotGraphic>();
		current_knot = null;
		
		validate();
		repaint();
		
		drawKnotString();
	}
	
	public void startChangeIntersections()
	{
		for (KnotGraphic cknotg : knots) cknotg.enableUI();
		
		validate();
		repaint();
	}
	
	public void stopChangeIntersections()
	{
		for (KnotGraphic cknotg : knots) cknotg.disableUI();
		repaint();
	}
	
	public void setGlobalDrawingOptions(KnotDrawingOptions options)
	{
		for(KnotGraphic knot : knots)
		{
			knot.setDrawingOptions(options);
		}
	}
	
	public void addKnotManagerEventListener(KnotManagerEventListener listener)
	{
		listenerList.add(KnotManagerEventListener.class, listener);
	}
	
	public void removeKnotManagerEventListener(KnotManagerEventListener listener)
	{
		listenerList.remove(KnotManagerEventListener.class, listener);
	}
	
	private void dispatchDrawingString(KnotManagerEvent evt)
	{
		Object[] listeners = listenerList.getListenerList();
		for (int i=0; i<listeners.length; i+=2)
		{
			if (listeners[i]==KnotManagerEventListener.class)
			{
				((KnotManagerEventListener)listeners[i+1]).drawingString(evt);
			}
		}
	}
	
	private void dispatchStringDrawn(KnotManagerEvent evt)
	{
		Object[] listeners = listenerList.getListenerList();
		for (int i=0; i<listeners.length; i+=2)
		{
			if (listeners[i]==KnotManagerEventListener.class)
			{
				((KnotManagerEventListener)listeners[i+1]).stringDrawn(evt);
			}
		}
	}
	
	public void forceRepaint()
	{
		for(KnotGraphic knot : knots) knot.needRepaint(null);
		repaint();
	}
	
	@Override
	public void needRepaint(RepaintEvent evt)
	{
		repaint();
	}
	
	
	
}

