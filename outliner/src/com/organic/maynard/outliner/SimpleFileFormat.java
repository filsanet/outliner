/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the
 * following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *
 *  - Neither the names "Java Outline Editor", "JOE" nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.organic.maynard.outliner;

import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.util.preferences.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import com.organic.maynard.util.string.Replace;

public class SimpleFileFormat

 implements SaveFileFormat, OpenFileFormat, JoeReturnCodes {

 // Constructors
 public SimpleFileFormat() {}


 // SaveFileFormat Interface
 public byte[] save(JoeTree tree, DocumentInfo docInfo) {
  StringBuffer buf = new StringBuffer();
  tree.getRootNode().depthPaddedValue(buf, PlatformCompatibility.platformToLineEnding(docInfo.getLineEnding()));

  try {
   return buf.toString().getBytes(docInfo.getEncodingType());
  } catch (UnsupportedEncodingException e) {
   e.printStackTrace();
   return buf.toString().getBytes();
  }
 }

 public boolean supportsComments() {return false;}
 public boolean supportsEditability() {return false;}
 public boolean supportsMoveability() {return false;}
 public boolean supportsAttributes() {return false;}
 public boolean supportsDocumentAttributes() {return false;}


 // OpenFileFormat Interface
 public int open(JoeTree tree, DocumentInfo docInfo, InputStream stream) {
  int success = FAILURE;

  String text = null;


  try {
   InputStreamReader inputStreamReader = new InputStreamReader(stream, docInfo.getEncodingType());
   BufferedReader buf = new BufferedReader(inputStreamReader);

   StringBuffer sb = new StringBuffer();
   String s;
   while((s = buf.readLine()) != null) {
    sb.append(s);
    sb.append(Preferences.LINE_END_STRING);
   }

   text = sb.toString();
  } catch(IOException e) {
   e.printStackTrace();
  }

  if (text != null) {
   Node newNode = new NodeImpl(tree,"");
   int padSuccess = PadSelection.pad(text, tree, 0,PlatformCompatibility.LINE_END_UNIX, newNode);

   switch (padSuccess) {

    case PadSelection.SUCCESS:
     tree.setRootNode(newNode);
     success = SUCCESS;
     break;

    case PadSelection.SUCCESS_MODIFIED:
     String yes = GUITreeLoader.reg.getText("yes");
     String no = GUITreeLoader.reg.getText("no");
     String confirm_open = GUITreeLoader.reg.getText("confirm_open");
     String msg = GUITreeLoader.reg.getText("confirmation_inconsistent_hierarchy");
     msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, docInfo.getPath());


     Object[] options = {yes, no};
     int result = JOptionPane.showOptionDialog(Outliner.outliner,
      msg,
      confirm_open,
      JOptionPane.YES_NO_OPTION,
      JOptionPane.QUESTION_MESSAGE,
      null,
      options,
      options[0]
     );

     if (result == JOptionPane.YES_OPTION) {
      success = SUCCESS_MODIFIED;
      tree.setRootNode(newNode);
      break;
     } else if (result == JOptionPane.NO_OPTION) {
      success = FAILURE_USER_ABORTED;
      break;
     }

    case PadSelection.FAILURE:
     success = FAILURE;
     break;
   }
  } else {
   success = FAILURE;
  }

  return success;
 }

 // File Extensions
 private HashMap extensions = new HashMap();

 public void addExtension(String ext, boolean isDefault) {
  extensions.put(ext, new Boolean(isDefault));
 }

 public void removeExtension(String ext) {
  extensions.remove(ext);
 }

 public String getDefaultExtension() {
  Iterator i = getExtensions();
  while (i.hasNext()) {
   String key = (String) i.next();
   Boolean value = (Boolean) extensions.get(key);

   if (value.booleanValue()) {
    return key;
   }
  }

  return null;
 }

 public Iterator getExtensions() {
  return extensions.keySet().iterator();
 }

 public boolean extensionExists(String ext) {
  Iterator it = getExtensions();
  while (it.hasNext()) {
   String key = (String) it.next();
   if (ext.equals(key)) {
    return true;
   }
  }

  return false;
 }
}
