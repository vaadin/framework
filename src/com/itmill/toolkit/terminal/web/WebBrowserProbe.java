/* *************************************************************************
 
                               IT Mill Toolkit 

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
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.terminal.web;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * The WebBrowserProbe uses JavaScript to determine the capabilities
 * of the client browser.
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class WebBrowserProbe {

	private static final String WA_NOSCRIPT = "WA_NOSCRIPT";

	private static final String CLIENT_TYPE = "wa_browser";

	/** Return the terminal type from the given session. 
	 *  @return WebBrowser instance for the given session. 
	 */
	public static WebBrowser getTerminalType(HttpSession session) {
		if (session != null)
			return (WebBrowser) session.getAttribute(CLIENT_TYPE);
		return null;
	}

	/** Set the terminal type for the given session. 
	 *  @return WebBrowser instance for the given session. 
	 */
	public static void setTerminalType(
		HttpSession session,
		WebBrowser terminal) {
		if (session != null)
			session.setAttribute(CLIENT_TYPE, terminal);
	}

	/** Handle client checking. 
	 *  @param request The HTTP request to process.
	 *  @param response HTTP response to write to.
	 *  @return true if response should include a probe script
	 **/
	public static boolean handleProbeRequest(
		HttpServletRequest request,
		Map parameters)
		throws ServletException {

		HttpSession s = request.getSession();
		WebBrowser browser = getTerminalType(s);
		if (browser != null) {

			// Check if no-script was requested
			if (parameters.containsKey(WA_NOSCRIPT)) {
				String val = ((String[]) parameters.get(WA_NOSCRIPT))[0];
				if (val != null && "1".equals(val)) {
					browser.setJavaScriptVersion(WebBrowser.JAVASCRIPT_NONE);
					browser.setClientSideChecked(true);
				} else {
					// Recheck
					browser.setClientSideChecked(false);
				}
			}

			// If client is alredy checked disable further checking
			if (browser.isClientSideChecked())
				return false;

		}

		// Create new type based on client parameters
		browser = probe(browser, request, parameters);
		setTerminalType(s,browser);

		// Set client as checked if parameters were found			
		if (parameters.containsKey("wa_clientprobe")) {
			String val = ((String[]) parameters.get("wa_clientprobe"))[0];
			browser.setClientSideChecked(val != null && "1".equals(val));
		}

		// Include probe script if requested and not alredy probed
		return browser.performClientCheck() && !browser.isClientSideChecked();

	}

	/** Determine versions based on user agent string. 
	 *  @param agent HTTP User-Agent request header.
	 *  @return new WebBrowser instance initialized based on agent features.
	 */
	public static WebBrowser probe(String agent) {
		WebBrowser res = new WebBrowser();
		if (agent == null)
			return res;

		// Set the agent string
		res.setBrowserApplication(agent);

		// Konqueror
		if (agent.indexOf("Konqueror") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JSCRIPT_5_6);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		}

		// Opera
		else if (
			(agent.indexOf("Opera 6.") >= 0)
				|| (agent.indexOf("Opera 5.") >= 0)
				|| (agent.indexOf("Opera 4.") >= 0)) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_3);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
					res.setMarkupVersion(WebBrowser.MARKUP_HTML_4_0);
		} else if (agent.indexOf("Opera 3.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_3);
			res.setJavaEnabled(false);
			res.setFrameSupport(true);
		}

		// OmniWeb
		else if (agent.indexOf("OmniWeb") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_3);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		}

		// Mosaic
		else if (agent.indexOf("Mosaic") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_3);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
			res.setMarkupVersion(WebBrowser.MARKUP_HTML_2_0);
		}

		// Lynx
		else if (agent.indexOf("Lynx") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_NONE);
			res.setJavaEnabled(false);
			res.setFrameSupport(true);
		}

		// Microsoft Browsers
		// See Microsoft documentation for details:
		// http://msdn.microsoft.com/library/default.asp?url=/library/
		//        en-us/script56/html/js56jsoriversioninformation.asp
		else if (agent.indexOf("MSIE 7.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JSCRIPT_5_7);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
			res.setMarkupVersion(WebBrowser.MARKUP_HTML_4_0);		
		} else if (agent.indexOf("MSIE 6.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JSCRIPT_5_6);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
			res.setMarkupVersion(WebBrowser.MARKUP_HTML_4_0);
		} else if (agent.indexOf("MSIE 5.5") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JSCRIPT_5_5);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
			res.setMarkupVersion(WebBrowser.MARKUP_HTML_4_0);
		} else if (agent.indexOf("MSIE 5.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JSCRIPT_5_0);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
			res.setMarkupVersion(WebBrowser.MARKUP_HTML_4_0);
		} else if (agent.indexOf("MSIE 4.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JSCRIPT_3_0);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
			res.setMarkupVersion(WebBrowser.MARKUP_HTML_4_0);
		} else if (agent.indexOf("MSIE 3.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JSCRIPT_3_0);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		} else if (agent.indexOf("MSIE 2.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_NONE);
			res.setJavaEnabled(false);
			if (agent.indexOf("Mac") >= 0) {
				res.setFrameSupport(true);
			} else {
				res.setFrameSupport(false);
			}
		}

		// Netscape browsers
		else if (agent.indexOf("Netscape6") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_5);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
			res.setMarkupVersion(WebBrowser.MARKUP_HTML_4_0);
		} else if (
			(agent.indexOf("Mozilla/4.06") >= 0)
				|| (agent.indexOf("Mozilla/4.7") >= 0)) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_3);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
					res.setMarkupVersion(WebBrowser.MARKUP_HTML_4_0);
		} else if (agent.indexOf("Mozilla/4.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_2);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		} else if (agent.indexOf("Mozilla/3.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_1);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		} else if (agent.indexOf("Mozilla/2.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_0);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		}

		// Mozilla Open-Source Browsers		
		else if (agent.indexOf("Mozilla/5.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_5);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
			res.setMarkupVersion(WebBrowser.MARKUP_HTML_4_0);
		}

		// Unknown browser
		else {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_UNCHECKED);
			res.setJavaEnabled(false);
			res.setMarkupVersion(WebBrowser.MARKUP_UNKNOWN);
			res.setFrameSupport(false);
		}

		return res;
	}

	/** Create new instance of WebBrowser by initializing the values
	 *  based on user request.
	 *  @param browser The browser to be updated. If null a new instance is created.
	 *  @param request Request to be used as defaults.
	 *  @param params Parameters to be used as defaults.
	 *  @return new WebBrowser instance initialized based on request parameters.
	 */
	public static WebBrowser probe(
		WebBrowser browser,
		HttpServletRequest request,
		Map params) {

		// Initialize defaults based on client features
		WebBrowser res = browser;
		if (res == null) {
			res = probe(request.getHeader("User-Agent"));
		}

		// Client locales
		Collection locales = res.getLocales();
		locales.clear();
		for (Enumeration e = request.getLocales(); e.hasMoreElements();) {
			locales.add(e.nextElement());
		}

		//Javascript version
		if (params.containsKey("wa_jsversion")) {
			String val = ((String[]) params.get("wa_jsversion"))[0];
			if (val != null) {
				res.setJavaScriptVersion(
					WebBrowser.parseJavaScriptVersion(val));
			}
		}
		//Java support
		if (params.containsKey("wa_javaenabled")) {
			String val = ((String[]) params.get("wa_javaenabled"))[0];
			if (val != null) {
				res.setJavaEnabled(Boolean.valueOf(val).booleanValue());
			}
		}
		//Screen width
		if (params.containsKey("wa_screenwidth")) {
			String val = ((String[]) params.get("wa_screenwidth"))[0];
			if (val != null) {
				try {
					res.setScreenWidth(Integer.parseInt(val));
				} catch (NumberFormatException e) {
					res.setScreenWidth(-1);
				}
			}
		}
		//Screen height
		if (params.containsKey("wa_screenheight")) {
			String val = ((String[]) params.get("wa_screenheight"))[0];
			if (val != null) {
				try {
					res.setScreenHeight(Integer.parseInt(val));
				} catch (NumberFormatException e) {
					res.setScreenHeight(-1);
				}
			}
		}

		return res;
	}
}