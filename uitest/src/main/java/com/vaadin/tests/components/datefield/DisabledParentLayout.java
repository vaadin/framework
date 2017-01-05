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
package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractLocalDateField;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author Vaadin Ltd
 */
public class DisabledParentLayout extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();

        content.setSpacing(true);
        content.setMargin(true);

        final VerticalLayout pane = new VerticalLayout();
        AbstractLocalDateField dateField = new TestDateField();
        pane.addComponent(dateField);

        content.addComponent(pane);

        Button button = new Button("Test");
        button.addClickListener(event -> pane.setEnabled(!pane.isEnabled()));
        content.addComponent(button);

        addComponent(content);
    }

    @Override
    protected String getTestDescription() {
        return "Data field should be functional after enabling disabled parent.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13124;
    }

}
