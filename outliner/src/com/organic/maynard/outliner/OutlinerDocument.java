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
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
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
import javax.swing.border.*;
import java.beans.*;
import gui.DummyJScrollPane;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class OutlinerDocument extends JInternalFrame implements ComponentListener, PropertyChangeListener {

	// Constants
	//public static final String UNTITLED_DOCUMENT_NAME = "Untitled";
	private static final ImageIcon ICON_DOCUMENT_SAVED = new ImageIcon(Outliner.GRAPHICS_DIR + "document_saved.gif");
	private static final ImageIcon ICON_DOCUMENT_UNSAVED = new ImageIcon(Outliner.GRAPHICS_DIR + "document_unsaved.gif");
	
	public static final int MIN_WIDTH = 300;
	public static final int MIN_HEIGHT = 100;
 
 	public static final int INITIAL_WIDTH = 450;
	public static final int INITIAL_HEIGHT = 450;

 	public static final int INITIAL_X = 5;
	public static final int INITIAL_Y = 5;
		
	// Class Variables
	private static int untitledDocumentCount = 0;
	private static OutlinerWindowMonitor monitor = new OutlinerWindowMonitor();


	// Instance Variables
	public OutlinerPanel panel = new OutlinerPanel(this);
	
	public DummyJScrollPane dummy = null;
	
	public TreeContext tree = new TreeContext(this);
	public UndoQueue undoQueue = new UndoQueue(this);
	public DocumentSettings settings = new DocumentSettings(this);
	public HoistStack hoistStack = new HoistStack(this);
	public AttributesPanel attPanel = new AttributesPanel(this);
	
	private DocumentInfo docInfo = null; // Used for saving/reverting
	
	private JSplitPane splitPane = null;
	private JScrollPane attJSP = new JScrollPane(attPanel);

	
	// The Constructor
	public OutlinerDocument(String title) {
		this(title, new DocumentInfo());
	}
	
	public OutlinerDocument(String title, DocumentInfo docInfo) {
		super("",true,true,true,true);
		
		setDocumentInfo(docInfo);
		
		Outliner.desktop.add(this, JLayeredPane.DEFAULT_LAYER);

		// Set the window title
		if (title.equals("")) {
			untitledDocumentCount++;
			setTitle(GUITreeLoader.reg.getText("untitled") + " " + untitledDocumentCount);
		} else {
			setTitle(title);
		}
		
		// Add it to the openDocuments list
		Outliner.addDocument(this);
		
		// Set the Component & Window Listeners
		addComponentListener(this);
		addInternalFrameListener(monitor);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			
		// Create the Layout
		restoreWindowToInitialSize();
		setLocation(INITIAL_X, INITIAL_Y);
		
		dummy = new DummyJScrollPane(panel, panel.layout.scrollBar);
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, dummy, attJSP);
		splitPane.setResizeWeight(1.0);
		splitPane.addPropertyChangeListener(this);

		// Set the icon in the frame header.
		setFrameIcon(ICON_DOCUMENT_UNSAVED);
		
		// Draw and Set Focus to the First Visible Node
		Outliner.menuBar.windowMenu.changeToWindow(this);
		
		dividerPosition = getSize().height - 120;
		splitPane.setDividerLocation(dividerPosition); // This sets the position in the event that we don't show the atts initially.

		if (Preferences.getPreferenceBoolean(Preferences.SHOW_ATTRIBUTES).cur) {
			showAttributes(true);
		} else {
			showAttributes(false);
		}

		setVisible(true);
	}
	
	// Accessors
	public DocumentInfo getDocumentInfo() {
		return this.docInfo;
	}
	
	public void setDocumentInfo(DocumentInfo docInfo) {
		this.docInfo = docInfo;
	}
	
	
	// Attributes Panel
	private boolean isShowingAttributes = false;
	private int dividerPosition = 0;
	
	public boolean isShowingAttributes() {return this.isShowingAttributes;}
	
	public void showAttributes(boolean b) {
		isShowingAttributes = b;

		if (isShowingAttributes()) {
			// Swap the components
			getContentPane().remove(dummy);
			splitPane.setTopComponent(dummy);
			attPanel.update();
			getContentPane().add(splitPane, BorderLayout.CENTER);
			
			// Restore the divider position.
			splitPane.setDividerLocation(dividerPosition);
		} else {
			// Store the current divider position.
			dividerPosition = splitPane.getDividerLocation();
			
			// Swap the components
			getContentPane().remove(splitPane);
			getContentPane().add(dummy, BorderLayout.CENTER);
		}

		if (isVisible()) {
			validate();
			panel.layout.redraw();
		}
	}
	
	public void destroy() {
		removeInternalFrameListener(monitor);
		removeComponentListener(this);
		getContentPane().remove(panel);
		
		docInfo = null;
		
		panel.destroy();
		panel = null;
		
		tree.destroy();
		tree = null;
		
		undoQueue.destroy();
		undoQueue = null;
		
		settings.destroy();
		settings = null;
		
		hoistStack.destroy();
		hoistStack = null;
		
		border = null;
		fileName = null;

		removeNotify();
		removeAll();
	}
	
	public void restoreWindowToInitialSize() {
		setSize(INITIAL_WIDTH,INITIAL_HEIGHT);
	}

	// Border
	private Border border = null;
	
	public void hideBorder() {
		border = getBorder();
		setBorder(null);
	}
	
	public void showBorder() {
		if (border != null) {
			setBorder(border);
		}
	}
	
	// This method taken from the workaround for bug #4309079.
	public void moveToFront() {
		//System.out.println("move to front " + getTitle());
		
		Window window = SwingUtilities.getWindowAncestor(this);
		Component focusOwner = (window != null) ? window.getFocusOwner() : null;
		boolean descendant = false;

		if (window != null && focusOwner != null && SwingUtilities.isDescendingFrom(focusOwner, this)) {
			descendant = true;
			requestFocus();
		}

		super.moveToFront();

		if (descendant) {
			focusOwner.requestFocus();
		}
	}


	// ComponentListener Interface
	public void componentResized(ComponentEvent e) {
		panel.layout.draw();
		panel.layout.setFocus(tree.getEditingNode(),tree.getComponentFocus());
	}
	
	public void componentHidden(ComponentEvent e) {} 
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}


	// File Saving and Modification
	private String fileName = "";
	private boolean fileModified = true;
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
		FileMenu.updateSaveMenuItem();
	}
	
	public String getFileName() {return fileName;}

	public void setFileModified(boolean fileModified) {
		// Abort if we're not changing state.
		if (fileModified == this.fileModified) {
			return;
		}
		
		this.fileModified = fileModified;
		FileMenu.updateSaveMenuItem();
		FileMenu.updateSaveAllMenuItem();
		
		if (fileModified) {
			setFrameIcon(ICON_DOCUMENT_UNSAVED);
		} else {
			setFrameIcon(ICON_DOCUMENT_SAVED);
		}
	}
	
	public boolean isFileModified() {return fileModified;}
	
	
	// Text Caret Positioning
	private int preferredCaretPosition = 0;

	public int getPreferredCaretPosition() {return preferredCaretPosition;}
	public void setPreferredCaretPosition(int position) {this.preferredCaretPosition = position;}
	
	public static int findNearestCaretPosition(int currentPosition, int preferredCaretPosition, Node node) {
		int retVal = currentPosition;
		
		if (preferredCaretPosition > retVal) {
			retVal = preferredCaretPosition;
		}
		
		if (retVal > node.getValue().length()) {
			int newPreferredCaretPosition = currentPosition;
			if (preferredCaretPosition < newPreferredCaretPosition) {
				preferredCaretPosition = newPreferredCaretPosition;
			}
			retVal = node.getValue().length();
		}
		return retVal;
	}
	
	
	// PropertyChangeListener Interface
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
			panel.layout.draw();
			panel.layout.setFocus(panel.doc.tree.getEditingNode(), panel.doc.tree.getComponentFocus());
		}
	}
}