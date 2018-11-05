package com.vaadin.v7.tests.server.component.abstractfield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.vaadin.v7.tests.VaadinClasses;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.Slider;

public class FieldDefaultValuesTest {

    @Test
    public void testFieldsHaveDefaultValueAfterClear() throws Exception {
        for (Field<?> field : createFields()) {
            Object originalValue = field.getValue();

            field.clear();

            Object clearedValue = field.getValue();

            assertEquals(
                    "Expected to get default value after clearing "
                            + field.getClass().getName(),
                    originalValue, clearedValue);
        }
    }

    @Test
    public void testFieldsAreEmptyAfterClear() throws Exception {
        int count = 0;
        for (Field<?> field : createFields()) {
            count++;
            field.clear();

            if (field instanceof Slider) {
                assertFalse(
                        "Slider should not be empty even after being cleared",
                        field.isEmpty());

            } else {
                assertTrue(
                        field.getClass().getName()
                                + " should be empty after being cleared",
                        field.isEmpty());
            }
        }
        assertTrue(count > 0);
    }

    @SuppressWarnings("rawtypes")
    private static List<Field<?>> createFields()
            throws InstantiationException, IllegalAccessException {
        List<Field<?>> fieldInstances = new ArrayList<>();

        for (Class<? extends Field> fieldType : VaadinClasses.getFields()) {
            fieldInstances.add(fieldType.newInstance());
        }
        return fieldInstances;
    }

}
