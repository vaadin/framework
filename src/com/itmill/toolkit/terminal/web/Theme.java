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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;

/**
 * This class provides an interface to the meta-information regarding a
 * particular theme. This entails for instanace the inheritance tree
 * of the various xsl-template files, the different requirments that the theme
 * imposes on the client browser, etc.
 * <p>
 * The WebAdapter uses themes to convert the UIDL description into client
 * representation, typically HTML or XHTML. A theme consists of set of XSL
 * template files which are used to perform XSL transform.
 * </p>
 * <p>
 * XSL files are divided into sets, which can have requirements. A file set is
 * included in transformation only if the given requirements are met. Following
 * requirements are supported:
 * <ul>
 * <li>User-Agent HTTP header substring matching</li>
 * <li>Markup language version</li>
 * <li>JavaScript version</li>
 * </ul>
 * Additionally following boolean operators may be applied to above
 * requirements:
 * <ul>
 * <li>NOT</li>
 * <li>AND</li>
 * <li>OR</li>
 * </ul>
 * The requirements are introduced in XML description file. See example below.
 * </p>
 * <p>
 * The theme description is XML data, and it can be loaded from file or stream.
 * The default filename is specified by <code>Theme.DESCRIPTIONFILE</code>.
 * 
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class Theme extends DefaultHandler {

	/** Default description file name. */
	public static final String DESCRIPTIONFILE = "description.xml";

	private static final String TAG_THEME = "theme";

	private static final String TAG_EXTENDS = "extends";

	private static final String TAG_DESCRIPTION = "description";

	private static final String TAG_FILE = "file";

	private static final String TAG_FILESET = "fileset";

	private static final String TAG_MODE = "mode";

	private static final String TAG_MODES = "modes";

	private static final String TAG_REQUIRE = "require";

	private static final String TAG_SUPPORTS = "supports";

	private static final String TAG_AUTHOR = "author";

	private static final String TAG_AND = "and";

	private static final String TAG_OR = "or";

	private static final String TAG_NOT = "not";

	private static final String ATTR_NAME = "name";

	private static final String ATTR_THEME = "theme";

	private static final String ATTR_EMAIL = "email";

	private static final String ATTR_MODE = "mode";

	private static final String ATTR_JAVASCRIPT = "javascript";

	private static final String ATTR_AGENT = "agent";

	private static final String ATTR_MARKUP = "markup";

	public static final String MODE_AJAX = "ajax";

	public static final String MODE_HTML = "html";

	public static final String MODE_FALLBACK = MODE_HTML;

	/** Name of the theme. */
	private String name;

	/** Theme description. */
	private String description;

	/** Author of the theme. */
	private Author author;

	/** Name of the theme, which this theme extends */
	private String parentTheme = null;

	/** Fileset of included XSL files. */
	private Fileset files = null;

	/** Stack of fileset used while parsing XML. */
	private Stack openFilesets = new Stack();

	/** Stack of string buffers used while parsing XML. */
	private Stack openStrings = new Stack();

	/** Supported modes name-to-requirements */
	private LinkedHashMap supportedModes = new LinkedHashMap();

	/** Currently open mode */
	private String currentlyOpenMode = null;

	/** Are we processing modes */
	private boolean modesListCurrentlyOpen = false;

	/** Is a NOT requirement element open. */
	private boolean isNOTRequirementOpen = false;

	/** Currently open requirements while parsing. */
	private Stack openRequirements = new Stack();

	/**
	 * Creates a new instance using XML description file. Instantiate new theme,
	 * by loading the description from given File.
	 * 
	 * @param descriptionFile
	 *            Description file
	 * @throws FileNotFoundException
	 *             Thrown if the given file is not found.
	 */
	public Theme(java.io.File descriptionFile) throws FileNotFoundException {
		parse(new InputSource(new FileInputStream(descriptionFile)));
	}

	/**
	 * Creates a new instance using XML description stream. Instantiate new
	 * theme, by loading the description from given InputSource.
	 * 
	 * @param descriptionStream
	 *            XML input to parse
	 */
	public Theme(InputStream descriptionStream) {
		try {
			parse(new InputSource(descriptionStream));
		} finally {
			try {
				descriptionStream.close();
			} catch (IOException ignored) {
			}
		}
	}

	/**
	 * Get the preferred operating mode supported by this theme for given
	 * terminal.
	 */
	public String getPreferredMode(WebBrowser terminal, ThemeSource themeSource) {

		// If no supported modes are declared, then we use parents preferred
		// mode
		if (parentTheme != null
				&& supportedModes.keySet().isEmpty()) {
			Theme parent = themeSource.getThemeByName(parentTheme);
			if (parent == null)
				throw new IllegalStateException("Parent theme '"+parentTheme+"' is not found for theme '"+getName()+"'.");
			return parent.getPreferredMode(terminal, themeSource);
		}
		
		// Iterate and test the modes in order
		for (Iterator i = supportedModes.keySet().iterator(); i.hasNext();) {
			String mode = (String) i.next();
			if (supportsMode(mode, terminal,themeSource))
				return mode;
		}

		return null;
	}

	/** Tests if this theme suppors given mode */
	public boolean supportsMode(String mode, WebBrowser terminal, ThemeSource themeSource) {

		// Theme must explicitly support the given mode
		RequirementCollection rc = (RequirementCollection) supportedModes
				.get(mode);
		
		if (rc == null || !rc.isMet(terminal))
			return false;

		// All parents must also support the mode
		if (parentTheme != null) {
			Theme parent = themeSource.getThemeByName(parentTheme);
			if (parent == null)
				throw new IllegalStateException("Parent theme '"+parentTheme+"' is not found for theme '"+getName()+"'.");
			if(!parent.supportsMode(mode, terminal, themeSource))
			return false;
		}

		return true;
	}

	/**
	 * Parse XML data.
	 * 
	 * @param descriptionSource
	 *            XML input source to parse
	 */
	private synchronized void parse(InputSource descriptionSource) {

		// Clean-up parse time data
		this.openStrings.clear();
		this.openFilesets.clear();
		this.openRequirements.clear();
		this.files = null;

		// Parse the Document
		try {
			XMLReader xr = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();

			xr.setContentHandler(this);
			xr.setErrorHandler(this);

			xr.parse(descriptionSource);

			return;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.getException().printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}

	}

	/**
	 * Parse start tag in XML stream.
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String local, String qName,
			Attributes atts) {

		if (TAG_THEME.equals(qName)) {
			this.name = atts.getValue(ATTR_NAME);
		} else if (TAG_DESCRIPTION.equals(qName)) {
			this.description = "(none)";
			this.openStrings.push(new StringBuffer());
		} else if (TAG_EXTENDS.equals(qName)) {
			String themeName = atts.getValue(ATTR_THEME);
			if (this.name.equals(themeName))
				throw new IllegalArgumentException("Theme " + this.name
						+ " extends itself.");
			if (parentTheme != null)
				throw new IllegalArgumentException("Only one extends statement is allowed");
			this.parentTheme = themeName;
		} else if (TAG_FILE.equals(qName)) {
			File f = new File(atts.getValue(ATTR_NAME));
			if (this.openFilesets.isEmpty()) {
				throw new IllegalStateException("Element '" + TAG_FILE
						+ "' must be within '" + TAG_FILESET + "' element.");
			}
			Fileset fs = (Fileset) this.openFilesets.peek();
			fs.addFile(f);
		} else if (TAG_FILESET.equals(qName)) {
			Fileset fs;
			String mode = atts.getValue(ATTR_MODE);
			if (mode != null && mode.length() == 0)
				mode = null;
			if (mode != null && !mode.equals(MODE_AJAX)
					&& !mode.equals(MODE_HTML))
				throw new IllegalStateException("Given mode '" + mode
						+ "' is not supported. (This version only supports '"
						+ MODE_HTML + "' and '" + MODE_AJAX + "')");
			fs = new Fileset(mode);

			// Use the first fileset as root fileset
			if (this.files == null) {
				this.files = fs;
			}

			// Add inner filesets to parent
			if (!this.openFilesets.isEmpty()) {
				((Fileset) this.openFilesets.peek()).addFile(fs);
			}

			this.openFilesets.push(fs);
		} else if (TAG_AUTHOR.equals(qName)) {
			this.author = new Author(atts.getValue(ATTR_NAME), atts
					.getValue(ATTR_EMAIL));
		} else if (TAG_MODES.equals(qName)) {
			if (modesListCurrentlyOpen)
				throw new IllegalStateException(
						"Modes element can not be inside another modes element");
			modesListCurrentlyOpen = true;
		} else if (TAG_MODE.equals(qName)) {
			if (!modesListCurrentlyOpen)
				throw new IllegalStateException(
						"Mode elements must be placed inside modes element");
			if (currentlyOpenMode != null)
				throw new IllegalStateException(
						"No mode is allowed inside mode");
			String name = atts.getValue(ATTR_NAME);
			if (name == null || name.length() == 0)
				throw new IllegalStateException(
						"Name is required for mode elements");
			this.currentlyOpenMode = name;
			RequirementCollection rc = new AndRequirement();
			supportedModes.put(name, rc);
		}
		// Requirements
		else if (TAG_REQUIRE.equals(qName)) {
			if (currentlyOpenMode != null) {
				RequirementCollection rc = (RequirementCollection) supportedModes
						.get(this.currentlyOpenMode);
				if (rc == null)
					throw new IllegalStateException("Tried to add requirements to mode '" + name + "', but requirements set was not properly created. (internal error)");
				this.openRequirements.push(rc);
			} else {
				if (this.openFilesets.isEmpty()) {
					throw new IllegalStateException("Element '" + TAG_REQUIRE
							+ "' must be within '" + TAG_FILESET + "' element.");
				}
				Fileset fs = (Fileset) this.openFilesets.peek();
				this.openRequirements.push(fs.getRequirements());
			}
		} else if (TAG_SUPPORTS.equals(qName)) {
			if (this.openFilesets.isEmpty() && currentlyOpenMode == null) {
				throw new IllegalStateException("Element '" + TAG_REQUIRE
						+ "' must be within '" + TAG_FILESET + "' element.");
			}
			if (this.openRequirements.isEmpty()) {
				throw new IllegalStateException("Element '" + TAG_SUPPORTS
						+ "' must be within '" + TAG_REQUIRE + "' element.");
			}
			this.addRequirements(atts,
					(RequirementCollection) this.openRequirements.peek(),
					this.isNOTRequirementOpen);
		} else if (TAG_NOT.equals(qName)) {
			this.isNOTRequirementOpen = true;
		} else if (TAG_AND.equals(qName)) {
			this.openRequirements.push(new AndRequirement());
		} else if (TAG_OR.equals(qName)) {
			this.openRequirements.push(new OrRequirement());
		}
	}

	/**
	 * Parse end tag in XML stream.
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {

		if (TAG_FILESET.equals(qName)) {
			this.openFilesets.pop();
		} else if (TAG_DESCRIPTION.equals(qName)) {
			this.description = ((StringBuffer) this.openStrings.pop())
					.toString();
		} else if (TAG_REQUIRE.equals(qName)) {
			this.openRequirements.pop();
		} else if (TAG_NOT.equals(qName)) {
			this.isNOTRequirementOpen = false;
		} else if (TAG_MODES.equals(qName)) {
			this.modesListCurrentlyOpen = false;
		} else if (TAG_MODE.equals(qName)) {
			this.currentlyOpenMode = null;
		} else if (TAG_OR.equals(qName) || TAG_AND.equals(qName)) {
			RequirementCollection r = (RequirementCollection) openRequirements.pop();
			if (openRequirements.size() < 1)
				throw new IllegalStateException();
			((RequirementCollection)openRequirements.peek()).addRequirement(r);
		}
	}

	/**
	 * Parse character data in XML stream.
	 * 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] data, int start, int length) {

		// if stack is not ready, data is not content of recognized element
		if (!this.openStrings.isEmpty()) {
			((StringBuffer) this.openStrings.peek())
					.append(data, start, length);
		} else {
			// read data which is not part of recognized element
		}
	}

	/**
	 * Add all requirements specified in attributes to fileset.
	 * 
	 * @param atts
	 *            Attribute set
	 * @param requirements
	 *            Collection where to add requirement rules.
	 * @param applyNot
	 *            Should the meaning of these requirement be negated.
	 */
	private void addRequirements(Attributes atts,
			RequirementCollection requirements, boolean applyNot) {

		// Create temporary collection for requirements
		Collection tmpReqs = new LinkedList();
		Requirement req = null;

		for (int i = 0; i < atts.getLength(); i++) {
			req = null;
			if (ATTR_JAVASCRIPT.equals(atts.getQName(i))) {
				req = new JavaScriptRequirement(WebBrowser
						.parseJavaScriptVersion(atts.getValue(i)));
			} else if (ATTR_AGENT.equals(atts.getQName(i))) {
				req = new AgentRequirement(atts.getValue(i));
			} else if (ATTR_MARKUP.equals(atts.getQName(i))) {
				req = new MarkupLanguageRequirement(WebBrowser
						.parseHTMLVersion(atts.getValue(i)));
			}
			// Add to temporary requirement collection and clear reference
			if (req != null)
				tmpReqs.add(req);
		}

		// Create implicit AND requirement if more than one
		// Rrequirements were specified in attributes
		if (tmpReqs.size() > 1) {
			req = new AndRequirement(tmpReqs);
		}

		// Apply NOT rule if requested
		if (applyNot) {
			req = new NotRequirement(req);
		}

		// Add to requirements
		requirements.addRequirement(req);
	}

	/**
	 * Get list of all files in this theme.
	 * 
	 * @return List of filenames belonging to this theme.
	 */
	public List getFileNames() {
		if (files == null)
			return new LinkedList();
		return files.getFileNames();
	}

	/**
	 * Get list of file names matching WebBrowserType.
	 * 
	 * @return list of filenames in this theme supporting the given terminal.
	 */
	public List getFileNames(WebBrowser terminal, String mode) {
		if (files == null)
			return new LinkedList();
		return this.files.getFileNames(terminal, mode);
	}

	/**
	 * String representation of Theme object. Used for debugging purposes only.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.name + " author='" + this.author + "'" + (parentTheme != null ? " inherits='"
				+ parentTheme + "'" : "" )+ " files={"
				+ (files != null ? files.toString() : "null") + "}";
	}

	/**
	 * Author information class. This class represents an single author of a
	 * theme package. Authors have name and contact email address properties.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class Author {

		private String name;

		private String email;

		public Author(String name, String email) {
			this.name = name;
			this.email = email;
		}

		/**
		 * Get the name of the author.
		 * 
		 * @return Name of the author.
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Get the email address of the author.
		 * 
		 * @return Email address of the author.
		 */
		public String getEmail() {
			return this.email;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return name + "(" + email + ")";
		}
	}

	/**
	 * Generic requirement. Interface implemented by reuirements introducing
	 * method for checking compability with given terminal.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public interface Requirement {

		/**
		 * Check that this requirement is met by given type of browser.
		 * 
		 * @param terminal
		 *            type of the web browser.
		 * @return True if terminal is compatible with this rule. False
		 *         otherwise.
		 */
		public boolean isMet(WebBrowser terminal);

	}

	/**
	 * Generic requirement collection interface. Requirement collection
	 * introducing methods for combining requirements into single requirement.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public interface RequirementCollection extends Requirement {

		/**
		 * Add new requirement to this collection.
		 * 
		 * @param requirement
		 *            Requirement to be added.
		 */
		public void addRequirement(Requirement requirement);

		/**
		 * Remove a requirement from this collection.
		 * 
		 * @param requirement
		 *            Requirement to be removed.
		 */
		public void removeRequirement(Requirement requirement);
	}

	/**
	 * Logical NOT requirement. Requirement implementing logical NOT operation.
	 * Wraps an another requirement and negates the meaning of it.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class NotRequirement implements Requirement {
		private Requirement requirement;

		/**
		 * Create new NOT requirement based on another requirement.
		 * 
		 * @param requirement
		 *            The requirement to ne negated.
		 */
		public NotRequirement(Requirement requirement) {
			this.requirement = requirement;
		}

		/**
		 * Check that this requirement is met by given type of browser.
		 * 
		 * @param terminal
		 *            type of the web browser.
		 * @return True if terminal is compatible with this rule. False
		 *         otherwise.
		 */
		public boolean isMet(WebBrowser terminal) {
			return !this.requirement.isMet(terminal);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "not(" + requirement + ")";
		}

	}

	/**
	 * Logical AND requirement. Implements a collection of requirements
	 * combining the included requirements using logical AND operation.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class AndRequirement implements RequirementCollection {

		private Collection requirements = new LinkedList();

		public AndRequirement() {
		}

		public AndRequirement(Collection requirements) {
			this.requirements.addAll(requirements);
		}

		public AndRequirement(Requirement req1, Requirement req2) {
			this.addRequirement(req1);
			this.addRequirement(req2);
		}

		public void addRequirement(Requirement requirement) {
			this.requirements.add(requirement);
		}

		public void removeRequirement(Requirement requirement) {
			this.requirements.remove(requirement);
		}

		/**
		 * Checks that all os the requirements in this collection are met.
		 * 
		 * @see Theme.Requirement#isMet(WebBrowser)
		 */
		public boolean isMet(WebBrowser terminal) {
			for (Iterator i = this.requirements.iterator(); i.hasNext();) {
				if (!((Requirement) i.next()).isMet(terminal)) {
					return false;
				}
			}
			return true;
		}

		public String toString() {
			String str = "";
			for (Iterator i = this.requirements.iterator(); i.hasNext();) {
				if (!"".equals(str))
					str += " AND ";
				str += "(" + ((Requirement) i.next()).toString() + ")";
			}
			return str;
		}

	}

	/**
	 * Logical OR requirement. Implements a collection of requirements combining
	 * the included requirements using logical AND operation.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class OrRequirement implements RequirementCollection {

		private Collection requirements = new LinkedList();

		public OrRequirement() {
		}

		public OrRequirement(Collection requirements) {
			this.requirements.addAll(requirements);
		}

		public OrRequirement(Requirement req1, Requirement req2) {
			this.addRequirement(req1);
			this.addRequirement(req2);
		}

		public void addRequirement(Requirement requirement) {
			this.requirements.add(requirement);
		}

		public void removeRequirement(Requirement requirement) {
			this.requirements.remove(requirement);
		}

		/**
		 * Checks that some of the requirements in this collection is met.
		 * 
		 * @see Theme.Requirement#isMet(WebBrowser)
		 */
		public boolean isMet(WebBrowser terminal) {
			for (Iterator i = this.requirements.iterator(); i.hasNext();) {
				if (((Requirement) i.next()).isMet(terminal)) {
					return true;
				}
			}
			return false;
		}

		public String toString() {
			String str = "";
			for (Iterator i = this.requirements.iterator(); i.hasNext();) {
				if (!"".equals(str))
					str += " OR ";
				str += "(" + ((Requirement) i.next()).toString() + ")";
			}
			return str;
		}
	}

	/**
	 * HTTP user agent requirement This requirements is used to ensure that the
	 * User-Agent string provided in HTTP request headers contains given
	 * substring.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class AgentRequirement implements Requirement {

		private String agentSubstring;

		public AgentRequirement(String agentSubString) {
			this.agentSubstring = agentSubString;
		}

		public boolean isMet(WebBrowser terminal) {
			return terminal.getBrowserApplication().indexOf(this.agentSubstring) >= 0;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return this.agentSubstring;
		}
	}

	/**
	 * Javascript version requirement This requirement is used to ensure a
	 * certain level of JavaScript version support.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class JavaScriptRequirement implements Requirement {

		private WebBrowser.JavaScriptVersion requiredVersion;

		public JavaScriptRequirement(
				WebBrowser.JavaScriptVersion requiredVersion) {
			this.requiredVersion = requiredVersion;
		}

		public boolean isMet(WebBrowser terminal) {
			if (terminal.getJavaScriptVersion().supports(this.requiredVersion))
				return true;
			return false;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return this.requiredVersion.toString();
		}
	}

	/**
	 * Markup language version requirement This requirement is used to ensure a
	 * certain level of Markup language version support.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class MarkupLanguageRequirement implements Requirement {

		private WebBrowser.MarkupVersion requiredVersion;

		public MarkupLanguageRequirement(
				WebBrowser.MarkupVersion requiredVersion) {
			this.requiredVersion = requiredVersion;
		}

		public boolean isMet(WebBrowser terminal) {
			if (terminal.getMarkupVersion().supports(this.requiredVersion))
				return true;
			return false;

		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return this.requiredVersion.toString();
		}

	}

	/**
	 * Theme XSL file description Description of a single XSL file included a
	 * theme.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class File {

		private String name;

		/**
		 * Create new file.
		 * 
		 * @param name
		 *            Name of the file.
		 */
		public File(String name) {
			this.name = name;
		}

		/**
		 * Get name of the file. The file name is relative and unique within a
		 * theme.
		 * 
		 * @return Name of the file.
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Does this file support the given terminal. Single file requirements
		 * are not supported and therefore this always returns true.
		 * 
		 * @see Theme.Fileset
		 * @return Always returns true.
		 */
		public boolean supports(WebBrowser terminal) {
			return true;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return this.getName();
		}

	}

	/**
	 * A recursive set of files sharing the same requirements.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class Fileset extends File {

		private RequirementCollection requirements = new AndRequirement();

		private Collection files = new LinkedList();

		private String mode;

		/**
		 * Create new empty fileset.
		 * 
		 * @param name
		 *            Name of the fileset.
		 */
		public Fileset(String mode) {
			super(null);
			this.mode = mode;
		}

		/** Add a file into fileset. */
		private void addFile(File file) {
			this.files.add(file);
		}

		/** Get requirements in this fileset. */
		private RequirementCollection getRequirements() {
			return this.requirements;
		}

		/**
		 * Get list of all files in this theme.
		 * 
		 * @return list of filenames.
		 */
		public List getFileNames() {

			List list = new LinkedList();

			for (Iterator i = this.files.iterator(); i.hasNext();) {
				File f = (File) i.next();

				// Recursively add included filesets
				if (f instanceof Fileset) {
					list.addAll(((Fileset) f).getFileNames());
				} else {
					list.add(f.getName());
				}
			}
			return list;

		}

		/**
		 * Get list of file names matching WebBrowserType.
		 * 
		 * @return list of filenames supporting the given terminal.
		 */
		public List getFileNames(WebBrowser terminal, String mode) {

			List list = new LinkedList();

			// If this set is not supported by the terminal or is explicitly set
			// into
			// another mode, no files are given
			if (!this.supports(terminal)
					|| (this.mode != null && !this.mode.equals(mode)))
				return list;

			for (Iterator i = this.files.iterator(); i.hasNext();) {
				File f = (File) i.next();

				// Recursively add included filesets if they are
				// supported
				if (f instanceof Fileset) {
					list.addAll(((Fileset) f).getFileNames(terminal, mode));

				} else {
					list.add(f.getName());
				}
			}
			return list;
		}

		/**
		 * Does this file support the given terminal.
		 * 
		 * @return True if fileset supports the given browser. False otherwise.
		 */
		public boolean supports(WebBrowser terminal) {
			if (requirements.isMet(terminal))
				return true;
			return false;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "name=[" + this.getName() + "] requires=["
					+ this.requirements + "] files=[" + files + "]";
		}
	}

	/**
	 * Returns the author of this theme.
	 * 
	 * @return Author of the theme.
	 */
	public Author getAuthor() {
		return author;
	}

	/**
	 * Returns the name of this theme.
	 * 
	 * @return Name of the theme.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the name of the parent theme.
	 * 
	 */
	public String getParent() {
		return parentTheme;
	}

	/** Get theme description */
	public String getDescription() {
		return description;
	}

}
