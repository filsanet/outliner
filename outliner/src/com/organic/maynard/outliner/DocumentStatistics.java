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
import javax.swing.*;
import org.xml.sax.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class DocumentStatistics extends AbstractGUITreeJDialog {
	
	// Constants
	private static final int INITIAL_WIDTH = 200;
	private static final int INITIAL_HEIGHT = 125;
	private static final int MINIMUM_WIDTH = 200;
	private static final int MINIMUM_HEIGHT = 125;
	
	
	// GUI Components
	private static JLabel documentTitleName = null;
	private static JLabel documentTitleValue = new JLabel("");

	private static JLabel lineCountName = null;
	private static JLabel lineCountValue = new JLabel("");

	private static JLabel charCountName = null;
	private static JLabel charCountValue = new JLabel("");
	
	
	// The Constructors
	public DocumentStatistics() {
		super(true, true, true, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);

		documentTitleName = new JLabel(GUITreeLoader.reg.getText("document") + " ");
		lineCountName = new JLabel(GUITreeLoader.reg.getText("lines") + " ");
		charCountName = new JLabel(GUITreeLoader.reg.getText("characters") + " ");
		
		// Create the Layout
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


	// GUITreeComponent Interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		Outliner.statistics = this;
	}
	
	public void show() {
		OutlinerDocument doc = Outliner.getMostRecentDocumentTouched();
		documentTitleValue.setText(doc.getTitle());
		
		int lineCount = doc.tree.getLineCount();
		lineCountValue.setText("" + lineCount);
		
		int charCount = doc.tree.getCharCount();
		charCountValue.setText("" + charCount);
		
		super.show();		
	}
}
