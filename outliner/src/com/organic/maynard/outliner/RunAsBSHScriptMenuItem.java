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
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
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
