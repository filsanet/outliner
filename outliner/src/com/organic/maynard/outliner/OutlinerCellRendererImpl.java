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

	private static Font font = new Font(Preferences.FONT_FACE.cur,Font.PLAIN,Preferences.FONT_SIZE.cur);	
	private static Cursor cursor = new Cursor(Cursor.TEXT_CURSOR);
	private static Insets marginInsets = new Insets(1,3,1,3);
	
	public static int textAreaWidth = 0;
	
	public Node node = null;
	public OutlineButton button = new OutlineButton(this);
	
	
	// The Constructors
	public OutlinerCellRendererImpl() {
		super();

		setFont(font);
		setCursor(cursor);
		setMargin(marginInsets);
		setSelectionColor(Preferences.TEXTAREA_FOREGROUND_COLOR.cur);
		setSelectedTextColor(Preferences.TEXTAREA_BACKGROUND_COLOR.cur);
		setLineWrap(true);

		if (Preferences.LINE_WRAP.cur.equals(Preferences.TXT_CHARACTERS)) {
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
		currentTextAreaSize = null;
		
		button = null;
	}
		
	public boolean isManagingFocus() {
		return true;
	}


	// Dimension Methods
	private Dimension currentTextAreaSize = new Dimension(1,1); // Random unlikely value so it isn't null.

	public void setCurrentTextAreaSize(Dimension d) {
		this.currentTextAreaSize = d;
	}
	
	public Dimension getCurrentTextAreaSize() {
		return currentTextAreaSize;
	}


	// Used to fire key events
	public void fireKeyEvent(KeyEvent event) {
		processKeyEvent(event);
	}


	// OutlinerCellRenderer Interface
	public void setVisible(boolean visibility) {
		super.setVisible(visibility);
		button.setVisible(visibility);
	}
	
	public void verticalShift (int amount) {
		Point tp = getLocation();
		tp.y += amount;
		setLocation(tp);

		Point bp = button.getLocation();
		bp.y += amount;
		button.setLocation(bp);
	}
	
	public void drawUp(Point p, Node node) {
		this.node = node;
		
		// Adjust color when we are selected
		updateColors();
		
		// Update the button
		updateButton();
		
		// Draw the TextArea
		setText(node.getValue());
		
		int indent = node.getDepth() * Preferences.INDENT.cur;
		int width = textAreaWidth - indent;
		
		// Size needs to be set twice. The first time forces the lines to flow. The second then sets the correct height.
		setSize(width,32);
		int height = getBestHeight();
		p.y -= (height + Preferences.VERTICAL_SPACING.cur);
		setBounds(p.x + indent + OutlineButton.BUTTON_WIDTH, p.y, width, height);
		
		// Draw the Button
		button.setBounds(p.x + indent, p.y, OutlineButton.BUTTON_WIDTH, height);
	}
		
	public void drawDown(Point p, Node node) {
		this.node = node;
		
		// Update the button
		updateButton();
		
		// Draw the TextArea
		setText(node.getValue());
		
		int indent = node.getDepth() * Preferences.INDENT.cur;
		int width = textAreaWidth - indent;
		
		// Size needs to be set twice. The first time forces the lines to flow. The second then sets the correct height.
		setSize(width,32);
		int height = getBestHeight();
		setBounds(p.x + indent + OutlineButton.BUTTON_WIDTH, p.y, width, height);
		
		// Draw the Button
		button.setBounds(p.x + indent, p.y, OutlineButton.BUTTON_WIDTH, height);
		
		p.y += height + Preferences.VERTICAL_SPACING.cur;	
		
		// Adjust color when we are selected
		updateColors();
	}
	
	private void updateColors() {
		if (node.isAncestorSelected()) {
			if (node.isSelected()) {
				setForeground(Preferences.TEXTAREA_BACKGROUND_COLOR.cur);
				setBackground(Preferences.TEXTAREA_FOREGROUND_COLOR.cur);
				button.setBackground(Preferences.TEXTAREA_FOREGROUND_COLOR.cur);
			} else {
				setForeground(Preferences.TEXTAREA_BACKGROUND_COLOR.cur);
				setBackground(Preferences.SELECTED_CHILD_COLOR.cur);
				button.setBackground(Preferences.SELECTED_CHILD_COLOR.cur);
			}
		} else {
			setForeground(Preferences.TEXTAREA_FOREGROUND_COLOR.cur);
			setBackground(Preferences.TEXTAREA_BACKGROUND_COLOR.cur);
			button.setBackground(Preferences.TEXTAREA_BACKGROUND_COLOR.cur);	
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
	
	private int getBestHeight() {
		return Math.max(getPreferredSize().height, OutlineButton.BUTTON_HEIGHT);
	}
}