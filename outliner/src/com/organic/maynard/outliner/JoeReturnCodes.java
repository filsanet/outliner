/**
 * JoeReturnCodes interface
 * 
 * Provides a set of return code constants for JOE methods
 * 
 *		
 * Copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 * Last Touched: 8/15/01 12:07PM
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

// we're part of this
package com.organic.maynard.outliner;

// Provide a set of return code constants for JOE programming
public interface JoeReturnCodes {
	
	// standard classics
	public static final int SUCCESS = 1 ;
	public static final int FAILURE = 0 ;
	
	// set success codes > 1, error/failure codes < 0

	// try to allocate additions with thoughts of future expansion and general elbow room
	// try to group similars
	

	// success codes
	
	// 1xx success flavors
	public static final int SUCCESS_MODIFIED = 100;		// from OpenFileFormat
	

	// failure codes
	
	// -1xx boundary errors
	public static final int ARRAY_SELECTOR_OUT_OF_BOUNDS = -100 ;
	
	// -2xx document errors
	public static final int DOCUMENT_NOT_FOUND = -200 ;
	public static final int DOCUMENT_IN_USE_ELSEWHERE = -210 ;
	
	// -3xx memory errors
	public static final int UNABLE_TO_ALLOCATE_MEMORY = -300 ;
	
	// -4xx printer errors
	public static final int PRINTER_COMMUNICATION_FAILURE = -400 ;
	
	// -5xx arithmetic errors
	public static final int ATTEMPT_TO_DIVIDE_BY_ZERO = -500 ;
	
	// -6xx user actions
	public static final int USER_ABORTED = -600 ;
	public static final int FAILURE_USER_ABORTED = -600;  	// from OpenFileFormat
	
	// -7xx internet errors
	public static final int URL_NOT_FOUND = -700 ;

	// -8xx nullness errors
	public static final int NULL_OBJECT_REFERENCE = -800 ;
	
	} // end interface JoeReturnCodes