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

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import com.vaadin.client.BrowserInfo;

/**
 * A scrollhandlers similar to {@link ScrollPanel}.
 * 
 */
public class FocusableScrollPanel extends SimpleFocusablePanel implements
        HasScrollHandlers, ScrollHandler {

    public FocusableScrollPanel() {
        // Prevent IE standard mode bug when a AbsolutePanel is contained.
        TouchScrollDelegate.enableTouchScrolling(this, getElement());
        Style style = getElement().getStyle();
        style.setProperty("zoom", "1");
        style.setPosition(Position.RELATIVE);
    }

    private DivElement focusElement;

    public FocusableScrollPanel(boolean useFakeFocusElement) {
        this();
        if (useFakeFocusElement) {
            focusElement = Document.get().createDivElement();
        }
    }

    private boolean useFakeFocusElement() {
        return focusElement != null;
    }

    @Override
    public void setWidget(Widget w) {
        super.setWidget(w);
        if (useFakeFocusElement()) {
            if (focusElement.getParentElement() == null) {
                Style style = focusElement.getStyle();
                style.setPosition(Position.FIXED);
                style.setTop(0, Unit.PX);
                style.setLeft(0, Unit.PX);
                getElement().appendChild(focusElement);
                /* Sink from focusElemet too as focusa and blur don't bubble */
                DOM.sinkEvents(focusElement, Event.FOCUSEVENTS);
                // revert to original, not focusable
                getElement().setPropertyObject("tabIndex", null);

            } else {
                moveFocusElementAfterWidget();
            }
        }
    }

    /**
     * Helper to keep focus element always in domChild[1]. Aids testing.
     */
    private void moveFocusElementAfterWidget() {
        getElement().insertAfter(focusElement, getWidget().getElement());
    }

    @Override
    public void setFocus(boolean focus) {
        if (useFakeFocusElement()) {
            if (focus) {
                FocusImpl.getFocusImplForPanel().focus(focusElement);
            } else {
                FocusImpl.getFocusImplForPanel().blur(focusElement);
            }
        } else {
            super.setFocus(focus);
        }
    }

    @Override
    public void setTabIndex(int tabIndex) {
        if (useFakeFocusElement()) {
            getElement().setTabIndex(-1);
            if (focusElement != null) {
                focusElement.setTabIndex(tabIndex);
            }
        } else {
            super.setTabIndex(tabIndex);
        }
    }

    @Override
    public HandlerRegistration addScrollHandler(ScrollHandler handler) {
        return addDomHandler(handler, ScrollEvent.getType());
    }

    /**
     * Gets the horizontal scroll position.
     * 
     * @return the horizontal scroll position, in pixels
     */
    public int getHorizontalScrollPosition() {
        return getElement().getScrollLeft();
    }

    /**
     * Gets the vertical scroll position.
     * 
     * @return the vertical scroll position, in pixels
     */
    public int getScrollPosition() {
        if (getElement().getPropertyJSO("_vScrollTop") != null) {
            return getElement().getPropertyInt("_vScrollTop");
        } else {
            return getElement().getScrollTop();
        }
    }

    /**
     * Sets the horizontal scroll position.
     * 
     * @param position
     *            the new horizontal scroll position, in pixels
     */
    public void setHorizontalScrollPosition(int position) {
        getElement().setScrollLeft(position);
    }

    /**
     * Sets the vertical scroll position.
     * 
     * @param position
     *            the new vertical scroll position, in pixels
     */
    public void setScrollPosition(int position) {
        if (BrowserInfo.get().isAndroidWithBrokenScrollTop()
                && BrowserInfo.get().requiresTouchScrollDelegate()) {
            ArrayList<com.google.gwt.dom.client.Element> elements = TouchScrollDelegate
                    .getElements(getElement());
            for (com.google.gwt.dom.client.Element el : elements) {
                final Style style = el.getStyle();
                style.setProperty("webkitTransform", "translate3d(0px,"
                        + -position + "px,0px)");
            }
            getElement().setPropertyInt("_vScrollTop", position);
        } else {
            getElement().setScrollTop(position);
        }
    }

    @Override
    public void onScroll(ScrollEvent event) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                focusElement.getStyle().setTop(getScrollPosition(), Unit.PX);
                focusElement.getStyle().setLeft(getHorizontalScrollPosition(),
                        Unit.PX);
            }
        });
    }

    public com.google.gwt.user.client.Element getFocusElement() {
        if (useFakeFocusElement()) {
            return focusElement.cast();
        } else {
            return getElement();
        }
    }

}
