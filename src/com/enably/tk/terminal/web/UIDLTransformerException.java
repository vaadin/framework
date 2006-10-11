/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2005 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   license version 2.1 as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */
   
package com.enably.tk.terminal.web;

/** Exception in transform process.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class UIDLTransformerException extends java.lang.Exception {

	private String HTMLDescription = null;
	private Throwable transformException = null;
	
	/**
	 * Creates a new instance of UIDLTransformerException without detail
	 * message.
	 */
	public UIDLTransformerException() {
	}

	/**
	 * Constructs an instance of UIDLTransformerException with the specified
	 * detail message.
	 * @param msg description of exception that occurred
	 * @param te Transform exception that occurred.
	 * @param desc the detailed description.
	 */
	public UIDLTransformerException(String msg, Throwable te, String desc) {
		super(msg);
		this.transformException = te;
		this.HTMLDescription = desc;
	}
	/** Returns the detailed description.
	 * @return Detailed description of exception.
	 */
	public String getHTMLDescription() {
		return HTMLDescription;
	}

	/** Returns the nested transform exception that occurred.
	 * @return Throwable
	 */
	public Throwable getTransformException() {
		return transformException;
	}

}
