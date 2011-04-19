package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import com.vaadin.terminal.gwt.client.Focusable;

/**
 * Compared to FocusPanel in GWT this panel does not support eg. accesskeys, but
 * is simpler by its dom hierarchy nor supports focusing via java api.
 */
public class SimpleFocusablePanel extends SimplePanel implements
        HasFocusHandlers, HasBlurHandlers, HasKeyDownHandlers,
        HasKeyPressHandlers, Focusable {

    public SimpleFocusablePanel() {
        // make focusable, as we don't need access key magic we don't need to
        // use FocusImpl.createFocusable
        setTabIndex(0);
    }

    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return addDomHandler(handler, FocusEvent.getType());
    }

    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return addDomHandler(handler, BlurEvent.getType());
    }

    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return addDomHandler(handler, KeyDownEvent.getType());
    }

    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return addDomHandler(handler, KeyPressEvent.getType());
    }

    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return addDomHandler(handler, KeyUpEvent.getType());
    }

    public void setFocus(boolean focus) {
        if (focus) {
            FocusImpl.getFocusImplForPanel().focus(getElement());
        } else {
            FocusImpl.getFocusImplForPanel().blur(getElement());
        }
    }

    public void focus() {
        setFocus(true);
    }

    public void setTabIndex(int tabIndex) {
        getElement().setTabIndex(tabIndex);
    }
}
