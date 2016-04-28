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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Client side implementation for ColorPickerArea.
 * 
 * @since 7.0.0
 */
public class VColorPickerArea extends Widget implements ClickHandler, HasHTML,
        HasClickHandlers {

    public static final String CLASSNAME = "v-colorpicker";
    private String color = null;

    private boolean isOpen;

    private HTML caption;
    private HTML area;

    /**
     * Initializes an area-style color picker widget.
     */
    public VColorPickerArea() {
        super();
        setElement(DOM.createDiv());
        setStyleName(CLASSNAME);

        caption = new HTML();
        caption.addStyleName("v-caption");
        caption.setWidth("");

        area = new HTML();
        area.setStylePrimaryName(getStylePrimaryName() + "-area");

        getElement().appendChild(caption.getElement());
        getElement().appendChild(area.getElement());

        addClickHandler(this);
    }

    /**
     * Adds a click handler to the widget and sinks the click event.
     * 
     * @param handler
     * @return HandlerRegistration used to remove the handler
     */
    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    @Override
    public void onClick(ClickEvent event) {
        setOpen(!isOpen);
    }

    @Override
    public void onBrowserEvent(Event event) {
        int type = DOM.eventGetType(event);
        switch (type) {
        case Event.ONCLICK:
            if (DOM.isOrHasChild(area.getElement(), DOM.eventGetTarget(event))) {
                super.onBrowserEvent(event);
            }
            break;
        default:
            super.onBrowserEvent(event);
        }
    }

    /**
     * Mark the popup opened/closed.
     * 
     * @param open
     */
    public void setOpen(boolean open) {
        isOpen = open;
    }

    /**
     * Check the popup's marked state.
     * 
     * @return true if the popup has been marked being open, false otherwise.
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Sets the caption's content to the given text.
     * 
     * @param text
     * 
     * @see Label#setText(String)
     */
    @Override
    public void setText(String text) {
        caption.setText(text);
    }

    /**
     * Gets the caption's contents as text.
     * 
     * @return the caption's text
     */
    @Override
    public String getText() {
        return caption.getText();
    }

    /**
     * Sets the caption's content to the given HTML.
     * 
     * @param html
     */
    @Override
    public void setHTML(String html) {
        caption.setHTML(html);
    }

    /**
     * Gets the caption's contents as HTML.
     * 
     * @return the caption's HTML
     */
    @Override
    public String getHTML() {
        return caption.getHTML();
    }

    /**
     * Sets the color for the area.
     * 
     * @param color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Update the color area with the currently set color.
     */
    public void refreshColor() {
        if (color != null) {
            // Set the color
            area.getElement().getStyle().setProperty("background", color);
        }
    }

    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);
        area.setStylePrimaryName(getStylePrimaryName() + "-area");
    }

    /**
     * Sets the color area's height. This height does not include caption or
     * decorations such as border, margin, and padding.
     */
    @Override
    public void setHeight(String height) {
        area.setHeight(height);
    }

    /**
     * Sets the color area's width. This width does not include caption or
     * decorations such as border, margin, and padding.
     */
    @Override
    public void setWidth(String width) {
        area.setWidth(width);
    }

}
