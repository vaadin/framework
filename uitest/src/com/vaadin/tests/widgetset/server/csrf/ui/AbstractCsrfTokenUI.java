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
package com.vaadin.tests.widgetset.server.csrf.ui;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.csrf.CsrfButton;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

/**
 * Abstract UI to test the CSRF token issue as reported in (#14111)
 * 
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@Widgetset(TestingWidgetSet.NAME)
public abstract class AbstractCsrfTokenUI extends AbstractTestUI {

    public static final String PRESS_ID = "PressMe";

    @Override
    protected void setup(VaadinRequest request) {

        addComponent(new Label("The button's text is the client token:"));
        addComponent(new CsrfButton());
        addComponent(new Label("This one is from the server"));
        addComponent(new Label(getSession().getCsrfToken()));
        Button pressMe = new Button("Click me to send a request");
        pressMe.setId(PRESS_ID);
        addComponent(pressMe);
    }

    @Override
    protected String getTestDescription() {
        return "Remove csrfToken from the request if security protection is disabled.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14111;
    }

}
