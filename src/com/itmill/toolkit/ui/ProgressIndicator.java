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

package com.itmill.toolkit.ui;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.util.ObjectProperty;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * <code>ProgressIndicator</code> is component that shows user state of a
 * process (like long computing or file upload)
 * 
 * <code>ProgressIndicator</code> has two mainmodes. One for indeterminate
 * processes and other (default) for processes which progress can be measured
 * 
 * May view an other property that indicates progress 0...1
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.1
 */
public class ProgressIndicator extends AbstractField implements Property,
		Property.Viewer, Property.ValueChangeListener {

	/**
	 * Content mode, where the label contains only plain text. The getValue()
	 * result is coded to XML when painting.
	 */
	public static final int CONTENT_TEXT = 0;

	/**
	 * Content mode, where the label contains preformatted text.
	 */
	public static final int CONTENT_PREFORMATTED = 1;

	private boolean indeterminate = false;

	private Property dataSource;

	private int pollingInterval = 1000;

	/**
	 * Creates an a new ProgressIndicator.
	 */
	public ProgressIndicator() {
		setPropertyDataSource(new ObjectProperty(new Float(0), Float.class));
	}

	/**
	 * Creates a new instance of ProgressIndicator with given state.
	 * 
	 * @param value
	 */
	public ProgressIndicator(Float value) {
		setPropertyDataSource(new ObjectProperty(value, Float.class));
	}

	/**
	 * Creates a new instance of ProgressIndicator with stae read from given
	 * datasource.
	 * 
	 * @param contentSource
	 */
	public ProgressIndicator(Property contentSource) {
		setPropertyDataSource(contentSource);
	}

	/**
	 * Gets the component UIDL tag.
	 * 
	 * @return the Component UIDL tag as string.
	 */
	public String getTag() {
		return "progressindicator";
	}

	/**
	 * Sets the component to read-only. Readonly is not used in
	 * ProgressIndicator.
	 * 
	 * @param readOnly
	 *            True to enable read-only mode, False to disable it.
	 */
	public void setReadOnly(boolean readOnly) {
		if (dataSource == null)
			throw new IllegalStateException("Datasource must be se");
		dataSource.setReadOnly(readOnly);
	}

	/**
	 * Is the component read-only ? Readonly is not used in ProgressIndicator -
	 * this returns allways false.
	 * 
	 * @return True if the component is in read only mode.
	 */
	public boolean isReadOnly() {
		if (dataSource == null)
			throw new IllegalStateException("Datasource must be se");
		return dataSource.isReadOnly();
	}

	/**
	 * Paints the content of this component.
	 * 
	 * @param target
	 *            the Paint Event.
	 * @throws PaintException
	 *             if the Paint Operation fails.
	 */
	public void paintContent(PaintTarget target) throws PaintException {
		target.addAttribute("indeterminate", indeterminate);
		target.addAttribute("pollinginterval", pollingInterval);
		target.addAttribute("state", this.getValue().toString());
	}

	/**
	 * Gets the value of the ProgressIndicator. Value of the ProgressIndicator
	 * is Float between 0 and 1.
	 * 
	 * @return the Value of the ProgressIndicator.
	 * @see com.itmill.toolkit.ui.AbstractField#getValue()
	 */
	public Object getValue() {
		if (dataSource == null)
			throw new IllegalStateException("Datasource must be se");
		return dataSource.getValue();
	}

	/**
	 * Sets the value of the ProgressIndicator. Value of the ProgressIndicator
	 * is the Float between 0 and 1.
	 * 
	 * @param newValue
	 *            the New value of the ProgressIndicator.
	 * @see com.itmill.toolkit.ui.AbstractField#setValue(java.lang.Object)
	 */
	public void setValue(Object newValue) {
		if (dataSource == null)
			throw new IllegalStateException("Datasource must be se");
		this.dataSource.setValue(newValue);
	}

	/**
	 * @see com.itmill.toolkit.ui.AbstractField#toString()
	 */
	public String toString() {
		if (dataSource == null)
			throw new IllegalStateException("Datasource must be se");
		return dataSource.toString();
	}

	/**
	 * @see com.itmill.toolkit.ui.AbstractField#getType()
	 */
	public Class getType() {
		if (dataSource == null)
			throw new IllegalStateException("Datasource must be se");
		return dataSource.getType();
	}

	/**
	 * Gets the viewing data-source property.
	 * 
	 * @return the datasource.
	 * @see com.itmill.toolkit.ui.AbstractField#getPropertyDataSource()
	 */
	public Property getPropertyDataSource() {
		return dataSource;
	}

	/**
	 * Sets the property as data-source for viewing.
	 * 
	 * @param newDataSource
	 *            the new data source.
	 * @see com.itmill.toolkit.ui.AbstractField#setPropertyDataSource(com.itmill.toolkit.data.Property)
	 */
	public void setPropertyDataSource(Property newDataSource) {
		// Stops listening the old data source changes
		if (dataSource != null
				&& Property.ValueChangeNotifier.class
						.isAssignableFrom(dataSource.getClass()))
			((Property.ValueChangeNotifier) dataSource).removeListener(this);

		// Sets the new data source
		dataSource = newDataSource;

		// Listens the new data source if possible
		if (dataSource != null
				&& Property.ValueChangeNotifier.class
						.isAssignableFrom(dataSource.getClass()))
			((Property.ValueChangeNotifier) dataSource).addListener(this);
	}

	/**
	 * Gets the mode of ProgressIndicator.
	 * 
	 * @return true if in indeterminate mode.
	 */
	public boolean getContentMode() {
		return indeterminate;
	}

	/**
	 * Sets the ProgressIndicator to indeterminate mode.
	 * 
	 * @param newValue
	 *            true to set to indeterminate mode.
	 */
	public void setIndeterminate(boolean newValue) {
		indeterminate = newValue;
	}

	/**
	 * Sets the interval that component checks for progress.
	 * 
	 * @param newValue
	 *            the interval in milliseconds.
	 */
	public void setPollingInterval(int newValue) {
		pollingInterval = newValue;
	}

	/**
	 * Gets the interval that component checks for progress.
	 * 
	 * @return the interval in milliseconds.
	 */
	public int getPollingInterval() {
		return pollingInterval;
	}

}
