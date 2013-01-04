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

package com.vaadin.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

public class VProgressIndicator extends Widget implements HasEnabled {

    public static final String CLASSNAME = "v-progressindicator";
    Element wrapper = DOM.createDiv();
    Element indicator = DOM.createDiv();

    protected boolean indeterminate = false;
    protected float state = 0.0f;
    private boolean enabled;

    public VProgressIndicator() {
        setElement(DOM.createDiv());
        getElement().appendChild(wrapper);
        setStyleName(CLASSNAME);
        wrapper.appendChild(indicator);
        indicator.setClassName(CLASSNAME + "-indicator");
        wrapper.setClassName(CLASSNAME + "-wrapper");
    }

    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
        setStyleName(CLASSNAME + "-indeterminate", indeterminate);
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
        this.enabled = enabled;
        setStyleName("v-disabled", !enabled);

    }

}
