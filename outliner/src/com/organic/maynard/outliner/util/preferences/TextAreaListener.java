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
 
package com.organic.maynard.outliner.util.preferences;

import com.organic.maynard.outliner.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.Window;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.xml.sax.*;

import com.organic.maynard.data.StringList;

public class TextAreaListener implements FocusListener {

	// Instance Fields
	private JTextArea list = null;
	private Preference pref = null;
	
	
	// Constructors
	public TextAreaListener(JTextArea list, Preference pref) {
		setList(list);
		setPreference(pref);
	}
	
	
	// Accessors
	public void setPreference(Preference pref) {
		this.pref = pref;
	}

	public void setList(JTextArea list) {
		this.list = list;
	}

	
	// FocusListener Interface
	public void focusGained(FocusEvent e) {
		handleUpdate();
	}
	
	public void focusLost(FocusEvent e) {
		handleUpdate();
	}


	private void handleUpdate() {
		// We can simplify this when we move more methods into the Preference Interface.
		if (pref instanceof PreferenceStringList) {
			// Update pref
			PreferenceStringList prefStringList = (PreferenceStringList) pref;
			
			String text = list.getText();
			
			StringList stringList = new StringList();
			StringTokenizer tokenizer = new StringTokenizer(text,"\n");
			while (tokenizer.hasMoreTokens()) {
				stringList.add(tokenizer.nextToken());
			}
			prefStringList.tmp = stringList;
			
			// Update list
			list.setText(text);
		}
	}
}

