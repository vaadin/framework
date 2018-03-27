/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.tests.components.grid;

import com.vaadin.tests.widgetset.client.grid.RowAwareRendererConnector.RowAwareRendererRpc;
import com.vaadin.ui.Grid.AbstractRenderer;
import com.vaadin.ui.Label;

public class RowAwareRenderer extends AbstractRenderer<Void> {
    public RowAwareRenderer(final Label debugLabel) {
        super(Void.class, "");
        registerRpc(new RowAwareRendererRpc() {
            @Override
            public void clicky(String key) {
                Object itemId = getItemId(key);
                debugLabel.setValue("key: " + key + ", itemId: " + itemId);
            }
        });
    }
}
