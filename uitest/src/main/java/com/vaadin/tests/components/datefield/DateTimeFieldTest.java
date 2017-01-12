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

import java.util.LinkedHashMap;

import com.vaadin.ui.DateTimeField;

/**
 * @author Vaadin Ltd
 *
 */
public class DateTimeFieldTest
        extends AbstractDateTimeFieldTest<DateTimeField> {

    @Override
    protected Class<DateTimeField> getTestClass() {
        return DateTimeField.class;
    }

    @Override
    protected void createActions() {
        super.createActions();

        createInputPromptSelectAction(CATEGORY_FEATURES);
        createTextEnabledAction(CATEGORY_FEATURES);
    }

    private void createInputPromptSelectAction(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("<none>", null);
        options.put("Please enter date", "Please enter date");
        options.put("åäöÅÄÖ", "åäöÅÄÖ");

        createSelectAction("Input prompt", category, options, "<none>",
                new Command<DateTimeField, String>() {

                    @Override
                    public void execute(DateTimeField c, String value,
                            Object data) {
                        c.setPlaceholder(value);

                    }
                });
    }

    private void createTextEnabledAction(String category) {
        this.createBooleanAction("Text field enabled", category, true,
                (field, value, data) -> field.setTextFieldEnabled(value));
    }
}
