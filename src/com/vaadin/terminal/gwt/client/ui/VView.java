/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;

/**
 *
 */
public class VView extends SimplePanel implements Container,
        WindowResizeListener, WindowCloseListener {

    private static final String CLASSNAME = "v-view";

    private String theme;

    private Paintable layout;

    private final LinkedHashSet<VWindow> subWindows = new LinkedHashSet<VWindow>();

    private String id;

    private ShortcutActionHandler actionHandler;

    /** stored width for IE resize optimization */
    private int width;

    /** stored height for IE resize optimization */
    private int height;

    private ApplicationConnection connection;

    /**
     * We are postponing resize process with IE. IE bugs with scrollbars in some
     * situations, that causes false onWindowResized calls. With Timer we will
     * give IE some time to decide if it really wants to keep current size
     * (scrollbars).
     */
    private Timer resizeTimer;

    private int scrollTop;

    private int scrollLeft;

    private boolean rendering;

    private boolean scrollable;

    private boolean immediate;

    /**
     * Reference to the parent frame/iframe. Null if there is no parent (i)frame
     * or if the application and parent frame are in different domains.
     */
    private Element parentFrame;

    public VView(String elementId) {
        super();
        setStyleName(CLASSNAME);

        DOM.sinkEvents(getElement(), Event.ONKEYDOWN | Event.ONSCROLL);

        // iview is focused when created so element needs tabIndex
        // 1 due 0 is at the end of natural tabbing order
        DOM.setElementProperty(getElement(), "tabIndex", "1");

        RootPanel root = RootPanel.get(elementId);
        root.add(this);
        root.removeStyleName("v-app-loading");

        BrowserInfo browser = BrowserInfo.get();

        // set focus to iview element by default to listen possible keyboard
        // shortcuts
        if (browser.isOpera() || browser.isSafari()
                && browser.getWebkitVersion() < 526) {
            // old webkits don't support focusing div elements
            Element fElem = DOM.createInputCheck();
            DOM.setStyleAttribute(fElem, "margin", "0");
            DOM.setStyleAttribute(fElem, "padding", "0");
            DOM.setStyleAttribute(fElem, "border", "0");
            DOM.setStyleAttribute(fElem, "outline", "0");
            DOM.setStyleAttribute(fElem, "width", "1px");
            DOM.setStyleAttribute(fElem, "height", "1px");
            DOM.setStyleAttribute(fElem, "position", "absolute");
            DOM.setStyleAttribute(fElem, "opacity", "0.1");
            DOM.appendChild(getElement(), fElem);
            focus(fElem);
        } else {
            focus(getElement());
        }

        parentFrame = getParentFrame();
    }

    private static native void focus(Element el)
    /*-{
        try {
            el.focus();
        } catch (e) {

        }
    }-*/;

    public String getTheme() {
        return theme;
    }

    /**
     * Used to reload host page on theme changes.
     */
    private static native void reloadHostPage()
    /*-{
         $wnd.location.reload();
     }-*/;

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        rendering = true;

        id = uidl.getId();
        boolean firstPaint = connection == null;
        connection = client;

        immediate = uidl.hasAttribute("immediate");

        String newTheme = uidl.getStringAttribute("theme");
        if (theme != null && !newTheme.equals(theme)) {
            // Complete page refresh is needed due css can affect layout
            // calculations etc
            reloadHostPage();
        } else {
            theme = newTheme;
        }
        if (uidl.hasAttribute("style")) {
            setStyleName(getStylePrimaryName() + " "
                    + uidl.getStringAttribute("style"));
        }

        if (uidl.hasAttribute("name")) {
            client.setWindowName(uidl.getStringAttribute("name"));
        }

        com.google.gwt.user.client.Window.setTitle(uidl
                .getStringAttribute("caption"));

        // Process children
        int childIndex = 0;

        // Open URL:s
        boolean isClosed = false; // was this window closed?
        while (childIndex < uidl.getChildCount()
                && "open".equals(uidl.getChildUIDL(childIndex).getTag())) {
            final UIDL open = uidl.getChildUIDL(childIndex);
            final String url = open.getStringAttribute("src");
            final String target = open.getStringAttribute("name");
            if (target == null) {
                // This window is closing. Send close event before
                // going to the new url
                isClosed = true;
                onWindowClosed();
                goTo(url);
            } else {
                String options;
                if (open.hasAttribute("border")) {
                    if (open.getStringAttribute("border").equals("minimal")) {
                        options = "menubar=yes,location=no,status=no";
                    } else {
                        options = "menubar=no,location=no,status=no";
                    }

                } else {
                    options = "resizable=yes,menubar=yes,toolbar=yes,directories=yes,location=yes,scrollbars=yes,status=yes";
                }

                if (open.hasAttribute("width")) {
                    int w = open.getIntAttribute("width");
                    options += ",width=" + w;
                }
                if (open.hasAttribute("height")) {
                    int h = open.getIntAttribute("height");
                    options += ",height=" + h;
                }

                Window.open(url, target, options);
            }
            childIndex++;
        }
        if (isClosed) {
            // don't render the content
            rendering = false;
            return;
        }

        // Draw this application level window
        UIDL childUidl = uidl.getChildUIDL(childIndex);
        final Paintable lo = client.getPaintable(childUidl);

        if (layout != null) {
            if (layout != lo) {
                // remove old
                client.unregisterPaintable(layout);
                // add new
                setWidget((Widget) lo);
                layout = lo;
            }
        } else {
            setWidget((Widget) lo);
            layout = lo;
        }

        layout.updateFromUIDL(childUidl, client);
        if (!childUidl.getBooleanAttribute("cached")) {
            updateParentFrameSize();
        }

        // Update subwindows
        final HashSet<VWindow> removedSubWindows = new HashSet<VWindow>(
                subWindows);

        // Open new windows
        while ((childUidl = uidl.getChildUIDL(childIndex++)) != null) {
            if ("window".equals(childUidl.getTag())) {
                final Paintable w = client.getPaintable(childUidl);
                if (subWindows.contains(w)) {
                    removedSubWindows.remove(w);
                } else {
                    subWindows.add((VWindow) w);
                }
                w.updateFromUIDL(childUidl, client);
            } else if ("actions".equals(childUidl.getTag())) {
                if (actionHandler == null) {
                    actionHandler = new ShortcutActionHandler(id, client);
                }
                actionHandler.updateActionMap(childUidl);
            } else if (childUidl.getTag().equals("notifications")) {
                for (final Iterator it = childUidl.getChildIterator(); it
                        .hasNext();) {
                    final UIDL notification = (UIDL) it.next();
                    String html = "";
                    if (notification.hasAttribute("icon")) {
                        final String parsedUri = client
                                .translateVaadinUri(notification
                                        .getStringAttribute("icon"));
                        html += "<IMG src=\"" + parsedUri + "\" />";
                    }
                    if (notification.hasAttribute("caption")) {
                        html += "<H1>"
                                + notification.getStringAttribute("caption")
                                + "</H1>";
                    }
                    if (notification.hasAttribute("message")) {
                        html += "<p>"
                                + notification.getStringAttribute("message")
                                + "</p>";
                    }

                    final String style = notification.hasAttribute("style") ? notification
                            .getStringAttribute("style")
                            : null;
                    final int position = notification
                            .getIntAttribute("position");
                    final int delay = notification.getIntAttribute("delay");
                    new VNotification(delay).show(html, position, style);
                }
            }
        }

        // Close old windows
        for (final Iterator<VWindow> rem = removedSubWindows.iterator(); rem
                .hasNext();) {
            final VWindow w = rem.next();
            client.unregisterPaintable(w);
            subWindows.remove(w);
            w.hide();
        }

        if (uidl.hasAttribute("focused")) {
            final String focusPid = uidl.getStringAttribute("focused");
            // set focused component when render phase is finished
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    final Paintable toBeFocused = connection
                            .getPaintable(focusPid);

                    /*
                     * Two types of Widgets can be focused, either implementing
                     * GWT HasFocus of a thinner Vaadin specific Focusable
                     * interface.
                     */
                    if (toBeFocused instanceof com.google.gwt.user.client.ui.Focusable) {
                        final com.google.gwt.user.client.ui.Focusable toBeFocusedWidget = (com.google.gwt.user.client.ui.Focusable) toBeFocused;
                        toBeFocusedWidget.setFocus(true);
                    } else if (toBeFocused instanceof Focusable) {
                        ((Focusable) toBeFocused).focus();
                    } else {
                        ApplicationConnection.getConsole().log(
                                "Could not focus component");
                    }
                }
            });
        }

        // Add window listeners on first paint, to prevent premature
        // variablechanges
        if (firstPaint) {
            Window.addWindowCloseListener(this);
            Window.addWindowResizeListener(this);
        }

        onWindowResized(Window.getClientWidth(), Window.getClientHeight());

        if (BrowserInfo.get().isSafari()) {
            Util.runWebkitOverflowAutoFix(getElement());
        }

        // finally set scroll position from UIDL
        if (uidl.hasVariable("scrollTop")) {
            scrollable = true;
            scrollTop = uidl.getIntVariable("scrollTop");
            DOM.setElementPropertyInt(getElement(), "scrollTop", scrollTop);
            scrollLeft = uidl.getIntVariable("scrollLeft");
            DOM.setElementPropertyInt(getElement(), "scrollLeft", scrollLeft);
        } else {
            scrollable = false;
        }

        rendering = false;
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

    public void onWindowResized(int width, int height) {
        if (BrowserInfo.get().isIE()) {
            /*
             * IE will give us some false resized events due bugs with
             * scrollbars. Postponing layout phase to see if size was really
             * changed.
             */
            if (resizeTimer == null) {
                resizeTimer = new Timer() {
                    @Override
                    public void run() {
                        boolean changed = false;
                        if (VView.this.width != getOffsetWidth()) {
                            VView.this.width = getOffsetWidth();
                            changed = true;
                            ApplicationConnection.getConsole().log(
                                    "window w" + VView.this.width);
                        }
                        if (VView.this.height != getOffsetHeight()) {
                            VView.this.height = getOffsetHeight();
                            changed = true;
                            ApplicationConnection.getConsole().log(
                                    "window h" + VView.this.height);
                        }
                        if (changed) {
                            ApplicationConnection
                                    .getConsole()
                                    .log(
                                            "Running layout functions due window resize");
                            connection.runDescendentsLayout(VView.this);

                            sendClientResized();
                        }
                    }
                };
            } else {
                resizeTimer.cancel();
            }
            resizeTimer.schedule(200);
        } else {
            if (width == VView.this.width && height == VView.this.height) {
                // No point in doing resize operations if window size has not
                // changed
                return;
            }

            VView.this.width = Window.getClientWidth();
            VView.this.height = Window.getClientHeight();

            ApplicationConnection.getConsole().log(
                    "Running layout functions due window resize");

            connection.runDescendentsLayout(this);
            Util.runWebkitOverflowAutoFix(getElement());

            sendClientResized();
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

    public void onWindowClosed() {

    }

    public String onWindowClosing() {
        // Change focus on this window in order to ensure that all state is
        // collected from textfields
        VTextField.flushChangesFromFocusedTextField();

        // Send the closing state to server
        connection.updateVariable(id, "close", true, false);
        connection.sendPendingVariableChangesSync();

        return null;
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
            return getElement().getOffsetWidth() - getExcessWidth();
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
        layout = (Paintable) newComponent;
    }

    public boolean requestLayout(Set<Paintable> child) {
        /*
         * Can never propagate further and we do not want need to re-layout the
         * layout which has caused this request.
         */
        updateParentFrameSize();

        return true;

    }

    private void updateParentFrameSize() {
        if (parentFrame == null) {
            return;
        }

        int childHeight = Util.getRequiredHeight(getWidget().getElement());
        int childWidth = Util.getRequiredWidth(getWidget().getElement());

        parentFrame.getStyle().setPropertyPx("width", childWidth);
        parentFrame.getStyle().setPropertyPx("height", childHeight);
    }

    private static native Element getParentFrame()
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

    public void updateCaption(Paintable component, UIDL uidl) {
        // NOP Subwindows never draw caption for their first child (layout)
    }

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

}
