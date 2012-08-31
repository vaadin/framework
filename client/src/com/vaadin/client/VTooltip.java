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
package com.vaadin.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.VOverlay;

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
        setSinkShadowEvents(true);
    }

    /**
     * Show a popup containing the information in the "info" tooltip
     * 
     * @param info
     */
    private void show(TooltipInfo info) {
        boolean hasContent = false;
        if (info.getErrorMessage() != null) {
            em.setVisible(true);
            em.updateMessage(info.getErrorMessage());
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
            // Issue #8454: With IE7 the tooltips size is calculated based on
            // the last tooltip's position, causing problems if the last one was
            // in the right or bottom edge. For this reason the tooltip is moved
            // first to 0,0 position so that the calculation goes correctly.
            setPopupPosition(0, 0);
            setPopupPositionAndShow(new PositionCallback() {
                @Override
                public void setPosition(int offsetWidth, int offsetHeight) {

                    if (offsetWidth > MAX_WIDTH) {
                        setWidth(MAX_WIDTH + "px");

                        // Check new height and width with reflowed content
                        offsetWidth = getOffsetWidth();
                        offsetHeight = getOffsetHeight();
                    }

                    int x = tooltipEventMouseX + 10 + Window.getScrollLeft();
                    int y = tooltipEventMouseY + 10 + Window.getScrollTop();

                    if (x + offsetWidth + MARGIN - Window.getScrollLeft() > Window
                            .getClientWidth()) {
                        x = Window.getClientWidth() - offsetWidth - MARGIN;
                    }

                    if (y + offsetHeight + MARGIN - Window.getScrollTop() > Window
                            .getClientHeight()) {
                        y = tooltipEventMouseY - 5 - offsetHeight;
                        if (y - Window.getScrollTop() < 0) {
                            // tooltip does not fit on top of the mouse either,
                            // put it at the top of the screen
                            y = Window.getScrollTop();
                        }
                    }

                    setPopupPosition(x, y);
                    sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
                }
            });
        } else {
            hide();
        }
    }

    private void showTooltip() {

        // Close current tooltip
        if (isShowing()) {
            closeNow();
        }

        // Schedule timer for showing the tooltip according to if it was
        // recently closed or not.
        int timeout = justClosed ? QUICK_OPEN_DELAY : OPEN_DELAY;
        showTimer.schedule(timeout);
        opening = true;
    }

    private void closeNow() {
        hide();
        setWidth("");
        closing = false;
    }

    private Timer showTimer = new Timer() {
        @Override
        public void run() {
            TooltipInfo info = tooltipEventHandler.getTooltipInfo();
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

    @Override
    public void onBrowserEvent(Event event) {
        final int type = DOM.eventGetType(event);
        // cancel closing event if tooltip is mouseovered; the user might want
        // to scroll of cut&paste

        if (type == Event.ONMOUSEOVER) {
            // Cancel closing so tooltip stays open and user can copy paste the
            // tooltip
            closeTimer.cancel();
            closing = false;
        }
    }

    /**
     * Replace current open tooltip with new content
     */
    public void replaceCurrentTooltip() {
        if (closing) {
            closeTimer.cancel();
            closeNow();
        }

        TooltipInfo info = tooltipEventHandler.getTooltipInfo();
        if (null != info) {
            show(info);
        }
        opening = false;
    }

    private class TooltipEventHandler implements MouseMoveHandler,
            ClickHandler, KeyDownHandler {

        /**
         * Current element hovered
         */
        private com.google.gwt.dom.client.Element currentElement = null;

        /**
         * Current tooltip active
         */
        private TooltipInfo currentTooltipInfo = null;

        /**
         * Get current active tooltip information
         * 
         * @return Current active tooltip information or null
         */
        public TooltipInfo getTooltipInfo() {
            return currentTooltipInfo;
        }

        /**
         * Locate connector and it's tooltip for given element
         * 
         * @param element
         *            Element used in search
         * @return true if connector and tooltip found
         */
        private boolean resolveConnector(Element element) {

            ComponentConnector connector = Util.getConnectorForElement(ac,
                    RootPanel.get(), element);

            // Try to find first connector with proper tooltip info
            TooltipInfo info = null;
            while (connector != null) {

                info = connector.getTooltipInfo(element);

                if (info != null && info.hasMessage()) {
                    break;
                }

                if (!(connector.getParent() instanceof ComponentConnector)) {
                    connector = null;
                    info = null;
                    break;
                }
                connector = (ComponentConnector) connector.getParent();
            }

            if (connector != null && info != null) {
                currentTooltipInfo = info;
                return true;
            }

            return false;
        }

        /**
         * Handle hide event
         * 
         * @param event
         *            Event causing hide
         */
        private void handleHideEvent() {
            hideTooltip();
            currentTooltipInfo = null;
        }

        @Override
        public void onMouseMove(MouseMoveEvent mme) {
            Event event = Event.as(mme.getNativeEvent());
            com.google.gwt.dom.client.Element element = Element.as(event
                    .getEventTarget());

            // We can ignore move event if it's handled by move or over already
            if (currentElement == element) {
                return;
            }
            currentElement = element;

            boolean connectorAndTooltipFound = resolveConnector((com.google.gwt.user.client.Element) element);
            if (!connectorAndTooltipFound) {
                if (isShowing()) {
                    handleHideEvent();
                } else {
                    currentTooltipInfo = null;
                }
            } else {
                updatePosition(event);
                if (isShowing()) {
                    replaceCurrentTooltip();
                } else {
                    showTooltip();
                }
            }
        }

        @Override
        public void onClick(ClickEvent event) {
            handleHideEvent();
        }

        @Override
        public void onKeyDown(KeyDownEvent event) {
            handleHideEvent();
        }
    }

    private final TooltipEventHandler tooltipEventHandler = new TooltipEventHandler();

    /**
     * Connects DOM handlers to widget that are needed for tooltip presentation.
     * 
     * @param widget
     *            Widget which DOM handlers are connected
     */
    public void connectHandlersToWidget(Widget widget) {
        widget.addDomHandler(tooltipEventHandler, MouseMoveEvent.getType());
        widget.addDomHandler(tooltipEventHandler, ClickEvent.getType());
        widget.addDomHandler(tooltipEventHandler, KeyDownEvent.getType());
    }
}
