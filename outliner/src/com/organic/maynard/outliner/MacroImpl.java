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
import javax.swing.*;
import java.io.*;

public abstract class MacroImpl implements Macro {
	
	// Instance Fields		
	private String name = null;
	private boolean undoable = true;
	private int undoableType = Macro.NOT_UNDOABLE;
	private MacroConfig configurator = null;
	
	// The Constructors	
	public MacroImpl(String name) {
		this.name = name;
	}

	public String getName() {return this.name;}
	public void setName(String name) {this.name = name;}
	
	public boolean isUndoable() {return undoable;}
	protected void setUndoable(boolean undoable) {this.undoable = undoable;}

	public int getUndoableType() {return undoableType;}
	protected void setUndoableType(int undoableType) {this.undoableType = undoableType;}
	
	public MacroConfig getConfigurator() {return this.configurator;}
	public void setConfigurator(MacroConfig configurator) {this.configurator = configurator;}

	public NodeRangePair process(NodeRangePair nodeRangePair) {
		return nodeRangePair;
	}
}