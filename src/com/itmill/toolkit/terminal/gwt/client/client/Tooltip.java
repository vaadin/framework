/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.itmill.toolkit.terminal.gwt.client.ui.ToolkitOverlay;

/**
 * TODO open for extension
 */
public class Tooltip extends ToolkitOverlay {
    private static final String CLASSNAME = "i-tooltip";
    private static final int MARGIN = 4;
    public static final int TOOLTIP_EVENTS = Event.ONKEYDOWN
            | Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEMOVE
            | Event.ONCLICK;
    protected static final int MAX_WIDTH = 500;
    ErrorMessage em = new ErrorMessage();
    Element description = DOM.createDiv();
    private Paintable tooltipOwner;
    private boolean closing = false;
    private boolean opening = false;
    private ApplicationConnection ac;

    public Tooltip(ApplicationConnection client) {
        super(false, false, true);
        ac = client;
        setStyleName(CLASSNAME);
        FlowPanel layout = new FlowPanel();
        setWidget(layout);
        layout.add(em);
        DOM.setElementProperty(description, "className", CLASSNAME + "-text");
        DOM.appendChild(layout.getElement(), description);
    }

    private void show(TooltipInfo info) {
        boolean hasContent = false;
        if (info.getErrorUidl() != null) {
            em.setVisible(true);
            em.updateFromUIDL(info.getErrorUidl());
            hasContent = true;
        } else {
            em.setVisible(false);
        }
        if (info.getTitle() != null && !"".equals(info.getTitle())) {
            DOM.setInnerHTML(description, info.getTitle());
            DOM.setStyleAttribute(description, "display", "");
            hasContent = true;
        } else {
            DOM.setInnerHTML(description, "");
            DOM.setStyleAttribute(description, "display", "none");
        }
        if (hasContent) {
            setPopupPositionAndShow(new PositionCallback() {
                public void setPosition(int offsetWidth, int offsetHeight) {

                    if (offsetWidth > MAX_WIDTH) {
                        setWidth(MAX_WIDTH + "px");
                    }

                    offsetWidth = getOffsetWidth();

                    int x = tooltipEventMouseX + 10 + Window.getScrollLeft();
                    int y = tooltipEventMouseY + 10 + Window.getScrollTop();

                    if (x + offsetWidth + MARGIN - Window.getScrollLeft() > Window
                            .getClientWidth()) {
                        x = Window.getClientWidth() - offsetWidth - MARGIN;
                    }

                    if (y + offsetHeight + MARGIN - Window.getScrollTop() > Window
                            .getClientHeight()) {
                        y = tooltipEventMouseY - 5 - offsetHeight;
                    }

                    setPopupPosition(x, y);
                    sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
                }
            });
        } else {
            hide();
        }
    }

    public void showTooltip(Paintable owner, Event event) {
        if (closing && tooltipOwner == owner) {
            closeTimer.cancel();
            closing = false;
            return;
        }
        updatePosition(event);

        if (opening) {
            showTimer.cancel();
        }
        tooltipOwner = owner;
        showTimer.schedule(1000);
        opening = true;

    }

    private Timer showTimer = new Timer() {
        public void run() {
            TooltipInfo info = ac.getTitleInfo(tooltipOwner);
            show(info);
            opening = false;

        }
    };

    private Timer closeTimer = new Timer() {
        public void run() {
            hide();
            closing = false;
            tooltipOwner = null;
            setWidth("");
        }
    };

    public void hideTooltip() {
        if (opening) {
            showTimer.cancel();
            opening = false;
            tooltipOwner = null;
        }
        if (!isAttached()) {
            return;
        }
        if (closing) {
            // already about to close
            return;
        }
        closeTimer.schedule(300);
        closing = true;
    }

    private int tooltipEventMouseX;
    private int tooltipEventMouseY;

    public void updatePosition(Event event) {
        tooltipEventMouseX = DOM.eventGetClientX(event);
        tooltipEventMouseY = DOM.eventGetClientY(event);

    }

    public void handleTooltipEvent(Event event, Paintable owner) {
        final int type = DOM.eventGetType(event);
        if ((Tooltip.TOOLTIP_EVENTS & type) == type) {
            if (type == Event.ONMOUSEOVER) {
                showTooltip(owner, event);
            } else if (type == Event.ONMOUSEMOVE) {
                updatePosition(event);
            } else {
                hideTooltip();
            }
        } else {
            // non-tooltip event, hide tooltip
            hideTooltip();
        }
    }

    public void onBrowserEvent(Event event) {
        final int type = DOM.eventGetType(event);
        // cancel closing event if tooltip is mouseovered; the user might want
        // to scroll of cut&paste

        switch (type) {
        case Event.ONMOUSEOVER:
            closeTimer.cancel();
            closing = false;
            break;
        case Event.ONMOUSEOUT:
            hideTooltip();
            break;
        default:
            // NOP
        }
    }

}
