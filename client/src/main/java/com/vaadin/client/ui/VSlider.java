/*
 * Copyright 2000-2022 Vaadin Ltd.
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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.WidgetUtil;
import com.vaadin.shared.ui.slider.SliderOrientation;

/**
 * Widget class for the Slider component.
 *
 * @author Vaadin Ltd
 *
 */
public class VSlider extends SimpleFocusablePanel
        implements Field, HasValue<Double>, SubPartAware {

    /** Default classname for this widget. */
    public static final String CLASSNAME = "v-slider";

    /**
     * Minimum size (width or height, depending on orientation) of the slider
     * base.
     */
    private static final int MIN_SIZE = 50;

    /**
     * Current client-side communication engine.
     *
     * @deprecated this field is no longer used by the framework
     */
    @Deprecated
    protected ApplicationConnection client;

    /**
     * Current connector id.
     *
     * @deprecated this field is no longer used by the framework
     */
    @Deprecated
    protected String id;

    /** Is this widget disabled. */
    protected boolean disabled;
    /** Is this widget read-only. */
    protected boolean readonly;

    private int acceleration = 1;
    /** Minimum value of slider. */
    protected double min;
    /** Maximum value of slider. */
    protected double max;
    /** Resolution (precision level) of slider. */
    protected int resolution;
    /** Current value of slider. */
    protected Double value;

    private boolean updateValueOnClick;
    /** Current orientation (vertical/horizontal) of slider. */
    protected SliderOrientation orientation = SliderOrientation.HORIZONTAL;

    private final HTML feedback = new HTML("", false);
    @SuppressWarnings("deprecation")
    private final VOverlay feedbackPopup = new VOverlay(true, false) {
        {
            setOwner(VSlider.this);
        }

        @Override
        public void show() {
            super.show();
            updateFeedbackPosition();
        }
    };

    /** DOM element for slider's base. */
    private final Element base;
    private static final int BASE_BORDER_WIDTH = 1;

    /** DOM element for slider's handle. */
    private final Element handle;

    /** DOM element for decrement arrow. */
    private final Element smaller;

    /** DOM element for increment arrow. */
    private final Element bigger;

    /** Temporary dragging/animation variables. */
    private boolean dragging = false;

    private VLazyExecutor delayedValueUpdater = new VLazyExecutor(100, () -> {
        fireValueChanged();
        acceleration = 1;
    });

    /**
     * Constructs a widget for the Slider component.
     */
    public VSlider() {
        super();

        base = DOM.createDiv();
        handle = DOM.createDiv();
        smaller = DOM.createDiv();
        bigger = DOM.createDiv();

        setStyleName(CLASSNAME);

        getElement().appendChild(bigger);
        getElement().appendChild(smaller);
        getElement().appendChild(base);
        base.appendChild(handle);

        // Hide initially
        smaller.getStyle().setDisplay(Display.NONE);
        bigger.getStyle().setDisplay(Display.NONE);

        sinkEvents(Event.MOUSEEVENTS | Event.ONMOUSEWHEEL | Event.KEYEVENTS
                | Event.FOCUSEVENTS | Event.TOUCHEVENTS);

        feedbackPopup.setWidget(feedback);
    }

    @Override
    public void setStyleName(String style) {
        updateStyleNames(style, false);
    }

    @Override
    public void setStylePrimaryName(String style) {
        updateStyleNames(style, true);
    }

    /**
     * Updates the style names for this widget and the child elements.
     *
     * @param styleName
     *            the new style name
     * @param isPrimaryStyleName
     *            {@code true} if the new style name is primary, {@code false}
     *            otherwise
     */
    protected void updateStyleNames(String styleName,
            boolean isPrimaryStyleName) {

        feedbackPopup.removeStyleName(getStylePrimaryName() + "-feedback");
        removeStyleName(getStylePrimaryName() + "-vertical");

        if (isPrimaryStyleName) {
            super.setStylePrimaryName(styleName);
        } else {
            super.setStyleName(styleName);
        }

        feedbackPopup.addStyleName(getStylePrimaryName() + "-feedback");
        base.setClassName(getStylePrimaryName() + "-base");
        handle.setClassName(getStylePrimaryName() + "-handle");
        smaller.setClassName(getStylePrimaryName() + "-smaller");
        bigger.setClassName(getStylePrimaryName() + "-bigger");

        if (isVertical()) {
            addStyleName(getStylePrimaryName() + "-vertical");
        }
    }

    /**
     * Updates the value shown in the feedback pop-up when the slider is moved.
     * The value should match the current value of this widget.
     *
     * @param value
     *            the new value to show
     */
    public void setFeedbackValue(double value) {
        feedback.setText(String.valueOf(value));
    }

    private void updateFeedbackPosition() {
        if (isVertical()) {
            feedbackPopup.setPopupPosition(
                    handle.getAbsoluteLeft() + handle.getOffsetWidth(),
                    handle.getAbsoluteTop() + handle.getOffsetHeight() / 2
                            - feedbackPopup.getOffsetHeight() / 2);
        } else {
            feedbackPopup.setPopupPosition(
                    handle.getAbsoluteLeft() + handle.getOffsetWidth() / 2
                            - feedbackPopup.getOffsetWidth() / 2,
                    handle.getAbsoluteTop() - feedbackPopup.getOffsetHeight());
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void buildBase() {
        final String styleAttribute = isVertical() ? "height" : "width";
        final String oppositeStyleAttribute = isVertical() ? "width" : "height";
        final String domProperty = isVertical() ? "offsetHeight"
                : "offsetWidth";

        // clear unnecessary opposite style attribute
        base.getStyle().clearProperty(oppositeStyleAttribute);

        /*
         * To resolve defect #13681 we should not return from method buildBase()
         * if slider has no parentElement, because such operations as
         * buildHandle() and setValues(), which are needed for Slider, are
         * called at the end of method buildBase(). And these methods will not
         * be called if there is no parentElement. So, instead of returning from
         * method buildBase() if there is no parentElement "if condition" is
         * applied to call code for parentElement only in case it exists.
         */
        if (getElement().hasParentElement()) {
            final Element p = getElement();
            if (p.getPropertyInt(domProperty) > MIN_SIZE) {
                if (isVertical()) {
                    setHeight();
                } else {
                    base.getStyle().clearProperty(styleAttribute);
                }
            } else {
                // Set minimum size and adjust after all components have
                // (supposedly) been drawn completely.
                base.getStyle().setPropertyPx(styleAttribute, MIN_SIZE);
                Scheduler.get().scheduleDeferred(new Command() {

                    @Override
                    public void execute() {
                        final Element p = getElement();
                        if (p.getPropertyInt(domProperty) > MIN_SIZE + 5
                                || propertyNotNullOrEmpty(styleAttribute, p)) {
                            if (isVertical()) {
                                setHeight();
                            } else {
                                base.getStyle().clearProperty(styleAttribute);
                            }
                            // Ensure correct position
                            setValue(value, false);
                        }
                    }

                    // Style has non empty property
                    private boolean propertyNotNullOrEmpty(
                            final String styleAttribute, final Element p) {
                        return p.getStyle().getProperty(styleAttribute) != null
                                && !p.getStyle().getProperty(styleAttribute)
                                        .isEmpty();
                    }
                });
            }
        }

        if (!isVertical()) {
            // Draw handle with a delay to allow base to gain maximum width
            Scheduler.get().scheduleDeferred(() -> {
                buildHandle();
                setValue(value, false);
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

        handle.getStyle().setProperty(handleAttribute, "0");

        // clear unnecessary opposite handle attribute
        handle.getStyle().clearProperty(oppositeHandleAttribute);
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
        } else if (targ.equals(base)
                && DOM.eventGetType(event) == Event.ONMOUSEUP
                && updateValueOnClick) {
            processBaseEvent(event);
            feedbackPopup.show();
        } else if (targ == smaller) {
            decreaseValue(true);
        } else if (targ == bigger) {
            increaseValue(true);
        } else if (isNavigationEvent(event)) {

            if (handleNavigation(event.getKeyCode(), event.getCtrlKey(),
                    event.getShiftKey())) {

                feedbackPopup.show();

                delayedValueUpdater.trigger();

                event.preventDefault();
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
        if (WidgetUtil.isTouchEvent(event)) {
            event.preventDefault(); // avoid simulated events
            event.stopPropagation();
        }
    }

    private boolean isNavigationEvent(Event event) {
        if (BrowserInfo.get().isGecko()
                && BrowserInfo.get().getGeckoVersion() < 65) {
            return DOM.eventGetType(event) == Event.ONKEYPRESS;
        } else {
            return DOM.eventGetType(event) == Event.ONKEYDOWN;
        }
    }

    private void processMouseWheelEvent(final Event event) {
        final int dir = event.getMouseWheelVelocityY();

        if (dir < 0) {
            increaseValue(false);
        } else {
            decreaseValue(false);
        }

        delayedValueUpdater.trigger();

        event.preventDefault();
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
                event.preventDefault(); // prevent selecting text
                DOM.eventCancelBubble(event, true);
                event.stopPropagation();
            }
            break;
        case Event.ONMOUSEMOVE:
        case Event.ONTOUCHMOVE:
            if (dragging) {
                setValueByEvent(event, false);
                updateFeedbackPosition();
                event.stopPropagation();
            }
            break;
        case Event.ONTOUCHEND:
            feedbackPopup.hide();
        case Event.ONMOUSEUP:
            // feedbackPopup.hide();
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
        if (!disabled && !readonly && !dragging) {
            setValueByEvent(event, true);
            DOM.eventCancelBubble(event, true);
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
            v = (baseSize - (coord - baseOffset))
                    / (double) (baseSize - handleSize) * (max - min) + min;
        } else {
            v = (coord - baseOffset) / (double) (baseSize - handleSize)
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
     *            the event whose position to check
     * @return the client position
     */
    protected int getEventPosition(Event event) {
        if (isVertical()) {
            return WidgetUtil.getTouchOrMouseClientY(event);
        } else {
            return WidgetUtil.getTouchOrMouseClientX(event);
        }
    }

    /**
     * Run internal layouting.
     */
    public void iLayout() {
        if (isVertical()) {
            setHeight();
        }
        // Update handle position
        setValue(value, false);
    }

    private void setHeight() {
        // Calculate decoration size
        base.getStyle().setHeight(0, Unit.PX);
        base.getStyle().setOverflow(Overflow.HIDDEN);
        int h = getElement().getOffsetHeight();
        if (h < MIN_SIZE) {
            h = MIN_SIZE;
        }
        base.getStyle().setHeight(h, Unit.PX);
        base.getStyle().clearOverflow();
    }

    private void fireValueChanged() {
        ValueChangeEvent.fire(VSlider.this, value);
    }

    /**
     * Handles the keyboard events handled by the Slider.
     *
     * @param keycode
     *            The key code received
     * @param ctrl
     *            Whether {@code CTRL} was pressed
     * @param shift
     *            Whether {@code SHIFT} was pressed
     * @return true if the navigation event was handled
     */
    public boolean handleNavigation(int keycode, boolean ctrl, boolean shift) {

        // No support for ctrl moving
        if (ctrl) {
            return false;
        }

        if (keycode == getNavigationUpKey() && isVertical()
                || keycode == getNavigationRightKey() && !isVertical()) {
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
                || keycode == getNavigationLeftKey() && !isVertical()) {
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

    /**
     * Sets the current client-side communication engine.
     *
     * @param client
     *            the application connection that manages this component
     * @deprecated the updated field is no longer used by the framework
     */
    @Deprecated
    public void setConnection(ApplicationConnection client) {
        this.client = client;
    }

    /**
     * Sets the id of this component's connector.
     *
     * @param id
     *            the connector id
     * @deprecated the updated field is no longer used by the framework
     */
    @Deprecated
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Disables or enables this slider. Users cannot interact with a disabled
     * widget, and the default styles show it as grayed out (via opacity). The
     * slider is enabled by default.
     *
     * @param disabled
     *            a boolean value specifying whether the slider should be
     *            disabled or not
     * @see #setReadOnly(boolean)
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Sets the read-only status of this slider. Users cannot interact with a
     * read-only widget, but the default styles don't show it grayed out unless
     * it's also disabled. The slider is not read-only by default.
     *
     * @param readonly
     *            a boolean value specifying whether the slider should be in
     *            read-only mode or not
     * @see #setDisabled(boolean)
     */
    public void setReadOnly(boolean readonly) {
        this.readonly = readonly;
    }

    private boolean isVertical() {
        return orientation == SliderOrientation.VERTICAL;
    }

    /**
     * Sets the slider orientation. Updates the style names if the given
     * orientation differs from previously set orientation.
     *
     * @param orientation
     *            the orientation to use
     */
    public void setOrientation(SliderOrientation orientation) {
        if (this.orientation != orientation) {
            this.orientation = orientation;
            updateStyleNames(getStylePrimaryName(), true);
        }
    }

    /**
     * Sets the minimum value for slider.
     *
     * @param value
     *            the minimum value to use
     */
    public void setMinValue(double value) {
        min = value;
    }

    /**
     * Sets the maximum value for slider.
     *
     * @param value
     *            the maximum value to use
     */
    public void setMaxValue(double value) {
        max = value;
    }

    /**
     * Sets the resolution (precision level) for slider as the number of
     * fractional digits that are considered significant. Determines how big
     * change is used when increasing or decreasing the value, and where more
     * precise values get rounded.
     *
     * @param resolution
     *            the number of digits after the decimal point
     */
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
        final int handleSize = handle.getPropertyInt(domProperty);
        final int baseSize = base.getPropertyInt(domProperty)
                - 2 * BASE_BORDER_WIDTH;

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

        handle.getStyle().setPropertyPx(styleAttribute, (int) Math.round(pos));

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

    @SuppressWarnings("deprecation")
    @Override
    public com.google.gwt.user.client.Element getSubPartElement(
            String subPart) {
        if (subPart.equals("popup")) {
            feedbackPopup.show();
            return feedbackPopup.getElement();
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getSubPartName(
            com.google.gwt.user.client.Element subElement) {
        if (feedbackPopup.getElement().isOrHasChild(subElement)) {
            return "popup";
        }
        return null;
    }

    /**
     * Specifies whether or not click event should update the Slider's value.
     *
     * @param updateValueOnClick
     *            {@code true} if a click should update slider's value,
     *            {@code false} otherwise
     */
    public void setUpdateValueOnClick(boolean updateValueOnClick) {
        this.updateValueOnClick = updateValueOnClick;
    }
}
