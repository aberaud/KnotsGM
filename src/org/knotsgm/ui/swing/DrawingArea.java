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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.util.Iterator;

import javax.swing.JComponent;

import org.knotsgm.core.Chain;
import org.knotsgm.core.ChainPoint;
import org.knotsgm.core.KnotPoint;

public class DrawingArea extends JComponent implements MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = -7824882236750735970L;
	private static final double distance = 20;
	
	public Chain drawingChain = null;
	private boolean creatingNewKnot = false;
	
	private KnotManager manager;
	
	public DrawingArea(KnotManager manager)
	{
		setOpaque(false);
		addMouseListener(this);
		addMouseMotionListener(this);
		this.manager = manager;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		if(drawingChain == null) return;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		
		paintChain(drawingChain, g2d);
	}
	
	public void paintChain(Chain chain, Graphics2D g)
	{
		g.setPaint(Color.black);
		g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		int length = chain.size();
		
		GeneralPath path = new GeneralPath();
		path.moveTo(chain.getFirst().x(), chain.getFirst().y());
		
		ChainPoint current_point = chain.get(1);
		if(length == 2)
		{
			path.lineTo(current_point.getPoint().x, current_point.getPoint().y);
			g.draw(path);
			return;
		}
		
		Iterator<ChainPoint> iterator = chain.iterator();
		KnotPoint current_center = KnotPoint.center(chain.getFirst().getPoint(), chain.getFirst().next().getPoint());
		KnotPoint last_point = iterator.next().getPoint();
		
		path.lineTo(current_center.x, current_center.y);
		
		for(current_point = iterator.next(); current_point != chain.getFirst(); current_point = iterator.next())
		{
			current_center = KnotPoint.center(last_point, current_point.getPoint());
			path.quadTo(last_point.x, last_point.y, current_center.x, current_center.y);
			last_point = current_point.getPoint();
		}
		
		path.lineTo(chain.getLast().getPoint().x, chain.getLast().getPoint().y);
		g.draw(path);
	}
	
	public void mousePressed(MouseEvent e)
	{
		//System.out.print("pressed\n");
		if(creatingNewKnot) return;
		creatingNewKnot = true;
		
		drawingChain = new Chain();
		//drawingChain.setClosed(false);
		
		java.awt.Point point = e.getPoint();
		
		drawingChain.addLast(new ChainPoint(point.x, point.y, drawingChain));
		drawingChain.addLast(new ChainPoint(point.x, point.y, drawingChain));
	}
	
	public void mouseDragged(MouseEvent e)
	{
		if(!creatingNewKnot) return;
		
		KnotPoint newp = new KnotPoint(e.getPoint());
		KnotPoint prev = drawingChain.getLast().previous().getPoint();
		
		drawingChain.getLast().getPoint().setLocation(newp);
		
		if(KnotPoint.distance(newp, prev) > distance)
		{
			drawingChain.pollLast();
			drawingChain.addLast(new ChainPoint((KnotPoint) newp.clone(), drawingChain));
			drawingChain.addLast(new ChainPoint((KnotPoint) newp.clone(), drawingChain));
		}
		
		drawLine();
	}
	
	private void drawLine()
	{
		if(!creatingNewKnot) return;
		
		repaint();
	}
	
	public void mouseReleased(MouseEvent e)
	{
		if(!creatingNewKnot) return;
		
		creatingNewKnot = false;
		
		if(drawingChain.size() < 5)
		{
			drawingChain.clear();
			drawingChain = null;
			repaint();
			return;	
		}
		
		
		drawingChain.removeLast();
		
		manager.drawingFinished(drawingChain);
		
		drawingChain = null;
		repaint();
	}
	
	public void mouseMoved(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
}
