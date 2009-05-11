/* 
@ITMillApache2LicenseForJavaFiles@
 */
// 
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.ContainerResizedListener;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class ISlider extends Widget implements Paintable, Field,
        ContainerResizedListener {

    public static final String CLASSNAME = "i-slider";

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

    private int handleSize;
    private double min;
    private double max;
    private int resolution;
    private Double value;
    private boolean vertical;
    private int size = -1;
    private boolean arrows;

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

    public ISlider() {
        super();

        setElement(DOM.createDiv());
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

        DOM.sinkEvents(getElement(), Event.MOUSEEVENTS | Event.ONMOUSEWHEEL);
        DOM.sinkEvents(base, Event.ONCLICK);
        DOM.sinkEvents(handle, Event.MOUSEEVENTS);
        DOM.sinkEvents(smaller, Event.ONMOUSEDOWN | Event.ONMOUSEUP
                | Event.ONMOUSEOUT);
        DOM.sinkEvents(bigger, Event.ONMOUSEDOWN | Event.ONMOUSEUP
                | Event.ONMOUSEOUT);
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

        handleSize = uidl.getIntAttribute("hsize");

        buildBase();

        if (!vertical) {
            // Draw handle with a delay to allow base to gain maximum width
            DeferredCommand.addCommand(new Command() {
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

    private void buildBase() {
        final String styleAttribute = vertical ? "height" : "width";
        final String domProperty = vertical ? "offsetHeight" : "offsetWidth";

        if (size == -1) {
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
                DeferredCommand.addCommand(new Command() {
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
        } else {
            DOM.setStyleAttribute(base, styleAttribute, size + "px");
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

        if (value.doubleValue() < min) {
            value = new Double(min);
        } else if (value.doubleValue() > max) {
            value = new Double(max);
        }

        // Update handle position
        final String styleAttribute = vertical ? "marginTop" : "marginLeft";
        final String domProperty = vertical ? "offsetHeight" : "offsetWidth";
        final int handleSize = Integer.parseInt(DOM.getElementProperty(handle,
                domProperty));
        final int baseSize = Integer.parseInt(DOM.getElementProperty(base,
                domProperty))
                - (2 * BASE_BORDER_WIDTH);

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
            // IE6 rounding behaves a little unstable, reduce one pixel so the
            // containing element (base) won't expand without limits
            p = range - p - (BrowserInfo.get().isIE6() ? 1 : 0);
        }
        final double pos = p;

        DOM.setStyleAttribute(handle, styleAttribute, (Math.round(pos)) + "px");

        // TODO give more detailed info when dragging and do roundup
        DOM.setElementAttribute(handle, "title", "" + v);

        // Update value
        this.value = new Double(v);

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
        } else {
            processBaseEvent(event);
        }
    }

    private Timer scrollTimer;

    private void processMouseWheelEvent(final Event event) {
        final int dir = DOM.eventGetMouseWheelVelocityY(event);

        if (dir < 0) {
            increaseValue(false);
        } else {
            decreaseValue(false);
        }

        if (scrollTimer != null) {
            scrollTimer.cancel();
        }
        scrollTimer = new Timer() {
            @Override
            public void run() {
                updateValueToServer();
            }
        };
        scrollTimer.schedule(100);

        DOM.eventPreventDefault(event);
        DOM.eventCancelBubble(event, true);
    }

    private void processHandleEvent(Event event) {
        switch (DOM.eventGetType(event)) {
        case Event.ONMOUSEDOWN:
            if (!disabled && !readonly) {
                dragging = true;
                DOM.setCapture(getElement());
                DOM.eventPreventDefault(event); // prevent selecting text
                DOM.eventCancelBubble(event, true);
            }
            break;
        case Event.ONMOUSEMOVE:
            if (dragging) {
                // DOM.setCapture(getElement());
                setValueByEvent(event, false);
            }
            break;
        case Event.ONMOUSEUP:
            dragging = false;
            DOM.releaseCapture(getElement());
            setValueByEvent(event, true);
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

        final int coord = vertical ? DOM.eventGetClientY(event) : DOM
                .eventGetClientX(event);
        final String domProperty = vertical ? "offsetHeight" : "offsetWidth";

        final double handleSize = Integer.parseInt(DOM.getElementProperty(
                handle, domProperty));
        final double baseSize = Integer.parseInt(DOM.getElementProperty(base,
                domProperty));
        final double baseOffset = vertical ? DOM.getAbsoluteTop(base)
                - handleSize / 2 : DOM.getAbsoluteLeft(base) + handleSize / 2;

        if (vertical) {
            v = ((baseSize - (coord - baseOffset)) / (baseSize - handleSize))
                    * (max - min) + min;
        } else {
            v = ((coord - baseOffset) / (baseSize - handleSize)) * (max - min)
                    + min;
        }

        if (v < min) {
            v = min;
        } else if (v > max) {
            v = max;
        }

        setValue(new Double(v), updateToServer);
    }

    public void iLayout() {
        if (vertical) {
            setHeight();
        }
        // Update handle position
        setValue(value, false);
    }

    private void setHeight() {
        if (size == -1) {
            // Calculate decoration size
            DOM.setStyleAttribute(base, "height", "0");
            DOM.setStyleAttribute(base, "overflow", "hidden");
            int h = DOM.getElementPropertyInt(getElement(), "offsetHeight");
            if (h < MIN_SIZE) {
                h = MIN_SIZE;
            }
            DOM.setStyleAttribute(base, "height", h + "px");
        } else {
            DOM.setStyleAttribute(base, "height", size + "px");
        }
        DOM.setStyleAttribute(base, "overflow", "");
    }

    private void updateValueToServer() {
        client.updateVariable(id, "value", value.doubleValue(), immediate);
    }

}
