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
package com.vaadin.client.debug.internal;

import java.util.HashSet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.ui.VWindow;

/**
 * Highlights a widget in the UI by overlaying a semi-transparent colored div.
 * <p>
 * Multiple highlights can be added, then selectively removed with
 * {@link #hide(Element)} or all at once with {@link #hideAll()}.
 * </p>
 * <p>
 * Note that highlights are intended to be short-term; highlights do not move or
 * disappear with the highlighted widget, and it is also fairly likely that
 * someone else calls {@link #hideAll()} eventually.
 * </p>
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public class Highlight {

    private static final String DEFAULT_COLOR = "red";
    private static final double DEFAULT_OPACITY = 0.3;
    private static final int MIN_WIDTH = 3;
    private static final int MIN_HEIGHT = 3;

    static HashSet<Element> highlights;

    /**
     * Highlight the {@link Widget} for the given {@link ComponentConnector}.
     * <p>
     * Pass the returned {@link Element} to {@link #hide(Element)} to remove
     * this particular highlight.
     * </p>
     * 
     * @param connector
     *            ComponentConnector
     * @return Highlight element
     */
    static Element show(ComponentConnector connector) {
        return show(connector, DEFAULT_COLOR);
    }

    /**
     * Highlight the {@link Widget} for the given connector if it is a
     * {@link ComponentConnector}. Hide any other highlight.
     * <p>
     * Pass the returned {@link Element} to {@link #hide(Element)} to remove
     * this particular highlight.
     * </p>
     * 
     * @since 7.1
     * 
     * @param connector
     *            the server connector to highlight
     * @return Highlight element, or <code>null</code> if the connector isn't a
     *         component
     */
    static Element showOnly(ServerConnector connector) {
        hideAll();
        if (connector instanceof ComponentConnector) {
            return show((ComponentConnector) connector);
        } else {
            return null;
        }
    }

    /**
     * Highlights the {@link Widget} for the given {@link ComponentConnector}
     * using the given color.
     * <p>
     * Pass the returned {@link Element} to {@link #hide(Element)} to remove
     * this particular highlight.
     * </p>
     * 
     * @param connector
     *            ComponentConnector
     * @param color
     *            Color of highlight
     * @return Highlight element
     */
    static Element show(ComponentConnector connector, String color) {
        if (connector != null) {
            Widget w = connector.getWidget();
            return show(w, color);
        }
        return null;
    }

    /**
     * Highlights the given {@link Widget}.
     * <p>
     * Pass the returned {@link Element} to {@link #hide(Element)} to remove
     * this particular highlight.
     * </p>
     * 
     * @param widget
     *            Widget to highlight
     * @return Highlight element
     */
    static Element show(Widget widget) {
        return show(widget, DEFAULT_COLOR);
    }

    /**
     * Highlight the given {@link Widget} using the given color.
     * <p>
     * Pass the returned {@link Element} to {@link #hide(Element)} to remove
     * this particular highlight.
     * </p>
     * 
     * @param widget
     *            Widget to highlight
     * @param color
     *            Color of highlight
     * @return Highlight element
     */
    static Element show(Widget widget, String color) {
        if (widget != null) {
            show(widget.getElement(), color);
        }
        return null;
    }

    /**
     * Highlights the given {@link Element}.
     * <p>
     * Pass the returned {@link Element} to {@link #hide(Element)} to remove
     * this particular highlight.
     * </p>
     * 
     * @param element
     *            Element to highlight
     * @return Highlight element
     */
    static Element show(Element element) {
        return show(element, DEFAULT_COLOR);
    }

    /**
     * Highlight the given {@link Element} using the given color.
     * <p>
     * Pass the returned highlight {@link Element} to {@link #hide(Element)} to
     * remove this particular highlight.
     * </p>
     * 
     * @param element
     *            Element to highlight
     * @param color
     *            Color of highlight
     * @return Highlight element
     */
    static Element show(Element element, String color) {
        if (element != null) {
            if (highlights == null) {
                highlights = new HashSet<Element>();
            }

            Element highlight = DOM.createDiv();
            Style style = highlight.getStyle();
            style.setTop(element.getAbsoluteTop(), Unit.PX);
            style.setLeft(element.getAbsoluteLeft(), Unit.PX);
            int width = element.getOffsetWidth();
            if (width < MIN_WIDTH) {
                width = MIN_WIDTH;
            }
            style.setWidth(width, Unit.PX);
            int height = element.getOffsetHeight();
            if (height < MIN_HEIGHT) {
                height = MIN_HEIGHT;
            }
            style.setHeight(height, Unit.PX);
            RootPanel.getBodyElement().appendChild(highlight);

            style.setPosition(Position.ABSOLUTE);
            style.setZIndex(VWindow.Z_INDEX + 1000);
            style.setBackgroundColor(color);
            style.setOpacity(DEFAULT_OPACITY);
            if (BrowserInfo.get().isIE()) {
                style.setProperty("filter", "alpha(opacity="
                        + (DEFAULT_OPACITY * 100) + ")");
            }

            highlights.add(highlight);

            return highlight;
        }
        return null;
    }

    /**
     * Hides the given highlight.
     * 
     * @param highlight
     *            Highlight to hide
     */
    static void hide(Element highlight) {
        if (highlight != null && highlight.getParentElement() != null) {
            highlight.getParentElement().removeChild(highlight);
            highlights.remove(highlight);
        }
    }

    /**
     * Hides all highlights
     */
    static void hideAll() {
        if (highlights != null) {
            for (Element highlight : highlights) {
                if (highlight.getParentElement() != null) {
                    highlight.getParentElement().removeChild(highlight);
                }
            }
            highlights = null;
        }
    }

}
