/*
 * Copyright 2000-2016 Vaadin Ltd.
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
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.TextBoxBase;

/**
 * This class represents a basic text input field with one row.
 *
 * @author Vaadin Ltd.
 *
 */
public class VTextField extends TextBoxBase
        implements Field, FocusHandler, BlurHandler, AbstractTextFieldWidget {

    public static final String CLASSNAME = "v-textfield";
    public static final String CLASSNAME_FOCUS = "focus";

    public VTextField() {
        this(DOM.createInputText());
    }

    protected VTextField(Element node) {
        super(node);
        setStyleName(CLASSNAME);
        addFocusHandler(this);
        addBlurHandler(this);
    }

    public void setMaxLength(int maxLength) {
        if (maxLength >= 0) {
            getElement().setPropertyInt("maxLength", maxLength);
        } else {
            getElement().removeAttribute("maxLength");
        }
    }

    public void setPlaceholder(String placeholder) {
        if (placeholder != null) {
            getElement().setAttribute("placeholder", placeholder);
        } else {
            getElement().removeAttribute("placeholder");
        }
    }

    @Override
    public void onBlur(BlurEvent event) {
        removeStyleDependentName(CLASSNAME_FOCUS);
    }

    @Override
    public void onFocus(FocusEvent event) {
        addStyleDependentName(CLASSNAME_FOCUS);
    }

}
