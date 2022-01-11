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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.vaadin.client.ui.ui.UIConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.ui.UI;

@Connect(UI.class)
public class CustomUIConnector extends UIConnector {
    @Override
    protected void init() {
        super.init();
        registerRpc(CustomUIConnectorRpc.class, new CustomUIConnectorRpc() {
            @Override
            public void test() {
                SpanElement span = Document.get().createSpanElement();
                span.setInnerText("This is the "
                        + CustomUIConnector.this.getClass().getSimpleName());
                Document.get().getBody().insertFirst(span);
            }
        });
    }
}
