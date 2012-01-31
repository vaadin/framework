/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler.ShortcutActionHandlerOwner;

/**
 *
 */
public class VView extends SimplePanel implements Container, ResizeHandler,
        Window.ClosingHandler, ShortcutActionHandlerOwner, Focusable {

    public static final String FRAGMENT_VARIABLE = "fragment";

    private static final String CLASSNAME = "v-view";

    public static final String NOTIFICATION_HTML_CONTENT_NOT_ALLOWED = "useplain";

    String theme;

    VPaintableWidget layout;

    final LinkedHashSet<VWindow> subWindows = new LinkedHashSet<VWindow>();

    String id;

    ShortcutActionHandler actionHandler;

    /** stored width for IE resize optimization */
    private int width;

    /** stored height for IE resize optimization */
    private int height;

    ApplicationConnection connection;

    /** Identifies the click event */
    public static final String CLICK_EVENT_ID = "click";

    /**
     * We are postponing resize process with IE. IE bugs with scrollbars in some
     * situations, that causes false onWindowResized calls. With Timer we will
     * give IE some time to decide if it really wants to keep current size
     * (scrollbars).
     */
    private Timer resizeTimer;

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

    /**
     * Reference to the parent frame/iframe. Null if there is no parent (i)frame
     * or if the application and parent frame are in different domains.
     */
    Element parentFrame;

    private HandlerRegistration historyHandlerRegistration;

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
                    windowSizeMaybeChanged(Window.getClientWidth(),
                            Window.getClientHeight());
                }

            });

    public VView() {
        super();
        setStyleName(CLASSNAME);

        // Allow focusing the view by using the focus() method, the view
        // should not be in the document focus flow
        getElement().setTabIndex(-1);
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
     * Called when the window might have been resized.
     * 
     * @param newWidth
     *            The new width of the window
     * @param newHeight
     *            The new height of the window
     */
    protected void windowSizeMaybeChanged(int newWidth, int newHeight) {
        boolean changed = false;
        if (width != newWidth) {
            width = newWidth;
            changed = true;
            VConsole.log("New window width: " + width);
        }
        if (height != newHeight) {
            height = newHeight;
            changed = true;
            VConsole.log("New window height: " + height);
        }
        if (changed) {
            VConsole.log("Running layout functions due to window resize");
            connection.runDescendentsLayout(VView.this);
            Util.runWebkitOverflowAutoFix(getElement());

            sendClientResized();
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
     * Evaluate the given script in the browser document.
     * 
     * @param script
     *            Script to be executed.
     */
    static native void eval(String script)
    /*-{
      try {
         if (script == null) return;
         $wnd.eval(script);
      } catch (e) {
      }
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
     * Tries to scroll paintable referenced from given UIDL snippet to be
     * visible.
     * 
     * @param uidl
     */
    void scrollIntoView(final UIDL uidl) {
        if (uidl.hasAttribute("scrollTo")) {
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    final VPaintableWidget paintable = (VPaintableWidget) uidl
                            .getPaintableAttribute("scrollTo", connection);
                    paintable.getWidgetForPaintable().getElement()
                            .scrollIntoView();
                }
            });
        }
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
        onResize();
    }

    /**
     * Called when a resize event is received.
     */
    void onResize() {
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
            windowSizeMaybeChanged(Window.getClientWidth(),
                    Window.getClientHeight());
        }
    }

    /**
     * Send new dimensions to the server.
     */
    private void sendClientResized() {
        connection.updateVariable(id, "height", height, false);
        connection.updateVariable(id, "width", width, immediate);
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

    private final RenderSpace myRenderSpace = new RenderSpace() {
        private int excessHeight = -1;
        private int excessWidth = -1;

        @Override
        public int getHeight() {
            return getElement().getOffsetHeight() - getExcessHeight();
        }

        private int getExcessHeight() {
            if (excessHeight < 0) {
                detectExcessSize();
            }
            return excessHeight;
        }

        private void detectExcessSize() {
            // TODO define that iview cannot be themed and decorations should
            // get to parent element, then get rid of this expensive and error
            // prone function
            final String overflow = getElement().getStyle().getProperty(
                    "overflow");
            getElement().getStyle().setProperty("overflow", "hidden");
            if (BrowserInfo.get().isIE()
                    && getElement().getPropertyInt("clientWidth") == 0) {
                // can't detect possibly themed border/padding width in some
                // situations (with some layout configurations), use empty div
                // to measure width properly
                DivElement div = Document.get().createDivElement();
                div.setInnerHTML("&nbsp;");
                div.getStyle().setProperty("overflow", "hidden");
                div.getStyle().setProperty("height", "1px");
                getElement().appendChild(div);
                excessWidth = getElement().getOffsetWidth()
                        - div.getOffsetWidth();
                getElement().removeChild(div);
            } else {
                excessWidth = getElement().getOffsetWidth()
                        - getElement().getPropertyInt("clientWidth");
            }
            excessHeight = getElement().getOffsetHeight()
                    - getElement().getPropertyInt("clientHeight");

            getElement().getStyle().setProperty("overflow", overflow);
        }

        @Override
        public int getWidth() {
            if (connection.getConfiguration().isStandalone()) {
                return getElement().getOffsetWidth() - getExcessWidth();
            }

            // If not running standalone, there might be multiple Vaadin apps
            // that won't shrink with the browser window as the components have
            // calculated widths (#3125)

            // Find all Vaadin applications on the page
            ArrayList<String> vaadinApps = new ArrayList<String>();
            loadAppIdListFromDOM(vaadinApps);

            // Store original styles here so they can be restored
            ArrayList<String> originalDisplays = new ArrayList<String>(
                    vaadinApps.size());

            String ownAppId = connection.getConfiguration().getRootPanelId();

            // Hiding elements causes browser to forget scroll position -> must
            // save values and restore when the elements are visible again #7976
            int originalScrollTop = Window.getScrollTop();
            int originalScrollLeft = Window.getScrollLeft();

            // Set display: none for all Vaadin apps
            for (int i = 0; i < vaadinApps.size(); i++) {
                String appId = vaadinApps.get(i);
                Element targetElement;
                if (appId.equals(ownAppId)) {
                    // Only hide the contents of current application
                    targetElement = layout.getWidgetForPaintable().getElement();
                } else {
                    // Hide everything for other applications
                    targetElement = Document.get().getElementById(appId);
                }
                Style layoutStyle = targetElement.getStyle();

                originalDisplays.add(i, layoutStyle.getDisplay());
                layoutStyle.setDisplay(Display.NONE);
            }

            int w = getElement().getOffsetWidth() - getExcessWidth();

            // Then restore the old display style before returning
            for (int i = 0; i < vaadinApps.size(); i++) {
                String appId = vaadinApps.get(i);
                Element targetElement;
                if (appId.equals(ownAppId)) {
                    targetElement = layout.getWidgetForPaintable().getElement();
                } else {
                    targetElement = Document.get().getElementById(appId);
                }
                Style layoutStyle = targetElement.getStyle();
                String originalDisplay = originalDisplays.get(i);

                if (originalDisplay.length() == 0) {
                    layoutStyle.clearDisplay();
                } else {
                    layoutStyle.setProperty("display", originalDisplay);
                }
            }

            // Scroll back to original location
            Window.scrollTo(originalScrollLeft, originalScrollTop);

            return w;
        }

        private int getExcessWidth() {
            if (excessWidth < 0) {
                detectExcessSize();
            }
            return excessWidth;
        }

        @Override
        public int getScrollbarSize() {
            return Util.getNativeScrollbarSize();
        }
    };

    private native static void loadAppIdListFromDOM(ArrayList<String> list)
    /*-{
         var j;
         for(j in $wnd.vaadin.vaadinConfigurations) {
            list.@java.util.Collection::add(Ljava/lang/Object;)(j);
         }
     }-*/;

    public RenderSpace getAllocatedSpace(Widget child) {
        return myRenderSpace;
    }

    public boolean hasChildComponent(Widget component) {
        return (component != null && component == layout);
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        // TODO This is untested as no layouts require this
        if (oldComponent != layout) {
            return;
        }

        setWidget(newComponent);
        layout = (VPaintableWidget) newComponent;
    }

    public boolean requestLayout(Set<Widget> children) {
        /*
         * Can never propagate further and we do not want need to re-layout the
         * layout which has caused this request.
         */
        updateParentFrameSize();

        // layout size change may affect its available space (scrollbars)
        connection.handleComponentRelativeSize(layout.getWidgetForPaintable());

        return true;

    }

    void updateParentFrameSize() {
        if (parentFrame == null) {
            return;
        }

        int childHeight = Util.getRequiredHeight(getWidget().getElement());
        int childWidth = Util.getRequiredWidth(getWidget().getElement());

        parentFrame.getStyle().setPropertyPx("width", childWidth);
        parentFrame.getStyle().setPropertyPx("height", childHeight);
    }

    static native Element getParentFrame()
    /*-{
        try {
            var frameElement = $wnd.frameElement;
            if (frameElement == null) {
                return null;
            }
            if (frameElement.getAttribute("autoResize") == "true") {
                return frameElement;
            }
        } catch (e) {
        }
        return null;
    }-*/;

    /**
     * Return an iterator for current subwindows. This method is meant for
     * testing purposes only.
     * 
     * @return
     */
    public ArrayList<VWindow> getSubWindowList() {
        ArrayList<VWindow> windows = new ArrayList<VWindow>(subWindows.size());
        for (VWindow widget : subWindows) {
            windows.add(widget);
        }
        return windows;
    }

    public ShortcutActionHandler getShortcutActionHandler() {
        return actionHandler;
    }

    public void focus() {
        getElement().focus();
    }

    public Widget getWidgetForPaintable() {
        return this;
    }

}
