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

import java.util.*;

public class AttributeContainerImpl implements AttributeContainer {

	// Instance Variables
	private HashMap attributes = null;


	// The Constructors
	public AttributeContainerImpl() {}


	public void setAttribute(String key, Object value) {
		if (attributes == null) {
			attributes = new HashMap();
		}
		attributes.put(key, value);
	}

	public void removeAttribute(String key) {
		if (attributes != null && key != null) {
			attributes.remove(key);
		}
	}
	
	public void clearAttributes() {
		if (attributes != null) {
			attributes.clear();
		}	
	}
	
	public Object getAttribute(String key) {
		if (attributes != null && key != null) {
			return attributes.get(key);
		}
		return null;
	}
	
	public int getAttributeCount() {
		if (attributes != null) {
			return attributes.size();
		}
		return 0;
	}
	
	public Iterator getAttributeKeys() {
		if (attributes != null) {
			return attributes.keySet().iterator();
		}
		return null;
	}
}