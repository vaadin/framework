/* 
@ITMillApache2LicenseForJavaFiles@
 */
// 
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.ContainerResizedListener;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;

public class VSlider extends SimpleFocusablePanel implements Paintable, Field,
        ContainerResizedListener {

    public static final String CLASSNAME = "v-slider";

    /**
     * Minimum size (width or height, depending on orientation) of the slider
     * base.
     */
    private static final int MIN_SIZE = 50;

    ApplicationConnection client;

    String id;

    private boolean immediate;
    private boolean disabled;
    private boolean readonly;
    private boolean scrollbarStyle;

    private int acceleration = 1;
    private int handleSize;
    private double min;
    private double max;
    private int resolution;
    private Double value;
    private boolean vertical;
    private boolean arrows;

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

                public void execute() {
                    updateValueToServer();
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
        DOM.setElementProperty(base, "className", CLASSNAME + "-base");
        DOM.setElementProperty(handle, "className", CLASSNAME + "-handle");
        DOM.setElementProperty(smaller, "className", CLASSNAME + "-smaller");
        DOM.setElementProperty(bigger, "className", CLASSNAME + "-bigger");

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

        feedbackPopup.addStyleName(CLASSNAME + "-feedback");
        feedbackPopup.setWidget(feedback);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        this.client = client;
        id = uidl.getId();

        // Ensure correct implementation
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        immediate = uidl.getBooleanAttribute("immediate");
        disabled = uidl.getBooleanAttribute("disabled");
        readonly = uidl.getBooleanAttribute("readonly");

        vertical = uidl.hasAttribute("vertical");
        arrows = uidl.hasAttribute("arrows");

        String style = "";
        if (uidl.hasAttribute("style")) {
            style = uidl.getStringAttribute("style");
        }

        scrollbarStyle = style.indexOf("scrollbar") > -1;

        if (arrows) {
            DOM.setStyleAttribute(smaller, "display", "block");
            DOM.setStyleAttribute(bigger, "display", "block");
        }

        if (vertical) {
            addStyleName(CLASSNAME + "-vertical");
        } else {
            removeStyleName(CLASSNAME + "-vertical");
        }

        min = uidl.getDoubleAttribute("min");
        max = uidl.getDoubleAttribute("max");
        resolution = uidl.getIntAttribute("resolution");
        value = new Double(uidl.getDoubleVariable("value"));

        setFeedbackValue(value);

        handleSize = uidl.getIntAttribute("hsize");

        buildBase();

        if (!vertical) {
            // Draw handle with a delay to allow base to gain maximum width
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    buildHandle();
                    setValue(value, false);
                }
            });
        } else {
            buildHandle();
            setValue(value, false);
        }
    }

    private void setFeedbackValue(double value) {
        String currentValue = "" + value;
        if (resolution == 0) {
            currentValue = "" + new Double(value).intValue();
        }
        feedback.setText(currentValue);
    }

    private void updateFeedbackPosition() {
        if (vertical) {
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

    private void buildBase() {
        final String styleAttribute = vertical ? "height" : "width";
        final String domProperty = vertical ? "offsetHeight" : "offsetWidth";

        final Element p = DOM.getParent(getElement());
        if (DOM.getElementPropertyInt(p, domProperty) > 50) {
            if (vertical) {
                setHeight();
            } else {
                DOM.setStyleAttribute(base, styleAttribute, "");
            }
        } else {
            // Set minimum size and adjust after all components have
            // (supposedly) been drawn completely.
            DOM.setStyleAttribute(base, styleAttribute, MIN_SIZE + "px");
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    final Element p = DOM.getParent(getElement());
                    if (DOM.getElementPropertyInt(p, domProperty) > (MIN_SIZE + 5)) {
                        if (vertical) {
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

        // TODO attach listeners for focusing and arrow keys
    }

    private void buildHandle() {
        final String styleAttribute = vertical ? "height" : "width";
        final String handleAttribute = vertical ? "marginTop" : "marginLeft";
        final String domProperty = vertical ? "offsetHeight" : "offsetWidth";

        DOM.setStyleAttribute(handle, handleAttribute, "0");

        if (scrollbarStyle) {
            // Only stretch the handle if scrollbar style is set.
            int s = (int) (Double.parseDouble(DOM.getElementProperty(base,
                    domProperty)) / 100 * handleSize);
            if (handleSize == -1) {
                final int baseS = Integer.parseInt(DOM.getElementProperty(base,
                        domProperty));
                final double range = (max - min) * (resolution + 1) * 3;
                s = (int) (baseS - range);
            }
            if (s < 3) {
                s = 3;
            }
            DOM.setStyleAttribute(handle, styleAttribute, s + "px");
        } else {
            DOM.setStyleAttribute(handle, styleAttribute, "");
        }

        // Restore visibility
        DOM.setStyleAttribute(handle, "visibility", "visible");

    }

    private void setValue(Double value, boolean updateToServer) {
        if (value == null) {
            return;
        }

        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }

        // Update handle position
        final String styleAttribute = vertical ? "marginTop" : "marginLeft";
        final String domProperty = vertical ? "offsetHeight" : "offsetWidth";
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
        if (vertical) {
            p = range - p;
        }
        final double pos = p;

        DOM.setStyleAttribute(handle, styleAttribute, (Math.round(pos)) + "px");

        // Update value
        this.value = new Double(v);
        setFeedbackValue(v);

        if (updateToServer) {
            updateValueToServer();
        }
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
                DOM.setElementProperty(handle, "className", CLASSNAME
                        + "-handle " + CLASSNAME + "-handle-active");
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
            DOM.setElementProperty(handle, "className", CLASSNAME + "-handle");
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
        } else if (DOM.eventGetType(event) == Event.ONMOUSEDOWN && dragging) {
            dragging = false;
            DOM.releaseCapture(getElement());
            setValueByEvent(event, true);
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
        if (vertical) {
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

        if (vertical) {
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
        if (vertical) {
            return Util.getTouchOrMouseClientY(event);
        } else {
            return Util.getTouchOrMouseClientX(event);
        }
    }

    public void iLayout() {
        if (vertical) {
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

    private void updateValueToServer() {
        client.updateVariable(id, "value", value.doubleValue(), immediate);
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

        if ((keycode == getNavigationUpKey() && vertical)
                || (keycode == getNavigationRightKey() && !vertical)) {
            if (shift) {
                for (int a = 0; a < acceleration; a++) {
                    increaseValue(false);
                }
                acceleration++;
            } else {
                increaseValue(false);
            }
            return true;
        } else if (keycode == getNavigationDownKey() && vertical
                || (keycode == getNavigationLeftKey() && !vertical)) {
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
}
