/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.client.ui;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.Util;
import com.vaadin.client.VCaptionWrapper;
import com.vaadin.client.VConsole;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.ShortcutActionHandler.ShortcutActionHandlerOwner;
import com.vaadin.client.ui.popupview.VisibilityChangeEvent;
import com.vaadin.client.ui.popupview.VisibilityChangeHandler;

public class VPopupView extends HTML implements Iterable<Widget> {

    public static final String CLASSNAME = "v-popupview";

    /**
     * For server-client communication.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public String uidlId;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /**
     * Helps to communicate popup visibility to the server.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public boolean hostPopupVisible;

    /** For internal use only. May be removed or replaced in the future. */
    public final CustomPopup popup;
    private final Label loading = new Label();

    /**
     * loading constructor
     */
    public VPopupView() {
        super();
        popup = new CustomPopup();

        setStyleName(CLASSNAME);
        popup.setStyleName(CLASSNAME + "-popup");
        loading.setStyleName(CLASSNAME + "-loading");

        setHTML("");
        popup.setWidget(loading);

        // When we click to open the popup...
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                preparePopup(popup);
                showPopup(popup);
                center();
                fireEvent(new VisibilityChangeEvent(true));
            }
        });

        // ..and when we close it
        popup.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                fireEvent(new VisibilityChangeEvent(false));
            }
        });

        // TODO: Enable animations once GWT fix has been merged
        popup.setAnimationEnabled(false);

        popup.setAutoHideOnHistoryEventsEnabled(false);
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void preparePopup(final CustomPopup popup) {
        popup.setVisible(true);
        popup.setWidget(loading);
        popup.show();
    }

    /**
     * Determines the correct position for a popup and displays the popup at
     * that position.
     * 
     * By default, the popup is shown centered relative to its host component,
     * ensuring it is visible on the screen if possible.
     * 
     * Can be overridden to customize the popup position.
     * 
     * @param popup
     */
    public void showPopup(final CustomPopup popup) {
        popup.setPopupPosition(0, 0);
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void center() {
        int windowTop = RootPanel.get().getAbsoluteTop();
        int windowLeft = RootPanel.get().getAbsoluteLeft();
        int windowRight = windowLeft + RootPanel.get().getOffsetWidth();
        int windowBottom = windowTop + RootPanel.get().getOffsetHeight();

        int offsetWidth = popup.getOffsetWidth();
        int offsetHeight = popup.getOffsetHeight();

        int hostHorizontalCenter = VPopupView.this.getAbsoluteLeft()
                + VPopupView.this.getOffsetWidth() / 2;
        int hostVerticalCenter = VPopupView.this.getAbsoluteTop()
                + VPopupView.this.getOffsetHeight() / 2;

        int left = hostHorizontalCenter - offsetWidth / 2;
        int top = hostVerticalCenter - offsetHeight / 2;

        // Don't show the popup outside the screen.
        if ((left + offsetWidth) > windowRight) {
            left -= (left + offsetWidth) - windowRight;
        }

        if ((top + offsetHeight) > windowBottom) {
            top -= (top + offsetHeight) - windowBottom;
        }

        if (left < 0) {
            left = 0;
        }

        if (top < 0) {
            top = 0;
        }

        popup.setPopupPosition(left, top);
    }

    /**
     * Make sure that we remove the popup when the main widget is removed.
     * 
     * @see com.google.gwt.user.client.ui.Widget#onUnload()
     */
    @Override
    protected void onDetach() {
        popup.hide();
        super.onDetach();
    }

    private static native void nativeBlur(Element e)
    /*-{
        if(e && e.blur) {
            e.blur();
        }
    }-*/;

    /**
     * This class is only public to enable overriding showPopup, and is
     * currently not intended to be extended or otherwise used directly. Its API
     * (other than it being a VOverlay) is to be considered private and
     * potentially subject to change.
     */
    public class CustomPopup extends VOverlay implements
            StateChangeEvent.StateChangeHandler {

        private ComponentConnector popupComponentConnector = null;

        /** For internal use only. May be removed or replaced in the future. */
        public Widget popupComponentWidget = null;

        /** For internal use only. May be removed or replaced in the future. */
        public VCaptionWrapper captionWrapper = null;

        private boolean hasHadMouseOver = false;
        private boolean hideOnMouseOut = true;
        private final Set<Element> activeChildren = new HashSet<Element>();

        private ShortcutActionHandler shortcutActionHandler;

        public CustomPopup() {
            super(true, false, true); // autoHide, not modal, dropshadow
            setOwner(VPopupView.this);
            // Delegate popup keyboard events to the relevant handler. The
            // events do not propagate automatically because the popup is
            // directly attached to the RootPanel.
            addDomHandler(new KeyDownHandler() {
                @Override
                public void onKeyDown(KeyDownEvent event) {
                    if (shortcutActionHandler != null) {
                        shortcutActionHandler.handleKeyboardEvent(Event
                                .as(event.getNativeEvent()));
                    }
                }
            }, KeyDownEvent.getType());
        }

        // For some reason ONMOUSEOUT events are not always received, so we have
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

            // Was the TAB key released outside of our popup?
            if (!eventTargetsPopup && type == Event.ONKEYUP
                    && event.getKeyCode() == KeyCodes.KEY_TAB) {
                // Should we hide on focus out (mouse out)?
                if (hideOnMouseOut) {
                    hide();
                    return true;
                }
            }

            return super.onEventPreview(event);
        }

        @Override
        public void hide(boolean autoClosed) {
            VConsole.log("Hiding popupview");
            syncChildren();
            clearPopupComponentConnector();
            hasHadMouseOver = false;
            shortcutActionHandler = null;
            super.hide(autoClosed);
        }

        @Override
        public void show() {
            // Find the shortcut action handler that should handle keyboard
            // events from the popup. The events do not propagate automatically
            // because the popup is directly attached to the RootPanel.
            Widget widget = VPopupView.this;
            while (shortcutActionHandler == null && widget != null) {
                if (widget instanceof ShortcutActionHandlerOwner) {
                    shortcutActionHandler = ((ShortcutActionHandlerOwner) widget)
                            .getShortcutActionHandler();
                }
                widget = widget.getParent();
            }

            super.show();
        }

        /**
         * Try to sync all known active child widgets to server
         */
        public void syncChildren() {
            // Notify children with focus
            if ((popupComponentWidget instanceof Focusable)) {
                ((Focusable) popupComponentWidget).setFocus(false);
            } else {

                checkForRTE(popupComponentWidget);
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

        private void checkForRTE(Widget popupComponentWidget2) {
            if (popupComponentWidget2 instanceof VRichTextArea) {
                ComponentConnector rtaConnector = Util
                        .findConnectorFor(popupComponentWidget2);
                if (rtaConnector != null) {
                    rtaConnector.flush();
                }
            } else if (popupComponentWidget2 instanceof HasWidgets) {
                HasWidgets hw = (HasWidgets) popupComponentWidget2;
                Iterator<Widget> iterator = hw.iterator();
                while (iterator.hasNext()) {
                    checkForRTE(iterator.next());
                }
            }
        }

        private void clearPopupComponentConnector() {
            if (popupComponentConnector != null) {
                popupComponentConnector.removeStateChangeHandler(this);
            }
            popupComponentConnector = null;
            popupComponentWidget = null;
            captionWrapper = null;
        }

        @Override
        public boolean remove(Widget w) {
            clearPopupComponentConnector();
            return super.remove(w);
        }

        public void setPopupConnector(ComponentConnector newPopupComponent) {

            if (newPopupComponent != popupComponentConnector) {
                if (popupComponentConnector != null) {
                    popupComponentConnector.removeStateChangeHandler(this);
                }
                Widget newWidget = newPopupComponent.getWidget();
                setWidget(newWidget);
                popupComponentWidget = newWidget;
                popupComponentConnector = newPopupComponent;
                popupComponentConnector.addStateChangeHandler("height", this);
                popupComponentConnector.addStateChangeHandler("width", this);
            }

        }

        public void setHideOnMouseOut(boolean hideOnMouseOut) {
            this.hideOnMouseOut = hideOnMouseOut;
        }

        @Override
        public com.google.gwt.user.client.Element getContainerElement() {
            return super.getContainerElement();
        }

        @Override
        public void onStateChanged(StateChangeEvent stateChangeEvent) {
            positionOrSizeUpdated();
        }

    }// class CustomPopup

    public HandlerRegistration addVisibilityChangeHandler(
            final VisibilityChangeHandler visibilityChangeHandler) {
        return addHandler(visibilityChangeHandler,
                VisibilityChangeEvent.getType());
    }

    @Override
    public Iterator<Widget> iterator() {
        return Collections.singleton((Widget) popup).iterator();
    }

}// class VPopupView
