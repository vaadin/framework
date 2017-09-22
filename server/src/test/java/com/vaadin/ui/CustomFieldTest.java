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
package com.vaadin.ui;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;

public class CustomFieldTest {

    public static class TestCustomField extends CustomField<String> {

        private String value = "initial";
        private Button button;

        @Override
        public String getValue() {
            return value;
        }

        @Override
        protected Component initContent() {
            button = new Button("Content");
            return button;
        }

        @Override
        protected void doSetValue(String value) {
            this.value = value;

        }

    }

    @Test(expected = NoSuchElementException.class)
    public void iterator() {
        TestCustomField field = new TestCustomField();
        // Needs to trigger initContent somehow as
        // iterator() can't do it even though it should...
        field.getContent();
        Iterator<Component> iterator = field.iterator();
        Assert.assertNotNull(iterator);
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(field.button, iterator.next());
        Assert.assertFalse(iterator.hasNext());
        iterator.next();
    }
}
