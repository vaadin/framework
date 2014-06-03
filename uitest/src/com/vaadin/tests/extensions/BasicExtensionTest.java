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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class BasicExtensionTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label label = new Label();
        addComponent(label);

        final BasicExtension rootExtension = new BasicExtension();
        rootExtension.extend(this);
        new BasicExtension().extend(label);
        addComponent(new Button("Remove root extension", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                rootExtension.remove();
            }
        }));
    }

    @Override
    protected String getTestDescription() {
        return "Simple test for extending components";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(6690);
    }

}
