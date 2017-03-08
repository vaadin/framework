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
package com.vaadin.tests.minitutorials.v7a1;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.data.util.converter.StringToDoubleConverter;
import com.vaadin.v7.ui.Table;

public class FormatTableValue extends AbstractReindeerTestUI {

    private static final String PERCENT_PROPERTY = "percent";
    private static final String CURRENCY_PROPERTY = "currency";
    private static final String DEFAULT_PROPERTY = "default";

    @Override
    protected void setup(VaadinRequest request) {
        Table table = new Table();
        table.setLocale(Locale.FRANCE);
        table.addContainerProperty(PERCENT_PROPERTY, Double.class, 0);
        table.addContainerProperty(CURRENCY_PROPERTY, Double.class, 0);
        table.addContainerProperty(DEFAULT_PROPERTY, Double.class, 0);

        Object itemId = table.addItem();
        table.getItem(itemId).getItemProperty(PERCENT_PROPERTY)
                .setValue(3.1415);
        table.getItem(itemId).getItemProperty(CURRENCY_PROPERTY)
                .setValue(3.1415);
        table.getItem(itemId).getItemProperty(DEFAULT_PROPERTY)
                .setValue(3.1415);

        table.setConverter(PERCENT_PROPERTY, new StringToDoubleConverter() {
            @Override
            protected NumberFormat getFormat(Locale locale) {
                return NumberFormat.getPercentInstance(locale);
            }
        });

        table.setConverter(CURRENCY_PROPERTY, new StringToDoubleConverter() {
            @Override
            protected NumberFormat getFormat(Locale locale) {
                return NumberFormat.getCurrencyInstance(locale);
            }
        });

        addComponent(table);
    }

    @Override
    protected String getTestDescription() {
        return "Mini tutorial for https://vaadin.com/wiki/-/wiki/Main/Formatting%20data%20in%20Table";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
