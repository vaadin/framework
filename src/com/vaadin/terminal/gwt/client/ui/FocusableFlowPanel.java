/*
@ITMillApache2LicenseForJavaFiles@
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import com.vaadin.terminal.gwt.client.Focusable;

public class FocusableFlowPanel extends FlowPanel implements HasFocusHandlers,
        HasBlurHandlers, HasKeyDownHandlers, HasKeyPressHandlers, Focusable {

    /**
     * Constructor
     */
    public FocusableFlowPanel() {
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
     * Sets/Removes the keyboard focus to the panel.
     * 
     * @param focus
     *            If set to true then the focus is moved to the panel, if set to
     *            false the focus is removed
     */
    public void setFocus(boolean focus) {
        if (focus) {
            FocusImpl.getFocusImplForPanel().focus(getElement());
        } else {
            FocusImpl.getFocusImplForPanel().blur(getElement());
        }
    }

    /**
     * Focus the panel
     */
    public void focus() {
        setFocus(true);
    }
}