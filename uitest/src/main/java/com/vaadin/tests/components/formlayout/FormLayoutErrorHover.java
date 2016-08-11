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
package com.vaadin.tests.components.formlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PopupDateField;

public class FormLayoutErrorHover extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        FormLayout formLayout = new FormLayout();
        PopupDateField fromDate = new PopupDateField("Date");
        fromDate.setImmediate(true);
        formLayout.addComponent(fromDate);

        addComponent(formLayout);
    }

    @Override
    protected String getTestDescription() {
        return "Enter some random text to the date field and press enter. Then hover the error indicator. This should show a message about the error.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8794);
    }

}
