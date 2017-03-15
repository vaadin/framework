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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.DateTimeField;

/**
 * @author Vaadin Ltd
 *
 */
public class TimePopupSelection extends AbstractTestUIWithLog {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    protected void setup(VaadinRequest request) {
        setLocale(Locale.ENGLISH);
        DateTimeField field = new DateTimeField();
        field.setResolution(DateTimeResolution.SECOND);

        field.setValue(LocalDateTime.of(2017, 1, 13, 1, 0));

        field.addValueChangeListener(
                event -> log(FORMATTER.format(event.getValue())));

        addComponent(field);
    }

}
