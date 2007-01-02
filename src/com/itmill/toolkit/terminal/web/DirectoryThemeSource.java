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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Theme source for reading themes from a directory on the Filesystem.
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class DirectoryThemeSource implements ThemeSource {

	private File path;
	private Theme theme;
	private ApplicationServlet webAdapterServlet;

	/** Collection of subdirectory entries */
	private Collection subdirs = new LinkedList();

	/** Creates a new instance of ThemeRepository by reading the themes
	 * from a local directory.
	 * @param path Path to the source directory .
	 * @param url External URL of the repository
	 * @throws FileNotFoundException if no theme files are found
	 */
	public DirectoryThemeSource(File path, ApplicationServlet webAdapterServlet)
		throws ThemeException, FileNotFoundException, IOException {

		this.path = path;
		this.theme = null;
		this.webAdapterServlet = webAdapterServlet;

		if (!this.path.isDirectory())
			throw new java.io.FileNotFoundException(
				"Theme path must be a directory ('" + this.path + "')");

		// Load description file
		File description = new File(path, Theme.DESCRIPTIONFILE);
		if (description.exists()) {
			try {
				this.theme = new Theme(description);
			} catch (Exception e) {
				throw new ThemeException(
					"ServletThemeSource: Failed to load '" + path,e);
			}

			// Debug info
			if (webAdapterServlet.isDebugMode(null)) {
				Log.info("Added DirectoryThemeSource: " + this.path);
			}

		} else {
			// There was no description file found. 
			// Handle subdirectories recursively		
			File[] files = this.path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					this.subdirs.add(
						new DirectoryThemeSource(files[i], webAdapterServlet));
				} else if (files[i].getName().toLowerCase().endsWith(".jar")) {
					this.subdirs.add(
						new JarThemeSource(files[i], webAdapterServlet, ""));
				}
			}

			if (this.subdirs.isEmpty()) {
				if (webAdapterServlet.isDebugMode(null)) {
					Log.debug(
						"DirectoryThemeSource: Ignoring empty directory: "
							+ path);
				}
			}
		}
	}

	/**
	 * @see com.itmill.toolkit.terminal.web.ThemeSource#getXSLStreams(Theme, WebBrowser)
	 */
	public Collection getXSLStreams(Theme theme, WebBrowser type)
		throws ThemeException {
		Collection xslFiles = new LinkedList();

		// If this directory contains a theme 
		// return XSL from this theme	
		if (this.theme != null) {

			if (webAdapterServlet.isDebugMode(null)) {
				Log.info("DirectoryThemeSource: Loading XSL from: " + theme);
			}

			// Reload the description file
			File description = new File(path, Theme.DESCRIPTIONFILE);
			if (description.exists()) {
				try {
					this.theme = new Theme(description);
				} catch (IOException e) {
					throw new ThemeException(
						"Failed to reload theme description" + e);
				}
			}

			Collection fileNames = theme.getFileNames(type, Theme.MODE_XSLT);

			// Add all XSL file streams
			for (Iterator i = fileNames.iterator(); i.hasNext();) {
				File f = new File(this.path, (String) i.next());
				if (f.getName().endsWith(".xsl"))
				try {
					xslFiles.add(new XSLStream(f.getName(),new FileInputStream(f)));
				} catch (FileNotFoundException e) {
					throw new ThemeException("XSL File not found: " + f);
				}
			}

		} else {

			// Handle subdirectories: return the first match
			for (Iterator i = this.subdirs.iterator(); i.hasNext();) {
				ThemeSource source = (ThemeSource) i.next();
				if (source.getThemes().contains(theme))
					xslFiles.addAll(source.getXSLStreams(theme, type));
			}
		}

		// Return the concatenated stream
		return xslFiles;

	}

	/**
	 * @see com.itmill.toolkit.terminal.web.ThemeSource#getModificationTime()
	 */
	public long getModificationTime() {

		long modTime = 0;

		// If this directory contains a theme 
		// return XSL from this theme	
		if (this.theme != null) {

			// Get modification time of the description file
			modTime = new File(this.path, Theme.DESCRIPTIONFILE).lastModified();

			// Get modification time of the themes directory itself
			if (this.path.lastModified() > modTime) {
				modTime = this.path.lastModified();
			}

			// Check modification time for all files
			Collection fileNames = theme.getFileNames();
			for (Iterator i = fileNames.iterator(); i.hasNext();) {
				File f = new File(this.path, (String) i.next());
				if (f.lastModified() > modTime) {
					modTime = f.lastModified();
				}
			}
		} else {
			// Handle subdirectories
			for (Iterator i = this.subdirs.iterator(); i.hasNext();) {
				ThemeSource source = (ThemeSource) i.next();
				long t = source.getModificationTime();
				if (t > modTime)
					modTime = t;
			}
		}

		return modTime;

	}

	/**
	 * @see com.itmill.toolkit.terminal.web.ThemeSource#getResource(String)
	 */
	public InputStream getResource(String resourceId)
		throws ThemeSource.ThemeException {

		// If this directory contains a theme 
		// return resource from this theme	
		if (this.theme != null) {

			try {
				return new FileInputStream(new File(this.path, resourceId));
			} catch (FileNotFoundException e) {
				throw new ThemeSource.ThemeException(
					"Resource " + resourceId + " not found.");
			}

		} else {
			int delim = resourceId.indexOf("/");
			String subResourceName = "";
			if (delim < resourceId.length() - 1)
				subResourceName = resourceId.substring(delim + 1);
			String subSourceName = resourceId.substring(0, delim);
			for (Iterator i = this.subdirs.iterator(); i.hasNext();) {
				ThemeSource source = (ThemeSource) i.next();
				if (source.getName().equals(subSourceName)) {
					return source.getResource(subResourceName);
				}
			}
		}

		throw new ThemeSource.ThemeException(
			"Resource " + resourceId + " not found.");

	}

	/**
	 * @see com.itmill.toolkit.terminal.web.ThemeSource#getThemes()
	 */
	public Collection getThemes() {
		Collection themes = new LinkedList();
		if (this.theme != null) {
			themes.add(this.theme);
		} else {
			for (Iterator i = this.subdirs.iterator(); i.hasNext();) {
				ThemeSource source = (ThemeSource) i.next();
				themes.addAll(source.getThemes());
			}

		}
		return themes;
	}

	/**
	 * @see com.itmill.toolkit.terminal.web.ThemeSource#getName()
	 */
	public String getName() {
		if (this.theme != null) {
			return this.theme.getName();
		} else {
			return this.path.getName();
		}
	}

	/**
	 * @see com.itmill.toolkit.terminal.web.ThemeSource#getThemeByName(String)
	 */
	public Theme getThemeByName(String name) {
		Collection themes = this.getThemes();
		for (Iterator i = themes.iterator(); i.hasNext();) {
			Theme t = (Theme) i.next();
			if (name != null && name.equals(t.getName()))
				return t;
		}
		return null;
	}

}
