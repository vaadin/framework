package com.vaadin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.BeanBinder.BeanBindingBuilder;
import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.tests.data.bean.BeanToValidate;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.TextField;

public class BeanBinderTest
        extends BinderTestBase<BeanBinder<BeanToValidate>, BeanToValidate> {

    private enum TestEnum {
    }

    private class TestClass {
        private CheckBoxGroup<TestEnum> enums;
        private TextField number = new TextField();
    }

    private class TestBean {
        private Set<TestEnum> enums;
        private int number;

        public Set<TestEnum> getEnums() {
            return enums;
        }

        public void setEnums(Set<TestEnum> enums) {
            this.enums = enums;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }

    @Before
    public void setUp() {
        binder = new BeanBinder<>(BeanToValidate.class);
        item = new BeanToValidate();
        item.setFirstname("Johannes");
        item.setAge(32);
    }

    @Test
    public void bindInstanceFields_parameters_type_erased() {
        BeanBinder<TestBean> otherBinder = new BeanBinder<>(TestBean.class);
        TestClass testClass = new TestClass();
        otherBinder.forField(testClass.number)
                .withConverter(new StringToIntegerConverter(""))
                .bind("number");

        // Should correctly bind the enum field without throwing
        otherBinder.bindInstanceFields(testClass);
    }

    @Test
    public void bindInstanceFields_automatically_binds_incomplete_forMemberField_bindings() {
        BeanBinder<TestBean> otherBinder = new BeanBinder<>(TestBean.class);
        TestClass testClass = new TestClass();

        otherBinder.forMemberField(testClass.number)
                .withConverter(new StringToIntegerConverter(""));
        otherBinder.bindInstanceFields(testClass);

        TestBean bean = new TestBean();
        otherBinder.setBean(bean);
        testClass.number.setValue("50");
        assertEquals(50, bean.number);
    }

    @Test(expected = IllegalStateException.class)
    public void bindInstanceFields_does_not_automatically_bind_incomplete_forField_bindings() {
        BeanBinder<TestBean> otherBinder = new BeanBinder<>(TestBean.class);
        TestClass testClass = new TestClass();

        otherBinder.forField(testClass.number)
                .withConverter(new StringToIntegerConverter(""));

        // Should throw an IllegalStateException since the binding for number is
        // not completed with bind
        otherBinder.bindInstanceFields(testClass);
    }

    @Test(expected = IllegalStateException.class)
    public void incomplete_forMemberField_bindings() {
        BeanBinder<TestBean> otherBinder = new BeanBinder<>(TestBean.class);
        TestClass testClass = new TestClass();

        otherBinder.forMemberField(testClass.number)
                .withConverter(new StringToIntegerConverter(""));

        // Should throw an IllegalStateException since the forMemberField
        // binding has not been completed
        otherBinder.setBean(new TestBean());
    }

    @Test
    public void fieldBound_bindBean_fieldValueUpdated() {
        binder.bind(nameField, "firstname");
        binder.setBean(item);

        assertEquals("Johannes", nameField.getValue());
    }

    @Test
    public void beanBound_bindField_fieldValueUpdated() {
        binder.setBean(item);
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
        binder.setBean(item);
        binder.bind(nameField, "firstname");

        nameField.setValue("Henri");

        assertEquals("Henri", item.getFirstname());
    }

    @Test
    public void readOnlyPropertyBound_setFieldValue_ignored() {
        binder.bind(nameField, "readOnlyProperty");
        binder.setBean(item);

        String propertyValue = item.getReadOnlyProperty();
        nameField.setValue("Foo");

        assertEquals(propertyValue, item.getReadOnlyProperty());
    }

    @Test
    public void beanBound_setInvalidFieldValue_validationError() {
        binder.setBean(item);
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
        binder.setBean(item);
    }

    @Test(expected = ClassCastException.class)
    public void fieldWithIncompatibleTypeBound_loadBean_throws() {
        binder.bind(ageField, "age");
        binder.readBean(item);
    }

    @Test(expected = ClassCastException.class)
    public void fieldWithIncompatibleTypeBound_saveBean_throws()
            throws Throwable {
        try {
            binder.bind(ageField, "age");
            binder.writeBean(item);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test
    public void fieldWithConverterBound_bindBean_fieldValueUpdated() {
        binder.forField(ageField)
                .withConverter(Integer::valueOf, String::valueOf).bind("age");
        binder.setBean(item);

        assertEquals("32", ageField.getValue());
    }

    @Test(expected = ClassCastException.class)
    public void fieldWithInvalidConverterBound_bindBean_fieldValueUpdated() {
        binder.forField(ageField).withConverter(Float::valueOf, String::valueOf)
                .bind("age");
        binder.setBean(item);

        assertEquals("32", ageField.getValue());
    }

    @Test
    public void beanBinderWithBoxedType() {
        binder.forField(ageField)
                .withConverter(Integer::valueOf, String::valueOf).bind("age");
        binder.setBean(item);

        ageField.setValue(String.valueOf(20));
        assertEquals(20, item.getAge());
    }

    private void assertInvalid(HasValue<?> field, String message) {
        BinderValidationStatus<?> status = binder.validate();
        List<BindingValidationStatus<?>> errors = status
                .getFieldValidationErrors();
        assertEquals(1, errors.size());
        assertSame(field, errors.get(0).getField());
        assertEquals(message, errors.get(0).getMessage().get());
    }

    @Test
    public void beanBindingChainingMethods() {
        Method[] methods = BeanBindingBuilder.class.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            try {
                Method actualMethod = BeanBindingBuilder.class.getMethod(
                        method.getName(), method.getParameterTypes());

                Assert.assertNotSame(
                        actualMethod + " should be overridden in "
                                + BeanBindingBuilder.class
                                + " with more specific return type ",
                        BindingBuilder.class, actualMethod.getReturnType());
            } catch (NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
