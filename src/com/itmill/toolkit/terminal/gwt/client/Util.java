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

    /**
     * Adds transparent PNG fix to image element; only use for IE6.
     * 
     * @param el
     *                IMG element
     * @param blankImageUrl
     *                URL to transparent one-pixel gif
     */
    public native static void addPngFix(Element el, String blankImageUrl)
    /*-{
        el.attachEvent("onload", function() {
            var src = el.src;
            if (src.indexOf(".png")<1) return;
            var w = el.width||16; 
            var h = el.height||16;
            el.src =blankImageUrl;
            el.style.height = h+"px";
            el.style.width = w+"px";
            el.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+src+"', sizingMethod='crop');";  
        },false);
    }-*/;

    /**
     * Clones given element as in JavaScript.
     * 
     * Deprecate this if there appears similar method into GWT someday.
     * 
     * @param element
     * @param deep
     *                clone child tree also
     * @return
     */
    public static native Element cloneNode(Element element, boolean deep)
    /*-{
        return element.cloneNode(deep);
    }-*/;
}
