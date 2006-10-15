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

import com.itmill.tk.terminal.Terminal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

/** Web browser terminal type.
 *
 * This class implements web browser properties, which declare the features of
 * the web browser.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class WebBrowser implements Terminal {

	private static WebBrowser DEFAULT = new WebBrowser();

	/** Content type */
	private String contentType = "text/html; charset=utf-8";

	/** Holds the collection of accepted locales */
	private Collection locales = new ArrayList();

	/** Holds value of property browserApplication. */
	private String browserApplication = null;

	/** Should the client side checkking be done. */
	private boolean performClientCheck = true;

	/** Holds value for property isClientSideChecked. */
	private boolean clientSideChecked = false;

	/** Holds value of property javaScriptVersion. */
	private JavaScriptVersion javaScriptVersion = JAVASCRIPT_UNCHECKED;

	/** Holds value of property javaEnabled. */
	private boolean javaEnabled = false;

	/** Holds value of property frameSupport. */
	private boolean frameSupport = false;

	/** Holds value of property markup version. */
	private MarkupVersion markupVersion = MARKUP_HTML_3_2;

	/** Pixel width of the terminal screen */
	private int screenWidth = -1;

	/** Pixel height of the terminal screen */
	private int screenHeight = -1;

	/** Constuctor with some autorecognition capabilities 
	 *  Retrieves all capability information reported in http request headers:
	 * <ul>
	 *  <li>User web browser (User-Agent)</li>
	 *  <li>Supported locale(s)</li>
	 * </ul>
	 */

	/**
	 * Constructor WebBrowserType.
	 * Creates default WebBrowserType instance.
	 */
	public WebBrowser() {
	}

	/** Get name of the default theme
	 * @return Name of the terminal window
	 */
	public String getDefaultTheme() {
		return "default";
	}

	/** Get the name and version of the web browser application.
	 *
	 * This is the version string reported by the web-browser in http headers.
	 * @return Web browser application.
	 */
	public String getBrowserApplication() {
		return this.browserApplication;
	}

	/** Get the version of the supported Java Script by the browser.
	 *
	 * Null if the Java Script is not supported.
	 * @return Version of the supported Java Script
	 */
	public JavaScriptVersion getJavaScriptVersion() {
		return this.javaScriptVersion;
	}

	/** Does the browser support frames ?
	 * @return True if the browser supports frames, False if not
	 */
	public boolean isFrameSupport() {
		return this.frameSupport;
	}

	/** Set the browser frame support
	 * @param frameSupport True if the browser supports frames, False if not
	 */
	public void setFrameSupport(boolean frameSupport) {
		this.frameSupport = frameSupport;
	}

	/** Get the supported markup language.
	 *
	 * @return Supported markup language
	 */
	public MarkupVersion getMarkupVersion() {
		return this.markupVersion;
	}

	/** Get height of the terminal window in pixels
	 * @return Height of the terminal window
	 */
	public int getScreenHeight() {
		return this.screenHeight;
	}

	/** Get width of the terminal window in pixels
	 * @return Width of the terminal window
	 */
	public int getScreenWidth() {
		return this.screenWidth;
	}

	/** Get the default locale requested by the browser.
	 * @return Default locale
	 */
	public Locale getDefaultLocale() {
		if (this.locales.isEmpty())
			return null;
		return (Locale) this.locales.iterator().next();
	}

	/** Hash code composed of the properties of the web browser type */
	public int hashCode() {
		return toString().hashCode();
	}

	/** Test the equality of the properties for two web browser types */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof WebBrowser) {
			return toString().equals(obj.toString());
		}
		return false;
	}

	/** Repsent the type of the web browser as string */
	public String toString() {

		String localeString = "[";
		for (Iterator i = this.locales.iterator();
			i.hasNext();
			localeString += ",") {
			localeString += ((Locale) i.next()).toString();
		}
		localeString += "]";

		// Return catenation of the properties
		return "Browser:"
			+ this.browserApplication
			+ ", "
			+ "Locales:"
			+ localeString
			+ ", "
			+ "Frames:"
			+ this.frameSupport
			+ ", "
			+ "JavaScript:"
			+ this.javaScriptVersion
			+ ", "
			+ "Java: "
			+ this.javaEnabled
			+ ", "
			+ "Markup:"
			+ this.markupVersion
			+ ", "
			+ "Height:"
			+ this.screenHeight
			+ ", "
			+ "Width:"
			+ this.screenWidth
			+ ", ClientCheck:"
			+ this.performClientCheck
			+ ", ClientCheckDone:"
			+ this.clientSideChecked;
	}

	/** Get preferred content type */
	public String getContentType() {
		return contentType;
	}

	/** Check if this type supports also given browser.
	 *  @return true if this type matches the given browser.
	 */
	public boolean supports(String browser) {
		return this.getBrowserApplication().indexOf(browser) > 0;
	}

	/** Check if this type supports given markup language version.
	 *  @return true if this type supports the given markup version.
	 */
	public boolean supports(MarkupVersion html) {
		return this.getMarkupVersion().supports(html);
	}

	/** Check if this type supports given javascript version.
	 *  @param js The javascript version to check for.
	 *  @return true if this type supports the given javascript version.
	 */
	public boolean supports(JavaScriptVersion js) {
		return this.getJavaScriptVersion().supports(js);
	}

	/** Parse HTML version from string.
	 * @return HTMLVersion instance.
	 */
	private MarkupVersion doParseHTMLVersion(String html) {
		for (int i = 0; i < MARKUP_VERSIONS.length; i++) {
			if (MARKUP_VERSIONS[i].name.equals(html))
				return MARKUP_VERSIONS[i];
		}
		return MARKUP_UNKNOWN;
	}

	/** Parse JavaScript version from string.
		 * @return HTMLVersion instance.
	 */
	private JavaScriptVersion doParseJavaScriptVersion(String js) {
		for (int i = 0; i < JAVASCRIPT_VERSIONS.length; i++) {
			if (JAVASCRIPT_VERSIONS[i]
				.name
				.toLowerCase()
				.startsWith(js.toLowerCase()))
				return JAVASCRIPT_VERSIONS[i];
		}
		return JAVASCRIPT_NONE;
	}

	/** Parse HTML version from string.
	 * @return HTMLVersion instance.
	 */
	public static MarkupVersion parseHTMLVersion(String html) {
		return DEFAULT.doParseHTMLVersion(html);
	}

	/** Parse JavaScript version from string.
	 * @return HTMLVersion instance.
	 */
	public static JavaScriptVersion parseJavaScriptVersion(String js) {
		return DEFAULT.doParseJavaScriptVersion(js);
	}

	/** Get the client side cheked property.
	 *  Certain terminal features can only be detected at client side. This
	 *  property indicates if the client side detections have been performed
	 *  for this type.
	 * @return true if client has sent information about its properties. Default false
	 */
	public boolean isClientSideChecked() {
		return this.clientSideChecked;
	}

	/** Set the client side checked property.
	 *  Certain terminal features can only be detected at client side. This
	 *  property indicates if the client side detections have been performed
	 *  for this type.
	 * @param true if client has sent information about its properties, false otherweise.
	 */
	public void setClientSideChecked(boolean value) {
		this.clientSideChecked = value;
	}

	/** Should the client features be checked using remote scripts.
	 *  Should the client side terminal feature check be performed.
	 * @return true if client side checking should be performed for this terminal type. Default false.
	 */
	public boolean performClientCheck() {
		return this.performClientCheck;
	}

	/** Should the  client features be checked using remote scripts.
	 * 
	 * @return true if client side checking should be performed for this terminal type. Default false.
	 */
	public void performClientCheck(boolean value) {
		this.performClientCheck = value;
	}

	/** Check if web browser supports Java.
	 * @return boolean
	 */
	public boolean isJavaEnabled() {
		return javaEnabled;
	}

	/** Returns the locales supported by the web browser.
	 * @return Collection
	 */
	public Collection getLocales() {
		return locales;
	}

	/** Sets the browser application.
	 *  This corresponds to User-Agent HTTP header.
	 * @param browserApplication The browserApplication to set
	 */
	public void setBrowserApplication(String browserApplication) {
		this.browserApplication = browserApplication;
	}

	/** Sets the default content type.
	 *  Default is <code>text/html</code>
	 * @param contentType The contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/** Sets the java enabled property.
	 * @param javaEnabled The javaEnabled to set
	 */
	public void setJavaEnabled(boolean javaEnabled) {
		this.javaEnabled = javaEnabled;
	}

	/**Sets the JavaScript version.
	 * @param javaScriptVersion The JavaScript version to set
	 */
	public void setJavaScriptVersion(JavaScriptVersion javaScriptVersion) {
		this.javaScriptVersion = javaScriptVersion;
	}

	/** Sets the markup language version.
	 * @param markupVersion ersion The markup language version to set
	 */
	public void setMarkupVersion(MarkupVersion markupVersion) {
		this.markupVersion = markupVersion;
	}

	/** Sets the screen height.
	 * @param screenHeight The screen height to set in pixels.
	 */
	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	/** Sets the screen width.
	 * @param screenWidth The screenWidth to set in pixels.
	 */
	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	/*
	 * Consts defining the supported markup language versions 
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	public class MarkupVersion {
		private String name;
		private int order;

		/**
		 * @see java.lang.Object#equals(Object)
		 */
		public boolean equals(Object obj) {
			if (obj != null && obj instanceof MarkupVersion)
				return name.equals(((MarkupVersion) obj).name);
			return false;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return name;
		}

		private MarkupVersion(String name, int order) {
			this.name = name;
			this.order = order;
		}

		/** Check compability with other HTML version.
		 *  @return true if this is compatible with the other, false otherwise
		 */
		public boolean supports(MarkupVersion other) {
			return (this.order >= other.order);
		}

	}

	public static final MarkupVersion MARKUP_UNKNOWN =
		DEFAULT.new MarkupVersion("HTML unknown", 0);
	public static final MarkupVersion MARKUP_HTML_2_0 =
		DEFAULT.new MarkupVersion("HTML 2.0", 20);
	public static final MarkupVersion MARKUP_HTML_3_2 =
		DEFAULT.new MarkupVersion("HTML 3.2", 32);
	public static final MarkupVersion MARKUP_HTML_4_0 =
		DEFAULT.new MarkupVersion("HTML 4.0", 40);
	public static final MarkupVersion MARKUP_XHTML_1_0 =
		DEFAULT.new MarkupVersion("XHTML 1.0", 110);
	public static final MarkupVersion MARKUP_XHTML_2_0 =
		DEFAULT.new MarkupVersion("XHTML 2.0", 120);
	public static final MarkupVersion MARKUP_WML_1_0 =
		DEFAULT.new MarkupVersion("WML 1.0", 10);
	public static final MarkupVersion MARKUP_WML_1_1 =
		DEFAULT.new MarkupVersion("WML 1.1", 11);
	public static final MarkupVersion MARKUP_WML_1_2 =
		DEFAULT.new MarkupVersion("WML 1.2", 12);

	public static final MarkupVersion[] MARKUP_VERSIONS =
		new MarkupVersion[] {
			MARKUP_UNKNOWN,
			MARKUP_HTML_2_0,
			MARKUP_HTML_3_2,
			MARKUP_HTML_4_0,
			MARKUP_XHTML_1_0,
			MARKUP_XHTML_2_0,
			MARKUP_WML_1_0,
			MARKUP_WML_1_1,
			MARKUP_WML_1_2 };
	/*
	 * Consts defining the supported JavaScript versions 
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	public class JavaScriptVersion {
		private String name;
		private int order;

		/**
		 * @see java.lang.Object#equals(Object)
		 */
		public boolean equals(Object obj) {
			if (obj != null && obj instanceof JavaScriptVersion)
				return name.equals(((JavaScriptVersion) obj).name);
			return false;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return name;
		}

		private JavaScriptVersion(String name, int order) {
			this.name = name;
			this.order = order;
		}

		/** Check compability with other JavaScript version.
		 *  Use this like:
		 *  <code>boolean isEcma = someVersion.supports(ECMA_262);</code>
		 *  @return true if this supports the other, false otherwise
		 */
		public boolean supports(JavaScriptVersion other) {

			// ECMA-262 support compare
			if (other.equals(ECMA_262)) {

				// JScript over 5.0 support ECMA-262
				if (this.order >= 100) {
					return (this.order >= JSCRIPT_5_0.order);
				} else {
					return (this.order >= JAVASCRIPT_1_3.order);
				}
			}

			// JavaScript version compare
			else if (this.order < 100 && other.order < 100) {
				return (this.order >= other.order);
			}

			// JScript version compare
			else if (this.order >= 100 && other.order >= 100) {
				return (this.order >= other.order);
			}

			return false;

		}

	}
	public static final JavaScriptVersion JAVASCRIPT_UNCHECKED =
		DEFAULT.new JavaScriptVersion("JavaScript unchecked", -1);
	public static final JavaScriptVersion JAVASCRIPT_NONE =
		DEFAULT.new JavaScriptVersion("JavaScript none", -1);
	public static final JavaScriptVersion JAVASCRIPT_1_0 =
		DEFAULT.new JavaScriptVersion("JavaScript 1.0", 10);
	public static final JavaScriptVersion JAVASCRIPT_1_1 =
		DEFAULT.new JavaScriptVersion("JavaScript 1.1", 11);
	public static final JavaScriptVersion JAVASCRIPT_1_2 =
		DEFAULT.new JavaScriptVersion("JavaScript 1.2", 12);
	public static final JavaScriptVersion JAVASCRIPT_1_3 =
		DEFAULT.new JavaScriptVersion("JavaScript 1.3", 13);
	public static final JavaScriptVersion JAVASCRIPT_1_4 =
		DEFAULT.new JavaScriptVersion("JavaScript 1.4", 14);
	public static final JavaScriptVersion JAVASCRIPT_1_5 =
		DEFAULT.new JavaScriptVersion("JavaScript 1.5", 15);
	public static final JavaScriptVersion JSCRIPT_1_0 =
		DEFAULT.new JavaScriptVersion("JScript 1.0", 110);
	public static final JavaScriptVersion JSCRIPT_3_0 =
		DEFAULT.new JavaScriptVersion("JScript 3.0", 130);
	public static final JavaScriptVersion JSCRIPT_4_0 =
		DEFAULT.new JavaScriptVersion("JScript 4.0", 140);
	public static final JavaScriptVersion JSCRIPT_5_0 =
		DEFAULT.new JavaScriptVersion("JScript 5.0", 150);
	public static final JavaScriptVersion JSCRIPT_5_1 =
		DEFAULT.new JavaScriptVersion("JScript 5.1", 151);
	public static final JavaScriptVersion JSCRIPT_5_5 =
		DEFAULT.new JavaScriptVersion("JScript 5.5", 155);
	public static final JavaScriptVersion JSCRIPT_5_6 =
		DEFAULT.new JavaScriptVersion("JScript 5.6", 156);
	public static final JavaScriptVersion ECMA_262 =
		DEFAULT.new JavaScriptVersion("ECMA-262", 262);

	public static final JavaScriptVersion[] JAVASCRIPT_VERSIONS =
		new JavaScriptVersion[] {
			JAVASCRIPT_UNCHECKED,
			JAVASCRIPT_NONE,
			JAVASCRIPT_1_0,
			JAVASCRIPT_1_1,
			JAVASCRIPT_1_2,
			JAVASCRIPT_1_3,
			JAVASCRIPT_1_4,
			JAVASCRIPT_1_5,
			JSCRIPT_1_0,
			JSCRIPT_3_0,
			JSCRIPT_4_0,
			JSCRIPT_5_0,
			JSCRIPT_5_1,
			JSCRIPT_5_5,
			JSCRIPT_5_6,
			ECMA_262 };
}
