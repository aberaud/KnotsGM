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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class KnotsGM
{
	public static final int VERBOSE_MODE = 1;
	
	public static final Integer MAJOR_VERSION = 0;
	public static final Integer MINOR_VERSION = 6;
	public static final Integer REVISION = 0;
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{	// Set System L&F
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} 
				catch (UnsupportedLookAndFeelException e)
				{
					throw new Error("UnsupportedLookAndFeelException Error");
				}
				catch (ClassNotFoundException e)
				{
					throw new Error("ClassNotFoundException Error");
				}
				catch (InstantiationException e)
				{
					throw new Error("InstantiationException Error");
				}
				catch (IllegalAccessException e)
				{
					throw new Error("IllegalAccessException Error");
				}
				
				createAndShowGUI();
			}
		});
	}
	
	private static void createAndShowGUI()
	{
		JFrame window = new JFrame("KnotsGM");
		window.setIconImage(new ImageIcon("ressources/ticone.png").getImage());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		KnotManager manager = new KnotManager();
		GUI activeZone = new GUI(manager, window);
		window.setContentPane(activeZone);
		
		window.pack();
		window.setVisible(true);
	}
	
	public static void debugMessage(String message, Integer verbosity)
	{
		if(verbosity > VERBOSE_MODE) return;
		System.out.println(message);
	}
	
	public static String getVersionString()
	{
		return MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION;
	}
	
}
