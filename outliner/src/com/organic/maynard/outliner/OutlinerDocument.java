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
import javax.swing.border.*;
import java.beans.*;

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
	public TreeContext tree = new TreeContext(this);
	public UndoQueue undoQueue = new UndoQueue(this);
	public DocumentSettings settings = new DocumentSettings(this);
	public HoistStack hoistStack = new HoistStack(this);
	public AttributesPanel attPanel = new AttributesPanel(this);
	
	private JSplitPane splitPane = null;
	private JScrollPane attJSP = new JScrollPane(attPanel);

	
	// The Constructor
	public OutlinerDocument(String title) {
		super("",true,true,true,true);
		
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
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, panel, attJSP);
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
	
	
	// Attributes Panel
	private boolean isShowingAttributes = false;
	private int dividerPosition = 0;
	
	public boolean isShowingAttributes() {return this.isShowingAttributes;}
	
	public void showAttributes(boolean b) {
		isShowingAttributes = b;

		if (isShowingAttributes()) {
			// Swap the components
			getContentPane().remove(panel);
			splitPane.setTopComponent(panel);
			attPanel.update();
			getContentPane().add(splitPane, BorderLayout.CENTER);
			
			// Restore the divider position.
			splitPane.setDividerLocation(dividerPosition);
		} else {
			// Store the current divider position.
			dividerPosition = splitPane.getDividerLocation();
			
			// Swap the components
			getContentPane().remove(splitPane);
			getContentPane().add(panel, BorderLayout.CENTER);
		}

		validate();
				
		panel.layout.draw();
		panel.layout.setFocus(tree.getEditingNode(),tree.getComponentFocus());
	}
	
	public void destroy() {
		removeInternalFrameListener(monitor);
		removeComponentListener(this);
		getContentPane().remove(panel);
		
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
	private boolean fileModified = false;
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
		Outliner.updateSaveMenuItem();
	}
	
	public String getFileName() {return fileName;}

	public void setFileModified(boolean fileModified) {
		this.fileModified = fileModified;
		Outliner.updateSaveMenuItem();
		Outliner.updateSaveAllMenuItem();
		
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