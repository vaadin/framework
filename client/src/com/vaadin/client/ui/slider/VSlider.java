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
//
package com.vaadin.client.ui.slider;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ContainerResizedListener;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.Field;
import com.vaadin.client.ui.SimpleFocusablePanel;
import com.vaadin.client.ui.VLazyExecutor;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.shared.ui.slider.SliderOrientation;

public class VSlider extends SimpleFocusablePanel implements Field,
        ContainerResizedListener, HasValue<Double> {

    public static final String CLASSNAME = "v-slider";

    /**
     * Minimum size (width or height, depending on orientation) of the slider
     * base.
     */
    private static final int MIN_SIZE = 50;

    protected ApplicationConnection client;

    protected String id;

    protected boolean immediate;
    protected boolean disabled;
    protected boolean readonly;

    private int acceleration = 1;
    protected double min;
    protected double max;
    protected int resolution;
    protected Double value;
    protected SliderOrientation orientation = SliderOrientation.HORIZONTAL;

    private final HTML feedback = new HTML("", false);
    private final VOverlay feedbackPopup = new VOverlay(true, false, true) {

        @Override
        public void show() {
            super.show();
            updateFeedbackPosition();
        }
    };

    /* DOM element for slider's base */
    private final Element base;
    private final int BASE_BORDER_WIDTH = 1;

    /* DOM element for slider's handle */
    private final Element handle;

    /* DOM element for decrement arrow */
    private final Element smaller;

    /* DOM element for increment arrow */
    private final Element bigger;

    /* Temporary dragging/animation variables */
    private boolean dragging = false;

    private VLazyExecutor delayedValueUpdater = new VLazyExecutor(100,
            new ScheduledCommand() {

                @Override
                public void execute() {
                    fireValueChanged();
                    acceleration = 1;
                }
            });

    public VSlider() {
        super();

        base = DOM.createDiv();
        handle = DOM.createDiv();
        smaller = DOM.createDiv();
        bigger = DOM.createDiv();

        setStyleName(CLASSNAME);

        DOM.appendChild(getElement(), bigger);
        DOM.appendChild(getElement(), smaller);
        DOM.appendChild(getElement(), base);
        DOM.appendChild(base, handle);

        // Hide initially
        DOM.setStyleAttribute(smaller, "display", "none");
        DOM.setStyleAttribute(bigger, "display", "none");
        DOM.setStyleAttribute(handle, "visibility", "hidden");

        sinkEvents(Event.MOUSEEVENTS | Event.ONMOUSEWHEEL | Event.KEYEVENTS
                | Event.FOCUSEVENTS | Event.TOUCHEVENTS);


        feedbackPopup.setWidget(feedback);
    }

    @Override
    public void setStyleName(String style) {
        updateStyleNames(style);
    }

    @Override
    public void setStylePrimaryName(String style) {
        updateStyleNames(style);
    }

    protected void updateStyleNames(String newPrimaryStyleName) {

        feedbackPopup.removeStyleName(getStylePrimaryName() + "-feedback");
        removeStyleName(getStylePrimaryName() + "-vertical");

        super.setStyleName(newPrimaryStyleName);

        feedbackPopup.addStyleName(getStylePrimaryName() + "-feedback");
        base.setClassName(getStylePrimaryName() + "-base");
        handle.setClassName(getStylePrimaryName() + "-handle");
        smaller.setClassName(getStylePrimaryName() + "-smaller");
        bigger.setClassName(getStylePrimaryName() + "-bigger");

        if (isVertical()) {
            addStyleName(getStylePrimaryName() + "-vertical");
        }
    }

    void setFeedbackValue(double value) {
        String currentValue = "" + value;
        if (resolution == 0) {
            currentValue = "" + new Double(value).intValue();
        }
        feedback.setText(currentValue);
    }

    private void updateFeedbackPosition() {
        if (isVertical()) {
            feedbackPopup.setPopupPosition(
                    DOM.getAbsoluteLeft(handle) + handle.getOffsetWidth(),
                    DOM.getAbsoluteTop(handle) + handle.getOffsetHeight() / 2
                            - feedbackPopup.getOffsetHeight() / 2);
        } else {
            feedbackPopup.setPopupPosition(
                    DOM.getAbsoluteLeft(handle) + handle.getOffsetWidth() / 2
                            - feedbackPopup.getOffsetWidth() / 2,
                    DOM.getAbsoluteTop(handle)
                            - feedbackPopup.getOffsetHeight());
        }
    }

    void buildBase() {
        final String styleAttribute = isVertical() ? "height" : "width";
        final String oppositeStyleAttribute = isVertical() ? "width" : "height";
        final String domProperty = isVertical() ? "offsetHeight"
                : "offsetWidth";

        // clear unnecessary opposite style attribute
        DOM.setStyleAttribute(base, oppositeStyleAttribute, "");

        final Element p = DOM.getParent(getElement());
        if (DOM.getElementPropertyInt(p, domProperty) > 50) {
            if (isVertical()) {
                setHeight();
            } else {
                DOM.setStyleAttribute(base, styleAttribute, "");
            }
        } else {
            // Set minimum size and adjust after all components have
            // (supposedly) been drawn completely.
            DOM.setStyleAttribute(base, styleAttribute, MIN_SIZE + "px");
            Scheduler.get().scheduleDeferred(new Command() {

                @Override
                public void execute() {
                    final Element p = DOM.getParent(getElement());
                    if (DOM.getElementPropertyInt(p, domProperty) > (MIN_SIZE + 5)) {
                        if (isVertical()) {
                            setHeight();
                        } else {
                            DOM.setStyleAttribute(base, styleAttribute, "");
                        }
                        // Ensure correct position
                        setValue(value, false);
                    }
                }
            });
        }

        if (!isVertical()) {
            // Draw handle with a delay to allow base to gain maximum width
            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    buildHandle();
                    setValue(value, false);
                }
            });
        } else {
            buildHandle();
            setValue(value, false);
        }

        // TODO attach listeners for focusing and arrow keys
    }

    void buildHandle() {
        final String handleAttribute = isVertical() ? "marginTop"
                : "marginLeft";
        final String oppositeHandleAttribute = isVertical() ? "marginLeft"
                : "marginTop";

        DOM.setStyleAttribute(handle, handleAttribute, "0");

        // clear unnecessary opposite handle attribute
        DOM.setStyleAttribute(handle, oppositeHandleAttribute, "");

        // Restore visibility
        DOM.setStyleAttribute(handle, "visibility", "visible");

    }

    @Override
    public void onBrowserEvent(Event event) {
        if (disabled || readonly) {
            return;
        }
        final Element targ = DOM.eventGetTarget(event);

        if (DOM.eventGetType(event) == Event.ONMOUSEWHEEL) {
            processMouseWheelEvent(event);
        } else if (dragging || targ == handle) {
            processHandleEvent(event);
        } else if (targ == smaller) {
            decreaseValue(true);
        } else if (targ == bigger) {
            increaseValue(true);
        } else if (DOM.eventGetType(event) == Event.MOUSEEVENTS) {
            processBaseEvent(event);
        } else if ((BrowserInfo.get().isGecko() && DOM.eventGetType(event) == Event.ONKEYPRESS)
                || (!BrowserInfo.get().isGecko() && DOM.eventGetType(event) == Event.ONKEYDOWN)) {

            if (handleNavigation(event.getKeyCode(), event.getCtrlKey(),
                    event.getShiftKey())) {

                feedbackPopup.show();

                delayedValueUpdater.trigger();

                DOM.eventPreventDefault(event);
                DOM.eventCancelBubble(event, true);
            }
        } else if (targ.equals(getElement())
                && DOM.eventGetType(event) == Event.ONFOCUS) {
            feedbackPopup.show();
        } else if (targ.equals(getElement())
                && DOM.eventGetType(event) == Event.ONBLUR) {
            feedbackPopup.hide();
        } else if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
            feedbackPopup.show();
        }
        if (Util.isTouchEvent(event)) {
            event.preventDefault(); // avoid simulated events
            event.stopPropagation();
        }
    }

    private void processMouseWheelEvent(final Event event) {
        final int dir = DOM.eventGetMouseWheelVelocityY(event);

        if (dir < 0) {
            increaseValue(false);
        } else {
            decreaseValue(false);
        }

        delayedValueUpdater.trigger();

        DOM.eventPreventDefault(event);
        DOM.eventCancelBubble(event, true);
    }

    private void processHandleEvent(Event event) {
        switch (DOM.eventGetType(event)) {
        case Event.ONMOUSEDOWN:
        case Event.ONTOUCHSTART:
            if (!disabled && !readonly) {
                focus();
                feedbackPopup.show();
                dragging = true;
                handle.setClassName(getStylePrimaryName() + "-handle");
                handle.addClassName(getStylePrimaryName() + "-handle-active");

                DOM.setCapture(getElement());
                DOM.eventPreventDefault(event); // prevent selecting text
                DOM.eventCancelBubble(event, true);
                event.stopPropagation();
                VConsole.log("Slider move start");
            }
            break;
        case Event.ONMOUSEMOVE:
        case Event.ONTOUCHMOVE:
            if (dragging) {
                VConsole.log("Slider move");
                setValueByEvent(event, false);
                updateFeedbackPosition();
                event.stopPropagation();
            }
            break;
        case Event.ONTOUCHEND:
            feedbackPopup.hide();
        case Event.ONMOUSEUP:
            // feedbackPopup.hide();
            VConsole.log("Slider move end");
            dragging = false;
            handle.setClassName(getStylePrimaryName() + "-handle");
            DOM.releaseCapture(getElement());
            setValueByEvent(event, true);
            event.stopPropagation();
            break;
        default:
            break;
        }
    }

    private void processBaseEvent(Event event) {
        if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
            if (!disabled && !readonly && !dragging) {
                setValueByEvent(event, true);
                DOM.eventCancelBubble(event, true);
            }
        }
    }

    private void decreaseValue(boolean updateToServer) {
        setValue(new Double(value.doubleValue() - Math.pow(10, -resolution)),
                updateToServer);
    }

    private void increaseValue(boolean updateToServer) {
        setValue(new Double(value.doubleValue() + Math.pow(10, -resolution)),
                updateToServer);
    }

    private void setValueByEvent(Event event, boolean updateToServer) {
        double v = min; // Fallback to min

        final int coord = getEventPosition(event);

        final int handleSize, baseSize, baseOffset;
        if (isVertical()) {
            handleSize = handle.getOffsetHeight();
            baseSize = base.getOffsetHeight();
            baseOffset = base.getAbsoluteTop() - Window.getScrollTop()
                    - handleSize / 2;
        } else {
            handleSize = handle.getOffsetWidth();
            baseSize = base.getOffsetWidth();
            baseOffset = base.getAbsoluteLeft() - Window.getScrollLeft()
                    + handleSize / 2;
        }

        if (isVertical()) {
            v = ((baseSize - (coord - baseOffset)) / (double) (baseSize - handleSize))
                    * (max - min) + min;
        } else {
            v = ((coord - baseOffset) / (double) (baseSize - handleSize))
                    * (max - min) + min;
        }

        if (v < min) {
            v = min;
        } else if (v > max) {
            v = max;
        }

        setValue(v, updateToServer);
    }

    /**
     * TODO consider extracting touches support to an impl class specific for
     * webkit (only browser that really supports touches).
     * 
     * @param event
     * @return
     */
    protected int getEventPosition(Event event) {
        if (isVertical()) {
            return Util.getTouchOrMouseClientY(event);
        } else {
            return Util.getTouchOrMouseClientX(event);
        }
    }

    @Override
    public void iLayout() {
        if (isVertical()) {
            setHeight();
        }
        // Update handle position
        setValue(value, false);
    }

    private void setHeight() {
        // Calculate decoration size
        DOM.setStyleAttribute(base, "height", "0");
        DOM.setStyleAttribute(base, "overflow", "hidden");
        int h = DOM.getElementPropertyInt(getElement(), "offsetHeight");
        if (h < MIN_SIZE) {
            h = MIN_SIZE;
        }
        DOM.setStyleAttribute(base, "height", h + "px");
        DOM.setStyleAttribute(base, "overflow", "");
    }

    private void fireValueChanged() {
        ValueChangeEvent.fire(VSlider.this, value);
    }

    /**
     * Handles the keyboard events handled by the Slider
     * 
     * @param event
     *            The keyboard event received
     * @return true iff the navigation event was handled
     */
    public boolean handleNavigation(int keycode, boolean ctrl, boolean shift) {

        // No support for ctrl moving
        if (ctrl) {
            return false;
        }

        if ((keycode == getNavigationUpKey() && isVertical())
                || (keycode == getNavigationRightKey() && !isVertical())) {
            if (shift) {
                for (int a = 0; a < acceleration; a++) {
                    increaseValue(false);
                }
                acceleration++;
            } else {
                increaseValue(false);
            }
            return true;
        } else if (keycode == getNavigationDownKey() && isVertical()
                || (keycode == getNavigationLeftKey() && !isVertical())) {
            if (shift) {
                for (int a = 0; a < acceleration; a++) {
                    decreaseValue(false);
                }
                acceleration++;
            } else {
                decreaseValue(false);
            }
            return true;
        }

        return false;
    }

    /**
     * Get the key that increases the vertical slider. By default it is the up
     * arrow key but by overriding this you can change the key to whatever you
     * want.
     * 
     * @return The keycode of the key
     */
    protected int getNavigationUpKey() {
        return KeyCodes.KEY_UP;
    }

    /**
     * Get the key that decreases the vertical slider. By default it is the down
     * arrow key but by overriding this you can change the key to whatever you
     * want.
     * 
     * @return The keycode of the key
     */
    protected int getNavigationDownKey() {
        return KeyCodes.KEY_DOWN;
    }

    /**
     * Get the key that decreases the horizontal slider. By default it is the
     * left arrow key but by overriding this you can change the key to whatever
     * you want.
     * 
     * @return The keycode of the key
     */
    protected int getNavigationLeftKey() {
        return KeyCodes.KEY_LEFT;
    }

    /**
     * Get the key that increases the horizontal slider. By default it is the
     * right arrow key but by overriding this you can change the key to whatever
     * you want.
     * 
     * @return The keycode of the key
     */
    protected int getNavigationRightKey() {
        return KeyCodes.KEY_RIGHT;
    }

    public void setConnection(ApplicationConnection client) {
        this.client = client;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setReadOnly(boolean readonly) {
        this.readonly = readonly;
    }

    private boolean isVertical() {
        return orientation == SliderOrientation.VERTICAL;
    }

    public void setOrientation(SliderOrientation orientation) {
        if (this.orientation != orientation) {
            this.orientation = orientation;
            updateStyleNames(getStylePrimaryName());
        }
    }

    public void setMinValue(double value) {
        min = value;
    }

    public void setMaxValue(double value) {
        max = value;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
            ValueChangeHandler<Double> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void setValue(Double value) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }

        // Update handle position
        final String styleAttribute = isVertical() ? "marginTop" : "marginLeft";
        final String domProperty = isVertical() ? "offsetHeight"
                : "offsetWidth";
        final int handleSize = Integer.parseInt(DOM.getElementProperty(handle,
                domProperty));
        final int baseSize = Integer.parseInt(DOM.getElementProperty(base,
                domProperty)) - (2 * BASE_BORDER_WIDTH);

        final int range = baseSize - handleSize;
        double v = value.doubleValue();

        // Round value to resolution
        if (resolution > 0) {
            v = Math.round(v * Math.pow(10, resolution));
            v = v / Math.pow(10, resolution);
        } else {
            v = Math.round(v);
        }
        final double valueRange = max - min;
        double p = 0;
        if (valueRange > 0) {
            p = range * ((v - min) / valueRange);
        }
        if (p < 0) {
            p = 0;
        }
        if (isVertical()) {
            p = range - p;
        }
        final double pos = p;

        DOM.setStyleAttribute(handle, styleAttribute, (Math.round(pos)) + "px");

        // Update value
        this.value = new Double(v);
        setFeedbackValue(v);
    }

    @Override
    public void setValue(Double value, boolean fireEvents) {
        if (value == null) {
            return;
        }

        setValue(value);

        if (fireEvents) {
            fireValueChanged();
        }
    }
}
