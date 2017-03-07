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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;

@SuppressWarnings("serial")
public class DateFields extends ComponentTestCase<DateField> {

    private static final Locale[] LOCALES = new Locale[] { Locale.US,
            Locale.TAIWAN, new Locale("fi", "FI") };

    @Override
    protected Class<DateField> getTestClass() {
        return DateField.class;
    }

    @Override
    protected void initializeComponents() {

        for (Locale locale : LOCALES) {
            DateField pd = createPopupDateField("Undefined width", "-1",
                    locale);
            pd.setId("Locale-" + locale.toString() + "-undefined-wide");
            addTestComponent(pd);
            pd = createPopupDateField("500px width", "500px", locale);
            pd.setId("Locale-" + locale.toString() + "-500px-wide");
            addTestComponent(pd);
            pd = createPopupDateField("Initially empty", "", locale);
            pd.setValue(null);
            pd.setId("Locale-" + locale.toString() + "-initially-empty");
            addTestComponent(pd);
        }

    }

    private DateField createPopupDateField(String caption, String width,
            Locale locale) {
        DateField pd = new DateField(caption + "(" + locale.toString() + ")");
        pd.setWidth(width);
        pd.setValue(LocalDate.of(1970, 05, 23));
        pd.setLocale(locale);
        pd.setResolution(DateResolution.YEAR);

        return pd;
    }

    @Override
    protected String getTestDescription() {
        return "A generic test for PopupDateFields in different configurations";
    }

    @Override
    protected List<Component> createActions() {
        List<Component> actions = super.createActions();
        actions.add(createResolutionSelectAction());
        actions.add(createInputPromptSelectAction());
        return actions;
    }

    private Component createResolutionSelectAction() {
        LinkedHashMap<String, DateResolution> options = new LinkedHashMap<>();
        options.put("Year", DateResolution.YEAR);
        options.put("Month", DateResolution.MONTH);
        options.put("Day", DateResolution.DAY);
        return createSelectAction("Resolution", options, "Year",
                (field, value, data) -> field.setResolution(value));
    }

    private Component createInputPromptSelectAction() {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("<none>", null);
        options.put("Please enter date", "Please enter date");
        options.put("åäöÅÄÖ", "åäöÅÄÖ");

        return createSelectAction("Input prompt", options, "<none>",
                new Command<DateField, String>() {

                    @Override
                    public void execute(DateField c, String value,
                            Object data) {
                        c.setPlaceholder(value);

                    }
                });
    }

}
