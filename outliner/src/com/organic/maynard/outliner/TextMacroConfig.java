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
import com.organic.maynard.util.string.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class TextMacroConfig extends MacroConfigImpl implements KeyListener {
	
	private static String PATTERN = null;
	
	private JLabel nameLabel = null;
	private JLabel patternLabel = null;
	
	private JTextField nameField = new JTextField();
	private JTextArea patternTextArea = new JTextArea();


	// The Constructor
	public TextMacroConfig() {
		super();

		PATTERN = GUITreeLoader.reg.getText("pattern");
	
		nameLabel = new JLabel(NAME);
		patternLabel = new JLabel(PATTERN);
		
		// Create the layout
		this.setLayout(new BorderLayout());

		Box mainBox = Box.createVerticalBox();
		mainBox.add(nameLabel);
		nameField.setMargin(new Insets(1,3,1,3));
		mainBox.add(nameField);
		mainBox.add(Box.createVerticalStrut(10));
		mainBox.add(patternLabel);

		// Prep the textarea
		patternTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		patternTextArea.setLineWrap(true);
		patternTextArea.setMargin(new Insets(1,3,1,3));
		patternTextArea.addKeyListener(this);
		
		JScrollPane patternScrollPane = new JScrollPane(patternTextArea);
		
		// Put it all together
		this.add(mainBox,BorderLayout.NORTH);
		this.add(patternScrollPane,BorderLayout.CENTER);
	}

	
	// MacroConfig Interface
	public void init(Macro macro) {
		super.init(macro);
		
		TextMacro textMacro = (TextMacro) getMacro();
		
		patternTextArea.setText(textMacro.getReplacementPattern());
		nameField.setText(textMacro.getName());
	}
	
	public boolean create() {
		TextMacro textMacro = (TextMacro) getMacro();
		
		String name = nameField.getText();

		if (MacroPopupMenu.validateExistence(name) && MacroPopupMenu.validateUniqueness(name) && MacroPopupMenu.validateRestrictedChars(name)) {
			textMacro.setName(name);
			textMacro.setReplacementPattern(patternTextArea.getText());
			return true;
		} else {
			return false;
		}
	}
	
	public boolean update() {
		TextMacro textMacro = (TextMacro) getMacro();
		
		String name = nameField.getText();

		if (MacroPopupMenu.validateExistence(name)) {
			if (name.equals(textMacro.getName())) {
				textMacro.setReplacementPattern(patternTextArea.getText());
				return true;
			} else if (MacroPopupMenu.validateUniqueness(name) && MacroPopupMenu.validateRestrictedChars(name)) {
				textMacro.setName(name);
				textMacro.setReplacementPattern(patternTextArea.getText());
				return true;
			}
		}
		return false;
	}

	
	// KeyListener Interface
	public void keyPressed(KeyEvent e) {
		if ((e.getKeyChar() == KeyEvent.VK_ENTER) || (e.getKeyChar() == KeyEvent.VK_TAB)) {
			e.consume();
			return;
		}
	}

	public void keyTyped(KeyEvent e) {}
	
	public void keyReleased(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_V) && e.isControlDown()) {
			String text = patternTextArea.getText();
			text = Replace.replace(text,"\t","");
			text = Replace.replace(text,"\n","");
			patternTextArea.setText(text);
		}
	}
}
