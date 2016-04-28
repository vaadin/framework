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

package com.vaadin.tests.widgetset.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.extensions.BasicExtension;

@Connect(BasicExtension.class)
public class BasicExtensionTestConnector extends AbstractExtensionConnector {
    private ServerConnector target;

    @Override
    protected void extend(ServerConnector target) {
        this.target = target;
        appendMessage(" extending ");
    }

    private void appendMessage(String action) {
        String message = getClass().getSimpleName() + action
                + target.getClass().getSimpleName();

        DivElement element = Document.get().createDivElement();
        element.setInnerText(message);

        Document.get().getBody().insertFirst(element);
    }

    @Override
    public void onUnregister() {
        appendMessage(" removed for ");
    }
}
