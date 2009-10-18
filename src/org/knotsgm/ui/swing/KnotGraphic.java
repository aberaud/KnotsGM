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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

import org.knotsgm.core.Intersection;
import org.knotsgm.core.Knot;
import org.knotsgm.core.KnotString;
import org.knotsgm.core.KnotStringBase;
import org.knotsgm.ui.KnotDrawingOptions;
import org.knotsgm.ui.swing.event.RepaintEvent;
import org.knotsgm.ui.swing.event.RepaintEventDispatcher;
import org.knotsgm.ui.swing.event.RepaintListener;

public class KnotGraphic extends JComponent implements RepaintListener, RepaintEventDispatcher, ActionListener
{
	private static final long serialVersionUID = 3848667061384485390L;
	
	private Knot knot;
	private ArrayList<KnotStringGraphic> strings_graphics = new ArrayList<KnotStringGraphic>();
	private ArrayList<IntersectionChanger> intersections_changers = new ArrayList<IntersectionChanger>();
	
	private BufferedImage buffer;
	private boolean dirty = true;
	
	private KnotDrawingOptions drawing_options = new KnotDrawingOptions();
	
	private EventListenerList listenerList = new EventListenerList();
	
	public KnotGraphic(KnotString chain)
	{
		knot = new Knot(chain);
		init();
	}
	
	public KnotGraphic(Knot newknot)
	{
		knot = newknot;
		init();
	}
	
	private void init()
	{
		//setBackground(Color.red);
		//setOpaque(true);
		Dimension size = knot.getDimension();
		setSize(size);
		setPreferredSize(size);
		
		for(KnotStringBase string : knot.getStrings()) 
		{
			KnotStringGraphic new_string_graphic = new KnotStringGraphic((KnotString) string);
			strings_graphics.add(new_string_graphic);
		}
	}
	
	@Override
	public boolean isOptimizedDrawingEnabled()
	{
		return false;
	}
	
	public Knot getKnot()
	{
		return knot;
	}
	
	public ArrayList<KnotStringGraphic> getStrings()
	{
		return strings_graphics;
	}
	
	public void addString(KnotString string)
	{
		Rectangle new_dims = new Rectangle();
		new_dims.setLocation(getLocation());
		
		Point diff = knot.addString(string);
		new_dims.x+=diff.x;
		new_dims.y+=diff.y;
		new_dims.setSize(knot.getDimension());
		
		KnotStringGraphic new_string_graphic = new KnotStringGraphic(string);
		strings_graphics.add(new_string_graphic);
		
		for(KnotStringGraphic sg : strings_graphics) sg.setBounds(new_dims);
		
		setBounds(new_dims);
		setPreferredSize(new_dims.getSize());
		
		dirty = true;
		repaint();
		dispatchNeedRepaint(new RepaintEvent(this));
	}
	
	public void enableUI()
	{
		Vector<Intersection> inters = knot.getIntersections();
		
		IntersectionChanger newchanger;
		
		int i = 0;
		
		for (Intersection cinter : inters)
		{
			newchanger = new IntersectionChanger(cinter);
			newchanger.addActionListener(this);
			add(newchanger);
			setComponentZOrder(newchanger, i++);
			intersections_changers.add(newchanger);
		}
		
		for (KnotStringGraphic string_graphic : strings_graphics)
		{
			add(string_graphic);
			string_graphic.addActionListener(this);
			setComponentZOrder(string_graphic, i++);
			string_graphic.setSize(getSize());
			string_graphic.setLocation(new Point(0,0));
		}
	}
	
	public void disableUI()
	{
		for(IntersectionChanger cic : intersections_changers)
		{
			cic.dispose();
			remove(cic);
		}
		for(KnotStringGraphic ssel : strings_graphics)
		{
			ssel.removeActionListener(this);
			remove(ssel);
		}
		
		intersections_changers.clear();
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		if (dirty || buffer == null)
		{
			Rectangle dimensions = knot.getBounds();
			buffer = new BufferedImage(dimensions.width, dimensions.height, BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2a = buffer.createGraphics();
			g2a.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			//g2a.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2a.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			
			paintKnot(g2a);
			
			if(drawing_options.show_debug_infos)
			{
				g2a.setPaint(Color.black);
				Vector<Intersection> inters = knot.getIntersections();
				for (Intersection cinter : inters)
				{
					g2a.drawString("" + cinter.getID(), (int)cinter.getLocation().x + 10, (int)cinter.getLocation().y + 10);
				}
			}
			
			dirty = false;
		}
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.drawImage(buffer, null, 0, 0);
	}
	
	protected void paintKnot(Graphics g)
	{
		KnotsGM.debugMessage("Rendering knot", 2);
		
		Graphics2D g2d = (Graphics2D) g;
		
		StringDrawingInfos[] drawinginfos = new StringDrawingInfos[strings_graphics.size()];
		
		int i = 0;
		for (KnotStringGraphic string: strings_graphics)
			drawinginfos[i++] = string.getDrawingInfos();		
		
		//White under the knot
		for (i = 0; i < drawinginfos.length; i++)
		{
			StringDrawingInfos stringDrawingInfos = drawinginfos[i];
			if(!stringDrawingInfos.style.mode) continue;
			g2d.setPaint(stringDrawingInfos.style.getOutsideColor());
			//g2d.setPaint(Color.red);
			g2d.setStroke(new BasicStroke(stringDrawingInfos.style.getOutsideWidth(), 
					BasicStroke.CAP_ROUND, 
					BasicStroke.JOIN_MITER));
			g2d.draw(stringDrawingInfos.under);
		}
		
		for (i = 0; i < drawinginfos.length; i++)
		{
			StringDrawingInfos stringDrawingInfos = drawinginfos[i];
			g2d.setPaint(stringDrawingInfos.style.getInsideColor());
			//g2d.setPaint(Color.black);
			g2d.setStroke(new BasicStroke(stringDrawingInfos.style.getInsideWidth(), 
					BasicStroke.CAP_ROUND, 
					BasicStroke.JOIN_MITER));
			g2d.draw(stringDrawingInfos.under);
		}
		
		for (i = 0; i < drawinginfos.length; i++)
		{
			StringDrawingInfos stringDrawingInfos = drawinginfos[i];
			g2d.setPaint(stringDrawingInfos.style.getOutsideColor());
			//g2d.setPaint(Color.green);
			g2d.setStroke(new BasicStroke(stringDrawingInfos.style.getOutsideWidth(), 
					BasicStroke.CAP_BUTT, 
					BasicStroke.JOIN_MITER));
			g2d.draw(stringDrawingInfos.over);
		}
		
		for (i = 0; i < drawinginfos.length; i++)
		{
			StringDrawingInfos stringDrawingInfos = drawinginfos[i];
			g2d.setPaint(stringDrawingInfos.style.getInsideColor());
			g2d.setStroke(new BasicStroke(stringDrawingInfos.style.getInsideWidth(), 
					BasicStroke.CAP_ROUND, 
					BasicStroke.JOIN_MITER));
			g2d.draw(stringDrawingInfos.over);
		}
		
		/*if(drawing_options.show_debug_infos)
		{
			g2d.setPaint(Color.black);
			g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
			for(ChainPoint p : knot.getStrings().get(0))
			{
				KnotPoint point = p.getDrawingPoint();
				g2d.drawLine((int)point.x-3, (int)point.y-3, (int)point.x+3, (int)point.y+3);
				g2d.drawLine((int)point.x+3, (int)point.y-3, (int)point.x-3, (int)point.y+3);
			}
		}*/
	}
	
	@Override
	public void needRepaint(RepaintEvent evt)
	{
		dirty = true;
		repaint();
	}
	
	public void ksync()
	{
		disableUI();
		enableUI();
		dirty = true;
		repaint();
	}
	
	public void setDrawingOptions(KnotDrawingOptions drawing_options)
	{
		this.drawing_options = drawing_options;
		dirty = true;
		repaint();
		dispatchNeedRepaint(new RepaintEvent(this));
	}
	
	public KnotDrawingOptions getDrawingOptions()
	{
		return drawing_options;
	}
	
	public void addRepaintEventListener(RepaintListener listener)
	{
		listenerList.add(RepaintListener.class, listener);
	}
	
	public void removeRepaintEventListener(RepaintListener listener)
	{
		listenerList.remove(RepaintListener.class, listener);
	}
	
	void dispatchNeedRepaint(RepaintEvent evt)
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
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() instanceof IntersectionChanger)
		{
			
		}
		else if(e.getSource() instanceof KnotStringGraphic)
		{
			KnotStringGraphic string_graphic = (KnotStringGraphic) e.getSource();
			if(e.getActionCommand() == KnotStringGraphic.STYLE_CHANGED)
			{
				
			}
			else if(e.getActionCommand() == KnotStringGraphic.STRING_DELETED)
			{
				Point diff = knot.removeString(string_graphic.getString());
				disableUI();
				string_graphic.dispose();
				strings_graphics.remove(string_graphic);
				enableUI();
				Point loc = getLocation();
				loc.x += diff.x;
				loc.y += diff.y;
				setLocation(loc);
			}
		}
		dirty = true;
		repaint();
		dispatchNeedRepaint(new RepaintEvent(this));
	}
}

