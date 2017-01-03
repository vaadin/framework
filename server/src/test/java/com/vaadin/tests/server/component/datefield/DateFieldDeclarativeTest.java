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
package com.vaadin.tests.server.component.datefield;

import org.junit.Test;

import com.vaadin.tests.server.component.abstractdatefield.AbstractLocalDateFieldDeclarativeTest;
import com.vaadin.ui.DateField;

/**
 * Tests the declarative support for implementations of {@link DateField}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DateFieldDeclarativeTest
        extends AbstractLocalDateFieldDeclarativeTest<DateField> {

    @Test
    public void remainingAttributes()
            throws InstantiationException, IllegalAccessException {
        String placeholder = "foo";
        String assistiveText = "at";
        boolean textFieldEnabled = false;
        String design = String.format(
                "<%s placeholder='%s' "
                        + "assistive-text='%s' text-field-enabled='%s'/>",
                getComponentTag(), placeholder, assistiveText,
                textFieldEnabled);

        DateField component = getComponentClass().newInstance();
        component.setPlaceholder(placeholder);
        component.setTextFieldEnabled(textFieldEnabled);
        component.setAssistiveText(assistiveText);

        testRead(design, component);
        testWrite(design, component);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-date-field";
    }

    @Override
    protected Class<? extends DateField> getComponentClass() {
        return DateField.class;
    }

}
