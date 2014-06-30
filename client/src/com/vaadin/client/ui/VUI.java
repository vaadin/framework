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

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.Focusable;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.Profiler;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.ShortcutActionHandler.ShortcutActionHandlerOwner;
import com.vaadin.client.ui.TouchScrollDelegate.TouchScrollHandler;
import com.vaadin.client.ui.ui.UIConnector;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.ui.ui.UIConstants;

/**
 * 
 */
public class VUI extends SimplePanel implements ResizeHandler,
        Window.ClosingHandler, ShortcutActionHandlerOwner, Focusable,
        com.google.gwt.user.client.ui.Focusable, HasResizeHandlers,
        HasScrollHandlers {

    private static int MONITOR_PARENT_TIMER_INTERVAL = 1000;

    /** For internal use only. May be removed or replaced in the future. */
    public String id;

    /** For internal use only. May be removed or replaced in the future. */
    public ShortcutActionHandler actionHandler;

    /*
     * Last known window size used to detect whether VView should be layouted
     * again. Detection must check window size, because the VView size might be
     * fixed and thus not automatically adapt to changed window sizes.
     */
    private int windowWidth;
    private int windowHeight;

    /*
     * Last know view size used to detect whether new dimensions should be sent
     * to the server.
     */
    private int viewWidth;
    private int viewHeight;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection connection;

    /**
     * Keep track of possible parent size changes when an embedded application.
     * 
     * Uses {@link #parentWidth} and {@link #parentHeight} as an optimization to
     * keep track of when there is a real change.
     */
    private Timer resizeTimer;

    /** stored width of parent for embedded application auto-resize */
    private int parentWidth;

    /** stored height of parent for embedded application auto-resize */
    private int parentHeight;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean immediate;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean resizeLazy = false;

    private HandlerRegistration historyHandlerRegistration;

    private TouchScrollHandler touchScrollHandler;

    /**
     * The current URI fragment, used to avoid sending updates if nothing has
     * changed.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public String currentFragment;

    /**
     * Listener for URI fragment changes. Notifies the server of the new value
     * whenever the value changes.
     */
    private final ValueChangeHandler<String> historyChangeHandler = new ValueChangeHandler<String>() {

        @Override
        public void onValueChange(ValueChangeEvent<String> event) {
            String newFragment = event.getValue();

            // Send the location to the server if the fragment has changed
            // and flush active connectors in UI.
            if (!newFragment.equals(currentFragment) && connection != null) {
                /*
                 * Ensure the fragment is properly encoded in all browsers
                 * (#10769)
                 * 
                 * createUrlBuilder does not properly pass an empty fragment to
                 * UrlBuilder on Webkit browsers so do it manually (#11686)
                 */
                String location = Window.Location
                        .createUrlBuilder()
                        .setHash(
                                URL.decodeQueryString(Window.Location.getHash()))
                        .buildString();

                currentFragment = newFragment;
                connection.flushActiveConnector();
                connection.updateVariable(id, UIConstants.LOCATION_VARIABLE,
                        location, true);
            }
        }
    };

    private VLazyExecutor delayedResizeExecutor = new VLazyExecutor(200,
            new ScheduledCommand() {

                @Override
                public void execute() {
                    performSizeCheck();
                }

            });

    private Element storedFocus;

    public VUI() {
        super();
        // Allow focusing the view by using the focus() method, the view
        // should not be in the document focus flow
        getElement().setTabIndex(-1);
        makeScrollable();
    }

    /**
     * Start to periodically monitor for parent element resizes if embedded
     * application (e.g. portlet).
     */
    @Override
    protected void onLoad() {
        super.onLoad();
        if (isMonitoringParentSize()) {
            resizeTimer = new Timer() {

                @Override
                public void run() {
                    // trigger check to see if parent size has changed,
                    // recalculate layouts
                    performSizeCheck();
                    resizeTimer.schedule(MONITOR_PARENT_TIMER_INTERVAL);
                }
            };
            resizeTimer.schedule(MONITOR_PARENT_TIMER_INTERVAL);
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        historyHandlerRegistration = History
                .addValueChangeHandler(historyChangeHandler);
        currentFragment = History.getToken();
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        historyHandlerRegistration.removeHandler();
        historyHandlerRegistration = null;
    }

    /**
     * Stop monitoring for parent element resizes.
     */

    @Override
    protected void onUnload() {
        if (resizeTimer != null) {
            resizeTimer.cancel();
            resizeTimer = null;
        }
        super.onUnload();
    }

    /**
     * Called when the window or parent div might have been resized.
     * 
     * This immediately checks the sizes of the window and the parent div (if
     * monitoring it) and triggers layout recalculation if they have changed.
     */
    protected void performSizeCheck() {
        windowSizeMaybeChanged(Window.getClientWidth(),
                Window.getClientHeight());
    }

    /**
     * Called when the window or parent div might have been resized.
     * 
     * This immediately checks the sizes of the window and the parent div (if
     * monitoring it) and triggers layout recalculation if they have changed.
     * 
     * @param newWindowWidth
     *            The new width of the window
     * @param newWindowHeight
     *            The new height of the window
     * 
     * @deprecated use {@link #performSizeCheck()}
     */
    @Deprecated
    protected void windowSizeMaybeChanged(int newWindowWidth,
            int newWindowHeight) {
        if (connection == null) {
            // Connection is null if the timer fires before the first UIDL
            // update
            return;
        }

        boolean changed = false;
        ComponentConnector connector = ConnectorMap.get(connection)
                .getConnector(this);
        if (windowWidth != newWindowWidth) {
            windowWidth = newWindowWidth;
            changed = true;
            connector.getLayoutManager().reportOuterWidth(connector,
                    newWindowWidth);
            VConsole.log("New window width: " + windowWidth);
        }
        if (windowHeight != newWindowHeight) {
            windowHeight = newWindowHeight;
            changed = true;
            connector.getLayoutManager().reportOuterHeight(connector,
                    newWindowHeight);
            VConsole.log("New window height: " + windowHeight);
        }
        Element parentElement = getElement().getParentElement();
        if (isMonitoringParentSize() && parentElement != null) {
            // check also for parent size changes
            int newParentWidth = parentElement.getClientWidth();
            int newParentHeight = parentElement.getClientHeight();
            if (parentWidth != newParentWidth) {
                parentWidth = newParentWidth;
                changed = true;
                VConsole.log("New parent width: " + parentWidth);
            }
            if (parentHeight != newParentHeight) {
                parentHeight = newParentHeight;
                changed = true;
                VConsole.log("New parent height: " + parentHeight);
            }
        }
        if (changed) {
            /*
             * If the window size has changed, layout the VView again and send
             * new size to the server if the size changed. (Just checking VView
             * size would cause us to ignore cases when a relatively sized VView
             * should shrink as the content's size is fixed and would thus not
             * automatically shrink.)
             */
            VConsole.log("Running layout functions due to window or parent resize");

            // update size to avoid (most) redundant re-layout passes
            // there can still be an extra layout recalculation if webkit
            // overflow fix updates the size in a deferred block
            if (isMonitoringParentSize() && parentElement != null) {
                parentWidth = parentElement.getClientWidth();
                parentHeight = parentElement.getClientHeight();
            }

            sendClientResized();

            LayoutManager layoutManager = connector.getLayoutManager();
            if (layoutManager.isLayoutRunning()) {
                layoutManager.layoutLater();
            } else {
                layoutManager.layoutNow();
            }
        }
    }

    /**
     * @return the name of the theme in use by this UI.
     * @deprecated as of 7.3. Use {@link UIConnector#getActiveTheme()} instead.
     */
    @Deprecated
    public String getTheme() {
        return ((UIConnector) ConnectorMap.get(connection).getConnector(this))
                .getActiveTheme();
    }

    /**
     * Returns true if the body is NOT generated, i.e if someone else has made
     * the page that we're running in. Otherwise we're in charge of the whole
     * page.
     * 
     * @return true if we're running embedded
     */
    public boolean isEmbedded() {
        return !getElement().getOwnerDocument().getBody().getClassName()
                .contains(ApplicationConstants.GENERATED_BODY_CLASSNAME);
    }

    /**
     * Returns true if the size of the parent should be checked periodically and
     * the application should react to its changes.
     * 
     * @return true if size of parent should be tracked
     */
    protected boolean isMonitoringParentSize() {
        // could also perform a more specific check (Liferay portlet)
        return isEmbedded();
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        int type = DOM.eventGetType(event);
        if (type == Event.ONKEYDOWN && actionHandler != null) {
            actionHandler.handleKeyboardEvent(event);
            return;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.logical.shared.ResizeHandler#onResize(com.google
     * .gwt.event.logical.shared.ResizeEvent)
     */

    @Override
    public void onResize(ResizeEvent event) {
        triggerSizeChangeCheck();
    }

    /**
     * Called when a resize event is received.
     * 
     * This may trigger a lazy refresh or perform the size check immediately
     * depending on the browser used and whether the server side requests
     * resizes to be lazy.
     */
    private void triggerSizeChangeCheck() {
        /*
         * IE (pre IE9 at least) will give us some false resize events due to
         * problems with scrollbars. Firefox 3 might also produce some extra
         * events. We postpone both the re-layouting and the server side event
         * for a while to deal with these issues.
         * 
         * We may also postpone these events to avoid slowness when resizing the
         * browser window. Constantly recalculating the layout causes the resize
         * operation to be really slow with complex layouts.
         */
        boolean lazy = resizeLazy || BrowserInfo.get().isIE8();

        if (lazy) {
            delayedResizeExecutor.trigger();
        } else {
            performSizeCheck();
        }
    }

    /**
     * Send new dimensions to the server.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public void sendClientResized() {
        Profiler.enter("VUI.sendClientResized");
        Element parentElement = getElement().getParentElement();
        int viewHeight = parentElement.getClientHeight();
        int viewWidth = parentElement.getClientWidth();

        ResizeEvent.fire(this, viewWidth, viewHeight);
        Profiler.leave("VUI.sendClientResized");
    }

    public native static void goTo(String url)
    /*-{
       $wnd.location = url;
     }-*/;

    @Override
    public void onWindowClosing(Window.ClosingEvent event) {
        // Change focus on this window in order to ensure that all state is
        // collected from textfields
        // TODO this is a naive hack, that only works with text fields and may
        // cause some odd issues. Should be replaced with a decent solution, see
        // also related BeforeShortcutActionListener interface. Same interface
        // might be usable here.
        VTextField.flushChangesFromFocusedTextField();
    }

    private native static void loadAppIdListFromDOM(ArrayList<String> list)
    /*-{
         var j;
         for(j in $wnd.vaadin.vaadinConfigurations) {
            // $entry not needed as function is not exported
            list.@java.util.Collection::add(Ljava/lang/Object;)(j);
         }
     }-*/;

    @Override
    public ShortcutActionHandler getShortcutActionHandler() {
        return actionHandler;
    }

    @Override
    public void focus() {
        setFocus(true);
    }

    /**
     * Ensures the widget is scrollable eg. after style name changes.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public void makeScrollable() {
        if (touchScrollHandler == null) {
            touchScrollHandler = TouchScrollDelegate.enableTouchScrolling(this);
        }
        touchScrollHandler.addElement(getElement());
    }

    @Override
    public HandlerRegistration addResizeHandler(ResizeHandler resizeHandler) {
        return addHandler(resizeHandler, ResizeEvent.getType());
    }

    @Override
    public HandlerRegistration addScrollHandler(ScrollHandler scrollHandler) {
        return addHandler(scrollHandler, ScrollEvent.getType());
    }

    @Override
    public int getTabIndex() {
        return FocusUtil.getTabIndex(this);
    }

    @Override
    public void setAccessKey(char key) {
        FocusUtil.setAccessKey(this, key);
    }

    @Override
    public void setFocus(boolean focused) {
        FocusUtil.setFocus(this, focused);
    }

    @Override
    public void setTabIndex(int index) {
        FocusUtil.setTabIndex(this, index);
    }

    /**
     * Allows to store the currently focused Element.
     * 
     * Current use case is to store the focus when a Window is opened. Does
     * currently handle only a single value. Needs to be extended for #12158
     * 
     * @param focusedElement
     */
    public void storeFocus() {
        storedFocus = Util.getFocusedElement();
    }

    /**
     * Restores the previously stored focus Element.
     * 
     * Current use case is to restore the focus when a Window is closed. Does
     * currently handle only a single value. Needs to be extended for #12158
     * 
     * @return the lastFocusElementBeforeDialogOpened
     */
    public void focusStoredElement() {
        if (storedFocus != null) {
            storedFocus.focus();

            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    storedFocus.focus();
                }
            });
        }
    }

}
