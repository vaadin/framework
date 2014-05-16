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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.Util;
import com.vaadin.client.VTooltip;
import com.vaadin.client.ui.aria.AriaHelper;
import com.vaadin.client.ui.aria.HandlesAriaInvalid;
import com.vaadin.client.ui.aria.HandlesAriaRequired;

public class VCheckBox extends com.google.gwt.user.client.ui.CheckBox implements
        Field, HandlesAriaInvalid, HandlesAriaRequired {

    public static final String CLASSNAME = "v-checkbox";

    /** For internal use only. May be removed or replaced in the future. */
    public String id;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean immediate;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public Element errorIndicatorElement;

    /** For internal use only. May be removed or replaced in the future. */
    public Icon icon;

    public VCheckBox() {
        setStyleName(CLASSNAME);

        Element el = DOM.getFirstChild(getElement());
        while (el != null) {
            DOM.sinkEvents(el,
                    (DOM.getEventsSunk(el) | VTooltip.TOOLTIP_EVENTS));
            el = DOM.getNextSibling(el);
        }

        if (BrowserInfo.get().isWebkit()) {
            // Webkit does not focus non-text input elements on click
            // (#11854)
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setFocus(true);
                }
            });
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        if (icon != null && (event.getTypeInt() == Event.ONCLICK)
                && (DOM.eventGetTarget(event) == icon.getElement())) {
            // Click on icon should do nothing if widget is disabled
            if (isEnabled()) {
                setValue(!getValue());
            }
        }
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONLOAD) {
            Util.notifyParentOfSizeChange(this, true);
        }
    }

    /**
     * Gives access to the input element.
     * 
     * @return Element of the CheckBox itself
     */
    private Element getCheckBoxElement() {
        // FIXME: Would love to use a better way to access the checkbox element
        return getElement().getFirstChildElement();
    }

    @Override
    public void setAriaRequired(boolean required) {
        AriaHelper.handleInputRequired(getCheckBoxElement(), required);
    }

    @Override
    public void setAriaInvalid(boolean invalid) {
        AriaHelper.handleInputInvalid(getCheckBoxElement(), invalid);
    }
}
