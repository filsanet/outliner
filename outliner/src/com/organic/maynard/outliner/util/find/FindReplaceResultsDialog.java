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

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class FindReplaceResultsDialog extends AbstractOutlinerJDialog implements MouseListener {

	// Constants
	private static final int MINIMUM_WIDTH = 400;
	private static final int MINIMUM_HEIGHT = 150;
 	private static final int INITIAL_WIDTH = 400;
	private static final int INITIAL_HEIGHT = 150;
	
	private static final String TOTAL_MATCHES = "Total Matches: ";
	
	
	// GUI
	private JTable table = new JTable();
	private JLabel totalMatches = new JLabel(TOTAL_MATCHES + "0");

	
	// Model
	private FindReplaceResultsModel model = null;

		
	// The Constructor
	public FindReplaceResultsDialog() {
		super(false, false, false, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
		
		table.addMouseListener(this);
		JScrollPane jsp = new JScrollPane(table);
		getContentPane().add(jsp, BorderLayout.CENTER);
		getContentPane().add(totalMatches, BorderLayout.SOUTH);

		setTitle("Find/Replace All Results");
		
		//pack();

		setVisible(false);
	}
	
	public FindReplaceResultsModel getModel() {return this.model;}
	
	public void show(FindReplaceResultsModel model) {
		this.model = model;
		model.setView(this);
		
		// Setup the JTable
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		updateTotalMatches();
		
		show();
		
		SwingUtilities.invokeLater(new Runnable(){public void run(){Outliner.outliner.requestFocus();}});
	}
	
	public void updateTotalMatches() {
		totalMatches.setText(TOTAL_MATCHES + model.size());
	}
	
	public void requestFocus() {}

	// MouseListener Interface
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {
		int row = table.rowAtPoint(e.getPoint());
		
		FindReplaceResult result = model.getResult(row);
		
		int type = result.getType();
		if (type == FindReplaceResult.TYPE_DOC) {
			handleDocumentClick(result);
		} else if (type == FindReplaceResult.TYPE_FILE) {
			handleFileClick(result);
		} else {
			System.out.println("ERROR: Unknown FindReplaceResult type.");
		}	
	}
	
	private void handleDocumentClick(FindReplaceResult result) {
		OutlinerDocument doc = result.getDocument();
		int line = result.getLine();
		int start = result.getStart();
		int end = start + result.getMatch().length();
		
		Outliner.outliner.requestFocus();
		Node node = GoToDialog.goToLineAndColumn(doc, line, start, false, true);
		doc.panel.layout.draw(node, OutlineLayoutManager.TEXT);
		WindowMenu.changeToWindow(doc);
	}
	
	private void handleFileClick(FindReplaceResult result) {
	
	}

}