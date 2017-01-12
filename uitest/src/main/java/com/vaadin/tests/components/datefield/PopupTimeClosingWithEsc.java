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
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.VerticalLayout;

public class PopupTimeClosingWithEsc extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        DateTimeField secondResolution = new DateTimeField("Second");
        secondResolution.setId("second");
        secondResolution.setResolution(DateTimeResolution.SECOND);

        DateTimeField minuteResolution = new DateTimeField("Minute");
        minuteResolution.setId("minute");
        minuteResolution.setResolution(DateTimeResolution.MINUTE);

        DateTimeField hourResolution = new DateTimeField("Hour");
        hourResolution.setId("hour");
        hourResolution.setResolution(DateTimeResolution.HOUR);

        DateTimeField month = new DateTimeField("Month");
        month.setId("month");
        month.setResolution(DateTimeResolution.MONTH);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addComponents(secondResolution, minuteResolution, hourResolution,
                month);
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
