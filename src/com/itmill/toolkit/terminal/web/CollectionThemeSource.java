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

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Theme source for consisting of collection of other theme sources. This class
 * is used to implement the retrieval of themes from multiple sources. Also this
 * class implements the inheritance of themes by first retrieving the relevant
 * parent theme information.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class CollectionThemeSource implements ThemeSource {

	private List sources = new LinkedList();

	/**
	 * @see com.itmill.toolkit.terminal.web.ThemeSource#getName()
	 */
	public String getName() {
		return "THEMES";
	}

	/**
	 * @see com.itmill.toolkit.terminal.web.ThemeSource#getXSLStreams(Theme,
	 *      WebBrowser)
	 */
	public Collection getXSLStreams(Theme theme, WebBrowser type)
			throws ThemeException {
		Collection xslFiles = new LinkedList();

		// Add parent theme XSL
		xslFiles.addAll(this.getParentXSLStreams(theme, type));

		// Add theme XSL, Handle subdirectories: return the first match
		for (Iterator i = this.sources.iterator(); i.hasNext();) {
			ThemeSource source = (ThemeSource) i.next();
			if (source.getThemes().contains(theme))
				xslFiles.addAll(source.getXSLStreams(theme, type));
		}

		return xslFiles;
	}

	private Collection getParentXSLStreams(Theme theme, WebBrowser type)
			throws ThemeException {
		Collection xslFiles = new LinkedList();
		String parentName = theme.getParent();
		if (parentName != null) {
			Theme parent = this.getThemeByName(parentName);
			if (parent != null) {
				xslFiles.addAll(this.getXSLStreams(parent, type));
			} else {
				throw new ThemeSource.ThemeException(
						"Parent theme not found for name: " + parentName);
			}
		}
		return xslFiles;
	}

	/**
	 * @see com.itmill.toolkit.terminal.web.ThemeSource#getModificationTime()
	 */
	public long getModificationTime() {
		long modTime = 0;
		for (Iterator i = this.sources.iterator(); i.hasNext();) {
			long t = ((ThemeSource) i.next()).getModificationTime();
			if (t > modTime)
				modTime = t;
		}
		return modTime;
	}

	/**
	 * @see com.itmill.toolkit.terminal.web.ThemeSource#getResource(String)
	 */
	public InputStream getResource(String resourceId) throws ThemeException {

		// Resolve theme name and resource name
		int delim = resourceId.indexOf("/");
		String subResourceId = "";
		String themeName = "";
		if (delim >= 0 && delim < resourceId.length() - 1) {
			subResourceId = resourceId.substring(delim + 1);
			themeName = resourceId.substring(0, delim);
		}

		// Get list of themes to look for the resource
		List themes = new LinkedList();
		while (themeName != null && themeName.length() > 0) {
			Theme t = this.getThemeByName(themeName);
			if (t != null) 
				themes.add(themeName);
			themeName = t.getParent();
		}

		// Iterate all themes in list
		for (Iterator ti = themes.iterator(); ti.hasNext();) {
			String name = (String) ti.next();
			String resource = name + "/" + subResourceId;
			// Search all sources
			for (Iterator i = this.sources.iterator(); i.hasNext();) {
				try {
					InputStream in = ((ThemeSource) i.next())
							.getResource(resource);
					if (in != null)
						return in;
				} catch (ThemeException e) {
					// Ignore and continue to next source
				}
			}
		}

		throw new ThemeException("Theme resource not found:" + subResourceId
				+ " in themes " + themes);
	}

	/**
	 * @see com.itmill.toolkit.terminal.web.ThemeSource#getThemes()
	 */
	public Collection getThemes() {
		Collection themes = new LinkedList();
		for (Iterator i = this.sources.iterator(); i.hasNext();) {
			Collection c = ((ThemeSource) i.next()).getThemes();
			themes.addAll(c);
		}
		return themes;
	}

	/**
	 * @see com.itmill.toolkit.terminal.web.ThemeSource#getThemeByName(String)
	 */
	public Theme getThemeByName(String name) {
		for (Iterator i = this.sources.iterator(); i.hasNext();) {
			Theme t = ((ThemeSource) i.next()).getThemeByName(name);
			if (t != null)
				return t;
		}
		return null;
	}

	/**
	 * Add new theme source to this collection.
	 * 
	 * @param source
	 *            Theme source to be added.
	 */
	public void add(ThemeSource source) {
		this.sources.add(source);
	}

}
