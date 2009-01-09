package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.ICaption;
import com.itmill.toolkit.terminal.gwt.client.ICaptionWrapper;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.RenderSpace;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IPopupView extends HTML implements Paintable {

    public static final String CLASSNAME = "i-popupview";

    /** For server-client communication */
    private String uidlId;
    private ApplicationConnection client;

    /** For inner classes */
    private final IPopupView hostReference = this;

    /** This variable helps to communicate popup visibility to the server */
    private boolean hostPopupVisible;

    private final CustomPopup popup;
    private final Label loading = new Label("Loading...");

    /**
     * loading constructor
     */
    public IPopupView() {
        super();
        popup = new CustomPopup();

        setStyleName(CLASSNAME);
        popup.setStylePrimaryName(CLASSNAME + "-popup");

        setHTML("PopupPanel");
        popup.setWidget(loading);

        // When we click to open the popup...
        addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                updateState(true);
            }
        });

        // ..and when we close it
        popup.addPopupListener(new PopupListener() {
            public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
                updateState(false);
            }
        });

        popup.setAnimationEnabled(true);

    }

    /**
     * 
     * 
     * @see com.itmill.toolkit.terminal.gwt.client.Paintable#updateFromUIDL(com.itmill.toolkit.terminal.gwt.client.UIDL,
     *      com.itmill.toolkit.terminal.gwt.client.ApplicationConnection)
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // This call should be made first. Ensure correct implementation,
        // and don't let the containing layout manage caption.
        if (client.updateComponent(this, uidl, false)) {
            return;
        }
        // These are for future server connections
        this.client = client;
        uidlId = uidl.getId();

        hostPopupVisible = uidl.getBooleanVariable("popupVisibility");

        setHTML(uidl.getStringAttribute("html"));

        if (uidl.hasAttribute("description")) {
            setTitle(uidl.getStringAttribute("description"));
        }

        if (uidl.hasAttribute("hideOnMouseOut")) {
            popup.setHideOnMouseOut(uidl.getBooleanAttribute("hideOnMouseOut"));
        }

        // Render the popup if visible and show it.
        if (hostPopupVisible) {
            UIDL popupUIDL = uidl.getChildUIDL(0);

            // showPopupOnTop(popup, hostReference);
            preparePopup(popup, hostReference);
            popup.updateFromUIDL(popupUIDL, client);
            showPopup(popup, hostReference);

            // The popup shouldn't be visible, try to hide it.
        } else {
            popup.hide();
        }
    }// updateFromUIDL

    /**
     * Update popup visibility to server
     * 
     * @param visibility
     */
    private void updateState(boolean visible) {
        // If we know the server connection
        // then update the current situation
        if (uidlId != null && client != null && this.isAttached()) {
            client.updateVariable(uidlId, "popupVisibility", visible, true);
        }
    }

    private void preparePopup(final CustomPopup popup, final Widget host) {
        popup.setVisible(false);
        popup.show();
    }

    private void showPopup(final CustomPopup popup, final Widget host) {
        int windowTop = RootPanel.get().getAbsoluteTop();
        int windowLeft = RootPanel.get().getAbsoluteLeft();
        int windowRight = windowLeft + RootPanel.get().getOffsetWidth();
        int windowBottom = windowTop + RootPanel.get().getOffsetHeight();

        int offsetWidth = popup.getOffsetWidth();
        int offsetHeight = popup.getOffsetHeight();

        int hostHorizontalCenter = host.getAbsoluteLeft()
                + host.getOffsetWidth() / 2;
        int hostVerticalCenter = host.getAbsoluteTop() + host.getOffsetHeight()
                / 2;

        int left = hostHorizontalCenter - offsetWidth / 2;
        int top = hostVerticalCenter - offsetHeight / 2;

        // Superclass takes care of top and left
        if ((left + offsetWidth) > windowRight) {
            left -= (left + offsetWidth) - windowRight;
        }

        if ((top + offsetHeight) > windowBottom) {
            top -= (top + offsetHeight) - windowBottom;
        }

        popup.setPopupPosition(left, top);

        popup.setVisible(true);
    }

    /**
     * Make sure that we remove the popup when the main widget is removed.
     * 
     * @see com.google.gwt.user.client.ui.Widget#onUnload()
     */
    @Override
    protected void onDetach() {
        popup.hide();
        client.unregisterPaintable(popup);
        super.onDetach();
    }

    private class CustomPopup extends IToolkitOverlay implements Container {

        private Paintable popupComponentPaintable = null;
        private Widget popupComponentWidget = null;
        private ICaptionWrapper captionWrapper = null;

        private boolean hasHadMouseOver = false;
        private boolean hideOnMouseOut = true;

        public CustomPopup() {
            super(true, false, true); // autoHide, not modal, dropshadow
        }

        // For some reason ONMOUSEOUT events are not always recieved, so we have
        // to use ONMOUSEMOVE that doesn't target the popup
        @Override
        public boolean onEventPreview(Event event) {
            Element target = DOM.eventGetTarget(event);
            boolean eventTargetsPopup = DOM.isOrHasChild(getElement(), target);
            int type = DOM.eventGetType(event);

            if (eventTargetsPopup && type == Event.ONMOUSEMOVE) {
                hasHadMouseOver = true;
            }

            if (!eventTargetsPopup && type == Event.ONMOUSEMOVE) {

                if (hasHadMouseOver && hideOnMouseOut) {
                    hide();
                    return true;
                }
            }

            return super.onEventPreview(event);
        }

        @Override
        public void hide() {
            unregisterPaintables();
            if (popupComponentWidget != null && popupComponentWidget != loading) {
                remove(popupComponentWidget);
            }
            hasHadMouseOver = false;
            super.hide();
        }

        @Override
        public boolean remove(Widget w) {

            popupComponentPaintable = null;
            popupComponentWidget = null;
            captionWrapper = null;

            return super.remove(w);
        }

        public boolean hasChildComponent(Widget component) {
            if (popupComponentWidget != null) {
                return popupComponentWidget.equals(component);
            } else {
                return false;
            }
        }

        public void replaceChildComponent(Widget oldComponent,
                Widget newComponent) {

            setWidget(newComponent);
            popupComponentWidget = newComponent;
        }

        public void updateCaption(Paintable component, UIDL uidl) {
            if (ICaption.isNeeded(uidl)) {
                if (captionWrapper != null) {
                    captionWrapper.updateCaption(uidl);
                } else {
                    captionWrapper = new ICaptionWrapper(component, client);
                    setWidget(captionWrapper);
                    captionWrapper.updateCaption(uidl);
                }
            } else {
                if (captionWrapper != null) {
                    setWidget(popupComponentWidget);
                }
            }

            popupComponentWidget = (Widget) component;
            popupComponentPaintable = component;
        }

        public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
            if (client.updateComponent(this, uidl, false)) {
                return;
            }

            Paintable newPopupComponent = client.getPaintable(uidl
                    .getChildUIDL(0));

            if (newPopupComponent != popupComponentPaintable) {

                setWidget((Widget) newPopupComponent);

                popupComponentWidget = (Widget) newPopupComponent;

                popupComponentPaintable = newPopupComponent;
            }

            popupComponentPaintable
                    .updateFromUIDL(uidl.getChildUIDL(0), client);

        }

        public void unregisterPaintables() {
            if (popupComponentPaintable != null) {
                client.unregisterPaintable(popupComponentPaintable);
            }
        }

        public boolean requestLayout(Set<Paintable> child) {
            return true;
        }

        public RenderSpace getAllocatedSpace(Widget child) {
            return new RenderSpace(RootPanel.get().getOffsetWidth(), RootPanel
                    .get().getOffsetHeight());
        }

        public boolean isHideOnMouseOut() {
            return hideOnMouseOut;
        }

        public void setHideOnMouseOut(boolean hideOnMouseOut) {
            this.hideOnMouseOut = hideOnMouseOut;
        }

    }// class CustomPopup
}// class IPopupView
