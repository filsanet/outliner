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

import org.xml.sax.*;

public class IntRangeValidator extends AbstractValidator implements Validator, GUITreeComponent {

	// Constants
	public static final String A_MIN = "min";
	public static final String A_MAX = "max";
	public static final String A_DEFAULT = "default";
	
	private int lowerBound = 0;
	private int upperBound = 0;
	private int defaultValue = 0;
	
	private boolean returnNearestValue = true;
	
	// Constructors
	public IntRangeValidator() {
	
	}
	
	public IntRangeValidator(int lowerBound, int upperBound, int defaultValue) {
		this(lowerBound,upperBound,defaultValue,true);
	}
	
	public IntRangeValidator(int lowerBound, int upperBound, int defaultValue, boolean returnNearestValue) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.defaultValue = defaultValue;
		this.returnNearestValue = returnNearestValue;
	}


	// GUITreeComponent Interface
	public void startSetup(AttributeList atts) {
		String min = atts.getValue(A_MIN);
		String max = atts.getValue(A_MAX);
		String def = atts.getValue(A_DEFAULT);
		
		try {
			this.lowerBound = Integer.parseInt(min);
			this.upperBound = Integer.parseInt(max);
			this.defaultValue = Integer.parseInt(def);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		super.startSetup(atts);
	}


	// Validator Interface
	public Object getValidValue(Object val) {
		if (val instanceof String) {
			return getValidValue((String) val);
		} else if (val instanceof Integer) {
			return getValidValue((Integer) val);
		} else {
			return null;
		}
	}
	
	private Integer getValidValue(String value) {
		Integer retVal = new Integer(defaultValue);
		try {
			retVal = getValidValue(new Integer(value));
		} catch (NumberFormatException nfe) {
			if (!returnNearestValue) {return null;}
			retVal = new Integer(defaultValue);
		}
		return retVal;
	}

	private Integer getValidValue(Integer value) {
		if (value.intValue() < lowerBound) {
			if (!returnNearestValue) {return null;}
			value = new Integer(lowerBound);
		} else if (value.intValue() > upperBound) {
			if (!returnNearestValue) {return null;}
			value = new Integer(upperBound);
		}
		return value;
	}
	
	
	// Additional Accessors
	public int getMin() {return this.lowerBound;}
	public int getMax() {return this.upperBound;}
}