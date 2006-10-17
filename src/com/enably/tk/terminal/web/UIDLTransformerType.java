/* *************************************************************************
 
                               Enably Toolkit 

               Development of Browser User Intarfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license/license.txt. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see license/licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@enably.com
   Finland                               company www: www.enably.com
   
   Primary source for information and releases: www.enably.com

   ********************************************************************** */

package com.enably.tk.terminal.web;

/** Type of the transformer.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class UIDLTransformerType {

	/** Holds value of property webBrowserType. */
	private WebBrowser webBrowser;

	/** Holds value of property theme. */
	private Theme theme;

	/** Creates a new instance of TransformerType */
	public UIDLTransformerType(WebBrowser webBrowserType, Theme theme) {
		if (webBrowserType == null || theme == null)
			throw new IllegalArgumentException("WebBrowserType and Theme must be non-null values");
		this.webBrowser = webBrowserType;
		this.theme = theme;
	}

	/** The hash code of the equal types are the same */
	public int hashCode() {

		return this.toString().hashCode();
	}

	/** Get the web browser type used in the UIDLTransformer of this type.
	 * @return Web browser type used.
	 */
	public WebBrowser getWebBrowser() {
		return this.webBrowser;
	}

	/** Get the theme used in the UIDLTransformer of this type.
	 * @return Theme used.
	 */
	public Theme getTheme() {
		return this.theme;
	}

	/** Two types are equal, if their properties are equal */
	public boolean equals(Object obj) {
		// Check that the object are of the same class
		if (!(obj.getClass().equals(this.getClass())))
			return false;

		// Check that the properties of the types are equal
		return this.toString().equals(obj.toString());
	}

	/** Textual representation of the UIDLTransformer type */
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
