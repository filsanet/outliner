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
 
package com.organic.maynard.outliner.util.preferences;

import com.organic.maynard.outliner.*;

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
	private static final int MINIMUM_WIDTH = 575;
	private static final int MINIMUM_HEIGHT = 430;
 	private static final int INITIAL_WIDTH = 575;
	private static final int INITIAL_HEIGHT = 430;


	// Instance Fields
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
		jsp.setMinimumSize(new Dimension(175,0));
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
		Outliner.documents.redrawAllOpenDocuments();
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