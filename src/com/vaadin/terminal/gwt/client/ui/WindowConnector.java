/*
@VaadinApache2LicenseForJavaFiles@
 */
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
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler.BeforeShortcutActionListener;

public class WindowConnector extends AbstractComponentContainerConnector
        implements BeforeShortcutActionListener, SimpleManagedLayout,
        PostLayoutListener {

    private static final String CLICK_EVENT_IDENTIFIER = PanelConnector.CLICK_EVENT_IDENTIFIER;

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this,
            CLICK_EVENT_IDENTIFIER) {

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidget().addDomHandler(handler, type);
        }
    };

    @Override
    protected boolean delegateCaptionHandling() {
        return false;
    };

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().id = uidl.getId();
        getWidget().client = client;

        // Workaround needed for Testing Tools (GWT generates window DOM
        // slightly different in different browsers).
        DOM.setElementProperty(getWidget().closeBox, "id", getWidget().id
                + "_window_close");

        if (uidl.hasAttribute("invisible")) {
            getWidget().hide();
            return;
        }

        if (isRealUpdate(uidl)) {
            if (uidl.getBooleanAttribute("modal") != getWidget().vaadinModality) {
                getWidget().setVaadinModality(!getWidget().vaadinModality);
            }
            if (!getWidget().isAttached()) {
                getWidget().setVisible(false); // hide until
                                               // possible centering
                getWidget().show();
            }
            if (uidl.getBooleanAttribute("resizable") != getWidget().resizable) {
                getWidget().setResizable(!getWidget().resizable);
            }
            getWidget().resizeLazy = uidl.hasAttribute(VView.RESIZE_LAZY);

            getWidget().setDraggable(!uidl.hasAttribute("fixedposition"));

            // Caption must be set before required header size is measured. If
            // the caption attribute is missing the caption should be cleared.
            getWidget()
                    .setCaption(
                            getState().getCaption(),
                            uidl.getStringAttribute(AbstractComponentConnector.ATTRIBUTE_ICON));
        }

        getWidget().visibilityChangesDisabled = true;
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }
        getWidget().visibilityChangesDisabled = false;

        clickEventHandler.handleEventHandlerRegistration(client);

        getWidget().immediate = getState().isImmediate();

        getWidget().setClosable(!getState().isReadOnly());

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
            getWidget().setPopupPosition(positionx, positiony);
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
                getWidget().contentPanel.setWidget(frame);
                showingUrl = true;
            } else {
                final String target = childUidl.getStringAttribute("name");
                Window.open(parsedUri, target, "");
            }
            childUidl = uidl.getChildUIDL(childIndex++);
        }

        final ComponentConnector lo = client.getPaintable(childUidl);
        if (getWidget().layout != null) {
            if (getWidget().layout != lo) {
                // remove old
                client.unregisterPaintable(getWidget().layout);
                getWidget().contentPanel.remove(getWidget().layout.getWidget());
                // add new
                if (!showingUrl) {
                    getWidget().contentPanel.setWidget(lo.getWidget());
                }
                getWidget().layout = lo;
            }
        } else if (!showingUrl) {
            getWidget().contentPanel.setWidget(lo.getWidget());
            getWidget().layout = lo;
        }

        getWidget().dynamicWidth = getState().isUndefinedWidth();
        getWidget().dynamicHeight = getState().isUndefinedHeight();

        getWidget().layoutRelativeWidth = uidl
                .hasAttribute("layoutRelativeWidth");
        getWidget().layoutRelativeHeight = uidl
                .hasAttribute("layoutRelativeHeight");

        if (getWidget().dynamicWidth && getWidget().layoutRelativeWidth) {
            /*
             * Relative layout width, fix window width before rendering (width
             * according to caption)
             */
            getWidget().setNaturalWidth();
        }

        getWidget().layout.updateFromUIDL(childUidl, client);
        if (!getWidget().dynamicHeight && getWidget().layoutRelativeWidth) {
            /*
             * Relative layout width, and fixed height. Must update the size to
             * be able to take scrollbars into account (layout gets narrower
             * space if it is higher than the window) -> only vertical scrollbar
             */
            client.runDescendentsLayout(getWidget());
        }

        /*
         * No explicit width is set and the layout does not have relative width
         * so fix the size according to the layout.
         */
        if (getWidget().dynamicWidth && !getWidget().layoutRelativeWidth) {
            getWidget().setNaturalWidth();
        }

        if (getWidget().dynamicHeight && getWidget().layoutRelativeHeight) {
            // Prevent resizing until height has been fixed
            getWidget().resizable = false;
        }

        // we may have actions and notifications
        if (uidl.getChildCount() > 1) {
            final int cnt = uidl.getChildCount();
            for (int i = 1; i < cnt; i++) {
                childUidl = uidl.getChildUIDL(i);
                if (childUidl.getTag().equals("actions")) {
                    if (getWidget().shortcutHandler == null) {
                        getWidget().shortcutHandler = new ShortcutActionHandler(
                                getId(), client);
                    }
                    getWidget().shortcutHandler.updateActionMap(childUidl);
                }
            }

        }

        // setting scrollposition must happen after children is rendered
        getWidget().contentPanel.setScrollPosition(uidl
                .getIntVariable("scrollTop"));
        getWidget().contentPanel.setHorizontalScrollPosition(uidl
                .getIntVariable("scrollLeft"));

        // Center this window on screen if requested
        // This has to be here because we might not know the content size before
        // everything is painted into the window
        if (uidl.getBooleanAttribute("center")) {
            // mark as centered - this is unset on move/resize
            getWidget().centered = true;
            getWidget().center();
        } else {
            // don't try to center the window anymore
            getWidget().centered = false;
        }
        getWidget().updateShadowSizeAndPosition();
        getWidget().setVisible(true);

        boolean sizeReduced = false;
        // ensure window is not larger than browser window
        if (getWidget().getOffsetWidth() > Window.getClientWidth()) {
            getWidget().setWidth(Window.getClientWidth() + "px");
            sizeReduced = true;
        }
        if (getWidget().getOffsetHeight() > Window.getClientHeight()) {
            getWidget().setHeight(Window.getClientHeight() + "px");
            sizeReduced = true;
        }

        if (getWidget().dynamicHeight && getWidget().layoutRelativeHeight) {
            /*
             * Window height is undefined, layout is 100% high so the layout
             * should define the initial window height but on resize the layout
             * should be as high as the window. We fix the height to deal with
             * this.
             */

            int h = getWidget().contents.getOffsetHeight()
                    + getWidget().getExtraHeight();
            int w = getWidget().getElement().getOffsetWidth();

            client.updateVariable(getId(), "height", h, false);
            client.updateVariable(getId(), "width", w, true);
        }

        if (sizeReduced) {
            // If we changed the size we need to update the size of the child
            // component if it is relative (#3407)
            client.runDescendentsLayout(getWidget());
        }

        Util.runWebkitOverflowAutoFix(getWidget().contentPanel.getElement());

        client.getView().getWidget().scrollIntoView(uidl);

        if (uidl.hasAttribute("bringToFront")) {
            /*
             * Focus as a side-efect. Will be overridden by
             * ApplicationConnection if another component was focused by the
             * server side.
             */
            getWidget().contentPanel.focus();
            getWidget().bringToFrontSequence = uidl
                    .getIntAttribute("bringToFront");
            VWindow.deferOrdering();
        }
    }

    public void updateCaption(ComponentConnector component, UIDL uidl) {
        // NOP, window has own caption, layout captio not rendered
    }

    public void onBeforeShortcutAction(Event e) {
        // NOP, nothing to update just avoid workaround ( causes excess
        // blur/focus )
    }

    @Override
    public VWindow getWidget() {
        return (VWindow) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VWindow.class);
    }

    public void layout() {
        getWidget().requestLayout();
    }

    public void postLayout() {
        VWindow window = getWidget();
        if (window.centered) {
            window.center();
            window.updateShadowSizeAndPosition();
        }
    }

}
