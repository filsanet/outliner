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

import com.organic.maynard.util.string.*;

public class DocumentStatistics extends JDialog {
	
	// GUI Components
	private static JLabel documentTitleName = new JLabel("Document: ");
	private static JLabel documentTitleValue = new JLabel("");

	private static JLabel lineCountName = new JLabel("Lines: ");
	private static JLabel lineCountValue = new JLabel("");

	private static JLabel charCountName = new JLabel("Characters: ");
	private static JLabel charCountValue = new JLabel("");
	
	
	// The Constructors
	public DocumentStatistics() {
		super(Outliner.outliner,"Document Statistics",true);
		
		// Create the Layout
		setSize(200,125);
		setResizable(true);
		
		Box vBox = Box.createVerticalBox();

		Box documentTitleBox = Box.createHorizontalBox();
		documentTitleBox.add(documentTitleName);
		documentTitleBox.add(documentTitleValue);
		vBox.add(documentTitleBox);

		vBox.add(Box.createVerticalStrut(5));

		Box lineCountBox = Box.createHorizontalBox();
		lineCountBox.add(lineCountName);
		lineCountBox.add(lineCountValue);
		vBox.add(lineCountBox);

		vBox.add(Box.createVerticalStrut(5));

		Box charCountBox = Box.createHorizontalBox();
		charCountBox.add(charCountName);
		charCountBox.add(charCountValue);
		vBox.add(charCountBox);
		
		getContentPane().add(vBox,BorderLayout.CENTER);
	}
	
	public void show() {
		OutlinerDocument doc = Outliner.getMostRecentDocumentTouched();
		documentTitleValue.setText(doc.getTitle());
		
		int lineCount = doc.tree.getLineCount();
		lineCountValue.setText("" + lineCount);
		
		int charCount = doc.tree.getCharCount();
		charCountValue.setText("" + charCount);

		Rectangle r = Outliner.outliner.getBounds();
		setLocation((int) (r.getCenterX() - getWidth()/2), (int) (r.getCenterY() - getHeight()/2));
		
		super.show();		
	}
}
