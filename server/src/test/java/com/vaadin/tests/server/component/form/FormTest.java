/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.server.component.form;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;

/**
 * Test for {@link Form}.
 * 
 * @author Vaadin Ltd
 */
public class FormTest {

    @Test
    public void testFocus() {
        Form form = new Form();
        final boolean firstFieldIsFocused[] = new boolean[1];
        TextField field1 = new TextField() {
            @Override
            public boolean isConnectorEnabled() {
                return false;
            }

            @Override
            public void focus() {
                firstFieldIsFocused[0] = true;
            }
        };

        final boolean secondFieldIsFocused[] = new boolean[1];
        TextField field2 = new TextField() {
            @Override
            public boolean isConnectorEnabled() {
                return true;
            }

            @Override
            public void focus() {
                secondFieldIsFocused[0] = true;
            }
        };
        form.addField("a", field1);
        form.addField("b", field2);
        form.focus();

        Assert.assertTrue("Field with enabled connector is not focused",
                secondFieldIsFocused[0]);
        Assert.assertFalse("Field with disabled connector is focused",
                firstFieldIsFocused[0]);
    }
}
