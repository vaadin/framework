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
package com.vaadin.tests.components.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;

@Theme("tests-tickets")
public class TestUITheme extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label label = new Label("A green label");
        label.setStyleName("green");
        addComponent(label);
    }

    @Override
    public String getTestDescription() {
        return "UI with @Theme(\"tests-tickets\"). The label uses a stylename that should only be defined in the test-tickets theme.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7885);
    }

}
