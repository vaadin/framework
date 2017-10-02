package com.vaadin.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.junit.Test;

@SuppressWarnings("unused")
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

        Field memberField;
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

        Field memberField;
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
