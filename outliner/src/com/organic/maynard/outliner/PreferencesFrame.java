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
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.border.*;

public class PreferencesFrame extends JFrame implements TreeSelectionListener, ActionListener {

	static final int MIN_WIDTH = 350;
	static final int MIN_HEIGHT = 375;
 
 	static final int INITIAL_WIDTH = 350;
	static final int INITIAL_HEIGHT = 375;

	// Main Component Containers
	public static final JPanel RIGHT_PANEL = new JPanel();
	public static final CardLayout CARD_LAYOUT = new CardLayout();
	public static final JPanel BOTTOM_PANEL = new JPanel();
	
	public static final JPanel cardLookAndFeel = new JPanel();
	public static final JPanel cardOpenAndSave = new JPanel();
	public static final JPanel cardEditor = new JPanel();
	public static final JPanel cardMisc = new JPanel();
	
	// Button Text and Other Copy
	public static final String OK = "OK";
	public static final String CANCEL = "Cancel";
	public static final String APPLY = "Apply";
	public static final String RESTORE_DEFAULTS = "Restore Defaults";
	public static final String FOREGROUND_COLOR = "Foreground Color";
	public static final String BACKGROUND_COLOR = "Background Color";
	public static final String DESKTOP_COLOR = "Desktop Color";
	public static final String TEXT_COLOR = "Text/Selection Color";
	public static final String SELECTED_CHILDREN_COLOR = "Selected Children Color";

	public static final String PANEL_EDITOR = "Editor";
	public static final String PANEL_LOOK_AND_FEEL = "Look & Feel";
	public static final String PANEL_OPEN_AND_SAVE = "Open & Save";
	public static final String PANEL_MISC = "Misc.";

	// Define Fields and Buttons
	
		// Main Panel
		public static final JButton BOTTOM_OK = new JButton(OK);
		public static final JButton BOTTOM_CANCEL = new JButton(CANCEL);
		public static final JButton BOTTOM_APPLY = new JButton(APPLY);
	
		// Look And Feel Panel
		public static final JButton DESKTOP_BACKGROUND_COLOR_BUTTON = new JButton("");
		public static final JButton PANEL_BACKGROUND_COLOR_BUTTON = new JButton("");
		public static final JButton TEXTAREA_BACKGROUND_COLOR_BUTTON = new JButton("");
		public static final JButton TEXTAREA_FOREGROUND_COLOR_BUTTON = new JButton("");
		public static final JButton SELECTED_CHILD_COLOR_BUTTON = new JButton("");
		public static final JTextField INDENT_FIELD = new JTextField(4);
		public static final JTextField VERTICAL_SPACING_FIELD = new JTextField(4);
		public static final JTextField LEFT_MARGIN_FIELD = new JTextField(4);
		public static final JTextField RIGHT_MARGIN_FIELD = new JTextField(4);
		public static final JTextField TOP_MARGIN_FIELD = new JTextField(4);
		public static final JTextField BOTTOM_MARGIN_FIELD = new JTextField(4);
		public static final JButton RESTORE_DEFAULT_LOOK_AND_FEEL_BUTTON = new JButton(RESTORE_DEFAULTS);
		
		// Editor Panel
		public static final JTextField UNDO_QUEUE_SIZE_FIELD = new JTextField(4);
		public static final JTextField FONT_SIZE_FIELD = new JTextField(4);

		public static final GraphicsEnvironment GRAPHICS_ENVIRONEMNT = GraphicsEnvironment.getLocalGraphicsEnvironment();
		public static final JComboBox FONT_FACE_COMBOBOX = new JComboBox(GRAPHICS_ENVIRONEMNT.getAvailableFontFamilyNames());
		
		public static final String[] LINE_WRAP_OPTIONS = {Preferences.TXT_WORDS,Preferences.TXT_CHARACTERS};
		public static final JComboBox LINE_WRAP_COMBOBOX = new JComboBox(LINE_WRAP_OPTIONS);
		
		public static final JButton RESTORE_DEFAULT_EDITOR_BUTTON = new JButton(RESTORE_DEFAULTS);

		// Misc Panel
		public static final JTextField RECENT_FILES_LIST_SIZE_FIELD = new JTextField(2);
		public static final JCheckBox PRINT_ENVIRONMENT_CHECKBOX = new JCheckBox();
		public static final JCheckBox NEW_DOC_ON_STARTUP_CHECKBOX = new JCheckBox();
		public static final JButton RESTORE_DEFAULT_MISC_BUTTON = new JButton(RESTORE_DEFAULTS);

		// Open And Save Panel
		public static final JComboBox LINE_END_COMBOBOX = new JComboBox(Preferences.PLATFORM_IDENTIFIERS);
		public static final JComboBox OPEN_ENCODING_COMBOBOX = new JComboBox();
		public static final JComboBox SAVE_ENCODING_COMBOBOX = new JComboBox();
		
		public static final JButton RESTORE_DEFAULT_OPEN_AND_SAVE_BUTTON = new JButton(RESTORE_DEFAULTS);
		
		static {
			// Look and Feel
			DESKTOP_BACKGROUND_COLOR_BUTTON.addActionListener(new LookAndFeelAction());
			DESKTOP_BACKGROUND_COLOR_BUTTON.setActionCommand(DESKTOP_COLOR);
			PANEL_BACKGROUND_COLOR_BUTTON.addActionListener(new LookAndFeelAction());
			PANEL_BACKGROUND_COLOR_BUTTON.setActionCommand(BACKGROUND_COLOR);
			TEXTAREA_BACKGROUND_COLOR_BUTTON.addActionListener(new LookAndFeelAction());
			TEXTAREA_BACKGROUND_COLOR_BUTTON.setActionCommand(FOREGROUND_COLOR);
			TEXTAREA_FOREGROUND_COLOR_BUTTON.addActionListener(new LookAndFeelAction());
			TEXTAREA_FOREGROUND_COLOR_BUTTON.setActionCommand(TEXT_COLOR);
			SELECTED_CHILD_COLOR_BUTTON.addActionListener(new LookAndFeelAction());
			SELECTED_CHILD_COLOR_BUTTON.setActionCommand(SELECTED_CHILDREN_COLOR);
					
			RESTORE_DEFAULT_LOOK_AND_FEEL_BUTTON.addActionListener(new LookAndFeelAction());		
			INDENT_FIELD.addFocusListener(new TextFieldListener(INDENT_FIELD, Preferences.INDENT));
			VERTICAL_SPACING_FIELD.addFocusListener(new TextFieldListener(VERTICAL_SPACING_FIELD, Preferences.VERTICAL_SPACING));
			LEFT_MARGIN_FIELD.addFocusListener(new TextFieldListener(LEFT_MARGIN_FIELD, Preferences.LEFT_MARGIN));
			RIGHT_MARGIN_FIELD.addFocusListener(new TextFieldListener(RIGHT_MARGIN_FIELD, Preferences.RIGHT_MARGIN));
			TOP_MARGIN_FIELD.addFocusListener(new TextFieldListener(TOP_MARGIN_FIELD, Preferences.TOP_MARGIN));
			BOTTOM_MARGIN_FIELD.addFocusListener(new TextFieldListener(BOTTOM_MARGIN_FIELD, Preferences.BOTTOM_MARGIN));

			// Editor
			RESTORE_DEFAULT_EDITOR_BUTTON.addActionListener(new EditorAction());		
			UNDO_QUEUE_SIZE_FIELD.addFocusListener(new TextFieldListener(UNDO_QUEUE_SIZE_FIELD, Preferences.UNDO_QUEUE_SIZE));
			FONT_SIZE_FIELD.addFocusListener(new TextFieldListener(FONT_SIZE_FIELD, Preferences.FONT_SIZE));
			FONT_FACE_COMBOBOX.addItemListener(new ComboBoxListener(FONT_FACE_COMBOBOX, Preferences.FONT_FACE));
			LINE_WRAP_COMBOBOX.addItemListener(new ComboBoxListener(LINE_WRAP_COMBOBOX, Preferences.LINE_WRAP));

			// Open and Save
			RESTORE_DEFAULT_OPEN_AND_SAVE_BUTTON.addActionListener(new OpenAndSaveAction());		
			LINE_END_COMBOBOX.addItemListener(new ComboBoxListener(LINE_END_COMBOBOX, Preferences.LINE_END));
			
			for (int i = 0; i < Preferences.ENCODINGS.size(); i++) {
				String encoding = (String) Preferences.ENCODINGS.elementAt(i);
				OPEN_ENCODING_COMBOBOX.addItem(encoding);
				SAVE_ENCODING_COMBOBOX.addItem(encoding);
			}
			
			OPEN_ENCODING_COMBOBOX.addItemListener(new ComboBoxListener(OPEN_ENCODING_COMBOBOX, Preferences.OPEN_ENCODING));
			SAVE_ENCODING_COMBOBOX.addItemListener(new ComboBoxListener(SAVE_ENCODING_COMBOBOX, Preferences.SAVE_ENCODING));

			// Misc
			RESTORE_DEFAULT_MISC_BUTTON.addActionListener(new MiscAction());		
			RECENT_FILES_LIST_SIZE_FIELD.addFocusListener(new TextFieldListener(RECENT_FILES_LIST_SIZE_FIELD, Preferences.RECENT_FILES_LIST_SIZE));
			PRINT_ENVIRONMENT_CHECKBOX.addActionListener(new CheckboxListener(PRINT_ENVIRONMENT_CHECKBOX, Preferences.PRINT_ENVIRONMENT));
			NEW_DOC_ON_STARTUP_CHECKBOX.addActionListener(new CheckboxListener(NEW_DOC_ON_STARTUP_CHECKBOX, Preferences.NEW_DOC_ON_STARTUP));
		}
		
	// The Constructor
	public PreferencesFrame() {
		super("Preferences");
		setVisible(false);
		
		addComponentListener(new WindowSizeManager(MIN_WIDTH,MIN_HEIGHT));
		addWindowListener(new PreferencesFrameWindowMonitor());
			
		// Create the Layout
		setSize(INITIAL_WIDTH,INITIAL_HEIGHT);
		setResizable(true);
		
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
		
		// Define the JTree
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("");
		
			DefaultMutableTreeNode editorNode = new DefaultMutableTreeNode(PANEL_EDITOR);
			rootNode.add(editorNode);
			
			DefaultMutableTreeNode lookAndFeelNode = new DefaultMutableTreeNode(PANEL_LOOK_AND_FEEL);
			rootNode.add(lookAndFeelNode);
			
			DefaultMutableTreeNode openAndSaveNode = new DefaultMutableTreeNode(PANEL_OPEN_AND_SAVE);
			rootNode.add(openAndSaveNode);
			
			DefaultMutableTreeNode miscNode = new DefaultMutableTreeNode(PANEL_MISC);
			rootNode.add(miscNode);
		
		DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
		
		JTree tree = new JTree(treeModel);
		tree.addTreeSelectionListener(this);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		JScrollPane jsp = new JScrollPane(tree);

		// Define the Right Panel
		RIGHT_PANEL.setLayout(CARD_LAYOUT);

		setupCardEditor();
		RIGHT_PANEL.add(cardEditor,PANEL_EDITOR);

		setupCardLookAndFeel();
		RIGHT_PANEL.add(cardLookAndFeel,PANEL_LOOK_AND_FEEL);

		setupCardOpenAndSave();
		RIGHT_PANEL.add(cardOpenAndSave,PANEL_OPEN_AND_SAVE);

		setupCardMisc();
		RIGHT_PANEL.add(cardMisc,PANEL_MISC);
		
		JScrollPane jsp2 = new JScrollPane(RIGHT_PANEL);
		
		// Put it all together
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,jsp,jsp2);
		getContentPane().add(BOTTOM_PANEL, BorderLayout.SOUTH);
		getContentPane().add(splitPane, BorderLayout.CENTER);
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
		} else if (e.getActionCommand().equals(CANCEL)) {
			main_cancel();
		} else if (e.getActionCommand().equals(APPLY)) {
			main_apply();
		}
	}
	
	private void main_ok() {
		Preferences.applyTemporaryToCurrent();
		Outliner.redrawAllOpenDocuments();
		Preferences.saveConfigFile(Outliner.CONFIG_FILE);
		this.setVisible(false);
	}

	public void main_cancel() {
		// Restore Prefs
		Preferences.restoreTemporaryToCurrent();
		
		// Restore GUI
		LookAndFeelAction.setLookAndFeelToCurrent();
		EditorAction.setEditorToCurrent();
		OpenAndSaveAction.setOpenAndSaveToCurrent();
		MiscAction.setMiscToCurrent();
		
		// Hide Window
		this.setVisible(false);
	}

	private void main_apply() {
		Preferences.applyTemporaryToCurrent();
		Outliner.redrawAllOpenDocuments();
		Preferences.saveConfigFile(Outliner.CONFIG_FILE);
	}
	
	// Setup Preference Panels
	private void setupCardLookAndFeel() {
		LookAndFeelAction.setLookAndFeelToCurrent();

		Box lookAndFeelBox = Box.createVerticalBox();

		JLabel lookAndFeelLabel = new JLabel(PANEL_LOOK_AND_FEEL);
		addSingleItemCentered(lookAndFeelLabel, lookAndFeelBox);
		
		lookAndFeelBox.add(Box.createVerticalStrut(10));

		addPreferenceItem(DESKTOP_COLOR, DESKTOP_BACKGROUND_COLOR_BUTTON, lookAndFeelBox);
		addPreferenceItem(BACKGROUND_COLOR, PANEL_BACKGROUND_COLOR_BUTTON, lookAndFeelBox);
		addPreferenceItem(FOREGROUND_COLOR, TEXTAREA_BACKGROUND_COLOR_BUTTON, lookAndFeelBox);
		addPreferenceItem(TEXT_COLOR, TEXTAREA_FOREGROUND_COLOR_BUTTON, lookAndFeelBox);
		addPreferenceItem(SELECTED_CHILDREN_COLOR, SELECTED_CHILD_COLOR_BUTTON, lookAndFeelBox);

		lookAndFeelBox.add(Box.createVerticalStrut(5));

		addPreferenceItem("Indent", INDENT_FIELD, lookAndFeelBox);

		lookAndFeelBox.add(Box.createVerticalStrut(5));

		addPreferenceItem("Vertical Spacing", VERTICAL_SPACING_FIELD, lookAndFeelBox);

		lookAndFeelBox.add(Box.createVerticalStrut(5));

		addPreferenceItem("Left Margin", LEFT_MARGIN_FIELD, lookAndFeelBox);
		addPreferenceItem("Right Margin", RIGHT_MARGIN_FIELD, lookAndFeelBox);
		addPreferenceItem("Top Margin", TOP_MARGIN_FIELD, lookAndFeelBox);
		addPreferenceItem("Bottom Margin", BOTTOM_MARGIN_FIELD, lookAndFeelBox);

		lookAndFeelBox.add(Box.createVerticalStrut(10));

		addSingleItemCentered(RESTORE_DEFAULT_LOOK_AND_FEEL_BUTTON, lookAndFeelBox);
		
		cardLookAndFeel.add(lookAndFeelBox);
	}

	private void setupCardEditor() {
		EditorAction.setEditorToCurrent();

		Box editorBox = Box.createVerticalBox();

		JLabel editorLabel = new JLabel(PANEL_EDITOR);
		addSingleItemCentered(editorLabel, editorBox);
		
		editorBox.add(Box.createVerticalStrut(10));

		addSingleItemCentered(FONT_FACE_COMBOBOX, editorBox);
		addPreferenceItem("Font Size", FONT_SIZE_FIELD, editorBox);

		editorBox.add(Box.createVerticalStrut(5));

		addPreferenceItem("Line Wrap", LINE_WRAP_COMBOBOX, editorBox);

		editorBox.add(Box.createVerticalStrut(5));

		addPreferenceItem("Undo Queue Size", UNDO_QUEUE_SIZE_FIELD, editorBox);

		editorBox.add(Box.createVerticalStrut(10));

		addSingleItemCentered(RESTORE_DEFAULT_EDITOR_BUTTON, editorBox);
		
		cardEditor.add(editorBox);
	}

	private void setupCardOpenAndSave() {
		OpenAndSaveAction.setOpenAndSaveToCurrent();

		Box openAndSaveBox = Box.createVerticalBox();

		JLabel openAndSaveLabel = new JLabel(PANEL_OPEN_AND_SAVE);
		addSingleItemCentered(openAndSaveLabel, openAndSaveBox);
		
		openAndSaveBox.add(Box.createVerticalStrut(10));

		addSingleItemCentered(new JLabel("Default Line Terminator"), openAndSaveBox);
		addSingleItemCentered(LINE_END_COMBOBOX, openAndSaveBox);

		openAndSaveBox.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel("Default Encoding when opening."), openAndSaveBox);
		addSingleItemCentered(OPEN_ENCODING_COMBOBOX, openAndSaveBox);

		openAndSaveBox.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel("Default Encoding when saving."), openAndSaveBox);
		addSingleItemCentered(SAVE_ENCODING_COMBOBOX, openAndSaveBox);

		openAndSaveBox.add(Box.createVerticalStrut(10));

		addSingleItemCentered(RESTORE_DEFAULT_OPEN_AND_SAVE_BUTTON, openAndSaveBox);
		
		cardOpenAndSave.add(openAndSaveBox);
	}

	private void setupCardMisc() {
		MiscAction.setMiscToCurrent();

		Box miscBox = Box.createVerticalBox();

		JLabel miscLabel = new JLabel(PANEL_MISC);
		addSingleItemCentered(miscLabel, miscBox);
		
		miscBox.add(Box.createVerticalStrut(10));

		addPreferenceItem("Recent Files List Size", RECENT_FILES_LIST_SIZE_FIELD, miscBox);
		addPreferenceItem("Print Environemnt", PRINT_ENVIRONMENT_CHECKBOX, miscBox);
		addPreferenceItem("New Document On Startup", NEW_DOC_ON_STARTUP_CHECKBOX, miscBox);

		miscBox.add(Box.createVerticalStrut(10));

		addSingleItemCentered(RESTORE_DEFAULT_MISC_BUTTON, miscBox);
		
		cardMisc.add(miscBox);
	}
	
	private void addPreferenceItem(String text, JComponent field, Container container) {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel(text));
		box.add(Box.createRigidArea(new Dimension(3,1)));
		field.setMaximumSize(field.getPreferredSize());
		box.add(field);
		container.add(box);
	}

	private void addSingleItemCentered(JComponent component, Container container) {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		component.setMaximumSize(component.getPreferredSize());
		box.add(component);
		box.add(Box.createHorizontalGlue());
		container.add(box);
	}
}

public class TextFieldListener implements FocusListener {
	private JTextField field = null;
	private Preference pref = null;
	
	public TextFieldListener(JTextField field, Preference pref) {
		this.field = field;
		this.pref = pref;
	}
	
	// FocusListener Interface
	public void focusGained(FocusEvent e) {
		handleUpdate();
	}
	
	public void focusLost(FocusEvent e) {
		handleUpdate();
	}
	
	private void handleUpdate() {
		if (pref instanceof PreferenceInt) {
			PreferenceInt prefInt = (PreferenceInt) pref;
			prefInt.setTmp(field.getText());
			field.setText(String.valueOf(prefInt.tmp));
		}	
	}
}

public class ComboBoxListener implements ItemListener {
	private JComboBox box = null;
	private Preference pref = null;
	
	public ComboBoxListener(JComboBox box, Preference pref) {
		this.box = box;
		this.pref = pref;
	}
	
	// ItemListener Interface
	public void itemStateChanged(ItemEvent e) {
		handleUpdate();
	}
		
	private void handleUpdate() {
		if (pref instanceof PreferenceString) {
			PreferenceString prefString = (PreferenceString) pref;
			prefString.setTmp((String) box.getSelectedItem());
			box.setSelectedItem(prefString.tmp);
		}	
	}
}

public class CheckboxListener implements ActionListener {
	private JCheckBox checkbox = null;
	private Preference pref = null;
	
	public CheckboxListener(JCheckBox checkbox, Preference pref) {
		this.checkbox = checkbox;
		this.pref = pref;
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		handleUpdate();
	}
	
	private void handleUpdate() {
		if (pref instanceof PreferenceBoolean) {
			PreferenceBoolean prefBoolean = (PreferenceBoolean) pref;
			prefBoolean.setTmp(checkbox.isSelected());
			checkbox.setSelected(prefBoolean.tmp);
		}	
	}
}

public class LookAndFeelAction implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(PreferencesFrame.RESTORE_DEFAULTS)) {
			try {
				PreferencesFrame.DESKTOP_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.DESKTOP_BACKGROUND_COLOR.def);
				Preferences.DESKTOP_BACKGROUND_COLOR.restoreTemporaryToDefault();

				PreferencesFrame.PANEL_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.PANEL_BACKGROUND_COLOR.def);
				Preferences.PANEL_BACKGROUND_COLOR.restoreTemporaryToDefault();

				PreferencesFrame.TEXTAREA_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.TEXTAREA_BACKGROUND_COLOR.def);
				Preferences.TEXTAREA_BACKGROUND_COLOR.restoreTemporaryToDefault();

				PreferencesFrame.TEXTAREA_FOREGROUND_COLOR_BUTTON.setBackground(Preferences.TEXTAREA_FOREGROUND_COLOR.def);
				Preferences.TEXTAREA_FOREGROUND_COLOR.restoreTemporaryToDefault();

				PreferencesFrame.SELECTED_CHILD_COLOR_BUTTON.setBackground(Preferences.SELECTED_CHILD_COLOR.def);
				Preferences.SELECTED_CHILD_COLOR.restoreTemporaryToDefault();

				PreferencesFrame.INDENT_FIELD.setText(String.valueOf(Preferences.INDENT.def));
				Preferences.INDENT.restoreTemporaryToDefault();
				
				PreferencesFrame.VERTICAL_SPACING_FIELD.setText(String.valueOf(Preferences.VERTICAL_SPACING.def));
				Preferences.VERTICAL_SPACING.restoreTemporaryToDefault();
				
				PreferencesFrame.LEFT_MARGIN_FIELD.setText(String.valueOf(Preferences.LEFT_MARGIN.def));
				Preferences.LEFT_MARGIN.restoreTemporaryToDefault();
				
				PreferencesFrame.RIGHT_MARGIN_FIELD.setText(String.valueOf(Preferences.RIGHT_MARGIN.def));
				Preferences.RIGHT_MARGIN.restoreTemporaryToDefault();
				
				PreferencesFrame.TOP_MARGIN_FIELD.setText(String.valueOf(Preferences.TOP_MARGIN.def));
				Preferences.TOP_MARGIN.restoreTemporaryToDefault();
				
				PreferencesFrame.BOTTOM_MARGIN_FIELD.setText(String.valueOf(Preferences.BOTTOM_MARGIN.def));
				Preferences.BOTTOM_MARGIN.restoreTemporaryToDefault();
			} catch (Exception ex) {
				System.out.println("Exception: " + ex);
			}
		} else if (e.getActionCommand().equals(PreferencesFrame.DESKTOP_COLOR)) {
			Color newColor = JColorChooser.showDialog(Outliner.prefs,"Choose Color",Preferences.DESKTOP_BACKGROUND_COLOR.tmp);
			if (newColor != null) {
				Preferences.DESKTOP_BACKGROUND_COLOR.tmp = newColor;
				PreferencesFrame.DESKTOP_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.DESKTOP_BACKGROUND_COLOR.tmp);
			}
		} else if (e.getActionCommand().equals(PreferencesFrame.BACKGROUND_COLOR)) {
			Color newColor = JColorChooser.showDialog(Outliner.prefs,"Choose Color",Preferences.PANEL_BACKGROUND_COLOR.tmp);
			if (newColor != null) {
				Preferences.PANEL_BACKGROUND_COLOR.tmp = newColor;
				PreferencesFrame.PANEL_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.PANEL_BACKGROUND_COLOR.tmp);
			}
		} else if (e.getActionCommand().equals(PreferencesFrame.FOREGROUND_COLOR)) {
			Color newColor = JColorChooser.showDialog(Outliner.prefs,"Choose Color",Preferences.TEXTAREA_BACKGROUND_COLOR.tmp);
			if (newColor != null) {
				Preferences.TEXTAREA_BACKGROUND_COLOR.tmp = newColor;
				PreferencesFrame.TEXTAREA_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.TEXTAREA_BACKGROUND_COLOR.tmp);
			}
		} else if (e.getActionCommand().equals(PreferencesFrame.TEXT_COLOR)) {
			Color newColor = JColorChooser.showDialog(Outliner.prefs,"Choose Color",Preferences.TEXTAREA_FOREGROUND_COLOR.tmp);
			if (newColor != null) {
				Preferences.TEXTAREA_FOREGROUND_COLOR.tmp = newColor;
				PreferencesFrame.TEXTAREA_FOREGROUND_COLOR_BUTTON.setBackground(Preferences.TEXTAREA_FOREGROUND_COLOR.tmp);
			}
		} else if (e.getActionCommand().equals(PreferencesFrame.SELECTED_CHILDREN_COLOR)) {
			Color newColor = JColorChooser.showDialog(Outliner.prefs,"Choose Color",Preferences.SELECTED_CHILD_COLOR.tmp);
			if (newColor != null) {
				Preferences.SELECTED_CHILD_COLOR.tmp = newColor;
				PreferencesFrame.SELECTED_CHILD_COLOR_BUTTON.setBackground(Preferences.SELECTED_CHILD_COLOR.tmp);
			}
		}
	}
	
	public static void setLookAndFeelToCurrent() {
		PreferencesFrame.DESKTOP_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.DESKTOP_BACKGROUND_COLOR.cur);
		PreferencesFrame.PANEL_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.PANEL_BACKGROUND_COLOR.cur);
		PreferencesFrame.TEXTAREA_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.TEXTAREA_BACKGROUND_COLOR.cur);
		PreferencesFrame.TEXTAREA_FOREGROUND_COLOR_BUTTON.setBackground(Preferences.TEXTAREA_FOREGROUND_COLOR.cur);
		PreferencesFrame.SELECTED_CHILD_COLOR_BUTTON.setBackground(Preferences.SELECTED_CHILD_COLOR.cur);
		
		PreferencesFrame.INDENT_FIELD.setText(String.valueOf(Preferences.INDENT.cur));
		PreferencesFrame.VERTICAL_SPACING_FIELD.setText(String.valueOf(Preferences.VERTICAL_SPACING.cur));
		PreferencesFrame.LEFT_MARGIN_FIELD.setText(String.valueOf(Preferences.LEFT_MARGIN.cur));
		PreferencesFrame.RIGHT_MARGIN_FIELD.setText(String.valueOf(Preferences.RIGHT_MARGIN.cur));
		PreferencesFrame.TOP_MARGIN_FIELD.setText(String.valueOf(Preferences.TOP_MARGIN.cur));
		PreferencesFrame.BOTTOM_MARGIN_FIELD.setText(String.valueOf(Preferences.BOTTOM_MARGIN.cur));
	}
}

public class EditorAction implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(PreferencesFrame.RESTORE_DEFAULTS)) {
			try {
				PreferencesFrame.UNDO_QUEUE_SIZE_FIELD.setText(String.valueOf(Preferences.UNDO_QUEUE_SIZE.def));
				Preferences.UNDO_QUEUE_SIZE.restoreTemporaryToDefault();

				PreferencesFrame.FONT_SIZE_FIELD.setText(String.valueOf(Preferences.FONT_SIZE.def));
				Preferences.FONT_SIZE.restoreTemporaryToDefault();

				PreferencesFrame.FONT_FACE_COMBOBOX.setSelectedItem(Preferences.FONT_FACE.def);
				Preferences.FONT_FACE.restoreTemporaryToDefault();

				PreferencesFrame.LINE_WRAP_COMBOBOX.setSelectedItem(Preferences.LINE_WRAP.def);
				Preferences.LINE_WRAP.restoreTemporaryToDefault();
			} catch (Exception ex) {
				System.out.println("Exception: " + ex);
			}
		}
	}
	
	public static void setEditorToCurrent() {
		PreferencesFrame.UNDO_QUEUE_SIZE_FIELD.setText(String.valueOf(Preferences.UNDO_QUEUE_SIZE.cur));
		PreferencesFrame.FONT_SIZE_FIELD.setText(String.valueOf(Preferences.FONT_SIZE.cur));
		PreferencesFrame.FONT_FACE_COMBOBOX.setSelectedItem(Preferences.FONT_FACE.cur);
		PreferencesFrame.LINE_WRAP_COMBOBOX.setSelectedItem(Preferences.LINE_WRAP.cur);
	}
}

public class OpenAndSaveAction implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(PreferencesFrame.RESTORE_DEFAULTS)) {
			try {
				PreferencesFrame.LINE_END_COMBOBOX.setSelectedItem(Preferences.LINE_END.def);
				Preferences.LINE_END.restoreTemporaryToDefault();

				PreferencesFrame.OPEN_ENCODING_COMBOBOX.setSelectedItem(Preferences.OPEN_ENCODING.def);
				Preferences.OPEN_ENCODING.restoreTemporaryToDefault();

				PreferencesFrame.SAVE_ENCODING_COMBOBOX.setSelectedItem(Preferences.SAVE_ENCODING.def);
				Preferences.SAVE_ENCODING.restoreTemporaryToDefault();
			} catch (Exception ex) {
				System.out.println("Exception: " + ex);
			}
		}
	}
	
	public static void setOpenAndSaveToCurrent() {
		PreferencesFrame.LINE_END_COMBOBOX.setSelectedItem(Preferences.LINE_END.cur);
		PreferencesFrame.OPEN_ENCODING_COMBOBOX.setSelectedItem(Preferences.OPEN_ENCODING.cur);
		PreferencesFrame.SAVE_ENCODING_COMBOBOX.setSelectedItem(Preferences.SAVE_ENCODING.cur);
	}
}

public class MiscAction implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(PreferencesFrame.RESTORE_DEFAULTS)) {
			try {
				PreferencesFrame.RECENT_FILES_LIST_SIZE_FIELD.setText(String.valueOf(Preferences.RECENT_FILES_LIST_SIZE.def));
				Preferences.RECENT_FILES_LIST_SIZE.restoreTemporaryToDefault();
				
				PreferencesFrame.PRINT_ENVIRONMENT_CHECKBOX.setSelected(Preferences.PRINT_ENVIRONMENT.def);
				Preferences.PRINT_ENVIRONMENT.restoreTemporaryToDefault();

				PreferencesFrame.NEW_DOC_ON_STARTUP_CHECKBOX.setSelected(Preferences.NEW_DOC_ON_STARTUP.def);
				Preferences.NEW_DOC_ON_STARTUP.restoreTemporaryToDefault();
			} catch (Exception ex) {
				System.out.println("Exception: " + ex);
			}
		}
	}
	
	public static void setMiscToCurrent() {
		PreferencesFrame.RECENT_FILES_LIST_SIZE_FIELD.setText(String.valueOf(Preferences.RECENT_FILES_LIST_SIZE.cur));
		PreferencesFrame.PRINT_ENVIRONMENT_CHECKBOX.setSelected(Preferences.PRINT_ENVIRONMENT.cur);
		PreferencesFrame.NEW_DOC_ON_STARTUP_CHECKBOX.setSelected(Preferences.NEW_DOC_ON_STARTUP.cur);
	}
}

public class PreferencesFrameWindowMonitor extends WindowAdapter {
	public void windowClosing(WindowEvent e) {
		PreferencesFrame pf = (PreferencesFrame) e.getWindow();
		pf.main_cancel();
	}
}
