package com.vaadin.data;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Slider;

public class BinderComponentTest
        extends BinderTestBase<Binder<String>, String> {

    enum TestValues {
        TRUE, FALSE, FILE_NOT_FOUND
    }

    @Before
    public void setUp() {
        binder = new Binder<>();
        item = "Foo";
    }

    @Test
    public void slider_bind_null() {
        double minValue = 10.5d;
        double initialValue = 28.2d;

        Slider slider = new Slider();
        slider.setResolution(1);
        slider.setMin(minValue);

        testFieldNullRepresentation(initialValue, slider);
    }

    @Test
    public void colorpicker_bind_null() {
        testFieldNullRepresentation(new Color(123, 254, 213),
                new ColorPicker());
    }

    @Test
    public void richtextarea_bind_null() {
        testFieldNullRepresentation("Test text", new RichTextArea());
    }

    @Test
    public void checkbox_bind_null() {
        testFieldNullRepresentation(true, new CheckBox());
    }

    @Test
    public void checkboxgroup_bind_null() {
        CheckBoxGroup<TestValues> checkBoxGroup = new CheckBoxGroup<>();
        checkBoxGroup.setItems(TestValues.values());
        testFieldNullRepresentation(
                Collections.singleton(TestValues.FILE_NOT_FOUND),
                checkBoxGroup);
    }

    private <T> void testFieldNullRepresentation(T initialValue,
            HasValue<T> field) {
        binder.bind(field, t -> null, (str, val) -> {
        });
        field.setValue(initialValue);
        assertEquals("Initial value of field unexpected", initialValue,
                field.getValue());
        binder.setBean(item);
        assertEquals("Null representation for field failed",
                field.getEmptyValue(), field.getValue());
    }

}
