package com.vaadin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.data.bean.BeanToValidate;
import com.vaadin.ui.TextField;

public class BeanBinderTest {

    private BeanBinder<BeanToValidate> binder;

    private TextField nameField;
    private TextField ageField;

    private BeanToValidate p = new BeanToValidate();

    @Before
    public void setUp() {
        binder = new BeanBinder<>(BeanToValidate.class);
        p.setFirstname("Johannes");
        p.setAge(32);
        nameField = new TextField();
        ageField = new TextField();
    }

    @Test
    public void fieldBound_bindBean_fieldValueUpdated() {
        binder.bind(nameField, "firstname");
        binder.bind(p);

        assertEquals("Johannes", nameField.getValue());
    }

    @Test
    public void beanBound_bindField_fieldValueUpdated() {
        binder.bind(p);
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
        binder.bind(p);
        binder.bind(nameField, "firstname");

        nameField.setValue("Henri");

        assertEquals("Henri", p.getFirstname());
    }

    @Test
    public void readOnlyPropertyBound_setFieldValue_ignored() {
        binder.bind(nameField, "readOnlyProperty");
        binder.bind(p);

        String propertyValue = p.getReadOnlyProperty();
        nameField.setValue("Foo");

        assertEquals(propertyValue, p.getReadOnlyProperty());
    }

    @Test
    public void beanBound_setInvalidFieldValue_validationError() {
        binder.bind(p);
        binder.bind(nameField, "firstname");

        nameField.setValue("H"); // too short

        assertEquals("Johannes", p.getFirstname());
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
        binder.bind(p);
    }

    @Test(expected = ClassCastException.class)
    public void fieldWithIncompatibleTypeBound_loadBean_throws() {
        binder.bind(ageField, "age");
        binder.load(p);
    }

    @Test(expected = ClassCastException.class)
    public void fieldWithIncompatibleTypeBound_saveBean_throws()
            throws Throwable {
        try {
            binder.bind(ageField, "age");
            binder.save(p);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test
    public void fieldWithConverterBound_bindBean_fieldValueUpdated() {
        binder.forField(ageField)
                .withConverter(Integer::valueOf, String::valueOf).bind("age");
        binder.bind(p);

        assertEquals("32", ageField.getValue());
    }

    @Test(expected = ClassCastException.class)
    public void fieldWithInvalidConverterBound_bindBean_fieldValueUpdated() {
        binder.forField(ageField).withConverter(Float::valueOf, String::valueOf)
                .bind("age");
        binder.bind(p);

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
