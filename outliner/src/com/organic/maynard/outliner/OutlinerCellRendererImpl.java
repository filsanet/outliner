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

public class OutlinerCellRendererImpl extends JTextArea implements OutlinerCellRenderer {

	private static Font font = null;
	private static Font readOnlyFont = null;
	private static Font immoveableFont = null;
	private static Font immoveableReadOnlyFont = null;
	
	private static Cursor cursor = new Cursor(Cursor.TEXT_CURSOR);
	private static Insets marginInsets = new Insets(1,3,1,3);
	
	public static int textAreaWidth = 0;
	
	public Node node = null;
	public OutlineButton button = new OutlineButton(this);
	public OutlineCommentIndicator iComment = new OutlineCommentIndicator(this);
	public OutlineEditableIndicator iEditable = new OutlineEditableIndicator(this);
	public OutlineMoveableIndicator iMoveable = new OutlineMoveableIndicator(this);
	public OutlineLineNumber lineNumber = new OutlineLineNumber(this);
	
	public int height = 0;
	
	static {
		updateFonts();
	}
	
	// The Constructors
	public OutlinerCellRendererImpl() {
		super();
				
		setFont(font);
		setCursor(cursor);
		setCaretColor(Preferences.getPreferenceColor(Preferences.SELECTED_CHILD_COLOR).cur);
		setMargin(marginInsets);
		setSelectionColor(Preferences.getPreferenceColor(Preferences.TEXTAREA_FOREGROUND_COLOR).cur);
		setSelectedTextColor(Preferences.getPreferenceColor(Preferences.TEXTAREA_BACKGROUND_COLOR).cur);
		setLineWrap(true);

		if (Preferences.getPreferenceString(Preferences.LINE_WRAP).cur.equals(Preferences.TXT_CHARACTERS)) {
			setWrapStyleWord(false);
		} else {
			setWrapStyleWord(true);
		}
		
		setVisible(false);
	}

	public void destroy() {
		removeAll();
		removeNotify();
		
		node = null;
		
		button = null;
		lineNumber = null;
	}
		
	public boolean isManagingFocus() {
		return true;
	}

	public static void updateFonts() {
		font = new Font(Preferences.getPreferenceString(Preferences.FONT_FACE).cur, Font.PLAIN, Preferences.getPreferenceInt(Preferences.FONT_SIZE).cur);
		readOnlyFont = new Font(Preferences.getPreferenceString(Preferences.FONT_FACE).cur, Font.ITALIC, Preferences.getPreferenceInt(Preferences.FONT_SIZE).cur);
		immoveableFont = new Font(Preferences.getPreferenceString(Preferences.FONT_FACE).cur, Font.BOLD, Preferences.getPreferenceInt(Preferences.FONT_SIZE).cur);
		immoveableReadOnlyFont = new Font(Preferences.getPreferenceString(Preferences.FONT_FACE).cur, Font.BOLD + Font.ITALIC, Preferences.getPreferenceInt(Preferences.FONT_SIZE).cur);
	}

	// Used to fire key events
	public void fireKeyEvent(KeyEvent event) {
		processKeyEvent(event);
	}


	// OutlinerCellRenderer Interface
	public void setVisible(boolean visibility) {
		super.setVisible(visibility);
		button.setVisible(visibility);
		iComment.setVisible(visibility);
		iEditable.setVisible(visibility);
		iMoveable.setVisible(visibility);
		lineNumber.setVisible(visibility);
	}
	
	public void verticalShift (int amount) {
		Point lp = lineNumber.getLocation();
		lp.y += amount;
		lineNumber.setLocation(lp);
		
		Point tp = getLocation();
		tp.y += amount;
		setLocation(tp);

		Point bp = button.getLocation();
		bp.y += amount;
		button.setLocation(bp);

		Point icp = iComment.getLocation();
		icp.y += amount;
		iComment.setLocation(icp);

		Point iep = iEditable.getLocation();
		iep.y += amount;
		iEditable.setLocation(iep);

		Point imp = iMoveable.getLocation();
		imp.y += amount;
		iMoveable.setLocation(imp);
	}
	
	public void drawUp(Point p, Node node) {
		this.node = node;
		
		// Adjust color when we are selected
		updateColors();
		
		// Update the button
		updateButton();

		// Update the Indicators
		updateCommentIndicator();
		updateEditableIndicator();
		updateMoveableIndicator();
		
		// Update font
		updateFont();
		
		// Draw the TextArea
		setText(node.getValue());
		
		int indent = node.getDepth() * Preferences.getPreferenceInt(Preferences.INDENT).cur;
		int width = textAreaWidth - indent;
		
		// Size needs to be set twice. The first time forces the lines to flow. The second then sets the correct height.
		setSize(width,32);
		height = getBestHeight();
		p.y -= (height + Preferences.getPreferenceInt(Preferences.VERTICAL_SPACING).cur);
		setBounds(p.x + indent + OutlineButton.BUTTON_WIDTH, p.y, width, height);
		
		// Draw the Button
		button.setBounds(p.x + indent, p.y, OutlineButton.BUTTON_WIDTH, height);
		
		// Draw the LineNumber
		if (Preferences.getPreferenceBoolean(Preferences.SHOW_LINE_NUMBERS).cur) {
			if (node.getTree().doc.hoistStack.isHoisted()) {
				// TODO: This value should be pre-calculated.
				int offset = node.getTree().doc.hoistStack.getLineCountOffset()  + node.getLineNumber(node.getTree().getLineCountKey());
				lineNumber.setText("" + offset);
			} else {
				lineNumber.setText("" + node.getLineNumber(node.getTree().getLineCountKey()));
			}
		} else {
			lineNumber.setText("");
		}
		lineNumber.setBounds(
			Preferences.getPreferenceInt(Preferences.LEFT_MARGIN).cur + OutlineCommentIndicator.BUTTON_WIDTH + OutlineEditableIndicator.BUTTON_WIDTH + OutlineMoveableIndicator.BUTTON_WIDTH, 
			p.y, 
			OutlineLineNumber.LINE_NUMBER_WIDTH + indent, 
			height
		);

		// Draw Indicators
		iComment.setBounds(
			Preferences.getPreferenceInt(Preferences.LEFT_MARGIN).cur + OutlineEditableIndicator.BUTTON_WIDTH + OutlineMoveableIndicator.BUTTON_WIDTH, 
			p.y, 
			OutlineCommentIndicator.BUTTON_WIDTH, 
			height
		);

		iEditable.setBounds(
			Preferences.getPreferenceInt(Preferences.LEFT_MARGIN).cur + OutlineMoveableIndicator.BUTTON_WIDTH, 
			p.y, 
			OutlineEditableIndicator.BUTTON_WIDTH, 
			height
		);

		iMoveable.setBounds(
			Preferences.getPreferenceInt(Preferences.LEFT_MARGIN).cur, 
			p.y, 
			OutlineMoveableIndicator.BUTTON_WIDTH, 
			height
		);
	}
		
	public void drawDown(Point p, Node node) {
		this.node = node;

		// Adjust color when we are selected
		updateColors();
				
		// Update the button
		updateButton();
		
		// Update the Indicators
		updateCommentIndicator();
		updateEditableIndicator();
		updateMoveableIndicator();

		// Update font
		updateFont();
				
		// Draw the TextArea
		setText(node.getValue());
		
		int indent = node.getDepth() * Preferences.getPreferenceInt(Preferences.INDENT).cur;
		int width = textAreaWidth - indent;
		
		// Size needs to be set twice. The first time forces the lines to flow. The second then sets the correct height.
		setSize(width,32);
		height = getBestHeight();
		setBounds(p.x + indent + OutlineButton.BUTTON_WIDTH, p.y, width, height);
		
		// Draw the Button
		button.setBounds(p.x + indent, p.y, OutlineButton.BUTTON_WIDTH, height);

		// Draw the LineNumber
		if (Preferences.getPreferenceBoolean(Preferences.SHOW_LINE_NUMBERS).cur) {
			if (node.getTree().doc.hoistStack.isHoisted()) {
				// TODO: This value should be pre-calculated.
				int offset = node.getTree().doc.hoistStack.getLineCountOffset()  + node.getLineNumber(node.getTree().getLineCountKey());
				lineNumber.setText("" + offset);
			} else {
				lineNumber.setText("" + node.getLineNumber(node.getTree().getLineCountKey()));
			}
		} else {
			lineNumber.setText("");
		}
		lineNumber.setBounds(
			Preferences.getPreferenceInt(Preferences.LEFT_MARGIN).cur + OutlineCommentIndicator.BUTTON_WIDTH + OutlineEditableIndicator.BUTTON_WIDTH + OutlineMoveableIndicator.BUTTON_WIDTH, 
			p.y, 
			OutlineLineNumber.LINE_NUMBER_WIDTH + indent, 
			height
		);


		// Draw Indicators
		iComment.setBounds(
			Preferences.getPreferenceInt(Preferences.LEFT_MARGIN).cur + OutlineEditableIndicator.BUTTON_WIDTH + OutlineMoveableIndicator.BUTTON_WIDTH, 
			p.y, 
			OutlineCommentIndicator.BUTTON_WIDTH, 
			height
		);

		iEditable.setBounds(
			Preferences.getPreferenceInt(Preferences.LEFT_MARGIN).cur + OutlineMoveableIndicator.BUTTON_WIDTH, 
			p.y, 
			OutlineEditableIndicator.BUTTON_WIDTH, 
			height
		);

		iMoveable.setBounds(
			Preferences.getPreferenceInt(Preferences.LEFT_MARGIN).cur, 
			p.y, 
			OutlineMoveableIndicator.BUTTON_WIDTH, 
			height
		);
						
		p.y += height + Preferences.getPreferenceInt(Preferences.VERTICAL_SPACING).cur;	

	}
	
	private void updateFont() {
		if (node.isEditable()) {
			setEditable(true);
		
			if (node.isMoveable()) {
				setFont(font);
			} else {
				setFont(immoveableFont);
			}
		} else {
			setEditable(false);
			
			if (node.isMoveable()) {
				setFont(readOnlyFont);
			} else {
				setFont(immoveableReadOnlyFont);
			}
		}	
	}
	
	private void updateColors() {
		if (node.isAncestorSelected()) {
			if (node.isComment()) {
				setForeground(Preferences.getPreferenceColor(Preferences.TEXTAREA_COMMENT_COLOR).cur);				
			} else {
				setForeground(Preferences.getPreferenceColor(Preferences.TEXTAREA_BACKGROUND_COLOR).cur);
			}
			
			lineNumber.setForeground(Preferences.getPreferenceColor(Preferences.SELECTED_CHILD_COLOR).cur);

			if (node.isSelected()) {
				setBackground(Preferences.getPreferenceColor(Preferences.TEXTAREA_FOREGROUND_COLOR).cur);
				lineNumber.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_COLOR).cur);
				button.setBackground(Preferences.getPreferenceColor(Preferences.TEXTAREA_FOREGROUND_COLOR).cur);
				iComment.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_COLOR).cur);
				iEditable.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_COLOR).cur);
				iMoveable.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_COLOR).cur);
				
			} else {
				setBackground(Preferences.getPreferenceColor(Preferences.SELECTED_CHILD_COLOR).cur);
				lineNumber.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_CHILD_COLOR).cur);
				button.setBackground(Preferences.getPreferenceColor(Preferences.SELECTED_CHILD_COLOR).cur);
				iComment.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_CHILD_COLOR).cur);
				iEditable.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_CHILD_COLOR).cur);
				iMoveable.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_CHILD_COLOR).cur);
			}
			
		} else {
			if (node.isComment()) {
				setForeground(Preferences.getPreferenceColor(Preferences.TEXTAREA_COMMENT_COLOR).cur);				
			} else {
				setForeground(Preferences.getPreferenceColor(Preferences.TEXTAREA_FOREGROUND_COLOR).cur);
			}
			
			setBackground(Preferences.getPreferenceColor(Preferences.TEXTAREA_BACKGROUND_COLOR).cur);
			lineNumber.setForeground(Preferences.getPreferenceColor(Preferences.TEXTAREA_FOREGROUND_COLOR).cur);
			lineNumber.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_COLOR).cur);
			button.setBackground(Preferences.getPreferenceColor(Preferences.TEXTAREA_BACKGROUND_COLOR).cur);
			iComment.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_COLOR).cur);
			iEditable.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_COLOR).cur);
			iMoveable.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_COLOR).cur);
		}	
	}
	
	private void updateButton() {
		if (node.isAncestorSelected()) {
			button.setSelected(true);
		} else {
			button.setSelected(false);
		}
		
		if (node.isLeaf()) {
			button.setNode(false);
		} else {
			button.setNode(true);
			if (node.isExpanded()) {
				button.setOpen(true);
			} else {
				button.setOpen(false);
			}
		}
		
		button.updateIcon();
	}
	
	private void updateCommentIndicator() {
		if (node.getCommentState() == Node.COMMENT_TRUE) {
			iComment.setPropertyInherited(false);
			iComment.setProperty(true);
			
		} else if (node.getCommentState() == Node.COMMENT_FALSE) {
			iComment.setPropertyInherited(false);
			iComment.setProperty(false);
		
		} else {
			iComment.setPropertyInherited(true);
			iComment.setProperty(node.isComment());
		}
		
		iComment.updateIcon();
	}
	
	private void updateEditableIndicator() {
		if (node.getEditableState() == Node.EDITABLE_TRUE) {
			iEditable.setPropertyInherited(false);
			iEditable.setProperty(true);
			
		} else if (node.getEditableState() == Node.EDITABLE_FALSE) {
			iEditable.setPropertyInherited(false);
			iEditable.setProperty(false);
		
		} else {
			iEditable.setPropertyInherited(true);
			iEditable.setProperty(node.isEditable());
		}
		
		iEditable.updateIcon();
	}

	private void updateMoveableIndicator() {
		if (node.getMoveableState() == Node.MOVEABLE_TRUE) {
			iMoveable.setPropertyInherited(false);
			iMoveable.setProperty(true);
			
		} else if (node.getMoveableState() == Node.MOVEABLE_FALSE) {
			iMoveable.setPropertyInherited(false);
			iMoveable.setProperty(false);
		
		} else {
			iMoveable.setPropertyInherited(true);
			iMoveable.setProperty(node.isMoveable());
		}
		
		iMoveable.updateIcon();
	}
	
	public int getBestHeight() { // This could be optimized so that the only comparison is the textarea, and everything else is already worked out.
		return Math.max(Math.max(Math.max(getPreferredSize().height, OutlineButton.BUTTON_HEIGHT), OutlineLineNumber.LINE_NUMBER_HEIGHT),  OutlineCommentIndicator.BUTTON_HEIGHT);
	}
}