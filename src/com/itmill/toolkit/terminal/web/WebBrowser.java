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

import com.itmill.toolkit.terminal.Terminal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

/**
 * Web browser terminal type.
 * 
 * This class implements web browser properties, which declare the features of
 * the web browser.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class WebBrowser implements Terminal {

	private static WebBrowser DEFAULT = new WebBrowser();

	/**
	 * Content type.
	 */
	private String contentType = "text/html; charset=utf-8";

	/**
	 * Holds the collection of accepted locales.
	 */
	private Collection locales = new ArrayList();

	/**
	 * Holds value of property browserApplication.
	 */
	private String browserApplication = null;

	/**
	 * Should the client side checkking be done.
	 */
	private boolean performClientCheck = true;

	/**
	 * Holds value for property isClientSideChecked.
	 */
	private boolean clientSideChecked = false;

	/**
	 * Holds value of property javaScriptVersion.
	 */
	private JavaScriptVersion javaScriptVersion = JAVASCRIPT_UNCHECKED;

	/**
	 * Holds value of property javaEnabled.
	 */
	private boolean javaEnabled = false;

	/**
	 * Holds value of property frameSupport.
	 */
	private boolean frameSupport = false;

	/**
	 * Holds value of property markup version.
	 */
	private MarkupVersion markupVersion = MARKUP_HTML_3_2;

	/**
	 * Pixel width of the terminal screen.
	 */
	private int screenWidth = -1;

	/**
	 * Pixel height of the terminal screen.
	 */
	private int screenHeight = -1;

	private RenderingMode renderingMode = RENDERING_MODE_UNDEFINED;

	/**
	 * Constuctor with some autorecognition capabilities Retrieves all
	 * capability information reported in http request headers:
	 * <ul>
	 * <li>User web browser (User-Agent)</li>
	 * <li>Supported locale(s)</li>
	 * </ul>
	 */

	/**
	 * Constructor WebBrowserType. Creates a default WebBrowserType instance.
	 */
	public WebBrowser() {
	}

	/**
	 * Gets the name of the default theme.
	 * 
	 * @return the Name of the terminal window.
	 */
	public String getDefaultTheme() {
		return ApplicationServlet.DEFAULT_THEME;
	}

	/**
	 * Gets the name and version of the web browser application. This is the
	 * version string reported by the web-browser in http headers.
	 * 
	 * @return the Web browser application.
	 */
	public String getBrowserApplication() {
		return this.browserApplication;
	}

	/**
	 * Gets the version of the supported Java Script by the browser.
	 * 
	 * <code>Null</code> if the Java Script is not supported.
	 * 
	 * @return the Version of the supported Java Script.
	 */
	public JavaScriptVersion getJavaScriptVersion() {
		return this.javaScriptVersion;
	}

	/**
	 * Does the browser support frames ?
	 * 
	 * @return <code>true</code> if the browser supports frames, otherwise
	 *         <code>false</code>.
	 */
	public boolean isFrameSupport() {
		return this.frameSupport;
	}

	/**
	 * Sets the browser frame support.
	 * 
	 * @param frameSupport
	 *            True if the browser supports frames, False if not.
	 */
	public void setFrameSupport(boolean frameSupport) {
		this.frameSupport = frameSupport;
	}

	/**
	 * Gets the supported markup language.
	 * 
	 * @return the Supported markup language
	 */
	public MarkupVersion getMarkupVersion() {
		return this.markupVersion;
	}

	/**
	 * Gets the height of the terminal window in pixels.
	 * 
	 * @return the Height of the terminal window.
	 */
	public int getScreenHeight() {
		return this.screenHeight;
	}

	/**
	 * Gets the width of the terminal window in pixels.
	 * 
	 * @return the Width of the terminal window.
	 */
	public int getScreenWidth() {
		return this.screenWidth;
	}

	/**
	 * Gets the default locale requested by the browser.
	 * 
	 * @return the Default locale.
	 */
	public Locale getDefaultLocale() {
		if (this.locales.isEmpty())
			return null;
		return (Locale) this.locales.iterator().next();
	}

	/**
	 * Hash code composed of the properties of the web browser type.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Tests the equality of the properties for two web browser types.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof WebBrowser) {
			return toString().equals(obj.toString());
		}
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		String localeString = "[";
		for (Iterator i = this.locales.iterator(); i.hasNext(); localeString += ",") {
			localeString += ((Locale) i.next()).toString();
		}
		localeString += "]";

		// Returns catenation of the properties
		return "Browser:" + this.browserApplication + ", " + "Locales:"
				+ localeString + ", " + "Frames:" + this.frameSupport + ", "
				+ "JavaScript:" + this.javaScriptVersion + ", " + "Java: "
				+ this.javaEnabled + ", " + "Markup:" + this.markupVersion
				+ ", " + "Height:" + this.screenHeight + ", " + "Width:"
				+ this.screenWidth + ", ClientCheck:" + this.performClientCheck
				+ ", ClientCheckDone:" + this.clientSideChecked;
	}

	/**
	 * Gets the preferred content type.
	 * 
	 * @return the content type.
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Checks if this type supports also given browser.
	 * 
	 * @param browser
	 *            the browser type.
	 * @return true if this type matches the given browser.
	 */
	public boolean supports(String browser) {
		return this.getBrowserApplication().indexOf(browser) >= 0;
	}

	/**
	 * Checks if this type supports given markup language version.
	 * 
	 * @param html
	 *            the markup language version.
	 * @return <code>true</ocde> if this type supports the given markup version,otherwise <code>false</code>.
	 */
	public boolean supports(MarkupVersion html) {
		return this.getMarkupVersion().supports(html);
	}

	/**
	 * Checks if this type supports given javascript version.
	 * 
	 * @param js
	 *            the javascript version to check for.
	 * @return true if this type supports the given javascript version.
	 */
	public boolean supports(JavaScriptVersion js) {
		return this.getJavaScriptVersion().supports(js);
	}

	/**
	 * Parses HTML version from string.
	 * 
	 * @param html
	 * @return HTMLVersion instance.
	 */
	private MarkupVersion doParseHTMLVersion(String html) {
		for (int i = 0; i < MARKUP_VERSIONS.length; i++) {
			if (MARKUP_VERSIONS[i].name.equals(html))
				return MARKUP_VERSIONS[i];
		}
		return MARKUP_UNKNOWN;
	}

	/**
	 * Parses JavaScript version from string.
	 * 
	 * @param js
	 *            the javascript version to check for.
	 * @return HTMLVersion instance.
	 */
	private JavaScriptVersion doParseJavaScriptVersion(String js) {
		for (int i = 0; i < JAVASCRIPT_VERSIONS.length; i++) {
			if (JAVASCRIPT_VERSIONS[i].name.toLowerCase().startsWith(
					js.toLowerCase()))
				return JAVASCRIPT_VERSIONS[i];
		}
		return JAVASCRIPT_NONE;
	}

	/**
	 * Parses HTML version from string.
	 * 
	 * @param html
	 * @return the HTMLVersion instance.
	 */
	public static MarkupVersion parseHTMLVersion(String html) {
		return DEFAULT.doParseHTMLVersion(html);
	}

	/**
	 * Parse JavaScript version from string.
	 * 
	 * @param js
	 *            the javascript version to check for.
	 * @return the HTMLVersion instance.
	 */
	public static JavaScriptVersion parseJavaScriptVersion(String js) {
		return DEFAULT.doParseJavaScriptVersion(js);
	}

	/**
	 * Gets the client side cheked property. Certain terminal features can only
	 * be detected at client side. This property indicates if the client side
	 * detections have been performed for this type.
	 * 
	 * @return <code>true</code> if client has sent information about its
	 *         properties. Default is <code>false</code>.
	 */
	public boolean isClientSideChecked() {
		return this.clientSideChecked;
	}

	/**
	 * Sets the client side checked property. Certain terminal features can only
	 * be detected at client side. This property indicates if the client side
	 * detections have been performed for this type.
	 * 
	 * @param value
	 *            true if client has sent information about its properties,
	 *            false otherweise.
	 */
	public void setClientSideChecked(boolean value) {
		this.clientSideChecked = value;
	}

	/**
	 * Should the client features be checked using remote scripts. Should the
	 * client side terminal feature check be performed.
	 * 
	 * @return <code>true</code> if client side checking should be performed
	 *         for this terminal type. Default is <code>false</code>.
	 */
	public boolean performClientCheck() {
		return this.performClientCheck;
	}

	/**
	 * Should the client features be checked using remote scripts.
	 * 
	 * @param value
	 * @return <code>true</code> if client side checking should be performed
	 *         for this terminal type. Default <code>false</code>.
	 */
	public void performClientCheck(boolean value) {
		this.performClientCheck = value;
	}

	/**
	 * Checks if web browser supports Java.
	 * 
	 * @return <code>true<code> if the browser supports java otherwise <code>false</code>.
	 */
	public boolean isJavaEnabled() {
		return javaEnabled;
	}

	/**
	 * Returns the locales supported by the web browser.
	 * 
	 * @return the Collection.
	 */
	public Collection getLocales() {
		return locales;
	}

	/**
	 * Sets the browser application. This corresponds to User-Agent HTTP header.
	 * 
	 * @param browserApplication
	 *            the browserApplication to set.
	 */
	public void setBrowserApplication(String browserApplication) {
		this.browserApplication = browserApplication;
	}

	/**
	 * Sets the default content type. Default is <code>text/html</code>
	 * 
	 * @param contentType
	 *            the contentType to set.
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Sets the java enabled property.
	 * 
	 * @param javaEnabled
	 *            the javaEnabled to set.
	 */
	public void setJavaEnabled(boolean javaEnabled) {
		this.javaEnabled = javaEnabled;
	}

	/**
	 * Sets the JavaScript version.
	 * 
	 * @param javaScriptVersion
	 *            the JavaScript version to set.
	 */
	public void setJavaScriptVersion(JavaScriptVersion javaScriptVersion) {
		this.javaScriptVersion = javaScriptVersion;
	}

	/**
	 * Sets the markup language version.
	 * 
	 * @param markupVersion
	 *            the markup language version to set.
	 */
	public void setMarkupVersion(MarkupVersion markupVersion) {
		this.markupVersion = markupVersion;
	}

	/**
	 * Sets the screen height.
	 * 
	 * @param screenHeight
	 *            the screen height to set in pixels.
	 */
	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	/**
	 * Sets the screen width.
	 * 
	 * @param screenWidth
	 *            the screenWidth to set in pixels.
	 */
	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	/*
	 * Consts defining the supported markup language versions @author IT Mill
	 * Ltd.
	 * 
	 * @version @VERSION@
	 * @since 3.0
	 */
	public class MarkupVersion {
		private String name;

		private int order;

		/**
		 * Returns <code>true</code> if and only if the argument is not
		 * <code>null</code> and is a Boolean object that represents the same
		 * boolean value as this object.
		 * 
		 * @param obj
		 *            the object to compare with.
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

		/**
		 * 
		 * @param name
		 * @param order
		 */
		private MarkupVersion(String name, int order) {
			this.name = name;
			this.order = order;
		}

		/**
		 * Checks the compability with other HTML version.
		 * 
		 * @param other
		 *            the HTML version.
		 * @return <code>true</code> if this is compatible with the other,
		 *         otherwise <code>false</code>.
		 */
		public boolean supports(MarkupVersion other) {
			return (this.order >= other.order);
		}

	}

	public static final MarkupVersion MARKUP_UNKNOWN = DEFAULT.new MarkupVersion(
			"HTML unknown", 0);

	public static final MarkupVersion MARKUP_HTML_2_0 = DEFAULT.new MarkupVersion(
			"HTML 2.0", 20);

	public static final MarkupVersion MARKUP_HTML_3_2 = DEFAULT.new MarkupVersion(
			"HTML 3.2", 32);

	public static final MarkupVersion MARKUP_HTML_4_0 = DEFAULT.new MarkupVersion(
			"HTML 4.0", 40);

	public static final MarkupVersion MARKUP_XHTML_1_0 = DEFAULT.new MarkupVersion(
			"XHTML 1.0", 110);

	public static final MarkupVersion MARKUP_XHTML_2_0 = DEFAULT.new MarkupVersion(
			"XHTML 2.0", 120);

	public static final MarkupVersion MARKUP_WML_1_0 = DEFAULT.new MarkupVersion(
			"WML 1.0", 10);

	public static final MarkupVersion MARKUP_WML_1_1 = DEFAULT.new MarkupVersion(
			"WML 1.1", 11);

	public static final MarkupVersion MARKUP_WML_1_2 = DEFAULT.new MarkupVersion(
			"WML 1.2", 12);

	public static final MarkupVersion[] MARKUP_VERSIONS = new MarkupVersion[] {
			MARKUP_UNKNOWN, MARKUP_HTML_2_0, MARKUP_HTML_3_2, MARKUP_HTML_4_0,
			MARKUP_XHTML_1_0, MARKUP_XHTML_2_0, MARKUP_WML_1_0, MARKUP_WML_1_1,
			MARKUP_WML_1_2 };

	/*
	 * Consts defining the supported JavaScript versions @author IT Mill Ltd.
	 * 
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

		/**
		 * 
		 * @param name
		 * @param order
		 */
		private JavaScriptVersion(String name, int order) {
			this.name = name;
			this.order = order;
		}

		/**
		 * Checks the compability with other JavaScript version. Use this like:
		 * <code>boolean isEcma = someVersion.supports(ECMA_262);</code>
		 * 
		 * @param other
		 *            the java script version.
		 * @return <code>true</code> if this supports the other, otherwise
		 *         <code>false</code>.
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

	public static final JavaScriptVersion JAVASCRIPT_UNCHECKED = DEFAULT.new JavaScriptVersion(
			"JavaScript unchecked", -1);

	public static final JavaScriptVersion JAVASCRIPT_NONE = DEFAULT.new JavaScriptVersion(
			"JavaScript none", -1);

	public static final JavaScriptVersion JAVASCRIPT_1_0 = DEFAULT.new JavaScriptVersion(
			"JavaScript 1.0", 10);

	public static final JavaScriptVersion JAVASCRIPT_1_1 = DEFAULT.new JavaScriptVersion(
			"JavaScript 1.1", 11);

	public static final JavaScriptVersion JAVASCRIPT_1_2 = DEFAULT.new JavaScriptVersion(
			"JavaScript 1.2", 12);

	public static final JavaScriptVersion JAVASCRIPT_1_3 = DEFAULT.new JavaScriptVersion(
			"JavaScript 1.3", 13);

	public static final JavaScriptVersion JAVASCRIPT_1_4 = DEFAULT.new JavaScriptVersion(
			"JavaScript 1.4", 14);

	public static final JavaScriptVersion JAVASCRIPT_1_5 = DEFAULT.new JavaScriptVersion(
			"JavaScript 1.5", 15);

	public static final JavaScriptVersion JSCRIPT_1_0 = DEFAULT.new JavaScriptVersion(
			"JScript 1.0", 110);

	public static final JavaScriptVersion JSCRIPT_3_0 = DEFAULT.new JavaScriptVersion(
			"JScript 3.0", 130);

	public static final JavaScriptVersion JSCRIPT_4_0 = DEFAULT.new JavaScriptVersion(
			"JScript 4.0", 140);

	public static final JavaScriptVersion JSCRIPT_5_0 = DEFAULT.new JavaScriptVersion(
			"JScript 5.0", 150);

	public static final JavaScriptVersion JSCRIPT_5_1 = DEFAULT.new JavaScriptVersion(
			"JScript 5.1", 151);

	public static final JavaScriptVersion JSCRIPT_5_5 = DEFAULT.new JavaScriptVersion(
			"JScript 5.5", 155);

	public static final JavaScriptVersion JSCRIPT_5_6 = DEFAULT.new JavaScriptVersion(
			"JScript 5.6", 156);

	public static final JavaScriptVersion JSCRIPT_5_7 = DEFAULT.new JavaScriptVersion(
			"JScript 5.7", 157);

	public static final JavaScriptVersion ECMA_262 = DEFAULT.new JavaScriptVersion(
			"ECMA-262", 262);

	public static final JavaScriptVersion[] JAVASCRIPT_VERSIONS = new JavaScriptVersion[] {
			JAVASCRIPT_UNCHECKED, JAVASCRIPT_NONE, JAVASCRIPT_1_0,
			JAVASCRIPT_1_1, JAVASCRIPT_1_2, JAVASCRIPT_1_3, JAVASCRIPT_1_4,
			JAVASCRIPT_1_5, JSCRIPT_1_0, JSCRIPT_3_0, JSCRIPT_4_0, JSCRIPT_5_0,
			JSCRIPT_5_1, JSCRIPT_5_5, JSCRIPT_5_6, JSCRIPT_5_7, ECMA_262 };

	/*
	 * Consts defining the rendering mode @author IT Mill Ltd.
	 * 
	 * @version @VERSION@
	 * @since 4.0
	 */
	public class RenderingMode {
		RenderingMode() {

		}
	}

	public static final RenderingMode RENDERING_MODE_UNDEFINED = DEFAULT.new RenderingMode();

	public static final RenderingMode RENDERING_MODE_HTML = DEFAULT.new RenderingMode();

	public static final RenderingMode RENDERING_MODE_AJAX = DEFAULT.new RenderingMode();

	/**
	 * Gets the current rendering mode.
	 * 
	 * @return the current rendering mode.
	 */
	public RenderingMode getRenderingMode() {
		return renderingMode;
	}

	/**
	 * Sets the current rendering mode.
	 * 
	 * @param renderingMode
	 *            the rendering mode.
	 */
	public void setRenderingMode(RenderingMode renderingMode) {
		this.renderingMode = renderingMode;
	}

}
