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
package com.vaadin.tests.widgetset.client.minitutorials.v7a3;

import com.google.gwt.user.client.Timer;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.minitutorials.v7a3.Refresher;

@Connect(Refresher.class)
public class RefresherConnector extends AbstractExtensionConnector {

    private Timer timer = new Timer() {
        @Override
        public void run() {
            getRpcProxy(RefresherRpc.class).refresh();
        }
    };

    @Override
    public void onStateChanged(StateChangeEvent event) {
        super.onStateChanged(event);
        timer.cancel();
        if (isEnabled()) {
            timer.scheduleRepeating(getState().interval);
        }
    }

    @Override
    public void onUnregister() {
        timer.cancel();
    }

    @Override
    protected void extend(ServerConnector target) {
        // Nothing for refresher to do here as it does not need to access the
        // connector it extends
    }

    @Override
    public RefresherState getState() {
        return (RefresherState) super.getState();
    }
}
