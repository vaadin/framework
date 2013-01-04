/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import com.vaadin.data.Property;
import com.vaadin.shared.ui.progressindicator.ProgressIndicatorServerRpc;
import com.vaadin.shared.ui.progressindicator.ProgressIndicatorState;

/**
 * <code>ProgressIndicator</code> is component that shows user state of a
 * process (like long computing or file upload)
 * 
 * <code>ProgressIndicator</code> has two main modes. One for indeterminate
 * processes and other (default) for processes which progress can be measured
 * 
 * May view an other property that indicates progress 0...1
 * 
 * @author Vaadin Ltd.
 * @since 4
 */
@SuppressWarnings("serial")
public class ProgressIndicator extends AbstractField<Float> implements
        Property.Viewer, Property.ValueChangeListener {

    private ProgressIndicatorServerRpc rpc = new ProgressIndicatorServerRpc() {

        @Override
        public void poll() {
            // Nothing to do.
        }
    };

    /**
     * Creates an a new ProgressIndicator.
     */
    public ProgressIndicator() {
        this(0.0f);
    }

    /**
     * Creates a new instance of ProgressIndicator with given state.
     * 
     * @param value
     */
    public ProgressIndicator(float value) {
        setValue(value);
        registerRpc(rpc);
    }

    /**
     * Creates a new instance of ProgressIndicator with state read from the
     * given datasource.
     * 
     * @param contentSource
     */
    public ProgressIndicator(Property contentSource) {
        setPropertyDataSource(contentSource);
        registerRpc(rpc);
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        getState().state = getValue();
    }

    /**
     * Gets the value of the ProgressIndicator. Value of the ProgressIndicator
     * is Float between 0 and 1.
     * 
     * @return the Value of the ProgressIndicator.
     * @see com.vaadin.ui.AbstractField#getValue()
     */
    @Override
    public Float getValue() {
        return super.getValue();
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
    public void setValue(Float newValue) {
        super.setValue(newValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractField#getType()
     */
    @Override
    public Class<Float> getType() {
        return Float.class;
    }

    @Override
    protected ProgressIndicatorState getState() {
        return (ProgressIndicatorState) super.getState();
    }

    /**
     * Sets whether or not the ProgressIndicator is indeterminate.
     * 
     * @param indeterminate
     *            true to set to indeterminate mode.
     */
    public void setIndeterminate(boolean indeterminate) {
        getState().indeterminate = indeterminate;
    }

    /**
     * Gets whether or not the ProgressIndicator is indeterminate.
     * 
     * @return true to set to indeterminate mode.
     */
    public boolean isIndeterminate() {
        return getState().indeterminate;
    }

    /**
     * Sets the interval that component checks for progress.
     * 
     * @param pollingInterval
     *            the interval in milliseconds.
     */
    public void setPollingInterval(int pollingInterval) {
        getState().pollingInterval = pollingInterval;
    }

    /**
     * Gets the interval that component checks for progress.
     * 
     * @return the interval in milliseconds.
     */
    public int getPollingInterval() {
        return getState().pollingInterval;
    }

}
