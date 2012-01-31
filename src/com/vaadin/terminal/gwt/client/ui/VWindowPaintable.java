package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler.BeforeShortcutActionListener;

public class VWindowPaintable extends VAbstractPaintableWidgetContainer
        implements BeforeShortcutActionListener {

    private static final String CLICK_EVENT_IDENTIFIER = VPanelPaintable.CLICK_EVENT_IDENTIFIER;

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this,
            CLICK_EVENT_IDENTIFIER) {

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidgetForPaintable().addDomHandler(handler, type);
        }
    };

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().id = uidl.getId();
        getWidgetForPaintable().client = client;

        // Workaround needed for Testing Tools (GWT generates window DOM
        // slightly different in different browsers).
        DOM.setElementProperty(getWidgetForPaintable().closeBox, "id",
                getWidgetForPaintable().id + "_window_close");

        if (uidl.hasAttribute("invisible")) {
            getWidgetForPaintable().hide();
            return;
        }

        if (!uidl.hasAttribute("cached")) {
            if (uidl.getBooleanAttribute("modal") != getWidgetForPaintable().vaadinModality) {
                getWidgetForPaintable().setVaadinModality(
                        !getWidgetForPaintable().vaadinModality);
            }
            if (!getWidgetForPaintable().isAttached()) {
                getWidgetForPaintable().setVisible(false); // hide until
                                                           // possible centering
                getWidgetForPaintable().show();
            }
            if (uidl.getBooleanAttribute("resizable") != getWidgetForPaintable().resizable) {
                getWidgetForPaintable().setResizable(
                        !getWidgetForPaintable().resizable);
            }
            getWidgetForPaintable().resizeLazy = uidl
                    .hasAttribute(VView.RESIZE_LAZY);

            getWidgetForPaintable().setDraggable(
                    !uidl.hasAttribute("fixedposition"));

            // Caption must be set before required header size is measured. If
            // the caption attribute is missing the caption should be cleared.
            getWidgetForPaintable().setCaption(
                    uidl.getStringAttribute("caption"),
                    uidl.getStringAttribute("icon"));
        }

        getWidgetForPaintable().visibilityChangesDisabled = true;
        if (client.updateComponent(this, uidl, false)) {
            return;
        }
        getWidgetForPaintable().visibilityChangesDisabled = false;

        clickEventHandler.handleEventHandlerRegistration(client);

        getWidgetForPaintable().immediate = uidl.hasAttribute("immediate");

        getWidgetForPaintable().setClosable(
                !uidl.getBooleanAttribute("readonly"));

        // Initialize the position form UIDL
        int positionx = uidl.getIntVariable("positionx");
        int positiony = uidl.getIntVariable("positiony");
        if (positionx >= 0 || positiony >= 0) {
            if (positionx < 0) {
                positionx = 0;
            }
            if (positiony < 0) {
                positiony = 0;
            }
            getWidgetForPaintable().setPopupPosition(positionx, positiony);
        }

        boolean showingUrl = false;
        int childIndex = 0;
        UIDL childUidl = uidl.getChildUIDL(childIndex++);
        while ("open".equals(childUidl.getTag())) {
            // TODO multiple opens with the same target will in practice just
            // open the last one - should we fix that somehow?
            final String parsedUri = client.translateVaadinUri(childUidl
                    .getStringAttribute("src"));
            if (!childUidl.hasAttribute("name")) {
                final Frame frame = new Frame();
                DOM.setStyleAttribute(frame.getElement(), "width", "100%");
                DOM.setStyleAttribute(frame.getElement(), "height", "100%");
                DOM.setStyleAttribute(frame.getElement(), "border", "0px");
                frame.setUrl(parsedUri);
                getWidgetForPaintable().contentPanel.setWidget(frame);
                showingUrl = true;
            } else {
                final String target = childUidl.getStringAttribute("name");
                Window.open(parsedUri, target, "");
            }
            childUidl = uidl.getChildUIDL(childIndex++);
        }

        final VPaintableWidget lo = client.getPaintable(childUidl);
        if (getWidgetForPaintable().layout != null) {
            if (getWidgetForPaintable().layout != lo) {
                // remove old
                client.unregisterPaintable(getWidgetForPaintable().layout);
                getWidgetForPaintable().contentPanel
                        .remove(getWidgetForPaintable().layout
                                .getWidgetForPaintable());
                // add new
                if (!showingUrl) {
                    getWidgetForPaintable().contentPanel.setWidget(lo
                            .getWidgetForPaintable());
                }
                getWidgetForPaintable().layout = lo;
            }
        } else if (!showingUrl) {
            getWidgetForPaintable().contentPanel.setWidget(lo
                    .getWidgetForPaintable());
            getWidgetForPaintable().layout = lo;
        }

        getWidgetForPaintable().dynamicWidth = !uidl.hasAttribute("width");
        getWidgetForPaintable().dynamicHeight = !uidl.hasAttribute("height");

        getWidgetForPaintable().layoutRelativeWidth = uidl
                .hasAttribute("layoutRelativeWidth");
        getWidgetForPaintable().layoutRelativeHeight = uidl
                .hasAttribute("layoutRelativeHeight");

        if (getWidgetForPaintable().dynamicWidth
                && getWidgetForPaintable().layoutRelativeWidth) {
            /*
             * Relative layout width, fix window width before rendering (width
             * according to caption)
             */
            getWidgetForPaintable().setNaturalWidth();
        }

        getWidgetForPaintable().layout.updateFromUIDL(childUidl, client);
        if (!getWidgetForPaintable().dynamicHeight
                && getWidgetForPaintable().layoutRelativeWidth) {
            /*
             * Relative layout width, and fixed height. Must update the size to
             * be able to take scrollbars into account (layout gets narrower
             * space if it is higher than the window) -> only vertical scrollbar
             */
            client.runDescendentsLayout(getWidgetForPaintable());
        }

        /*
         * No explicit width is set and the layout does not have relative width
         * so fix the size according to the layout.
         */
        if (getWidgetForPaintable().dynamicWidth
                && !getWidgetForPaintable().layoutRelativeWidth) {
            getWidgetForPaintable().setNaturalWidth();
        }

        if (getWidgetForPaintable().dynamicHeight
                && getWidgetForPaintable().layoutRelativeHeight) {
            // Prevent resizing until height has been fixed
            getWidgetForPaintable().resizable = false;
        }

        // we may have actions and notifications
        if (uidl.getChildCount() > 1) {
            final int cnt = uidl.getChildCount();
            for (int i = 1; i < cnt; i++) {
                childUidl = uidl.getChildUIDL(i);
                if (childUidl.getTag().equals("actions")) {
                    if (getWidgetForPaintable().shortcutHandler == null) {
                        getWidgetForPaintable().shortcutHandler = new ShortcutActionHandler(
                                getId(), client);
                    }
                    getWidgetForPaintable().shortcutHandler
                            .updateActionMap(childUidl);
                }
            }

        }

        // setting scrollposition must happen after children is rendered
        getWidgetForPaintable().contentPanel.setScrollPosition(uidl
                .getIntVariable("scrollTop"));
        getWidgetForPaintable().contentPanel.setHorizontalScrollPosition(uidl
                .getIntVariable("scrollLeft"));

        // Center this window on screen if requested
        // This has to be here because we might not know the content size before
        // everything is painted into the window
        if (uidl.getBooleanAttribute("center")) {
            // mark as centered - this is unset on move/resize
            getWidgetForPaintable().centered = true;
            getWidgetForPaintable().center();
        } else {
            // don't try to center the window anymore
            getWidgetForPaintable().centered = false;
        }
        getWidgetForPaintable().updateShadowSizeAndPosition();
        getWidgetForPaintable().setVisible(true);

        boolean sizeReduced = false;
        // ensure window is not larger than browser window
        if (getWidgetForPaintable().getOffsetWidth() > Window.getClientWidth()) {
            getWidgetForPaintable().setWidth(Window.getClientWidth() + "px");
            sizeReduced = true;
        }
        if (getWidgetForPaintable().getOffsetHeight() > Window
                .getClientHeight()) {
            getWidgetForPaintable().setHeight(Window.getClientHeight() + "px");
            sizeReduced = true;
        }

        if (getWidgetForPaintable().dynamicHeight
                && getWidgetForPaintable().layoutRelativeHeight) {
            /*
             * Window height is undefined, layout is 100% high so the layout
             * should define the initial window height but on resize the layout
             * should be as high as the window. We fix the height to deal with
             * this.
             */

            int h = getWidgetForPaintable().contents.getOffsetHeight()
                    + getWidgetForPaintable().getExtraHeight();
            int w = getWidgetForPaintable().getElement().getOffsetWidth();

            client.updateVariable(getId(), "height", h, false);
            client.updateVariable(getId(), "width", w, true);
        }

        if (sizeReduced) {
            // If we changed the size we need to update the size of the child
            // component if it is relative (#3407)
            client.runDescendentsLayout(getWidgetForPaintable());
        }

        Util.runWebkitOverflowAutoFix(getWidgetForPaintable().contentPanel
                .getElement());

        client.getView().getWidgetForPaintable().scrollIntoView(uidl);

        if (uidl.hasAttribute("bringToFront")) {
            /*
             * Focus as a side-efect. Will be overridden by
             * ApplicationConnection if another component was focused by the
             * server side.
             */
            getWidgetForPaintable().contentPanel.focus();
            getWidgetForPaintable().bringToFrontSequence = uidl
                    .getIntAttribute("bringToFront");
            VWindow.deferOrdering();
        }
    }

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        // NOP, window has own caption, layout captio not rendered
    }

    public void onBeforeShortcutAction(Event e) {
        // NOP, nothing to update just avoid workaround ( causes excess
        // blur/focus )
    }

    @Override
    public VWindow getWidgetForPaintable() {
        return (VWindow) super.getWidgetForPaintable();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VWindow.class);
    }

}
