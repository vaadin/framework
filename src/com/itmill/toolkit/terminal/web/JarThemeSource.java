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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/** Theme source for reading themes from a JAR archive.
 *  At this time only jar files are supported and an archive
 *  may not contain any recursive archives.
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class JarThemeSource implements ThemeSource {

	private File file;
	private JarFile jar;
	private Theme theme;
	private String path;
	private String name;
	private WebAdapterServlet webAdapterServlet;
	private Cache resourceCache = new Cache();

	/** Collection of subdirectory entries */
	private Collection subdirs = new LinkedList();

	/** Creates a new instance of ThemeRepository by reading the themes
	 * from a local directory.
	 * @param file Path to the JAR archive .
	 * @param path Path inside the archive to be processed.
	 * @throws FileNotFoundException if no theme files are found
	 */
	public JarThemeSource(
		File file,
		WebAdapterServlet webAdapterServlet,
		String path)
		throws ThemeException, FileNotFoundException, IOException {

		this.file = file;
		this.jar = new JarFile(file);
		this.theme = null;
		this.path = path;
		if (this.path.length() > 0 && !this.path.endsWith("/")) {
			this.path = this.path + "/";
		}
		this.name = file.getName();
		if (this.name.toLowerCase().endsWith(".jar")) {
			this.name = this.name.substring(0, this.name.length() - 4);
		}


		this.webAdapterServlet = webAdapterServlet;

		// Load description file
		JarEntry entry = jar.getJarEntry(this.path + Theme.DESCRIPTIONFILE);
		if (entry != null) {
			try {
				this.theme = new Theme(jar.getInputStream(entry));
			} catch (Exception e) {
				throw new ThemeException(
					"JarThemeSource: Failed to load '" + path + "': ",e);
			}

			// Debug info
			if (webAdapterServlet.isDebugMode()) {
				Log.debug("Added JarThemeSource: " + this.file + ":" + this.path);
			}

		} else {
			// There was no description file found. 
			// Handle subdirectories recursively		
			for (Enumeration entries = jar.entries();
				entries.hasMoreElements();
				) {
				JarEntry e = (JarEntry) entries.nextElement();
				if (e.getName().startsWith(this.path)) {
					if (e.getName().endsWith("/")
						&& e.getName().indexOf('/', this.path.length())
							== (e.getName().length() - 1)) {
						this.subdirs.add(
							new JarThemeSource(
								this.file,
								this.webAdapterServlet,
								e.getName()));
					}
				}
			}

			if (this.subdirs.isEmpty()) {
				if (webAdapterServlet.isDebugMode()) {
					Log.info(
						"JarThemeSource: Ignoring empty JAR path: "
							+ this.file
							+ " path: "
							+ this.path);
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

			if (webAdapterServlet.isDebugMode()) {
				Log.info("JarThemeSource: Loading XSL from: " + theme);
			}

			// Reload the theme if JAR has been modified
			JarEntry entry = jar.getJarEntry(this.path + Theme.DESCRIPTIONFILE);
			if (entry != null) {
				try {
					this.theme = new Theme(jar.getInputStream(entry));
				} catch (IOException e) {
					throw new ThemeException(
						"Failed to read description: "
							+ this.file
							+ ":"
							+ this.path
							+ Theme.DESCRIPTIONFILE);
				}
			}

			Collection fileNames = theme.getFileNames(type);
			// Add all XSL file streams
			for (Iterator i = fileNames.iterator(); i.hasNext();) {
				entry = jar.getJarEntry(this.path + (String) i.next());
				try {
					xslFiles.add(new XSLStream(entry.getName(),jar.getInputStream(entry)));
				} catch (java.io.FileNotFoundException e) {
					throw new ThemeException(
						"XSL File not found: " + this.file + ": " + entry);
				} catch (java.io.IOException e) {
					throw new ThemeException(
						"Failed to read XSL file. " + this.file + ": " + entry);
				}
			}

		} else {

			// Handle subdirectories in archive: return the first match
			for (Iterator i = this.subdirs.iterator(); i.hasNext();) {
				ThemeSource source = (ThemeSource) i.next();
				if (source.getThemes().contains(theme))
					xslFiles.addAll(source.getXSLStreams(theme, type));
			}
		}

		return xslFiles;
	}

	/** Return modication time of the jar file.
	 * @see com.itmill.toolkit.terminal.web.ThemeSource#getModificationTime()
	 */
	public long getModificationTime() {
		return this.file.lastModified();
	}

	/**
	 * @see com.itmill.toolkit.terminal.web.ThemeSource#getResource(String)
	 */
	public InputStream getResource(String resourceId)
		throws ThemeSource.ThemeException {

		// Strip off the theme name prefix from resource id
		if (this.theme != null && 
				this.theme.getName() != null && 
				resourceId.startsWith(this.theme.getName()+"/")){
			resourceId = resourceId.substring(this.theme.getName().length()+1);
		}
		
		// Return the resource inside the jar file
		JarEntry entry = jar.getJarEntry(resourceId);
		if (entry != null)
			try {

				// Try cache
				byte[] data = (byte[]) resourceCache.get(entry);
				if (data != null)
					return new ByteArrayInputStream(data);

				// Read data
				int bufSize = 1024;
				ByteArrayOutputStream out = new ByteArrayOutputStream(bufSize);
				InputStream in = jar.getInputStream(entry);
				byte[] buf = new byte[bufSize];
				int n = 0;
				while ((n = in.read(buf)) >= 0) {
					out.write(buf, 0, n);
				}
				in.close();
				data = out.toByteArray();

				// Cache data
				resourceCache.put(entry, data);
				return new ByteArrayInputStream(data);
			} catch (IOException e) {
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
			return this.name;
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

	/**
	 * @author IT Mill Ltd.
		 * @version @VERSION@
		 * @since 3.0
		 */
	private class Cache {

		private Map data = new HashMap();

		public void put(Object key, Object value) {
			data.put(
				key,
				new SoftReference(new CacheItem(value, key.toString())));
		}

		public Object get(Object key) {
			SoftReference ref = (SoftReference) data.get(key);
			if (ref != null)
				return ((CacheItem) ref.get()).getData();
			return null;
		}

		public void clear() {
			data.clear();
		}
	}

	/**
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	private class CacheItem {

		private Object data;
		private String name;

		public CacheItem(Object data, String name) {
			this.name = name;
			this.data = data;
		}

		public Object getData() {
			return this.data;
		};

		public void finalize() throws Throwable {
			this.data = null;
			this.name = null;
			super.finalize();
		}

	}

}
