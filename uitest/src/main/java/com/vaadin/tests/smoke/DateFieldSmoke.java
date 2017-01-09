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
package com.vaadin.tests.smoke;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.v7.ui.InlineDateField;
import com.vaadin.v7.ui.PopupDateField;

/**
 * @author Vaadin Ltd
 *
 */
public class DateFieldSmoke extends AbstractTestUIWithLog {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
            "yyyy.MM.dd", Locale.ENGLISH);

    @Override
    protected void setup(VaadinRequest request) {
        setLocale(Locale.ENGLISH);

        InlineDateField inline = new InlineDateField();
        PopupDateField popup = new PopupDateField();

        int year = 2016 - 1900;
        popup.setValue(new Date(year, 11, 28));
        inline.setValue(new Date(year, 11, 29));

        popup.setDateFormat("MM/dd/yy");
        inline.setDateFormat("MM/dd/yy");

        popup.addValueChangeListener(event -> log(
                "Popup value is : " + FORMAT.format(popup.getValue())));
        inline.addValueChangeListener(event -> log(
                "Inline value is : " + FORMAT.format(inline.getValue())));

        addComponents(inline, popup);
    }

}
