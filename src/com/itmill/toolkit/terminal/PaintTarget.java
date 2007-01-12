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

package com.itmill.toolkit.terminal;

/** This interface defines the methods for 
 *  painting XML to the UIDL stream. 
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public interface PaintTarget {

	/** Print single XMLsection.
	 *
	 * Prints full XML section. The section data is escaped from XML tags and
	 * surrounded by XML start and end-tags.
	 */
	public void addSection(String sectionTagName, String sectionData)
		throws PaintException;

	/**  Print element start tag of a paintable section.
	 * Starts a paintable section using the given tag. The PaintTarget may
	 * implement a caching scheme, that checks the paintable has actually
	 * changed or can a cached version be used instead. This method should call
	 * the startTag method.
	 *
	 * If the Paintable is found in cache and this function returns true it may
	 * omit the content and close the tag, in which case cached content should
	 * be used.
	 *
	 * @param paintable The paintable to start
	 * @param tagName The name of the start tag
	 * @return true if paintable found in cache, false otherwise.
	 * @see #startTag(String)
	 * @since 3.1
	 */
	public boolean startTag(Paintable paintable, String tag)
		throws PaintException;


	/**  Print element start tag.
	 *
	 * <pre>Todo:
	 * Checking of input values
	 * </pre>
	 *
	 * @param tagName The name of the start tag
	 *
	 */
	public void startTag(String tagName) throws PaintException;

	/** Print element end tag.
	 *
	 * If the parent tag is closed before
	 * every child tag is closed an PaintException is raised.
	 *
	 * @param tag The name of the end tag
	 * @exception IOException The writing failed due to input/output error
	 */
	public void endTag(String tagName) throws PaintException;

	/** Adds a boolean attribute to component.
	 *  Atributes must be added before any content is written.
	 *
	 *  @param name Attribute name
	 *  @param value Attribute value
	 *  @return this object
	 */
	public void addAttribute(String name, boolean value) throws PaintException;

	/** Adds a integer attribute to component.
	 *  Atributes must be added before any content is written.
	 *
	 *  @param name Attribute name
	 *  @param value Attribute value
	 *  @return this object
	 */
	public void addAttribute(String name, int value) throws PaintException;

	/** Adds a resource attribute to component.
	 *  Atributes must be added before any content is written.
	 *
	 *  @param name Attribute name
	 *  @param value Attribute value
	 *  @return this object
	 */
	public void addAttribute(String name, Resource value)
		throws PaintException;

	/** Adds a long attribute to component.
	 *  Atributes must be added before any content is written.
	 *
	 *  @param name Attribute name
	 *  @param value Attribute value
	 *  @return this object
	 */
	public void addAttribute(String name, long value) throws PaintException;

	/** Adds a string attribute to component.
	 *  Atributes must be added before any content is written.
	 *
	 *  @param name Boolean attribute name
	 *  @param value Boolean attribute value
	 *  @return this object
	 */
	public void addAttribute(String name, String value) throws PaintException;

	/** Add a string type variable.
	 *  @param owner Listener for variable changes
	 *  @param name Variable name
	 *  @param value Variable initial value
	 *  @return Reference to this.
	 */
	public void addVariable(VariableOwner owner, String name, String value)
		throws PaintException;

	/** Add a int type variable.
	 *  @param owner Listener for variable changes
	 *  @param name Variable name
	 *  @param value Variable initial value
	 *  @return Reference to this.
	 */
	public void addVariable(VariableOwner owner, String name, int value)
		throws PaintException;

	/** Add a boolean type variable.
	 *  @param owner Listener for variable changes
	 *  @param name Variable name
	 *  @param value Variable initial value
	 *  @return Reference to this.
	 */
	public void addVariable(VariableOwner owner, String name, boolean value)
		throws PaintException;

	/** Add a string array type variable.
	 *  @param owner Listener for variable changes
	 *  @param name Variable name
	 *  @param value Variable initial value
	 *  @return Reference to this.
	 */
	public void addVariable(VariableOwner owner, String name, String[] value)
		throws PaintException;

	/** Add a upload stream type variable.
	 *  @param owner Listener for variable changes
	 *  @param name Variable name
	 *  @param value Variable initial value
	 *  @return Reference to this.
	 */
	public void addUploadStreamVariable(VariableOwner owner, String name)
		throws PaintException;

	/** Print single XML section.
	*
	* Prints full XML section. The section data must be XML and it is
	* surrounded by XML start and end-tags.
	*/
	public void addXMLSection(
		String sectionTagName,
		String sectionData,
		String namespace)
		throws PaintException;

	/** Add UIDL directly. 
	 * The UIDL must be valid in accordance with the UIDL.dtd
	 */
	public void addUIDL(java.lang.String uidl) throws PaintException;

	/** Add text node. All the contents of the text are XML-escaped.
	 * @param text Text to add
	 */
	void addText(String text) throws PaintException;

	/** Add CDATA node to target UIDL-tree.
	 * @param text Character data to add
	 * @since 3.1
	 */
	void addCharacterData(String text) throws PaintException;
}
