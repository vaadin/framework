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
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.VErrorMessage;

public class VForm extends ComplexPanel implements KeyDownHandler {

    public static final String CLASSNAME = "v-form";

    /** For internal use only. May be removed or replaced in the future. */
    public String id;

    /** For internal use only. May be removed or replaced in the future. */
    public Widget lo;

    /** For internal use only. May be removed or replaced in the future. */
    public Element legend = DOM.createLegend();

    /** For internal use only. May be removed or replaced in the future. */
    public Element caption = DOM.createSpan();

    /** For internal use only. May be removed or replaced in the future. */
    public Element desc = DOM.createDiv();

    /** For internal use only. May be removed or replaced in the future. */
    public Icon icon;

    /** For internal use only. May be removed or replaced in the future. */
    public VErrorMessage errorMessage = new VErrorMessage();

    /** For internal use only. May be removed or replaced in the future. */
    public Element fieldContainer = DOM.createDiv();

    /** For internal use only. May be removed or replaced in the future. */
    public Element footerContainer = DOM.createDiv();

    /** For internal use only. May be removed or replaced in the future. */
    public Element fieldSet = DOM.createFieldSet();

    /** For internal use only. May be removed or replaced in the future. */
    public Widget footer;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public ShortcutActionHandler shortcutHandler;

    /** For internal use only. May be removed or replaced in the future. */
    public HandlerRegistration keyDownRegistration;

    public VForm() {
        setElement(DOM.createDiv());
        getElement().appendChild(fieldSet);
        setStyleName(CLASSNAME);
        fieldSet.appendChild(legend);
        legend.appendChild(caption);

        fieldSet.appendChild(desc); // Adding description for initial padding
                                    // measurements, removed later if no
                                    // description is set

        fieldSet.appendChild(fieldContainer);
        errorMessage.setVisible(false);

        fieldSet.appendChild(errorMessage.getElement());
        fieldSet.appendChild(footerContainer);

        errorMessage.setOwner(this);
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        updateStyleNames();
    }

    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);
        updateStyleNames();
    }

    protected void updateStyleNames() {
        fieldContainer.setClassName(getStylePrimaryName() + "-content");
        errorMessage.setStyleName(getStylePrimaryName() + "-errormessage");
        desc.setClassName(getStylePrimaryName() + "-description");
        footerContainer.setClassName(getStylePrimaryName() + "-footer");
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        shortcutHandler.handleKeyboardEvent(Event.as(event.getNativeEvent()));
    }

    public void setFooterWidget(Widget footerWidget) {
        if (footer != null) {
            remove(footer);
        }
        if (footerWidget != null) {
            super.add(footerWidget, footerContainer);
        }
        footer = footerWidget;
    }

    public void setLayoutWidget(Widget newLayoutWidget) {
        if (lo != null) {
            remove(lo);
        }
        if (newLayoutWidget != null) {
            super.add(newLayoutWidget, fieldContainer);
        }
        lo = newLayoutWidget;
    }
}
