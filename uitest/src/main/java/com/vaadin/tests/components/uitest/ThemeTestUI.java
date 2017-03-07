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
package com.vaadin.tests.components.uitest;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.TextField;

public class ThemeTestUI extends AbstractReindeerTestUI {

    private TextField customStyle;
    private Button setStyleName;
    private TestSampler sampler;
    private String customStyleName = null;

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setSpacing(true);

        createCustomStyleStringField();

        HorizontalLayout selectors = new HorizontalLayout();

        selectors.addComponent(customStyle);
        selectors.addComponent(setStyleName);

        addComponent(selectors);

        sampler = new TestSampler();
        addComponent(sampler);

    }

    private void createCustomStyleStringField() {
        customStyle = new TextField();
        customStyle.setId("customstyle");
        setStyleName = new Button("Set stylename", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                onCustomStyleNameChanged(customStyle.getValue());
            }
        });
        setStyleName.setId("setcuststyle");

    }

    private void onCustomStyleNameChanged(String newStyleName) {
        sampler.setCustomStyleNameToComponents(customStyleName, newStyleName);
        customStyleName = newStyleName;
    }

    @Override
    protected String getTestDescription() {
        return "Test Sampler application with support for changing themes and stylenames.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8031;
    }

}
