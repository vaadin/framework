/*
@VaadinApache2LicenseForJavaFiles@
 */
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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import com.vaadin.terminal.gwt.client.Focusable;

/**
 * Adds keyboard focus to {@link FlexPanel}.
 */
public class FocusableFlexTable extends FlexTable implements HasFocusHandlers,
        HasBlurHandlers, HasKeyDownHandlers, HasKeyPressHandlers, Focusable {

    /**
     * Default constructor.
     */
    public FocusableFlexTable() {
        // make focusable, as we don't need access key magic we don't need to
        // use FocusImpl.createFocusable
        getElement().setTabIndex(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.HasFocusHandlers#addFocusHandler(com.
     * google.gwt.event.dom.client.FocusHandler)
     */
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return addDomHandler(handler, FocusEvent.getType());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.HasBlurHandlers#addBlurHandler(com.google
     * .gwt.event.dom.client.BlurHandler)
     */
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return addDomHandler(handler, BlurEvent.getType());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.HasKeyDownHandlers#addKeyDownHandler(
     * com.google.gwt.event.dom.client.KeyDownHandler)
     */
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return addDomHandler(handler, KeyDownEvent.getType());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.HasKeyPressHandlers#addKeyPressHandler
     * (com.google.gwt.event.dom.client.KeyPressHandler)
     */
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return addDomHandler(handler, KeyPressEvent.getType());
    }

    /**
     * Sets the keyboard focus to the panel
     * 
     * @param focus
     *            Should the panel have keyboard focus. If true the keyboard
     *            focus will be moved to the
     */
    public void setFocus(boolean focus) {
        if (focus) {
            FocusImpl.getFocusImplForPanel().focus(getElement());
        } else {
            FocusImpl.getFocusImplForPanel().blur(getElement());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.Focusable#focus()
     */
    public void focus() {
        setFocus(true);
    }
}
