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

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.xml.sax.*;

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
