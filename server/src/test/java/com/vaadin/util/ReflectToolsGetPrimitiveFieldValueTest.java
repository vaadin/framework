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

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ReflectToolsGetPrimitiveFieldValueTest {
    @Test
    public void getFieldValueViaGetter() {
        class MyClass {
            public int field = 1;
        }

        MyClass myInstance = new MyClass();

        java.lang.reflect.Field memberField;
        Object fieldValue = false;
        try {
            memberField = myInstance.getClass().getField("field");
            fieldValue = ReflectTools.getJavaFieldValue(myInstance,
                    memberField);
        } catch (Exception e) {
        }
        assertFalse(fieldValue instanceof Boolean);
    }
}
