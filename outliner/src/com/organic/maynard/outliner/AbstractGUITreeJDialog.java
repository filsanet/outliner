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
import org.xml.sax.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public abstract class AbstractGUITreeJDialog extends JDialog implements GUITreeComponent, JoeXMLConstants {
	
	// Fields
	private WindowSizeManager windowSizeManager = null;
		
	private boolean alwaysCenter = true;
	private boolean hasBeenShown = false;
	
	// The Constructors
	public AbstractGUITreeJDialog(boolean resizeOnShow, boolean alwaysCenter, boolean modal, int initialWidth, int initialHeight, int minimumWidth, int minimumHeight) {
		super(Outliner.outliner, "", modal);
		
		this.alwaysCenter = alwaysCenter;

		setSize(initialWidth, initialHeight);
		addComponentListener(new WindowSizeManager(resizeOnShow, initialWidth, initialHeight, minimumWidth, minimumHeight));
	}

	// GUITreeComponent interface
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}

	public void startSetup(AttributeList atts) {
		setTitle(atts.getValue(A_TITLE));
		setVisible(false);
	}
	
	public void endSetup(AttributeList atts) {}
	
	public void show() {
		if (alwaysCenter || !hasBeenShown) {
			hasBeenShown = true;
			
			Rectangle r = Outliner.outliner.getBounds();
			setLocation((int) (r.getCenterX() - getWidth()/2), (int) (r.getCenterY() - getHeight()/2));
		}
		
		super.show();
	}
}
