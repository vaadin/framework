/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.client.ui.colorpicker;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.colorpicker.AbstractColorPickerState;

/**
 * An abstract class that defines default implementation for a color picker
 * connector.
 *
 * @since 7.0.0
 */
public abstract class AbstractColorPickerConnector
        extends AbstractComponentConnector implements ClickHandler {

    private static final String DEFAULT_WIDTH_STYLE = "v-default-caption-width";

    @Override
    public AbstractColorPickerState getState() {
        return (AbstractColorPickerState) super.getState();
    }

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        // NOTE: this method is called after @DelegateToWidget
        super.onStateChanged(stateChangeEvent);
        if (stateChangeEvent.hasPropertyChanged("color")) {
            refreshColor();

            if (getState().showDefaultCaption && (getState().caption == null
                    || getState().caption.isEmpty())) {

                setCaption(getState().color);
            }
        }
        if (stateChangeEvent.hasPropertyChanged("caption")
                || stateChangeEvent.hasPropertyChanged("htmlContentAllowed")
                || stateChangeEvent.hasPropertyChanged("showDefaultCaption")) {

            setCaption(getCaption());
            refreshDefaultCaptionStyle();
        }
    }

    @Override
    public void init() {
        super.init();
        if (getWidget() instanceof HasClickHandlers) {
            ((HasClickHandlers) getWidget()).addClickHandler(this);
        }
    }

    /**
     * Get caption for the color picker widget.
     *
     * @return the caption
     */
    protected String getCaption() {
        if (getState().showDefaultCaption && (getState().caption == null
                || getState().caption.isEmpty())) {
            return getState().color;
        }
        return getState().caption;
    }

    /**
     * Add/remove default caption style.
     */
    protected void refreshDefaultCaptionStyle() {
        if (getState().showDefaultCaption
                && (getState().caption == null || getState().caption.isEmpty())
                && getState().width.isEmpty()) {
            getWidget().addStyleName(DEFAULT_WIDTH_STYLE);
        } else {
            getWidget().removeStyleName(DEFAULT_WIDTH_STYLE);
        }
    }

    /**
     * Set caption of the color picker widget.
     *
     * @param caption
     *            the caption to set
     */
    protected abstract void setCaption(String caption);

    /**
     * Update the widget to show the currently selected color.
     */
    protected abstract void refreshColor();

}
