/**
 * Copyright (C) 2000 Maynard Demmon, maynard@organic.com
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
 
package com.organic.maynard.outliner;

import java.awt.*;
import java.awt.event.*;
import java.awt.Window;
import java.awt.datatransfer.*;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.border.*;

import com.organic.maynard.util.string.*;

public class DocumentSettings extends JDialog implements ActionListener {
	OutlinerDocument doc = null;
	
	// Instance Fields (Document Preferences) These are NOT saved.
	public PreferenceString lineEnd = new PreferenceString(Preferences.LINE_END.cur,Preferences.LINE_END.cur,"");
	public PreferenceString saveEncoding = new PreferenceString(Preferences.SAVE_ENCODING.cur,Preferences.SAVE_ENCODING.cur,"");

	// GUI Elements
	public JButton buttonOK = new JButton("OK");
	public JButton buttonCancel = new JButton("Cancel");
	public JButton buttonRestoreToGlobal = new JButton("Restore to Global");
	public JComboBox lineEndComboBox = new JComboBox(Preferences.PLATFORM_IDENTIFIERS);
	public JComboBox saveEncodingComboBox = new JComboBox();

	// The Constructors
	public DocumentSettings(OutlinerDocument document) {
		//super(document,"Document Settings",true);
		
		this.doc = document;
		
		// Create the Layout
		setSize(250,200);
		setResizable(false);
		
		// Define the Bottom Panel
		JPanel bottomPanel = new JPanel();

		bottomPanel.setLayout(new FlowLayout());
		
		buttonOK.addActionListener(this);
		bottomPanel.add(buttonOK);

		buttonCancel.addActionListener(this);
		bottomPanel.add(buttonCancel);

		getContentPane().add(bottomPanel,BorderLayout.SOUTH);
		
		// Setup ComboBoxes
		for (int i = 0; i < Preferences.ENCODINGS.size(); i++) {
			String encoding = (String) Preferences.ENCODINGS.elementAt(i);
			saveEncodingComboBox.addItem(encoding);
		}

		lineEndComboBox.addItemListener(new ComboBoxListener(lineEndComboBox, lineEnd));
		saveEncodingComboBox.addItemListener(new ComboBoxListener(saveEncodingComboBox, saveEncoding));		

		// Define the Center Panel
		buttonRestoreToGlobal.addActionListener(this);
		
		Box box = Box.createVerticalBox();

		addSingleItemCentered(new JLabel("Line Terminator"), box);
		addSingleItemCentered(lineEndComboBox, box);

		box.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel("Encoding when saving."), box);
		addSingleItemCentered(saveEncodingComboBox, box);

		box.add(Box.createVerticalStrut(10));

		addSingleItemCentered(buttonRestoreToGlobal, box);

		getContentPane().add(box,BorderLayout.CENTER);
	
		// Set the default button
		getRootPane().setDefaultButton(buttonOK);
	
	}

	public boolean useDocumentSettings = false;

	public void show() {
		syncPrefs();
		useDocumentSettings = true;
		super.show();
	}
		
	public void syncPrefs() {		
		lineEnd.restoreTemporaryToCurrent();
		lineEndComboBox.setSelectedItem(lineEnd.tmp);

		saveEncoding.restoreTemporaryToCurrent();
		saveEncodingComboBox.setSelectedItem(saveEncoding.tmp);
	}
	
	// Misc methods
	private void addSingleItemCentered(JComponent component, Container container) {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		component.setMaximumSize(component.getPreferredSize());
		box.add(component);
		box.add(Box.createHorizontalGlue());
		container.add(box);
	}

	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			ok();
		} else if (e.getActionCommand().equals("Cancel")) {
			cancel();
		} else if (e.getActionCommand().equals("Restore to Global")) {
			restoreToGlobal();
		}
	}
	
	private void ok() {
		if (!lineEnd.cur.equals(lineEnd.tmp) || !saveEncoding.cur.equals(saveEncoding.tmp)) {
			doc.setFileModified(true);
		}
		
		lineEnd.cur = lineEnd.tmp;
		saveEncoding.cur = saveEncoding.tmp;
		this.hide();
	}

	private void cancel() {
		this.hide();
	}

	private void restoreToGlobal() {
		lineEnd.tmp = Preferences.LINE_END.cur;
		saveEncoding.tmp = Preferences.SAVE_ENCODING.cur;
		
		lineEndComboBox.setSelectedItem(lineEnd.tmp);
		saveEncodingComboBox.setSelectedItem(saveEncoding.tmp);
	}
}
