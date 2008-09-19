/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
        if($wnd.console)
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
     * Called when the size of one or more widgets have changed during
     * rendering. Finds parent container and notifies them of the size change.
     * 
     * @param widgets
     */
    public static void componentSizeUpdated(Set<Widget> widgets) {
        if (widgets.isEmpty()) {
            return;
        }

        Set<Container> parents = new HashSet<Container>();

        for (Widget widget : widgets) {
            Widget parent = widget.getParent();
            while (parent != null && !(parent instanceof Container)) {
                parent = parent.getParent();
            }
            if (parent != null) {
                parents.add((Container) parent);
            }
        }

        Set<Widget> parentChanges = new HashSet<Widget>();
        for (Container parent : parents) {
            if (!parent.childComponentSizesUpdated()) {
                parentChanges.add((Widget) parent);
            }
        }

        componentSizeUpdated(parentChanges);
    }

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
                int w = -1, h = -1;

                if (container instanceof WidgetSpaceAllocator) {
                    w = ((WidgetSpaceAllocator) container)
                            .getAllocatedWidth(child);
                    h = ((WidgetSpaceAllocator) container)
                            .getAllocatedHeight(child);
                }

                ((ContainerResizedListener) child).iLayout(w, h);
            } else if (child instanceof HasWidgets) {
                final HasWidgets childContainer = (HasWidgets) child;
                runDescendentsLayout(childContainer);
            }
        }
    }

    public interface WidgetSpaceAllocator {
        int getAllocatedWidth(Widget child);

        int getAllocatedHeight(Widget child);
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
     *            IMG element
     * @param blankImageUrl
     *            URL to transparent one-pixel gif
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
     *            clone child tree also
     * @return
     */
    public static native Element cloneNode(Element element, boolean deep)
    /*-{
        return element.cloneNode(deep);
    }-*/;

    public static int measureHorizontalPadding(Element element, int paddingGuess) {
        String originalWidth = DOM.getStyleAttribute(element, "width");
        int originalOffsetWidth = element.getOffsetWidth();
        int widthGuess = (originalOffsetWidth + paddingGuess);
        DOM.setStyleAttribute(element, "width", widthGuess + "px");
        int padding = widthGuess - element.getOffsetWidth();

        DOM.setStyleAttribute(element, "width", originalWidth);
        return padding;
    }

    public static void setWidthExcludingPadding(Element element,
            int requestedWidth, int paddingGuess) {

        int widthGuess = requestedWidth - paddingGuess;
        if (widthGuess < 0) {
            widthGuess = 0;
        }

        DOM.setStyleAttribute(element, "width", widthGuess + "px");
        int captionOffsetWidth = DOM.getElementPropertyInt(element,
                "offsetWidth");

        int actualPadding = captionOffsetWidth - widthGuess;

        if (actualPadding != paddingGuess) {
            DOM.setStyleAttribute(element, "width", requestedWidth
                    - actualPadding + "px");

        }

    }

}
