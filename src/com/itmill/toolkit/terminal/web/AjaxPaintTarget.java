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

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ApplicationResource;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Paintable;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.terminal.UploadStream;
import com.itmill.toolkit.terminal.VariableOwner;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Stack;

/**
 * User Interface Description Language Target.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.1
 */
public class AjaxPaintTarget implements PaintTarget {

	/* Document type declarations */
	private final static String UIDL_XML_DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	private final static String UIDL_ARG_NAME = "name";

	private final static String UIDL_ARG_VALUE = "value";

	private final static String UIDL_ARG_ID = "id";

	private Stack mOpenTags;

	private boolean mTagArgumentListOpen;

	private PrintWriter uidlBuffer;

	private AjaxVariableMap variableMap;

	private boolean closed = false;

	private AjaxApplicationManager manager;

	private boolean trackPaints = false;

	private int numberOfPaints = 0;

	/**
	 * Creates a new XMLPrintWriter, without automatic line flushing.
	 * 
	 * @param variableMap
	 * @param manager
	 * @param output
	 *            A character-output stream.
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public AjaxPaintTarget(AjaxVariableMap variableMap,
			AjaxApplicationManager manager, OutputStream output)
			throws PaintException {

		// Sets the cache
		this.manager = manager;

		// Sets the variable map
		this.variableMap = variableMap;

		// Sets the target for UIDL writing
		try {
			this.uidlBuffer = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(output, "UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Internal error");
		}

		// Initialize tag-writing
		mOpenTags = new Stack();
		mTagArgumentListOpen = false;

		// Adds document declaration
		this.print(UIDL_XML_DECL + "\n\n");

		// Adds UIDL start tag and its attributes
		this.startTag("changes");

	}

	/**
	 * Ensures that the currently open element tag is closed.
	 */
	private void ensureClosedTag() {
		if (mTagArgumentListOpen) {
			append(">");
			mTagArgumentListOpen = false;
		}
	}

	/**
	 * Method append.This method is thread safe.
	 * 
	 * @param string
	 *            the text to insert.
	 */
	private void append(String string) {
		uidlBuffer.print(string);
	}

	/**
	 * Prints the element start tag.
	 * 
	 * <pre>
	 * Todo:
	 *  Checking of input values
	 *  
	 * </pre>
	 * 
	 * @param tagName
	 *            the name of the start tag.
	 * @throws PaintException
	 *             if the paint operation failed.
	 * 
	 */
	public void startTag(String tagName) throws PaintException {
		// In case of null data output nothing:
		if (tagName == null)
			throw new NullPointerException();

		// Increments paint tracker
		if (this.isTrackPaints()) {
			this.numberOfPaints++;
		}

		// Ensures that the target is open
		if (this.closed)
			throw new PaintException(
					"Attempted to write to a closed PaintTarget.");

		// Makes sure that the open start tag is closed before
		// anything is written.
		ensureClosedTag();

		// Checks tagName and attributes here
		mOpenTags.push(tagName);

		// Prints the tag with attributes
		append("<" + tagName);

		mTagArgumentListOpen = true;
	}

	/**
	 * Prints the element end tag.
	 * 
	 * If the parent tag is closed before every child tag is closed an
	 * PaintException is raised.
	 * 
	 * @param tag
	 *            the name of the end tag.
	 * @throws Paintexception
	 *             if the paint operation failed.
	 */
	public void endTag(String tagName) throws PaintException {
		// In case of null data output nothing:
		if (tagName == null)
			throw new NullPointerException();

		// Ensure that the target is open
		if (this.closed)
			throw new PaintException(
					"Attempted to write to a closed PaintTarget.");

		String lastTag = "";

		lastTag = (String) mOpenTags.pop();
		if (!tagName.equalsIgnoreCase(lastTag))
			throw new PaintException("Invalid UIDL: wrong ending tag: '"
					+ tagName + "' expected: '" + lastTag + "'.");

		// Make sure that the open start tag is closed before
		// anything is written.
		if (mTagArgumentListOpen) {
			append(">");
			mTagArgumentListOpen = false;
		}

		// Writes the end (closing) tag
		append("</" + lastTag + ">");
		flush();
	}

	/**
	 * Substitutes the XML sensitive characters with predefined XML entities.
	 * 
	 * @param xml
	 *            the String to be substituted.
	 * @return A new string instance where all occurrences of XML sensitive
	 *         characters are substituted with entities.
	 */
	static public String escapeXML(String xml) {
		if (xml == null || xml.length() <= 0)
			return "";
		return escapeXML(new StringBuffer(xml)).toString();
	}

	/**
	 * Substitutes the XML sensitive characters with predefined XML entities.
	 * 
	 * @param xml
	 *            the String to be substituted.
	 * @return A new StringBuffer instance where all occurrences of XML
	 *         sensitive characters are substituted with entities.
	 * 
	 */
	static public StringBuffer escapeXML(StringBuffer xml) {
		if (xml == null || xml.length() <= 0)
			return new StringBuffer("");

		StringBuffer result = new StringBuffer(xml.length() * 2);

		for (int i = 0; i < xml.length(); i++) {
			char c = xml.charAt(i);
			String s = toXmlChar(c);
			if (s != null) {
				result.append(s);
			} else {
				result.append(c);
			}
		}
		return result;
	}

	/**
	 * Substitutes a XML sensitive character with predefined XML entity.
	 * 
	 * @param c
	 *            the Character to be replaced with an entity.
	 * @return String of the entity or null if character is not to be replaced
	 *         with an entity.
	 */
	private static String toXmlChar(char c) {
		switch (c) {
		case '&':
			return "&amp;"; // & => &amp;
		case '>':
			return "&gt;"; // > => &gt;
		case '<':
			return "&lt;"; // < => &lt;
		case '"':
			return "&quot;"; // " => &quot;
		case '\'':
			return "&apos;"; // ' => &apos;
		default:
			return null;
		}
	}

	/**
	 * Prints XML.
	 * 
	 * Writes pre-formatted XML to stream. Well-formness of XML is checked.
	 * 
	 * <pre>
	 * 
	 *  TODO: XML checking should be made
	 *  
	 * </pre>
	 * 
	 * @param str
	 *            the string to print.
	 */
	private void print(String str) {
		// In case of null data output nothing:
		if (str == null)
			return;

		// Make sure that the open start tag is closed before
		// anything is written.
		ensureClosedTag();

		// Write what was given
		append(str);
	}

	/**
	 * Prints XML-escaped text.
	 * 
	 * @param str
	 * @throws PaintException
	 *             if the paint operation failed.
	 * 
	 */
	public void addText(String str) throws PaintException {
		addUIDL(escapeXML(str));
	}

	/**
	 * Adds a boolean attribute to component. Atributes must be added before any
	 * content is written.
	 * 
	 * @param name
	 *            the Attribute name.
	 * @param value
	 *            the Attribute value.
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void addAttribute(String name, boolean value) throws PaintException {
		addAttribute(name, String.valueOf(value));
	}

	/**
	 * Adds a resource attribute to component. Atributes must be added before
	 * any content is written.
	 * 
	 * @param name
	 *            the Attribute name.
	 * @param value
	 *            the Attribute value.
	 * 
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void addAttribute(String name, Resource value) throws PaintException {

		if (value instanceof ExternalResource) {
			addAttribute(name, ((ExternalResource) value).getURL());

		} else if (value instanceof ApplicationResource) {
			ApplicationResource r = (ApplicationResource) value;
			Application a = r.getApplication();
			if (a == null)
				throw new PaintException(
						"Application not specified for resorce "
								+ value.getClass().getName());
			String uri = a.getURL().getPath();
			if (uri.charAt(uri.length() - 1) != '/')
				uri += "/";
			uri += a.getRelativeLocation(r);
			addAttribute(name, uri);

		} else if (value instanceof ThemeResource) {
			String uri = "theme://" + ((ThemeResource) value).getResourceId();
			addAttribute(name, uri);
		} else
			throw new PaintException("Ajax adapter does not "
					+ "support resources of type: "
					+ value.getClass().getName());

	}

	/**
	 * Adds a integer attribute to component. Atributes must be added before any
	 * content is written.
	 * 
	 * @param name
	 *            the Attribute name.
	 * @param value
	 *            the Attribute value.
	 * 
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void addAttribute(String name, int value) throws PaintException {
		addAttribute(name, String.valueOf(value));
	}

	/**
	 * Adds a long attribute to component. Atributes must be added before any
	 * content is written.
	 * 
	 * @param name
	 *            the Attribute name.
	 * @param value
	 *            the Attribute value.
	 * 
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void addAttribute(String name, long value) throws PaintException {
		addAttribute(name, String.valueOf(value));
	}

	/**
	 * Adds a string attribute to component. Atributes must be added before any
	 * content is written.
	 * 
	 * @param name
	 *            the Boolean attribute name.
	 * @param value
	 *            the Boolean attribute value.
	 * 
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void addAttribute(String name, String value) throws PaintException {
		// In case of null data output nothing:
		if ((value == null) || (name == null))
			throw new NullPointerException(
					"Parameters must be non-null strings");

		// Ensure that the target is open
		if (this.closed)
			throw new PaintException(
					"Attempted to write to a closed PaintTarget.");

		// Check that argument list is writable.
		if (!mTagArgumentListOpen)
			throw new PaintException("XML argument list not open.");

		append(" " + name + "=\"" + escapeXML(value) + "\"");
	}

	/**
	 * Adds a string type variable.
	 * 
	 * @param owner
	 *            the Listener for variable changes.
	 * @param name
	 *            the Variable name.
	 * @param value
	 *            the Variable initial value.
	 * 
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void addVariable(VariableOwner owner, String name, String value)
			throws PaintException {
		String code = variableMap.registerVariable(name, String.class, value,
				owner);
		startTag("string");
		addAttribute(UIDL_ARG_ID, code);
		addAttribute(UIDL_ARG_NAME, name);
		addText(value);
		endTag("string");
	}

	/**
	 * Adds a int type variable.
	 * 
	 * @param owner
	 *            the Listener for variable changes.
	 * @param name
	 *            the Variable name.
	 * @param value
	 *            the Variable initial value.
	 * 
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void addVariable(VariableOwner owner, String name, int value)
			throws PaintException {
		String code = variableMap.registerVariable(name, Integer.class,
				new Integer(value), owner);
		startTag("integer");
		addAttribute(UIDL_ARG_ID, code);
		addAttribute(UIDL_ARG_NAME, name);
		addAttribute(UIDL_ARG_VALUE, String.valueOf(value));
		endTag("integer");
	}

	/**
	 * Adds a boolean type variable.
	 * 
	 * @param owner
	 *            the Listener for variable changes.
	 * @param name
	 *            the Variable name.
	 * @param value
	 *            the Variable initial value.
	 * 
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void addVariable(VariableOwner owner, String name, boolean value)
			throws PaintException {
		String code = variableMap.registerVariable(name, Boolean.class,
				new Boolean(value), owner);
		startTag("boolean");
		addAttribute(UIDL_ARG_ID, code);
		addAttribute(UIDL_ARG_NAME, name);
		addAttribute(UIDL_ARG_VALUE, String.valueOf(value));
		endTag("boolean");
	}

	/**
	 * Adds a string array type variable.
	 * 
	 * @param owner
	 *            the Listener for variable changes.
	 * @param name
	 *            the Variable name.
	 * @param value
	 *            the Variable initial value.
	 * 
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void addVariable(VariableOwner owner, String name, String[] value)
			throws PaintException {
		String code = variableMap.registerVariable(name, String[].class, value,
				owner);
		startTag("array");
		addAttribute(UIDL_ARG_ID, code);
		addAttribute(UIDL_ARG_NAME, name);
		for (int i = 0; i < value.length; i++)
			addSection("ai", value[i]);
		endTag("array");
	}

	/**
	 * Adds a upload stream type variable.
	 * 
	 * @param owner
	 *            the Listener for variable changes.
	 * @param name
	 *            the Variable name.
	 * 
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void addUploadStreamVariable(VariableOwner owner, String name)
			throws PaintException {
		String code = variableMap.registerVariable(name, UploadStream.class,
				null, owner);
		startTag("uploadstream");
		addAttribute(UIDL_ARG_ID, code);
		addAttribute(UIDL_ARG_NAME, name);
		endTag("uploadstream");
	}

	/**
	 * Prints the single text section.
	 * 
	 * Prints full text section. The section data is escaped from XML tags and
	 * surrounded by XML start and end-tags.
	 * 
	 * @param sectionTagName
	 *            the name of the tag.
	 * @param sectionData
	 *            the section data to be printed.
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void addSection(String sectionTagName, String sectionData)
			throws PaintException {
		startTag(sectionTagName);
		addText(sectionData);
		endTag(sectionTagName);
	}

	/**
	 * Adds XML directly to UIDL.
	 * 
	 * @param xml
	 *            the Xml to be added.
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void addUIDL(String xml) throws PaintException {

		// Ensure that the target is open
		if (this.closed)
			throw new PaintException(
					"Attempted to write to a closed PaintTarget.");

		// Make sure that the open start tag is closed before
		// anything is written.
		ensureClosedTag();

		// Escape and write what was given
		if (xml != null)
			append(xml);

	}

	/**
	 * Adds XML section with namespace.
	 * 
	 * @param sectionTagName
	 *            the name of the tag.
	 * @param sectionData
	 *            the section data.
	 * @param namespace
	 *            the namespace to be added.
	 * @throws PaintException
	 *             if the paint operation failed.
	 * 
	 * @see com.itmill.toolkit.terminal.PaintTarget#addXMLSection(String,
	 *      String, String)
	 */
	public void addXMLSection(String sectionTagName, String sectionData,
			String namespace) throws PaintException {

		// Ensure that the target is open
		if (this.closed)
			throw new PaintException(
					"Attempted to write to a closed PaintTarget.");

		startTag(sectionTagName);
		if (namespace != null)
			addAttribute("xmlns", namespace);
		append(">");
		mTagArgumentListOpen = false;

		if (sectionData != null)
			append(sectionData);
		endTag(sectionTagName);
	}

	/**
	 * Gets the UIDL already printed to stream. Paint target must be closed
	 * before the <code>getUIDL</code> can be called.
	 * 
	 * @return the UIDL.
	 */
	public String getUIDL() {
		if (this.closed) {
			return uidlBuffer.toString();
		}
		throw new IllegalStateException(
				"Tried to read UIDL from open PaintTarget");
	}

	/**
	 * Closes the paint target. Paint target must be closed before the
	 * <code>getUIDL</code> can be called. Subsequent attempts to write to
	 * paint target. If the target was already closed, call to this function is
	 * ignored. will generate an exception.
	 * 
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void close() throws PaintException {
		if (!this.closed) {
			this.endTag("changes");
			flush();

			// Close all
			this.uidlBuffer.close();
			this.closed = true;
		}
	}

	/**
	 * Method flush.
	 */
	private void flush() {
		this.uidlBuffer.flush();
	}

	/**
	 * @see com.itmill.toolkit.terminal.PaintTarget#startTag(com.itmill.toolkit.terminal.Paintable,
	 *      java.lang.String)
	 */
	public boolean startTag(Paintable paintable, String tag)
			throws PaintException {
		startTag(tag);
		String id = manager.getPaintableId(paintable);
		paintable.addListener(manager);
		addAttribute("id", id);
		return false;
	}

	/**
	 * @see com.itmill.toolkit.terminal.PaintTarget#addCharacterData(java.lang.String)
	 */
	public void addCharacterData(String text) throws PaintException {
		// TODO: This should check the validity of characters
		ensureClosedTag();
		append(escapeXML(text));
	}

	/**
	 * 
	 * @return
	 */
	public boolean isTrackPaints() {
		return trackPaints;
	}

	/**
	 * Gets the number of paints.
	 * 
	 * @return the number of paints.
	 */
	public int getNumberOfPaints() {
		return numberOfPaints;
	}

	/**
	 * Sets the tracking to true or false.
	 * 
	 * This also resets the number of paints.
	 * 
	 * @param enabled
	 *            is the tracking is enabled or not.
	 * @see #getNumberOfPaints()
	 */
	public void setTrackPaints(boolean enabled) {
		this.trackPaints = enabled;
		this.numberOfPaints = 0;
	}

}
