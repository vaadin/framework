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
package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.VerticalLayout;

public class AriaDisabled extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        content.setSpacing(true);

        final DateField disabledDateField = new DateField("Disabled DateField");
        disabledDateField.setEnabled(false);

        setContent(content);
        content.addComponent(disabledDateField);
        content.addComponent(new DateField("Enabled DateField"));
    }

    @Override
    protected String getTestDescription() {
        return "Test for aria-disabled attribute on DateField.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13463;
    }
}
