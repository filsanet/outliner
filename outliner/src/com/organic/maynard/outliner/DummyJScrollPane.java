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
import java.awt.event.*;

public class DummyJScrollPane extends JScrollPane {
	private OutlinerPanel panel = null;
	
	public DummyJScrollPane(OutlinerPanel panel) {
		super(panel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.panel = panel;
	}

	public JScrollBar getVerticalScrollBarProxy() {
		try {
			return panel.layout.scrollBar;
		} catch (NullPointerException npe) {
			return super.getVerticalScrollBar();
		}
	}
}
