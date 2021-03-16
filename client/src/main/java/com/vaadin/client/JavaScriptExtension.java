/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client;

import com.vaadin.client.communication.HasJavaScriptConnectorHelper;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.shared.JavaScriptExtensionState;
import com.vaadin.shared.ui.Connect;

@Connect(AbstractJavaScriptExtension.class)
public final class JavaScriptExtension extends AbstractExtensionConnector
        implements HasJavaScriptConnectorHelper {
    private final JavaScriptConnectorHelper helper = new JavaScriptConnectorHelper(
            this);

    @Override
    protected void init() {
        super.init();
        helper.init();
    }

    @Override
    public JavaScriptConnectorHelper getJavascriptConnectorHelper() {
        return helper;
    }

    @Override
    public JavaScriptExtensionState getState() {
        return (JavaScriptExtensionState) super.getState();
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        helper.onUnregister();
    }

    @Override
    protected void extend(ServerConnector target) {
        // Nothing to do for JavaScriptExtension here. Everything is done in
        // javascript.
    }
}
