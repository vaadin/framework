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
import com.google.gwt.user.client.ui.Button;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.Util;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.button.ButtonServerRpc;

public class VNativeButton extends Button implements ClickHandler {

    public static final String CLASSNAME = "v-nativebutton";

    /** For internal use only. May be removed or replaced in the future. */
    public String paintableId;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public ButtonServerRpc buttonRpcProxy;

    /** For internal use only. May be removed or replaced in the future. */
    public Element errorIndicatorElement;

    /** For internal use only. May be removed or replaced in the future. */
    public final Element captionElement = DOM.createSpan();

    /** For internal use only. May be removed or replaced in the future. */
    public Icon icon;

    /**
     * Helper flag to handle special-case where the button is moved from under
     * mouse while clicking it. In this case mouse leaves the button without
     * moving.
     */
    private boolean clickPending;

    private boolean cancelNextClick = false;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean disableOnClick = false;

    public VNativeButton() {
        setStyleName(CLASSNAME);

        getElement().appendChild(captionElement);
        captionElement.setClassName(getStyleName() + "-caption");

        addClickHandler(this);

        sinkEvents(Event.ONMOUSEDOWN | Event.ONLOAD | Event.ONMOUSEMOVE
                | Event.ONFOCUS);
    }

    @Override
    public void setText(String text) {
        captionElement.setInnerText(text);
    }

    @Override
    public void setHTML(String html) {
        captionElement.setInnerHTML(html);
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);

        if (DOM.eventGetType(event) == Event.ONLOAD) {
            Util.notifyParentOfSizeChange(this, true);

        } else if (DOM.eventGetType(event) == Event.ONMOUSEDOWN
                && event.getButton() == Event.BUTTON_LEFT) {
            clickPending = true;

        } else if (DOM.eventGetType(event) == Event.ONMOUSEMOVE) {
            clickPending = false;
        } else if (DOM.eventGetType(event) == Event.ONMOUSEOUT) {
            if (clickPending) {
                click();
            }
            clickPending = false;
        } else if (event.getTypeInt() == Event.ONFOCUS) {
            if (BrowserInfo.get().isIE()
                    && BrowserInfo.get().getBrowserMajorVersion() < 11
                    && clickPending) {
                /*
                 * The focus event will mess up IE and IE will not trigger the
                 * mouse up event (which in turn triggers the click event) until
                 * the mouse is moved. This will result in it appearing as a
                 * native button not triggering the event. So we manually
                 * trigger the click here and cancel the next original event
                 * which will occur on the next mouse move. See ticket #11094
                 * for details.
                 */
                click();
                clickPending = false;
                cancelNextClick = true;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event
     * .dom.client.ClickEvent)
     */
    @Override
    public void onClick(ClickEvent event) {
        if (paintableId == null || client == null || cancelNextClick) {
            cancelNextClick = false;
            return;
        }

        if (BrowserInfo.get().isWebkit()) {
            // Webkit does not focus non-text input elements on click
            // (#11854)
            setFocus(true);
        }
        if (disableOnClick) {
            setEnabled(false);
            // FIXME: This should be moved to NativeButtonConnector along with
            // buttonRpcProxy
            addStyleName(ApplicationConnection.DISABLED_CLASSNAME);
            buttonRpcProxy.disableOnClick();
        }

        // Add mouse details
        MouseEventDetails details = MouseEventDetailsBuilder
                .buildMouseEventDetails(event.getNativeEvent(), getElement());
        buttonRpcProxy.click(details);

        clickPending = false;
    }

}
