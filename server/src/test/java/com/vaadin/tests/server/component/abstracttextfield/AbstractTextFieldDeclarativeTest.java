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
package com.vaadin.tests.server.component.abstracttextfield;

import java.util.Locale;

import org.junit.Test;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.tests.server.component.abstractfield.AbstractFieldDeclarativeTest;
import com.vaadin.ui.AbstractTextField;

/**
 * Tests declarative support for AbstractTextField.
 *
 * @since
 * @author Vaadin Ltd
 */
public abstract class AbstractTextFieldDeclarativeTest<T extends AbstractTextField>
        extends AbstractFieldDeclarativeTest<T, String> {

    @Test
    public void abstractTextFieldAttributes()
            throws InstantiationException, IllegalAccessException {
        int maxLength = 5;
        String placeholder = "foo";
        ValueChangeMode mode = ValueChangeMode.EAGER;
        int timeout = 100;
        String design = String.format(
                "<%s maxlength='%d' placeholder='%s' "
                        + "value-change-mode='%s' value-change-timeout='%d'/>",
                getComponentTag(), maxLength, placeholder,
                mode.name().toLowerCase(Locale.ROOT), timeout);

        T component = getComponentClass().newInstance();

        component.setMaxLength(maxLength);
        component.setPlaceholder(placeholder);
        component.setValueChangeMode(mode);
        component.setValueChangeTimeout(timeout);

        testRead(design, component);
        testWrite(design, component);
    }

}
