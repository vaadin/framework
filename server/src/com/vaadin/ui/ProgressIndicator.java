/* 
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.ui;

import java.util.Map;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.LegacyComponent;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;

/**
 * <code>ProgressIndicator</code> is component that shows user state of a
 * process (like long computing or file upload)
 * 
 * <code>ProgressIndicator</code> has two mainmodes. One for indeterminate
 * processes and other (default) for processes which progress can be measured
 * 
 * May view an other property that indicates progress 0...1
 * 
 * @author Vaadin Ltd.
 * @since 4
 */
@SuppressWarnings("serial")
public class ProgressIndicator extends AbstractField<Number> implements
        Property.Viewer, Property.ValueChangeListener, LegacyComponent {

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
        setPropertyDataSource(new ObjectProperty<Float>(new Float(0),
                Float.class));
    }

    /**
     * Creates a new instance of ProgressIndicator with given state.
     * 
     * @param value
     */
    public ProgressIndicator(Float value) {
        setPropertyDataSource(new ObjectProperty<Float>(value, Float.class));
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
     * Sets the component to read-only. Readonly is not used in
     * ProgressIndicator.
     * 
     * @param readOnly
     *            True to enable read-only mode, False to disable it.
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        if (dataSource == null) {
            throw new IllegalStateException("Datasource must be se");
        }
        dataSource.setReadOnly(readOnly);
    }

    /**
     * Is the component read-only ? Readonly is not used in ProgressIndicator -
     * this returns allways false.
     * 
     * @return True if the component is in read only mode.
     */
    @Override
    public boolean isReadOnly() {
        if (dataSource == null) {
            throw new IllegalStateException("Datasource must be se");
        }
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
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute("indeterminate", indeterminate);
        target.addAttribute("pollinginterval", pollingInterval);
        target.addAttribute("state", getValue().toString());
    }

    /**
     * Gets the value of the ProgressIndicator. Value of the ProgressIndicator
     * is Float between 0 and 1.
     * 
     * @return the Value of the ProgressIndicator.
     * @see com.vaadin.ui.AbstractField#getValue()
     */
    @Override
    public Number getValue() {
        if (dataSource == null) {
            throw new IllegalStateException("Datasource must be set");
        }
        // TODO conversions to eliminate cast
        return (Number) dataSource.getValue();
    }

    /**
     * Sets the value of the ProgressIndicator. Value of the ProgressIndicator
     * is the Float between 0 and 1.
     * 
     * @param newValue
     *            the New value of the ProgressIndicator.
     * @see com.vaadin.ui.AbstractField#setValue()
     */
    @Override
    public void setValue(Number newValue) {
        if (dataSource == null) {
            throw new IllegalStateException("Datasource must be set");
        }
        dataSource.setValue(newValue);
    }

    /**
     * @see com.vaadin.ui.AbstractField#getType()
     */
    @Override
    public Class<? extends Number> getType() {
        if (dataSource == null) {
            throw new IllegalStateException("Datasource must be set");
        }
        return dataSource.getType();
    }

    /**
     * Gets the viewing data-source property.
     * 
     * @return the datasource.
     * @see com.vaadin.ui.AbstractField#getPropertyDataSource()
     */
    @Override
    public Property getPropertyDataSource() {
        return dataSource;
    }

    /**
     * Sets the property as data-source for viewing.
     * 
     * @param newDataSource
     *            the new data source.
     * @see com.vaadin.ui.AbstractField#setPropertyDataSource(com.vaadin.data.Property)
     */
    @Override
    public void setPropertyDataSource(Property newDataSource) {
        // Stops listening the old data source changes
        if (dataSource != null
                && Property.ValueChangeNotifier.class
                        .isAssignableFrom(dataSource.getClass())) {
            ((Property.ValueChangeNotifier) dataSource).removeListener(this);
        }

        // Sets the new data source
        dataSource = newDataSource;

        // Listens the new data source if possible
        if (dataSource != null
                && Property.ValueChangeNotifier.class
                        .isAssignableFrom(dataSource.getClass())) {
            ((Property.ValueChangeNotifier) dataSource).addListener(this);
        }
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
     * Sets wheter or not the ProgressIndicator is indeterminate.
     * 
     * @param newValue
     *            true to set to indeterminate mode.
     */
    public void setIndeterminate(boolean newValue) {
        indeterminate = newValue;
        markAsDirty();
    }

    /**
     * Gets whether or not the ProgressIndicator is indeterminate.
     * 
     * @return true to set to indeterminate mode.
     */
    public boolean isIndeterminate() {
        return indeterminate;
    }

    /**
     * Sets the interval that component checks for progress.
     * 
     * @param newValue
     *            the interval in milliseconds.
     */
    public void setPollingInterval(int newValue) {
        pollingInterval = newValue;
        markAsDirty();
    }

    /**
     * Gets the interval that component checks for progress.
     * 
     * @return the interval in milliseconds.
     */
    public int getPollingInterval() {
        return pollingInterval;
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        // TODO Remove once LegacyComponent is no longer implemented

    }

}
