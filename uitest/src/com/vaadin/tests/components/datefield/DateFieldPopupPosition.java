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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;

/**
 * Test UI for date field Popup calendar.
 * 
 * @author Vaadin Ltd
 */
public abstract class DateFieldPopupPosition extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout layout = new HorizontalLayout();
        addComponent(layout);
        Label gap = new Label();
        gap.setWidth(250, Unit.PIXELS);
        layout.addComponent(gap);
        PopupDateField field = new PopupDateField();
        layout.addComponent(field);
    }

    @Override
    protected Integer getTicketNumber() {
        return 14757;
    }

    @Override
    protected String getTestDescription() {
        return "Calendar popup should not placed on the top of text field when "
                + "there is no space on bottom.";
    }
}
