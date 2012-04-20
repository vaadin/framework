/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import com.vaadin.terminal.gwt.client.BrowserInfo;

/**
 * A scrollhandlers similar to {@link ScrollPanel}.
 * 
 */
public class FocusableScrollPanel extends SimpleFocusablePanel implements
        HasScrollHandlers, ScrollHandler {

    public FocusableScrollPanel() {
        // Prevent IE standard mode bug when a AbsolutePanel is contained.
        Style style = getElement().getStyle();
        style.setOverflow(Overflow.AUTO);
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
                if (BrowserInfo.get().isIE6()) {
                    style.setOverflow(Overflow.HIDDEN);
                    style.setHeight(0, Unit.PX);
                    style.setWidth(0, Unit.PX);
                    style.setPosition(Position.ABSOLUTE);

                    addScrollHandler(this);
                } else {
                    style.setPosition(Position.FIXED);
                    style.setTop(0, Unit.PX);
                    style.setLeft(0, Unit.PX);
                }
                getElement().appendChild(focusElement);
                /* Sink from focusElemet too as focusa and blur don't bubble */
                DOM.sinkEvents(
                        (com.google.gwt.user.client.Element) focusElement
                                .cast(), Event.FOCUSEVENTS);
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
                FocusImpl.getFocusImplForPanel().focus(
                        (Element) focusElement.cast());
            } else {
                FocusImpl.getFocusImplForPanel().blur(
                        (Element) focusElement.cast());
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
        return getElement().getScrollTop();
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
        getElement().setScrollTop(position);
    }

    public void onScroll(ScrollEvent event) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            public void execute() {
                focusElement.getStyle().setTop(getScrollPosition(), Unit.PX);
                focusElement.getStyle().setLeft(getHorizontalScrollPosition(),
                        Unit.PX);
            }
        });
    }

}
