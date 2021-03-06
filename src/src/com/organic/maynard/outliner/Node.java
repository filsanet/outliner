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

public interface Node {

	public Node cloneClean();

	// Parent Methods
	public void setParent(Node node);
	public Node getParent();
	
	// Child Methods
	public int numOfChildren();
	public void appendChild(Node node);
	public void removeChild(Node node);
	public Node getChild(int i);
	public Node getFirstChild();
	public Node getLastChild();
	public Node getLastDecendent();
	public Node getLastViewableDecendent();
	public Node getYoungestVisibleAncestor();
	public int insertChildrenIntoVisibleNodesCache(TreeContext tree, int index);
	public void removeFromVisibleNodesCache(TreeContext tree);
	public void insertChild(Node node, int i);
	
	public int getChildIndex(Node node);
	
	public boolean isLeaf();
	public boolean isRoot();
	public boolean isDecendantOf(Node node);
	
	// Tree Accessor Methods
	public TreeContext getTree();
	
	// Visibility Methods
	public void setVisible(boolean visible);
	public boolean isVisible();
	public void setPartiallyVisible(boolean visible);
	public boolean isPartiallyVisible();

	// Selection Methods
	public void setSelected(boolean selected);
	public boolean isSelected();
	public boolean isAncestorSelected();

	// Depth Methods
	public void setDepth(int depth);
	public int getDepth();
	
	public void setDepthRecursively(int depth);
	
	// Navigation Methods
	public void setExpandedClean(boolean expanded);
	public void setExpanded(boolean expanded);
	public boolean isExpanded();
	public void ExpandAllSubheads();
	public void CollapseAllSubheads();
	public void expandAllAncestors();

	public int currentIndex();
	
	public boolean isFirstChild();
	public boolean isLastChild();
	
	public Node nextSibling();
	public Node prevSibling();
	public Node next();
	public Node nextUnSelectedNode();
	public Node nextNode();
	public Node prev();
	
	// Data Methods
	public void setValue(String str);
	public String getValue();

	// String Representation Methods
	public String depthPaddedValue();
	public String depthPaddedValue(String lineEndString);
}