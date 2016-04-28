/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.StyleConstants;

/**
 * Widget for showing the current progress of a long running task.
 * <p>
 * The default mode is to show the current progress internally represented by a
 * floating point value between 0 and 1 (inclusive). The progress bar can also
 * be in an indeterminate mode showing an animation indicating that the task is
 * running but without providing any information about the current progress.
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public class VProgressBar extends Widget implements HasEnabled {

    public static final String PRIMARY_STYLE_NAME = "v-progressbar";

    Element wrapper = DOM.createDiv();
    Element indicator = DOM.createDiv();

    private boolean indeterminate = false;
    private float state = 0.0f;
    private boolean enabled;

    public VProgressBar() {
        setElement(DOM.createDiv());
        getElement().appendChild(wrapper);
        wrapper.appendChild(indicator);

        setStylePrimaryName(PRIMARY_STYLE_NAME);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.UIObject#setStylePrimaryName(java.lang.
     * String)
     */
    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);
        indicator.setClassName(getStylePrimaryName() + "-indicator");
        wrapper.setClassName(getStylePrimaryName() + "-wrapper");

    }

    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
        setStyleName(getStylePrimaryName() + "-indeterminate", indeterminate);
    }

    public void setState(float state) {
        final int size = Math.round(100 * state);
        indicator.getStyle().setWidth(size, Unit.PCT);
    }

    public boolean isIndeterminate() {
        return indeterminate;
    }

    public float getState() {
        return state;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            setStyleName(StyleConstants.DISABLED, !enabled);
        }
    }
}
