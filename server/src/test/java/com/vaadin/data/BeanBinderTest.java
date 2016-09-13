package com.vaadin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.data.bean.BeanToValidate;

public class BeanBinderTest extends
        BinderTestBase<BeanBinder<BeanToValidate>, BeanToValidate> {

    @Before
    public void setUp() {
        binder = new BeanBinder<>(BeanToValidate.class);
        item = new BeanToValidate();
        item.setFirstname("Johannes");
        item.setAge(32);
    }

    @Test
    public void fieldBound_bindBean_fieldValueUpdated() {
        binder.bind(nameField, "firstname");
        binder.bind(item);

        assertEquals("Johannes", nameField.getValue());
    }

    @Test
    public void beanBound_bindField_fieldValueUpdated() {
        binder.bind(item);
        binder.bind(nameField, "firstname");

        assertEquals("Johannes", nameField.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void bindInvalidPropertyName_throws() {
        binder.bind(nameField, "firstnaem");
    }

    @Test(expected = NullPointerException.class)
    public void bindNullPropertyName_throws() {
        binder.bind(nameField, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void bindNonReadableProperty_throws() {
        binder.bind(nameField, "writeOnlyProperty");
    }

    @Test
    public void beanBound_setValidFieldValue_propertyValueChanged() {
        binder.bind(item);
        binder.bind(nameField, "firstname");

        nameField.setValue("Henri");

        assertEquals("Henri", item.getFirstname());
    }

    @Test
    public void readOnlyPropertyBound_setFieldValue_ignored() {
        binder.bind(nameField, "readOnlyProperty");
        binder.bind(item);

        String propertyValue = item.getReadOnlyProperty();
        nameField.setValue("Foo");

        assertEquals(propertyValue, item.getReadOnlyProperty());
    }

    @Test
    public void beanBound_setInvalidFieldValue_validationError() {
        binder.bind(item);
        binder.bind(nameField, "firstname");

        nameField.setValue("H"); // too short

        assertEquals("Johannes", item.getFirstname());
        assertInvalid(nameField, "size must be between 3 and 16");
    }

    @Test
    public void beanNotBound_setInvalidFieldValue_validationError() {
        binder.bind(nameField, "firstname");

        nameField.setValue("H"); // too short

        assertInvalid(nameField, "size must be between 3 and 16");
    }

    @Test
    public void explicitValidatorAdded_setInvalidFieldValue_explicitValidatorRunFirst() {
        binder.forField(nameField).withValidator(name -> name.startsWith("J"),
                "name must start with J").bind("firstname");

        nameField.setValue("A");

        assertInvalid(nameField, "name must start with J");
    }

    @Test
    public void explicitValidatorAdded_setInvalidFieldValue_beanValidatorRun() {
        binder.forField(nameField).withValidator(name -> name.startsWith("J"),
                "name must start with J").bind("firstname");

        nameField.setValue("J");

        assertInvalid(nameField, "size must be between 3 and 16");
    }

    @Test(expected = ClassCastException.class)
    public void fieldWithIncompatibleTypeBound_bindBean_throws() {
        binder.bind(ageField, "age");
        binder.bind(item);
    }

    @Test(expected = ClassCastException.class)
    public void fieldWithIncompatibleTypeBound_loadBean_throws() {
        binder.bind(ageField, "age");
        binder.load(item);
    }

    @Test(expected = ClassCastException.class)
    public void fieldWithIncompatibleTypeBound_saveBean_throws()
            throws Throwable {
        try {
            binder.bind(ageField, "age");
            binder.save(item);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test
    public void fieldWithConverterBound_bindBean_fieldValueUpdated() {
        binder.forField(ageField)
                .withConverter(Integer::valueOf, String::valueOf).bind("age");
        binder.bind(item);

        assertEquals("32", ageField.getValue());
    }

    @Test(expected = ClassCastException.class)
    public void fieldWithInvalidConverterBound_bindBean_fieldValueUpdated() {
        binder.forField(ageField).withConverter(Float::valueOf, String::valueOf)
                .bind("age");
        binder.bind(item);

        assertEquals("32", ageField.getValue());
    }

    private void assertInvalid(HasValue<?> field, String message) {
        BinderValidationStatus<?> status = binder.validate();
        List<ValidationStatus<?>> errors = status.getFieldValidationErrors();
        assertEquals(1, errors.size());
        assertSame(field, errors.get(0).getField());
        assertEquals(message, errors.get(0).getMessage().get());
    }
}
