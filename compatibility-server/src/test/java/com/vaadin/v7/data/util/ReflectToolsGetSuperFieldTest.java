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
package com.vaadin.v7.data.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.annotations.PropertyId;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.TextField;

public class ReflectToolsGetSuperFieldTest {

    @Test
    public void getFieldFromSuperClass() {
        class MyClass {
            @PropertyId("testProperty")
            TextField test = new TextField("This is a test");
        }
        class MySubClass extends MyClass {
            // no fields here
        }

        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("testProperty",
                new ObjectProperty<String>("Value of testProperty"));

        MySubClass form = new MySubClass();

        FieldGroup binder = new FieldGroup(item);
        binder.bindMemberFields(form);

        assertTrue("Value of testProperty".equals(form.test.getValue()));
    }

}
