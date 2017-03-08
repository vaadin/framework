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
package com.vaadin.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ReflectToolsGetFieldValueByTypeTest {
    @Test
    public void getFieldValue() {
        class MyClass {
            public Integer getField() {
                return 1;
            }

            public void setField(Integer i) {
            }

        }
        class MySubClass extends MyClass {
            public String field = "Hello";
        }

        MySubClass myInstance = new MySubClass();

        java.lang.reflect.Field memberField;
        Object fieldValue = false;
        try {
            memberField = myInstance.getClass().getField("field");
            // Should get a String value. Without the third parameter (calling
            // ReflectTools.getJavaFieldValue(Object object, LegacyField field))
            // would
            // get an Integer value
            fieldValue = ReflectTools.getJavaFieldValue(myInstance, memberField,
                    String.class);
        } catch (Exception e) {
        }
        assertTrue(fieldValue instanceof String);

    }

    @Test
    public void getFieldValueViaGetter() {
        class MyClass {
            public Integer field = 1;
        }
        class MySubClass extends MyClass {
            public String field = "Hello";
        }

        MySubClass myInstance = new MySubClass();

        java.lang.reflect.Field memberField;
        try {
            memberField = myInstance.getClass().getField("field");
            // Should throw an IllegalArgument exception as the mySubClass class
            // doesn't have an Integer field.
            ReflectTools.getJavaFieldValue(myInstance, memberField,
                    Integer.class);
            fail("Previous method call should have thrown an exception");
        } catch (Exception e) {
        }
    }
}
