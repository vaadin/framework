/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ui.IWindow;

public final class IDebugConsole extends IWindow implements Console,
        EventListener {

    /**
     * Builds number. For example 0-custom_tag in 5.0.0-custom_tag.
     */
    public static final String VERSION;

    /* Initialize version numbers from string replaced by build-script. */
    static {
        if ("@VERSION@".equals("@" + "VERSION" + "@")) {
            VERSION = "5.9.9-INTERNAL-NONVERSIONED-DEBUG-BUILD";
        } else {
            VERSION = "@VERSION@";
        }
    }

    private final Panel panel;
    private com.google.gwt.dom.client.Element restartApplicationElement;
    private Element clearButtonElement;

    public IDebugConsole(ApplicationConnection client,
            ApplicationConfiguration cnf, boolean showWindow) {
        super();

        this.client = client;
        panel = new FlowPanel();

        Element buttonDiv = DOM.createDiv();
        getContainerElement().appendChild(buttonDiv);

        final ScrollPanel p = new ScrollPanel(panel);

        Button clearButton = new Button("Clear");
        clearButtonElement = clearButton.getElement();

        Button restartApplicationButton = new Button("Restart application");
        restartApplicationElement = restartApplicationButton.getElement();
        buttonDiv.appendChild(clearButtonElement);
        buttonDiv.appendChild(restartApplicationElement);
        DOM.sinkEvents(clearButton.getElement(), Event.ONCLICK);

        setWidget(p);
        setCaption("Debug window");

        setPixelSize(400, 300);
        setPopupPosition(Window.getClientWidth() - 400 - 20, 0);

        if (showWindow) {
            show();
        }

        ;

        log("Toolkit application servlet version: " + cnf.getSerletVersion());
        log("Widget set is built on version: " + VERSION);
        log("Application version: " + cnf.getApplicationVersion());

        if (!cnf.getSerletVersion().equals(VERSION)) {
            error("Warning: your widget set seems to be built with different "
                    + "version than the one used on server. Unexpected "
                    + "behavior may occur.");
        }
    }

    private void minimize() {
        // TODO stack to bottom (create window manager of some sort)
        setPixelSize(60, 60);
        setPopupPosition(Window.getClientWidth() - 142, 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.gwt.client.Console#log(java.lang.String)
     */
    public void log(String msg) {
        panel.add(new HTML(msg));
        System.out.println(msg);
        logFirebug(msg);
    }

    private static native void logFirebug(String msg)
    /*-{
    if (typeof(console) != "undefined") {
      console.log(msg);
    }
    }-*/;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itmill.toolkit.terminal.gwt.client.Console#error(java.lang.String)
     */
    public void error(String msg) {
        panel.add((new HTML(msg)));
        System.out.println(msg);
        logFirebug(msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itmill.toolkit.terminal.gwt.client.Console#printObject(java.lang.
     * Object)
     */
    public void printObject(Object msg) {
        panel.add((new Label(msg.toString())));
        logFirebug(msg.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itmill.toolkit.terminal.gwt.client.Console#dirUIDL(com.itmill.toolkit
     * .terminal.gwt.client.UIDL)
     */
    public void dirUIDL(UIDL u) {
        panel.add(u.print_r());
    }

    public void setSize(Event event, boolean updateVariables) {
        super.setSize(event, false);
    }

    public void onScroll(Widget widget, int scrollLeft, int scrollTop) {

    }

    public void setPopupPosition(int left, int top) {
        // Keep the popup within the browser's client area, so that they can't
        // get
        // 'lost' and become impossible to interact with. Note that we don't
        // attempt
        // to keep popups pegged to the bottom and right edges, as they will
        // then
        // cause scrollbars to appear, so the user can't lose them.
        if (left < 0) {
            left = 0;
        }
        if (top < 0) {
            top = 0;
        }

        // Set the popup's position manually, allowing setPopupPosition() to be
        // called before show() is called (so a popup can be positioned without
        // it
        // 'jumping' on the screen).
        Element elem = getElement();
        DOM.setStyleAttribute(elem, "left", left + "px");
        DOM.setStyleAttribute(elem, "top", top + "px");
    }

    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);

        final int type = DOM.eventGetType(event);
        if (type == Event.BUTTON_LEFT) {

            if (event.getTarget() == restartApplicationElement) {
                String href = Window.Location.getHref();
                if (Window.Location.getParameter("restartApplication") == null) {
                    if (href.contains("?")) {
                        href += "&restartApplication";
                    } else {
                        href += "?restartApplication";
                    }

                    Window.Location.replace(href);
                } else {
                    Window.Location.replace(href);
                }
            } else if (event.getTarget() == clearButtonElement) {
                panel.clear();
            }
        }
    }
}
