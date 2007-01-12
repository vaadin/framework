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

import java.util.Stack;

/** User Interface Description Language Target.
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class WebPaintTarget implements PaintTarget {

	/* Document type declarations */
	private final static String UIDL_XML_DECL =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	/* commonly used tags and argument names */
	private final static String UIDL_ARG_NAME = "name";
	private final static String UIDL_ARG_VALUE = "value";
	private final static String UIDL_ARG_ID = "id";
	private Stack mOpenTags;
	private boolean mTagArgumentListOpen;
	private StringBuffer uidlBuffer;
	private StringBuffer tagBuffer;
	private HttpVariableMap variableMap;
	private boolean closed = false;
	private ApplicationServlet webAdapterServlet;
	private Theme theme;
    private static final int TAG_BUFFER_DEFAULT_SIZE = 20;
    private boolean mSuppressOutput = false;


	/** Create a new XMLPrintWriter, without automatic line flushing.
	 * 
	 *
	 * @param out  A character-output stream.
	 */
	public WebPaintTarget(
		HttpVariableMap variableMap,
		UIDLTransformerType type,
		ApplicationServlet webAdapterServlet,
		Theme theme)
		throws PaintException {

		// Host servlet
		this.webAdapterServlet = webAdapterServlet;

		// Target theme
		this.theme = theme;

		// Set the variable map
		this.variableMap = variableMap;

		// Set the target for UIDL writing
		this.uidlBuffer = new StringBuffer();

		// Set the target for TAG data
		this.tagBuffer = new StringBuffer();
		
		// Initialize tag-writing
		mOpenTags = new Stack();
		mTagArgumentListOpen = false;

		//Add document declaration
		this.print(UIDL_XML_DECL + "\n\n");

		// Add UIDL start tag and its attributes
		this.startTag("uidl");

		// Name of the active theme
		this.addAttribute("theme", type.getTheme().getName());

	}

	/** Ensures that the currently open element tag is closed.
	 */
	private void ensureClosedTag() {
		if (mTagArgumentListOpen) {
			tagBuffer.append(">");
			mTagArgumentListOpen = false;
			append(tagBuffer);			
		}
	}
	/**  Print element start tag.
	 *
	 * <pre>Todo:
	 * Checking of input values
	 * </pre>
	 *
	 * @param tagName The name of the start tag
	 *
	 */
	public void startTag(String tagName) throws PaintException {
		// In case of null data output nothing:
		if (tagName == null)
			throw new NullPointerException();

		//Ensure that the target is open
		if (this.closed)
			throw new PaintException("Attempted to write to a closed PaintTarget.");

		// Make sure that the open start tag is closed before
		// anything is written.
		ensureClosedTag();

		// Check tagName and attributes here
		mOpenTags.push(tagName);
		tagBuffer = new StringBuffer(TAG_BUFFER_DEFAULT_SIZE);

		// Print the tag with attributes
		tagBuffer.append("<" + tagName);

		mTagArgumentListOpen = true;
	}

	/** Print element end tag.
	 *
	 * If the parent tag is closed before
	 * every child tag is closed a PaintException is raised.
	 *
	 * @param tag The name of the end tag
	 */
	public void endTag(String tagName) throws PaintException {
		// In case of null data output nothing:
		if (tagName == null)
			throw new NullPointerException();

		//Ensure that the target is open
		if (this.closed)
			throw new PaintException("Attempted to write to a closed PaintTarget.");

		String lastTag = "";

		lastTag = (String) mOpenTags.pop();
		if (!tagName.equalsIgnoreCase(lastTag))
			throw new PaintException(
				"Invalid UIDL: wrong ending tag: '"
					+ tagName
					+ "' expected: '"
					+ lastTag
					+ "'.");

		// Make sure that the open start tag is closed before
		// anything is written.
		ensureClosedTag();

		//Write the end (closing) tag
		append("</" + lastTag + "\n>");
		
		// NOTE: We re-enable the output (if it has been disabled)
		// for subsequent tags. The output is suppressed if tag
		// contains attribute "invisible" with value true.
		mSuppressOutput = false;
	}
	
	/** Append data into UIDL output buffer.
	 * 
	 * @param data String to be appended.
	 */
	private void append(String data) {
	    if (!mSuppressOutput) {
	        uidlBuffer.append(data);
	    }
	}

	/** Append data into UIDL output buffer.
	 * 
	 * @param data StringBuffer to be appended.
	 */
	private void append(StringBuffer data) {
	    if (!mSuppressOutput) {
		    uidlBuffer.append(data);	        
	    }
	}
	
	/** Substitute the XML sensitive characters with predefined XML entities.
	 *
	 * @return A new string instance where all occurrences of XML sensitive
	 * characters are substituted with entities.
	 */
	static public String escapeXML(String xml) {
		if (xml == null || xml.length() <= 0)
			return "";
		return escapeXML(new StringBuffer(xml)).toString();
	}

	/** Substitute the XML sensitive characters with predefined XML entities.
	 * @param xml the String to be substituted
	 * @return A new StringBuffer instance where all occurrences of XML
	 * sensitive characters are substituted with entities.
	 *
	 */
	static public StringBuffer escapeXML(StringBuffer xml) {
		if (xml == null || xml.length() <= 0)
			return new StringBuffer("");

		StringBuffer result = new StringBuffer(xml.length()*2);

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

	/** Substitute a XML sensitive character with predefined XML entity.
	 * @param c Character to be replaced with an entity.
	 * @return String of the entity or null if character is not to be replaced
	 * with an entity.
	 */
	private static String toXmlChar(char c) {
		switch (c) {
			case '&' :
				return "&amp;"; // & => &amp;		
			case '>' :
				return "&gt;"; // > => &gt;		
			case '<' :
				return "&lt;"; // < => &lt;		
			case '"' :
				return "&quot;"; // " => &quot;			
			case '\'' :
				return "&apos;"; // ' => &apos;		
			default :
				return null;
		}
	}

	/** Print XML.
	 *
	 * Writes pre-formatted XML to stream. Well-formness of XML is checked.
	 * <pre>
	 * TODO: XML checking should be made
	 * </pre>
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

	/** Print XML-escaped text.
	 *
	 */
	public void addText(String str) throws PaintException {
		addUIDL(escapeXML(str));
	}

	/** Adds a boolean attribute to component.
	 *  Atributes must be added before any content is written.
	 *
	 *  @param name Attribute name
	 *  @param value Attribute value
	 */
	public void addAttribute(String name, boolean value)
		throws PaintException {
	    if ("invisible".equals(name) && value) {
	        // NOTE: If we receive the "invisible attribute
	        // we filter these tags (and ceontent) from 
	        // them out from the output. 
	        this.mSuppressOutput = true;
	    } else {
			addAttribute(name, String.valueOf(value));	        
	    }
	}

	/** Adds a resource attribute to component.
	 *  Atributes must be added before any content is written.
	 *
	 *  @param name Attribute name
	 *  @param value Attribute value
	 */
	public void addAttribute(String name, Resource value)
		throws PaintException {

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
			addAttribute(
				name,
				webAdapterServlet.getResourceLocation(
					theme.getName(),
					(ThemeResource) value));
		} else
			throw new PaintException(
				"Web adapter does not "
					+ "support resources of type: "
					+ value.getClass().getName());

	}

	/** Adds a integer attribute to component.
	 *  Atributes must be added before any content is written.
	 *
	 *  @param name Attribute name
	 *  @param value Attribute value
	 *  @return this object
	 */
	public void addAttribute(String name, int value) throws PaintException {
		addAttribute(name, String.valueOf(value));
	}

	/** Adds a long attribute to component.
	 * Atributes must be added before any content is written.
	 *
	 * @param name Attribute name
	 * @param value Attribute value
	 * @return this object
	 */
	public void addAttribute(String name, long value) throws PaintException {
		addAttribute(name, String.valueOf(value));
	}

	/** Adds a string attribute to component.
	 *  Atributes must be added before any content is written.
	 *
	 *  @param name Boolean attribute name
	 *  @param value Boolean attribute value
	 *  @return this object
	 */
	public void addAttribute(String name, String value) throws PaintException {
		// In case of null data output nothing:
		if ((value == null) || (name == null))
			throw new NullPointerException("Parameters must be non-null strings ("+name+"="+value+")");

		//Ensure that the target is open
		if (this.closed)
			throw new PaintException("Attempted to write to a closed PaintTarget.");

		// Check that argument list is writable.
		if (!mTagArgumentListOpen)
			throw new PaintException("XML argument list not open.");

		tagBuffer.append(" " + name + "=\"" + escapeXML(value) + "\"");
	}

	/** Add a string type variable.
	 *  @param owner Listener for variable changes
	 *  @param name Variable name
	 *  @param value Variable initial value
	 *  @return Reference to this.
	 */
	public void addVariable(VariableOwner owner, String name, String value)
		throws PaintException {
		String code = variableMap.registerVariable(name, String.class, value, owner);
		startTag("string");
		addAttribute(UIDL_ARG_ID, code);
		addAttribute(UIDL_ARG_NAME, name);
		addText(value);
		endTag("string");
	}

	/** Add a int type variable.
	 *  @param owner Listener for variable changes
	 *  @param name Variable name
	 *  @param value Variable initial value
	 *  @return Reference to this.
	 */
	public void addVariable(VariableOwner owner, String name, int value)
		throws PaintException {
		String code = variableMap.registerVariable(name, Integer.class, new Integer(value), owner);
		startTag("integer");
		addAttribute(UIDL_ARG_ID, code);
		addAttribute(UIDL_ARG_NAME, name);
		addAttribute(UIDL_ARG_VALUE, String.valueOf(value));
		endTag("integer");
	}

	/** Add a boolean type variable.
	 *  @param owner Listener for variable changes
	 *  @param name Variable name
	 *  @param value Variable initial value
	 *  @return Reference to this.
	 */
	public void addVariable(VariableOwner owner, String name, boolean value)
		throws PaintException {
		String code = variableMap.registerVariable(name, Boolean.class, new Boolean(value), owner);
		startTag("boolean");
		addAttribute(UIDL_ARG_ID, code);
		addAttribute(UIDL_ARG_NAME, name);
		addAttribute(UIDL_ARG_VALUE, String.valueOf(value));
		endTag("boolean");
	}

	/** Add a string array type variable.
	 *  @param owner Listener for variable changes
	 *  @param name Variable name
	 *  @param value Variable initial value
	 *  @return Reference to this.
	 */
	public void addVariable(VariableOwner owner, String name, String[] value)
		throws PaintException {
		String code = variableMap.registerVariable(name, String[].class, value, owner);
		startTag("array");
		addAttribute(UIDL_ARG_ID, code);
		addAttribute(UIDL_ARG_NAME, name);
		for (int i = 0; i < value.length; i++)
			addSection("ai", value[i]);
		endTag("array");
	}

	/** Add a upload stream type variable.
	 * @param owner Listener for variable changes
	 * @param name Variable name
	 * @param value Variable initial value
	 * @return Reference to this.
	 */
	public void addUploadStreamVariable(VariableOwner owner, String name)
		throws PaintException {
		String code =
			variableMap.registerVariable(name, UploadStream.class, null, owner);
		startTag("uploadstream");
		addAttribute(UIDL_ARG_ID, code);
		addAttribute(UIDL_ARG_NAME, name);
		endTag("uploadstream");
	}

	/** Print single text section.
	 *
	 * Prints full text section. The section data is escaped from XML tags and
	 * surrounded by XML start and end-tags.
	 */
	public void addSection(String sectionTagName, String sectionData)
		throws PaintException {
		startTag(sectionTagName);
		addText(sectionData);
		endTag(sectionTagName);
	}

	/** Add XML dirctly to UIDL  */
	public void addUIDL(String xml) throws PaintException {

		//Ensure that the target is open
		if (this.closed)
			throw new PaintException("Attempted to write to a closed PaintTarget.");

		// Make sure that the open start tag is closed before
		// anything is written.
		ensureClosedTag();

		// Escape and write what was given
		if (xml != null)
			append(xml);

	}
	/** Add XML section with namespace
	 * @see com.itmill.toolkit.terminal.PaintTarget#addXMLSection(String, String, String)
	 */
	public void addXMLSection(
		String sectionTagName,
		String sectionData,
		String namespace)
		throws PaintException {

		//Ensure that the target is open
		if (this.closed)
			throw new PaintException("Attempted to write to a closed PaintTarget.");

		startTag(sectionTagName);
		if (namespace != null)
			addAttribute("xmlns", namespace);

		// Close that starting tag
		ensureClosedTag();

		if (sectionData != null)
			append(sectionData);
		endTag(sectionTagName);
	}

	/** Get the UIDL already printed to stream. 
	 * Paint target must be closed before the getUIDL()
	 * cn be called.
	 */
	public String getUIDL() {
		if (this.closed) {
			return uidlBuffer.toString();
		}
		throw new IllegalStateException("Tried to read UIDL from open PaintTarget");
	}

	/** Close the paint target. 
	 * Paint target must be closed before the getUIDL()
	 * cn be called.
	 * Subsequent attempts to write to paint target.
	 * If the target was already closed, call to this
	 * function is ignored.
	 * will generate an exception.
	 */
	public void close() throws PaintException {
		if (!this.closed) {
			this.endTag("uidl");
			this.closed = true;
		}
	}
	
	/**  Print element start tag of a paintable section.
	 * Starts a paintable section using the given tag. The PaintTarget may
	 * implement a caching scheme, that checks the paintable has actually
	 * changed or can a cached version be used instead. This method should call
	 * the startTag method. <p>  If the Paintable is found in cache and this
	 * function returns true it may omit the content and close the tag, in which
	 * case cached content should be used.
	 * </p><b>Note:</b> Web adapter does not currently implement caching and
	 * this function always returns false.
	 * @param paintable The paintable to start
	 * @param tagName The name of the start tag
	 * @return false
	 * @see com.itmill.toolkit.terminal.PaintTarget#startTag(Paintable, String),
	 * #startTag(String)
	 * @since 3.1
	 */
	public boolean startTag(Paintable paintable, String tag)
		throws PaintException {
			startTag(tag);
		return false;
	}

	/** Add CDATA node to target UIDL-tree.
	 * @param text Character data to add
	 * @since 3.1
	 */
	public void addCharacterData(String text) throws PaintException {
		addUIDL("<![CDATA["+text+"]]>");
	}

}
