/**
 * Copyright (C) 2002 Maynard Demmon, maynard@organic.com
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
 
package com.organic.maynard.outliner.util.spelling;

import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.util.undo.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import org.xml.sax.*;
import java.util.*;

import com.swabunga.spell.event.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class SpellingCheckerDialog extends AbstractOutlinerJDialog implements ActionListener, JoeXMLConstants {
	
	// Constants
	private static final int MINIMUM_WIDTH = 425;
	private static final int MINIMUM_HEIGHT = 200;
 	private static final int INITIAL_WIDTH = 425;
	private static final int INITIAL_HEIGHT = 200;
	
	
	// Instance Fields
	private int word_index = 0; // The index of the current word we're checking.
	private SpellingCheckerWrapper spellChecker = null;
	
	
	// Main Component Containers
	private JPanel PANEL_BUTTONS = null;
	private JPanel PANEL_MAIN = null;
	private JScrollPane JSP = null;
	
	// Button Text and Other Copy
	public static String DONE = null;
	public static String SKIP = null;
	public static String SKIP_ALL = null;
	public static String REPLACE = null;
	public static String REPLACE_ALL = null;
	
	// Define Fields and Buttons
	public JTextPane TEXT = null;
	public JComboBox SUGGESTIONS = null;
	public JLabel WORD = null;
	public JLabel STATUS = null;
	public JButton BUTTON_DONE = null;
	public JButton BUTTON_SKIP = null;
	public JButton BUTTON_SKIP_ALL = null;
	public JButton BUTTON_REPLACE = null;
	public JButton BUTTON_REPLACE_ALL = null;
	
	
	// The Constructor
	public SpellingCheckerDialog(SpellingCheckerWrapper spellChecker) {
		super(false, true, true, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
		
		this.spellChecker = spellChecker;
		
		// Button Text and Other Copy
		DONE = GUITreeLoader.reg.getText("done");
		SKIP = GUITreeLoader.reg.getText("skip");
		SKIP_ALL = GUITreeLoader.reg.getText("skip_all");
		REPLACE = GUITreeLoader.reg.getText("replace");
		REPLACE_ALL = GUITreeLoader.reg.getText("replace_all");
		
		// Define Fields and Buttons
		this.STATUS = new JLabel();
		
		this.TEXT = new JTextPane();
		StyledDocument doc = TEXT.getStyledDocument();
		Style style = TEXT.addStyle("Red", null);
		StyleConstants.setForeground(style, Color.red);
		StyleConstants.setBold(style, true);
		TEXT.setEditable(false);
		TEXT.setEnabled(false);
		
		this.JSP = new JScrollPane(TEXT);
		this.WORD = new JLabel("Misspelt Word: ");
		
		this.SUGGESTIONS = new JComboBox();
		this.SUGGESTIONS.setEditable(true);
		
		this.BUTTON_DONE = new JButton(DONE);
		this.BUTTON_DONE.addActionListener(this);
		
		this.BUTTON_SKIP = new JButton(SKIP);
		this.BUTTON_SKIP.addActionListener(this);
		
		this.BUTTON_SKIP_ALL = new JButton(SKIP_ALL);
		this.BUTTON_SKIP_ALL.addActionListener(this);
		
		this.BUTTON_REPLACE = new JButton(REPLACE);
		this.BUTTON_REPLACE.addActionListener(this);
		
		this.BUTTON_REPLACE_ALL = new JButton(REPLACE_ALL);
		this.BUTTON_REPLACE_ALL.addActionListener(this);
		
		getRootPane().setDefaultButton(BUTTON_REPLACE);
		
		// Add Components
		this.PANEL_MAIN = new JPanel();
		this.PANEL_MAIN.setLayout(new BorderLayout());
		this.PANEL_MAIN.add(this.WORD, BorderLayout.NORTH);
		this.PANEL_MAIN.add(this.JSP, BorderLayout.CENTER);
		this.PANEL_MAIN.add(this.SUGGESTIONS, BorderLayout.SOUTH);
		
		this.PANEL_BUTTONS = new JPanel();
		this.PANEL_BUTTONS.setLayout(new FlowLayout());
		this.PANEL_BUTTONS.add(this.BUTTON_DONE);
		this.PANEL_BUTTONS.add(this.BUTTON_SKIP);
		this.PANEL_BUTTONS.add(this.BUTTON_SKIP_ALL);
		this.PANEL_BUTTONS.add(this.BUTTON_REPLACE);
		this.PANEL_BUTTONS.add(this.BUTTON_REPLACE_ALL);
		
		getContentPane().add(STATUS, BorderLayout.NORTH);
		getContentPane().add(PANEL_MAIN, BorderLayout.CENTER);
		getContentPane().add(PANEL_BUTTONS, BorderLayout.SOUTH);
		
		pack();
	}
	
	private boolean stop_thread = false;
	public boolean shouldStop() {
		return this.stop_thread;
	}
	
	public void stop() {
		this.stop_thread = true;
	}
	
	private CompoundUndoableEdit undoable = null;
	
	public void reset() {
		this.stop_thread = false;
		this.word_index = 0;
		this.skip_list = new HashMap();
		this.replace_list = new HashMap();
		this.spellChecker.reset();
		
		OutlinerDocument doc = (OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched();
		undoable = new CompoundUndoableEdit(doc.tree);
		
		updateGUI();
	}
	
	public void checkSpellingForDocument() {
		reset();
		Thread t = new Thread(new Runnable() {
			public void run() {
				OutlinerDocument doc = (OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched();
				Outliner.spellChecker.checkSpellingForDocument(doc);
				Outliner.spellChecker.getDialog().stop();
				Outliner.spellChecker.getDialog().updateButtons();
			}
		});
		t.start();
		show();
	}
	
	public void checkSpellingForSelection() {
		reset();
		Thread t = new Thread(new Runnable() {
			public void run() {
				OutlinerDocument doc = (OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched();
				Outliner.spellChecker.checkSpellingForSelection(doc);
				Outliner.spellChecker.getDialog().stop();
				Outliner.spellChecker.getDialog().updateButtons();
			}
		});
		t.start();
		show();
	}
	
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		// File Menu
		if (e.getActionCommand().equals(DONE)) {
			done();
		} else if (e.getActionCommand().equals(SKIP)) {
			skip();
		} else if (e.getActionCommand().equals(SKIP_ALL)) {
			skip_all();
		} else if (e.getActionCommand().equals(REPLACE)) {
			replace();
		} else if (e.getActionCommand().equals(REPLACE_ALL)) {
			replace_all();
		}
	}
	
	private void done() {
		this.stop();
		
		OutlinerDocument doc = (OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched();
		if (!undoable.isEmpty()) {
			doc.getUndoQueue().add(undoable);
		}
		doc.panel.layout.redraw();
		hide();
	}
	
	private void skip() {
		this.word_index++;
		
		Node node = this.spellChecker.getMisspeltWordNode(word_index);
		updateDifference(node, 0);
		
		boolean success = updateGUI();
		if (!success) {
			this.word_index--;
			if (!this.stop_thread) {
				updateGUI();
			}
		} else {
			SpellCheckEvent event = this.spellChecker.getMisspeltWord(word_index);
			
			String word = event.getInvalidWord();
			if (skip_list.get(word) != null) {
				skip();
			}
			if (replace_list.get(word) != null) {
				replace();
			}
		}
	}
	
	private HashMap skip_list = null;
	
	private void skip_all() {
		SpellCheckEvent event = this.spellChecker.getMisspeltWord(word_index);
		String word = event.getInvalidWord();
		skip_list.put(word,word);
		
		skip();
	}
	
	public void replace() {
		SpellCheckEvent event = this.spellChecker.getMisspeltWord(word_index);
		String word = event.getInvalidWord();
		
		String replacement = (String) replace_list.get(word);
		if (replacement == null) {
			replacement = SUGGESTIONS.getSelectedItem().toString();
		}
		
		Node node = this.spellChecker.getMisspeltWordNode(word_index);
		
		int difference = replacement.length() - word.length();
		updateDifference(node, difference);
		
		int offset = this.spellChecker.getMisspeltWordOffset(word_index) + event.getWordContextPosition() + current_offset_adj - difference;
		
		String old_value = node.getValue();
		String new_value = old_value.substring(0,offset) + replacement + old_value.substring(offset + word.length(),old_value.length());
		
		node.setValue(new_value);
		
		undoable.addPrimitive(new PrimitiveUndoableEdit(node, old_value, new_value));
		
		//System.out.println("REPLACEMENT: " + replacement);
		
		skip();
	}
	
	private Node current_node = null;
	private int current_offset_adj = 0;
	private void updateDifference(Node node, int difference) {
		if (current_node == null) {
			current_node = node;
			current_offset_adj = difference;
		} else if (current_node == node) {
			current_offset_adj += difference;
		} else if (current_node != node) {
			current_node = node;
			current_offset_adj = difference;
		}
	}
	
	private HashMap replace_list = null;
	
	public void replace_all() {
		SpellCheckEvent event = this.spellChecker.getMisspeltWord(word_index);
		String word = event.getInvalidWord();
		String replacement = SUGGESTIONS.getSelectedItem().toString();
		replace_list.put(word,replacement);
		
		replace();
	}
	
	protected boolean updateGUI() {
		SpellCheckEvent event = this.spellChecker.getMisspeltWord(word_index);
		updateButtons();
		if (event == null) {
			// Update Word Display
			WORD.setText("Misspelt Word: ");
			
			// Update Combobox
			SUGGESTIONS.removeAllItems();
			
			// Update TextArea
			TEXT.setText("");
			
			return false;
		} else {
			// Update Word Display
			String word = event.getInvalidWord();
			WORD.setText("Misspelt Word: " + word);
			
			// Update Combobox
			SUGGESTIONS.removeAllItems();
			
			java.util.List suggestions = event.getSuggestions();
			if (suggestions.size() > 0) {
				for (Iterator suggestedWord = suggestions.iterator(); suggestedWord.hasNext();) {
					SUGGESTIONS.addItem(suggestedWord.next());
				}
			}
			
			// Update TextArea
			Node node = this.spellChecker.getMisspeltWordNode(word_index);
			int offset = this.spellChecker.getMisspeltWordOffset(word_index);
			TEXT.setText(node.getValue());
			//TEXT.setText(event.getWordContext());
			//int position = event.getWordContextPosition();
			int position = offset + event.getWordContextPosition() + current_offset_adj;
			
			StyledDocument doc = TEXT.getStyledDocument();
			doc.setCharacterAttributes(position, word.length(), TEXT.getStyle("Red"), true);
			
			return true;
		}
	}
	
	protected void updateButtons() {
		int current_word_count = spellChecker.getCurrentWordCount();
		StringBuffer text = new StringBuffer();
		if (this.stop_thread) {
			text.append("Done finding misspelled words. ");
		} else {
			text.append("Spellcheck in progress. ");
		}
		
		if (word_index >= current_word_count) {
			STATUS.setText(text.append("Done with spellcheck.").toString());
			BUTTON_SKIP.setEnabled(false);
			BUTTON_SKIP_ALL.setEnabled(false);
			BUTTON_REPLACE.setEnabled(false);
			BUTTON_REPLACE_ALL.setEnabled(false);
		} else {
			STATUS.setText(text.append(word_index + 1).append(" of ").append(current_word_count).append(".").toString());
			BUTTON_SKIP.setEnabled(true);
			BUTTON_SKIP_ALL.setEnabled(true);
			BUTTON_REPLACE.setEnabled(true);
			BUTTON_REPLACE_ALL.setEnabled(true);
		}
	}
}