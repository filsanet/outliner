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

public class IntRangeValidator {
	
	private int lowerBound = 0;
	private int upperBound = 0;
	private int defaultValue = 0;
	
	private boolean returnNearestValue = true;
	
	// Constructors
	public IntRangeValidator(int lowerBound, int upperBound, int defaultValue) {
		this(lowerBound,upperBound,defaultValue,true);
	}
	
	public IntRangeValidator(int lowerBound, int upperBound, int defaultValue, boolean returnNearestValue) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.defaultValue = defaultValue;
		this.returnNearestValue = returnNearestValue;
	}

	public Integer getValidValue(String value) {
		Integer retVal = new Integer(defaultValue);
		try {
			retVal = getValidValue(Integer.parseInt(value));
		} catch (NumberFormatException nfe) {
			if (!returnNearestValue) {return null;}
			retVal = new Integer(defaultValue);
		}
		return retVal;
	}

	public Integer getValidValue(int value) {
		Integer retVal = new Integer(value);
		if (retVal.intValue() < lowerBound) {
			if (!returnNearestValue) {return null;}
			retVal = new Integer(lowerBound);
		} else if (retVal.intValue() > upperBound) {
			if (!returnNearestValue) {return null;}
			retVal = new Integer(upperBound);
		}
		return retVal;
	}

}