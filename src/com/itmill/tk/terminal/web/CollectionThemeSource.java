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

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/** Theme source for consisting of collection of other theme sources.
 * This class is used to implement the retrieval of themes
 * from multiple sources. Also this class implements the inheritance 
 * of themes by first retrieving the relevant parent theme information.
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class CollectionThemeSource implements ThemeSource {

	private List sources = new LinkedList();

	/**
	 * @see com.itmill.tk.terminal.web.ThemeSource#getName()
	 */
	public String getName() {
		return "THEMES";
	}

	/**
	 * @see com.itmill.tk.terminal.web.ThemeSource#getXSLStreams(Theme, WebBrowser)
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
		Collection parents = theme.getParentThemes();
		for (Iterator i = parents.iterator(); i.hasNext();) {
			String name = (String) i.next();
			Theme parent = this.getThemeByName(name);
			if (parent != null) {
				xslFiles.addAll(this.getXSLStreams(parent, type));
			} else {
				throw new ThemeSource.ThemeException(
					"Parent theme not found for name: " + name);
			}
		}
		return xslFiles;
	}

	/**
	 * @see com.itmill.tk.terminal.web.ThemeSource#getModificationTime()
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
	 * @see com.itmill.tk.terminal.web.ThemeSource#getResource(String)
	 */
	public InputStream getResource(String resourceId) throws ThemeException {

		// Resolve theme name and resource name
		int delim = resourceId.indexOf("/");
		String subResourceId = "";
		String themeName = "";
		if (delim >=0 && delim < resourceId.length() - 1) {
			subResourceId = resourceId.substring(delim + 1);
			themeName = resourceId.substring(0, delim);
		}

		// Get list of themes to look for the resource
		List themes = new LinkedList();
		if (themeName.length() > 0) {
			Theme t = this.getThemeByName(themeName);
			if (t != null) {
				themes.add(t.getName());
				addAllParents(themes, t);
			}
		}

		// Iterate all themes in list
		for (Iterator ti = themes.iterator(); ti.hasNext();) {
			String name = (String) ti.next();
			String resource = name + "/" + subResourceId;
			// Search all sources
			for (Iterator i = this.sources.iterator(); i.hasNext();) {
				try {
					InputStream in =
						((ThemeSource) i.next()).getResource(resource);
					if (in != null)
						return in;
				} catch (ThemeException e) {
					// Ignore and continue to next source
				}
			}
		}

		throw new ThemeException(
			"Theme resource not found:"
				+ subResourceId
				+ " in themes "
				+ themes);
	}
	/** Recusrivelu get list of parent themes in inheritace order.
	 *  
	 * @param t Theme which parents should be listed
	 * @return Collection of themes in inheritance order.
	 */
	private void addAllParents(List list, Theme t) {
		if (t == null)
			return;

		List parents = t.getParentThemes();
		list.addAll(parents);
		for (Iterator i = parents.iterator(); i.hasNext();) {
			addAllParents(list, this.getThemeByName((String) i.next()));
		}
	}

	/**
	 * @see com.itmill.tk.terminal.web.ThemeSource#getThemes()
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
	 * @see com.itmill.tk.terminal.web.ThemeSource#getThemeByName(String)
	 */
	public Theme getThemeByName(String name) {
		for (Iterator i = this.sources.iterator(); i.hasNext();) {
			Theme t = ((ThemeSource) i.next()).getThemeByName(name);
			if (t != null)
				return t;
		}
		return null;
	}

	/**Add new theme source to this collection.
	 * @param source Theme source to be added.
	 */
	public void add(ThemeSource source) {
		this.sources.add(source);
	}

}
