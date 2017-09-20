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
package com.vaadin.tests.widgetset.client;

import java.util.List;

import com.google.gwt.user.client.ui.Label;
import com.vaadin.client.metadata.ConnectorBundleLoader;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.ConnectorBundleStatusDisplay;

@Connect(ConnectorBundleStatusDisplay.class)
public class ConnectorBundleStatusDisplayConnector extends AbstractComponentConnector {
    @Override
    public Label getWidget() {
        return (Label) super.getWidget();
    }

    @Override
    protected void init() {
        super.init();
        registerRpc(ConnectorBundleStatusRpc.class,
                new ConnectorBundleStatusRpc() {
                    @Override
                    public void updateStatus() {
                        ConnectorBundleStatusDisplayConnector.this.updateStatus();
                    }
                });

        updateStatus();
    }

    private void updateStatus() {
        List<String> bundles = ConnectorBundleLoader.get().getLoadedBundles();
        getWidget().setText(bundles.toString());
    }
}
