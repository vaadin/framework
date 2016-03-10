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
package com.vaadin.ui;

import java.io.Serializable;

import com.vaadin.shared.ui.ui.UIState.TooltipConfigurationState;

/**
 * Provides method for configuring the tooltip.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public interface TooltipConfiguration extends Serializable {
    /**
     * Returns the time (in ms) the tooltip should be displayed after an event
     * that will cause it to be closed (e.g. mouse click outside the component,
     * key down).
     * 
     * @return The close timeout
     */
    public int getCloseTimeout();

    /**
     * Sets the time (in ms) the tooltip should be displayed after an event that
     * will cause it to be closed (e.g. mouse click outside the component, key
     * down).
     * 
     * @param closeTimeout
     *            The close timeout
     */
    public void setCloseTimeout(int closeTimeout);

    /**
     * Returns the time (in ms) during which {@link #getQuickOpenDelay()} should
     * be used instead of {@link #getOpenDelay()}. The quick open delay is used
     * when the tooltip has very recently been shown, is currently hidden but
     * about to be shown again.
     * 
     * @return The quick open timeout
     */
    public int getQuickOpenTimeout();

    /**
     * Sets the time (in ms) that determines when {@link #getQuickOpenDelay()}
     * should be used instead of {@link #getOpenDelay()}. The quick open delay
     * is used when the tooltip has very recently been shown, is currently
     * hidden but about to be shown again.
     * 
     * @param quickOpenTimeout
     *            The quick open timeout
     */
    public void setQuickOpenTimeout(int quickOpenTimeout);

    /**
     * Returns the time (in ms) that should elapse before a tooltip will be
     * shown, in the situation when a tooltip has very recently been shown
     * (within {@link #getQuickOpenDelay()} ms).
     * 
     * @return The quick open delay
     */
    public int getQuickOpenDelay();

    /**
     * Sets the time (in ms) that should elapse before a tooltip will be shown,
     * in the situation when a tooltip has very recently been shown (within
     * {@link #getQuickOpenDelay()} ms).
     * 
     * @param quickOpenDelay
     *            The quick open delay
     */
    public void setQuickOpenDelay(int quickOpenDelay);

    /**
     * Returns the time (in ms) that should elapse after an event triggering
     * tooltip showing has occurred (e.g. mouse over) before the tooltip is
     * shown. If a tooltip has recently been shown, then
     * {@link #getQuickOpenDelay()} is used instead of this.
     * 
     * @return The open delay
     */
    public int getOpenDelay();

    /**
     * Sets the time (in ms) that should elapse after an event triggering
     * tooltip showing has occurred (e.g. mouse over) before the tooltip is
     * shown. If a tooltip has recently been shown, then
     * {@link #getQuickOpenDelay()} is used instead of this.
     * 
     * @param openDelay
     *            The open delay
     */
    public void setOpenDelay(int openDelay);

    /**
     * Returns the maximum width of the tooltip popup.
     * 
     * @return The maximum width the tooltip popup
     */
    public int getMaxWidth();

    /**
     * Sets the maximum width of the tooltip popup.
     * 
     * @param maxWidth
     *            The maximum width the tooltip popup
     */
    public void setMaxWidth(int maxWidth);
}

class TooltipConfigurationImpl implements TooltipConfiguration {
    private UI ui;

    public TooltipConfigurationImpl(UI ui) {
        this.ui = ui;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.UI.Tooltip#getCloseTimeout()
     */
    @Override
    public int getCloseTimeout() {
        return getState(false).closeTimeout;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Tooltip#setCloseTimeout(int)
     */
    @Override
    public void setCloseTimeout(int closeTimeout) {
        getState().closeTimeout = closeTimeout;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Tooltip#getQuickOpenTimeout()
     */
    @Override
    public int getQuickOpenTimeout() {
        return getState(false).quickOpenTimeout;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Tooltip#setQuickOpenTimeout(int)
     */
    @Override
    public void setQuickOpenTimeout(int quickOpenTimeout) {
        getState().quickOpenTimeout = quickOpenTimeout;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Tooltip#getQuickOpenDelay()
     */
    @Override
    public int getQuickOpenDelay() {
        return getState(false).quickOpenDelay;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Tooltip#setQuickOpenDelay(int)
     */
    @Override
    public void setQuickOpenDelay(int quickOpenDelay) {
        getState().quickOpenDelay = quickOpenDelay;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Tooltip#getOpenDelay()
     */
    @Override
    public int getOpenDelay() {
        return getState(false).openDelay;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Tooltip#setOpenDelay(int)
     */
    @Override
    public void setOpenDelay(int openDelay) {
        getState().openDelay = openDelay;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Tooltip#getMaxWidth()
     */
    @Override
    public int getMaxWidth() {
        return getState(false).maxWidth;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Tooltip#setMaxWidth(int)
     */
    @Override
    public void setMaxWidth(int maxWidth) {
        getState().maxWidth = maxWidth;
    }

    private TooltipConfigurationState getState() {
        return ui.getState().tooltipConfiguration;
    }

    private TooltipConfigurationState getState(boolean markAsDirty) {
        return ui.getState(markAsDirty).tooltipConfiguration;
    }

}
