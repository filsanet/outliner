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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.xml.sax.*;

import bsh.*;
import bsh.util.*;

public class BSHConsole extends AbstractGUITreeJDialog {

	// Constants
	private static final int INITIAL_WIDTH = 350;
	private static final int INITIAL_HEIGHT = 300;
 	private static final int MINIMUM_WIDTH = 350;
	private static final int MINIMUM_HEIGHT = 300;

	// The Constructors
	public BSHConsole() {
		super(false, false, false, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
	}

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		JConsole console = new JConsole();
		Interpreter interpreter = new Interpreter(console);

		Thread thread = new Thread(interpreter);
		thread.start();
					
		getContentPane().add(console, BorderLayout.CENTER);
	}
}