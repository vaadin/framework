/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.shared.util.SharedUtil;

/**
 * Utility methods which are related to client side code only
 */
public class WidgetUtil {

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
     * Helper method for a bug fix #14041. For mozilla getKeyCode return 0 for
     * space bar (because space is considered as char). If return 0 use
     * getCharCode.
     * 
     * @param event
     * @return return key code
     * @since 7.2.4
     */
    public static int getKeyCode(KeyEvent<?> event) {
        int keyCode = event.getNativeEvent().getKeyCode();
        if (keyCode == 0) {
            keyCode = event.getNativeEvent().getCharCode();
        }
        return keyCode;
    }

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
        // Call elementFromPoint two times to make sure IE8 also returns something sensible if the application is running in an iframe
        el = $wnd.document.elementFromPoint(clientX, clientY);
        if(el != null && el.nodeType == 3) {
            el = el.parentNode;
        }
        return el;
    }-*/;

    public static float parseRelativeSize(String size) {
        if (size == null || !size.endsWith("%")) {
            return -1;
        }

        try {
            return Float.parseFloat(size.substring(0, size.length() - 1));
        } catch (Exception e) {
            getLogger().warning("Unable to parse relative size");
            return -1;
        }
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
        if (attribute == null) {
            return "";
        }
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
        element.getStyle().setWidth(widthGuess, Unit.PX);
        int padding = element.getOffsetWidth() - widthGuess;

        element.getStyle().setProperty("width", originalWidth);

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
        element.getStyle().setHeight(widthGuess, Unit.PX);
        int padding = element.getOffsetHeight() - widthGuess;

        element.getStyle().setProperty("height", originalHeight);
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
        widget.getElement().getStyle().setProperty("width", width);
    }

    private static void setHeight(Widget widget, String height) {
        widget.getElement().getStyle().setProperty("height", height);
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

        element.getStyle().setWidth(widthGuess, Unit.PX);
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
            element.getStyle().setWidth(w, Unit.PX);

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

        element.getStyle().setHeight(heightGuess, Unit.PX);
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
            element.getStyle().setHeight(h, Unit.PX);

        }

        return actualPadding;

    }

    public static void setFloat(Element element, String value) {
        if (BrowserInfo.get().isIE()) {
            element.getStyle().setProperty("styleFloat", value);
        } else {
            element.getStyle().setProperty("cssFloat", value);
        }
    }

    private static int detectedScrollbarSize = -1;
    private static int detectedSubPixelRoundingFactor = -1;

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
     * Defers the execution of {@link #runWebkitOverflowAutoFix(Element)}
     * 
     * @since 7.2.6
     * @param elem
     *            with overflow auto
     */
    public static void runWebkitOverflowAutoFixDeferred(final Element elem) {
        Scheduler.get().scheduleDeferred(new Command() {

            @Override
            public void execute() {
                WidgetUtil.runWebkitOverflowAutoFix(elem);
            }
        });

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
        if (BrowserInfo.get().requiresOverflowAutoFix()) {
            final String originalOverflow = elem.getStyle().getProperty(
                    "overflow");
            final String originalOverflowX = elem.getStyle().getProperty(
                    "overflowX");
            final String originalOverflowY = elem.getStyle().getProperty(
                    "overflowY");
            if ("hidden".equals(originalOverflow)
                    || "hidden".equals(originalOverflowX)
                    || "hidden".equals(originalOverflowY)) {
                return;
            }

            // check the scrolltop value before hiding the element
            final int scrolltop = elem.getScrollTop();
            final int scrollleft = elem.getScrollLeft();
            elem.getStyle().setProperty("overflow", "hidden");

            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    // Dough, Safari scroll auto means actually just a moped
                    elem.getStyle().setProperty("overflow", originalOverflow);
                    if (!originalOverflowX.isEmpty()) {
                        elem.getStyle().setProperty("overflowX",
                                originalOverflowX);
                    }
                    if (!originalOverflowY.isEmpty()) {
                        elem.getStyle().setProperty("overflowY",
                                originalOverflowY);
                    }

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

    public static void alert(String string) {
        if (true) {
            Window.alert(string);
        }
    }

    /**
     * Gets the border-box width for the given element, i.e. element width +
     * border + padding. Always rounds up to nearest integer.
     * 
     * @param element
     *            The element to check
     * @return The border-box width for the element
     */
    public static int getRequiredWidth(com.google.gwt.dom.client.Element element) {
        int reqWidth = getRequiredWidthBoundingClientRect(element);
        if (BrowserInfo.get().isIE() && !BrowserInfo.get().isIE8()) {
            int csSize = getRequiredWidthComputedStyle(element);
            if (csSize == reqWidth + 1) {
                // If computed style reports one pixel larger than requiredWidth
                // we would be rounding in the wrong direction in IE9. Round up
                // instead.
                // We do not always use csSize as it e.g. for 100% wide Labels
                // in GridLayouts produces senseless values (see e.g.
                // ThemeTestUI with Runo).
                return csSize;
            }
        }
        return reqWidth;
    }

    /**
     * Gets the border-box width for the given element, i.e. element width +
     * border + padding.
     * 
     * @param element
     *            The element to check
     * @return The border-box width for the element
     */
    public static double getRequiredWidthDouble(
            com.google.gwt.dom.client.Element element) {
        double reqWidth = getRequiredWidthBoundingClientRectDouble(element);
        if (BrowserInfo.get().isIE() && !BrowserInfo.get().isIE8()) {
            double csWidth = getRequiredWidthComputedStyleDouble(element);
            if (csWidth > reqWidth && csWidth <= (reqWidth + 1)) {
                // IE9 rounds reqHeight to integers BUT sometimes reports wrong
                // csHeight it seems, so we only use csHeight if it is within a
                // rounding error

                return csWidth;
            }
        }
        return reqWidth;
    }

    /**
     * Gets the border-box height for the given element, i.e. element height +
     * border + padding. Always rounds up to nearest integer.
     * 
     * @param element
     *            The element to check
     * @return The border-box height for the element
     */
    public static int getRequiredHeight(
            com.google.gwt.dom.client.Element element) {
        int reqHeight = getRequiredHeightBoundingClientRect(element);
        if (BrowserInfo.get().isIE() && !BrowserInfo.get().isIE8()) {
            int csSize = getRequiredHeightComputedStyle(element);
            if (csSize == reqHeight + 1) {
                // If computed style reports one pixel larger than
                // requiredHeight we would be rounding in the wrong direction in
                // IE9. Round up instead.
                // We do not always use csSize as it e.g. for 100% wide Labels
                // in GridLayouts produces senseless values (see e.g.
                // ThemeTestUI with Runo).
                return csSize;
            }
        }
        return reqHeight;
    }

    /**
     * Gets the border-box height for the given element, i.e. element height +
     * border + padding.
     * 
     * @param element
     *            The element to check
     * @return The border-box height for the element
     */
    public static double getRequiredHeightDouble(
            com.google.gwt.dom.client.Element element) {
        double reqHeight = getRequiredHeightBoundingClientRectDouble(element);
        if (BrowserInfo.get().isIE() && !BrowserInfo.get().isIE8()) {
            double csHeight = getRequiredHeightComputedStyleDouble(element);
            if (csHeight > reqHeight && csHeight <= (reqHeight + 1)) {
                // IE9 rounds reqHeight to integers BUT sometimes reports wrong
                // csHeight it seems, so we only use csHeight if it is within a
                // rounding error

                // Although sometimes it also happens that IE9 returns an
                // incorrectly rounded down requiredHeight and a computed height
                // which is exactly one larger, hence the "<="...
                return csHeight;
            }
        }
        return reqHeight;
    }

    /**
     * Calculates the width of the element's bounding rectangle.
     * <p>
     * In case the browser doesn't support bounding rectangles, the returned
     * value is the offset width.
     * 
     * @param element
     *            the element of which to calculate the width
     * @return the width of the element
     */
    public static int getRequiredWidthBoundingClientRect(
            com.google.gwt.dom.client.Element element) {
        return (int) Math
                .ceil(getRequiredWidthBoundingClientRectDouble(element));
    }

    /**
     * Calculates the width of the element's bounding rectangle to subpixel
     * precision.
     * <p>
     * In case the browser doesn't support bounding rectangles, the returned
     * value is the offset width.
     * 
     * @param element
     *            the element of which to calculate the width
     * @return the subpixel-accurate width of the element
     * @since 7.4
     */
    public static native double getRequiredWidthBoundingClientRectDouble(
            com.google.gwt.dom.client.Element element)
    /*-{
        if (element.getBoundingClientRect) {
          var rect = element.getBoundingClientRect();
          return rect.right - rect.left;
        } else {
          return element.offsetWidth;
        }
    }-*/;

    public static int getRequiredHeightComputedStyle(
            com.google.gwt.dom.client.Element element) {
        return (int) Math.ceil(getRequiredHeightComputedStyleDouble(element));
    }

    public static native double getRequiredHeightComputedStyleDouble(
            com.google.gwt.dom.client.Element element)
    /*-{
         var cs = element.ownerDocument.defaultView.getComputedStyle(element);
         var heightPx = cs.height;
         if(heightPx == 'auto'){
             // Fallback for inline elements
             return @com.vaadin.client.WidgetUtil::getRequiredHeightBoundingClientRectDouble(Lcom/google/gwt/dom/client/Element;)(element);
         }
         var height = parseFloat(heightPx); // Will automatically skip "px" suffix
         var border = parseFloat(cs.borderTopWidth) + parseFloat(cs.borderBottomWidth); // Will automatically skip "px" suffix 
         var padding = parseFloat(cs.paddingTop) + parseFloat(cs.paddingBottom); // Will automatically skip "px" suffix
         return height+border+padding;
     }-*/;

    public static int getRequiredWidthComputedStyle(
            com.google.gwt.dom.client.Element element) {
        return (int) Math.ceil(getRequiredWidthComputedStyleDouble(element));
    }

    public static native int getRequiredWidthComputedStyleDouble(
            com.google.gwt.dom.client.Element element)
    /*-{
         var cs = element.ownerDocument.defaultView.getComputedStyle(element);
         var widthPx = cs.width;
         if(widthPx == 'auto'){
             // Fallback for inline elements
             return @com.vaadin.client.WidgetUtil::getRequiredWidthBoundingClientRectDouble(Lcom/google/gwt/dom/client/Element;)(element);
         }
         var width = parseFloat(widthPx); // Will automatically skip "px" suffix
         var border = parseFloat(cs.borderLeftWidth) + parseFloat(cs.borderRightWidth); // Will automatically skip "px" suffix
         var padding = parseFloat(cs.paddingLeft) + parseFloat(cs.paddingRight); // Will automatically skip "px" suffix
         return width+border+padding;
     }-*/;

    /**
     * Calculates the height of the element's bounding rectangle.
     * <p>
     * In case the browser doesn't support bounding rectangles, the returned
     * value is the offset height.
     * 
     * @param element
     *            the element of which to calculate the height
     * @return the height of the element
     */
    public static int getRequiredHeightBoundingClientRect(
            com.google.gwt.dom.client.Element element) {
        return (int) Math
                .ceil(getRequiredHeightBoundingClientRectDouble(element));
    }

    /**
     * Calculates the height of the element's bounding rectangle to subpixel
     * precision.
     * <p>
     * In case the browser doesn't support bounding rectangles, the returned
     * value is the offset height.
     * 
     * @param element
     *            the element of which to calculate the height
     * @return the subpixel-accurate height of the element
     * @since 7.4
     */
    public static native double getRequiredHeightBoundingClientRectDouble(
            com.google.gwt.dom.client.Element element)
    /*-{
        var height;
        if (element.getBoundingClientRect != null) {
          var rect = element.getBoundingClientRect();
          height = rect.bottom - rect.top;
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
     * Helper method to find first instance of given Widget type found by
     * traversing DOM upwards from given element.
     * <p>
     * <strong>Note:</strong> If {@code element} is inside some widget {@code W}
     * , <em>and</em> {@code W} in turn is wrapped in a {@link Composite}
     * {@code C}, this method will not find {@code W}. It returns either
     * {@code C} or null, depending on whether the class parameter matches. This
     * may also be the case with other Composite-like classes that hijack the
     * event handling of their child widget(s).
     * 
     * @param element
     *            the element where to start seeking of Widget
     * @param class1
     *            the Widget type to seek for
     */
    @SuppressWarnings("unchecked")
    public static <T> T findWidget(Element element,
            Class<? extends Widget> class1) {
        if (element != null) {
            /* First seek for the first EventListener (~Widget) from dom */
            EventListener eventListener = null;
            while (eventListener == null && element != null) {
                eventListener = Event.getEventListener(element);
                if (eventListener == null) {
                    element = element.getParentElement();
                }
            }
            if (eventListener instanceof Widget) {
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
     * Performs a hack to trigger a re-layout in the IE8. This is usually
     * necessary in cases where IE8 "forgets" to update child elements when they
     * resize.
     * 
     * @param e
     *            The element to perform the hack on
     */
    public static final void forceIE8Redraw(Element e) {
        if (BrowserInfo.get().isIE8()) {
            forceIERedraw(e);
        }
    }

    /**
     * Performs a hack to trigger a re-layout in the IE browser. This is usually
     * necessary in cases where IE "forgets" to update child elements when they
     * resize.
     * 
     * @since 7.3
     * @param e
     *            The element to perform the hack on
     */
    public static void forceIERedraw(Element e) {
        if (BrowserInfo.get().isIE()) {
            setStyleTemporarily(e, "zoom", "1");
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
            DOM.sinkEvents(imgElements.getItem(i), Event.ONLOAD);
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
     * Find the element corresponding to the coordinates in the passed mouse
     * event. Please note that this is not always the same as the target of the
     * event e.g. if event capture is used.
     * 
     * @param event
     *            the mouse event to get coordinates from
     * @return the element at the coordinates of the event
     */
    public static Element getElementUnderMouse(NativeEvent event) {
        int pageX = getTouchOrMouseClientX(event);
        int pageY = getTouchOrMouseClientY(event);

        return getElementFromPoint(pageX, pageY);
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

        /*
         * Fixes infocusable form fields in Safari of iOS 5.x and some Android
         * browsers.
         */
        Widget targetWidget = findWidget(target, null);
        if (targetWidget instanceof com.google.gwt.user.client.ui.Focusable) {
            final com.google.gwt.user.client.ui.Focusable toBeFocusedWidget = (com.google.gwt.user.client.ui.Focusable) targetWidget;
            toBeFocusedWidget.setFocus(true);
        } else if (targetWidget instanceof Focusable) {
            ((Focusable) targetWidget).focus();
        }

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
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
     * Gets the currently focused element.
     * 
     * @return The active element or null if no active element could be found.
     */
    public native static Element getFocusedElement()
    /*-{
       if ($wnd.document.activeElement) {
           return $wnd.document.activeElement;
       }

       return null;
     }-*/;

    /**
     * Gets currently focused element and checks if it's editable
     * 
     * @since 7.4
     * 
     * @return true if focused element is editable
     */
    public static boolean isFocusedElementEditable() {
        Element focusedElement = WidgetUtil.getFocusedElement();
        if (focusedElement != null) {
            String tagName = focusedElement.getTagName();
            String contenteditable = focusedElement
                    .getAttribute("contenteditable");

            return "textarea".equalsIgnoreCase(tagName)
                    || "input".equalsIgnoreCase(tagName)
                    || "true".equalsIgnoreCase(contenteditable);
        }
        return false;
    }

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

    /**
     * Checks if the given event is either a touch event or caused by the left
     * mouse button
     * 
     * @param event
     * @return true if the event is a touch event or caused by the left mouse
     *         button, false otherwise
     */
    public static boolean isTouchEventOrLeftMouseButton(Event event) {
        boolean touchEvent = WidgetUtil.isTouchEvent(event);
        return touchEvent || event.getButton() == Event.BUTTON_LEFT;
    }

    /**
     * Resolve a relative URL to an absolute URL based on the current document's
     * location.
     * 
     * @param url
     *            a string with the relative URL to resolve
     * @return the corresponding absolute URL as a string
     */
    public static String getAbsoluteUrl(String url) {
        if (BrowserInfo.get().isIE8()) {
            // The hard way - must use innerHTML and attach to DOM in IE8
            DivElement divElement = Document.get().createDivElement();
            divElement.getStyle().setDisplay(Display.NONE);

            RootPanel.getBodyElement().appendChild(divElement);
            divElement.setInnerHTML("<a href='" + escapeAttribute(url)
                    + "' ></a>");

            AnchorElement a = divElement.getChild(0).cast();
            String href = a.getHref();

            RootPanel.getBodyElement().removeChild(divElement);
            return href;
        } else {
            AnchorElement a = Document.get().createAnchorElement();
            a.setHref(url);
            return a.getHref();
        }
    }

    /**
     * Sets the selection range of an input element.
     * 
     * We need this JSNI function to set selection range so that we can use the
     * optional direction attribute to set the anchor to the end and the focus
     * to the start. This makes Firefox work the same way as other browsers
     * (#13477)
     * 
     * @param elem
     *            the html input element.
     * @param pos
     *            the index of the first selected character.
     * @param length
     *            the selection length.
     * @param direction
     *            a string indicating the direction in which the selection was
     *            performed. This may be "forward" or "backward", or "none" if
     *            the direction is unknown or irrelevant.
     * 
     * @since 7.3
     */
    public native static void setSelectionRange(Element elem, int pos,
            int length, String direction)
    /*-{
       try {
           elem.setSelectionRange(pos, pos + length, direction);
       } catch (e) {
          // Firefox throws exception if TextBox is not visible, even if attached
       }
    }-*/;

    /**
     * The allowed value inaccuracy when comparing two double-typed pixel
     * values.
     * <p>
     * Since we're comparing pixels on a screen, epsilon must be less than 1.
     * 0.49 was deemed a perfectly fine and beautifully round number.
     */
    public static final double PIXEL_EPSILON = 0.49d;

    /**
     * Compares two double values with the error margin of
     * {@link #PIXEL_EPSILON} (i.e. {@value #PIXEL_EPSILON})
     * 
     * @param num1
     *            the first value for which to compare equality
     * @param num2
     *            the second value for which to compare equality
     * @since 7.4
     * 
     * @return true if the values are considered equals; false otherwise
     */
    public static boolean pixelValuesEqual(final double num1, final double num2) {
        return Math.abs(num1 - num2) <= PIXEL_EPSILON;
    }

    public static native TextRectangle getBoundingClientRect(Element e)
    /*-{
        return e.getBoundingClientRect();
    }-*/;

    public static final class TextRectangle extends JavaScriptObject {
        protected TextRectangle() {
        }

        public native double getBottom()
        /*-{
            return this.bottom;
        }-*/;

        public native double getHeight()
        /*-{
            return this.height;
        }-*/;

        public native double getLeft()
        /*-{
            return this.left;
        }-*/;

        public native double getRight()
        /*-{
            return this.right;
        }-*/;

        public native double getTop()
        /*-{
            return this.top;
        }-*/;

        public native double getWidth()
        /*-{
            return this.width;
        }-*/;
    }

    /**
     * Wrap a css size value and its unit and translate back and forth to the
     * string representation.<br/>
     * Eg. 50%, 123px, ...
     * 
     * @since 7.2.6
     * @author Vaadin Ltd
     */
    @SuppressWarnings("serial")
    public static class CssSize implements Serializable {

        /*
         * Map the size units with their type.
         */
        private static Map<String, Unit> type2Unit = new HashMap<String, Style.Unit>();
        static {
            for (Unit unit : Unit.values()) {
                type2Unit.put(unit.getType(), unit);
            }
        }

        /**
         * Gets the unit value by its type.
         * 
         * @param type
         *            the type of the unit as found in the style.
         * @return the unit value.
         */
        public static Unit unitByType(String type) {
            return type2Unit.get(type);
        }

        /*
         * Regex to parse the size.
         */
        private static final RegExp sizePattern = RegExp
                .compile(SharedUtil.SIZE_PATTERN);

        /**
         * Parse the size from string format to {@link CssSize}.
         * 
         * @param s
         *            the size as string.
         * @return a {@link CssSize} object.
         */
        public static CssSize fromString(String s) {
            if (s == null) {
                return null;
            }

            s = s.trim();
            if ("".equals(s)) {
                return null;
            }

            float size = 0;
            Unit unit = null;

            MatchResult matcher = sizePattern.exec(s);
            if (matcher.getGroupCount() > 1) {

                size = Float.parseFloat(matcher.getGroup(1));
                if (size < 0) {
                    size = -1;
                    unit = Unit.PX;

                } else {
                    String symbol = matcher.getGroup(2);
                    unit = unitByType(symbol);
                }
            } else {
                throw new IllegalArgumentException("Invalid size argument: \""
                        + s + "\" (should match " + sizePattern.getSource()
                        + ")");
            }
            return new CssSize(size, unit);
        }

        /**
         * Creates a {@link CssSize} using a value and its measurement unit.
         * 
         * @param value
         *            the value.
         * @param unit
         *            the unit.
         * @return the {@link CssSize} object.
         */
        public static CssSize fromValueUnit(float value, Unit unit) {
            return new CssSize(value, unit);
        }

        /*
         * The value.
         */
        private final float value;

        /*
         * The measure unit.
         */
        private final Unit unit;

        private CssSize(float value, Unit unit) {
            this.value = value;
            this.unit = unit;
        }

        /**
         * Gets the value for this css size.
         * 
         * @return the value.
         */
        public float getValue() {
            return value;
        }

        /**
         * Gets the measurement unit for this css size.
         * 
         * @return the unit.
         */
        public Unit getUnit() {
            return unit;
        }

        @Override
        public String toString() {
            return value + unit.getType();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CssSize) {
                CssSize size = (CssSize) obj;
                return size.value == value && size.unit == unit;
            }

            return false;
        }

        /**
         * Check whether the two sizes are equals.
         * 
         * @param cssSize1
         *            the first size to compare.
         * @param cssSize2
         *            the other size to compare with the first one.
         * @return true if the two sizes are equals, otherwise false.
         */
        public static boolean equals(String cssSize1, String cssSize2) {
            return CssSize.fromString(cssSize1).equals(
                    CssSize.fromString(cssSize2));
        }

    }

    private static Logger getLogger() {
        return Logger.getLogger(WidgetUtil.class.getName());
    }

    /**
     * Returns the thickness of the given element's top border.
     * <p>
     * The value is determined using computed style when available and
     * calculated otherwise.
     * 
     * @since 7.5.0
     * @param element
     *            the element to measure
     * @return the top border thickness
     */
    public static double getBorderTopThickness(Element element) {
        return getBorderThickness(element, new String[] { "borderTopWidth" });
    }

    /**
     * Returns the thickness of the given element's bottom border.
     * <p>
     * The value is determined using computed style when available and
     * calculated otherwise.
     * 
     * @since 7.5.0
     * @param element
     *            the element to measure
     * @return the bottom border thickness
     */
    public static double getBorderBottomThickness(Element element) {
        return getBorderThickness(element, new String[] { "borderBottomWidth" });
    }

    /**
     * Returns the combined thickness of the given element's top and bottom
     * borders.
     * <p>
     * The value is determined using computed style when available and
     * calculated otherwise.
     * 
     * @since 7.5.0
     * @param element
     *            the element to measure
     * @return the top and bottom border thickness
     */
    public static double getBorderTopAndBottomThickness(Element element) {
        return getBorderThickness(element, new String[] { "borderTopWidth",
                "borderBottomWidth" });
    }

    /**
     * Returns the thickness of the given element's left border.
     * <p>
     * The value is determined using computed style when available and
     * calculated otherwise.
     * 
     * @since 7.5.0
     * @param element
     *            the element to measure
     * @return the left border thickness
     */
    public static double getBorderLeftThickness(Element element) {
        return getBorderThickness(element, new String[] { "borderLeftWidth" });
    }

    /**
     * Returns the thickness of the given element's right border.
     * <p>
     * The value is determined using computed style when available and
     * calculated otherwise.
     * 
     * @since 7.5.0
     * @param element
     *            the element to measure
     * @return the right border thickness
     */
    public static double getBorderRightThickness(Element element) {
        return getBorderThickness(element, new String[] { "borderRightWidth" });
    }

    /**
     * Returns the thickness of the given element's left and right borders.
     * <p>
     * The value is determined using computed style when available and
     * calculated otherwise.
     * 
     * @since 7.5.0
     * @param element
     *            the element to measure
     * @return the top border thickness
     */
    public static double getBorderLeftAndRightThickness(Element element) {
        return getBorderThickness(element, new String[] { "borderLeftWidth",
                "borderRightWidth" });
    }

    private static native double getBorderThickness(
            com.google.gwt.dom.client.Element element, String[] borderNames)
    /*-{
        if (typeof $wnd.getComputedStyle === 'function') {
            var computedStyle = $wnd.getComputedStyle(element);
            var width = 0;
            for (i=0; i< borderNames.length; i++) {
                var borderWidth = computedStyle[borderNames[i]];
                width += parseFloat(borderWidth);
            }
            return width;
        } else {
            var parentElement = element.offsetParent;
            var cloneElement = element.cloneNode(false);
            cloneElement.style.boxSizing ="content-box";
            parentElement.appendChild(cloneElement);
            cloneElement.style.height = "10px"; // IE8 wants the height to be set to something...
            var heightWithBorder = cloneElement.offsetHeight;
            for (i=0; i< borderNames.length; i++) {
                cloneElement.style[borderNames[i]] = "0";
            }
            var heightWithoutBorder = cloneElement.offsetHeight;
            parentElement.removeChild(cloneElement);
            
            return heightWithBorder - heightWithoutBorder;
        }
    }-*/;

    /**
     * Rounds the given size up to a value which the browser will accept.
     * 
     * Safari/WebKit uses 1/64th of a pixel to enable using integer math
     * (http://trac.webkit.org/wiki/LayoutUnit).
     * 
     * Firefox uses 1/60th of a pixel because it is divisible by three
     * (https://bugzilla.mozilla.org/show_bug.cgi?id=1070940)
     * 
     * @since 7.5.1
     * @param size
     *            the value to round
     * @return the rounded value
     */
    public static double roundSizeUp(double size) {
        return roundSize(size, true);
    }

    /**
     * Rounds the given size down to a value which the browser will accept.
     * 
     * Safari/WebKit uses 1/64th of a pixel to enable using integer math
     * (http://trac.webkit.org/wiki/LayoutUnit).
     * 
     * Firefox uses 1/60th of a pixel because it is divisible by three
     * (https://bugzilla.mozilla.org/show_bug.cgi?id=1070940)
     * 
     * IE9+ uses 1/100th of a pixel
     * 
     * @since 7.5.1
     * @param size
     *            the value to round
     * @return the rounded value
     */
    public static double roundSizeDown(double size) {
        return roundSize(size, false);
    }

    private static double roundSize(double size, boolean roundUp) {
        if (BrowserInfo.get().isIE8()) {
            if (roundUp) {
                return Math.ceil(size);
            } else {
                return (int) size;
            }
        }

        double factor = getSubPixelRoundingFactor();
        if (factor < 0 || size < 0) {
            return size;
        }

        if (roundUp) {
            return roundSizeUp(size, factor);
        } else {
            return roundSizeDown(size, factor);
        }
    }

    /**
     * Returns the factor used by browsers to round subpixel values
     * 
     * @since 7.5.1
     * @return the factor N used by the browser when storing subpixels as X+Y/N
     */
    private static double getSubPixelRoundingFactor() {
        // Detects how the browser does subpixel rounding
        // Currently Firefox uses 1/60th pixels
        // and Safari uses 1/64th pixels
        // IE 1/100th pixels
        if (detectedSubPixelRoundingFactor != -1) {
            return detectedSubPixelRoundingFactor;
        }

        double probeSize = 0.999999;
        DivElement div = Document.get().createDivElement();
        Document.get().getBody().appendChild(div);
        div.getStyle().setHeight(probeSize, Unit.PX);
        ComputedStyle computedStyle = new ComputedStyle(div);
        double computedHeight = computedStyle.getHeight();

        if (computedHeight < probeSize) {
            // Rounded down by browser, all browsers but Firefox do this
            // today
            detectedSubPixelRoundingFactor = (int) Math
                    .round(1.0 / (1.0 - computedHeight));
        } else {
            // Rounded up / to nearest by browser
            probeSize = 1;

            while (computedStyle.getHeight() != 0.0) {
                computedHeight = computedStyle.getHeight();
                probeSize /= 2.0;
                div.getStyle().setHeight(probeSize, Unit.PX);
            }

            detectedSubPixelRoundingFactor = (int) Math
                    .round(1.0 / computedHeight);
        }

        div.removeFromParent();
        return detectedSubPixelRoundingFactor;
    }

    private static double roundSizeUp(double size, double divisor) {
        // In: 12.51, 60.0

        // 12
        double integerPart = (int) size;

        // (12.51 - 12) * 60 = 30.6
        double nrFractions = (size - integerPart) * divisor;

        // 12 + ceil(30.6) / 60 = 12 + 31/60 = 12.51666
        return integerPart + (Math.ceil(nrFractions)) / divisor;
    }

    private static double roundSizeDown(double size, double divisor) {
        // In: 12.51, 60.0

        // 12
        double integerPart = (int) size;

        // (12.51 - 12) * 60 = 30.6
        double nrFractions = (size - integerPart) * divisor;

        // 12 + int(30.6) / 60 = 12 + 30/60 = 12.5
        return integerPart + ((int) nrFractions) / divisor;
    }
}
