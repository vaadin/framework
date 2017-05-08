package com.vaadin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.BeanBinderTest.RequiredConstraints.SubConstraint;
import com.vaadin.data.BeanBinderTest.RequiredConstraints.SubSubConstraint;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.tests.data.bean.BeanToValidate;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.TextField;

public class BeanBinderTest
        extends BinderTestBase<Binder<BeanToValidate>, BeanToValidate> {

    private enum TestEnum {
    }

    private class TestClass {
        private CheckBoxGroup<TestEnum> enums;
        private TextField number = new TextField();
    }

    private static class TestBean implements Serializable {
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

    public static class RequiredConstraints implements Serializable {
        @NotNull
        @Max(10)
        private String firstname;

        @Size(min = 3, max = 16)
        @Digits(integer = 3, fraction = 2)
        private String age;

        @NotEmpty
        private String lastname;

        private SubConstraint subfield;

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public SubConstraint getSubfield() {
            return subfield;
        }

        public void setSubfield(SubConstraint subfield) {
            this.subfield = subfield;
        }

        public static class SubConstraint implements Serializable {

            @NotNull
            @NotEmpty
            @Size(min = 5)
            private String name;

            private SubSubConstraint subsub;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public SubSubConstraint getSubsub() {
                return subsub;
            }

            public void setSubsub(SubSubConstraint subsub) {
                this.subsub = subsub;
            }

        }

        public static class SubSubConstraint implements Serializable {

            @Size(min = 10)
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

        }
    }

    @Before
    public void setUp() {
        binder = new BeanValidationBinder<>(BeanToValidate.class);
        item = new BeanToValidate();
        item.setFirstname("Johannes");
        item.setAge(32);
    }

    @Test
    public void bindInstanceFields_parameters_type_erased() {
        Binder<TestBean> otherBinder = new Binder<>(TestBean.class);
        TestClass testClass = new TestClass();
        otherBinder.forField(testClass.number)
                .withConverter(new StringToIntegerConverter("")).bind("number");

        // Should correctly bind the enum field without throwing
        otherBinder.bindInstanceFields(testClass);
        testSerialization(otherBinder);
    }

    @Test
    public void bindInstanceFields_automatically_binds_incomplete_forMemberField_bindings() {
        Binder<TestBean> otherBinder = new Binder<>(TestBean.class);
        TestClass testClass = new TestClass();

        otherBinder.forMemberField(testClass.number)
                .withConverter(new StringToIntegerConverter(""));
        otherBinder.bindInstanceFields(testClass);

        TestBean bean = new TestBean();
        otherBinder.setBean(bean);
        testClass.number.setValue("50");
        assertEquals(50, bean.number);
        testSerialization(otherBinder);
    }

    @Test(expected = IllegalStateException.class)
    public void bindInstanceFields_does_not_automatically_bind_incomplete_forField_bindings() {
        Binder<TestBean> otherBinder = new Binder<>(TestBean.class);
        TestClass testClass = new TestClass();

        otherBinder.forField(testClass.number)
                .withConverter(new StringToIntegerConverter(""));

        // Should throw an IllegalStateException since the binding for number is
        // not completed with bind
        otherBinder.bindInstanceFields(testClass);
    }

    @Test(expected = IllegalStateException.class)
    public void incomplete_forMemberField_bindings() {
        Binder<TestBean> otherBinder = new Binder<>(TestBean.class);
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

    @Test
    public void firstName_isNotNullConstraint_fieldIsRequired() {
        BeanValidationBinder<RequiredConstraints> binder = new BeanValidationBinder<>(
                RequiredConstraints.class);
        RequiredConstraints bean = new RequiredConstraints();

        TextField field = new TextField();
        binder.bind(field, "firstname");
        binder.setBean(bean);

        Assert.assertTrue(field.isRequiredIndicatorVisible());
        testSerialization(binder);
    }

    @Test
    public void age_minSizeConstraint_fieldIsRequired() {
        BeanValidationBinder<RequiredConstraints> binder = new BeanValidationBinder<>(
                RequiredConstraints.class);
        RequiredConstraints bean = new RequiredConstraints();

        TextField field = new TextField();
        binder.bind(field, "age");
        binder.setBean(bean);

        Assert.assertTrue(field.isRequiredIndicatorVisible());
        testSerialization(binder);
    }

    @Test
    public void lastName_minSizeConstraint_fieldIsRequired() {
        BeanValidationBinder<RequiredConstraints> binder = new BeanValidationBinder<>(
                RequiredConstraints.class);
        RequiredConstraints bean = new RequiredConstraints();

        TextField field = new TextField();
        binder.bind(field, "lastname");
        binder.setBean(bean);

        Assert.assertTrue(field.isRequiredIndicatorVisible());
        testSerialization(binder);
    }

    @Test
    public void subfield_name_fieldIsRequired() {
        BeanValidationBinder<RequiredConstraints> binder = new BeanValidationBinder<>(
                RequiredConstraints.class);
        RequiredConstraints bean = new RequiredConstraints();
        bean.setSubfield(new RequiredConstraints.SubConstraint());

        TextField field = new TextField();
        binder.bind(field, "subfield.name");
        binder.setBean(bean);

        Assert.assertTrue(field.isRequiredIndicatorVisible());
        testSerialization(binder);
    }

    @Test
    public void subsubfield_name_fieldIsRequired() {
        BeanValidationBinder<RequiredConstraints> binder = new BeanValidationBinder<>(
                RequiredConstraints.class);
        RequiredConstraints bean = new RequiredConstraints();
        RequiredConstraints.SubConstraint subfield = new RequiredConstraints.SubConstraint();
        subfield.setSubsub(new SubSubConstraint());
        bean.setSubfield(subfield);

        TextField field = new TextField();
        binder.bind(field, "subfield.subsub.value");
        binder.setBean(bean);

        Assert.assertTrue(field.isRequiredIndicatorVisible());
        testSerialization(binder);
    }

    @Test
    public void subfield_name_valueCanBeValidated() {
        BeanValidationBinder<RequiredConstraints> binder = new BeanValidationBinder<>(
                RequiredConstraints.class);
        TextField field = new TextField();

        binder.bind(field, "subfield.name");
        RequiredConstraints bean = new RequiredConstraints();
        bean.setSubfield(new SubConstraint());
        binder.setBean(bean);
        Assert.assertFalse(binder.validate().isOk());
        field.setValue("overfive");
        Assert.assertTrue(binder.validate().isOk());
    }

    @Test
    public void subSubfield_name_valueCanBeValidated() {
        BeanValidationBinder<RequiredConstraints> binder = new BeanValidationBinder<>(
                RequiredConstraints.class);
        TextField field = new TextField();

        binder.bind(field, "subfield.subsub.value");
        RequiredConstraints bean = new RequiredConstraints();
        SubConstraint subfield = new SubConstraint();
        bean.setSubfield(subfield);
        subfield.setSubsub(new SubSubConstraint());
        binder.setBean(bean);

        Assert.assertFalse(binder.validate().isOk());
        field.setValue("overtencharacters");
        Assert.assertTrue(binder.validate().isOk());
    }

    private void assertInvalid(HasValue<?> field, String message) {
        BinderValidationStatus<?> status = binder.validate();
        List<BindingValidationStatus<?>> errors = status
                .getFieldValidationErrors();
        assertEquals(1, errors.size());
        assertSame(field, errors.get(0).getField());
        assertEquals(message, errors.get(0).getMessage().get());
    }
}
