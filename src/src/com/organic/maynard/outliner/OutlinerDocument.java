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
import javax.swing.border.*;
import javax.swing.event.*;

public class OutlinerDocument extends JInternalFrame implements ComponentListener, ClipboardOwner {

	// Constants
	public static final ImageIcon ICON_DOCUMENT_SAVED = new ImageIcon(Outliner.GRAPHICS_DIR + System.getProperty("file.separator") + "document_saved.gif");
	public static final ImageIcon ICON_DOCUMENT_UNSAVED = new ImageIcon(Outliner.GRAPHICS_DIR + System.getProperty("file.separator") + "document_unsaved.gif");
	
	public static final int MIN_WIDTH = 300;
	public static final int MIN_HEIGHT = 100;
 
 	static final int INITIAL_WIDTH = 450;
	static final int INITIAL_HEIGHT = 450;
		
	// Class Variables
	static int untitledDocumentCount = 0;

	// Instance Variables
	public outlinerPanel panel = new outlinerPanel(this);
	public TreeContext tree = new TreeContext(this);
	public UndoQueue undoQueue = new UndoQueue(this);
	public DocumentSettings settings = new DocumentSettings(this);

	
	// The Constructor
	public OutlinerDocument(String title) {
		super("",true,true,true,true);
		
		Outliner.desktop.add(this, JLayeredPane.DEFAULT_LAYER);

		// Set the window title
		if (title.equals("")) {
			untitledDocumentCount++;
			setTitle("Untitled " + untitledDocumentCount);
		} else {
			setTitle(title);
		}
		
		// Add it to the openDocuments list
		Outliner.addDocument(this);
		
		// Add it to the window list
		WindowMenu.addWindow(this);
		
		// Set the Component & Window Listeners
		addComponentListener(this);
		addInternalFrameListener(new OutlinerWindowMonitor());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			
		// Create the Layout
		restoreWindowToInitialSize();
		setLocation(5,5);
		getContentPane().add(panel, BorderLayout.CENTER);
		
		// Set the icon in the frame header.
		setFrameIcon(ICON_DOCUMENT_UNSAVED);
		
		// Draw and Set Focus to the First Visible Node
		Outliner.menuBar.windowMenu.changeToWindow(this);
				
		panel.layout.draw((Node) tree.visibleNodes.get(0), outlineLayoutManager.TEXT);

		setVisible(true);
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


	// ClipboardOwner Interface
	public Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	public void lostOwnership(Clipboard clipboard, Transferable contents) {}


	// File Saving
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

}