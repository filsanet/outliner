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
import java.util.*;
import javax.swing.*;
import org.xml.sax.*;

public class DocumentAttributesView extends AbstractGUITreeJDialog implements ActionListener {
	
	// Constants
	private static final int INITIAL_WIDTH = 350;
	private static final int INITIAL_HEIGHT = 400;
	private static final int MINIMUM_WIDTH = 250;
	private static final int MINIMUM_HEIGHT = 300;

	protected static final String OK = "OK";
	protected static final String CANCEL = "Cancel";


	// GUI Elements
	protected Box box = Box.createVerticalBox();
	
	protected DocumentAttributesPanel attPanel = null;

	protected JButton buttonOK = new JButton(OK);
	protected JButton buttonCancel = new JButton(CANCEL);


	// The Constructors
	public DocumentAttributesView() {
		super(false, false, true, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
	}

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		Outliner.documentAttributes = this;
		
		// Define the Bottom Panel
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		bottomPanel.add(buttonOK);
		bottomPanel.add(buttonCancel);
		
		getContentPane().add(bottomPanel,BorderLayout.SOUTH);

		// Add Listeners
		buttonOK.addActionListener(this);
		buttonCancel.addActionListener(this);
			
		// Define the Center Panel
		attPanel = new DocumentAttributesPanel();
		JScrollPane jsp = new JScrollPane(attPanel);
		
		getContentPane().add(jsp,BorderLayout.CENTER);
	
		// Set the default button
		getRootPane().setDefaultButton(buttonOK);
	}
	

	// Configuration 
	protected TreeContext tree = null;
	
	public void configureAndShow(TreeContext tree) {
		this.tree = tree;
		
		attPanel.update(this);

		super.show();
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(OK)) {
			ok();
		} else if (e.getActionCommand().equals(CANCEL)) {
			cancel();
		}
	}
	
	private void ok() {
		applyChanges();
		hide();
	}
	
	private void cancel() {
		hide();
	}

	private void applyChanges() {
		tree.clearAttributes();
		
		AttributeTableModel model = attPanel.model;
		
		for (int i = 0; i < model.keys.size(); i++) {
			String key = (String) model.keys.get(i);
			Object value = model.values.get(i);
			
			tree.setAttribute(key, value);
		}
		
		tree.doc.setFileModified(true);
	}
}
