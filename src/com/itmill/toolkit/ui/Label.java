/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Interfaces Made Easy

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

package com.itmill.toolkit.ui;

import java.lang.reflect.Method;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.util.ObjectProperty;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/** Label component for showing non-editable short texts.
 *
 * The label content can be set to the modes specified by the final members
 * CONTENT_*
 * 
 * <p>The contents of the label may contain simple
 * formatting:
 * <ul>
 * <li> <b>&lt;b></b> Bold
 * <li> <b>&lt;i></b> Italic
 * <li> <b>&lt;u></b> Underlined
 * <li> <b>&lt;br/></b> Linebreak
 * <li> <b>&lt;ul>&lt;li>item 1&lt;/li>&lt;li>item 2&lt;/li>&lt;/ul></b> List of items
 * </ul>
 * The <b>b</b>,<b>i</b>,<b>u</b> and <b>li</b> tags can contain all the
 * tags in the list recursively.
 * </p>
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class Label
	extends AbstractComponent
	implements
		Property,
		Property.Viewer,
		Property.ValueChangeListener,
		Property.ValueChangeNotifier, Comparable {

	/** Content mode, where the label contains only plain text. The getValue()
	 * result is coded to XML when painting. 
	 */
	public static final int CONTENT_TEXT = 0;

	/** Content mode, where the label contains preformatted text.
	 */
	public static final int CONTENT_PREFORMATTED = 1;

	/** Formatted content mode, where the contents is XML restricted to the
	 * UIDL 1.0 formatting markups.
	 */
	public static final int CONTENT_UIDL = 2;

	/** Content mode, where the label contains XHTML. Contents is then enclosed in
	 * DIV elements having namespace of "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd".	 
	 */
	public static final int CONTENT_XHTML = 3;

	/** Content mode, where the label contains well-formed or well-balanced XML. 
	 * Each of the root elements must have their default namespace specified.
	 */
	public static final int CONTENT_XML = 4;

	/** Content mode, where the label contains RAW output. Output is not 
	 * required to comply to with XML. In Web Adapter output is inserted inside
	 * the resulting HTML document as-is. This is useful for some specific 
	 * purposes where possibly broken HTML content needs to be shown, but in 
	 * most cases XHTML mode should be preferred.
	 */
	public static final int CONTENT_RAW = 5;

	/** The default content mode is plain text */
	public static final int CONTENT_DEFAULT = CONTENT_TEXT;

	private Property dataSource;
	private int contentMode = CONTENT_DEFAULT;

	/** Creates an empty Label. */
	public Label() {
		setPropertyDataSource(new ObjectProperty("", String.class));
	}

	/** Creates a new instance of Label with text-contents. */
	public Label(String content) {
		setPropertyDataSource(new ObjectProperty(content, String.class));
	}

	/** Creates a new instance of Label with text-contents read from given datasource. */
	public Label(Property contentSource) {
		setPropertyDataSource(contentSource);
	}

	/** Creates a new instance of Label with text-contents. */
	public Label(String content, int contentMode) {
		setPropertyDataSource(new ObjectProperty(content, String.class));
		setContentMode(contentMode);
	}

	/** Creates a new instance of Label with text-contents read from given datasource. */
	public Label(Property contentSource, int contentMode) {
		setPropertyDataSource(contentSource);
		setContentMode(contentMode);
	}

	/** Get component UIDL tag.
	 * @return Component UIDL tag as string.
	 */
	public String getTag() {
		return "label";
	}

	/** Set the component to read-only.
	 * Readonly is not used in label.
	 * @param readOnly True to enable read-only mode, False to disable it
	 */
	public void setReadOnly(boolean readOnly) {
		if (dataSource == null)
			throw new IllegalStateException("Datasource must be se");
		dataSource.setReadOnly(readOnly);
	}

	/** Is the component read-only ?
	 * Readonly is not used in label - this returns allways false.
	 * @return True iff the component is in read only mode
	 */
	public boolean isReadOnly() {
		if (dataSource == null)
			throw new IllegalStateException("Datasource must be se");
		return dataSource.isReadOnly();
	}

	/** Paint the content of this component.
	 * @param event PaintEvent.
	 */
	public void paintContent(PaintTarget target) throws PaintException {
		if (contentMode == CONTENT_TEXT)
			target.addText(toString());
		else if (contentMode == CONTENT_UIDL)
			target.addUIDL(toString());
		else if (contentMode == CONTENT_XHTML) {
			target.startTag("data");
			target.addXMLSection(
				"div",
				toString(),
				"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
			target.endTag("data");
		} else if (contentMode == CONTENT_PREFORMATTED) {
			target.startTag("pre");
			target.addText(toString());
			target.endTag("pre");
		} else if (contentMode == CONTENT_XML) {
			target.addXMLSection("data", toString(), null);
		} else if (contentMode == CONTENT_RAW) {
			target.startTag("data");
			target.addAttribute("escape",false);
			target.addCharacterData(toString());
			target.endTag("data");
		}

	}

	/** Get the value of the label.
	 * Value of the label is the XML contents of the label.
	 * @return Value of the label
	 */
	public Object getValue() {
		if (dataSource == null)
			throw new IllegalStateException("Datasource must be se");
		return dataSource.getValue();
	}

	/** Set the value of the label.
	 * Value of the label is the XML contents of the label.
	 * @param newValue New value of the label
	 */
	public void setValue(Object newValue) {
		if (dataSource == null)
			throw new IllegalStateException("Datasource must be se");
		this.dataSource.setValue(newValue);
	}

	public String toString() {
		if (dataSource == null)
			throw new IllegalStateException("Datasource must be se");
		return dataSource.toString();
	}

	public Class getType() {
		if (dataSource == null)
			throw new IllegalStateException("Datasource must be se");
		return dataSource.getType();
	}

	/** Get viewing data-source property.  */
	public Property getPropertyDataSource() {
		return dataSource;
	}

	/** Set the property as data-source for viewing.  */
	public void setPropertyDataSource(Property newDataSource) {
		// Stop listening the old data source changes
		if (dataSource != null
			&& Property.ValueChangeNotifier.class.isAssignableFrom(
				dataSource.getClass()))
			 ((Property.ValueChangeNotifier) dataSource).removeListener(this);

		// Set the new data source
		dataSource = newDataSource;

		// Listen the new data source if possible
		if (dataSource != null
			&& Property.ValueChangeNotifier.class.isAssignableFrom(
				dataSource.getClass()))
			 ((Property.ValueChangeNotifier) dataSource).addListener(this);
	}

	/** Get the content mode of the Label.
	 * 
	 * <p>Possible content modes include:
	 *   <ul>
	 *     <li><b>CONTENT_TEXT</b> 
	 * 			Content mode, where the label contains only plain text. The 
	 * 			getValue() result is coded to XML when painting.</li>
	 *     <li><b>CONTENT_PREFORMATTED</b>
	 * 			Content mode, where the label contains preformatted text.</li>
	 *     <li><b>CONTENT_UIDL</b>
	 * 			Formatted content mode, where the contents is XML restricted to 
	 * 			the UIDL 1.0 formatting markups.</li>
	 *     <li><b>CONTENT_XHTML</b>
	 * 			Content mode, where the label contains XHTML. Contents is then 
	 * 			enclosed in DIV elements having namespace of 
	 * 			"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd".</li>
	 *     <li><b>CONTENT_XML</b>
	 * 			Content mode, where the label contains well-formed or 
	 * 			well-balanced XML. Each of the root elements must have their 
	 * 			default namespace specified.</li>
	 *     <li><b>CONTENT_RAW</b>
	 * 			Content mode, where the label contains RAW output. Output is not 
	 * 			required to comply to with XML. In Web Adapter output is 
	 * 			inserted inside the resulting HTML document as-is. This is 
	 * 			useful for some specific purposes where possibly broken HTML 
	 * 			content needs to be shown, but in most cases XHTML mode should 
	 * 			be preferred.</li>
	 *   </ul></p>
	 * 
	 * @return Content mode of the label.
	 */
	public int getContentMode() {
		return contentMode;
	}

	/** Set the content mode of the Label.
	 * 
	 * <p>Possible content modes include:
	 *   <ul>
	 *     <li><b>CONTENT_TEXT</b> 
	 * 			Content mode, where the label contains only plain text. The 
	 * 			getValue() result is coded to XML when painting.</li>
	 *     <li><b>CONTENT_PREFORMATTED</b>
	 * 			Content mode, where the label contains preformatted text.</li>
	 *     <li><b>CONTENT_UIDL</b>
	 * 			Formatted content mode, where the contents is XML restricted to 
	 * 			the UIDL 1.0 formatting markups.</li>
	 *     <li><b>CONTENT_XHTML</b>
	 * 			Content mode, where the label contains XHTML. Contents is then 
	 * 			enclosed in DIV elements having namespace of 
	 * 			"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd".</li>
	 *     <li><b>CONTENT_XML</b>
	 * 			Content mode, where the label contains well-formed or 
	 * 			well-balanced XML. Each of the root elements must have their 
	 * 			default namespace specified.</li>
	 *     <li><b>CONTENT_RAW</b>
	 * 			Content mode, where the label contains RAW output. Output is not 
	 * 			required to comply to with XML. In Web Adapter output is 
	 * 			inserted inside the resulting HTML document as-is. This is 
	 * 			useful for some specific purposes where possibly broken HTML 
	 * 			content needs to be shown, but in most cases XHTML mode should 
	 * 			be preferred.</li>
	 *   </ul></p>
	 * 
	 * @param contentMode New content mode of the label.
	 */
	public void setContentMode(int contentMode) {
		if (contentMode >= CONTENT_TEXT && contentMode <= CONTENT_RAW)
			this.contentMode = contentMode;
	}

	/* Value change events ****************************************** */

	private static final Method VALUE_CHANGE_METHOD;

	static {
		try {
			VALUE_CHANGE_METHOD =
				Property.ValueChangeListener.class.getDeclaredMethod(
					"valueChange",
					new Class[] { Property.ValueChangeEvent.class });
		} catch (java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException();
		}
	}

	/** Value change event 
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	public class ValueChangeEvent
		extends Component.Event
		implements Property.ValueChangeEvent {

		/**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3906084563938586935L;

        /** New instance of text change event
		 * @param source Source of the event.
		 */
		public ValueChangeEvent(Label source) {
			super(source);
		}

		/** Value where the event occurred
		 * @return Source of the event.
		 */
		public Property getProperty() {
			return (Property) getSource();
		}
	}

	/** Add value change listener
	 * @param listener Listener to be added.
	 */
	public void addListener(Property.ValueChangeListener listener) {
		addListener(
			Label.ValueChangeEvent.class,
			listener,
			VALUE_CHANGE_METHOD);
	}

	/** Remove value change listener
	 * @param listener Listener to be removed.
	 */
	public void removeListener(Property.ValueChangeListener listener) {
		removeListener(
			Label.ValueChangeEvent.class,
			listener,
			VALUE_CHANGE_METHOD);
	}

	/** Emit options change event. */
	protected void fireValueChange() {
		// Set the error message
		fireEvent(new Label.ValueChangeEvent(this));
		requestRepaint();
	}

	/** Listen value change events from data source.
	 * @see com.itmill.toolkit.data.Property.ValueChangeListener#valueChange(Property.ValueChangeEvent)
	 */
	public void valueChange(Property.ValueChangeEvent event) {
		fireValueChange();
	}

	/** Compare Label to other objects. 
	 * 
	 * <p>Labels can be compared to other labels for sorting label contents. 
	 * This is especially handy for sorting table columns.</p>
	 * 
	 * <p>In RAW, PREFORMATTED and TEXT modes, the label contents are 
	 * compared as is. In XML, UIDL and XHTML modes, only CDATA is compared and
	 * tags ignored. If the other object is not a Label, its toString() return
	 * value is used in comparison.</p>
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * @param other Other object to compare to
	 * @return a negative integer, zero, or a positive integer as this object  is less than, equal to, or greater than the specified object.
	 */
	public int compareTo(Object other) {
		
		String thisValue;
		String otherValue;
		
		if (contentMode == CONTENT_XML || contentMode == CONTENT_UIDL || contentMode == CONTENT_XHTML)
			thisValue = stripTags(toString());
		else 
			thisValue = toString();
		
		if (other instanceof Label && (((Label)other).getContentMode() == CONTENT_XML ||
				((Label)other).getContentMode() == CONTENT_UIDL || 
				((Label)other).getContentMode() == CONTENT_XHTML))
			otherValue = stripTags(other.toString());
		else 
			otherValue = other.toString();
		
		return thisValue.compareTo(otherValue);
	}

	/** Strip tags from the XML.
	 * 
	 * @param xml String containing a XML snippet
	 * @return The original XML without tags
	 */
	private String stripTags(String xml) {
		
		StringBuffer res = new StringBuffer();

		int processed = 0;
		int xmlLen = xml.length();
		while (processed < xmlLen) {
			int next = xml.indexOf('<',processed);
			if (next < 0) 
				next = xmlLen;
			res.append(xml.substring(processed,next));
			if (processed < xmlLen) {
				next = xml.indexOf('>',processed);
				if (next < 0) next = xmlLen;
				processed = next+1;
			}
		}
		
		return res.toString();
	}

}
