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

public interface Undoable {
	public void destroy();
	
	public static final int EDIT_TYPE = 0;

	public static final int PRIMITIVE_DELETE_TYPE = 1;
	public static final int COMPOUND_DELETE_TYPE = 2;

	public static final int PRIMITIVE_INSERT_TYPE = 3;
	public static final int COMPOUND_INSERT_TYPE = 4;

	public static final int PRIMITIVE_MOVE_TYPE = 5;
	public static final int COMPOUND_MOVE_TYPE = 6;

	public static final int PRIMITIVE_EDIT_TYPE = 7;
	public static final int COMPOUND_EDIT_TYPE = 8;

	public static final int PRIMITIVE_REPLACE_TYPE = 9;
	public static final int COMPOUND_REPLACE_TYPE = 10;

	public void undo();
	public void redo();
	public int getType();
}