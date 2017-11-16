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
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;

public class DateFieldFocus extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        DateField dateField = new DateField();
        dateField.addFocusListener(e -> log("focused"));
        dateField.addBlurListener(e -> log("blurred"));
        addComponent(dateField);

        TextField textField = new TextField();
        textField.setCaption("second");
        addComponent(textField);
    }

    @Override
    protected String getTestDescription() {
        return "DateField should not trigger events when nagivating between sub-components.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 1008;
    }

}
