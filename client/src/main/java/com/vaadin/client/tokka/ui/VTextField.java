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

package com.vaadin.client.tokka.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.vaadin.client.ui.Field;

/**
 * This class represents a basic text input field with one row.
 * 
 * @author Vaadin Ltd.
 * 
 */
public class VTextField extends TextBoxBase implements Field {

    public static final String CLASSNAME = "v-textfield";

    public VTextField() {
        this(DOM.createInputText());
        setStyleName(CLASSNAME);
    }

    protected VTextField(Element node) {
        super(node);
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
}
