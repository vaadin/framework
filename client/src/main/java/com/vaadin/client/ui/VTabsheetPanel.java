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

package com.vaadin.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.TouchScrollDelegate.TouchScrollHandler;

/**
 * A panel that displays all of its child widgets in a 'deck', where only one
 * can be visible at a time. It is used by
 * {@link com.vaadin.client.ui.VTabsheet}.
 * 
 * This class has the same basic functionality as the GWT DeckPanel
 * {@link com.google.gwt.user.client.ui.DeckPanel}, with the exception that it
 * doesn't manipulate the child widgets' width and height attributes.
 */
public class VTabsheetPanel extends ComplexPanel {

    private Widget visibleWidget;

    private final TouchScrollHandler touchScrollHandler;

    /**
     * Creates an empty tabsheet panel.
     */
    public VTabsheetPanel() {
        setElement(DOM.createDiv());
        touchScrollHandler = TouchScrollDelegate.enableTouchScrolling(this);
    }

    /**
     * Adds the specified widget to the deck.
     * 
     * @param w
     *            the widget to be added
     */
    @Override
    public void add(Widget w) {
        Element el = createContainerElement();
        DOM.appendChild(getElement(), el);
        super.add(w, el);
    }

    private Element createContainerElement() {
        Element el = DOM.createDiv();
        el.getStyle().setPosition(Position.ABSOLUTE);
        hide(el);
        touchScrollHandler.addElement(el);
        return el;
    }

    /**
     * Gets the index of the currently-visible widget.
     * 
     * @return the visible widget's index
     */
    public int getVisibleWidget() {
        return getWidgetIndex(visibleWidget);
    }

    /**
     * Inserts a widget before the specified index.
     * 
     * @param w
     *            the widget to be inserted
     * @param beforeIndex
     *            the index before which it will be inserted
     * @throws IndexOutOfBoundsException
     *             if <code>beforeIndex</code> is out of range
     */
    public void insert(Widget w, int beforeIndex) {
        Element el = createContainerElement();
        DOM.insertChild(getElement(), el, beforeIndex);
        super.insert(w, el, beforeIndex, false);
    }

    @Override
    public boolean remove(Widget w) {
        Element child = w.getElement();
        Element parent = null;
        if (child != null) {
            parent = DOM.getParent(child);
        }
        final boolean removed = super.remove(w);
        if (removed) {
            if (visibleWidget == w) {
                visibleWidget = null;
            }
            if (parent != null) {
                DOM.removeChild(getElement(), parent);
            }
            touchScrollHandler.removeElement(parent);
        }
        return removed;
    }

    /**
     * Shows the widget at the specified index. This causes the currently-
     * visible widget to be hidden.
     * 
     * @param index
     *            the index of the widget to be shown
     */
    public void showWidget(int index) {
        checkIndexBoundsForAccess(index);
        Widget newVisible = getWidget(index);
        if (visibleWidget != newVisible) {
            if (visibleWidget != null) {
                hide(DOM.getParent(visibleWidget.getElement()));
            }
            visibleWidget = newVisible;
            touchScrollHandler.setElements(visibleWidget.getElement()
                    .getParentElement());
        }
        // Always ensure the selected tab is visible. If server prevents a tab
        // change we might end up here with visibleWidget == newVisible but its
        // parent is still hidden.
        unHide(DOM.getParent(visibleWidget.getElement()));
    }

    private void hide(Element e) {
        e.getStyle().setVisibility(Visibility.HIDDEN);
        e.getStyle().setTop(-100000, Unit.PX);
        e.getStyle().setLeft(-100000, Unit.PX);
    }

    private void unHide(Element e) {
        e.getStyle().setTop(0, Unit.PX);
        e.getStyle().setLeft(0, Unit.PX);
        e.getStyle().clearVisibility();
    }

    public void fixVisibleTabSize(int width, int height, int minWidth) {
        if (visibleWidget == null) {
            return;
        }

        boolean dynamicHeight = false;

        if (height < 0) {
            height = visibleWidget.getOffsetHeight();
            dynamicHeight = true;
        }
        if (width < 0) {
            width = visibleWidget.getOffsetWidth();
        }
        if (width < minWidth) {
            width = minWidth;
        }

        Element wrapperDiv = visibleWidget.getElement().getParentElement();

        // width first
        getElement().getStyle().setPropertyPx("width", width);
        wrapperDiv.getStyle().setPropertyPx("width", width);

        if (dynamicHeight) {
            // height of widget might have changed due wrapping
            height = visibleWidget.getOffsetHeight();
        }
        // v-tabsheet-tabsheetpanel height
        getElement().getStyle().setPropertyPx("height", height);

        // widget wrapper height
        if (dynamicHeight) {
            wrapperDiv.getStyle().clearHeight();
        } else {
            // widget wrapper height
            wrapperDiv.getStyle().setPropertyPx("height", height);
        }
    }

    public void replaceComponent(Widget oldComponent, Widget newComponent) {
        boolean isVisible = (visibleWidget == oldComponent);
        int widgetIndex = getWidgetIndex(oldComponent);
        remove(oldComponent);
        insert(newComponent, widgetIndex);
        if (isVisible) {
            showWidget(widgetIndex);
        }
    }
}
