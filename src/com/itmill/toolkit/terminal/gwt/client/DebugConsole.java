/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ui.IWindow;

public final class DebugConsole extends IWindow implements Console {

    private final Panel panel;

    public DebugConsole(ApplicationConnection client) {
        super();
        this.client = client;
        panel = new FlowPanel();
        final ScrollPanel p = new ScrollPanel();
        p.add(panel);
        setWidget(p);
        setCaption("Debug window");
        minimize();
        show();
    }

    private void minimize() {
        // TODO stack to bottom (create window manager of some sort)
        setPixelSize(60, 60);
        setPopupPosition(Window.getClientWidth()
                - (60 + IWindow.BORDER_WIDTH_HORIZONTAL), Window
                .getClientHeight()
                - (80 + IWindow.BORDER_WIDTH_VERTICAL));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.gwt.client.Console#log(java.lang.String)
     */
    public void log(String msg) {
        panel.add(new HTML(msg));
        System.out.println(msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.gwt.client.Console#error(java.lang.String)
     */
    public void error(String msg) {
        panel.add((new HTML(msg)));
        System.out.println(msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.gwt.client.Console#printObject(java.lang.Object)
     */
    public void printObject(Object msg) {
        panel.add((new Label(msg.toString())));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.gwt.client.Console#dirUIDL(com.itmill.toolkit.terminal.gwt.client.UIDL)
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

}
