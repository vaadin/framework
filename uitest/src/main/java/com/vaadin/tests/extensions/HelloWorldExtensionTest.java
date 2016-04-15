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
package com.vaadin.tests.extensions;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class HelloWorldExtensionTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final HelloWorldExtension extension = new HelloWorldExtension();
        extension.setGreeting("Kind words");
        addExtension(extension);

        addComponent(new Button("Greet again", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                extension.greetAgain();
            }
        }));
    }

    @Override
    protected String getTestDescription() {
        return "Testing basic Extension";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
