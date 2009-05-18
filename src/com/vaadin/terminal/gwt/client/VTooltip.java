/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.vaadin.terminal.gwt.client.ui.VOverlay;

/**
 * TODO open for extension
 */
public class VTooltip extends VOverlay {
    private static final String CLASSNAME = "v-tooltip";
    private static final int MARGIN = 4;
    public static final int TOOLTIP_EVENTS = Event.ONKEYDOWN
            | Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEMOVE
            | Event.ONCLICK;
    protected static final int MAX_WIDTH = 500;
    private static final int QUICK_OPEN_TIMEOUT = 1000;
    private static final int CLOSE_TIMEOUT = 300;
    private static final int OPEN_DELAY = 750;
    private static final int QUICK_OPEN_DELAY = 100;
    VErrorMessage em = new VErrorMessage();
    Element description = DOM.createDiv();
    private Paintable tooltipOwner;
    private boolean closing = false;
    private boolean opening = false;
    private ApplicationConnection ac;
    // Open next tooltip faster. Disabled after 2 sec of showTooltip-silence.
    private boolean justClosed = false;

    public VTooltip(ApplicationConnection client) {
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
            // return to same tooltip, cancel closing
            closeTimer.cancel();
            closing = false;
            justClosedTimer.cancel();
            justClosed = false;
            return;
        }

        if (closing) {
            closeNow();
        }

        updatePosition(event);

        if (opening) {
            showTimer.cancel();
        }
        tooltipOwner = owner;
        if (justClosed) {
            showTimer.schedule(QUICK_OPEN_DELAY);
        } else {
            showTimer.schedule(OPEN_DELAY);
        }
        opening = true;
    }

    private void closeNow() {
        if (closing) {
            hide();
            tooltipOwner = null;
            setWidth("");
            closing = false;
        }
    }

    private Timer showTimer = new Timer() {
        @Override
        public void run() {
            TooltipInfo info = ac.getTitleInfo(tooltipOwner);
            if (null != info) {
                show(info);
            }
            opening = false;
        }
    };

    private Timer closeTimer = new Timer() {
        @Override
        public void run() {
            closeNow();
            justClosedTimer.schedule(2000);
            justClosed = true;
        }
    };

    private Timer justClosedTimer = new Timer() {
        @Override
        public void run() {
            justClosed = false;
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
        closeTimer.schedule(CLOSE_TIMEOUT);
        closing = true;
        justClosed = true;
        justClosedTimer.schedule(QUICK_OPEN_TIMEOUT);

    }

    private int tooltipEventMouseX;
    private int tooltipEventMouseY;

    public void updatePosition(Event event) {
        tooltipEventMouseX = DOM.eventGetClientX(event);
        tooltipEventMouseY = DOM.eventGetClientY(event);

    }

    public void handleTooltipEvent(Event event, Paintable owner) {
        final int type = DOM.eventGetType(event);
        if ((VTooltip.TOOLTIP_EVENTS & type) == type) {
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

    @Override
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
