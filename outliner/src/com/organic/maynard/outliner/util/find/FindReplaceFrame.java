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
 
package com.organic.maynard.outliner.util.find;

import com.organic.maynard.outliner.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import org.xml.sax.*;
import com.organic.maynard.util.string.Replace;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.perl.MalformedPerl5PatternException;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.MatchResult;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class FindReplaceFrame extends AbstractGUITreeJDialog implements ActionListener, KeyListener, ListSelectionListener {

	// Constants
	private static final int MODE_CURRENT_DOCUMENT = 1;
	private static final int MODE_ALL_OPEN_DOCUMENTS = 2;
	private static final int MODE_FILE_SYSTEM = 3;
	private static final int MODE_UNKNOWN = -1;

	private static final int MINIMUM_WIDTH = 500;
	private static final int MINIMUM_HEIGHT = 400;
 	private static final int INITIAL_WIDTH = 500;
	private static final int INITIAL_HEIGHT = 400;

	private static final String REGEX_MATCH_START = "m/";
	private static final String REGEX_MATCH_END = "/";
	private static final String REGEX_MATCH_END_IGNORE_CASE = "/i";

	private static final String REGEX_REPLACE_START = "s/";
	private static final String REGEX_REPLACE_MIDDLE = "/";
	private static final String REGEX_REPLACE_END = "/";
	private static final String REGEX_REPLACE_END_IGNORE_CASE = "/i";

	
	// Perl Regex
	private static Perl5Util util = new Perl5Util();
	private static PatternMatcherInput input = null;
	private static MatchResult result = null;

        	
	// Button Text and Other Copy
	private static String FIND = null;
	private static String FIND_ALL = null;
	private static String REPLACE = null;
	private static String REPLACE_ALL = null;

	private static String NEW = null;
	private static String DELETE = null;

	private static String START_AT_TOP = null;
	private static String WRAP_ARROUND = null;
	private static String SELECTION_ONLY = null;
	private static String IGNORE_CASE = null;
	private static String INCLUDE_READ_ONLY_NODES = null;
	private static String REGEXP = null;
	
	private static String CURRENT_DOCUMENT = "Current Document";
	private static String ALL_OPEN_DOCUMENTS = "All Open Documents";
	private static String FILE_SYSTEM = "File System";
	
	private static String PATH = "Path";
	private static String INCLUDE_SUB_DIRECTORIES = "Include Sub Directories";
	private static String FILE_FILTER = "File Filter";

	// Define Fields and Buttons
	private static JCheckBox CHECKBOX_START_AT_TOP = null;
	private static JCheckBox CHECKBOX_WRAP_AROUND = null;
	private static JCheckBox CHECKBOX_SELECTION_ONLY = null;
	private static JCheckBox CHECKBOX_IGNORE_CASE = null;
	private static JCheckBox CHECKBOX_INCLUDE_READ_ONLY_NODES = null;
	private static JCheckBox CHECKBOX_REGEXP = null;

	private static JRadioButton RADIO_CURRENT_DOCUMENT = null;
	private static JRadioButton RADIO_ALL_OPEN_DOCUMENTS = null;
	private static JRadioButton RADIO_FILE_SYSTEM = null;
	
	private static JButton BUTTON_FIND = null;
	private static JButton BUTTON_FIND_ALL = null;
	private static JButton BUTTON_REPLACE = null;
	private static JButton BUTTON_REPLACE_ALL = null;

	private static JLabel LABEL_FIND = null;
	private static JTextArea TEXTAREA_FIND = null;

	private static JLabel LABEL_REPLACE = null;
	private static JTextArea TEXTAREA_REPLACE = null;

	private static JCheckBox CHECKBOX_INCLUDE_SUB_DIRECTORIES = null;
	private static JComboBox COMBOBOX_PATH = null;
	private static JComboBox COMBOBOX_FILE_FILTER = null;

	// Define the left panel
	protected static JList LIST = new JList();
	private static JScrollPane jsp = null;

	private static JButton BUTTON_NEW = null;
	private static JButton BUTTON_DELETE = null;

	
	// Model
	public static FindReplaceModel model = null;
	
	private static FindReplaceDialog findReplaceDialog = null;
	
	// Static Methods
	private static boolean documentRadiosEnabled = true;
	
	public static void enableButtons() {
		RADIO_CURRENT_DOCUMENT.setEnabled(true);
		RADIO_ALL_OPEN_DOCUMENTS.setEnabled(true);
		
		if (RADIO_CURRENT_DOCUMENT.isSelected()) {
			BUTTON_FIND.setEnabled(true);
			BUTTON_FIND_ALL.setEnabled(true);
			BUTTON_REPLACE.setEnabled(true);
			BUTTON_REPLACE_ALL.setEnabled(true);
			CHECKBOX_START_AT_TOP.setEnabled(true);
			CHECKBOX_WRAP_AROUND.setEnabled(true);
			CHECKBOX_SELECTION_ONLY.setEnabled(true);
			CHECKBOX_INCLUDE_READ_ONLY_NODES.setEnabled(true);
			CHECKBOX_INCLUDE_SUB_DIRECTORIES.setEnabled(false);
			COMBOBOX_PATH.setEnabled(false);
			COMBOBOX_FILE_FILTER.setEnabled(false);
			
		} else if (RADIO_ALL_OPEN_DOCUMENTS.isSelected()) {
			BUTTON_FIND.setEnabled(true);
			BUTTON_FIND_ALL.setEnabled(true);
			BUTTON_REPLACE.setEnabled(true);
			BUTTON_REPLACE_ALL.setEnabled(true);
			CHECKBOX_START_AT_TOP.setEnabled(false);
			CHECKBOX_WRAP_AROUND.setEnabled(false);
			CHECKBOX_SELECTION_ONLY.setEnabled(false);
			CHECKBOX_INCLUDE_READ_ONLY_NODES.setEnabled(true);
			CHECKBOX_INCLUDE_SUB_DIRECTORIES.setEnabled(false);
			COMBOBOX_PATH.setEnabled(false);
			COMBOBOX_FILE_FILTER.setEnabled(false);
			
		}

		documentRadiosEnabled = true;
	}

	public static void disableButtons() {
		RADIO_CURRENT_DOCUMENT.setEnabled(false);
		RADIO_ALL_OPEN_DOCUMENTS.setEnabled(false);

		if (RADIO_CURRENT_DOCUMENT.isSelected() || RADIO_ALL_OPEN_DOCUMENTS.isSelected()) {
			BUTTON_FIND.setEnabled(false);
			BUTTON_FIND_ALL.setEnabled(false);
			BUTTON_REPLACE.setEnabled(false);
			BUTTON_REPLACE_ALL.setEnabled(false);
			CHECKBOX_START_AT_TOP.setEnabled(false);
			CHECKBOX_WRAP_AROUND.setEnabled(false);
			CHECKBOX_SELECTION_ONLY.setEnabled(false);
			CHECKBOX_INCLUDE_READ_ONLY_NODES.setEnabled(false);
			CHECKBOX_INCLUDE_SUB_DIRECTORIES.setEnabled(false);
			COMBOBOX_PATH.setEnabled(false);
			COMBOBOX_FILE_FILTER.setEnabled(false);
		}
				
		documentRadiosEnabled = false;
	}

		
	// The Constructor
	public FindReplaceFrame() {
		super(false, false, false, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);

		FIND = GUITreeLoader.reg.getText("find");
		FIND_ALL = "Find All"; //TBD: update gui tree and use: GUITreeLoader.reg.getText("find_all");
		REPLACE = GUITreeLoader.reg.getText("replace");
		REPLACE_ALL = GUITreeLoader.reg.getText("replace_all");

		START_AT_TOP = GUITreeLoader.reg.getText("start_at_top");
		WRAP_ARROUND = GUITreeLoader.reg.getText("wrap_around");
		SELECTION_ONLY = GUITreeLoader.reg.getText("selection_only");
		IGNORE_CASE = GUITreeLoader.reg.getText("ignore_case");
		INCLUDE_READ_ONLY_NODES = GUITreeLoader.reg.getText("include_read_only_nodes");
		REGEXP = GUITreeLoader.reg.getText("regexp");

		CHECKBOX_REGEXP = new JCheckBox(REGEXP);
		CHECKBOX_REGEXP.addActionListener(this);
		CHECKBOX_START_AT_TOP = new JCheckBox(START_AT_TOP);
		CHECKBOX_START_AT_TOP.addActionListener(this);
		CHECKBOX_WRAP_AROUND = new JCheckBox(WRAP_ARROUND);
		CHECKBOX_WRAP_AROUND.addActionListener(this);
		CHECKBOX_SELECTION_ONLY = new JCheckBox(SELECTION_ONLY);
		CHECKBOX_SELECTION_ONLY.addActionListener(this);
		CHECKBOX_IGNORE_CASE = new JCheckBox(IGNORE_CASE);
		CHECKBOX_IGNORE_CASE.addActionListener(this);
		CHECKBOX_INCLUDE_READ_ONLY_NODES = new JCheckBox(INCLUDE_READ_ONLY_NODES);
		CHECKBOX_INCLUDE_READ_ONLY_NODES.addActionListener(this);

		CHECKBOX_INCLUDE_SUB_DIRECTORIES = new JCheckBox(INCLUDE_SUB_DIRECTORIES);
		CHECKBOX_INCLUDE_SUB_DIRECTORIES.addActionListener(this);
		COMBOBOX_PATH = new JComboBox();
		COMBOBOX_PATH.setEditable(true);
		COMBOBOX_PATH.addActionListener(this);
		COMBOBOX_FILE_FILTER = new JComboBox();
		COMBOBOX_FILE_FILTER.setEditable(true);
		COMBOBOX_FILE_FILTER.addActionListener(this);
		
		RADIO_CURRENT_DOCUMENT = new JRadioButton(CURRENT_DOCUMENT);
		RADIO_CURRENT_DOCUMENT.addActionListener(this);
		RADIO_ALL_OPEN_DOCUMENTS = new JRadioButton(ALL_OPEN_DOCUMENTS);
		RADIO_ALL_OPEN_DOCUMENTS.addActionListener(this);
		RADIO_FILE_SYSTEM = new JRadioButton(FILE_SYSTEM);
		RADIO_FILE_SYSTEM.addActionListener(this);
		
		// Add the radio buttons to a group.
		ButtonGroup radioButtonGroup = new ButtonGroup();
		radioButtonGroup.add(RADIO_CURRENT_DOCUMENT);
		radioButtonGroup.add(RADIO_ALL_OPEN_DOCUMENTS);
		radioButtonGroup.add(RADIO_FILE_SYSTEM);
		RADIO_CURRENT_DOCUMENT.setSelected(true);

		BUTTON_FIND = new JButton(FIND);
		BUTTON_FIND_ALL = new JButton(FIND_ALL);
		BUTTON_REPLACE = new JButton(REPLACE);
		BUTTON_REPLACE_ALL = new JButton(REPLACE_ALL);
		
		LABEL_FIND = new JLabel(FIND);
		TEXTAREA_FIND = new JTextArea();
		TEXTAREA_FIND.getDocument().addDocumentListener(new FindReplaceJTextAreaDocumentListener(FindReplaceJTextAreaDocumentListener.TYPE_FIND));

		LABEL_REPLACE = new JLabel(REPLACE);
		TEXTAREA_REPLACE = new JTextArea();
		TEXTAREA_REPLACE.getDocument().addDocumentListener(new FindReplaceJTextAreaDocumentListener(FindReplaceJTextAreaDocumentListener.TYPE_REPLACE));

		Insets insets = new Insets(1,3,1,3);
		Cursor cursor = new Cursor(Cursor.TEXT_CURSOR);
		
		TEXTAREA_FIND.setName(FIND);
		TEXTAREA_FIND.setCursor(cursor);
		TEXTAREA_FIND.setLineWrap(true);
		TEXTAREA_FIND.setMargin(insets);
		TEXTAREA_FIND.setRows(3);
	
		TEXTAREA_REPLACE.setName(REPLACE);
		TEXTAREA_REPLACE.setCursor(cursor);
		TEXTAREA_REPLACE.setLineWrap(true);
		TEXTAREA_REPLACE.setMargin(insets);
		TEXTAREA_REPLACE.setRows(3);

		// Left Panel
		NEW = GUITreeLoader.reg.getText("new");
		DELETE = GUITreeLoader.reg.getText("delete");

		BUTTON_NEW = new JButton(NEW);
		BUTTON_DELETE = new JButton(DELETE);
		
		LIST.setModel(new DefaultListModel());
		LIST.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		LIST.addListSelectionListener(this);

		LIST.addMouseListener(
			new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						int index = LIST.locationToIndex(e.getPoint());
						DefaultListModel model = (DefaultListModel) LIST.getModel();
						findReplaceDialog.show(FindReplaceDialog.MODE_RENAME);
					}
				}
			}
		);
		
		jsp = new JScrollPane(LIST);
	
		disableButtons();
	}
	
	public void show() {
		TEXTAREA_FIND.requestFocus();
		super.show();
	}


	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		Outliner.findReplace = this;

		model = new FindReplaceModel();
		findReplaceDialog = new FindReplaceDialog();
			
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED), new EmptyBorder(new Insets(5,5,5,5))));
				
		// Match Options
		JPanel matchOptionsPanel = new JPanel();
		matchOptionsPanel.setLayout(new BorderLayout());
		matchOptionsPanel.setBorder(new TitledBorder(" Match "));
		Box matchOptionsBox = Box.createVerticalBox();
		matchOptionsBox.add(CHECKBOX_REGEXP);
		matchOptionsBox.add(CHECKBOX_IGNORE_CASE);
		matchOptionsPanel.add(matchOptionsBox, BorderLayout.CENTER);
		
		// Scope Options
		JPanel scopeOptionsPanel = new JPanel();
		scopeOptionsPanel.setLayout(new BorderLayout());
		scopeOptionsPanel.setBorder(new TitledBorder(" Scope "));
		
		Box scopeOptionsBox = Box.createVerticalBox();
		
		Box documentScopeBox1 = Box.createHorizontalBox();
		documentScopeBox1.add(RADIO_CURRENT_DOCUMENT);
		documentScopeBox1.add(Box.createHorizontalStrut(15));
		documentScopeBox1.add(RADIO_ALL_OPEN_DOCUMENTS);
		scopeOptionsBox.add(documentScopeBox1);
		
		scopeOptionsBox.add(CHECKBOX_START_AT_TOP);
		scopeOptionsBox.add(CHECKBOX_WRAP_AROUND);
		scopeOptionsBox.add(CHECKBOX_SELECTION_ONLY);
		scopeOptionsBox.add(CHECKBOX_INCLUDE_READ_ONLY_NODES);

		scopeOptionsBox.add(Box.createVerticalStrut(5));

		Box documentScopeBox2 = Box.createHorizontalBox();
		documentScopeBox2.add(RADIO_FILE_SYSTEM);
		scopeOptionsBox.add(documentScopeBox2);
		
		scopeOptionsBox.add(COMBOBOX_PATH);
		scopeOptionsBox.add(CHECKBOX_INCLUDE_SUB_DIRECTORIES);
		scopeOptionsBox.add(COMBOBOX_FILE_FILTER);
		
		scopeOptionsPanel.add(scopeOptionsBox, BorderLayout.CENTER);
		
		// Define Button Box
		BUTTON_FIND.addActionListener(this);
		BUTTON_FIND_ALL.addActionListener(this);
		BUTTON_REPLACE.addActionListener(this);
		BUTTON_REPLACE_ALL.addActionListener(this);

		Box buttonBox = Box.createHorizontalBox();
		
		buttonBox.add(BUTTON_FIND);
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(BUTTON_FIND_ALL);
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(BUTTON_REPLACE);
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(BUTTON_REPLACE_ALL);

		// Set the default button.
		getRootPane().setDefaultButton(BUTTON_FIND);

		// Define FindReplace Box
		TEXTAREA_FIND.addKeyListener(this);
		TEXTAREA_REPLACE.addKeyListener(this);
		
		Box findReplaceBox = Box.createVerticalBox();
		
		findReplaceBox.add(LABEL_FIND);
		JScrollPane findScrollPane = new JScrollPane(TEXTAREA_FIND);
		findReplaceBox.add(findScrollPane);
		
		findReplaceBox.add(Box.createVerticalStrut(10));
		
		findReplaceBox.add(LABEL_REPLACE);
		JScrollPane replaceScrollPane = new JScrollPane(TEXTAREA_REPLACE);
		findReplaceBox.add(replaceScrollPane);
		
		findReplaceBox.add(Box.createVerticalStrut(10));
		
		findReplaceBox.add(matchOptionsPanel);
		findReplaceBox.add(scopeOptionsPanel);
		findReplaceBox.add(buttonBox);
		
		rightPanel.add(findReplaceBox, BorderLayout.CENTER);
		
		getContentPane().add(rightPanel, BorderLayout.CENTER);
		
		// Define Left Panel
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED), new EmptyBorder(new Insets(5,5,5,5))));

		BUTTON_NEW.addActionListener(this);
		BUTTON_DELETE.addActionListener(this);

		leftPanel.add(jsp, BorderLayout.CENTER);
		Box listBox = Box.createHorizontalBox();
		listBox.add(BUTTON_NEW);
		listBox.add(Box.createHorizontalStrut(5));
		listBox.add(BUTTON_DELETE);
		leftPanel.add(listBox, BorderLayout.NORTH);

		getContentPane().add(leftPanel, BorderLayout.EAST);
		
		LIST.setSelectedIndex(0);
		
		pack();
	}


	// ListSelectionListenerInterface
	protected int currentIndex = -1;
	
	public void valueChanged(ListSelectionEvent e) {
		this.currentIndex = LIST.getSelectedIndex();
		
		// Sync View to Model for new index
		if ((currentIndex >= 0) && (currentIndex < model.getSize())) {
			CHECKBOX_START_AT_TOP.setSelected(model.getStartAtTop(currentIndex));
			CHECKBOX_WRAP_AROUND.setSelected(model.getWrapAround(currentIndex));
			CHECKBOX_SELECTION_ONLY.setSelected(model.getSelectionOnly(currentIndex));
			CHECKBOX_IGNORE_CASE.setSelected(model.getIgnoreCase(currentIndex));
			CHECKBOX_INCLUDE_READ_ONLY_NODES.setSelected(model.getIncludeReadOnly(currentIndex));
			CHECKBOX_REGEXP.setSelected(model.getRegExp(currentIndex));
			TEXTAREA_FIND.setText(model.getFind(currentIndex));
			TEXTAREA_REPLACE.setText(model.getReplace(currentIndex));
		}
	}
	
	// KeyListener Interface
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			JTextArea text = (JTextArea) e.getSource();
			
			BUTTON_FIND.doClick(100);
			
			e.consume();
			return;
		}
		
		if (e.getKeyChar() == KeyEvent.VK_TAB) {
			JTextArea text = (JTextArea) e.getSource();
			
			if (text.getName().equals(FIND)) {
				TEXTAREA_REPLACE.requestFocus();
			} else if (text.getName().equals(REPLACE)) {
				TEXTAREA_FIND.requestFocus();
			}
			
			e.consume();
			return;
		}
	}

	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		// File Menu
		if (e.getActionCommand().equals(FIND)) {
			find(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(FIND_ALL)) {
			find_all(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(REPLACE)) {
			replace(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(REPLACE_ALL)) {
			replace_all(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(NEW)) {
			newFindReplace();
		} else if (e.getActionCommand().equals(DELETE)) {
			deleteFindReplace();
		
		// CheckBoxes
		} else if (e.getActionCommand().equals(START_AT_TOP)) {
			model.setStartAtTop(currentIndex, CHECKBOX_START_AT_TOP.isSelected());
		} else if (e.getActionCommand().equals(WRAP_ARROUND)) {
			model.setWrapAround(currentIndex, CHECKBOX_WRAP_AROUND.isSelected());
		} else if (e.getActionCommand().equals(SELECTION_ONLY)) {
			model.setSelectionOnly(currentIndex, CHECKBOX_SELECTION_ONLY.isSelected());
		} else if (e.getActionCommand().equals(IGNORE_CASE)) {
			model.setIgnoreCase(currentIndex, CHECKBOX_IGNORE_CASE.isSelected());
		} else if (e.getActionCommand().equals(INCLUDE_READ_ONLY_NODES)) {
			model.setIncludeReadOnly(currentIndex, CHECKBOX_INCLUDE_READ_ONLY_NODES.isSelected());
		} else if (e.getActionCommand().equals(REGEXP)) {
			model.setRegExp(currentIndex, CHECKBOX_REGEXP.isSelected());
		
		// RadioButtons
		} else if (e.getActionCommand().equals(CURRENT_DOCUMENT)) {
			BUTTON_FIND.setEnabled(true);
			BUTTON_FIND_ALL.setEnabled(true);
			BUTTON_REPLACE.setEnabled(true);
			BUTTON_REPLACE_ALL.setEnabled(true);
			CHECKBOX_START_AT_TOP.setEnabled(true);
			CHECKBOX_WRAP_AROUND.setEnabled(true);
			CHECKBOX_SELECTION_ONLY.setEnabled(true);
			CHECKBOX_INCLUDE_READ_ONLY_NODES.setEnabled(true);
			CHECKBOX_INCLUDE_SUB_DIRECTORIES.setEnabled(false);
			COMBOBOX_PATH.setEnabled(false);
			COMBOBOX_FILE_FILTER.setEnabled(false);

		} else if (e.getActionCommand().equals(ALL_OPEN_DOCUMENTS)) {
			BUTTON_FIND.setEnabled(true);
			BUTTON_FIND_ALL.setEnabled(true);
			BUTTON_REPLACE.setEnabled(true);
			BUTTON_REPLACE_ALL.setEnabled(true);
			CHECKBOX_START_AT_TOP.setEnabled(false);
			CHECKBOX_WRAP_AROUND.setEnabled(false);
			CHECKBOX_SELECTION_ONLY.setEnabled(false);
			CHECKBOX_INCLUDE_READ_ONLY_NODES.setEnabled(true);
			CHECKBOX_INCLUDE_SUB_DIRECTORIES.setEnabled(false);
			COMBOBOX_PATH.setEnabled(false);
			COMBOBOX_FILE_FILTER.setEnabled(false);
			
		} else if (e.getActionCommand().equals(FILE_SYSTEM)) {
			BUTTON_FIND.setEnabled(false);
			BUTTON_FIND_ALL.setEnabled(true);
			BUTTON_REPLACE.setEnabled(false);
			BUTTON_REPLACE_ALL.setEnabled(true);
			CHECKBOX_START_AT_TOP.setEnabled(false);
			CHECKBOX_WRAP_AROUND.setEnabled(false);
			CHECKBOX_SELECTION_ONLY.setEnabled(false);
			CHECKBOX_INCLUDE_READ_ONLY_NODES.setEnabled(false);
			CHECKBOX_INCLUDE_SUB_DIRECTORIES.setEnabled(true);
			COMBOBOX_PATH.setEnabled(true);
			COMBOBOX_FILE_FILTER.setEnabled(true);
		}
	}
	
	private int getFindReplaceMode() {
		if (RADIO_CURRENT_DOCUMENT.isSelected()) {
			return MODE_CURRENT_DOCUMENT;
		} else if (RADIO_ALL_OPEN_DOCUMENTS.isSelected()) {
			return MODE_ALL_OPEN_DOCUMENTS;
		} else if (RADIO_FILE_SYSTEM.isSelected()) {
			return MODE_FILE_SYSTEM;
		} else {
			return MODE_UNKNOWN;
		}
	}
	
	private void newFindReplace() {
		findReplaceDialog.show(FindReplaceDialog.MODE_NEW);
	}
	
	private void deleteFindReplace() {
		int selectedIndex = LIST.getSelectedIndex();
		
		if (selectedIndex != -1) { // Don't delete if there's no selection.
			if (selectedIndex != 0) { // Never delete the default.
				String confirm_delete = GUITreeLoader.reg.getText("confirm_delete");
				String msg = GUITreeLoader.reg.getText("do_you_want_to_delete");
				// Confirm Delete
				int result = JOptionPane.showConfirmDialog(Outliner.findReplace, msg, confirm_delete, JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					model.remove(selectedIndex);
					LIST.setSelectedIndex(selectedIndex - 1);
					LIST.requestFocus();
				}
			}
		}
	}
	
	private void find(OutlinerDocument doc) {
		int mode = getFindReplaceMode();
		
		switch (mode) {
			case MODE_CURRENT_DOCUMENT:
				find(
					doc, 
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				break;
			
			case MODE_ALL_OPEN_DOCUMENTS:
				findAllOpenDocuments(
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				break;
			
			case MODE_UNKNOWN:
				System.out.println("ERROR: Unknown Find/Replace mode.");
				break;
		}
	}

	private void find_all(OutlinerDocument doc) {
		int mode = getFindReplaceMode();
		
		FindReplaceResultsModel results = new FindReplaceResultsModel();
		
		switch (mode) {
			case MODE_CURRENT_DOCUMENT:
				findAll(
					results,
					doc, 
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				
				break;
			
			case MODE_ALL_OPEN_DOCUMENTS:
				findAllAllOpenDocuments(
					results,
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				
				break;
			
			case MODE_FILE_SYSTEM:
				Object path = COMBOBOX_PATH.getSelectedItem();
				if (path == null) {
					path = new String("");
				}

				Object extensions = COMBOBOX_FILE_FILTER.getSelectedItem();
				if (extensions == null) {
					extensions = new String("");
				}
				
				findAllFileSystem(
					results,
					path.toString(),
					CHECKBOX_INCLUDE_SUB_DIRECTORIES.isSelected(),
					extensions.toString(), 
					TEXTAREA_FIND.getText(), 
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_REGEXP.isSelected()
				);
				break;
			
			case MODE_UNKNOWN:
				System.out.println("ERROR: Unknown Find/Replace mode.");
				break;
		}

		if (results.size() == 0) {
			// Beep to alert user no result found.
			Outliner.outliner.getToolkit().beep();
			return;
		}

		Outliner.findReplaceResultsDialog.show(results);
	}
	
	private void replace(OutlinerDocument doc) {
		int mode = getFindReplaceMode();

		switch (mode) {
			case MODE_CURRENT_DOCUMENT:
				replace(
					doc, 
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				break;
			
			case MODE_ALL_OPEN_DOCUMENTS:
				replaceAllOpenDocuments(
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				break;
			
			case MODE_UNKNOWN:
				System.out.println("ERROR: Unknown Find/Replace mode.");
				break;
		}
	}

	private void replace_all(OutlinerDocument doc) {
		int mode = getFindReplaceMode();
		
		FindReplaceResultsModel results = new FindReplaceResultsModel();
		
		switch (mode) {
			case MODE_CURRENT_DOCUMENT:
				replaceAll(
					results,
					doc, 
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				
				doc.panel.layout.redraw();
				break;
			
			case MODE_ALL_OPEN_DOCUMENTS:
				replaceAllAllOpenDocuments(
					results,
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				
				Outliner.redrawAllOpenDocuments();
				break;
			
			case MODE_FILE_SYSTEM:
				replaceAllFileSystem(results);
				break;
			
			case MODE_UNKNOWN:
				System.out.println("ERROR: Unknown Find/Replace mode.");
				break;
		}

		if (results.size() == 0) {
			// Beep to alert user no result found.
			Outliner.outliner.getToolkit().beep();
			return;
		}

		Outliner.findReplaceResultsDialog.show(results);
	}
		
	// This method is public and should have no direct dependancy on 
	// find/replace GUI so that it can be called from other classes.
	public void find(
		OutlinerDocument doc, 
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		NodeRangePair location = findLocation(
			doc, 
			sFind, 
			sReplace, 
			false, 
			selectionOnly, 
			startAtTop,
			ignoreCase, 
			includeReadOnlyNodes, 
			wrapAround,
			isRegexp
		);
		
		if (location == null) {
			// Beep to alert user no result found.
			Outliner.outliner.getToolkit().beep();
		} else {
			// Shorthand
			JoeTree tree = doc.tree;

			// Insert the node into the visible nodes and clear the selection.
			tree.insertNode(location.node);
			tree.clearSelection();
			
			// Record the EditingNode and CursorPosition
			tree.setEditingNode(location.node);
			tree.setCursorPosition(location.endIndex);
			tree.setCursorMarkPosition(location.startIndex);
			tree.setComponentFocus(OutlineLayoutManager.TEXT);
			
			// Update Preferred Caret Position
			doc.setPreferredCaretPosition(location.endIndex);
			
			// Freeze Undo Editing
			UndoableEdit.freezeUndoEdit(location.node);
			
			// Bring the window to the front
			try {
				Outliner.outliner.requestFocus();
				doc.setSelected(true);
			} catch (java.beans.PropertyVetoException pve) {
				pve.printStackTrace();
			}

			// Redraw and Set Focus
			doc.panel.layout.draw(location.node,OutlineLayoutManager.TEXT);
		}
	}

	private static FileSystemFind fileSystemFind = null;
	
	private String[] convertListToStringArray(java.util.List list) {
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = (String) list.get(i);
		}
		return array;
	}
	
	public void findAllFileSystem(
		FindReplaceResultsModel model, 
		String startingPath, 
		boolean includeSubDirectories,
		String fileExtensions, 
		String sFind, 
		boolean ignoreCase, 
		boolean isRegexp
	) {
		// Lazy Instantiation
		if (fileSystemFind == null) {
			fileSystemFind = new FileSystemFind();
		}
		
		// Prep Query
		if (isRegexp) {
			sFind = prepareRegEx(false, ignoreCase, sFind, "");
		}
		
		// Prep Extensions List
		ArrayList extensions = new ArrayList();
		StringTokenizer tok = new StringTokenizer(fileExtensions);
		while (tok.hasMoreTokens()) {
			String extension = tok.nextToken();
			extensions.add(extension);
		}

		// Do it
		fileSystemFind.find(model, convertListToStringArray(extensions), startingPath, sFind, isRegexp, includeSubDirectories);
	}
	
	public void replaceAllFileSystem(FindReplaceResultsModel model) {
	
	}

	// This method is public and should have no direct dependancy on 
	// find/replace GUI so that it can be called from other classes.
	public void findAllOpenDocuments(
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		Iterator openDocuments = Outliner.getLoopedOpenDocumentIterator();
		
		boolean matchFound = false;
		
		while (openDocuments.hasNext()) {
			OutlinerDocument doc = (OutlinerDocument) openDocuments.next();
			
			NodeRangePair location = null;
			
			if (doc == Outliner.getMostRecentDocumentTouched()) {
				location = findLocation(
					doc, 
					sFind, 
					sReplace, 
					false, 
					selectionOnly, 
					startAtTop,
					ignoreCase, 
					includeReadOnlyNodes, 
					wrapAround,
					isRegexp
				);
			} else {
				Node firstNode = doc.tree.getRootNode().getFirstChild();
				Node lastNode = doc.tree.getRootNode().getLastChild().getLastDecendent();
				
				location = findText(
					firstNode,
					0, 
					lastNode, 
					lastNode.getValue().length(), 
					sFind,
					sReplace, 
					false, 
					false,
					false,
					ignoreCase,
					includeReadOnlyNodes,
					false,
					isRegexp
				);
			}

			if (location == null) {
				
			} else {
				// Shorthand
				JoeTree tree = doc.tree;
	
				// Insert the node into the visible nodes and clear the selection.
				tree.insertNode(location.node);
				tree.clearSelection();
				
				// Record the EditingNode and CursorPosition
				tree.setEditingNode(location.node);
				tree.setCursorPosition(location.endIndex);
				tree.setCursorMarkPosition(location.startIndex);
				tree.setComponentFocus(OutlineLayoutManager.TEXT);
				
				// Update Preferred Caret Position
				doc.setPreferredCaretPosition(location.endIndex);
				
				// Freeze Undo Editing
				UndoableEdit.freezeUndoEdit(location.node);
				
				// Bring the window to the front
				Outliner.outliner.requestFocus();
				WindowMenu.changeToWindow(doc);
				
				matchFound = true;
				break;
			}
		}
		
		if (!matchFound) {
			// One last try on the entire current doc since we may have missed a match in the portion of the doc before the cursor..
			find(
				Outliner.getMostRecentDocumentTouched(), 
				sFind,
				sReplace,
				false,
				true,
				ignoreCase,
				includeReadOnlyNodes,
				false,
				isRegexp
			);
		}
	}

	public void findAllAllOpenDocuments(
		FindReplaceResultsModel results,
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		Iterator openDocuments = Outliner.getLoopedOpenDocumentIterator();
		
		while (openDocuments.hasNext()) {
			OutlinerDocument doc = (OutlinerDocument) openDocuments.next();
			
			findAll(
				results,
				doc, 
				sFind,
				sReplace,
				false,
				true,
				ignoreCase,
				includeReadOnlyNodes,
				false,
				isRegexp
			);
		}
	}
	
	public void findAll(
		FindReplaceResultsModel results,
		OutlinerDocument doc, 
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		int count = 0;

		String replacement = sReplace;
		String textToMatch = sFind;
		if (textToMatch.equals("")) {
			// Beep to alert user no result found.
			Outliner.outliner.getToolkit().beep();

			return;
		}
		
		if (selectionOnly) {
			if (doc.tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
				if (doc.tree.getCursorPosition() == doc.tree.getCursorMarkPosition()) {
					// No selection, so return.
					return;
				} else {
					int cursor = doc.tree.getCursorPosition();
					int mark = doc.tree.getCursorMarkPosition();

					Node nodeStart = doc.tree.getEditingNode();
					int cursorStart = Math.min(cursor,mark);
					Node nodeEnd = doc.tree.getEditingNode();
					int cursorEnd = Math.max(cursor,mark);			
		
					while (true) {
						//System.out.println("range: " + cursorStart + " : " + cursorEnd);
						if ((nodeStart == nodeEnd) && (cursorStart == cursorEnd)) {break;}
						NodeRangePair location = findText(
							nodeStart,
							cursorStart,
							nodeEnd,
							cursorEnd,
							textToMatch,
							replacement,
							false,
							true,
							true, 
							ignoreCase, 
							includeReadOnlyNodes, 
							wrapAround, 
							isRegexp
						);
						
						if (location == null) {
							if (count == 0) {
								return;
							} else {
								break;
							}
						}
						if (location.loopedOver) {break;}
						
						// Add the Result
						String match = location.node.getValue().substring(location.startIndex, location.endIndex);
						int lineNumber = location.node.getLineNumber();
						FindReplaceResult result = new FindReplaceResult(doc, lineNumber, location.startIndex, match, "");
						results.addResult(result);
						
						nodeStart = location.node;
						cursorStart = location.endIndex;
						
						count++;
					}
					
					// Adjust cursor and mark for new selection.
					doc.tree.setCursorPosition(cursorEnd);
					doc.tree.setCursorMarkPosition(Math.min(cursor,mark));
						
				}
			} else {
				for (int i = 0; i < doc.tree.getSelectedNodes().size(); i++) {					
					Node nodeStart = doc.tree.getSelectedNodes().get(i);
					int cursorStart = 0;
					Node nodeEnd = nodeStart.getLastDecendent();
					int cursorEnd = nodeEnd.getValue().length();
		
					while (true) {
						//System.out.println("range: " + cursorStart + " : " + cursorEnd);
						if ((nodeStart == nodeEnd) && (cursorStart == cursorEnd)) {break;}
						NodeRangePair location = findText(
							nodeStart,
							cursorStart,
							nodeEnd,
							cursorEnd,
							textToMatch,
							replacement,
							false,
							false,
							true, 
							ignoreCase, 
							includeReadOnlyNodes, 
							wrapAround, 
							isRegexp
						);
						
						if (location == null) {
							break;
						}
						if (location.loopedOver) {break;}
						
						// Add the Result
						String match = location.node.getValue().substring(location.startIndex, location.endIndex);
						String replacementTemp = sReplace;
						int lineNumber = location.node.getLineNumber();
						
						FindReplaceResult result = new FindReplaceResult(doc, lineNumber, location.startIndex, match, "");
						results.addResult(result);

						nodeStart = location.node;
						cursorStart = location.endIndex;
						
						count++;
					}					
					
				}
			}	
		} else {
			Node nodeStart = doc.tree.getRootNode().getFirstChild();
			int cursorStart = 0;
			Node nodeEnd = doc.tree.getRootNode().getLastDecendent();
			int cursorEnd = nodeEnd.getValue().length();			

			while (true) {
				//System.out.println("range: " + cursorStart + " : " + cursorEnd);
				NodeRangePair location = findText(
					nodeStart,
					cursorStart,
					nodeEnd,
					cursorEnd,
					textToMatch,
					replacement,
					false,
					false,
					true, 
					ignoreCase, 
					includeReadOnlyNodes, 
					wrapAround, 
					isRegexp
				);
				
				if (location == null) {
					if (count == 0) {
						return;
					} else {
						break;
					}
				}
				if (location.loopedOver) {break;}
				
				// Add the Result
				String match = location.node.getValue().substring(location.startIndex, location.endIndex);
				String replacementTemp = sReplace;
				int lineNumber = location.node.getLineNumber();
				if (isRegexp) {
					replacementTemp = this.replacementText.substring(0, this.matchLength + this.difference);
				}
				FindReplaceResult result = new FindReplaceResult(doc, lineNumber, location.startIndex, match, "");
				results.addResult(result);

				nodeStart = location.node;
				cursorStart = location.endIndex;
				
				count++;
			}
		}
	}




	public void replace(
		OutlinerDocument doc, 
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		NodeRangePair location = findLocation(
			doc, 
			sFind, 
			sReplace, 
			true, 
			selectionOnly, 
			startAtTop,
			ignoreCase, 
			includeReadOnlyNodes, 
			wrapAround,
			isRegexp
		);
		
		if (location == null) {
			// Beep to alert user no result found.
			Outliner.outliner.getToolkit().beep();
		} else {
			// Shorthand
			JoeTree tree = doc.tree;

			// Create the undoable
			int difference = sReplace.length() - (location.endIndex - location.startIndex);
			if (isRegexp) {
				difference = this.difference;
			}

			String oldText = location.node.getValue();
			String newText = oldText.substring(0,location.startIndex) + sReplace + oldText.substring(location.endIndex,oldText.length());

			if (isRegexp) {
				newText = oldText.substring(0,location.startIndex) + this.replacementText;
			}
										
			int oldPosition = location.endIndex;
			int newPosition = location.endIndex + difference;
			doc.undoQueue.add(new UndoableEdit(location.node,oldText,newText,oldPosition,newPosition,oldPosition,location.startIndex));

			// Update the model
			location.node.setValue(newText);
			
			// Insert the node into the visible nodes and clear the selection.
			tree.insertNode(location.node);
			tree.clearSelection();
			
			// Record the EditingNode and CursorPosition
			tree.setEditingNode(location.node);
			tree.setCursorPosition(location.endIndex + difference);
			tree.setCursorMarkPosition(location.startIndex);
			tree.setComponentFocus(OutlineLayoutManager.TEXT);
			
			// Update Preferred Caret Position
			doc.setPreferredCaretPosition(location.endIndex);
			
			// Freeze Undo Editing
			UndoableEdit.freezeUndoEdit(location.node);

			// Bring the window to the front
			try {
				Outliner.outliner.requestFocus();
				doc.setSelected(true);
			} catch (java.beans.PropertyVetoException pve) {
				pve.printStackTrace();
			}
			
			// Redraw and Set Focus
			doc.panel.layout.draw(location.node,OutlineLayoutManager.TEXT);
		}
	}

	// This method is public and should have no direct dependancy on 
	// find/replace GUI so that it can be called from other classes.
	public void replaceAllOpenDocuments(
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		Iterator openDocuments = Outliner.getLoopedOpenDocumentIterator();
		
		boolean matchFound = false;
		
		while (openDocuments.hasNext()) {
			OutlinerDocument doc = (OutlinerDocument) openDocuments.next();
			
			NodeRangePair location = null;
			
			if (doc == Outliner.getMostRecentDocumentTouched()) {
				location = findLocation(
					doc, 
					sFind, 
					sReplace, 
					false, 
					selectionOnly, 
					startAtTop,
					ignoreCase, 
					includeReadOnlyNodes, 
					wrapAround,
					isRegexp
				);
			} else {
				Node firstNode = doc.tree.getRootNode().getFirstChild();
				Node lastNode = doc.tree.getRootNode().getLastChild().getLastDecendent();
				
				location = findText(
					firstNode,
					0, 
					lastNode, 
					lastNode.getValue().length(), 
					sFind,
					sReplace, 
					false, 
					false,
					false,
					ignoreCase,
					includeReadOnlyNodes,
					false,
					isRegexp
				);
			}

			if (location == null) {
				
			} else {
				// Shorthand
				JoeTree tree = doc.tree;
	
				// Create the undoable
				int difference = sReplace.length() - (location.endIndex - location.startIndex);
				if (isRegexp) {
					difference = this.difference;
				}
	
				String oldText = location.node.getValue();
				String newText = oldText.substring(0,location.startIndex) + sReplace + oldText.substring(location.endIndex,oldText.length());
	
				if (isRegexp) {
					newText = oldText.substring(0,location.startIndex) + this.replacementText;
				}
											
				int oldPosition = location.endIndex;
				int newPosition = location.endIndex + difference;
				doc.undoQueue.add(new UndoableEdit(location.node,oldText,newText,oldPosition,newPosition,oldPosition,location.startIndex));
	
				// Update the model
				location.node.setValue(newText);
				
				// Insert the node into the visible nodes and clear the selection.
				tree.insertNode(location.node);
				tree.clearSelection();
				
				// Record the EditingNode and CursorPosition
				tree.setEditingNode(location.node);
				tree.setCursorPosition(location.endIndex + difference);
				tree.setCursorMarkPosition(location.startIndex);
				tree.setComponentFocus(OutlineLayoutManager.TEXT);
				
				// Update Preferred Caret Position
				doc.setPreferredCaretPosition(location.endIndex);
				
				// Freeze Undo Editing
				UndoableEdit.freezeUndoEdit(location.node);
	
				// Bring the window to the front
				Outliner.outliner.requestFocus();
				WindowMenu.changeToWindow(doc);
				
				matchFound = true;
				break;
			}
		}
		
		if (!matchFound) {
			// One last try on the entire current doc since we may have missed a match in the portion of the doc before the cursor..
			replace(
				Outliner.getMostRecentDocumentTouched(), 
				sFind,
				sReplace,
				false,
				true,
				ignoreCase,
				includeReadOnlyNodes,
				false,
				isRegexp
			);
		}
	}

	public void replaceAllAllOpenDocuments(
		FindReplaceResultsModel results,
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		Iterator openDocuments = Outliner.getLoopedOpenDocumentIterator();
		
		while (openDocuments.hasNext()) {
			OutlinerDocument doc = (OutlinerDocument) openDocuments.next();
			
			replaceAll(
				results,
				doc, 
				sFind,
				sReplace,
				false,
				true,
				ignoreCase,
				includeReadOnlyNodes,
				false,
				isRegexp
			);
		}
	}
	
	public void replaceAll(
		FindReplaceResultsModel results,
		OutlinerDocument doc, 
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		int count = 0;

		String replacement = sReplace;
		String textToMatch = sFind;
		if (textToMatch.equals("")) {
			// Beep to alert user no result found.
			Outliner.outliner.getToolkit().beep();

			return;
		}
		
		CompoundUndoableEdit undoable = new CompoundUndoableEdit(doc.tree);
		boolean undoableAdded = false;
		
		if (selectionOnly) {
			if (doc.tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
				if (doc.tree.getCursorPosition() == doc.tree.getCursorMarkPosition()) {
					// No selection, so return.
					return;
				} else {
					int cursor = doc.tree.getCursorPosition();
					int mark = doc.tree.getCursorMarkPosition();

					Node nodeStart = doc.tree.getEditingNode();
					int cursorStart = Math.min(cursor,mark);
					Node nodeEnd = doc.tree.getEditingNode();
					int cursorEnd = Math.max(cursor,mark);			
		
					while (true) {
						//System.out.println("range: " + cursorStart + " : " + cursorEnd);
						if ((nodeStart == nodeEnd) && (cursorStart == cursorEnd)) {break;}
						NodeRangePair location = findText(
							nodeStart,
							cursorStart,
							nodeEnd,
							cursorEnd,
							textToMatch,
							replacement,
							false,
							true,
							true, 
							ignoreCase, 
							includeReadOnlyNodes, 
							wrapAround, 
							isRegexp
						);
						
						if (location == null) {
							if (count == 0) {
								return;
							} else {
								break;
							}
						}
						if (location.loopedOver) {break;}
						
						if (!undoableAdded) {
							doc.undoQueue.add(undoable);
							undoableAdded = true;
						}
						
						// Replace the Text
						String oldText = location.node.getValue();
						String newText = oldText.substring(0,location.startIndex) + sReplace + oldText.substring(location.endIndex,oldText.length()); //
						if (isRegexp) {
							newText = oldText.substring(0,location.startIndex) + this.replacementText;
						}
						location.node.setValue(newText);
						
						// Add the primitive undoable
						undoable.addPrimitive(new PrimitiveUndoableEdit(location.node,oldText,newText));
						
						// Add the Result
						String match = oldText.substring(location.startIndex, location.endIndex);
						String replacementTemp = sReplace;
						int lineNumber = location.node.getLineNumber();
						if (isRegexp) {
							replacementTemp = this.replacementText.substring(0, this.matchLength + this.difference);
						}
						FindReplaceResult result = new FindReplaceResult(doc, lineNumber, location.startIndex, match, replacementTemp); //TBD: line needs to be real.
						results.addResult(result);
						
						// Setup for next replacement
						int difference = sReplace.length() - (location.endIndex - location.startIndex);
						if (isRegexp) {
							difference = this.difference;
						}

		
						if (nodeEnd == location.node) {
							cursorEnd += difference;
						}
						nodeStart = location.node;
						cursorStart = location.endIndex + difference;
						
						count++;
					}
					
					// Adjust cursor and mark for new selection.
					doc.tree.setCursorPosition(cursorEnd);
					doc.tree.setCursorMarkPosition(Math.min(cursor,mark));
						
				}
			} else {
				for (int i = 0; i < doc.tree.getSelectedNodes().size(); i++) {					
					Node nodeStart = doc.tree.getSelectedNodes().get(i);
					int cursorStart = 0;
					Node nodeEnd = nodeStart.getLastDecendent();
					int cursorEnd = nodeEnd.getValue().length();
		
					while (true) {
						//System.out.println("range: " + cursorStart + " : " + cursorEnd);
						if ((nodeStart == nodeEnd) && (cursorStart == cursorEnd)) {break;}
						NodeRangePair location = findText(
							nodeStart,
							cursorStart,
							nodeEnd,
							cursorEnd,
							textToMatch,
							replacement,
							false,
							false,
							true, 
							ignoreCase, 
							includeReadOnlyNodes, 
							wrapAround, 
							isRegexp
						);
						
						if (location == null) {
							break;
						}
						if (location.loopedOver) {break;}
						
						if (!undoableAdded) {
							doc.undoQueue.add(undoable);
							undoableAdded = true;
						}
						
						// Replace the Text
						String oldText = location.node.getValue();
						String newText = oldText.substring(0,location.startIndex) + sReplace + oldText.substring(location.endIndex,oldText.length()); //
						if (isRegexp) {
							newText = oldText.substring(0,location.startIndex) + this.replacementText;
						}
						location.node.setValue(newText);
						
						// Add the primitive undoable
						undoable.addPrimitive(new PrimitiveUndoableEdit(location.node,oldText,newText));

						// Add the Result
						String match = oldText.substring(location.startIndex, location.endIndex);
						String replacementTemp = sReplace;
						int lineNumber = location.node.getLineNumber();
						if (isRegexp) {
							replacementTemp = this.replacementText.substring(0, this.matchLength + this.difference);
						}
						FindReplaceResult result = new FindReplaceResult(doc, lineNumber, location.startIndex, match, replacementTemp); //TBD: line needs to be real.
						results.addResult(result);

						// Setup for next replacement
						int difference = sReplace.length() - (location.endIndex - location.startIndex);
						if (isRegexp) {
							difference = this.difference;
						}
		
						if (nodeEnd == location.node) {
							cursorEnd += difference;
						}
						nodeStart = location.node;
						cursorStart = location.endIndex + difference;
						
						count++;
					}					
					
				}
			}	
		} else {
			Node nodeStart = doc.tree.getRootNode().getFirstChild();
			int cursorStart = 0;
			Node nodeEnd = doc.tree.getRootNode().getLastDecendent();
			int cursorEnd = nodeEnd.getValue().length();			

			while (true) {
				//System.out.println("range: " + cursorStart + " : " + cursorEnd);
				NodeRangePair location = findText(
					nodeStart,
					cursorStart,
					nodeEnd,
					cursorEnd,
					textToMatch,
					replacement,
					false,
					false,
					true, 
					ignoreCase, 
					includeReadOnlyNodes, 
					wrapAround, 
					isRegexp
				);
				
				if (location == null) {
					if (count == 0) {
						return;
					} else {
						break;
					}
				}
				if (location.loopedOver) {break;}
				
				if (!undoableAdded) {
					doc.undoQueue.add(undoable);
					undoableAdded = true;
				}
				
				// Replace the Text
				String oldText = location.node.getValue();
				String newText = oldText.substring(0,location.startIndex) + sReplace + oldText.substring(location.endIndex,oldText.length()); //
				if (isRegexp) {
					newText = oldText.substring(0,location.startIndex) + this.replacementText;
				}
				location.node.setValue(newText);
				
				// Add the primitive undoable
				undoable.addPrimitive(new PrimitiveUndoableEdit(location.node,oldText,newText));

				// Add the Result
				String match = oldText.substring(location.startIndex, location.endIndex);
				String replacementTemp = sReplace;
				int lineNumber = location.node.getLineNumber();
				if (isRegexp) {
					replacementTemp = this.replacementText.substring(0, this.matchLength + this.difference);
				}
				FindReplaceResult result = new FindReplaceResult(doc, lineNumber, location.startIndex, match, replacementTemp); //TBD: line needs to be real.
				results.addResult(result);

				// Setup for next replacement
				int difference = sReplace.length() - (location.endIndex - location.startIndex);
				if (isRegexp) {
					difference = this.difference;
				}

				if (nodeEnd == location.node) {
					cursorEnd += difference;
				}
				nodeStart = location.node;
				cursorStart = location.endIndex + difference;
				
				count++;
			}
		}
	}
	
	private NodeRangePair findLocation (
		OutlinerDocument doc, 
		String textToMatch,
		String replacement,
		boolean isReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		NodeRangePair location = null;
		
		if (textToMatch.equals("")) {
			return null;
		}

		if (selectionOnly) {
			if (doc.tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
				if (doc.tree.getCursorPosition() == doc.tree.getCursorMarkPosition()) {
					// No selection, so return.
					return null;
				} else {
					Node node = doc.tree.getEditingNode();
					int cursor = doc.tree.getCursorPosition();
					int mark = doc.tree.getCursorMarkPosition();
					
					location = findText(node, Math.min(cursor,mark), node, Math.max(cursor,mark), textToMatch, replacement, false, true, isReplace, ignoreCase, includeReadOnlyNodes, wrapAround, isRegexp);
				}
			} else {
				for (int i = 0; i < doc.tree.getSelectedNodes().size(); i++) {
					// Record the Insert in the undoable
					Node nodeStart = doc.tree.getSelectedNodes().get(i);
					int cursorStart = 0;
					Node nodeEnd = nodeStart.getLastDecendent();
					int cursorEnd = nodeEnd.getValue().length();
					
					location = findText(nodeStart, cursorStart, nodeEnd, cursorEnd, textToMatch, replacement, false, false, isReplace, ignoreCase, includeReadOnlyNodes, wrapAround, isRegexp);
					
					if (location != null) {
						break;
					}
				}
			}
		} else {
			// End Values
			Node nodeEnd = doc.tree.getEditingNode();
			int cursorEnd = doc.tree.getCursorPosition();

			// Start Values
			Node nodeStart = null;
			int cursorStart = 0;
	
			if (startAtTop) {
				nodeStart = doc.tree.getRootNode().getFirstChild();
				nodeEnd = doc.tree.getRootNode().getLastDecendent();
				cursorStart = 0;
			} else {
				nodeStart = doc.tree.getEditingNode();
				cursorStart = doc.tree.getCursorPosition();
			}
			
			if (nodeStart.isSelected()) {
				cursorStart = 0;
				cursorEnd = 0;
			}
			
			location = findText(nodeStart, cursorStart, nodeEnd, cursorEnd, textToMatch, replacement, false, false, isReplace, ignoreCase, includeReadOnlyNodes, wrapAround, isRegexp);
		}
		
		return location;
	}
	
	private NodeRangePair findText(
		Node startNode, 
		int start, 
		Node endNode, 
		int end, 
		String match,
		String replacement, 
		boolean loopedOver, 
		boolean done,
		boolean isReplace,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		// [srk] possible bug w/ bad params
		// check for nulls
		if (startNode == null
			|| endNode == null
			|| match == null
			|| replacement == null) { return null; }
				
		String text = startNode.getValue();
		
		// Find the match
		int matchStart = -1;
		if (startNode == endNode) {
			if (end > start) {
				matchStart = matchText(text.substring(start,end), match, replacement, ignoreCase, isRegexp, isReplace);
				done = true;
			} else {
				matchStart = matchText(text.substring(start,text.length()), match, replacement, ignoreCase, isRegexp, isReplace);
			}
		} else {
			matchStart = matchText(text.substring(start,text.length()), match, replacement, ignoreCase, isRegexp, isReplace);
		}
				
		// Match Found		
		if (matchStart != -1) {
			// Deal with read-only nodes for replace
			if (isReplace && !includeReadOnlyNodes && !startNode.isEditable()) {
				// Do nothing so we keep Looking
			} else {
				matchStart += start;
				int matchEnd = matchStart;
				if (isRegexp) {
					matchEnd += this.matchLength;
				} else {
					matchEnd += match.length();
				}
				return new NodeRangePair(startNode,matchStart,matchEnd,loopedOver);
			}
		}
		
		// We ran out of places to look.
		if (done) {
			return null;
		}
				
		// No match found, so move on to the next node.
		Node nextNodeToSearch = startNode.nextNode();
		if (nextNodeToSearch.isRoot()) {
			if (!wrapAround) {
				return null;
			}
			nextNodeToSearch = nextNodeToSearch.nextNode();
			loopedOver = true;
		}
		
		// We've gone as far as we can so stop.
		if (endNode == nextNodeToSearch) {
			done = true;
		}

		// Try it again
		return findText(nextNodeToSearch, 0, endNode, end, match, replacement, loopedOver, done, isReplace, ignoreCase, includeReadOnlyNodes, wrapAround, isRegexp);
	}


	private int matchLength = 0;
	private int difference = 0;
	private String replacementText = null;
	
	private String prepareRegEx(boolean isReplace, boolean ignoreCase, String match, String replacement) {
		StringBuffer retVal = new StringBuffer();
		
		if (isReplace) {
			retVal.append(REGEX_REPLACE_START).append(match).append(REGEX_REPLACE_MIDDLE).append(replacement);
			if (ignoreCase) {
				retVal.append(REGEX_REPLACE_END_IGNORE_CASE);
			} else {
				retVal.append(REGEX_REPLACE_END);
			}
		
		} else {
			retVal.append(REGEX_MATCH_START).append(match);
			if (ignoreCase) {
				retVal.append(REGEX_MATCH_END_IGNORE_CASE);
			} else {
				retVal.append(REGEX_MATCH_END);
			}		
		}
		
		return retVal.toString();
	}
	
	private int matchText(
		String text, 
		String match, 
		String replacement, 
		boolean ignoreCase,
		boolean isRegexp,
		boolean isReplace
	) {
		if (isRegexp) {
			// Prepare input
			input = new PatternMatcherInput(text);

			// Prepare the regex
			String regex = prepareRegEx(isReplace, ignoreCase, match, replacement);
							
			if (isReplace) {
				// Prepare the replacement regex
				String subRegex = prepareRegEx(isReplace, ignoreCase, match, replacement);
								
				// Do the regex find and return result
				try {
					if (util.match(regex, input)) {
						result = util.getMatch();
						
						this.replacementText = util.substitute(subRegex, text);
						
						this.matchLength = result.length(); // Store length since this method does not return it.
						this.difference = this.replacementText.length() - text.length();
						
						int matchStartIndex = result.beginOffset(0);
						int matchEndIndex = matchStartIndex + this.matchLength;
						int replacementEndIndex = matchEndIndex + this.difference;
						
						this.replacementText = this.replacementText.substring(matchStartIndex, this.replacementText.length());
						return matchStartIndex;
					}
				} catch (MalformedPerl5PatternException e) {
					System.out.println("MalformedPerl5PatternException: " + e.getMessage());
				}
				return -1;
			} else {
				// Do the regex find and return result
				try {
					if (util.match(regex, input)) {
						result = util.getMatch();
						matchLength = result.length(); // Store length since this method does not return it.
						return result.beginOffset(0);
					}
				} catch (MalformedPerl5PatternException e) {
					System.out.println("MalformedPerl5PatternException: " + e.getMessage());
				}
				return -1;
			}

		} else {
			if (ignoreCase) {
				text = text.toLowerCase();
				match = match.toLowerCase();
				return text.indexOf(match);
			} else {
				return text.indexOf(match);
			}
		}
	}
}

class FindReplaceDialog extends JDialog implements ActionListener {

	// Constants
	public static final int MODE_NEW = 0;
	public static final int MODE_RENAME = 1;

	private int currentMode = -1;
	
	private static String OK = null;
	private static String CANCEL = null;
	private static String NEW_FIND_REPLACE = null;
	private static String RENAME_FIND_REPLACE = null;
	private static String NAME = null;

	private static String ERROR_EXISTANCE = null;


	// GUI Elements
	private JButton buttonOK = null;
	private JButton buttonCancel = null;
	private JTextField nameField = null;
	private JLabel errorLabel = null;

	// Constructors	
	public FindReplaceDialog() {
		super(Outliner.findReplace, "", true);
		
		OK = GUITreeLoader.reg.getText("ok");
		CANCEL = GUITreeLoader.reg.getText("cancel");
		NEW_FIND_REPLACE = GUITreeLoader.reg.getText("new_find_replace");
		RENAME_FIND_REPLACE = GUITreeLoader.reg.getText("rename_find_replace");
		NAME = GUITreeLoader.reg.getText("name");
		ERROR_EXISTANCE = GUITreeLoader.reg.getText("error_name_existance");

		buttonOK = new JButton(OK);
		buttonCancel = new JButton(CANCEL);
		nameField = new JTextField(20);
		errorLabel = new JLabel(" ");
		
		// Create the Layout
		setResizable(false);
		
		// Adding window adapter to fix problem where initial focus won't go to the textfield.
		// Solution found at: http://forums.java.sun.com/thread.jsp?forum=57&thread=124417&start=15&range=15;
		addWindowListener(
			new WindowAdapter() {
				public void windowOpened(WindowEvent e) {
					nameField.requestFocus();
				}
			}
		);

		// Define the Bottom Panel
		JPanel bottomPanel = new JPanel();

		bottomPanel.setLayout(new FlowLayout());
		
		buttonOK.addActionListener(this);
		bottomPanel.add(buttonOK);

		buttonCancel.addActionListener(this);
		bottomPanel.add(buttonCancel);

		getContentPane().add(bottomPanel,BorderLayout.SOUTH);

		// Define the Center Panel
		Box box = Box.createVerticalBox();

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(NAME), box);
		AbstractPreferencesPanel.addSingleItemCentered(nameField, box);

		box.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(errorLabel, box);

		getContentPane().add(box,BorderLayout.CENTER);

		// Set the default button
		getRootPane().setDefaultButton(buttonOK);
		
		pack();
	}
	
	public void show(int mode) {
		this.currentMode = mode;
		
		if (mode == MODE_NEW) {
			setTitle(NEW_FIND_REPLACE);
			nameField.setText("");
		} else if (mode == MODE_RENAME) {
			setTitle(RENAME_FIND_REPLACE);
			FindReplaceModel model = Outliner.findReplace.model;
			String name = model.getName(Outliner.findReplace.LIST.getSelectedIndex());
			nameField.setText(name);
		}
		
		errorLabel.setText(" ");
		
		nameField.requestFocus();

		Rectangle r = Outliner.outliner.getBounds();
		setLocation((int) (r.getCenterX() - getWidth()/2), (int) (r.getCenterY() - getHeight()/2));
		
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
		String name = nameField.getText();
		
		// Validate Existence
		if ((name == null) || name.equals("")) {
			errorLabel.setText(ERROR_EXISTANCE);
			return;
		}
		
		// All is good so lets make the change
		FindReplaceModel model = Outliner.findReplace.model;

		JList list = Outliner.findReplace.LIST;
		
		if (currentMode == MODE_NEW) {
			model.add(model.getSize(), name, "", "", false, false, false, false, false, false);
			list.setSelectedIndex(model.getSize() - 1);
		} else if (currentMode == MODE_RENAME) {
			model.setName(Outliner.findReplace.LIST.getSelectedIndex(), name);
		}
		
		list.requestFocus();
		
		this.hide();
	}

	private void cancel() {
		hide();
	}
}


class FindReplaceJTextAreaDocumentListener implements DocumentListener {
	public static final int TYPE_FIND = 0;
	public static final int TYPE_REPLACE = 1;
	
	private int type = -1;	
	public FindReplaceJTextAreaDocumentListener(int type) {
		this.type = type;
	}

	public void changedUpdate(DocumentEvent e) {update(e);}
	public void insertUpdate(DocumentEvent e) {update(e);}
	public void removeUpdate(DocumentEvent e) {update(e);}
	
	private void update(DocumentEvent e) {
		javax.swing.text.Document doc = e.getDocument();
		
		int currentIndex = Outliner.findReplace.currentIndex;
		String text = "";
		try {
			text = doc.getText(0, doc.getLength());
		} catch (javax.swing.text.BadLocationException ble) {
			ble.printStackTrace();
		}
		
		if (type == TYPE_FIND) {
			Outliner.findReplace.model.setFind(currentIndex, text);
		} else if (type == TYPE_REPLACE) {
			Outliner.findReplace.model.setReplace(currentIndex, text);
		}
	}
}