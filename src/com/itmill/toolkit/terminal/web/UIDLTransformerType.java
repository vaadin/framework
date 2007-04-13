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
 * Type of the transformer.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class UIDLTransformerType {

	/** 
	 * Holds the value of property webBrowserType. 
	 */
	private WebBrowser webBrowser;

	/** 
	 * Holds the value of property theme. 
	 */
	private Theme theme;

	/** 
	 * Creates a new instance of TransformerType.
	 * @param webBrowserType the web browser type.
	 * @param theme the property theme.
	 */
	public UIDLTransformerType(WebBrowser webBrowserType, Theme theme) {
		if (webBrowserType == null || theme == null)
			throw new IllegalArgumentException("WebBrowserType and Theme must be non-null values");
		this.webBrowser = webBrowserType;
		this.theme = theme;
	}

	/** 
	 * Returns the hash code for this string.
	 * @return the hash code value. 
	 */
	public int hashCode() {

		return this.toString().hashCode();
	}

	/** 
	 * Gets the web browser type used in the UIDLTransformer of this type.
	 * @return the Web browser type used.
	 */
	public WebBrowser getWebBrowser() {
		return this.webBrowser;
	}

	/** 
	 * Gets the theme used in the UIDLTransformer of this type.
	 * @return the Theme used.
	 */
	public Theme getTheme() {
		return this.theme;
	}

	/**
	 * Two types are equal, if their properties are equal.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		// Checks that the object are of the same class
		if (!(obj.getClass().equals(this.getClass())))
			return false;

		// Checks that the properties of the types are equal
		return this.toString().equals(obj.toString());
	}

	/**
	 * Textual representation of the UIDLTransformer type.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return " theme='"
			+ theme.getName()
			+ "' js="
			+ webBrowser.getJavaScriptVersion()
			+ "' markup='"
			+ webBrowser.getMarkupVersion()
			+ "'";
	}

}
