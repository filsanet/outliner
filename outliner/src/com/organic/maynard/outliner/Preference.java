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

public interface Preference {

	public void restoreCurrentToDefault();
	public void restoreTemporaryToDefault();
	public void restoreTemporaryToCurrent();
	public void applyTemporaryToCurrent();
	
	public String getCommand();
	public void setCommand(String command);
	
	public void setValidator(Validator v);
	public Validator getValidator();
	
	public void setCur(String s);
	public void setDef(String s);
	public void setTmp(String s);
	
	public String getCur();
	public String getDef();
	public String getTmp();

}