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
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public class MacroPopupMenu extends JPopupMenu implements ActionListener, MouseListener {

	public static final int UPPER_BUFFER_SIZE = 30;
	public static final int LOWER_BUFFER_SIZE = 50;

	public Vector macros = new Vector();
	
	// The Constructors
	public MacroPopupMenu() {
		super();
	}

	// Overrides show in JPopup.
	public void show(Component invoker, int x, int y) {
		if (macros.size() > 0) {
			Point p = getPopupMenuOrigin(invoker, x, y);
    		super.show(invoker,p.x,p.y);
		}
	}

	// This code is a start, until we can find a better way to popup long menus.
	protected Point getPopupMenuOrigin(Component invoker, int x, int y){
		//Figure out the sizes needed to calculate the menu position
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension pmSize = this.getSize();
		// For the first time the menu is popped up
		// the size has not yet been initialised
		if(pmSize.width==0){
			pmSize = this.getPreferredSize();
		}
		
		Point absp = new Point(x,y);
		SwingUtilities.convertPointToScreen(absp, invoker);
		
		int aleft = absp.x + pmSize.width;
		int abottom = absp.y + pmSize.height;
		
		if(aleft > screenSize.width) {
			x -= aleft - screenSize.width;
		}
		
		if(abottom > screenSize.height) {
			y -= abottom - screenSize.height;
		}
		
		return new Point(x,y);
	}
	
	public void insert(Component item, int i) {
		item.addMouseListener(this);
		super.insert(item,i);
	}

	// MouseListener Interface
	public void mouseEntered(MouseEvent e) {
		JMenuItem item = (JMenuItem) e.getSource();
		int itemHeight = item.getHeight();
		Point p = new Point(0,itemHeight/2);
		
		SwingUtilities.convertPointToScreen(p, item);
		
		Point location = this.getLocationOnScreen();
		
		int lowerBound = getLowerScreenBoundary();
		int upperBound = getUpperScreenBoundary();
		
		if (p.y < lowerBound) {
			location.y += itemHeight;
			this.setLocation(location);
			try {
				Thread.sleep(100);
			} catch (InterruptedException ie) {
				System.out.println("Interrupted Exception: " + ie);
 			}
		} else if (p.y > upperBound) {
			location.y -= itemHeight;
			this.setLocation(location);
			try {
				Thread.sleep(100);
			} catch (InterruptedException ie) {
				System.out.println("Interrupted Exception: " + ie);
 			}
		}
 	}
 	public void mouseExited(MouseEvent e) {}
 	public void mousePressed(MouseEvent e) {}
 	public void mouseReleased(MouseEvent e) {}
 	public void mouseClicked(MouseEvent e) {}
	
	protected int getLowerScreenBoundary() {
		return UPPER_BUFFER_SIZE;
	}
	
	protected int getUpperScreenBoundary() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return screenSize.height - LOWER_BUFFER_SIZE;
	}

		
	public void init() {
		// Code to load the menu up with macros from disk.
		File macroDir = new File(Outliner.MACROS_DIR);
		String[] filelist = macroDir.list();
		
		System.out.println("Loading Macros...");
		for (int i = 0; i < filelist.length; i++) {
			loadMacro(Outliner.MACROS_DIR + filelist[i]);
		}
		System.out.println("Done Loading Macros.");
	}
	
	private void loadMacro(String filename) {
		System.out.println("\tMacro: " + filename);
		Macro macro = Outliner.macroManager.loadMacro(filename);
		
		// Add it to the Popup Menu
		int i = addMacro(macro);
		
		// Add it to the list in the MacroManager
		((DefaultListModel) Outliner.macroManager.macroList.getModel()).insertElementAt(macro.getName(),i);
	}

	public boolean isNameUnique(String name) {
		for (int i = 0; i < macros.size(); i++) {
			Macro macro = getMacro(i);
			if (name.equals(macro.getName())) {
				return false;
			}
		}
		return true;	
	}
	
	public int addMacro(Macro macro) {
		// Find the correct spot to add it alphabetically
		int i;
		for (i = 0; i < macros.size(); i++) {
			Macro macroTemp = (Macro) macros.elementAt(i);
			if (macroTemp.getName().compareTo(macro.getName()) >= 0) {
				break;
			}
		}
		
		macros.insertElementAt(macro,i);
		JMenuItem item = new JMenuItem(macro.getName());
		item.addActionListener(this);
		this.insert(item,i);
		return i;
	}
	
	public int removeMacro(Macro macro) {
		int index = macros.indexOf(macro);
		macros.removeElementAt(index);
		this.remove(index);
		return index;
	}
	
	public Macro getMacro(int i) {
		return (Macro) macros.elementAt(i);	
	}
	
	public Macro getMacro(String name) {
		for (int i = 0; i < macros.size(); i++) {
			Macro macro = (Macro) macros.elementAt(i);
			if (macro.getName().equals(name)) {
				return macro;
			}
		}
		return null;
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		startWaitCursor();

		// Get the Macro
		String macroKey = e.getActionCommand();
		Macro macro = getMacro(macroKey);

		// Shorthand
		OutlinerDocument document = Outliner.getMostRecentDocumentTouched();
		TreeContext tree = document.tree;
		
		// Handle Undoability Confirmation
		if (!macro.isUndoable()) {
			int result = JOptionPane.showConfirmDialog(document, "This operation is not undoable.\nProceed anyway?","",JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				// Proceed, do nothing
			} else if (result == JOptionPane.CANCEL_OPTION) {
				return;
			}			
		}
		
		if (macro.getUndoableType() == Macro.SIMPLE_UNDOABLE) {
			//doComplexUndoableMacro(document,tree,macro);
			doSimpleUndoableMacro(document,tree,macro);
		} else if (macro.getUndoableType() == Macro.COMPLEX_UNDOABLE) {
			doComplexUndoableMacro(document,tree,macro);
		} else {
			// Need code for when it is not undoable.
		}
		
		endWaitCursor();
		
		// Redraw and Set Focus
		tree.doc.panel.layout.draw();
		tree.doc.panel.layout.setFocus(tree.getEditingNode(),tree.getComponentFocus());
		
	}
	
	private void doSimpleUndoableMacro(OutlinerDocument document, TreeContext tree, Macro macro) {
		CompoundUndoableEdit undoable = new CompoundUndoableEdit(tree);
		
		if (tree.getComponentFocus() == outlineLayoutManager.TEXT) {
			// Create a nodeRangePair
			Node node = tree.getEditingNode();
			int cursor = tree.getCursorPosition();
			int mark = tree.getCursorMarkPosition();
			int startIndex = Math.min(cursor,mark);
			int endIndex = Math.max(cursor,mark);
			
			NodeRangePair nodeRangePair = new NodeRangePair(node,startIndex,endIndex);
			
			// Process the macro and create undoable
			String oldText = nodeRangePair.node.getValue();
			macro.process(nodeRangePair);
			String newText = nodeRangePair.node.getValue();
			
			if (macro.isUndoable()) {
				undoable.addPrimitive(new PrimitiveUndoableEdit(nodeRangePair.node,oldText,newText));
			}
			
			tree.setCursorPosition(nodeRangePair.endIndex);
			tree.setCursorMarkPosition(nodeRangePair.startIndex);
		} else {
			for (int i = 0; i < tree.selectedNodes.size(); i++) {
				// Create a nodeRangePair
				NodeRangePair nodeRangePair = new NodeRangePair((Node) tree.selectedNodes.get(i),-1,-1);
				
				// Process the macro and create undoable
				String oldText = nodeRangePair.node.getValue();
				macro.process(nodeRangePair);
				String newText = nodeRangePair.node.getValue();
				
				if (macro.isUndoable()) {
					undoable.addPrimitive(new PrimitiveUndoableEdit(nodeRangePair.node,oldText,newText));
				}				
			}
		}

		if (macro.isUndoable()) {
			document.undoQueue.add(undoable);
		} else {
			document.undoQueue.clear();
		}	
	}

	private void doComplexUndoableMacro(OutlinerDocument document, TreeContext tree, Macro macro) {
		Node parent = tree.getEditingNode().getParent();
		CompoundUndoableReplace undoable = new CompoundUndoableReplace(parent);
		
		int primitiveCount = 0;
		
		if (tree.getComponentFocus() == outlineLayoutManager.TEXT) {
			// Create a nodeRangePair
			Node node = tree.getEditingNode();
			int cursor = tree.getCursorPosition();
			int mark = tree.getCursorMarkPosition();
			int startIndex = Math.min(cursor,mark);
			int endIndex = Math.max(cursor,mark);
			
			Node clonedNode = node.cloneClean();
			NodeRangePair nodeRangePair = new NodeRangePair(clonedNode,startIndex,endIndex);
			
			// Process the macro and create undoable
			Object obj = macro.process(nodeRangePair);
			if (obj != null) {
				undoable.addPrimitive(new PrimitiveUndoableReplace(parent,node,nodeRangePair.node));
				primitiveCount++;
			}
		} else {
			for (int i = 0; i < tree.selectedNodes.size(); i++) {
				// Create a nodeRangePair
				Node node = (Node) tree.selectedNodes.get(i);
				Node clonedNode = node.cloneClean();
				NodeRangePair nodeRangePair = new NodeRangePair(clonedNode,-1,-1);

				// Process the macro
				Object obj = macro.process(nodeRangePair);
				if (obj != null) {
					undoable.addPrimitive(new PrimitiveUndoableReplace(parent,node,nodeRangePair.node));
					primitiveCount++;
				}
			}
		}

		if (primitiveCount > 0) {
			document.undoQueue.add(undoable);
			undoable.redo();
		}
	}

	// Class Methods
	public static Cursor normalCursor = null;
	public static Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	
	public static void startWaitCursor() {
		Component comp = Outliner.outliner.getGlassPane();
		
		// Store the normal cursor
		normalCursor = comp.getCursor();
		
		// Set the cursor to the wait cursor
		comp.setVisible(true);
		comp.setCursor(waitCursor);
	}
	
	public static void endWaitCursor() {
		Component comp = Outliner.outliner.getGlassPane();
		
		if (normalCursor != null) {
			comp.setCursor(normalCursor);
		}
		comp.setVisible(false);
	}
}