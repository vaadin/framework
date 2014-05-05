package com.vaadin.util;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ReflectToolsGetPrimitiveFieldValue {
    @Test
    public void getFieldValueViaGetter() {
        class MyClass {
            public int field = 1;
        }

        MyClass myInstance = new MyClass();

        java.lang.reflect.Field memberField;
        Object fieldValue = new Boolean(false);
        try {
            memberField = myInstance.getClass().getField("field");
            fieldValue = ReflectTools
                    .getJavaFieldValue(myInstance, memberField);
        } catch (Exception e) {
        }
        assertFalse(fieldValue instanceof Boolean);
    }
}
