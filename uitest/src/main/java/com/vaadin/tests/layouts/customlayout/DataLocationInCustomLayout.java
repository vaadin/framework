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
package com.vaadin.tests.layouts.customlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;

@SuppressWarnings("serial")
public class DataLocationInCustomLayout extends AbstractReindeerTestUI {

    protected static final String BUTTON_ID = "DataLocationInCustomLayoutTestButtonId";

    @Override
    protected Integer getTicketNumber() {
        return 8416;
    }

    @Override
    protected String getTestDescription() {
        return "A test for adding a component with the data-location attribute in a "
                + "CustomLayout: a button should be visible.";
    }

    @Override
    protected void setup(VaadinRequest request) {
        setTheme("tests-tickets");
        CustomLayout customLayout = new CustomLayout("Github8416");
        final Button button = new Button("Button");
        button.setId(BUTTON_ID);
        customLayout.addComponent(button, "dataloc");
        addComponent(customLayout);
    }

}
