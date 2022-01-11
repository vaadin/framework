/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.tests.widgetset.client;

import com.vaadin.client.BrowserInfo;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.LayoutManagerIE8;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.extensions.LayoutMemoryUsageIE8Extension;

@Connect(LayoutMemoryUsageIE8Extension.class)
public class LayoutMemoryUsageIE8ExtensionConnector
        extends AbstractExtensionConnector {

    @Override
    protected void extend(ServerConnector target) {
        if (BrowserInfo.get().isIE8()) {
            LayoutManagerIE8 manager = (LayoutManagerIE8) LayoutManager
                    .get(getConnection());
            configureGetMapSizeJS(manager);
        }
    }

    private native void configureGetMapSizeJS(LayoutManagerIE8 manager)
    /*-{
        $wnd.vaadin.getMeasuredSizesCount = function() {
            return manager.@com.vaadin.client.LayoutManagerIE8::getMeasuredSizesMapSize()();
        };
    }-*/;
}
