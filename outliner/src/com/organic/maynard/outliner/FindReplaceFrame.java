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

public class FindReplaceFrame extends AbstractGUITreeJDialog implements ActionListener, KeyListener {

	// Constants
	private static final int MINIMUM_WIDTH = 350;
	private static final int MINIMUM_HEIGHT = 400;
 	private static final int INITIAL_WIDTH = 350;
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
	private static String REPLACE = null;
	private static String REPLACE_ALL = null;

	private static String START_AT_TOP = null;
	private static String WRAP_ARROUND = null;
	private static String SELECTION_ONLY = null;
	private static String IGNORE_CASE = null;
	private static String INCLUDE_READ_ONLY_NODES = null;
	private static String REGEXP = null;

	// Define Fields and Buttons
	private static JCheckBox CHECKBOX_START_AT_TOP = null;
	private static JCheckBox CHECKBOX_WRAP_AROUND = null;
	private static JCheckBox CHECKBOX_SELECTION_ONLY = null;
	private static JCheckBox CHECKBOX_IGNORE_CASE = null;
	private static JCheckBox CHECKBOX_INCLUDE_READ_ONLY_NODES = null;
	private static JCheckBox CHECKBOX_REGEXP = null;
	
	private static JButton BUTTON_FIND = null;
	private static JButton BUTTON_REPLACE = null;
	private static JButton BUTTON_REPLACE_ALL = null;

	private static JLabel LABEL_FIND = null;
	private static JTextArea TEXTAREA_FIND = null;

	private static JLabel LABEL_REPLACE = null;
	private static JTextArea TEXTAREA_REPLACE = null;
	
	
	// Static Methods
	public static void enableButtons() {
		BUTTON_FIND.setEnabled(true);
		BUTTON_REPLACE.setEnabled(true);
		BUTTON_REPLACE_ALL.setEnabled(true);
	}

	public static void disableButtons() {
		BUTTON_FIND.setEnabled(false);
		BUTTON_REPLACE.setEnabled(false);
		BUTTON_REPLACE_ALL.setEnabled(false);
	}
		
	// The Constructor
	public FindReplaceFrame() {
		super(false, false, false, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);

		FIND = GUITreeLoader.reg.getText("find");
		REPLACE = GUITreeLoader.reg.getText("replace");
		REPLACE_ALL = GUITreeLoader.reg.getText("replace_all");

		START_AT_TOP = GUITreeLoader.reg.getText("start_at_top");
		WRAP_ARROUND = GUITreeLoader.reg.getText("wrap_around");
		SELECTION_ONLY = GUITreeLoader.reg.getText("selection_only");
		IGNORE_CASE = GUITreeLoader.reg.getText("ignore_case");
		INCLUDE_READ_ONLY_NODES = GUITreeLoader.reg.getText("include_read_only_nodes");	
		REGEXP = "Regexp";	

		CHECKBOX_REGEXP = new JCheckBox(REGEXP);
		CHECKBOX_START_AT_TOP = new JCheckBox(START_AT_TOP);
		CHECKBOX_WRAP_AROUND = new JCheckBox(WRAP_ARROUND);
		CHECKBOX_SELECTION_ONLY = new JCheckBox(SELECTION_ONLY);
		CHECKBOX_IGNORE_CASE = new JCheckBox(IGNORE_CASE);
		CHECKBOX_INCLUDE_READ_ONLY_NODES = new JCheckBox(INCLUDE_READ_ONLY_NODES);
		
		BUTTON_FIND = new JButton(FIND);
		BUTTON_REPLACE = new JButton(REPLACE);
		BUTTON_REPLACE_ALL = new JButton(REPLACE_ALL);

		LABEL_FIND = new JLabel(FIND);
		TEXTAREA_FIND = new JTextArea();

		LABEL_REPLACE = new JLabel(REPLACE);
		TEXTAREA_REPLACE = new JTextArea();

		Insets insets = new Insets(1,3,1,3);
		Cursor cursor = new Cursor(Cursor.TEXT_CURSOR);
		
		TEXTAREA_FIND.setName(FIND);
		TEXTAREA_FIND.setCursor(cursor);
		TEXTAREA_FIND.setLineWrap(true);
		TEXTAREA_FIND.setMargin(insets);
	
		TEXTAREA_REPLACE.setName(REPLACE);
		TEXTAREA_REPLACE.setCursor(cursor);
		TEXTAREA_REPLACE.setLineWrap(true);
		TEXTAREA_REPLACE.setMargin(insets);
		
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
		
		// Define the options Box
		Box optionsBox = Box.createHorizontalBox();
			
			// Scope Options
			Box scopeOptionsBox = Box.createVerticalBox();
			scopeOptionsBox.add(CHECKBOX_START_AT_TOP);
			scopeOptionsBox.add(CHECKBOX_WRAP_AROUND);
			scopeOptionsBox.add(CHECKBOX_SELECTION_ONLY);
			scopeOptionsBox.add(CHECKBOX_INCLUDE_READ_ONLY_NODES);
			
			// Match Options
			scopeOptionsBox.add(CHECKBOX_REGEXP);
			scopeOptionsBox.add(CHECKBOX_IGNORE_CASE);
			
			// Define Button Box
			BUTTON_FIND.addActionListener(this);
			BUTTON_REPLACE.addActionListener(this);
			BUTTON_REPLACE_ALL.addActionListener(this);

			Box buttonBox = Box.createVerticalBox();
			buttonBox.add(BUTTON_FIND);
			buttonBox.add(Box.createVerticalStrut(5));
			buttonBox.add(BUTTON_REPLACE);
			buttonBox.add(Box.createVerticalStrut(5));
			buttonBox.add(BUTTON_REPLACE_ALL);
			
			// Put it all together
			optionsBox.add(scopeOptionsBox);
			optionsBox.add(buttonBox);

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
		
		findReplaceBox.add(optionsBox);
		
		getContentPane().add(findReplaceBox, BorderLayout.CENTER);		
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
		} else if (e.getActionCommand().equals(REPLACE)) {
			replace(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(REPLACE_ALL)) {
			replace_all(Outliner.getMostRecentDocumentTouched());
		}
	}
	
	private void find(OutlinerDocument doc) {
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
	}

	private void replace(OutlinerDocument doc) {
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
	}

	private void replace_all(OutlinerDocument doc) {
		replaceAll(
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
		
		if (location != null) {
			// Shorthand
			TreeContext tree = doc.tree;

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
		
		if (location != null) {
			// Shorthand
			TreeContext tree = doc.tree;

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

	public void replaceAll(
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
				for (int i = 0; i < doc.tree.selectedNodes.size(); i++) {					
					Node nodeStart = (Node) doc.tree.selectedNodes.get(i);
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
			Node nodeStart = doc.tree.rootNode.getFirstChild();
			int cursorStart = 0;
			Node nodeEnd = doc.tree.rootNode.getLastDecendent();
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
		
		if (count == 0) {return;}

		// Bring the window to the front
		try {
			Outliner.outliner.requestFocus();
			doc.setSelected(true);
		} catch (java.beans.PropertyVetoException pve) {
			pve.printStackTrace();
		}
		
		// Redraw and Set Focus
		doc.panel.layout.draw(doc.tree.getEditingNode(),doc.tree.getComponentFocus());
		
		// Popup a dialogue with the replacement count.
		String replacementText = GUITreeLoader.reg.getText("replacements");
		if (count == 1) {
			replacementText = GUITreeLoader.reg.getText("replacement");
		}
		String msg = GUITreeLoader.reg.getText("replacements_made");
		msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, "" + count);
		msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_2, replacementText);

		JOptionPane.showMessageDialog(doc, msg);
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
				for (int i = 0; i < doc.tree.selectedNodes.size(); i++) {
					// Record the Insert in the undoable
					Node nodeStart = (Node) doc.tree.selectedNodes.get(i);
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
				nodeStart = doc.tree.rootNode.getFirstChild();
				nodeEnd = doc.tree.rootNode.getLastDecendent();
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
		
		// We rean out of places to look.
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
			String regex = null;
			if (ignoreCase) {
				regex = REGEX_MATCH_START + match + REGEX_MATCH_END_IGNORE_CASE;
			} else {
				regex = REGEX_MATCH_START + match + REGEX_MATCH_END;
			}
							
			if (isReplace) {
				// Prepare the replacement regex
				String subRegex = null;
				if (ignoreCase) {
					subRegex = REGEX_REPLACE_START + match + REGEX_REPLACE_MIDDLE + replacement + REGEX_REPLACE_END_IGNORE_CASE;
				} else {
					subRegex = REGEX_REPLACE_START + match + REGEX_REPLACE_MIDDLE + replacement + REGEX_REPLACE_END;
				}
								
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