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
package com.vaadin.client.ui.calendar.schedule;

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
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import com.vaadin.client.Focusable;

/**
 * A Grid that can be focused
 * 
 * @since 7.1
 * @author Vaadin Ltd.
 * 
 */
public class FocusableGrid extends Grid implements HasFocusHandlers,
        HasBlurHandlers, HasKeyDownHandlers, HasKeyPressHandlers, Focusable {

    /**
     * Constructor
     */
    public FocusableGrid() {
        super();
        makeFocusable();
    }

    public FocusableGrid(int rows, int columns) {
        super(rows, columns);
        makeFocusable();
    }

    protected void makeFocusable() {
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public void focus() {
        setFocus(true);
    }
}
