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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.RenderInformation.FloatSize;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.communication.MethodInvocation;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.util.SharedUtil;

import elemental.js.json.JsJsonValue;
import elemental.json.JsonValue;

public class Util {

    /**
     * Helper method for debugging purposes.
     * 
     * Stops execution on firefox browsers on a breakpoint.
     * 
     * @deprecated As of 7.4.0, use {@link WidgetUtil#browserDebugger()}
     *             instead.
     */
    @Deprecated
    public static void browserDebugger() {
        WidgetUtil.browserDebugger();
    }

    /**
     * Helper method for a bug fix #14041. For mozilla getKeyCode return 0 for
     * space bar (because space is considered as char). If return 0 use
     * getCharCode.
     * 
     * @deprecated As of 7.4.0, use {@link WidgetUtil#getKeyCode(KeyEvent)}
     *             instead.
     * 
     * @param event
     * @return return key code
     * @since 7.2.4
     */
    @Deprecated
    public static int getKeyCode(KeyEvent<?> event) {
        return WidgetUtil.getKeyCode(event);
    }

    /**
     * 
     * Returns the topmost element of from given coordinates.
     * 
     * TODO fix crossplat issues clientX vs pageX. See quircksmode. Not critical
     * for vaadin as we scroll div istead of page.
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#getElementFromPoint(int, int)} instead.
     * 
     * @param x
     * @param y
     * @return the element at given coordinates
     */
    @Deprecated
    public static com.google.gwt.user.client.Element getElementFromPoint(
            int clientX, int clientY) {
        return DOM.asOld(WidgetUtil.getElementFromPoint(clientX, clientY));
    }

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
     * 
     * @deprecated As of 7.0, use
     *             {@link LayoutManager#setNeedsMeasure(ComponentConnector)}
     *             instead
     */
    @Deprecated
    public static void notifyParentOfSizeChange(Widget widget, boolean lazy) {
        ComponentConnector connector = findConnectorFor(widget);
        if (connector != null) {
            connector.getLayoutManager().setNeedsMeasure(connector);
            if (!lazy) {
                connector.getLayoutManager().layoutNow();
            }
        }
    }

    public static ComponentConnector findConnectorFor(Widget widget) {
        List<ApplicationConnection> runningApplications = ApplicationConfiguration
                .getRunningApplications();
        for (ApplicationConnection applicationConnection : runningApplications) {
            ConnectorMap connectorMap = applicationConnection.getConnectorMap();
            ComponentConnector connector = connectorMap.getConnector(widget);
            if (connector == null) {
                continue;
            }
            if (connector.getConnection() == applicationConnection) {
                return connector;
            }
        }

        return null;
    }

    /**
     * @deprecated As of 7.4.0, use {@link WidgetUtil#parseRelativeSize(String)}
     *             instead.
     */
    @Deprecated
    public static float parseRelativeSize(String size) {
        return WidgetUtil.parseRelativeSize(size);
    }

    /**
     * Converts html entities to text.
     * 
     * @deprecated As of 7.4.0, use {@link WidgetUtil#escapeHTML(String)}
     *             instead.
     * 
     * @param html
     * @return escaped string presentation of given html
     */
    @Deprecated
    public static String escapeHTML(String html) {
        return WidgetUtil.escapeHTML(html);
    }

    /**
     * Escapes the string so it is safe to write inside an HTML attribute.
     * 
     * @deprecated As of 7.4.0, use {@link WidgetUtil#escapeAttribute(String)}
     *             instead.
     * 
     * @param attribute
     *            The string to escape
     * @return An escaped version of <literal>attribute</literal>.
     */
    @Deprecated
    public static String escapeAttribute(String attribute) {
        return WidgetUtil.escapeAttribute(attribute);
    }

    /**
     * Clones given element as in JavaScript.
     * 
     * Deprecate this if there appears similar method into GWT someday.
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#cloneNode(Element, boolean)} instead.
     * 
     * @param element
     * @param deep
     *            clone child tree also
     * @return
     */
    @Deprecated
    public static com.google.gwt.user.client.Element cloneNode(Element element,
            boolean deep) {
        return DOM.asOld(WidgetUtil.cloneNode(element, deep));
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#measureHorizontalPaddingAndBorder(Element, int)}
     *             instead.
     */
    @Deprecated
    public static int measureHorizontalPaddingAndBorder(Element element,
            int paddingGuess) {
        return WidgetUtil.measureHorizontalPaddingAndBorder(element,
                paddingGuess);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#measureVerticalPaddingAndBorder(Element, int)}
     *             instead.
     */
    @Deprecated
    public static int measureVerticalPaddingAndBorder(Element element,
            int paddingGuess) {
        return WidgetUtil
                .measureVerticalPaddingAndBorder(element, paddingGuess);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#measureHorizontalBorder(Element)} instead.
     */
    @Deprecated
    public static int measureHorizontalBorder(Element element) {
        return WidgetUtil.measureHorizontalBorder(element);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#measureVerticalBorder(Element)} instead.
     */
    @Deprecated
    public static int measureVerticalBorder(Element element) {
        return WidgetUtil.measureVerticalBorder(element);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#measureMarginLeft(Element)} instead.
     */
    @Deprecated
    public static int measureMarginLeft(Element element) {
        return WidgetUtil.measureMarginLeft(element);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#setHeightExcludingPaddingAndBorder(Widget, String, int)}
     *             instead.
     */
    @Deprecated
    public static int setHeightExcludingPaddingAndBorder(Widget widget,
            String height, int paddingBorderGuess) {
        return WidgetUtil.setHeightExcludingPaddingAndBorder(widget, height,
                paddingBorderGuess);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#setWidthExcludingPaddingAndBorder(Widget, String, int)}
     *             instead.
     */
    @Deprecated
    public static int setWidthExcludingPaddingAndBorder(Widget widget,
            String width, int paddingBorderGuess) {
        return WidgetUtil.setWidthExcludingPaddingAndBorder(widget, width,
                paddingBorderGuess);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#setWidthExcludingPaddingAndBorder(Element, int, int, boolean)}
     *             instead.
     */
    @Deprecated
    public static int setWidthExcludingPaddingAndBorder(Element element,
            int requestedWidth, int horizontalPaddingBorderGuess,
            boolean requestedWidthIncludesPaddingBorder) {
        return WidgetUtil.setWidthExcludingPaddingAndBorder(element,
                requestedWidth, horizontalPaddingBorderGuess,
                requestedWidthIncludesPaddingBorder);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#setHeightExcludingPaddingAndBorder(Element, int, int, boolean)}
     *             instead.
     */
    @Deprecated
    public static int setHeightExcludingPaddingAndBorder(Element element,
            int requestedHeight, int verticalPaddingBorderGuess,
            boolean requestedHeightIncludesPaddingBorder) {
        return WidgetUtil.setHeightExcludingPaddingAndBorder(element,
                requestedHeight, verticalPaddingBorderGuess,
                requestedHeightIncludesPaddingBorder);
    }

    /**
     * @deprecated As of 7.4, use {@link Class#getSimpleName()} instead.
     */
    @Deprecated
    public static String getSimpleName(Object widget) {
        if (widget == null) {
            return "(null)";
        }

        String name = widget.getClass().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * @deprecated As of 7.4.0, use {@link WidgetUtil#setFloat(Element, String)}
     *             instead.
     */
    @Deprecated
    public static void setFloat(Element element, String value) {
        WidgetUtil.setFloat(element, value);
    }

    /**
     * @deprecated As of 7.4.0, use {@link WidgetUtil#getNativeScrollbarSize()}
     *             instead.
     */
    @Deprecated
    public static int getNativeScrollbarSize() {
        return WidgetUtil.getNativeScrollbarSize();
    }

    /**
     * Defers the execution of {@link #runWebkitOverflowAutoFix(Element)}
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#runWebkitOverflowAutoFixDeferred(Element)}
     *             instead.
     * 
     * @since 7.2.6
     * @param elem
     *            with overflow auto
     */
    @Deprecated
    public static void runWebkitOverflowAutoFixDeferred(final Element elem) {
        WidgetUtil.runWebkitOverflowAutoFixDeferred(elem);
    }

    /**
     * Run workaround for webkits overflow auto issue.
     * 
     * See: our bug #2138 and https://bugs.webkit.org/show_bug.cgi?id=21462
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#runWebkitOverflowAutoFix(Element)} instead.
     * 
     * @param elem
     *            with overflow auto
     */
    @Deprecated
    public static void runWebkitOverflowAutoFix(final Element elem) {
        WidgetUtil.runWebkitOverflowAutoFix(elem);
    }

    /**
     * Parses shared state and fetches the relative size of the component. If a
     * dimension is not specified as relative it will return -1. If the shared
     * state does not contain width or height specifications this will return
     * null.
     * 
     * @param state
     * @return
     */
    public static FloatSize parseRelativeSize(AbstractComponentState state) {
        if (ComponentStateUtil.isUndefinedHeight(state)
                && ComponentStateUtil.isUndefinedWidth(state)) {
            return null;
        }

        float relativeWidth = WidgetUtil.parseRelativeSize(state.width);
        float relativeHeight = WidgetUtil.parseRelativeSize(state.height);

        FloatSize relativeSize = new FloatSize(relativeWidth, relativeHeight);
        return relativeSize;

    }

    @Deprecated
    public static boolean isCached(UIDL uidl) {
        return uidl.getBooleanAttribute("cached");
    }

    /**
     * @deprecated As of 7.4.0, use {@link WidgetUtil#alert(String)} instead.
     */
    @Deprecated
    public static void alert(String string) {
        WidgetUtil.alert(string);
    }

    /**
     * Checks if a and b are equals using {@link #equals(Object)}. Handles null
     * values as well. Does not ensure that objects are of the same type.
     * Assumes that the first object's equals method handle equals properly.
     * 
     * @param a
     *            The first value to compare
     * @param b
     *            The second value to compare
     * @return
     * @deprecated As of 7.1 use {@link SharedUtil#equals(Object)} instead
     */
    @Deprecated
    public static boolean equals(Object a, Object b) {
        return SharedUtil.equals(a, b);
    }

    public static void updateRelativeChildrenAndSendSizeUpdateEvent(
            ApplicationConnection client, HasWidgets container, Widget widget) {
        notifyParentOfSizeChange(widget, false);
    }

    /**
     * Gets the border-box width for the given element, i.e. element width +
     * border + padding. Always rounds up to nearest integer.
     * 
     * @deprecated As of 7.4.0, use {@link WidgetUtil#getRequiredWidth(Element)}
     *             instead.
     * 
     * @param element
     *            The element to check
     * @return The border-box width for the element
     */
    @Deprecated
    public static int getRequiredWidth(com.google.gwt.dom.client.Element element) {
        return WidgetUtil.getRequiredWidth(element);
    }

    /**
     * Gets the border-box height for the given element, i.e. element height +
     * border + padding. Always rounds up to nearest integer.
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#getRequiredHeight(Element)} instead.
     * 
     * @param element
     *            The element to check
     * @return The border-box height for the element
     */
    @Deprecated
    public static int getRequiredHeight(
            com.google.gwt.dom.client.Element element) {
        return WidgetUtil.getRequiredHeight(element);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#getRequiredWidthBoundingClientRect(Element)}
     *             instead.
     */
    @Deprecated
    public int getRequiredWidthBoundingClientRect(
            com.google.gwt.dom.client.Element element) {
        return WidgetUtil.getRequiredWidthBoundingClientRect(element);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#getRequiredHeightComputedStyle(Element)}
     *             instead.
     */
    @Deprecated
    public static int getRequiredHeightComputedStyle(
            com.google.gwt.dom.client.Element element) {
        return WidgetUtil.getRequiredHeightComputedStyle(element);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#getRequiredWidthComputedStyle(Element)}
     *             instead.
     */
    @Deprecated
    public static int getRequiredWidthComputedStyle(
            com.google.gwt.dom.client.Element element) {
        return WidgetUtil.getRequiredWidthComputedStyle(element);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#getRequiredHeightBoundingClientRect(Element)}
     *             instead.
     */
    @Deprecated
    public static int getRequiredHeightBoundingClientRect(
            com.google.gwt.dom.client.Element element) {
        return WidgetUtil.getRequiredHeightBoundingClientRect(element);
    }

    /**
     * @deprecated As of 7.4.0, use {@link WidgetUtil#getRequiredWidth(Widget)}
     *             instead.
     */
    @Deprecated
    public static int getRequiredWidth(Widget widget) {
        return WidgetUtil.getRequiredWidth(widget);
    }

    /**
     * @deprecated As of 7.4.0, use {@link WidgetUtil#getRequiredHeight(Widget)}
     *             instead.
     */
    @Deprecated
    public static int getRequiredHeight(Widget widget) {
        return WidgetUtil.getRequiredHeight(widget);
    }

    /**
     * Detects what is currently the overflow style attribute in given element.
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#mayHaveScrollBars(Element)} instead.
     * 
     * @param pe
     *            the element to detect
     * @return true if auto or scroll
     */
    @Deprecated
    public static boolean mayHaveScrollBars(com.google.gwt.dom.client.Element pe) {
        return WidgetUtil.mayHaveScrollBars(pe);
    }

    /**
     * Locates the nested child component of <literal>parent</literal> which
     * contains the element <literal>element</literal>. The child component is
     * also returned if "element" is part of its caption. If
     * <literal>element</literal> is not part of any child component, null is
     * returned.
     * 
     * This method returns the deepest nested VPaintableWidget.
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
    public static ComponentConnector getConnectorForElement(
            ApplicationConnection client, Widget parent, Element element) {

        Element browseElement = element;
        Element rootElement = parent.getElement();

        while (browseElement != null && browseElement != rootElement) {

            ComponentConnector connector = ConnectorMap.get(client)
                    .getConnector(browseElement);

            if (connector == null) {
                String ownerPid = VCaption.getCaptionOwnerPid(browseElement);
                if (ownerPid != null) {
                    connector = (ComponentConnector) ConnectorMap.get(client)
                            .getConnector(ownerPid);
                }
            }

            if (connector != null) {
                // check that inside the rootElement
                while (browseElement != null && browseElement != rootElement) {
                    browseElement = browseElement.getParentElement();
                }
                if (browseElement != rootElement) {
                    return null;
                } else {
                    return connector;
                }
            }

            browseElement = browseElement.getParentElement();
        }

        // No connector found, element is possibly inside a VOverlay
        // If the overlay has an owner, try to find the owner's connector
        VOverlay overlay = findWidget(element, VOverlay.class);
        if (overlay != null && overlay.getOwner() != null) {

            return getConnectorForElement(client, client.getUIConnector()
                    .getWidget(), overlay.getOwner().getElement());
        } else {
            return null;
        }
    }

    /**
     * Will (attempt) to focus the given DOM Element.
     * 
     * @deprecated As of 7.4.0, use {@link WidgetUtil#focus(Element)} instead.
     * 
     * @param el
     *            the element to focus
     */
    @Deprecated
    public static void focus(Element el) {
        WidgetUtil.focus(el);
    }

    /**
     * Helper method to find the nearest parent paintable instance by traversing
     * the DOM upwards from given element.
     * 
     * @param element
     *            the element to start from
     */
    public static ComponentConnector findPaintable(
            ApplicationConnection client, Element element) {
        Widget widget = Util.findWidget(element, null);
        ConnectorMap vPaintableMap = ConnectorMap.get(client);
        while (widget != null && !vPaintableMap.isConnector(widget)) {
            widget = widget.getParent();
        }
        return vPaintableMap.getConnector(widget);

    }

    /**
     * Helper method to find first instance of given Widget type found by
     * traversing DOM upwards from given element.
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#findWidget(Element, Class)} instead.
     * 
     * @param element
     *            the element where to start seeking of Widget
     * @param class1
     *            the Widget type to seek for
     */
    @Deprecated
    public static <T> T findWidget(Element element,
            Class<? extends Widget> class1) {
        return WidgetUtil.findWidget(element, class1);
    }

    /**
     * Force webkit to redraw an element
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#forceWebkitRedraw(Element)} instead.
     * 
     * @param element
     *            The element that should be redrawn
     */
    @Deprecated
    public static void forceWebkitRedraw(Element element) {
        WidgetUtil.forceWebkitRedraw(element);
    }

    /**
     * Performs a hack to trigger a re-layout in the IE8. This is usually
     * necessary in cases where IE8 "forgets" to update child elements when they
     * resize.
     * 
     * @deprecated As of 7.4.0, use {@link WidgetUtil#forceIE8Redraw(Element)}
     *             instead.
     * 
     * @param e
     *            The element to perform the hack on
     */
    @Deprecated
    public static final void forceIE8Redraw(Element e) {
        WidgetUtil.forceIE8Redraw(e);
    }

    /**
     * Performs a hack to trigger a re-layout in the IE browser. This is usually
     * necessary in cases where IE "forgets" to update child elements when they
     * resize.
     * 
     * @deprecated As of 7.4.0, use {@link WidgetUtil#forceIERedraw(Element)}
     *             instead.
     * 
     * @since 7.3
     * @param e
     *            The element to perform the hack on
     */
    @Deprecated
    public static void forceIERedraw(Element e) {
        WidgetUtil.forceIERedraw(e);
    }

    /**
     * Detaches and re-attaches the element from its parent. The element is
     * reattached at the same position in the DOM as it was before.
     * 
     * Does nothing if the element is not attached to the DOM.
     * 
     * @deprecated As of 7.4.0, use {@link WidgetUtil#detachAttach(Element)}
     *             instead.
     * 
     * @param element
     *            The element to detach and re-attach
     */
    @Deprecated
    public static void detachAttach(Element element) {
        WidgetUtil.detachAttach(element);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#sinkOnloadForImages(Element)} instead.
     */
    @Deprecated
    public static void sinkOnloadForImages(Element element) {
        WidgetUtil.sinkOnloadForImages(element);
    }

    /**
     * Returns the index of the childElement within its parent.
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#getChildElementIndex(Element)} instead.
     * 
     * @param subElement
     * @return
     */
    @Deprecated
    public static int getChildElementIndex(Element childElement) {
        return WidgetUtil.getChildElementIndex(childElement);
    }

    private static void printConnectorInvocations(
            ArrayList<MethodInvocation> invocations, String id,
            ApplicationConnection c) {
        ServerConnector connector = ConnectorMap.get(c).getConnector(id);
        if (connector != null) {
            getLogger().info("\t" + id + " (" + connector.getClass() + ") :");
        } else {
            getLogger().warning(
                    "\t" + id + ": Warning: no corresponding connector for id "
                            + id);
        }
        for (MethodInvocation invocation : invocations) {
            Object[] parameters = invocation.getParameters();
            String formattedParams = null;
            if (ApplicationConstants.UPDATE_VARIABLE_METHOD.equals(invocation
                    .getMethodName()) && parameters.length == 2) {
                // name, value
                Object value = parameters[1];
                // TODO paintables inside lists/maps get rendered as
                // components in the debug console
                String formattedValue = value instanceof ServerConnector ? ((ServerConnector) value)
                        .getConnectorId() : String.valueOf(value);
                formattedParams = parameters[0] + " : " + formattedValue;
            }
            if (null == formattedParams) {
                formattedParams = (null != parameters) ? Arrays
                        .toString(parameters) : null;
            }
            getLogger().info(
                    "\t\t" + invocation.getInterfaceName() + "."
                            + invocation.getMethodName() + "("
                            + formattedParams + ")");
        }
    }

    static void logVariableBurst(ApplicationConnection c,
            Collection<MethodInvocation> loggedBurst) {
        try {
            getLogger().info("Variable burst to be sent to server:");
            String curId = null;
            ArrayList<MethodInvocation> invocations = new ArrayList<MethodInvocation>();
            for (MethodInvocation methodInvocation : loggedBurst) {
                String id = methodInvocation.getConnectorId();

                if (curId == null) {
                    curId = id;
                } else if (!curId.equals(id)) {
                    printConnectorInvocations(invocations, curId, c);
                    invocations.clear();
                    curId = id;
                }
                invocations.add(methodInvocation);
            }
            if (!invocations.isEmpty()) {
                printConnectorInvocations(invocations, curId, c);
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error sending variable burst", e);
        }
    }

    /**
     * Temporarily sets the {@code styleProperty} to {@code tempValue} and then
     * resets it to its current value. Used mainly to work around rendering
     * issues in IE (and possibly in other browsers)
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#setStyleTemporarily(Element, String, String)}
     *             instead.
     * 
     * @param element
     *            The target element
     * @param styleProperty
     *            The name of the property to set
     * @param tempValue
     *            The temporary value
     */
    @Deprecated
    public static void setStyleTemporarily(Element element,
            final String styleProperty, String tempValue) {
        WidgetUtil.setStyleTemporarily(element, styleProperty, tempValue);
    }

    /**
     * A helper method to return the client position from an event. Returns
     * position from either first changed touch (if touch event) or from the
     * event itself.
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#getTouchOrMouseClientX(Event)} instead.
     * 
     * @param event
     * @return
     */
    @Deprecated
    public static int getTouchOrMouseClientX(Event event) {
        return WidgetUtil.getTouchOrMouseClientX(event);
    }

    /**
     * Find the element corresponding to the coordinates in the passed mouse
     * event. Please note that this is not always the same as the target of the
     * event e.g. if event capture is used.
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#getElementUnderMouse(NativeEvent)} instead.
     * 
     * @param event
     *            the mouse event to get coordinates from
     * @return the element at the coordinates of the event
     */
    @Deprecated
    public static com.google.gwt.user.client.Element getElementUnderMouse(
            NativeEvent event) {
        return DOM.asOld(WidgetUtil.getElementUnderMouse(event));
    }

    /**
     * A helper method to return the client position from an event. Returns
     * position from either first changed touch (if touch event) or from the
     * event itself.
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#getTouchOrMouseClientY(Event)} instead.
     * 
     * @param event
     * @return
     */
    @Deprecated
    public static int getTouchOrMouseClientY(Event event) {
        return WidgetUtil.getTouchOrMouseClientY(event);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#getTouchOrMouseClientY(NativeEvent)}
     *             instead.
     * 
     * @see #getTouchOrMouseClientY(Event)
     * @param currentGwtEvent
     * @return
     */
    @Deprecated
    public static int getTouchOrMouseClientY(NativeEvent currentGwtEvent) {
        return WidgetUtil.getTouchOrMouseClientY(currentGwtEvent);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#getTouchOrMouseClientX(NativeEvent)}
     *             instead.
     * 
     * @see #getTouchOrMouseClientX(Event)
     * 
     * @param event
     * @return
     */
    @Deprecated
    public static int getTouchOrMouseClientX(NativeEvent event) {
        return WidgetUtil.getTouchOrMouseClientX(event);
    }

    /**
     * @deprecated As of 7.4.0, use {@link WidgetUtil#isTouchEvent(Event)}
     *             instead.
     */
    @Deprecated
    public static boolean isTouchEvent(Event event) {
        return WidgetUtil.isTouchEvent(event);
    }

    /**
     * @deprecated As of 7.4.0, use {@link WidgetUtil#isTouchEvent(NativeEvent)}
     *             instead.
     */
    @Deprecated
    public static boolean isTouchEvent(NativeEvent event) {
        return WidgetUtil.isTouchEvent(event);
    }

    /**
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#simulateClickFromTouchEvent(Event, Widget)}
     *             instead.
     */
    @Deprecated
    public static void simulateClickFromTouchEvent(Event touchevent,
            Widget widget) {
        WidgetUtil.simulateClickFromTouchEvent(touchevent, widget);
    }

    /**
     * Gets the currently focused element.
     * 
     * @deprecated As of 7.4.0, use {@link WidgetUtil#getFocusedElement()}
     *             instead.
     * 
     * @return The active element or null if no active element could be found.
     */
    @Deprecated
    public static com.google.gwt.user.client.Element getFocusedElement() {
        return DOM.asOld(WidgetUtil.getFocusedElement());
    }

    /**
     * Gets the currently focused element for Internet Explorer.
     * 
     * @return The currently focused element
     * @deprecated Use #getFocusedElement instead
     */
    @Deprecated
    public static com.google.gwt.user.client.Element getIEFocusedElement() {
        return getFocusedElement();
    }

    /**
     * Gets currently focused element and checks if it's editable
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#isFocusedElementEditable()} instead.
     * 
     * @since 7.4
     * 
     * @return true if focused element is editable
     */
    @Deprecated
    public static boolean isFocusedElementEditable() {
        return WidgetUtil.isFocusedElementEditable();
    }

    /**
     * Kind of stronger version of isAttached(). In addition to std isAttached,
     * this method checks that this widget nor any of its parents is hidden. Can
     * be e.g used to check whether component should react to some events or
     * not.
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#isAttachedAndDisplayed(Widget)} instead.
     * 
     * @param widget
     * @return true if attached and displayed
     */
    @Deprecated
    public static boolean isAttachedAndDisplayed(Widget widget) {
        return WidgetUtil.isAttachedAndDisplayed(widget);
    }

    /**
     * Scrolls an element into view vertically only. Modified version of
     * Element.scrollIntoView.
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#scrollIntoViewVertically(Element)} instead.
     * 
     * @param elem
     *            The element to scroll into view
     */
    @Deprecated
    public static void scrollIntoViewVertically(Element elem) {
        WidgetUtil.scrollIntoViewVertically(elem);
    }

    /**
     * Checks if the given event is either a touch event or caused by the left
     * mouse button
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#isTouchEventOrLeftMouseButton(Event)}
     *             instead.
     * 
     * @param event
     * @return true if the event is a touch event or caused by the left mouse
     *         button, false otherwise
     */
    @Deprecated
    public static boolean isTouchEventOrLeftMouseButton(Event event) {
        return WidgetUtil.isTouchEventOrLeftMouseButton(event);
    }

    /**
     * Performs a shallow comparison of the collections.
     * 
     * @param collection1
     *            The first collection
     * @param collection2
     *            The second collection
     * @return true if the collections contain the same elements in the same
     *         order, false otherwise
     */
    public static boolean collectionsEquals(Collection<?> collection1,
            Collection<?> collection2) {
        if (collection1 == null) {
            return collection2 == null;
        }
        if (collection2 == null) {
            return false;
        }

        if (collection1.size() != collection2.size()) {
            return false;
        }

        Iterator<?> collection1Iterator = collection1.iterator();
        Iterator<?> collection2Iterator = collection2.iterator();

        while (collection1Iterator.hasNext()) {
            Object collection1Object = collection1Iterator.next();
            Object collection2Object = collection2Iterator.next();
            if (collection1Object != collection2Object) {
                return false;
            }
        }

        return true;
    }

    public static String getConnectorString(ServerConnector p) {
        if (p == null) {
            return "null";
        }
        return getSimpleName(p) + " (" + p.getConnectorId() + ")";
    }

    /**
     * Resolve a relative URL to an absolute URL based on the current document's
     * location.
     * 
     * @deprecated As of 7.4.0, use {@link WidgetUtil#getAbsoluteUrl(String)}
     *             instead.
     * 
     * @param url
     *            a string with the relative URL to resolve
     * @return the corresponding absolute URL as a string
     */
    @Deprecated
    public static String getAbsoluteUrl(String url) {
        return WidgetUtil.getAbsoluteUrl(url);
    }

    /**
     * Sets the selection range of an input element.
     * 
     * We need this JSNI function to set selection range so that we can use the
     * optional direction attribute to set the anchor to the end and the focus
     * to the start. This makes Firefox work the same way as other browsers
     * (#13477)
     * 
     * @deprecated As of 7.4.0, use
     *             {@link WidgetUtil#setSelectionRange(Element, int, int, String)}
     *             instead.
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
    @Deprecated
    public static void setSelectionRange(Element elem, int pos, int length,
            String direction) {
        WidgetUtil.setSelectionRange(elem, pos, length, direction);
    }

    /**
     * Converts a native {@link JavaScriptObject} into a {@link JsonValue}. This
     * is a no-op in GWT code compiled to javascript, but needs some special
     * handling to work when run in JVM.
     * 
     * @param jso
     *            the java script object to represent as json
     * @return the json representation
     */
    public static <T extends JsonValue> T jso2json(JavaScriptObject jso) {
        if (GWT.isProdMode()) {
            return (T) jso.<JsJsonValue> cast();
        } else {
            return elemental.json.Json.instance().parse(stringify(jso));
        }
    }

    /**
     * Converts a {@link JsonValue} into a native {@link JavaScriptObject}. This
     * is a no-op in GWT code compiled to javascript, but needs some special
     * handling to work when run in JVM.
     * 
     * @param jsonValue
     *            the json value
     * @return a native javascript object representation of the json value
     */
    public static JavaScriptObject json2jso(JsonValue jsonValue) {
        if (GWT.isProdMode()) {
            return ((JavaScriptObject) jsonValue.toNative()).cast();
        } else {
            return parse(jsonValue.toJson());
        }
    }

    /**
     * Convert a {@link JavaScriptObject} into a string representation.
     * 
     * @param json
     *            a JavaScript object to be converted to a string
     * @return JSON in string representation
     */
    private native static String stringify(JavaScriptObject json)
    /*-{
        return JSON.stringify(json);
    }-*/;

    /**
     * Parse a string containing JSON into a {@link JavaScriptObject}.
     * 
     * @param <T>
     *            the overlay type to expect from the parse
     * @param jsonAsString
     * @return a JavaScript object constructed from the parse
     */
    public native static <T extends JavaScriptObject> T parse(
            String jsonAsString)
    /*-{
        return JSON.parse(jsonAsString);
    }-*/;

    private static Logger getLogger() {
        return Logger.getLogger(Util.class.getName());
    }
}
