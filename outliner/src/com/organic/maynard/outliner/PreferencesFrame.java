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
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import org.xml.sax.*;
import java.util.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class PreferencesFrame extends AbstractGUITreeJDialog implements TreeSelectionListener, ActionListener, JoeXMLConstants {

	// Constants
	private static final int MINIMUM_WIDTH = 450;
	private static final int MINIMUM_HEIGHT = 430;
 	private static final int INITIAL_WIDTH = 450;
	private static final int INITIAL_HEIGHT = 430;


	// Fields
	private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("");
	private DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);


	// Main Component Containers
	public static final JPanel RIGHT_PANEL = new JPanel();
	public static final CardLayout CARD_LAYOUT = new CardLayout();
	public static final JPanel BOTTOM_PANEL = new JPanel();
		
	// Button Text and Other Copy
	public static String OK = null;
	public static String CANCEL = null;
	public static String APPLY = null;
	public static String RESTORE_DEFAULTS = null;

	// Define Fields and Buttons
	public static JButton BOTTOM_OK = null;
	public static JButton BOTTOM_CANCEL = null;
	public static JButton BOTTOM_APPLY = null;


	// The Constructor
	public PreferencesFrame() {
		super(false, false, false, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);

		// Button Text and Other Copy
		OK = GUITreeLoader.reg.getText("ok");
		CANCEL = GUITreeLoader.reg.getText("cancel");
		APPLY = GUITreeLoader.reg.getText("apply");
		RESTORE_DEFAULTS = GUITreeLoader.reg.getText("restore_defaults");

		// Define Fields and Buttons
		BOTTOM_OK = new JButton(OK);
		BOTTOM_CANCEL = new JButton(CANCEL);
		BOTTOM_APPLY = new JButton(APPLY);
	}


	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		// Define the Bottom Panel
		BOTTOM_PANEL.setLayout(new FlowLayout());
		
		BOTTOM_OK.addActionListener(this);
		BOTTOM_PANEL.add(BOTTOM_OK);

		BOTTOM_CANCEL.addActionListener(this);
		BOTTOM_PANEL.add(BOTTOM_CANCEL);

		BOTTOM_APPLY.addActionListener(this);
		BOTTOM_PANEL.add(BOTTOM_APPLY);
		
		// Set the default button
		getRootPane().setDefaultButton(BOTTOM_OK);

		// Define the Right Panel
		RIGHT_PANEL.setLayout(CARD_LAYOUT);
	}
	
	public void endSetup(AttributeList atts) {
		// Define the JTree		
		JTree tree = new JTree(treeModel);
		tree.addTreeSelectionListener(this);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Put it all together
		JScrollPane jsp = new JScrollPane(tree);
		jsp.setMinimumSize(new Dimension(100,0));
		JScrollPane jsp2 = new JScrollPane(RIGHT_PANEL);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, jsp, jsp2);
		splitPane.setResizeWeight(0.0);
		
		getContentPane().add(BOTTOM_PANEL, BorderLayout.SOUTH);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		super.endSetup(atts);		
	}

	
	//public void addPanelToTree(String name) {
	//	rootNode.add(new DefaultMutableTreeNode(name));
	//}

	private DefaultMutableTreeNode lastNode = rootNode;
	private int lastNodeDepth = -1;
	
	public void addPanelToTree(String name, int depth) {
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
		
		if (depth > lastNodeDepth) {
			lastNode.add(newNode);
		} else if (depth == lastNodeDepth) {
			((DefaultMutableTreeNode) lastNode.getParent()).add(newNode);
		} else {
			int depthDifference = lastNodeDepth - depth;
			
			TreeNode parent = lastNode.getParent();
			for (int i = 0; i < depthDifference; i++) {
				parent = parent.getParent();
			}
			
			((DefaultMutableTreeNode) parent).add(newNode);
		}

		lastNode = newNode;
		lastNodeDepth = depth;
	}
	
	// TreeSelectionListener interface
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
		CARD_LAYOUT.show(RIGHT_PANEL, (String) node.getUserObject());
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		// File Menu
		if (e.getActionCommand().equals(OK)) {
			main_ok();
		} else if (e.getActionCommand().equals(APPLY)) {
			main_apply();
		} else if (e.getActionCommand().equals(CANCEL)) {
			main_cancel();
		}
	}
	
	private void main_ok() {
		main_apply();
		hide();
	}

	private void main_apply() {
		Preferences.applyTemporaryToCurrent();
		Preferences.applyCurrentToApplication();
		Preferences.saveConfigFile(Outliner.CONFIG_FILE);
		Outliner.redrawAllOpenDocuments();
	}

	public void main_cancel() {
		// Restore Prefs
		Preferences.restoreTemporaryToCurrent();

		// Restore GUI
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);

		Iterator it = prefs.getPreferencesPanelKeys();
		while (it.hasNext()) {
			String key = (String) it.next();
			PreferencesPanel panel = prefs.getPreferencesPanel(key);
			panel.setToCurrent();
		}
				

		hide();
	}
}