/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import com.vaadin.shared.ui.ui.UIState.LoadingIndicatorConfiguration;

/**
 * Provides method for configuring the loading indicator.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public interface LoadingIndicator extends Serializable {
    /**
     * Sets the delay before the loading indicator is shown. The default is
     * 300ms.
     * 
     * @param initialDelay
     *            The initial delay (in ms)
     */
    public void setInitialDelay(int initialDelay);

    /**
     * Returns the delay before the loading indicator is shown.
     * 
     * @return The initial delay (in ms)
     */
    public int getInitialDelay();

    /**
     * Sets the delay before the loading indicator goes into the "delay" state.
     * The delay is calculated from the time when the loading indicator was
     * triggered. The default is 1500ms.
     * 
     * @param delayStateDelay
     *            The delay before going into the "delay" state (in ms)
     */
    public void setDelayStateDelay(int delayStateDelay);

    /**
     * Returns the delay before the loading indicator goes into the "delay"
     * state. The delay is calculated from the time when the loading indicator
     * was triggered.
     * 
     * @return The delay before going into the "delay" state (in ms)
     */
    public int getDelayStateDelay();

    /**
     * Sets the delay before the loading indicator goes into the "wait" state.
     * The delay is calculated from the time when the loading indicator was
     * triggered. The default is 5000ms.
     * 
     * @param waitStateDelay
     *            The delay before going into the "wait" state (in ms)
     */
    public void setWaitStateDelay(int waitStateDelay);

    /**
     * Returns the delay before the loading indicator goes into the "wait"
     * state. The delay is calculated from the time when the loading indicator
     * was triggered.
     * 
     * @return The delay before going into the "wait" state (in ms)
     */
    public int getWaitStateDelay();
}

class LoadingIndicatorImpl implements LoadingIndicator {
    private UI ui;

    public LoadingIndicatorImpl(UI ui) {
        this.ui = ui;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.LoadingIndicator#setInitialDelay(int)
     */
    @Override
    public void setInitialDelay(int initialDelay) {
        getState().initialDelay = initialDelay;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.LoadingIndicator#getInitialDelay()
     */
    @Override
    public int getInitialDelay() {
        return getState(false).initialDelay;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.LoadingIndicator#setDelayStateDelay(int)
     */
    @Override
    public void setDelayStateDelay(int delayStateDelay) {
        getState().delayStateDelay = delayStateDelay;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.LoadingIndicator#getDelayStateDelay()
     */
    @Override
    public int getDelayStateDelay() {
        return getState(false).delayStateDelay;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.LoadingIndicator#setWaitStateDelay(int)
     */
    @Override
    public void setWaitStateDelay(int waitStateDelay) {
        getState().waitStateDelay = waitStateDelay;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.LoadingIndicator#getWaitStateDelay()
     */
    @Override
    public int getWaitStateDelay() {
        return getState(false).waitStateDelay;
    }

    private LoadingIndicatorConfiguration getState() {
        return ui.getState().loadingIndicatorConfiguration;
    }

    private LoadingIndicatorConfiguration getState(boolean markAsDirty) {
        return ui.getState(markAsDirty).loadingIndicatorConfiguration;
    }

}
