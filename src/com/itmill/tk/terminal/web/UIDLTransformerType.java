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
   
package com.itmill.tk.terminal.web;

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
