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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.EventListenerList;

import org.knotsgm.core.ChainPoint;
import org.knotsgm.core.KnotString;
import org.knotsgm.core.Style;

public class KnotStringGraphic extends JComponent implements MouseListener, ActionListener
{
	private static final long serialVersionUID = -697876738918393795L;
	private static final short margin = 20;
	
	public static final String STYLE_CHANGED = "style_changed";
	public static final String STRING_DELETED = "string_deleted";
	
	private EventListenerList actionListenerList = new EventListenerList();
	
	private KnotString string;
	private StringDrawingInfos drawing_infos;
	private BufferedImage buffer;
	
	private boolean dirty = true;
	private Rectangle dims;
	
	private JPopupMenu rcmenu;
	private JMenuItem delete_btn;
	private JRadioButtonMenuItem selectstyleglobal_btn;
	private JRadioButtonMenuItem selectstyle1_btn;
	private JRadioButtonMenuItem selectstyle2_btn;
	private JMenuItem selectcolor_btn;
	private ButtonGroup group = new ButtonGroup();
	
	public KnotStringGraphic(KnotString knotstring)
	{
		drawing_infos = new StringDrawingInfos();
		drawing_infos.style = new Style(Style.DEFAULT_WIDTH, true, true);
		
		KnotsGM.debugMessage("Création d'une ficelle", 1);
		
		setLayout(null);
		setOpaque(false);
		string = knotstring;
		
		setLocation(0, 0);
		
		addMouseListener(this);
		
		rcmenu = new JPopupMenu();
		
		delete_btn = new JMenuItem("Supprimer");
		
		selectstyleglobal_btn = new JRadioButtonMenuItem("Style global", true);
		selectstyle1_btn = new JRadioButtonMenuItem("Style \"Maths\"", false);
		selectstyle2_btn = new JRadioButtonMenuItem("Style \"Marin\"", false);
		selectcolor_btn = new JMenuItem("Modifier la couleur...");
		
		group.add(selectstyleglobal_btn);
		group.add(selectstyle1_btn);
		group.add(selectstyle2_btn);
		
		rcmenu.add(delete_btn);
		rcmenu.add(selectstyleglobal_btn);
		rcmenu.add(selectstyle1_btn);
		rcmenu.add(selectstyle2_btn);
		rcmenu.add(selectcolor_btn);
		
		delete_btn.addActionListener(this);
		selectstyleglobal_btn.addActionListener(this);
		selectstyle1_btn.addActionListener(this);
		selectstyle2_btn.addActionListener(this);
		selectcolor_btn.addActionListener(this);
	}
	
	public KnotString getString()
	{
		return string;
	}
	
	public StringDrawingInfos getDrawingInfos()
	{
		drawing_infos = updateDrawingInfos(string);
		return drawing_infos;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		KnotsGM.debugMessage("UI : Button pressed : " + e.getActionCommand(), 2);
		
		Object source = e.getSource();
		dirty = true;
		
		if(source == delete_btn)
		{
			dispatchActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, STRING_DELETED));
		}
		else if(source == selectstyleglobal_btn)
		{	
			drawing_infos.style.followGlobal = true;
			dispatchActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, STYLE_CHANGED));
		}
		else if(source == selectstyle1_btn)
		{
			drawing_infos.style.followGlobal = false;
			drawing_infos.style.mode = false;
			dispatchActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, STYLE_CHANGED));
		}
		else if(source == selectstyle2_btn)
		{
			drawing_infos.style.followGlobal = false;
			drawing_infos.style.mode = true;
			dispatchActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, STYLE_CHANGED));
		}
		else if(source == selectcolor_btn)
		{
			drawing_infos.style.color = JColorChooser.showDialog(this, "Modifier la couleur du rond de ficelle", drawing_infos.style.color);
			dispatchActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, STYLE_CHANGED));
		}
	}
	
	public void itemStateChanged(ItemEvent e)
	{
		System.out.println(e);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		//Graphics2D g2d = (Graphics2D) g;
		//Dimension dims = string.getDimension();
		//System.out.println("string painted");
		
		if (dirty || buffer == null)
		{
			dims = string.getBounds();
			buffer = new BufferedImage(dims.width+2*margin, dims.height+2*margin, BufferedImage.TYPE_BYTE_BINARY);
			
			Graphics2D g2a = buffer.createGraphics();
			g2a.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			
			g2a.translate(-dims.x+margin, -dims.y+margin);
			paintSelector(g2a);
			
			//g2a.drawRect(dims.x, dims.y, dims.width, dims.height);
			
			
			dirty = false;
		}
		
		//g2d.drawImage(buffer, null, dims.x-margin, dims.y-margin);
	}
	
	private void paintSelector(Graphics2D g2d)
	{
		Color c = new Color(0xFFFFFF);
		
		g2d.setPaint(c);
		g2d.setStroke(new BasicStroke(drawing_infos.style.getOutsideWidth(), 
				BasicStroke.CAP_ROUND, 
				BasicStroke.JOIN_MITER));
		g2d.draw(drawing_infos.under);
		g2d.draw(drawing_infos.over);
	}
	
	
	public StringDrawingInfos updateDrawingInfos(KnotString chain)
	{
		StringDrawingInfos drawingInfos = new StringDrawingInfos();
		drawingInfos.over = new GeneralPath();
		drawingInfos.under = new GeneralPath();
		drawingInfos.style = drawing_infos.style;
		
		GeneralPath overlayer = drawingInfos.over;
		GeneralPath underlayer = drawingInfos.under;
		
		GeneralPath drawOn;
		GeneralPath prev = null;
		
		for(ChainPoint point : chain)
		{
			try
			{
				if(	point.getNearIntersection() == null || point.getNearIntersection().getPolarity(point.getNearIntersectionPoint()))
					drawOn = overlayer;
				else
					drawOn = underlayer;
			}
			catch(Exception e)
			{
				throw new Error("Arrrrgh ! Rhaaargg...." + e.toString());
			}
			
			if(prev != drawOn) drawOn.moveTo(point.getPoint().x, point.getPoint().y);
			drawOn.quadTo(point.getDrawingPoint().x,
					point.getDrawingPoint().y,
					point.getNextPoint().x,
					point.getNextPoint().y);
			
			prev = drawOn;
		}
		
		return drawingInfos;
		//dirty = false;
	}
	
	public void dispose()
	{
		buffer.flush();
		buffer = null;
		string = null;
	}
	
	@Override
	public boolean contains(int x, int y)
	{
		int color;
		if(buffer == null) return false;
		try
		{
			color = buffer.getRGB(x-dims.x+margin, y-dims.y+margin);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			return false;
		}
		if(color == -1) return true;
		return false;
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		//if(e.getButton() == MouseEvent.BUTTON3)
		//{
		//	showRightClickMenu(e);
		//}
	}
	
	private void showRightClickMenu(MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			rcmenu.show(e.getComponent(),e.getX(), e.getY());
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
		Style style = drawing_infos.style;
		style.bold = true;
		dirty = true;
		repaint();
		dispatchActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "style_changed"));		
	}
	
	@Override
	public void mouseExited(MouseEvent e)
	{
		Style style = drawing_infos.style;
		style.bold = false;
		dirty = true;
		repaint();
		dispatchActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "style_changed"));
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		showRightClickMenu(e);
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		showRightClickMenu(e);
	}
	
	
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
