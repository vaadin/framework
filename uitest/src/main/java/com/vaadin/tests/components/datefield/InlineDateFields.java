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
import com.vaadin.ui.InlineDateField;

@SuppressWarnings("serial")
public class InlineDateFields extends ComponentTestCase<InlineDateField> {

    private static final Locale[] LOCALES = new Locale[] { Locale.US,
            Locale.TAIWAN, new Locale("fi", "FI") };

    @Override
    protected Class<InlineDateField> getTestClass() {
        return InlineDateField.class;
    }

    @Override
    protected void initializeComponents() {

        InlineDateField hidden = new InlineDateField();
        hidden.setVisible(false); // Used to break rest of layout #8693
        addComponent(hidden);

        Locale locale = LOCALES[0];

        InlineDateField pd = createInlineDateField("Undefined width", "-1",
                locale);
        pd.setId("Locale-" + locale.toString() + "-undefined-wide");
        addTestComponent(pd);
        pd = createInlineDateField("300px width", "300px", locale);
        pd.setId("Locale-" + locale.toString() + "-300px-wide");
        addTestComponent(pd);
        pd = createInlineDateField("Initially empty", "", locale);
        pd.setValue(null);
        pd.setId("Locale-" + locale.toString() + "-initially-empty");
        addTestComponent(pd);

    }

    private InlineDateField createInlineDateField(String caption, String width,
            Locale locale) {
        InlineDateField pd = new InlineDateField(
                caption + "(" + locale.toString() + ")");
        pd.setWidth(width);
        pd.setValue(LocalDate.of(1970, 05, 23));
        pd.setLocale(locale);
        pd.setResolution(DateResolution.YEAR);

        return pd;
    }

    @Override
    protected String getTestDescription() {
        return "A generic test for InlineDateFields in different configurations";
    }

    @Override
    protected List<Component> createActions() {
        List<Component> actions = super.createActions();
        actions.add(createResolutionSelectAction());
        actions.add(createLocaleSelectAction());
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

    private Component createLocaleSelectAction() {
        LinkedHashMap<String, Locale> options = new LinkedHashMap<>();
        for (Locale locale : LOCALES) {
            options.put(locale.toString(), locale);
        }
        return createSelectAction("Locale", options, LOCALES[0].toString(),
                new Command<InlineDateField, Locale>() {

                    @Override
                    public void execute(InlineDateField c, Locale value,
                            Object data) {
                        c.setCaption(c.getCaption().replaceAll(
                                c.getLocale().toString(), value.toString()));
                        c.setLocale(value);

                    }
                });
    }

}
