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
import com.vaadin.ui.InlineDateField;

import java.util.Locale;

public class DateRangeWithSqlDate extends AbstractTestUI {

    // 2014-12-01
    private static final java.sql.Date startDate = new java.sql.Date(
            1417467822699L);

    // 2014-12-02
    private static final java.sql.Date endDate = new java.sql.Date(
            1417554763317L);

    @Override
    protected void setup(VaadinRequest request) {
        DateField df = new InlineDateField();
        df.setLocale(Locale.US);
        df.setRangeStart(startDate);
        df.setRangeEnd(endDate);

        df.setValue(startDate);

        addComponent(df);
    }

    @Override
    protected String getTestDescription() {
        return "Test that java.sql.Date can be given to specify date range start and end dates.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15342;
    }

}
