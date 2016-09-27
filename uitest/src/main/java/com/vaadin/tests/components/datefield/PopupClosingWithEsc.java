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
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.VerticalLayout;

public class PopupClosingWithEsc extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        AbstractDateField df1 = new TestDateField("Day");
        df1.setId("day");
        df1.setResolution(Resolution.DAY);

        AbstractDateField df2 = new TestDateField("Month");
        df2.setId("month");
        df2.setResolution(Resolution.MONTH);

        AbstractDateField df3 = new TestDateField("Year");
        df3.setId("year");
        df3.setResolution(Resolution.YEAR);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addComponents(df1, df2, df3);
        setContent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "Testing that the DateField popup can be closed with ESC key.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12317;
    }

}
