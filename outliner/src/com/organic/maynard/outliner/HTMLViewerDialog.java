/**
 * Copyright (C) 2003 Maynard Demmon, maynard@organic.com
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

import com.organic.maynard.outliner.guitree.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import org.xml.sax.*;

import java.net.URL;
import java.io.*;
import com.organic.maynard.swing.HTMLViewer;

import java.beans.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class HTMLViewerDialog extends AbstractGUITreeJDialog implements PropertyChangeListener, ActionListener {
	
	// Constants
	private static final int INITIAL_WIDTH = 400;
	private static final int INITIAL_HEIGHT = 400;
	private static final int MINIMUM_WIDTH = 200;
	private static final int MINIMUM_HEIGHT = 125;
	
	private static String CLOSE = null;
	
	// Instance Fields
	private boolean initialized = false;
	
	// GUI Components
	private JScrollPane jsp = null;
	private HTMLViewer viewer = null;
	private JButton closeButton = null;
	
	
	// The Constructors
	public HTMLViewerDialog() {
		super(false, false, false, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
		
		Outliner.html_viewer = this;
	}
	
	private void initialize() {
		this.viewer = new HTMLViewer();
		this.viewer.addPropertyChangeListener(this);
		this.jsp = new JScrollPane(this.viewer);
		
		getContentPane().add(this.jsp, BorderLayout.CENTER);
		
		CLOSE = GUITreeLoader.reg.getText("close");
		this.closeButton = new JButton(CLOSE);
		this.closeButton.addActionListener(this);
		
		JPanel closePanel = new JPanel();
		closePanel.add(this.closeButton, BorderLayout.CENTER);
		
		getContentPane().add(closePanel, BorderLayout.SOUTH);
		
		getRootPane().setDefaultButton(this.closeButton);
		
		this.initialized = true;
	}
	
	
	public boolean isInitialized() {
		return this.initialized;
	}
	
	public void show() {
		// Lazy Instantiation
		if (!isInitialized()) {
			initialize();
		}
		
		super.show();
	}
	
	public void setHTML(URL url) {
		// DEBUG
		/*try {
			InputStream in = url.openConnection().getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
			boolean eof = false;
			while (!eof) {
				String theLine = buffer.readLine();
				if (theLine == null) {
					eof = true;
				} else {
					System.out.println(theLine);
				}
			}
			in.close();
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		}*/
		
		
		// Lazy Instantiation
		if (!isInitialized()) {
			initialize();
		}
		
		try {
			this.viewer.setPage(url);
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		}
	}
	
	// PropertyChangeListener Interface
	public void propertyChange(PropertyChangeEvent e) {
		String name = e.getPropertyName();
		if (name.equals("page")) {
			setTitle(this.viewer.getTitle());
		}
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(CLOSE)) {
			this.hide();
		}
	}
}
