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
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

import org.knotsgm.core.Intersection;
import org.knotsgm.core.KnotPoint;
import org.knotsgm.ui.swing.event.RepaintEventDispatcher;
import org.knotsgm.ui.swing.event.RepaintListener;

public class IntersectionChanger extends JComponent implements MouseListener, RepaintEventDispatcher
{
	private static final long serialVersionUID = -6003330134201021774L;
	private static final short size = 30;
	
	private EventListenerList actionListenerList = new EventListenerList();
	private EventListenerList repaintListenerList = new EventListenerList();
	
	private Intersection intersection; 
	protected BufferedImage buffer;
	
	protected boolean dirty = true;
	protected boolean show = false;
	
	public IntersectionChanger(Intersection inters)
	{
		setOpaque(false);
		intersection = inters;
		
		KnotPoint loc = (KnotPoint)intersection.getLocation().clone();
		setLocation((int)loc.x-size/2, (int)loc.y-size/2);
		setSize(new Dimension(size, size));
		
		addMouseListener(this);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		if (buffer == null || dirty)
		{
			buffer = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2a = buffer.createGraphics();
			g2a.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			paintIC(g2a);
			
			dirty = false;
		}
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.drawImage(buffer, null, 0, 0);
	}
	
	private void paintIC(Graphics2D g2a)
	{
		g2a.setPaint(new Color(show?0x88888888:0x000000FF, true));
		g2a.fill(new Ellipse2D.Double(0, 0, size, size));
	}
	
	public void dispose()
	{
		buffer.flush();
		buffer = null;
		intersection = null;
	}
	
	public void mouseClicked(MouseEvent arg0)
	{
		KnotsGM.debugMessage("Modification de l'intersection " + intersection.getID(), 1);
		intersection.reverse();
		dispatchActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "intersection_changed"));
	}
	
	public void mouseEntered(MouseEvent arg0)
	{
		show = true;
		dirty = true;
		
		repaint();
	}
	
	public void mouseExited(MouseEvent arg0)
	{
		show = false;
		dirty = true;
		repaint();
	}
	
	public void mousePressed(MouseEvent arg0){}
	public void mouseReleased(MouseEvent arg0){}
	
	@Override
	public void addRepaintEventListener(RepaintListener listener)
	{
		repaintListenerList.add(RepaintListener.class, listener);
	}
	
	@Override
	public void removeRepaintEventListener(RepaintListener listener)
	{
		repaintListenerList.remove(RepaintListener.class, listener);
	}
	
	/*
	private void dispatchRepaintEvent(RepaintEvent evt)
	{
		Object[] listeners = repaintListenerList.getListenerList();
		for (int i=0; i<listeners.length; i+=2)
		{
			if (listeners[i]==RepaintListener.class)
			{
				((RepaintListener)listeners[i+1]).needRepaint(evt);
            }
        }
    }
	 */
	
	public void addActionListener(ActionListener listener)
	{
		actionListenerList.add(ActionListener.class, listener);
	}
	
	public void removeActionListener(ActionListener listener)
	{
		actionListenerList.remove(ActionListener.class, listener);
	}
	
	private void dispatchActionEvent(ActionEvent evt)
	{
		Object[] listeners = actionListenerList.getListenerList();
		for (int i=0; i<listeners.length; i+=2)
		{
			if (listeners[i]==ActionListener.class)
			{
				((ActionListener)listeners[i+1]).actionPerformed(evt);
			}
		}
	}
	
}
