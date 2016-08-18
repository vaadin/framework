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

import org.junit.Test;

import com.vaadin.shared.ui.textfield.ValueChangeMode;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.TextField;

/**
 * Tests declarative support for AbstractTextField.
 *
 * @since
 * @author Vaadin Ltd
 */
public class AbstractTextFieldDeclarativeTest
        extends DeclarativeTestBase<AbstractTextField> {

    @Test
    public void testAttributes() {
        String design = "<vaadin-text-field "
                // + "null-representation=this-is-null "
                // + "null-setting-allowed "
                + "maxlength=5 columns=3 "
                + "placeholder=input value-change-mode=eager "
                + "value-change-timeout=100 />";
        AbstractTextField tf = new TextField();
        // FIXME
        // tf.setNullRepresentation("this-is-null");
        // tf.setNullSettingAllowed(true);
        tf.setMaxLength(5);
        tf.setColumns(3);
        tf.setPlaceholder("input");
        tf.setValueChangeMode(ValueChangeMode.EAGER);
        tf.setValueChangeTimeout(100);
        testRead(design, tf);
        testWrite(design, tf);
    }

}
