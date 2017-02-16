/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.client.widgets;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.vaadin.client.Focusable;

/**
 * Focusable composite whose widget is {@link ChildFocusAwareFlowPanel} (flow
 * panel that tracks focus/blur events from its children).
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public abstract class FocusableFlowPanelComposite extends Composite
        implements HasAllFocusHandlers, Focusable {

    private final ChildFocusAwareFlowPanel panel;

    /**
     * Creates a new instance.
     */
    protected FocusableFlowPanelComposite() {
        panel = new ChildFocusAwareFlowPanel();
        initWidget(panel);
    }

    @Override
    protected final ChildFocusAwareFlowPanel getWidget() {
        return (ChildFocusAwareFlowPanel) super.getWidget();
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return panel.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return panel.addBlurHandler(handler);
    }

    @Override
    public void focus() {
        getWidget().focus();
    }
}
