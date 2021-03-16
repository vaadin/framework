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
package com.vaadin.client.ui;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;

/**
 * Connector used as a placeholder for extensions that are not present in the
 * widgetset.
 * 
 * @since 7.7.4
 * @author Vaadin Ltd
 */
public class UnknownExtensionConnector extends AbstractExtensionConnector {
    @Override
    protected void extend(ServerConnector target) {
        // Noop
    }
}
