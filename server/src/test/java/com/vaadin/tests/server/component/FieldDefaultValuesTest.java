package com.vaadin.tests.server.component;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.VaadinClasses;
import com.vaadin.ui.Field;
import com.vaadin.ui.Slider;

public class FieldDefaultValuesTest {

    @Test
    public void testFieldsHaveDefaultValueAfterClear() throws Exception {
        for (Field<?> field : createFields()) {
            Object originalValue = field.getValue();

            field.clear();

            Object clearedValue = field.getValue();

            Assert.assertEquals(
                    "Expected to get default value after clearing "
                            + field.getClass().getName(),
                    originalValue, clearedValue);
        }
    }

    @Test
    public void testFieldsAreEmptyAfterClear() throws Exception {
        for (Field<?> field : createFields()) {
            field.clear();

            if (field instanceof Slider) {
                Assert.assertFalse(
                        "Slider should not be empty even after being cleared",
                        field.isEmpty());

            } else {
                Assert.assertTrue(
                        field.getClass().getName()
                                + " should be empty after being cleared",
                        field.isEmpty());
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static List<Field<?>> createFields()
            throws InstantiationException, IllegalAccessException {
        List<Field<?>> fieldInstances = new ArrayList<Field<?>>();

        for (Class<? extends Field> fieldType : VaadinClasses.getFields()) {
            fieldInstances.add(fieldType.newInstance());
        }
        return fieldInstances;
    }

}
