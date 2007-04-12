/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Interfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license.pdf. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.terminal.web;

/** 
 * Exception in the transform process.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class UIDLTransformerException extends java.lang.Exception {

	private static final long serialVersionUID = 5648982356058143223L;
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
	 * @param msg the description of exception that occurred.
	 * @param te the Transform exception that occurred.
	 * @param desc the detailed description.
	 */
	public UIDLTransformerException(String msg, Throwable te, String desc) {
		super(msg);
		this.transformException = te;
		this.HTMLDescription = desc;
	}
	
	/** 
	 * Returns the detailed description.
	 * @return the Detailed description of exception.
	 */
	public String getHTMLDescription() {
		return HTMLDescription;
	}

	/** 
	 * Returns the nested transform exception that occurred.
	 * @return the transform exception
	 */
	public Throwable getTransformException() {
		return transformException;
	}

}
