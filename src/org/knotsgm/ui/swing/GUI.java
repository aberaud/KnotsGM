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

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.knotsgm.core.Knot;
import org.knotsgm.core.Style;
import org.knotsgm.io.KVGExporter;
import org.knotsgm.io.KVGImporter;
import org.knotsgm.io.KnotImporter;
import org.knotsgm.ui.KnotDrawingOptions;
import org.knotsgm.ui.swing.event.KnotManagerEvent;
import org.knotsgm.ui.swing.event.KnotManagerEventListener;

public class GUI extends JPanel implements ActionListener, KnotManagerEventListener
{
	private static final long serialVersionUID = 2118063032226504698L;
	
	private static final String WEBSITE_URL = "http://knotsgm.beraud.org";
	
	private JFrame window;
	private KnotManager knotmanager;
	
	private JPanel appcontainer;
	private JToolBar toolbar;
	
	private JButton newstring_btn;
	private JButton newdoc_btn;
	private JButton compute_btn;
	private JToggleButton debug_btn;
	
	private JMenuBar menubar;
	
	private JMenu fileMenu = new JMenu("Fichier");
	private JMenuItem open_button = new JMenuItem("Ouvrir...");
	private JMenuItem save_button = new JMenuItem("Sauvegarder...");
	private JMenuItem export_button = new JMenuItem("Exporter...");
	private JMenuItem exit_button = new JMenuItem("Quitter");
	
	private JMenu styleMenu = new JMenu("Style");
	private ButtonGroup styleGroup = new ButtonGroup();
	private JRadioButtonMenuItem selectstyle1_btn = new JRadioButtonMenuItem("Style Lacan", true);
	private JRadioButtonMenuItem selectstyle2_btn = new JRadioButtonMenuItem("Style Rolfsen");
	private JRadioButtonMenuItem selectstyle3_btn = new JRadioButtonMenuItem("Style Listing");
	
	private JMenu aboutMenu = new JMenu("Aide");
	private JMenuItem website_btn = new JMenuItem("Site web");
	private JMenuItem about_btn = new JMenuItem("À propos");
	
	private ExportDialog export_dialog;
	
	Desktop desktop = null;
	
	public GUI(KnotManager manager, JFrame window)
	{
		super(new BorderLayout());
		
		appcontainer = new JPanel(new BorderLayout());
		
		this.window = window;
		knotmanager = manager;
		manager.addKnotManagerEventListener(this);
		
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		
		newstring_btn = new JButton("Ajouter ficelle");
		newdoc_btn = new JButton("Nouveau document");
		compute_btn = new JButton("Calculer");
		debug_btn = new JToggleButton("Infos de débugage");
		//debug_btn.s
		
		toolbar.add(newstring_btn);
		toolbar.add(newdoc_btn);
		//toolbar.add(compute_btn);
		toolbar.add(debug_btn);
		
		generateMenuBar();
		
		newstring_btn.addActionListener(this);
		newdoc_btn.addActionListener(this);
		compute_btn.addActionListener(this);
		debug_btn.addActionListener(this);
		
		add(menubar, BorderLayout.PAGE_START);
		appcontainer.add(toolbar, BorderLayout.PAGE_START);
		appcontainer.add(knotmanager, BorderLayout.CENTER);
		
		add(appcontainer, BorderLayout.CENTER);
		
		knotmanager.drawKnotString();
		
		if (Desktop.isDesktopSupported())
			desktop = Desktop.getDesktop();
		
	}
	
	private void generateMenuBar()
	{
		menubar = new JMenuBar();
		
		fileMenu.add(open_button);
		fileMenu.add(save_button);
		fileMenu.add(export_button);
		fileMenu.add(exit_button);
		
		styleGroup.add(selectstyle1_btn);
		styleGroup.add(selectstyle2_btn);
		styleMenu.add(selectstyle1_btn);
		styleMenu.add(selectstyle2_btn);
		styleMenu.add(selectstyle3_btn);
		
		aboutMenu.add(website_btn);
		aboutMenu.add(about_btn);
		
		menubar.add(fileMenu);
		menubar.add(styleMenu);
		menubar.add(aboutMenu);
		
		open_button.addActionListener(this);
		save_button.addActionListener(this);
		exit_button.addActionListener(this);
		export_button.addActionListener(this);
		selectstyle1_btn.addActionListener(this);
		selectstyle2_btn.addActionListener(this);
		about_btn.addActionListener(this);
		website_btn.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		KnotsGM.debugMessage("UI : Button pressed : " + e.getActionCommand(), 2);
		Object source = e.getSource();
		
		if(source == newstring_btn)
		{
			knotmanager.drawKnotString();
		}
		else if(source == newdoc_btn)
		{
			knotmanager.clear();
		}
		else if(source == compute_btn)
		{
			KnotGraphic knot_graphic = knotmanager.getKnots().get(0);
			Knot knot = knot_graphic.getKnot();
			
			knot.simplify();
			knot_graphic.ksync();
		}
		else if(source == debug_btn)
		{
			KnotDrawingOptions drawing_options = new KnotDrawingOptions(); 
			drawing_options.show_debug_infos = ((JToggleButton)source).isSelected();
			knotmanager.setGlobalDrawingOptions(drawing_options);
		}
		else if(source == open_button)
		{
			File file = null;
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Fichiers KVG", "kvg");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION)
				file = chooser.getSelectedFile();
			else return;
			
			KnotImporter importer = new KVGImporter();
			try
			{
				Knot knot = importer.importKnot(file);
				newstring_btn.setEnabled(true);
				newdoc_btn.setEnabled(true);
				knotmanager.stopDrawing();
				knotmanager.addKnot(knot);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
		else if(source == save_button)
		{
			File file = null;
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Fichiers KVG", "kvg");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION)
				file = chooser.getSelectedFile();
			else return;
			
			KnotGraphic knot_graphic = knotmanager.getKnots().get(0);
			Knot knot = knot_graphic.getKnot();
			KVGExporter exporter = new KVGExporter();
			exporter.exportKnot(knot, file);
		}
		else if(source == exit_button)
		{
			System.exit(0);
		}
		else if(source == export_button)
		{
			Vector<KnotGraphic> knots =  knotmanager.getKnots();
			if(knots.size() == 0)
			{
				JOptionPane.showMessageDialog(window, "Aucun noeud dessiné !");
				return;
			}
			export_dialog = new ExportDialog(window, knots.get(0));
			export_dialog.setVisible(true);
		}
		else if(source == selectstyle1_btn)
		{
			Style.setGlobalMode(false);
			knotmanager.forceRepaint();
		}
		else if(source == selectstyle2_btn)
		{
			Style.setGlobalMode(true);
			knotmanager.forceRepaint();
		}
		else if(source == website_btn)
		{
			openWebsite();
		}
		else if(source == about_btn)
		{
			JOptionPane.showMessageDialog(window, "KnotsGM\nversion "+ KnotsGM.getVersionString()+"\n(c) copyright 2009 Adrien Béraud <adrienberaud@gmail.com>\n\n" +
					"KnotsGM is free software: you can redistribute it and/or modify \n" +
					"it under the terms of the GNU General Public License as published by \n" +
					"the Free Software Foundation, either version 3 of the License, or \n" +
			"(at your option) any later version.");
		}
	}
	
	public void itemStateChanged(ItemEvent e)
	{
		System.out.println(e);
	}
	
	
	public void drawingString(KnotManagerEvent evt)
	{
		newstring_btn.setEnabled(false);
		newdoc_btn.setEnabled(false);
	}
	
	public void stringDrawn(KnotManagerEvent evt)
	{
		newstring_btn.setEnabled(true);
		newdoc_btn.setEnabled(true);
	}
	
	private void openWebsite()
	{
		try
		{
			desktop.browse(new URI(WEBSITE_URL));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
	}
	
}
