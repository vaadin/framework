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

package com.vaadin.client.ui.panel;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Focusable;
import com.vaadin.client.ui.Icon;
import com.vaadin.client.ui.ShortcutActionHandler;
import com.vaadin.client.ui.ShortcutActionHandler.ShortcutActionHandlerOwner;
import com.vaadin.client.ui.TouchScrollDelegate;
import com.vaadin.client.ui.TouchScrollDelegate.TouchScrollHandler;

public class VPanel extends SimplePanel implements ShortcutActionHandlerOwner,
        Focusable {

    public static final String CLASSNAME = "v-panel";

    ApplicationConnection client;

    String id;

    final Element captionNode = DOM.createDiv();

    private final Element captionText = DOM.createSpan();

    private Icon icon;

    final Element bottomDecoration = DOM.createDiv();

    final Element contentNode = DOM.createDiv();

    private Element errorIndicatorElement;

    ShortcutActionHandler shortcutHandler;

    int scrollTop;

    int scrollLeft;

    private TouchScrollHandler touchScrollHandler;

    public VPanel() {
        super();
        DivElement captionWrap = Document.get().createDivElement();
        captionWrap.appendChild(captionNode);
        captionNode.appendChild(captionText);

        captionWrap.setClassName(CLASSNAME + "-captionwrap");
        captionNode.setClassName(CLASSNAME + "-caption");
        contentNode.setClassName(CLASSNAME + "-content");
        bottomDecoration.setClassName(CLASSNAME + "-deco");

        getElement().appendChild(captionWrap);

        /*
         * Make contentNode focusable only by using the setFocus() method. This
         * behaviour can be changed by invoking setTabIndex() in the serverside
         * implementation
         */
        contentNode.setTabIndex(-1);

        getElement().appendChild(contentNode);

        getElement().appendChild(bottomDecoration);
        setStyleName(CLASSNAME);
        DOM.sinkEvents(getElement(), Event.ONKEYDOWN);
        DOM.sinkEvents(contentNode, Event.ONSCROLL | Event.TOUCHEVENTS);

        contentNode.getStyle().setProperty("position", "relative");
        getElement().getStyle().setProperty("overflow", "hidden");

        makeScrollable();
    }

    /**
     * Sets the keyboard focus on the Panel
     * 
     * @param focus
     *            Should the panel have focus or not.
     */
    public void setFocus(boolean focus) {
        if (focus) {
            getContainerElement().focus();
        } else {
            getContainerElement().blur();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.Focusable#focus()
     */

    @Override
    public void focus() {
        setFocus(true);

    }

    @Override
    protected Element getContainerElement() {
        return contentNode;
    }

    void setCaption(String text) {
        DOM.setInnerHTML(captionText, text);
    }

    void setErrorIndicatorVisible(boolean showError) {
        if (showError) {
            if (errorIndicatorElement == null) {
                errorIndicatorElement = DOM.createSpan();
                DOM.setElementProperty(errorIndicatorElement, "className",
                        "v-errorindicator");
                DOM.sinkEvents(errorIndicatorElement, Event.MOUSEEVENTS);
                sinkEvents(Event.MOUSEEVENTS);
            }
            DOM.insertBefore(captionNode, errorIndicatorElement, captionText);
        } else if (errorIndicatorElement != null) {
            DOM.removeChild(captionNode, errorIndicatorElement);
            errorIndicatorElement = null;
        }
    }

    void setIconUri(String iconUri, ApplicationConnection client) {
        if (iconUri == null) {
            if (icon != null) {
                DOM.removeChild(captionNode, icon.getElement());
                icon = null;
            }
        } else {
            if (icon == null) {
                icon = new Icon(client);
                DOM.insertChild(captionNode, icon.getElement(), 0);
            }
            icon.setUri(iconUri);
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);

        final Element target = DOM.eventGetTarget(event);
        final int type = DOM.eventGetType(event);
        if (type == Event.ONKEYDOWN && shortcutHandler != null) {
            shortcutHandler.handleKeyboardEvent(event);
            return;
        }
        if (type == Event.ONSCROLL) {
            int newscrollTop = DOM.getElementPropertyInt(contentNode,
                    "scrollTop");
            int newscrollLeft = DOM.getElementPropertyInt(contentNode,
                    "scrollLeft");
            if (client != null
                    && (newscrollLeft != scrollLeft || newscrollTop != scrollTop)) {
                scrollLeft = newscrollLeft;
                scrollTop = newscrollTop;
                client.updateVariable(id, "scrollTop", scrollTop, false);
                client.updateVariable(id, "scrollLeft", scrollLeft, false);
            }
        }
    }

    @Override
    public ShortcutActionHandler getShortcutActionHandler() {
        return shortcutHandler;
    }

    /**
     * Ensures the panel is scrollable eg. after style name changes
     */
    void makeScrollable() {
        if (touchScrollHandler == null) {
            touchScrollHandler = TouchScrollDelegate.enableTouchScrolling(this);
        }
        touchScrollHandler.addElement(contentNode);
    }
}
