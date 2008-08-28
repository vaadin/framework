package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;
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
import com.itmill.toolkit.terminal.gwt.client.ICaption;
import com.itmill.toolkit.terminal.gwt.client.ICaptionWrapper;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IPopupView extends HTML implements Paintable, Container {

    public static final String CLASSNAME = "i-popupview";

    /** For server-client communication */
    private String uidlId;
    private ApplicationConnection client;

    /** For inner classes */
    private final IPopupView hostReference = this;

    /** This variable helps to communicate popup visibility to the server */
    private boolean hostPopupVisible;

    private CustomPopup popup;
    private final Label loading = new Label("Loading...");

    // Browser window sizes
    int windowTop;
    int windowLeft;
    int windowRight;
    int windowBottom;

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

        updateWindowSize();

        hostPopupVisible = uidl.getBooleanAttribute("popupVisible");
        setHTML(uidl.getStringAttribute("html"));

        if (uidl.hasAttribute("description")) {
            setTitle(uidl.getStringAttribute("description"));
        }

        // Render the popup if visible and show it. The component inside can
        // change dynamically.
        if (hostPopupVisible) {
            UIDL popupUIDL = uidl.getChildUIDL(0);

            popup.updateFromUIDL(popupUIDL, client);
            showPopupOnTop(popup, hostReference);

        } else { // The popup isn't visible so we should remove its child
            popup.setWidget(null);
        }
    }// updateFromUIDL

    /**
     * 
     * @param visibility
     */
    private void updateState(boolean visibility) {
        // If we know the server connection
        // then update the current situation
        if (uidlId != null && client != null) {
            client.updateVariable(uidlId, "popupVisibility", visibility, true);
        }
    }

    /**
     * This shows the popup on top of the widget below. This function allows us
     * to position the popup before making it visible.
     * 
     * @param popup
     *            the popup to show
     * @param host
     *            the widget to draw the popup on
     */
    private void showPopupOnTop(final CustomPopup popup, final Widget host) {
        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                int hostHorizontalCenter = host.getAbsoluteLeft()
                        + host.getOffsetWidth() / 2;
                int hostVerticalCenter = host.getAbsoluteTop()
                        + host.getOffsetHeight() / 2;

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
            }
        });
    }

    public void updateWindowSize() {
        windowTop = RootPanel.get().getAbsoluteTop();
        windowLeft = RootPanel.get().getAbsoluteLeft();
        windowRight = windowLeft + RootPanel.get().getOffsetWidth();
        windowBottom = windowTop + RootPanel.get().getOffsetHeight();
    }

    public boolean hasChildComponent(Widget component) {
        return popup.equals(component);
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        if (newComponent == null || newComponent instanceof CustomPopup) {
            popup.hide();

            if (popup != null) {
                client.unregisterPaintable(popup);
            }
            popup = (CustomPopup) newComponent;

        } else {
            throw new IllegalArgumentException(
                    "PopupPanel only supports components of type CustomPopup");
        }

    }

    public void updateCaption(Paintable component, UIDL uidl) {

    }

    public static native void nativeBlur(Element e) /*-{ 
                                                if(e.focus) {
                                                    e.blur();
                                                  }
                                              }-*/;

    private class CustomPopup extends IToolkitOverlay implements Container {

        private Paintable popupComponentPaintable = null;
        private Widget popupComponentWidget = null;
        private ICaptionWrapper captionWrapper = null;

        private boolean hasHadMouseOver = false;
        private Set activeChildren;

        public CustomPopup() {
            super(true, false, true); // autoHide, not modal, dropshadow
            activeChildren = new HashSet();
        }

        // For some reason ONMOUSEOUT events are not always recieved, so we have
        // to use ONMOUSEMOVE that doesn't target the popup
        public boolean onEventPreview(Event event) {

            Element target = DOM.eventGetTarget(event);
            boolean eventTargetsPopup = DOM.isOrHasChild(getElement(), target);
            int type = DOM.eventGetType(event);

            // Catch children that use keyboard, so we can unfocus them when
            // hiding
            if (type == Event.ONKEYPRESS) {
                activeChildren.add(target);
            }

            if (eventTargetsPopup & type == Event.ONMOUSEMOVE) {
                hasHadMouseOver = true;
            }

            if (!eventTargetsPopup & type == Event.ONMOUSEMOVE) {
                if (hasHadMouseOver) {
                    hide();
                    hasHadMouseOver = false;
                    return true;
                }
            }

            return super.onEventPreview(event);
        }

        public void hide() {
            // Notify children with focus
            if ((popupComponentWidget instanceof HasFocus)) {
                ((HasFocus) popupComponentWidget).setFocus(false);
            }

            for (Iterator iterator = activeChildren.iterator(); iterator
                    .hasNext();) {
                nativeBlur((Element) iterator.next());
            }
            activeChildren.clear();

            super.hide();
        }

        public void setWidget(Widget w) {
            super.setWidget(w);

            if (w == null) {

                unregisterPaintables();

                popupComponentPaintable = null;
                popupComponentWidget = null;
                captionWrapper = null;
            }
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
            // System.out.println("CustomPopup replacechildcomponent");
            if (oldComponent != null) {
                client.unregisterPaintable((Paintable) oldComponent);
            }

            popupComponentWidget = newComponent;

            setWidget(popupComponentWidget);
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

        }

        public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
            if (client.updateComponent(this, uidl, false)) {
                return;
            }

            Paintable newPopupComponent = client.getPaintable(uidl
                    .getChildUIDL(0));

            if (newPopupComponent != popupComponentPaintable) {

                unregisterPaintables();

                popupComponentWidget = (Widget) newPopupComponent;

                popup.setWidget(popupComponentWidget);
                popupComponentPaintable = newPopupComponent;
                popupComponentPaintable.updateFromUIDL(uidl.getChildUIDL(0),
                        client);
            }

        }

        private void unregisterPaintables() {
            if (popupComponentPaintable != null) {
                client.unregisterPaintable(popupComponentPaintable);
            }
        }

    }// class CustomPopup

}// class IPopupView
