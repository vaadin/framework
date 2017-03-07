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
package com.vaadin.tests.components.javascriptcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Label;

import elemental.json.JsonArray;

public class JSComponentLoadingIndicator extends AbstractReindeerTestUI {

    @JavaScript({ "JSComponent.js" })
    public class JSComponent extends AbstractJavaScriptComponent {
        public JSComponent() {
            addFunction("test", new JavaScriptFunction() {
                @Override
                public void call(JsonArray arguments) {
                    try {
                        Thread.sleep(1000);
                        Label label = new Label("pong");
                        label.addStyleName("pong");
                        addComponent(label);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new JSComponent());
    }

}
