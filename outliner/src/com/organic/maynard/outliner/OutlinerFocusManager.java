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
import java.util.*;
import javax.swing.*;
import javax.swing.text.Caret;

import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

// This class allows re-routing of keyEvents back to the correct OutlinerCellRendererImpl.
// It is possible when a draw is occuring that changes the draw direction that keyEvents will
// be sent to the old renderer before the focus manager has a chance to catch up. This class
// intercepts any miss-targetd key events and sends them off to the correct renderer, the one
// attached to the current editing node. 
public class OutlinerFocusManager extends DefaultFocusManager {

	public void processKeyEvent(Component c, KeyEvent e) {
		try {
			if (c instanceof OutlinerCellRendererImpl) {
				OutlinerCellRendererImpl renderer = (OutlinerCellRendererImpl) c;
				TreeContext tree = renderer.node.getTree();
				if (renderer.node != tree.getEditingNode()) {
					tree.doc.panel.layout.getUIComponent(tree.getEditingNode()).fireKeyEvent(e);
					e.consume();
					return;
				}
			}
		} catch (NullPointerException npe) {
			// Document may have been destroyed in the interim, so let's abort.
			return;
		}
		super.processKeyEvent(c,e);
	}
}