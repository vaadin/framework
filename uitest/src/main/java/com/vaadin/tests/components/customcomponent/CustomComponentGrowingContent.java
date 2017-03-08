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
package com.vaadin.tests.components.customcomponent;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

public class CustomComponentGrowingContent extends TestBase {
    @Override
    protected void setup() {
        final Label label = new Label("Short content");
        label.setWidth(null);

        addComponent(new CustomComponent() {
            {
                GridLayout mainLayout = new GridLayout(1, 1);
                mainLayout.addComponent(label);
                mainLayout.setSizeUndefined();
                setSizeUndefined();
                setCompositionRoot(mainLayout);
            }
        });

        addComponent(new Button("Set long content", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                label.setValue("Longer content that should be fully visible");
            }
        }));
    }

    @Override
    protected String getDescription() {
        return "The width of the custom component should increase when its content grows";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7326);
    }
}
