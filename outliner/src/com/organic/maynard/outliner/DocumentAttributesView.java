/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 *  - Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer. 
 * 
 *  - Redistributions in binary form must reproduce the above 
 *    copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided 
 *    with the distribution. 
 * 
 *  - Neither the names "Java Outline Editor", "JOE" nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
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

	protected static String OK = null;
	protected static String CANCEL = null;


	// GUI Elements
	protected Box box = Box.createVerticalBox();
	
	protected DocumentAttributesPanel attPanel = null;

	protected JButton buttonOK = null;
	protected JButton buttonCancel = null;


	// The Constructors
	public DocumentAttributesView() {
		super(false, false, true, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
	}

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);

		OK = GUITreeLoader.reg.getText("ok");
		CANCEL = GUITreeLoader.reg.getText("cancel");
		
		buttonOK= new JButton(OK);
		buttonCancel = new JButton(CANCEL);

		
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
	protected JoeTree tree = null;
	
	public void configureAndShow(JoeTree tree) {
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
		
		tree.getDocument().setFileModified(true);
	}
}
