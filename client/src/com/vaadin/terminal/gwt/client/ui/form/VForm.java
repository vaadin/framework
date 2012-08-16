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

package com.vaadin.terminal.gwt.client.ui.form;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.VErrorMessage;
import com.vaadin.terminal.gwt.client.ui.Icon;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler;

public class VForm extends ComplexPanel implements KeyDownHandler {

    protected String id;

    public static final String CLASSNAME = "v-form";

    Widget lo;
    Element legend = DOM.createLegend();
    Element caption = DOM.createSpan();
    Element desc = DOM.createDiv();
    Icon icon;
    VErrorMessage errorMessage = new VErrorMessage();

    Element fieldContainer = DOM.createDiv();

    Element footerContainer = DOM.createDiv();

    Element fieldSet = DOM.createFieldSet();

    Widget footer;

    ApplicationConnection client;

    ShortcutActionHandler shortcutHandler;

    HandlerRegistration keyDownRegistration;

    public VForm() {
        setElement(DOM.createDiv());
        getElement().appendChild(fieldSet);
        setStyleName(CLASSNAME);
        fieldSet.appendChild(legend);
        legend.appendChild(caption);
        desc.setClassName("v-form-description");
        fieldSet.appendChild(desc); // Adding description for initial padding
                                    // measurements, removed later if no
                                    // description is set
        fieldContainer.setClassName(CLASSNAME + "-content");
        fieldSet.appendChild(fieldContainer);
        errorMessage.setVisible(false);
        errorMessage.setStyleName(CLASSNAME + "-errormessage");
        fieldSet.appendChild(errorMessage.getElement());
        fieldSet.appendChild(footerContainer);
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        shortcutHandler.handleKeyboardEvent(Event.as(event.getNativeEvent()));
    }

    @Override
    protected void add(Widget child, Element container) {
        // Overridden to allow VFormPaintable to call this. Should be removed
        // once functionality from VFormPaintable is moved to VForm.
        super.add(child, container);
    }
}
