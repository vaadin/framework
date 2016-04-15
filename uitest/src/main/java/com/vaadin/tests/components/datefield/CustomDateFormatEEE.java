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
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.VerticalLayout;

public class CustomDateFormatEEE extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Calendar cal = Calendar.getInstance();
        cal.set(2014, 2, 14); // Friday

        DateField df = new DateField("Should display 14/03/2014 Fri");
        df.setResolution(Resolution.DAY);
        df.setLocale(new Locale("en", "US"));

        String pattern = "dd/MM/yyyy EEE";
        df.setDateFormat(pattern);
        df.setValue(cal.getTime());
        df.setWidth("200px");

        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(df);
        layout.setMargin(true);
        setContent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "Verifies that \"EEE\" works as a part of custom date pattern";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13443;
    }

}
