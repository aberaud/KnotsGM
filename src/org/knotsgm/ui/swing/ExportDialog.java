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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.knotsgm.io.BMPExporter;
import org.knotsgm.io.JPEGExporter;
import org.knotsgm.io.KnotExporter;
import org.knotsgm.io.PNGExporter;

public class ExportDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = -5639513397599636890L;
	
	KnotGraphic export_component;
	
	JFileChooser chooser;
	ButtonGroup format_group;
	JRadioButton jpg_radio;
	JRadioButton png_radio;
	JRadioButton bmp_radio;
	
	JSlider quality_slider;
	JButton save_btn;
	
	public ExportDialog(Frame owner, KnotGraphic component)
	{
		super(owner, "Exporter...", true);
		
		export_component = component;
		
		chooser = new JFileChooser();
		
		format_group = new ButtonGroup();
		
		jpg_radio = new JRadioButton(JPEGExporter.JPEG_EXTENSION);
		png_radio = new JRadioButton(PNGExporter.PNG_EXTENSION, true);
		bmp_radio = new JRadioButton(BMPExporter.BMP_EXTENSION);
		
		jpg_radio.addActionListener(this);
		png_radio.addActionListener(this);
		bmp_radio.addActionListener(this);
		
		format_group.add(jpg_radio);
		format_group.add(bmp_radio);
		format_group.add(png_radio);
		
		quality_slider = new JSlider(0, 100, 100);
		quality_slider.setMinorTickSpacing(10);
		quality_slider.setMajorTickSpacing(50);
		quality_slider.setPaintTicks(true);
		quality_slider.setPaintLabels(true);
		quality_slider.setEnabled(false);
		
		save_btn = new JButton("Save");
		save_btn.addActionListener(this);
		
		setLayout(new BorderLayout());
		
		add(new JLabel("Select settings for the exported image :"), BorderLayout.PAGE_START);
		
		JPanel quality_chooser = new JPanel();
		quality_chooser.setLayout(new BoxLayout(quality_chooser, BoxLayout.LINE_AXIS));
		quality_chooser.add(new JLabel("Compression "));
		quality_chooser.add(quality_slider);
		
		JPanel format_chooser = new JPanel();
		format_chooser.setLayout(new BoxLayout(format_chooser, BoxLayout.LINE_AXIS));
		format_chooser.add(jpg_radio);
		format_chooser.add(png_radio);
		format_chooser.add(bmp_radio);
		
		JPanel main_options = new JPanel();
		main_options.setLayout(new BoxLayout(main_options, BoxLayout.PAGE_AXIS));
		main_options.add(format_chooser);
		main_options.add(quality_chooser);
		
		add(main_options, BorderLayout.CENTER);
		add(save_btn, BorderLayout.PAGE_END);
		
		pack();
	}
	
	private String getFormatString()
	{
		if(jpg_radio.isSelected()) return JPEGExporter.JPEG_EXTENSION;
		else if(png_radio.isSelected()) return PNGExporter.PNG_EXTENSION;
		else if(bmp_radio.isSelected()) return BMPExporter.BMP_EXTENSION;
		
		return "png";
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if(source == save_btn) saveImage();
		else if(source == jpg_radio) quality_slider.setEnabled(true);
		else quality_slider.setEnabled(false);
	}
	
	private void saveImage()
	{
		FileNameExtensionFilter filter = new FileNameExtensionFilter(getFormatString().toUpperCase(new Locale("en"))+" Images", getFormatString());
		chooser.removeChoosableFileFilter(chooser.getFileFilter());
		chooser.setFileFilter(filter);
		int returnVal = chooser.showSaveDialog(this);
		
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return;
		
		try
		{
			File file = chooser.getSelectedFile();
			
			file = forceFileExtension(file, getFormatString());
			
			KnotExporter exporter = null;
			if(getFormatString() == JPEGExporter.JPEG_EXTENSION)
				exporter = new JPEGExporter(quality_slider.getValue()/100.f);
			else if(getFormatString() == PNGExporter.PNG_EXTENSION)
				exporter = new PNGExporter();
			else if(getFormatString() == BMPExporter.BMP_EXTENSION)
				exporter = new BMPExporter();
			
			exporter.exportKnot(export_component, file);
			dispose();
		}
		catch (Exception exception) {
			KnotsGM.debugMessage("Can't write to file", 1);
		}
	}
	
	private File forceFileExtension(File file, String extention)
	{
		if(file.exists()) return file;
		String name = file.getName();
		String[] names = name.split(".");
		if(names.length == 0)
			return new File(file.getParentFile(), name+"."+extention);
		return file;
	}
}
