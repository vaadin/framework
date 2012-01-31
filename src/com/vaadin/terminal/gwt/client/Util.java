/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.RenderInformation.FloatSize;

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
     * 
     * Returns the topmost element of from given coordinates.
     * 
     * TODO fix crossplat issues clientX vs pageX. See quircksmode. Not critical
     * for vaadin as we scroll div istead of page.
     * 
     * @param x
     * @param y
     * @return the element at given coordinates
     */
    public static native Element getElementFromPoint(int clientX, int clientY)
    /*-{
        var el = $wnd.document.elementFromPoint(clientX, clientY);
        if(el != null && el.nodeType == 3) {
            el = el.parentNode;
        }
        return el;
    }-*/;

    private static final int LAZY_SIZE_CHANGE_TIMEOUT = 400;
    private static Set<Widget> latelyChangedWidgets = new HashSet<Widget>();

    private static Timer lazySizeChangeTimer = new Timer() {
        private boolean lazySizeChangeTimerScheduled = false;

        @Override
        public void run() {
            componentSizeUpdated(latelyChangedWidgets);
            latelyChangedWidgets.clear();
            lazySizeChangeTimerScheduled = false;
        }

        @Override
        public void schedule(int delayMillis) {
            if (lazySizeChangeTimerScheduled) {
                cancel();
            } else {
                lazySizeChangeTimerScheduled = true;
            }
            super.schedule(delayMillis);
        }
    };

    /**
     * This helper method can be called if components size have been changed
     * outside rendering phase. It notifies components parent about the size
     * change so it can react.
     * 
     * When using this method, developer should consider if size changes could
     * be notified lazily. If lazy flag is true, method will save widget and
     * wait for a moment until it notifies parents in chunks. This may vastly
     * optimize layout in various situation. Example: if component have a lot of
     * images their onload events may fire "layout phase" many times in a short
     * period.
     * 
     * @param widget
     * @param lazy
     *            run componentSizeUpdated lazyly
     */
    public static void notifyParentOfSizeChange(Widget widget, boolean lazy) {
        if (lazy) {
            latelyChangedWidgets.add(widget);
            lazySizeChangeTimer.schedule(LAZY_SIZE_CHANGE_TIMEOUT);
        } else {
            Set<Widget> widgets = new HashSet<Widget>();
            widgets.add(widget);
            Util.componentSizeUpdated(widgets);
        }
    }

    /**
     * Called when the size of one or more widgets have changed during
     * rendering. Finds parent container and notifies them of the size change.
     * 
     * @param paintables
     */
    public static void componentSizeUpdated(Set<Widget> widgets) {
        if (widgets.isEmpty()) {
            return;
        }

        Map<Container, Set<Widget>> childWidgets = new HashMap<Container, Set<Widget>>();

        for (Widget widget : widgets) {
            if (!widget.isAttached()) {
                continue;
            }

            // ApplicationConnection.getConsole().log(
            // "Widget " + Util.getSimpleName(widget) + " size updated");
            Widget parent = widget.getParent();
            while (parent != null && !(parent instanceof Container)) {
                parent = parent.getParent();
            }
            if (parent != null) {
                Set<Widget> set = childWidgets.get(parent);
                if (set == null) {
                    set = new HashSet<Widget>();
                    childWidgets.put((Container) parent, set);
                }
                set.add(widget);
            }
        }

        Set<Widget> parentChanges = new HashSet<Widget>();
        for (Container parent : childWidgets.keySet()) {
            if (!parent.requestLayout(childWidgets.get(parent))) {
                parentChanges.add((Widget) parent);
            }
        }

        componentSizeUpdated(parentChanges);
    }

    public static float parseRelativeSize(String size) {
        if (size == null || !size.endsWith("%")) {
            return -1;
        }

        try {
            return Float.parseFloat(size.substring(0, size.length() - 1));
        } catch (Exception e) {
            VConsole.log("Unable to parse relative size");
            return -1;
        }
    }

    /**
     * Returns closest parent Widget in hierarchy that implements Container
     * interface
     * 
     * @param component
     * @return closest parent Container
     */
    public static Container getLayout(Widget component) {
        Widget parent = component.getParent();
        while (parent != null && !(parent instanceof Container)) {
            parent = parent.getParent();
        }
        if (parent != null) {
            assert ((Container) parent).hasChildComponent(component);

            return (Container) parent;
        }
        return null;
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
        String escapedText = DOM.getInnerHTML(escapeHtmlHelper);
        if (BrowserInfo.get().isIE8()) {
            // #7478 IE8 "incorrectly" returns "<br>" for newlines set using
            // setInnerText. The same for " " which is converted to "&nbsp;"
            escapedText = escapedText.replaceAll("<(BR|br)>", "\n");
            escapedText = escapedText.replaceAll("&nbsp;", " ");
        }
        return escapedText;
    }

    /**
     * Escapes the string so it is safe to write inside an HTML attribute.
     * 
     * @param attribute
     *            The string to escape
     * @return An escaped version of <literal>attribute</literal>.
     */
    public static String escapeAttribute(String attribute) {
        attribute = attribute.replace("\"", "&quot;");
        attribute = attribute.replace("'", "&#39;");
        attribute = attribute.replace(">", "&gt;");
        attribute = attribute.replace("<", "&lt;");
        attribute = attribute.replace("&", "&amp;");
        return attribute;
    }

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

    public static int measureHorizontalPaddingAndBorder(Element element,
            int paddingGuess) {
        String originalWidth = DOM.getStyleAttribute(element, "width");

        int originalOffsetWidth = element.getOffsetWidth();
        int widthGuess = (originalOffsetWidth - paddingGuess);
        if (widthGuess < 1) {
            widthGuess = 1;
        }
        DOM.setStyleAttribute(element, "width", widthGuess + "px");
        int padding = element.getOffsetWidth() - widthGuess;

        DOM.setStyleAttribute(element, "width", originalWidth);

        return padding;
    }

    public static int measureVerticalPaddingAndBorder(Element element,
            int paddingGuess) {
        String originalHeight = DOM.getStyleAttribute(element, "height");
        int originalOffsetHeight = element.getOffsetHeight();
        int widthGuess = (originalOffsetHeight - paddingGuess);
        if (widthGuess < 1) {
            widthGuess = 1;
        }
        DOM.setStyleAttribute(element, "height", widthGuess + "px");
        int padding = element.getOffsetHeight() - widthGuess;

        DOM.setStyleAttribute(element, "height", originalHeight);
        return padding;
    }

    public static int measureHorizontalBorder(Element element) {
        int borders;

        if (BrowserInfo.get().isIE()) {
            String width = element.getStyle().getProperty("width");
            String height = element.getStyle().getProperty("height");

            int offsetWidth = element.getOffsetWidth();
            int offsetHeight = element.getOffsetHeight();
            if (offsetHeight < 1) {
                offsetHeight = 1;
            }
            if (offsetWidth < 1) {
                offsetWidth = 10;
            }
            element.getStyle().setPropertyPx("height", offsetHeight);
            element.getStyle().setPropertyPx("width", offsetWidth);

            borders = element.getOffsetWidth() - element.getClientWidth();

            element.getStyle().setProperty("width", width);
            element.getStyle().setProperty("height", height);
        } else {
            borders = element.getOffsetWidth()
                    - element.getPropertyInt("clientWidth");
        }
        assert borders >= 0;

        return borders;
    }

    public static int measureVerticalBorder(Element element) {
        int borders;
        if (BrowserInfo.get().isIE()) {
            String width = element.getStyle().getProperty("width");
            String height = element.getStyle().getProperty("height");

            int offsetWidth = element.getOffsetWidth();
            int offsetHeight = element.getOffsetHeight();
            if (offsetHeight < 1) {
                offsetHeight = 1;
            }
            if (offsetWidth < 1) {
                offsetWidth = 10;
            }
            element.getStyle().setPropertyPx("width", offsetWidth);

            element.getStyle().setPropertyPx("height", offsetHeight);

            borders = element.getOffsetHeight()
                    - element.getPropertyInt("clientHeight");

            element.getStyle().setProperty("height", height);
            element.getStyle().setProperty("width", width);
        } else {
            borders = element.getOffsetHeight()
                    - element.getPropertyInt("clientHeight");
        }
        assert borders >= 0;

        return borders;
    }

    public static int measureMarginLeft(Element element) {
        return element.getAbsoluteLeft()
                - element.getParentElement().getAbsoluteLeft();
    }

    public static int setHeightExcludingPaddingAndBorder(Widget widget,
            String height, int paddingBorderGuess) {
        if (height.equals("")) {
            setHeight(widget, "");
            return paddingBorderGuess;
        } else if (height.endsWith("px")) {
            int pixelHeight = Integer.parseInt(height.substring(0,
                    height.length() - 2));
            return setHeightExcludingPaddingAndBorder(widget.getElement(),
                    pixelHeight, paddingBorderGuess, false);
        } else {
            // Set the height in unknown units
            setHeight(widget, height);
            // Use the offsetWidth
            return setHeightExcludingPaddingAndBorder(widget.getElement(),
                    widget.getOffsetHeight(), paddingBorderGuess, true);
        }
    }

    private static void setWidth(Widget widget, String width) {
        DOM.setStyleAttribute(widget.getElement(), "width", width);
    }

    private static void setHeight(Widget widget, String height) {
        DOM.setStyleAttribute(widget.getElement(), "height", height);
    }

    public static int setWidthExcludingPaddingAndBorder(Widget widget,
            String width, int paddingBorderGuess) {
        if (width.equals("")) {
            setWidth(widget, "");
            return paddingBorderGuess;
        } else if (width.endsWith("px")) {
            int pixelWidth = Integer.parseInt(width.substring(0,
                    width.length() - 2));
            return setWidthExcludingPaddingAndBorder(widget.getElement(),
                    pixelWidth, paddingBorderGuess, false);
        } else {
            setWidth(widget, width);
            return setWidthExcludingPaddingAndBorder(widget.getElement(),
                    widget.getOffsetWidth(), paddingBorderGuess, true);
        }
    }

    public static int setWidthExcludingPaddingAndBorder(Element element,
            int requestedWidth, int horizontalPaddingBorderGuess,
            boolean requestedWidthIncludesPaddingBorder) {

        int widthGuess = requestedWidth - horizontalPaddingBorderGuess;
        if (widthGuess < 0) {
            widthGuess = 0;
        }

        DOM.setStyleAttribute(element, "width", widthGuess + "px");
        int captionOffsetWidth = DOM.getElementPropertyInt(element,
                "offsetWidth");

        int actualPadding = captionOffsetWidth - widthGuess;

        if (requestedWidthIncludesPaddingBorder) {
            actualPadding += actualPadding;
        }

        if (actualPadding != horizontalPaddingBorderGuess) {
            int w = requestedWidth - actualPadding;
            if (w < 0) {
                // Cannot set negative width even if we would want to
                w = 0;
            }
            DOM.setStyleAttribute(element, "width", w + "px");

        }

        return actualPadding;

    }

    public static int setHeightExcludingPaddingAndBorder(Element element,
            int requestedHeight, int verticalPaddingBorderGuess,
            boolean requestedHeightIncludesPaddingBorder) {

        int heightGuess = requestedHeight - verticalPaddingBorderGuess;
        if (heightGuess < 0) {
            heightGuess = 0;
        }

        DOM.setStyleAttribute(element, "height", heightGuess + "px");
        int captionOffsetHeight = DOM.getElementPropertyInt(element,
                "offsetHeight");

        int actualPadding = captionOffsetHeight - heightGuess;

        if (requestedHeightIncludesPaddingBorder) {
            actualPadding += actualPadding;
        }

        if (actualPadding != verticalPaddingBorderGuess) {
            int h = requestedHeight - actualPadding;
            if (h < 0) {
                // Cannot set negative height even if we would want to
                h = 0;
            }
            DOM.setStyleAttribute(element, "height", h + "px");

        }

        return actualPadding;

    }

    public static String getSimpleName(Object widget) {
        if (widget == null) {
            return "(null)";
        }

        String name = widget.getClass().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public static void setFloat(Element element, String value) {
        if (BrowserInfo.get().isIE()) {
            DOM.setStyleAttribute(element, "styleFloat", value);
        } else {
            DOM.setStyleAttribute(element, "cssFloat", value);
        }
    }

    private static int detectedScrollbarSize = -1;

    public static int getNativeScrollbarSize() {
        if (detectedScrollbarSize < 0) {
            Element scroller = DOM.createDiv();
            scroller.getStyle().setProperty("width", "50px");
            scroller.getStyle().setProperty("height", "50px");
            scroller.getStyle().setProperty("overflow", "scroll");
            scroller.getStyle().setProperty("position", "absolute");
            scroller.getStyle().setProperty("marginLeft", "-5000px");
            RootPanel.getBodyElement().appendChild(scroller);
            detectedScrollbarSize = scroller.getOffsetWidth()
                    - scroller.getPropertyInt("clientWidth");

            RootPanel.getBodyElement().removeChild(scroller);
        }
        return detectedScrollbarSize;
    }

    /**
     * Run workaround for webkits overflow auto issue.
     * 
     * See: our bug #2138 and https://bugs.webkit.org/show_bug.cgi?id=21462
     * 
     * @param elem
     *            with overflow auto
     */
    public static void runWebkitOverflowAutoFix(final Element elem) {
        // Add max version if fix lands sometime to Webkit
        // Starting from Opera 11.00, also a problem in Opera
        if ((BrowserInfo.get().getWebkitVersion() > 0 || BrowserInfo.get()
                .getOperaVersion() >= 11) && getNativeScrollbarSize() > 0) {
            final String originalOverflow = elem.getStyle().getProperty(
                    "overflow");
            if ("hidden".equals(originalOverflow)) {
                return;
            }

            // check the scrolltop value before hiding the element
            final int scrolltop = elem.getScrollTop();
            final int scrollleft = elem.getScrollLeft();
            elem.getStyle().setProperty("overflow", "hidden");

            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    // Dough, Safari scroll auto means actually just a moped
                    elem.getStyle().setProperty("overflow", originalOverflow);

                    if (scrolltop > 0 || elem.getScrollTop() > 0) {
                        int scrollvalue = scrolltop;
                        if (scrollvalue == 0) {
                            // mysterious are the ways of webkits scrollbar
                            // handling. In some cases webkit reports bad (0)
                            // scrolltop before hiding the element temporary,
                            // sometimes after.
                            scrollvalue = elem.getScrollTop();
                        }
                        // fix another bug where scrollbar remains in wrong
                        // position
                        elem.setScrollTop(scrollvalue - 1);
                        elem.setScrollTop(scrollvalue);
                    }

                    // fix for #6940 : Table horizontal scroll sometimes not
                    // updated when collapsing/expanding columns
                    // Also appeared in Safari 5.1 with webkit 534 (#7667)
                    if ((BrowserInfo.get().isChrome() || (BrowserInfo.get()
                            .isSafari() && BrowserInfo.get().getWebkitVersion() >= 534))
                            && (scrollleft > 0 || elem.getScrollLeft() > 0)) {
                        int scrollvalue = scrollleft;

                        if (scrollvalue == 0) {
                            // mysterious are the ways of webkits scrollbar
                            // handling. In some cases webkit may report a bad
                            // (0) scrollleft before hiding the element
                            // temporary, sometimes after.
                            scrollvalue = elem.getScrollLeft();
                        }
                        // fix another bug where scrollbar remains in wrong
                        // position
                        elem.setScrollLeft(scrollvalue - 1);
                        elem.setScrollLeft(scrollvalue);
                    }
                }
            });
        }

    }

    /**
     * Parses the UIDL parameter and fetches the relative size of the component.
     * If a dimension is not specified as relative it will return -1. If the
     * UIDL does not contain width or height specifications this will return
     * null.
     * 
     * @param uidl
     * @return
     */
    public static FloatSize parseRelativeSize(UIDL uidl) {
        boolean hasAttribute = false;
        String w = "";
        String h = "";
        if (uidl.hasAttribute("width")) {
            hasAttribute = true;
            w = uidl.getStringAttribute("width");
        }
        if (uidl.hasAttribute("height")) {
            hasAttribute = true;
            h = uidl.getStringAttribute("height");
        }

        if (!hasAttribute) {
            return null;
        }

        float relativeWidth = Util.parseRelativeSize(w);
        float relativeHeight = Util.parseRelativeSize(h);

        FloatSize relativeSize = new FloatSize(relativeWidth, relativeHeight);
        return relativeSize;

    }

    public static boolean isCached(UIDL uidl) {
        return uidl.getBooleanAttribute("cached");
    }

    public static void alert(String string) {
        if (true) {
            Window.alert(string);
        }
    }

    public static boolean equals(Object a, Object b) {
        if (a == null) {
            return b == null;
        }

        return a.equals(b);
    }

    public static void updateRelativeChildrenAndSendSizeUpdateEvent(
            ApplicationConnection client, HasWidgets container, Widget widget) {
        /*
         * Relative sized children must be updated first so the component has
         * the correct outer dimensions when signaling a size change to the
         * parent.
         */
        Iterator<Widget> childIterator = container.iterator();
        while (childIterator.hasNext()) {
            Widget w = childIterator.next();
            client.handleComponentRelativeSize(w);
        }

        HashSet<Widget> widgets = new HashSet<Widget>();
        widgets.add(widget);
        Util.componentSizeUpdated(widgets);
    }

    public static native int getRequiredWidth(
            com.google.gwt.dom.client.Element element)
    /*-{
        if (element.getBoundingClientRect) {
          var rect = element.getBoundingClientRect();
          return Math.ceil(rect.right - rect.left);
        } else {
          return element.offsetWidth;
        }
    }-*/;

    public static native int getRequiredHeight(
            com.google.gwt.dom.client.Element element)
    /*-{
        var height;
        if (element.getBoundingClientRect != null) {
          var rect = element.getBoundingClientRect();
          height = Math.ceil(rect.bottom - rect.top);
        } else {
          height = element.offsetHeight;
        }
        return height;
    }-*/;

    public static int getRequiredWidth(Widget widget) {
        return getRequiredWidth(widget.getElement());
    }

    public static int getRequiredHeight(Widget widget) {
        return getRequiredHeight(widget.getElement());
    }

    /**
     * Detects what is currently the overflow style attribute in given element.
     * 
     * @param pe
     *            the element to detect
     * @return true if auto or scroll
     */
    public static boolean mayHaveScrollBars(com.google.gwt.dom.client.Element pe) {
        String overflow = getComputedStyle(pe, "overflow");
        if (overflow != null) {
            if (overflow.equals("auto") || overflow.equals("scroll")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * A simple helper method to detect "computed style" (aka style sheets +
     * element styles). Values returned differ a lot depending on browsers.
     * Always be very careful when using this.
     * 
     * @param el
     *            the element from which the style property is detected
     * @param p
     *            the property to detect
     * @return String value of style property
     */
    private static native String getComputedStyle(
            com.google.gwt.dom.client.Element el, String p)
    /*-{
        try {
        
        if (el.currentStyle) {
            // IE
            return el.currentStyle[p];
        } else if (window.getComputedStyle) {
            // Sa, FF, Opera
            var view = el.ownerDocument.defaultView;
            return view.getComputedStyle(el,null).getPropertyValue(p);
        } else {
            // fall back for non IE, Sa, FF, Opera
            return "";
        }
        } catch (e) {
            return "";
        }

     }-*/;

    /**
     * Locates the child component of <literal>parent</literal> which contains
     * the element <literal>element</literal>. The child component is also
     * returned if "element" is part of its caption. If
     * <literal>element</literal> is not part of any child component, null is
     * returned.
     * 
     * This method returns the immediate child of the parent that contains the
     * element. See
     * {@link #getPaintableForElement(ApplicationConnection, Container, Element)}
     * for the deepest nested paintable of parent that contains the element.
     * 
     * @param client
     *            A reference to ApplicationConnection
     * @param parent
     *            The widget that contains <literal>element</literal>.
     * @param element
     *            An element that is a sub element of the parent
     * @return The VPaintableWidget which the element is a part of. Null if the
     *         element does not belong to a child.
     */
    public static VPaintableWidget getChildPaintableForElement(
            ApplicationConnection client, Container parent, Element element) {
        Element rootElement = ((Widget) parent).getElement();
        while (element != null && element != rootElement) {
            VPaintableWidget paintable = VPaintableMap.get(client)
                    .getPaintable(element);
            if (paintable == null) {
                String ownerPid = VCaption.getCaptionOwnerPid(element);
                if (ownerPid != null) {
                    paintable = (VPaintableWidget) VPaintableMap.get(client)
                            .getPaintable(ownerPid);
                }
            }

            if (paintable != null
                    && parent.hasChildComponent(paintable
                            .getWidgetForPaintable())) {
                return paintable;
            }

            element = (Element) element.getParentElement();
        }

        return null;
    }

    /**
     * Locates the nested child component of <literal>parent</literal> which
     * contains the element <literal>element</literal>. The child component is
     * also returned if "element" is part of its caption. If
     * <literal>element</literal> is not part of any child component, null is
     * returned.
     * 
     * This method returns the deepest nested VPaintableWidget. See
     * {@link #getChildPaintableForElement(ApplicationConnection, Container, Element)}
     * for the immediate child component of parent that contains the element.
     * 
     * @param client
     *            A reference to ApplicationConnection
     * @param parent
     *            The widget that contains <literal>element</literal>.
     * @param element
     *            An element that is a sub element of the parent
     * @return The VPaintableWidget which the element is a part of. Null if the
     *         element does not belong to a child.
     */
    public static VPaintableWidget getPaintableForElement(
            ApplicationConnection client, Widget parent, Element element) {
        Element rootElement = parent.getElement();
        while (element != null && element != rootElement) {
            VPaintableWidget paintable = VPaintableMap.get(client)
                    .getPaintable(element);
            if (paintable == null) {
                String ownerPid = VCaption.getCaptionOwnerPid(element);
                if (ownerPid != null) {
                    paintable = (VPaintableWidget) VPaintableMap.get(client)
                            .getPaintable(ownerPid);
                }
            }

            if (paintable != null) {
                // check that inside the rootElement
                while (element != null && element != rootElement) {
                    element = (Element) element.getParentElement();
                }
                if (element != rootElement) {
                    return null;
                } else {
                    return paintable;
                }
            }

            element = (Element) element.getParentElement();
        }

        return null;
    }

    /**
     * Will (attempt) to focus the given DOM Element.
     * 
     * @param el
     *            the element to focus
     */
    public static native void focus(Element el)
    /*-{
        try {
            el.focus();
        } catch (e) {

        }
    }-*/;

    /**
     * Helper method to find the nearest parent paintable instance by traversing
     * the DOM upwards from given element.
     * 
     * @param element
     *            the element to start from
     */
    public static VPaintableWidget findPaintable(ApplicationConnection client,
            Element element) {
        Widget widget = Util.findWidget(element, null);
        VPaintableMap vPaintableMap = VPaintableMap.get(client);
        while (widget != null && !vPaintableMap.isPaintable(widget)) {
            widget = widget.getParent();
        }
        return vPaintableMap.getPaintable(widget);

    }

    /**
     * Helper method to find first instance of given Widget type found by
     * traversing DOM upwards from given element.
     * 
     * @param element
     *            the element where to start seeking of Widget
     * @param class1
     *            the Widget type to seek for
     */
    public static <T> T findWidget(Element element,
            Class<? extends Widget> class1) {
        if (element != null) {
            /* First seek for the first EventListener (~Widget) from dom */
            EventListener eventListener = null;
            while (eventListener == null && element != null) {
                eventListener = Event.getEventListener(element);
                if (eventListener == null) {
                    element = (Element) element.getParentElement();
                }
            }
            if (eventListener != null) {
                /*
                 * Then find the first widget of type class1 from widget
                 * hierarchy
                 */
                Widget w = (Widget) eventListener;
                while (w != null) {
                    if (class1 == null || w.getClass() == class1) {
                        return (T) w;
                    }
                    w = w.getParent();
                }
            }
        }
        return null;
    }

    /**
     * Force webkit to redraw an element
     * 
     * @param element
     *            The element that should be redrawn
     */
    public static void forceWebkitRedraw(Element element) {
        Style style = element.getStyle();
        String s = style.getProperty("webkitTransform");
        if (s == null || s.length() == 0) {
            style.setProperty("webkitTransform", "scale(1)");
        } else {
            style.setProperty("webkitTransform", "");
        }
    }

    /**
     * Detaches and re-attaches the element from its parent. The element is
     * reattached at the same position in the DOM as it was before.
     * 
     * Does nothing if the element is not attached to the DOM.
     * 
     * @param element
     *            The element to detach and re-attach
     */
    public static void detachAttach(Element element) {
        if (element == null) {
            return;
        }

        Node nextSibling = element.getNextSibling();
        Node parent = element.getParentNode();
        if (parent == null) {
            return;
        }

        parent.removeChild(element);
        if (nextSibling == null) {
            parent.appendChild(element);
        } else {
            parent.insertBefore(element, nextSibling);
        }

    }

    public static void sinkOnloadForImages(Element element) {
        NodeList<com.google.gwt.dom.client.Element> imgElements = element
                .getElementsByTagName("img");
        for (int i = 0; i < imgElements.getLength(); i++) {
            DOM.sinkEvents((Element) imgElements.getItem(i), Event.ONLOAD);
        }

    }

    /**
     * Returns the index of the childElement within its parent.
     * 
     * @param subElement
     * @return
     */
    public static int getChildElementIndex(Element childElement) {
        int idx = 0;
        Node n = childElement;
        while ((n = n.getPreviousSibling()) != null) {
            idx++;
        }

        return idx;
    }

    private static void printPaintablesVariables(ArrayList<String[]> vars,
            String id, ApplicationConnection c) {
        VPaintableWidget paintable = (VPaintableWidget) VPaintableMap.get(c)
                .getPaintable(id);
        if (paintable != null) {
            VConsole.log("\t" + id + " (" + paintable.getClass() + ") :");
            for (String[] var : vars) {
                VConsole.log("\t\t" + var[1] + " (" + var[2] + ")" + " : "
                        + var[0]);
            }
        } else {
            VConsole.log("\t" + id + ": Warning: no corresponding paintable!");
        }
    }

    static void logVariableBurst(ApplicationConnection c,
            ArrayList<String> loggedBurst) {
        try {
            VConsole.log("Variable burst to be sent to server:");
            String curId = null;
            ArrayList<String[]> vars = new ArrayList<String[]>();
            for (int i = 0; i < loggedBurst.size(); i++) {
                String value = loggedBurst.get(i++);
                String[] split = loggedBurst
                        .get(i)
                        .split(String
                                .valueOf(ApplicationConnection.VAR_FIELD_SEPARATOR));
                String id = split[0];

                if (curId == null) {
                    curId = id;
                } else if (!curId.equals(id)) {
                    printPaintablesVariables(vars, curId, c);
                    vars.clear();
                    curId = id;
                }
                split[0] = value;
                vars.add(split);
            }
            if (!vars.isEmpty()) {
                printPaintablesVariables(vars, curId, c);
            }
        } catch (Exception e) {
            VConsole.error(e);
        }
    }

    /**
     * Temporarily sets the {@code styleProperty} to {@code tempValue} and then
     * resets it to its current value. Used mainly to work around rendering
     * issues in IE (and possibly in other browsers)
     * 
     * @param element
     *            The target element
     * @param styleProperty
     *            The name of the property to set
     * @param tempValue
     *            The temporary value
     */
    public static void setStyleTemporarily(Element element,
            final String styleProperty, String tempValue) {
        final Style style = element.getStyle();
        final String currentValue = style.getProperty(styleProperty);

        style.setProperty(styleProperty, tempValue);
        element.getOffsetWidth();
        style.setProperty(styleProperty, currentValue);

    }

    /**
     * A helper method to return the client position from an event. Returns
     * position from either first changed touch (if touch event) or from the
     * event itself.
     * 
     * @param event
     * @return
     */
    public static int getTouchOrMouseClientX(Event event) {
        if (isTouchEvent(event)) {
            return event.getChangedTouches().get(0).getClientX();
        } else {
            return event.getClientX();
        }
    }

    /**
     * A helper method to return the client position from an event. Returns
     * position from either first changed touch (if touch event) or from the
     * event itself.
     * 
     * @param event
     * @return
     */
    public static int getTouchOrMouseClientY(Event event) {
        if (isTouchEvent(event)) {
            return event.getChangedTouches().get(0).getClientY();
        } else {
            return event.getClientY();
        }
    }

    /**
     * 
     * @see #getTouchOrMouseClientY(Event)
     * @param currentGwtEvent
     * @return
     */
    public static int getTouchOrMouseClientY(NativeEvent currentGwtEvent) {
        return getTouchOrMouseClientY(Event.as(currentGwtEvent));
    }

    /**
     * @see #getTouchOrMouseClientX(Event)
     * 
     * @param event
     * @return
     */
    public static int getTouchOrMouseClientX(NativeEvent event) {
        return getTouchOrMouseClientX(Event.as(event));
    }

    public static boolean isTouchEvent(Event event) {
        return event.getType().contains("touch");
    }

    public static boolean isTouchEvent(NativeEvent event) {
        return isTouchEvent(Event.as(event));
    }

    public static void simulateClickFromTouchEvent(Event touchevent,
            Widget widget) {
        Touch touch = touchevent.getChangedTouches().get(0);
        final NativeEvent createMouseUpEvent = Document.get()
                .createMouseUpEvent(0, touch.getScreenX(), touch.getScreenY(),
                        touch.getClientX(), touch.getClientY(), false, false,
                        false, false, NativeEvent.BUTTON_LEFT);
        final NativeEvent createMouseDownEvent = Document.get()
                .createMouseDownEvent(0, touch.getScreenX(),
                        touch.getScreenY(), touch.getClientX(),
                        touch.getClientY(), false, false, false, false,
                        NativeEvent.BUTTON_LEFT);
        final NativeEvent createMouseClickEvent = Document.get()
                .createClickEvent(0, touch.getScreenX(), touch.getScreenY(),
                        touch.getClientX(), touch.getClientY(), false, false,
                        false, false);

        /*
         * Get target with element from point as we want the actual element, not
         * the one that sunk the event.
         */
        final Element target = getElementFromPoint(touch.getClientX(),
                touch.getClientY());
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            public void execute() {
                try {
                    target.dispatchEvent(createMouseDownEvent);
                    target.dispatchEvent(createMouseUpEvent);
                    target.dispatchEvent(createMouseClickEvent);
                } catch (Exception e) {
                }

            }
        });

    }

    /**
     * Gets the currently focused element for Internet Explorer.
     * 
     * @return The currently focused element
     */
    public native static Element getIEFocusedElement()
    /*-{
       if ($wnd.document.activeElement) {
           return $wnd.document.activeElement;
       }
       
       return null;
     }-*/
    ;

    /**
     * Kind of stronger version of isAttached(). In addition to std isAttached,
     * this method checks that this widget nor any of its parents is hidden. Can
     * be e.g used to check whether component should react to some events or
     * not.
     * 
     * @param widget
     * @return true if attached and displayed
     */
    public static boolean isAttachedAndDisplayed(Widget widget) {
        if (widget.isAttached()) {
            /*
             * Failfast using offset size, then by iterating the widget tree
             */
            boolean notZeroSized = widget.getOffsetHeight() > 0
                    || widget.getOffsetWidth() > 0;
            return notZeroSized || checkVisibilityRecursively(widget);
        } else {
            return false;
        }
    }

    private static boolean checkVisibilityRecursively(Widget widget) {
        if (widget.isVisible()) {
            Widget parent = widget.getParent();
            if (parent == null) {
                return true; // root panel
            } else {
                return checkVisibilityRecursively(parent);
            }
        } else {
            return false;
        }
    }

    /**
     * Scrolls an element into view vertically only. Modified version of
     * Element.scrollIntoView.
     * 
     * @param elem
     *            The element to scroll into view
     */
    public static native void scrollIntoViewVertically(Element elem)
    /*-{
        var top = elem.offsetTop;
        var height = elem.offsetHeight;
    
        if (elem.parentNode != elem.offsetParent) {
          top -= elem.parentNode.offsetTop;
        }
    
        var cur = elem.parentNode;
        while (cur && (cur.nodeType == 1)) {
          if (top < cur.scrollTop) {
            cur.scrollTop = top;
          }
          if (top + height > cur.scrollTop + cur.clientHeight) {
            cur.scrollTop = (top + height) - cur.clientHeight;
          }
    
          var offsetTop = cur.offsetTop;
          if (cur.parentNode != cur.offsetParent) {
            offsetTop -= cur.parentNode.offsetTop;
          }
           
          top += offsetTop - cur.scrollTop;
          cur = cur.parentNode;
        }
     }-*/;
}
