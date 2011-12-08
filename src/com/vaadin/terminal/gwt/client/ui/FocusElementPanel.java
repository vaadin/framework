/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import com.vaadin.terminal.gwt.client.BrowserInfo;

/**
 * A panel that contains an always visible 0x0 size element that holds the focus
 * for all browsers but IE6.
 */
public class FocusElementPanel extends SimpleFocusablePanel {

    private DivElement focusElement;

    public FocusElementPanel() {
        focusElement = Document.get().createDivElement();
    }

    @Override
    public void setWidget(Widget w) {
        super.setWidget(w);
        if (!BrowserInfo.get().isIE6()) {
            if (focusElement.getParentElement() == null) {
                Style style = focusElement.getStyle();
                style.setPosition(Position.FIXED);
                style.setTop(0, Unit.PX);
                style.setLeft(0, Unit.PX);
                getElement().appendChild(focusElement);
                /* Sink from focusElement too as focus and blur don't bubble */
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
        if (BrowserInfo.get().isIE6()) {
            super.setFocus(focus);
        } else {
            if (focus) {
                FocusImpl.getFocusImplForPanel().focus(
                        (Element) focusElement.cast());
            } else {
                FocusImpl.getFocusImplForPanel().blur(
                        (Element) focusElement.cast());
            }
        }
    }

    @Override
    public void setTabIndex(int tabIndex) {
        if (BrowserInfo.get().isIE6()) {
            super.setTabIndex(tabIndex);
        } else {
            getElement().setTabIndex(-1);
            if (focusElement != null) {
                focusElement.setTabIndex(tabIndex);
            }
        }
    }

    /**
     * @return the focus element
     */
    public Element getFocusElement() {
        return focusElement.cast();
    }
}
