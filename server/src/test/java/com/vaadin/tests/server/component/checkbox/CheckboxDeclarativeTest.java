/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.tests.server.component.checkbox;

import com.vaadin.tests.server.component.abstractfield.AbstractFieldDeclarativeTest;
import com.vaadin.ui.CheckBox;

/**
 * Tests declarative support for implementations of {@link CheckBox}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class CheckboxDeclarativeTest
        extends AbstractFieldDeclarativeTest<CheckBox, Boolean> {

    @Override
    public void valueDeserialization()
            throws InstantiationException, IllegalAccessException {
        String design = "<vaadin-check-box checked />";
        CheckBox checkBox = new CheckBox();

        checkBox.setValue(true);

        testRead(design, checkBox);
        testWrite(design, checkBox);
    }

    @Override
    public void readOnlyValue()
            throws InstantiationException, IllegalAccessException {
        String design = "<vaadin-check-box readonly checked />";

        CheckBox checkBox = new CheckBox();

        checkBox.setValue(true);
        checkBox.setReadOnly(true);

        testRead(design, checkBox);
        testWrite(design, checkBox);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-check-box";
    }

    @Override
    protected Class<CheckBox> getComponentClass() {
        return CheckBox.class;
    }

}
