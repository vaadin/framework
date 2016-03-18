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

package com.vaadin.client.ui.progressindicator;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.VProgressBar;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.progressindicator.ProgressBarState;
import com.vaadin.ui.ProgressBar;

/**
 * Connector for {@link VProgressBar}.
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
@Connect(ProgressBar.class)
public class ProgressBarConnector extends AbstractFieldConnector {

    public ProgressBarConnector() {
        super();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        getWidget().setIndeterminate(getState().indeterminate);
        getWidget().setState(getState().state);
    }

    @Override
    public ProgressBarState getState() {
        return (ProgressBarState) super.getState();
    }

    @Override
    public VProgressBar getWidget() {
        return (VProgressBar) super.getWidget();
    }

}
