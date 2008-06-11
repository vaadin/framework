/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

import java.util.Iterator;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class Util {

    /**
     * Helper method for debugging purposes.
     * 
     * Stops execution on firefox browsers on a breakpoint.
     * 
     */
    public static native void browserDebugger()
    /*-{
        if(window.console)
            debugger;
    }-*/;

    /**
     * Nulls oncontextmenu function on given element. We need to manually clear
     * context menu events due bad browsers memory leaks, since GWT don't
     * support them.
     * 
     * @param el
     */
    public native static void removeContextMenuEvent(Element el)
    /*-{
      	el.oncontextmenu = null;
    }-*/;

    /**
     * Traverses recursively ancestors until ContainerResizedListener child
     * widget is found. They will delegate it futher if needed.
     * 
     * @param container
     */
    public static void runDescendentsLayout(HasWidgets container) {
        final Iterator childWidgets = container.iterator();
        while (childWidgets.hasNext()) {
            final Widget child = (Widget) childWidgets.next();
            if (child instanceof ContainerResizedListener) {
                ((ContainerResizedListener) child).iLayout();
            } else if (child instanceof HasWidgets) {
                final HasWidgets childContainer = (HasWidgets) child;
                runDescendentsLayout(childContainer);
            }
        }
    }

    /**
     * Returns closest parent Widget in hierarchy that implements Container
     * interface
     * 
     * @param component
     * @return closest parent Container
     */
    public static Container getParentLayout(Widget component) {
        Widget parent = component.getParent();
        while (parent != null && !(parent instanceof Container)) {
            parent = parent.getParent();
        }
        if (parent != null && ((Container) parent).hasChildComponent(component)) {
            return (Container) parent;
        }
        return null;
    }

    /**
     * Detects if current browser is IE.
     * 
     * @deprecated use BrowserInfo class instead
     * 
     * @return true if IE
     */
    public static boolean isIE() {
        return BrowserInfo.get().isIE();
    }

    /**
     * Detects if current browser is IE6.
     * 
     * @deprecated use BrowserInfo class instead
     * 
     * @return true if IE6
     */
    public static boolean isIE6() {
        return BrowserInfo.get().isIE6();
    }

    /**
     * @deprecated use BrowserInfo class instead
     * @return
     */
    public static boolean isIE7() {
        return BrowserInfo.get().isIE7();
    }

    /**
     * @deprecated use BrowserInfo class instead
     * @return
     */
    public static boolean isFF2() {
        return BrowserInfo.get().isFF2();
    }

    private static final Element escapeHtmlHelper = DOM.createDiv();

    /**
     * Converts html entities to text.
     * 
     * @param html
     * @return escaped string presentation of given html
     */
    public static String escapeHTML(String html) {
        DOM.setInnerText(escapeHtmlHelper, html);
        return DOM.getInnerHTML(escapeHtmlHelper);
    }
}
