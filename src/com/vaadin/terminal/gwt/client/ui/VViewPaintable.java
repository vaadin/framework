package com.vaadin.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public class VViewPaintable extends VAbstractPaintableWidgetContainer {

    private static final String CLICK_EVENT_IDENTIFIER = VPanelPaintable.CLICK_EVENT_IDENTIFIER;

    public void updateFromUIDL(final UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().rendering = true;

        getWidgetForPaintable().id = uidl.getId();
        boolean firstPaint = getWidgetForPaintable().connection == null;
        getWidgetForPaintable().connection = client;

        getWidgetForPaintable().immediate = uidl.hasAttribute("immediate");
        getWidgetForPaintable().resizeLazy = uidl
                .hasAttribute(VView.RESIZE_LAZY);
        String newTheme = uidl.getStringAttribute("theme");
        if (getWidgetForPaintable().theme != null
                && !newTheme.equals(getWidgetForPaintable().theme)) {
            // Complete page refresh is needed due css can affect layout
            // calculations etc
            getWidgetForPaintable().reloadHostPage();
        } else {
            getWidgetForPaintable().theme = newTheme;
        }
        if (uidl.hasAttribute("style")) {
            getWidgetForPaintable().setStyleName(
                    getWidgetForPaintable().getStylePrimaryName() + " "
                            + uidl.getStringAttribute("style"));
        }

        clickEventHandler.handleEventHandlerRegistration(client);

        if (!getWidgetForPaintable().isEmbedded()
                && uidl.hasAttribute("caption")) {
            // only change window title if we're in charge of the whole page
            com.google.gwt.user.client.Window.setTitle(uidl
                    .getStringAttribute("caption"));
        }

        // Process children
        int childIndex = 0;

        // Open URL:s
        boolean isClosed = false; // was this window closed?
        while (childIndex < uidl.getChildCount()
                && "open".equals(uidl.getChildUIDL(childIndex).getTag())) {
            final UIDL open = uidl.getChildUIDL(childIndex);
            final String url = client.translateVaadinUri(open
                    .getStringAttribute("src"));
            final String target = open.getStringAttribute("name");
            if (target == null) {
                // source will be opened to this browser window, but we may have
                // to finish rendering this window in case this is a download
                // (and window stays open).
                Scheduler.get().scheduleDeferred(new Command() {
                    public void execute() {
                        VView.goTo(url);
                    }
                });
            } else if ("_self".equals(target)) {
                // This window is closing (for sure). Only other opens are
                // relevant in this change. See #3558, #2144
                isClosed = true;
                VView.goTo(url);
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
            // don't render the content, something else will be opened to this
            // browser view
            getWidgetForPaintable().rendering = false;
            return;
        }

        // Draw this application level window
        UIDL childUidl = uidl.getChildUIDL(childIndex);
        final VPaintableWidget lo = client.getPaintable(childUidl);

        if (getWidgetForPaintable().layout != null) {
            if (getWidgetForPaintable().layout != lo) {
                // remove old
                client.unregisterPaintable(getWidgetForPaintable().layout);
                // add new
                getWidgetForPaintable().setWidget(lo.getWidgetForPaintable());
                getWidgetForPaintable().layout = lo;
            }
        } else {
            getWidgetForPaintable().setWidget(lo.getWidgetForPaintable());
            getWidgetForPaintable().layout = lo;
        }

        getWidgetForPaintable().layout.updateFromUIDL(childUidl, client);
        if (!childUidl.getBooleanAttribute("cached")) {
            getWidgetForPaintable().updateParentFrameSize();
        }

        // Save currently open subwindows to track which will need to be closed
        final HashSet<VWindow> removedSubWindows = new HashSet<VWindow>(
                getWidgetForPaintable().subWindows);

        // Handle other UIDL children
        while ((childUidl = uidl.getChildUIDL(++childIndex)) != null) {
            String tag = childUidl.getTag().intern();
            if (tag == "actions") {
                if (getWidgetForPaintable().actionHandler == null) {
                    getWidgetForPaintable().actionHandler = new ShortcutActionHandler(
                            getWidgetForPaintable().id, client);
                }
                getWidgetForPaintable().actionHandler
                        .updateActionMap(childUidl);
            } else if (tag == "execJS") {
                String script = childUidl.getStringAttribute("script");
                VView.eval(script);
            } else if (tag == "notifications") {
                for (final Iterator<?> it = childUidl.getChildIterator(); it
                        .hasNext();) {
                    final UIDL notification = (UIDL) it.next();
                    VNotification.showNotification(client, notification);
                }
            } else {
                // subwindows
                final VPaintableWidget w = client.getPaintable(childUidl);
                if (getWidgetForPaintable().subWindows.contains(w)) {
                    removedSubWindows.remove(w);
                } else {
                    getWidgetForPaintable().subWindows.add((VWindow) w);
                }
                w.updateFromUIDL(childUidl, client);
            }
        }

        // Close old windows which where not in UIDL anymore
        for (final Iterator<VWindow> rem = removedSubWindows.iterator(); rem
                .hasNext();) {
            final VWindow w = rem.next();
            client.unregisterPaintable(VPaintableMap.get(getConnection())
                    .getPaintable(w));
            getWidgetForPaintable().subWindows.remove(w);
            w.hide();
        }

        if (uidl.hasAttribute("focused")) {
            // set focused component when render phase is finished
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    VPaintableWidget paintable = (VPaintableWidget) uidl
                            .getPaintableAttribute("focused", getConnection());

                    final Widget toBeFocused = paintable
                            .getWidgetForPaintable();
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
                        VConsole.log("Could not focus component");
                    }
                }
            });
        }

        // Add window listeners on first paint, to prevent premature
        // variablechanges
        if (firstPaint) {
            Window.addWindowClosingHandler(getWidgetForPaintable());
            Window.addResizeHandler(getWidgetForPaintable());
        }

        getWidgetForPaintable().onResize();

        // finally set scroll position from UIDL
        if (uidl.hasVariable("scrollTop")) {
            getWidgetForPaintable().scrollable = true;
            getWidgetForPaintable().scrollTop = uidl
                    .getIntVariable("scrollTop");
            DOM.setElementPropertyInt(getWidgetForPaintable().getElement(),
                    "scrollTop", getWidgetForPaintable().scrollTop);
            getWidgetForPaintable().scrollLeft = uidl
                    .getIntVariable("scrollLeft");
            DOM.setElementPropertyInt(getWidgetForPaintable().getElement(),
                    "scrollLeft", getWidgetForPaintable().scrollLeft);
        } else {
            getWidgetForPaintable().scrollable = false;
        }

        // Safari workaround must be run after scrollTop is updated as it sets
        // scrollTop using a deferred command.
        if (BrowserInfo.get().isSafari()) {
            Util.runWebkitOverflowAutoFix(getWidgetForPaintable().getElement());
        }

        getWidgetForPaintable().scrollIntoView(uidl);

        if (uidl.hasAttribute(VView.FRAGMENT_VARIABLE)) {
            getWidgetForPaintable().currentFragment = uidl
                    .getStringAttribute(VView.FRAGMENT_VARIABLE);
            if (!getWidgetForPaintable().currentFragment.equals(History
                    .getToken())) {
                History.newItem(getWidgetForPaintable().currentFragment, true);
            }
        } else {
            // Initial request for which the server doesn't yet have a fragment
            // (and haven't shown any interest in getting one)
            getWidgetForPaintable().currentFragment = History.getToken();

            // Include current fragment in the next request
            client.updateVariable(getWidgetForPaintable().id,
                    VView.FRAGMENT_VARIABLE,
                    getWidgetForPaintable().currentFragment, false);
        }

        getWidgetForPaintable().rendering = false;
    }

    public void init(String rootPanelId,
            ApplicationConnection applicationConnection) {
        DOM.sinkEvents(getWidgetForPaintable().getElement(), Event.ONKEYDOWN
                | Event.ONSCROLL);

        // iview is focused when created so element needs tabIndex
        // 1 due 0 is at the end of natural tabbing order
        DOM.setElementProperty(getWidgetForPaintable().getElement(),
                "tabIndex", "1");

        RootPanel root = RootPanel.get(rootPanelId);

        // Remove the v-app-loading or any splash screen added inside the div by
        // the user
        root.getElement().setInnerHTML("");
        // For backwards compatibility with static index pages only.
        // No longer added by AbstractApplicationServlet/Portlet
        root.removeStyleName("v-app-loading");

        String themeUri = applicationConnection.getConfiguration()
                .getThemeUri();
        String themeName = themeUri.substring(themeUri.lastIndexOf('/'));
        themeName = themeName.replaceAll("[^a-zA-Z0-9]", "");
        root.addStyleName("v-theme-" + themeName);

        root.add(getWidgetForPaintable());

        if (applicationConnection.getConfiguration().isStandalone()) {
            // set focus to iview element by default to listen possible keyboard
            // shortcuts. For embedded applications this is unacceptable as we
            // don't want to steal focus from the main page nor we don't want
            // side-effects from focusing (scrollIntoView).
            getWidgetForPaintable().getElement().focus();
        }

        getWidgetForPaintable().parentFrame = VView.getParentFrame();
    }

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this,
            CLICK_EVENT_IDENTIFIER) {

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidgetForPaintable().addDomHandler(handler, type);
        }
    };

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        // NOP The main view never draws caption for its layout
    }

    @Override
    public VView getWidgetForPaintable() {
        return (VView) super.getWidgetForPaintable();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VView.class);
    }

}
