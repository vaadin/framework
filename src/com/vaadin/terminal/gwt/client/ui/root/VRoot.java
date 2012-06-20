/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.root;

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler.ShortcutActionHandlerOwner;
import com.vaadin.terminal.gwt.client.ui.TouchScrollDelegate;
import com.vaadin.terminal.gwt.client.ui.TouchScrollDelegate.TouchScrollHandler;
import com.vaadin.terminal.gwt.client.ui.VLazyExecutor;
import com.vaadin.terminal.gwt.client.ui.textfield.VTextField;

/**
 *
 */
public class VRoot extends SimplePanel implements ResizeHandler,
        Window.ClosingHandler, ShortcutActionHandlerOwner, Focusable {

    public static final String FRAGMENT_VARIABLE = "fragment";

    public static final String BROWSER_HEIGHT_VAR = "browserHeight";

    public static final String BROWSER_WIDTH_VAR = "browserWidth";

    private static final String CLASSNAME = "v-view";

    public static final String NOTIFICATION_HTML_CONTENT_NOT_ALLOWED = "useplain";

    private static int MONITOR_PARENT_TIMER_INTERVAL = 1000;

    String theme;

    String id;

    ShortcutActionHandler actionHandler;

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

    ApplicationConnection connection;

    /** Identifies the click event */
    public static final String CLICK_EVENT_ID = "click";

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

    int scrollTop;

    int scrollLeft;

    boolean rendering;

    boolean scrollable;

    boolean immediate;

    boolean resizeLazy = false;

    /**
     * Attribute name for the lazy resize setting .
     */
    public static final String RESIZE_LAZY = "rL";

    private HandlerRegistration historyHandlerRegistration;

    private TouchScrollHandler touchScrollHandler;

    /**
     * The current URI fragment, used to avoid sending updates if nothing has
     * changed.
     */
    String currentFragment;

    /**
     * Listener for URI fragment changes. Notifies the server of the new value
     * whenever the value changes.
     */
    private final ValueChangeHandler<String> historyChangeHandler = new ValueChangeHandler<String>() {

        public void onValueChange(ValueChangeEvent<String> event) {
            String newFragment = event.getValue();

            // Send the new fragment to the server if it has changed
            if (!newFragment.equals(currentFragment) && connection != null) {
                currentFragment = newFragment;
                connection.updateVariable(id, FRAGMENT_VARIABLE, newFragment,
                        true);
            }
        }
    };

    private VLazyExecutor delayedResizeExecutor = new VLazyExecutor(200,
            new ScheduledCommand() {

                public void execute() {
                    performSizeCheck();
                }

            });

    public VRoot() {
        super();
        setStyleName(CLASSNAME);

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

            connector.getLayoutManager().layoutNow();
        }
    }

    public String getTheme() {
        return theme;
    }

    /**
     * Used to reload host page on theme changes.
     */
    static native void reloadHostPage()
    /*-{
         $wnd.location.reload();
     }-*/;

    /**
     * Returns true if the body is NOT generated, i.e if someone else has made
     * the page that we're running in. Otherwise we're in charge of the whole
     * page.
     * 
     * @return true if we're running embedded
     */
    public boolean isEmbedded() {
        return !getElement().getOwnerDocument().getBody().getClassName()
                .contains(ApplicationConnection.GENERATED_BODY_CLASSNAME);
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
        } else if (scrollable && type == Event.ONSCROLL) {
            updateScrollPosition();
        }
    }

    /**
     * Updates scroll position from DOM and saves variables to server.
     */
    private void updateScrollPosition() {
        int oldTop = scrollTop;
        int oldLeft = scrollLeft;
        scrollTop = DOM.getElementPropertyInt(getElement(), "scrollTop");
        scrollLeft = DOM.getElementPropertyInt(getElement(), "scrollLeft");
        if (connection != null && !rendering) {
            if (oldTop != scrollTop) {
                connection.updateVariable(id, "scrollTop", scrollTop, false);
            }
            if (oldLeft != scrollLeft) {
                connection.updateVariable(id, "scrollLeft", scrollLeft, false);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.logical.shared.ResizeHandler#onResize(com.google
     * .gwt.event.logical.shared.ResizeEvent)
     */

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
     */
    private void sendClientResized() {
        Element parentElement = getElement().getParentElement();
        int viewHeight = parentElement.getClientHeight();
        int viewWidth = parentElement.getClientWidth();

        connection.updateVariable(id, "height", viewHeight, false);
        connection.updateVariable(id, "width", viewWidth, false);

        int windowWidth = Window.getClientWidth();
        int windowHeight = Window.getClientHeight();

        connection.updateVariable(id, BROWSER_WIDTH_VAR, windowWidth, false);
        connection.updateVariable(id, BROWSER_HEIGHT_VAR, windowHeight,
                immediate);
    }

    public native static void goTo(String url)
    /*-{
       $wnd.location = url;
     }-*/;

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

    public ShortcutActionHandler getShortcutActionHandler() {
        return actionHandler;
    }

    public void focus() {
        getElement().focus();
    }

    /**
     * Ensures the root is scrollable eg. after style name changes.
     */
    void makeScrollable() {
        if (touchScrollHandler == null) {
            touchScrollHandler = TouchScrollDelegate.enableTouchScrolling(this);
        }
        touchScrollHandler.addElement(getElement());
    }
}
