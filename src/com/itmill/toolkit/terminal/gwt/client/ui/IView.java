/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

/**
 * 
 */
public class IView extends SimplePanel implements Paintable,
        WindowResizeListener {

    private static final String CLASSNAME = "i-view";

    private String theme;

    private Paintable layout;

    private final HashSet subWindows = new HashSet();

    private String id;

    private ShortcutActionHandler actionHandler;

    public IView(String elementId) {
        super();
        setStyleName(CLASSNAME);
        DOM.sinkEvents(getElement(), Event.ONKEYDOWN);

        RootPanel.get(elementId).add(this);

        Window.addWindowResizeListener(this);
    }

    public String getTheme() {
        return theme;
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        id = uidl.getId();

        // Some attributes to note
        theme = uidl.getStringAttribute("theme");
        com.google.gwt.user.client.Window.setTitle(uidl
                .getStringAttribute("caption"));

        // Process children
        int childIndex = 0;

        // Open URL:s
        while (childIndex < uidl.getChildCount()
                && "open".equals(uidl.getChildUIDL(childIndex).getTag())) {
            final UIDL open = uidl.getChildUIDL(childIndex);
            final String url = open.getStringAttribute("src");
            final String target = open.getStringAttribute("name");
            if (target == null) {
                goTo(url);
            } else {
                // TODO width & height
                Window.open(url, target != null ? target : null, "");
            }
            childIndex++;
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

        // Update subwindows
        final HashSet removedSubWindows = new HashSet(subWindows);

        // Open new windows
        while ((childUidl = uidl.getChildUIDL(childIndex++)) != null) {
            if ("window".equals(childUidl.getTag())) {
                final Paintable w = client.getPaintable(childUidl);
                if (subWindows.contains(w)) {
                    removedSubWindows.remove(w);
                } else {
                    subWindows.add(w);
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
                                .translateToolkitUri(notification
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
                    new Notification(delay).show(html, position, style);
                }
            }
        }

        // Close old windows
        for (final Iterator rem = removedSubWindows.iterator(); rem.hasNext();) {
            final IWindow w = (IWindow) rem.next();
            client.unregisterPaintable(w);
            subWindows.remove(w);
            w.hide();
        }

        if (true) {
            // IE somehow fails some layout on first run, force layout
            // functions
            Util.runDescendentsLayout(this);
        }

    }

    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (DOM.eventGetType(event) == Event.ONKEYDOWN && actionHandler != null) {
            actionHandler.handleKeyboardEvent(event);
            return;
        }
    }

    public void onWindowResized(int width, int height) {
        Util.runDescendentsLayout(this);
    }

    public native static void goTo(String url)
    /*-{
       $wnd.location = url;
     }-*/;

}
