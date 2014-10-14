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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import com.vaadin.client.Focusable;
import com.vaadin.client.Util;

public class VContextMenu extends VOverlay implements SubPartAware {

    private ActionOwner actionOwner;

    private final CMenuBar menu = new CMenuBar();

    private int left;

    private int top;

    private Element focusedElement;

    private VLazyExecutor delayedImageLoadExecutioner = new VLazyExecutor(100,
            new ScheduledCommand() {
                @Override
                public void execute() {
                    imagesLoaded();
                }
            });

    /**
     * This method should be used only by Client object as only one per client
     * should exists. Request an instance via client.getContextMenu();
     * 
     * @param cli
     *            to be set as an owner of menu
     */
    public VContextMenu() {
        super(true, false, true);
        setWidget(menu);
        setStyleName("v-contextmenu");
        getElement().setId(DOM.createUniqueId());

        addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                Element currentFocus = Util.getFocusedElement();
                if (focusedElement != null
                        && (currentFocus == null
                                || menu.getElement().isOrHasChild(currentFocus) || RootPanel
                                .getBodyElement().equals(currentFocus))) {
                    focusedElement.focus();
                    focusedElement = null;
                }
            }
        });
    }

    protected void imagesLoaded() {
        if (isVisible()) {
            show();
        }
    }

    /**
     * Sets the element from which to build menu
     * 
     * @param ao
     */
    public void setActionOwner(ActionOwner ao) {
        actionOwner = ao;
    }

    /**
     * Shows context menu at given location IF it contain at least one item.
     * 
     * @param left
     * @param top
     */
    public void showAt(int left, int top) {
        final Action[] actions = actionOwner.getActions();
        if (actions == null || actions.length == 0) {
            // Only show if there really are actions
            return;
        }
        this.left = left;
        this.top = top;
        menu.clearItems();
        for (int i = 0; i < actions.length; i++) {
            final Action a = actions[i];
            menu.addItem(new MenuItem(a.getHTML(), true, a));
        }

        // Attach onload listeners to all images
        Util.sinkOnloadForImages(menu.getElement());

        // Store the currently focused element, which will be re-focused when
        // context menu is closed
        focusedElement = Util.getFocusedElement();

        // reset height (if it has been previously set explicitly)
        setHeight("");

        setPopupPositionAndShow(new PositionCallback() {
            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                // mac FF gets bad width due GWT popups overflow hacks,
                // re-determine width
                offsetWidth = menu.getOffsetWidth();
                int left = VContextMenu.this.left;
                int top = VContextMenu.this.top;
                if (offsetWidth + left > Window.getClientWidth()) {
                    left = left - offsetWidth;
                    if (left < 0) {
                        left = 0;
                    }
                }
                if (offsetHeight + top > Window.getClientHeight()) {
                    top = Math.max(0, Window.getClientHeight() - offsetHeight);
                }
                if (top == 0) {
                    setHeight(Window.getClientHeight() + "px");
                }
                setPopupPosition(left, top);

                /*
                 * Move keyboard focus to menu, deferring the focus setting so
                 * the focus is certainly moved to the menu in all browser after
                 * the positioning has been done.
                 */
                Scheduler.get().scheduleDeferred(new Command() {
                    @Override
                    public void execute() {
                        // Focus the menu.
                        menu.setFocus(true);

                        // Unselect previously selected items
                        menu.selectItem(null);
                    }
                });

            }
        });
    }

    public void showAt(ActionOwner ao, int left, int top) {
        setActionOwner(ao);
        showAt(left, top);
    }

    /**
     * Extend standard Gwt MenuBar to set proper settings and to override
     * onPopupClosed method so that PopupPanel gets closed.
     */
    class CMenuBar extends MenuBar implements HasFocusHandlers,
            HasBlurHandlers, HasKeyDownHandlers, HasKeyPressHandlers,
            Focusable, LoadHandler, KeyUpHandler {
        public CMenuBar() {
            super(true);
            addDomHandler(this, LoadEvent.getType());
            addDomHandler(this, KeyUpEvent.getType());
        }

        @Override
        public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
            super.onPopupClosed(sender, autoClosed);

            // make focusable, as we don't need access key magic we don't need
            // to
            // use FocusImpl.createFocusable
            getElement().setTabIndex(0);

            hide();
        }

        /*
         * public void onBrowserEvent(Event event) { // Remove current selection
         * when mouse leaves if (DOM.eventGetType(event) == Event.ONMOUSEOUT) {
         * Element to = DOM.eventGetToElement(event); if
         * (!DOM.isOrHasChild(getElement(), to)) { DOM.setElementProperty(
         * super.getSelectedItem().getElement(), "className",
         * super.getSelectedItem().getStylePrimaryName()); } }
         * 
         * super.onBrowserEvent(event); }
         */

        private MenuItem getItem(int index) {
            return super.getItems().get(index);
        }

        @Override
        public HandlerRegistration addFocusHandler(FocusHandler handler) {
            return addDomHandler(handler, FocusEvent.getType());
        }

        @Override
        public HandlerRegistration addBlurHandler(BlurHandler handler) {
            return addDomHandler(handler, BlurEvent.getType());
        }

        @Override
        public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
            return addDomHandler(handler, KeyDownEvent.getType());
        }

        @Override
        public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
            return addDomHandler(handler, KeyPressEvent.getType());
        }

        public void setFocus(boolean focus) {
            if (focus) {
                FocusImpl.getFocusImplForPanel().focus(getElement());
            } else {
                FocusImpl.getFocusImplForPanel().blur(getElement());
            }
        }

        @Override
        public void focus() {
            setFocus(true);
        }

        @Override
        public void onLoad(LoadEvent event) {
            // Handle icon onload events to ensure shadow is resized correctly
            delayedImageLoadExecutioner.trigger();
        }

        @Override
        public void onKeyUp(KeyUpEvent event) {
            // Allow to close context menu with ESC
            if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                hide();
            }
        }
    }

    @Override
    public com.google.gwt.user.client.Element getSubPartElement(String subPart) {
        int index = Integer.parseInt(subPart.substring(6));
        // ApplicationConnection.getConsole().log(
        // "Searching element for selection index " + index);
        MenuItem item = menu.getItem(index);
        // ApplicationConnection.getConsole().log("Item: " + item);
        // Item refers to the td, which is the parent of the clickable element
        return item.getElement().getFirstChildElement().cast();
    }

    @Override
    public String getSubPartName(com.google.gwt.user.client.Element subElement) {
        if (getElement().isOrHasChild(subElement)) {
            com.google.gwt.dom.client.Element e = subElement;
            {
                while (e != null && !e.getTagName().toLowerCase().equals("tr")) {
                    e = e.getParentElement();
                    // ApplicationConnection.getConsole().log("Found row");
                }
            }
            com.google.gwt.dom.client.TableSectionElement parentElement = (TableSectionElement) e
                    .getParentElement();
            NodeList<TableRowElement> rows = parentElement.getRows();
            for (int i = 0; i < rows.getLength(); i++) {
                if (rows.getItem(i) == e) {
                    // ApplicationConnection.getConsole().log(
                    // "Found index for row" + 1);
                    return "option" + i;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    /**
     * Hides context menu if it is currently shown by given action owner.
     * 
     * @param actionOwner
     */
    public void ensureHidden(ActionOwner actionOwner) {
        if (this.actionOwner == actionOwner) {
            hide();
        }
    }
}
