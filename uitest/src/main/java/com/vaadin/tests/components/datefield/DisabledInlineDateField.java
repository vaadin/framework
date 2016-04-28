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

import java.util.Calendar;
import java.util.Date;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;

public class DisabledInlineDateField extends AbstractTestUI {

    private static final Date testDate;
    static {
        Calendar cal = Calendar.getInstance();
        cal.set(2014, 5, 5);
        testDate = cal.getTime();
    }

    @Override
    protected void setup(VaadinRequest request) {
        DateField df = new InlineDateField("Disabled");
        df.setValue(testDate);
        df.setEnabled(false);
        addComponent(df);

        df = new InlineDateField("Read-only");
        df.setValue(testDate);
        df.setReadOnly(true);
        addComponent(df);
    }

    @Override
    protected String getTestDescription() {
        return "Testing disabled and read-only modes of InlineDateField.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10262;
    }

}
