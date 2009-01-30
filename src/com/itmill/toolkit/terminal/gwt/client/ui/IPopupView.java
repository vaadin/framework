package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasFocus;
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

public class IPopupView extends HTML implements Container {

    public static final String CLASSNAME = "i-popupview";

    /** For server-client communication */
    private String uidlId;
    private ApplicationConnection client;

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

        setHTML("(No HTML defined for PopupView)");
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
                ((CustomPopup) sender).syncChildren();
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
            preparePopup(popup);
            popup.updateFromUIDL(popupUIDL, client);
            showPopup(popup);

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
        if (uidlId != null && client != null && isAttached()) {
            client.updateVariable(uidlId, "popupVisibility", visible, true);
        }
    }

    private void preparePopup(final CustomPopup popup) {
        popup.setVisible(false);
        popup.show();
    }

    private void showPopup(final CustomPopup popup) {
        int windowTop = RootPanel.get().getAbsoluteTop();
        int windowLeft = RootPanel.get().getAbsoluteLeft();
        int windowRight = windowLeft + RootPanel.get().getOffsetWidth();
        int windowBottom = windowTop + RootPanel.get().getOffsetHeight();

        int offsetWidth = popup.getOffsetWidth();
        int offsetHeight = popup.getOffsetHeight();

        int hostHorizontalCenter = IPopupView.this.getAbsoluteLeft()
                + IPopupView.this.getOffsetWidth() / 2;
        int hostVerticalCenter = IPopupView.this.getAbsoluteTop()
                + IPopupView.this.getOffsetHeight() / 2;

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
        client.unregisterPaintable(popup.popupComponentPaintable);
        super.onDetach();
    }

    private static native void nativeBlur(Element e)
    /*-{ 
        if(e && e.blur) {
            e.blur();
        }
    }-*/;

    private class CustomPopup extends IToolkitOverlay {

        private Paintable popupComponentPaintable = null;
        private Widget popupComponentWidget = null;
        private ICaptionWrapper captionWrapper = null;

        private boolean hasHadMouseOver = false;
        private boolean hideOnMouseOut = true;
        private final Set<Element> activeChildren = new HashSet<Element>();
        private boolean hiding = false;

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

            // Catch children that use keyboard, so we can unfocus them when
            // hiding
            if (eventTargetsPopup && type == Event.ONKEYPRESS) {
                activeChildren.add(target);
            }

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
            hiding = true;
            unregisterPaintables();
            if (popupComponentWidget != null && popupComponentWidget != loading) {
                remove(popupComponentWidget);
            }
            hasHadMouseOver = false;
            super.hide();
        }

        /**
         * Try to sync all known active child widgets to server
         */
        public void syncChildren() {
            // Notify children with focus
            if ((popupComponentWidget instanceof HasFocus)) {
                ((HasFocus) popupComponentWidget).setFocus(false);
            }

            // Notify children that have used the keyboard
            for (Element e : activeChildren) {
                try {
                    nativeBlur(e);
                } catch (Exception ignored) {
                }
            }
            activeChildren.clear();
        }

        @Override
        public boolean remove(Widget w) {

            popupComponentPaintable = null;
            popupComponentWidget = null;
            captionWrapper = null;

            return super.remove(w);
        }

        public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

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

        public void setHideOnMouseOut(boolean hideOnMouseOut) {
            this.hideOnMouseOut = hideOnMouseOut;
        }

        /*
         * 
         * We need a hack make popup act as a child of IPopupView in toolkits
         * component tree, but work in default GWT manner when closing or
         * opening.
         * 
         * (non-Javadoc)
         * 
         * @see com.google.gwt.user.client.ui.Widget#getParent()
         */
        @Override
        public Widget getParent() {
            if (!isAttached() || hiding) {
                return super.getParent();
            } else {
                return IPopupView.this;
            }
        }

        @Override
        protected void onDetach() {
            super.onDetach();
            hiding = false;
        }

    }// class CustomPopup

    // Container methods

    public RenderSpace getAllocatedSpace(Widget child) {
        return new RenderSpace(RootPanel.get().getOffsetWidth(), RootPanel
                .get().getOffsetHeight());
    }

    public boolean hasChildComponent(Widget component) {
        if (popup.popupComponentWidget != null) {
            return popup.popupComponentWidget == component;
        } else {
            return false;
        }
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {

        popup.setWidget(newComponent);
        popup.popupComponentWidget = newComponent;
    }

    public boolean requestLayout(Set<Paintable> child) {
        return true;
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        if (ICaption.isNeeded(uidl)) {
            if (popup.captionWrapper != null) {
                popup.captionWrapper.updateCaption(uidl);
            } else {
                popup.captionWrapper = new ICaptionWrapper(component, client);
                popup.setWidget(popup.captionWrapper);
                popup.captionWrapper.updateCaption(uidl);
            }
        } else {
            if (popup.captionWrapper != null) {
                popup.setWidget(popup.popupComponentWidget);
            }
        }

        popup.popupComponentWidget = (Widget) component;
        popup.popupComponentPaintable = component;
    }

}// class IPopupView
