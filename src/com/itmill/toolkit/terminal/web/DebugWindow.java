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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.http.HttpSession;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.data.util.MethodProperty;
import com.itmill.toolkit.terminal.FileResource;
import com.itmill.toolkit.ui.*;

/**
 * This class provides a debugging window where one may view the UIDL of
 * the current window, or in a tabset the UIDL of an active frameset.
 * 
 * It is primarily inteded for creating and debugging themes.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class DebugWindow extends Window {

	protected static String WINDOW_NAME = "debug";

	private Application debuggedApplication;
	private HashMap rawUIDL = new HashMap();
	private WebAdapterServlet servlet;
	private HttpSession session;

	private TabSheet tabs = new TabSheet();
	private Select themeSelector;
	private Label applicationInfo = new Label("", Label.CONTENT_XHTML);

	/**Create new debug window for an application.
	 * @param debuggedApplication Application to be debugged.
	 * @param session Session to be debugged.
	 * @param servlet Servlet to be debugged.
	 */
	protected DebugWindow(
		Application debuggedApplication,
		HttpSession session,
		WebAdapterServlet servlet) {

		super("Debug window");
		setName(WINDOW_NAME);
		setServlet(servlet);
		setSession(session);
		setBorder(Window.BORDER_NONE);
		

		// Create control buttons
		OrderedLayout controls =
			new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
		controls.addComponent(
			new Button("Restart Application", this, "restartApplication"));
		controls.addComponent(
			new Button("Clear Session", this, "clearSession"));
		Collection themes = servlet.getThemeSource().getThemes();
		Collection names = new LinkedList();
		for (Iterator i = themes.iterator(); i.hasNext();) {
			names.add(((Theme) i.next()).getName());
		}

		// Create theme selector
		themeSelector = new Select("Application Theme", names);
		themeSelector.setWriteThrough(false);

		// Terminal type editor
		Label terminal =
			new Label("<h2>Terminal Information</h2> ", Label.CONTENT_XHTML);
		Form browser = new Form();
		browser.setItemDataSource(
			new BeanItem(WebBrowserProbe.getTerminalType(session)));
		browser.removeItemProperty("class");
		browser.replaceWithSelect(
			"javaScriptVersion",
			WebBrowser.JAVASCRIPT_VERSIONS,
			WebBrowser.JAVASCRIPT_VERSIONS);
		browser.replaceWithSelect(
			"markupVersion",
			WebBrowser.MARKUP_VERSIONS,
			WebBrowser.MARKUP_VERSIONS);
		browser.setWriteThrough(false);
		Button setbrowser =
			new Button("Set terminal information", browser, "commit");
		setbrowser.dependsOn(browser);

		// Arrange the UI in tabsheet
		TabSheet infoTabs = new TabSheet();
		addComponent(infoTabs);

		OrderedLayout appInfo = new OrderedLayout();
		infoTabs.addTab(appInfo, "Application",null);
		appInfo.addComponent(applicationInfo);
		appInfo.addComponent(controls);
		appInfo.addComponent(themeSelector);
		appInfo.addComponent(new Button("Change theme", this, "commitTheme"));
		

		OrderedLayout winInfo = new OrderedLayout();
		infoTabs.addTab(winInfo, "Windows",null);
		winInfo.addComponent(tabs);
		winInfo.addComponent(new Button("Save UIDL", this, "saveUIDL"));

		OrderedLayout termInfo = new OrderedLayout();
		infoTabs.addTab(termInfo, "Terminal",null);
		termInfo.addComponent(terminal);
		termInfo.addComponent(browser);
		termInfo.addComponent(setbrowser);

		// Set the debugged application
		setDebuggedApplication(debuggedApplication);

	}

	protected Select createSelect(
		String caption,
		Object[] keys,
		String[] names) {
		Select s = new Select(caption);
		s.addContainerProperty("name", String.class, "");
		for (int i = 0; i < keys.length; i++) {
			s.addItem(keys[i]).getItemProperty("name").setValue(names[i]);
		}
		s.setItemCaptionPropertyId("name");
		return s;
	}

	public void saveUIDL() {

		synchronized (rawUIDL) {

			String currentUIDL = (String) rawUIDL.get(tabs.getSelectedTab());

			if (currentUIDL == null)
				return;

			DateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
			File file =
				new File(
					"/uidl-debug"
						+ df.format(new Date(System.currentTimeMillis()))
						+ ".xml");
			try {
				BufferedWriter out =
					new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(file)));
				out.write(currentUIDL);
				out.close();

				//Open the UIDL also
				open(new FileResource(file, this.getApplication()));
				Log.info("UIDL written to file " + file);
			} catch (FileNotFoundException e) {
				Log.info("Failed to write debug to " + file + ": " + e);
			} catch (IOException e) {
				Log.info("Failed to write debug to " + file + ": " + e);
			}
		}
	}

	public void commitTheme() {
		themeSelector.commit();
	}

	public void clearSession() {
		session.invalidate();
	}

	public void restartApplication() {
		if (debuggedApplication != null)
			debuggedApplication.close();
	}

	protected void setWindowUIDL(Window window, String uidl) {
		String caption = "UIDL:" + window.getName();
		synchronized (tabs) {
			for (Iterator i = tabs.getComponentIterator(); i.hasNext();) {
				Component c = (Component) i.next();
				if (tabs.getTabCaption(c).equals(caption)) {
					((Label) c).setValue(getHTMLFormattedUIDL(caption, uidl));
					((Label) c).setContentMode(Label.CONTENT_XHTML);
					rawUIDL.put(c, uidl);
					caption = null;
				}
			}

			// Add new tab
			if (caption != null) {
				Label l = new Label(getHTMLFormattedUIDL(caption, uidl));
				l.setContentMode(Label.CONTENT_XHTML);
				rawUIDL.put(l, uidl);
				tabs.addTab(l, caption, null);
			}
		}
	}

	protected String getHTMLFormattedUIDL(String caption, String uidl) {
		StringBuffer sb = new StringBuffer();

		// Print formatted UIDL with errors embedded
		//Perl5Util util = new Perl5Util();

		int row = 0;
		int prev = 0;
		int index = 0;
		boolean lastLineWasEmpty = false;

		sb.append(
			"<TABLE WIDTH=\"100%\" STYLE=\"border-left: 1px solid black; "
				+ "border-right: 1px solid black; border-bottom: "
				+ "1px solid black; border-top: 1px solid black\""
				+ " cellpadding=\"0\" cellspacing=\"0\" BORDER=\"0\">");

		if (caption != null)
			sb.append(
				"<TR><TH BGCOLOR=\"#ddddff\" COLSPAN=\"2\">"
					+ "<FONT SIZE=\"+2\">"
					+ caption
					+ "</FONT></TH></TR>\n");

		boolean unfinished = true;
		while (unfinished) {
			row++;

			// Get individual line
			index = uidl.indexOf('\n', prev);
			String line;
			if (index < 0) {
				unfinished = false;
				line = uidl.substring(prev);
			} else {
				line = uidl.substring(prev, index);
				prev = index + 1;
			}

			// Escape the XML
			line = WebPaintTarget.escapeXML(line);

			// Code beautification : Comment lines
			line =
				replaceAll(
					line,
					"&lt;!--",
					"<SPAN STYLE = \"color: #00dd00\">&lt;!--");
			line = replaceAll(line, "--&gt;", "--&gt;</SPAN>");

			while (line.length() > 0 && line.charAt(0) == ' ') {
				line = line.substring(1);
			}
			boolean isEmpty = (line.length() == 0 || line.equals("\r"));
			line = " " + line;

			if (!(isEmpty && lastLineWasEmpty))
				sb.append(
					"<TR"
						+ ((row % 10) > 4 ? " BGCOLOR=\"#eeeeff\"" : "")
						+ ">"
						+ "<TD VALIGN=\"top\" ALIGN=\"rigth\" STYLE=\"border-right: 1px solid gray\"> "
						+ String.valueOf(row)
						+ " </TD><TD>"
						+ line
						+ "</TD></TR>\n");

			lastLineWasEmpty = isEmpty;

		}

		sb.append("</TABLE>\n");

		return sb.toString();
	}

	/**
	 * Replaces the characters in a substring of this <code>String</code>
	 * with characters in the specified <code>String</code>. The substring
	 * begins at the specified <code>start</code> and extends to the character
	 * at index <code>end - 1</code> or to the end of the
	 * <code>String</code> if no such character exists. First the
	 * characters in the substring are removed and then the specified
	 * <code>String</code> is inserted at <code>start</code>. (The
	 * <code>StringBuffer</code> will be lengthened to accommodate the
	 * specified String if necessary.)
	 * <p>
	 * NOTE: This operation is slow.
	 * </p>
	 * 
	 * @param      start    The beginning index, inclusive.
	 * @param      end      The ending index, exclusive.
	 * @param      str   String that will replace previous contents.
	 * @return     This string buffer.
	 * @exception  StringIndexOutOfBoundsException  if <code>start</code>
	 *             is negative, greater than <code>length()</code>, or
	 *		   greater than <code>end</code>.
	 */
	protected static String replace(
		String text,
		int start,
		int end,
		String str) {
		return new StringBuffer(text).replace(start, end, str).toString();
	}

	protected static String replaceAll(
		String text,
		String oldStr,
		String newStr) {
		StringBuffer sb = new StringBuffer(text);

		int newStrLen = newStr.length();
		int oldStrLen = oldStr.length();
		if (oldStrLen <= 0)
			return text;

		int i = 0;
		while (i <= sb.length() - oldStrLen) {
			if (sb.substring(i, i + oldStrLen).equals(oldStr)) {
				sb.replace(i, i + oldStrLen, newStr);
				i += newStrLen;
			} else {
				i++;
			}
		}
		return sb.toString();
	}

	/**
	 * Sets the application.
	 * @param application The application to set
	 */
	protected void setDebuggedApplication(Application application) {
		this.debuggedApplication = application;
		if (application != null) {
			applicationInfo.setValue(
				"<h2>Application Class</h2> "
					+ application.getClass().getName());
			themeSelector.setPropertyDataSource(
				new MethodProperty(application, "theme"));
		}
	}

	/**
	 * Returns the servlet.
	 * @return WebAdapterServlet
	 */
	protected WebAdapterServlet getServlet() {
		return servlet;
	}

	/**
	 * Returns the session.
	 * @return HttpSession
	 */
	protected HttpSession getSession() {
		return session;
	}

	/**
	 * Sets the servlet.
	 * @param servlet The servlet to set
	 */
	protected void setServlet(WebAdapterServlet servlet) {
		this.servlet = servlet;
	}

	/**
	 * Sets the session.
	 * @param session The session to set
	 */
	protected void setSession(HttpSession session) {
		this.session = session;
	}

}
