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
package com.vaadin.client.ui.colorpicker;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.ui.SubPartAware;

/**
 * Client side implementation for ColorPickerGradient.
 * 
 * @since 7.0.0
 * 
 */
public class VColorPickerGradient extends FocusPanel implements
        MouseDownHandler, MouseUpHandler, MouseMoveHandler, SubPartAware {

    /** Set the CSS class name to allow styling. */
    public static final String CLASSNAME = "v-colorpicker-gradient";
    public static final String CLASSNAME_BACKGROUND = CLASSNAME + "-background";
    public static final String CLASSNAME_FOREGROUND = CLASSNAME + "-foreground";
    public static final String CLASSNAME_LOWERBOX = CLASSNAME + "-lowerbox";
    public static final String CLASSNAME_HIGHERBOX = CLASSNAME + "-higherbox";
    public static final String CLASSNAME_CONTAINER = CLASSNAME + "-container";
    public static final String CLASSNAME_CLICKLAYER = CLASSNAME + "-clicklayer";
    private static final String CLICKLAYER_ID = "clicklayer";

    private final HTML background;
    private final HTML foreground;
    private final HTML lowercross;
    private final HTML highercross;
    private final HTML clicklayer;
    private final AbsolutePanel container;

    private boolean mouseIsDown = false;

    private int cursorX;
    private int cursorY;

    private int width = 220;
    private int height = 220;

    /**
     * Instantiates the client side component for a color picker gradient.
     */
    public VColorPickerGradient() {
        super();

        setStyleName(CLASSNAME);

        background = new HTML();
        background.setStyleName(CLASSNAME_BACKGROUND);
        background.setPixelSize(width, height);

        foreground = new HTML();
        foreground.setStyleName(CLASSNAME_FOREGROUND);
        foreground.setPixelSize(width, height);

        clicklayer = new HTML();
        clicklayer.setStyleName(CLASSNAME_CLICKLAYER);
        clicklayer.setPixelSize(width, height);
        clicklayer.addMouseDownHandler(this);
        clicklayer.addMouseUpHandler(this);
        clicklayer.addMouseMoveHandler(this);

        lowercross = new HTML();
        lowercross.setPixelSize(width / 2, height / 2);
        lowercross.setStyleName(CLASSNAME_LOWERBOX);

        highercross = new HTML();
        highercross.setPixelSize(width / 2, height / 2);
        highercross.setStyleName(CLASSNAME_HIGHERBOX);

        container = new AbsolutePanel();
        container.setStyleName(CLASSNAME_CONTAINER);
        container.setPixelSize(width, height);
        container.add(background, 0, 0);
        container.add(foreground, 0, 0);
        container.add(lowercross, 0, height / 2);
        container.add(highercross, width / 2, 0);
        container.add(clicklayer, 0, 0);

        add(container);
    }

    /**
     * Returns the latest x-coordinate for pressed-down mouse cursor.
     */
    protected int getCursorX() {
        return cursorX;
    }

    /**
     * Returns the latest y-coordinate for pressed-down mouse cursor.
     */
    protected int getCursorY() {
        return cursorY;
    }

    /**
     * Sets the given css color as the background.
     * 
     * @param bgColor
     */
    protected void setBGColor(String bgColor) {
        if (bgColor == null) {
            background.getElement().getStyle().clearBackgroundColor();
        } else {
            background.getElement().getStyle().setBackgroundColor(bgColor);
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        event.preventDefault();

        mouseIsDown = true;
        setCursor(event.getX(), event.getY());
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        event.preventDefault();
        mouseIsDown = false;
        setCursor(event.getX(), event.getY());

        cursorX = event.getX();
        cursorY = event.getY();
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        event.preventDefault();

        if (mouseIsDown) {
            setCursor(event.getX(), event.getY());
        }
    }

    /**
     * Sets the latest coordinates for pressed-down mouse cursor and updates the
     * cross elements.
     * 
     * @param x
     * @param y
     */
    public void setCursor(int x, int y) {
        cursorX = x;
        cursorY = y;
        if (x >= 0) {
            lowercross.getElement().getStyle().setWidth(x, Unit.PX);
        }
        if (y >= 0) {
            lowercross.getElement().getStyle().setTop(y, Unit.PX);
        }
        if (y >= 0) {
            lowercross.getElement().getStyle().setHeight(height - y, Unit.PX);
        }

        if (x >= 0) {
            highercross.getElement().getStyle().setWidth(width - x, Unit.PX);
        }
        if (x >= 0) {
            highercross.getElement().getStyle().setLeft(x, Unit.PX);
        }
        if (y >= 0) {
            highercross.getElement().getStyle().setHeight(y, Unit.PX);
        }
    }

    @Override
    public com.google.gwt.user.client.Element getSubPartElement(String subPart) {
        if (subPart.equals(CLICKLAYER_ID)) {
            return clicklayer.getElement();
        }

        return null;
    }

    @Override
    public String getSubPartName(com.google.gwt.user.client.Element subElement) {
        if (clicklayer.getElement().isOrHasChild(subElement)) {
            return CLICKLAYER_ID;
        }

        return null;
    }
}
