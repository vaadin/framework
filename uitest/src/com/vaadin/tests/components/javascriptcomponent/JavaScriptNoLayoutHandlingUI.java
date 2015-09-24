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
package com.vaadin.tests.components.javascriptcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class JavaScriptNoLayoutHandlingUI extends AbstractTestUIWithLog {

    public static class MyJSComponentState extends JavaScriptComponentState {
        // Using public methods as these are handled before public fields in the
        // parent
        private int aaa = 1;

        public int getAaa() {
            return aaa;
        }

        public void setAaa(int aaa) {
            this.aaa = aaa;
        }
    }

    @JavaScript("MyJS.js")
    public static class MyJsComponent extends AbstractJavaScriptComponent {

        @Override
        protected MyJSComponentState getState() {
            return (MyJSComponentState) super.getState();
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        final MyJsComponent myComponent = new MyJsComponent();
        myComponent.setId("js");
        addComponent(myComponent);
        addComponent(new Button("Send update", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                myComponent.getState().aaa++;
            }

        }));

    }

}
