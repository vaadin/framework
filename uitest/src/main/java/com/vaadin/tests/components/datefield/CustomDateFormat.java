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

import java.time.LocalDate;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractLocalDateField;

public class CustomDateFormat extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Locale locale = new Locale("fi", "FI");
        AbstractLocalDateField df = new TestDateField();
        df.setResolution(DateResolution.DAY);
        df.setLocale(locale);
        df.setWidth("300px");

        String pattern = "d. MMMM'ta 'yyyy 'klo'";
        df.setDateFormat(pattern);

        df.setValue(LocalDate.of(2010, 1, 1));

        addComponent(df);

    }

    @Override
    protected String getTestDescription() {
        return "Month name should be visible in text box if format pattern includes it";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3490;
    }

}
