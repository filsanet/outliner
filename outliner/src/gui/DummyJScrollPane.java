/**
 * JMouseWheel: Mouse wheel support for Java applications on Win32 platforms
 * Copyright (C) 2001 Maynard Demmon, maynard@organic.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see http://www.gnu.org
 */
 
package gui;

import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import java.awt.Component;

/**
 * DummyJScrollPane enables the use of mouse wheel support with
 * JScrollBars that are not attached to a JScrollPane.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
 
public class DummyJScrollPane extends JScrollPane {
	private JScrollBar scrollbar = null;
	
	/**
	 * Creates a DummyJScrollPane.
	 *
	 * @param view      the <code>Component</code> that the <code>JScrollPane</code>
	 *                  will create a view for. The view will then become the active
	 *                  mouse wheel region for the scrollbar provided in the next
	 *                  parameter.
	 * @param scrollbar the scrollbar we want mouse wheel support for.
	 */
	public DummyJScrollPane(Component view, JScrollBar scrollbar) {
		super(view, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.scrollbar = scrollbar;
	}
	
	/**
	 * gets the configured <code>JScrollBar</code> or if <code>null</code> gets the
	 * <code>JScrollBar</code> from the <code>JScrollPane</code> superclass.
	 *
	 * @return the configured <code>JScrollBar</code> or, if <code>null</code>, the
	 *         <code>JScrollBar</code> from the superclass.
	 */
	public JScrollBar getVerticalScrollBarProxy() {
		try {
			return this.scrollbar;
		} catch (NullPointerException npe) {
			return super.getVerticalScrollBar();
		}
	}
}