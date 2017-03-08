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
import java.util.stream.Stream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;

@SuppressWarnings("serial")
public class PopupDateFieldExtendedRange extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setSpacing(true);

        final DateField[] fields = new DateField[3];

        fields[0] = makeDateField();
        fields[0].setLocale(new Locale("fi", "FI"));
        fields[0].setCaption("Finnish locale");

        fields[1] = makeDateField();
        fields[1].setLocale(new Locale("en", "US"));
        fields[1].setCaption("US English locale");

        fields[2] = makeDateField();
        fields[2].setLocale(new Locale("fi", "FI"));
        fields[2].setShowISOWeekNumbers(true);
        fields[2].setCaption("Finnish locale with week numbers");

        for (DateField f : fields) {
            addComponent(f);
        }

        addComponent(new Button("Change date", event -> Stream.of(fields)
                .forEach(field -> field.setValue(LocalDate.of(2010, 2, 16)))));
    }

    @Override
    protected String getTestDescription() {
        return "Show a few days of the preceding and following months in the datefield popup";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6718;
    }

    private DateField makeDateField() {
        DateField pdf = new DateField();
        pdf.setResolution(DateResolution.DAY);
        pdf.setValue(LocalDate.of(2011, 1, 1));
        return pdf;
    }
}
