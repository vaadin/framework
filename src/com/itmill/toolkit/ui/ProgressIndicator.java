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

package com.itmill.toolkit.ui;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.util.ObjectProperty;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/** 
 * ProgressIndicator is component that shows user state of a prosess 
 * (like long computing or file upload)
 *
 * ProgressIndicator has two mainmodes. One for indeterminete prosesses and 
 * other (default) for prosesses which progress can be measured
 *
 * May view an other property that indicates progress 0...1
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 29122006
 */
public class ProgressIndicator
	extends AbstractField
	implements
		Property,
		Property.Viewer,
		Property.ValueChangeListener {

	/** Content mode, where the label contains only plain text. The getValue()
	 * result is coded to XML when painting. 
	 */
	public static final int CONTENT_TEXT = 0;

	/** Content mode, where the label contains preformatted text.
	 */
	public static final int CONTENT_PREFORMATTED = 1;
	
	private boolean indeterminate;

	private Property dataSource;

	private int pollingInterval;

	/** Creates an a new ProgressIndicator. */
	public ProgressIndicator() {
		setPropertyDataSource(new ObjectProperty(new Float(0), Float.class));
	}

	/** Creates a new instance of ProgressIndicator with given state. */
	public ProgressIndicator(Float value) {
		setPropertyDataSource(new ObjectProperty(value, Float.class));
	}

	/** Creates a new instance of ProgressIndicator with stae read from given datasource. */
	public ProgressIndicator(Property contentSource) {
		setPropertyDataSource(contentSource);
	}

	/** Get component UIDL tag.
	 * @return Component UIDL tag as string.
	 */
	public String getTag() {
		return "progressindicator";
	}

	/** Set the component to read-only.
	 * Readonly is not used in ProgressIndicator.
	 * @param readOnly True to enable read-only mode, False to disable it
	 */
	public void setReadOnly(boolean readOnly) {
		if (dataSource == null)
			throw new IllegalStateException("Datasource must be se");
		dataSource.setReadOnly(readOnly);
	}

	/** Is the component read-only ?
	 * Readonly is not used in ProgressIndicator - this returns allways false.
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
		target.addAttribute("indeterminate", indeterminate);
		target.addAttribute("pollinginterval", pollingInterval);
		target.addAttribute("state", this.getValue().toString());
	}

	/** Get the value of the ProgressIndicator.
	 * Value of the ProgressIndicator is Float between 0 and 1
	 * @return Value of the ProgressIndicator
	 */
	public Object getValue() {
		if (dataSource == null)
			throw new IllegalStateException("Datasource must be se");
		return dataSource.getValue();
	}

	/** Set the value of the ProgressIndicator.
	 * Value of the ProgressIndicator is the Float  between 0 and 1
	 * @param newValue New value of the ProgressIndicator
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

	/** Get the mode of ProgressIndicator.
	 * 
	 * @return true if in indeterminate mode
	 */
	public boolean getContentMode() {
		return indeterminate;
	}
	
	/**
	 * Set ProgressIndicator to indeterminate mode
	 * 
	 * @param newValue true to set to indeterminate mode
	 */
	public void setIndeterminate(boolean newValue) {
		indeterminate = newValue;
	}
	
	/**
	 * Set interval that compnent checks for progress
	 * @param newValue interval in milliseconds
	 */
	public void setPollingInterval(int newValue) {
		pollingInterval = newValue;
	}
	
	/**
	 * Get interval that component checks for progress
	 * @return interval in milliseconds
	 */
	public int getPollingInterval() {
		return pollingInterval;
	}

}
