/*
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.v7.ui;

import org.jsoup.nodes.Element;

import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.shared.ui.progressindicator.ProgressBarState;

/**
 * Shows the current progress of a long running task.
 * <p>
 * The default mode is to show the current progress internally represented by a
 * floating point value between 0 and 1 (inclusive). The progress bar can also
 * be in an indeterminate mode showing an animation indicating that the task is
 * running but without providing any information about the current progress.
 *
 * @since 7.1
 * @author Vaadin Ltd
 *
 * @deprecated As of 8.0 replaced by {@link com.vaadin.ui.ProgressBar} based on
 *             the new data binding API
 */
@Deprecated
public class ProgressBar extends AbstractField<Float> {

    private static final float DEFAULT_VALUE = 0f;

    /**
     * Creates a new progress bar initially set to 0% progress.
     */
    public ProgressBar() {
        this(DEFAULT_VALUE);
    }

    /**
     * Creates a new progress bar with the given initial value.
     *
     * @param progress
     *            the initial progress value
     */
    public ProgressBar(float progress) {
        setValue(Float.valueOf(progress));
    }

    /**
     * Creates a new progress bar bound to the given data source.
     *
     * @param dataSource
     *            the property to bind this progress bar to
     */
    public ProgressBar(Property<?> dataSource) {
        setPropertyDataSource(dataSource);
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        // Update value in state even if the property hasn't fired any event
        getState().state = getValue();
    }

    /**
     * Gets the value of this progress bar. The value is a <code>float</code>
     * between 0 and 1 where 0 represents no progress at all and 1 represents
     * fully completed.
     *
     * @return the current progress value
     */
    @Override
    public Float getValue() {
        return super.getValue();
    }

    /**
     * Sets the value of this progress bar. The value is a <code>float</code>
     * between 0 and 1 where 0 represents no progress at all and 1 represents
     * fully completed.
     *
     * @param newValue
     *            the current progress value
     */
    @Override
    public void setValue(Float newValue) {
        super.setValue(newValue);
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }

    @Override
    protected ProgressBarState getState() {
        return (ProgressBarState) super.getState();
    }

    @Override
    protected ProgressBarState getState(boolean markAsDirty) {
        return (ProgressBarState) super.getState(markAsDirty);
    }

    /**
     * Sets whether or not this progress indicator is indeterminate. In
     * indeterminate mode there is an animation indicating that the task is
     * running but without providing any information about the current progress.
     *
     * @param indeterminate
     *            <code>true</code> to set to indeterminate mode; otherwise
     *            <code>false</code>
     */
    public void setIndeterminate(boolean indeterminate) {
        getState().indeterminate = indeterminate;
    }

    /**
     * Gets whether or not this progress indicator is indeterminate. In
     * indeterminate mode there is an animation indicating that the task is
     * running but without providing any information about the current progress.
     *
     * @return <code>true</code> if set to indeterminate mode; otherwise
     *         <code>false</code>
     */
    public boolean isIndeterminate() {
        return getState(false).indeterminate;
    }

    /*
     * Overridden to keep the shared state in sync with the AbstractField
     * internal value. Should be removed once AbstractField is refactored to use
     * shared state.
     *
     * See tickets #10921 and #11064.
     */
    @Override
    protected void setInternalValue(Float newValue) {
        super.setInternalValue(newValue);
        if (newValue == null) {
            newValue = Float.valueOf(0);
        }
        getState().state = newValue;
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        if (design.hasAttr("value") && !design.attr("value").isEmpty()) {
            setValue(DesignAttributeHandler.readAttribute("value",
                    design.attributes(), Float.class), false, true);
        }
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        Float defaultValue = ((ProgressBar) designContext
                .getDefaultInstance(this)).getValue();
        DesignAttributeHandler.writeAttribute("value", design.attributes(),
                getValue(), defaultValue, Float.class, designContext);
    }

    @Override
    public void clear() {
        setValue(DEFAULT_VALUE);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || getValue() == DEFAULT_VALUE;

    }
}
