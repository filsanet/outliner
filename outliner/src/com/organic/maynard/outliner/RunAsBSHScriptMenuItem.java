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

import bsh.Interpreter;
import bsh.NameSpace;
import javax.swing.*;
import java.awt.event.*;
import org.xml.sax.*;

public class RunAsBSHScriptMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);

		setEnabled(false);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		MacroPopupMenu.startWaitCursor();
		runAsScript();
		MacroPopupMenu.endWaitCursor();
	}
	
	private void runAsScript() {
		// Get the script from the document.
		OutlinerDocument doc = Outliner.getMostRecentDocumentTouched();
		
		if (doc == null) {
			return;
		}
		
		StringBuffer textBuffer = new StringBuffer();
		doc.tree.getRootNode().getRecursiveValue(textBuffer, Preferences.LINE_END_STRING, false);
		String script = textBuffer.toString();
		
		// Abort if script is empty
		if (script.equals("")) {
			return;
		}
		
		try {
			Interpreter bsh = new Interpreter();
			NameSpace nameSpace = new NameSpace("outliner");
			nameSpace.importPackage("com.organic.maynard.outliner");
			bsh.setNameSpace(nameSpace);
			bsh.eval(script);
		} catch (Exception ex) {
			System.out.println("BSH Exception: " + ex.getMessage());
			JOptionPane.showMessageDialog(doc, "BSH Exception: " + ex.getMessage());
			return;
		}	
	}
}
