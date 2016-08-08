package com.vaadin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Binder.Binding;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.TextField;

public class BinderTest {

    private static class StatusBean {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }

    Binder<Person> binder;

    TextField nameField;
    TextField ageField;

    Person p = new Person();

    Validator<String> notEmpty = Validator.from(val -> !val.isEmpty(),
            "Value cannot be empty");
    Converter<String, Integer> stringToInteger = Converter.from(
            Integer::valueOf, String::valueOf, e -> "Value must be a number");
    Validator<Integer> notNegative = Validator.from(x -> x >= 0,
            "Value must be positive");

    @Before
    public void setUp() {
        binder = new Binder<>();
        p.setFirstName("Johannes");
        p.setAge(32);
        nameField = new TextField();
        ageField = new TextField();
    }

    @Test(expected = NullPointerException.class)
    public void bindingNullBeanThrows() {
        binder.bind(null);
    }

    @Test(expected = NullPointerException.class)
    public void bindingNullFieldThrows() {
        binder.forField(null);
    }

    @Test(expected = NullPointerException.class)
    public void bindingNullGetterThrows() {
        binder.bind(nameField, null, Person::setFirstName);
    }

    @Test
    public void fieldValueUpdatedOnBeanBind() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        binder.bind(p);
        assertEquals("Johannes", nameField.getValue());
    }

    @Test
    public void fieldValueUpdatedWithShortcutBind() {
        bindName();
        assertEquals("Johannes", nameField.getValue());
    }

    @Test
    public void fieldValueUpdatedIfBeanAlreadyBound() {
        binder.bind(p);
        binder.bind(nameField, Person::getFirstName, Person::setFirstName);

        assertEquals("Johannes", nameField.getValue());
        nameField.setValue("Artur");
        assertEquals("Artur", p.getFirstName());
    }

    @Test
    public void getBeanReturnsBoundBeanOrNothing() {
        assertFalse(binder.getBean().isPresent());
        binder.bind(p);
        assertSame(p, binder.getBean().get());
        binder.unbind();
        assertFalse(binder.getBean().isPresent());
    }

    @Test
    public void fieldValueSavedToPropertyOnChange() {
        bindName();
        nameField.setValue("Henri");
        assertEquals("Henri", p.getFirstName());
    }

    @Test
    public void fieldValueNotSavedAfterUnbind() {
        bindName();
        nameField.setValue("Henri");
        binder.unbind();
        nameField.setValue("Aleksi");
        assertEquals("Henri", p.getFirstName());
    }

    @Test
    public void bindNullSetterIgnoresValueChange() {
        binder.bind(nameField, Person::getFirstName, null);
        binder.bind(p);
        nameField.setValue("Artur");
        assertEquals(p.getFirstName(), "Johannes");
    }

    @Test
    public void bindToAnotherBeanStopsUpdatingOriginalBean() {
        bindName();
        nameField.setValue("Leif");

        Person p2 = new Person();
        p2.setFirstName("Marlon");
        binder.bind(p2);
        assertEquals("Marlon", nameField.getValue());
        assertEquals("Leif", p.getFirstName());
        assertSame(p2, binder.getBean().get());

        nameField.setValue("Ilia");
        assertEquals("Ilia", p2.getFirstName());
        assertEquals("Leif", p.getFirstName());
    }

    @Test
    public void save_unbound_noChanges() {
        Binder<Person> binder = new Binder<>();
        Person person = new Person();

        int age = 10;
        person.setAge(age);

        binder.save(person);

        Assert.assertEquals(age, person.getAge());
    }

    @Test
    public void save_bound_beanIsUpdated() {
        Binder<Person> binder = new Binder<>();
        binder.bind(nameField, Person::getFirstName, Person::setFirstName);

        Person person = new Person();

        String fieldValue = "bar";
        nameField.setValue(fieldValue);

        person.setFirstName("foo");

        binder.save(person);

        Assert.assertEquals(fieldValue, person.getFirstName());
    }

    @Test
    public void load_bound_fieldValueIsUpdated() {
        Binder<Person> binder = new Binder<>();
        binder.bind(nameField, Person::getFirstName, Person::setFirstName);

        Person person = new Person();

        String name = "bar";
        person.setFirstName(name);
        binder.load(person);

        Assert.assertEquals(name, nameField.getValue());
    }

    @Test
    public void load_unbound_noChanges() {
        Binder<Person> binder = new Binder<>();

        nameField.setValue("");

        Person person = new Person();

        String name = "bar";
        person.setFirstName(name);
        binder.load(person);

        Assert.assertEquals("", nameField.getValue());
    }

    @Test
    public void validate_notBound_noErrors() {
        Binder<Person> binder = new Binder<>();

        List<ValidationError<?>> errors = binder.validate();

        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void bound_validatorsAreOK_noErrors() {
        Binder<Person> binder = new Binder<>();
        Binding<Person, String, String> binding = binder.forField(nameField);
        binding.withValidator(Validator.alwaysPass()).bind(Person::getFirstName,
                Person::setFirstName);

        nameField.setComponentError(new UserError(""));
        List<ValidationError<?>> errors = binder.validate();

        Assert.assertTrue(errors.isEmpty());
        Assert.assertNull(nameField.getComponentError());
    }

    @SuppressWarnings("serial")
    @Test
    public void bound_validatorsFail_errors() {
        Binder<Person> binder = new Binder<>();
        Binding<Person, String, String> binding = binder.forField(nameField);
        binding.withValidator(Validator.alwaysPass());
        String msg1 = "foo";
        String msg2 = "bar";
        binding.withValidator(new Validator<String>() {
            @Override
            public Result<String> apply(String value) {
                return new SimpleResult<>(null, msg1);
            }
        });
        binding.withValidator(value -> false, msg2);
        binding.bind(Person::getFirstName, Person::setFirstName);

        List<ValidationError<?>> errors = binder.validate();

        Assert.assertEquals(1, errors.size());

        Set<String> errorMessages = errors.stream()
                .map(ValidationError::getMessage).collect(Collectors.toSet());
        Assert.assertTrue(errorMessages.contains(msg1));

        Set<?> fields = errors.stream().map(ValidationError::getField)
                .collect(Collectors.toSet());
        Assert.assertEquals(1, fields.size());
        Assert.assertTrue(fields.contains(nameField));

        ErrorMessage componentError = nameField.getComponentError();
        Assert.assertNotNull(componentError);
        Assert.assertEquals("foo",
                ((AbstractErrorMessage) componentError).getMessage());
    }

    private void bindName() {
        binder.bind(nameField, Person::getFirstName, Person::setFirstName);
        binder.bind(p);
    }

    private void bindAgeWithValidatorConverterValidator() {
        binder.forField(ageField).withValidator(notEmpty)
                .withConverter(stringToInteger).withValidator(notNegative)
                .bind(Person::getAge, Person::setAge);
        binder.bind(p);
    }

    @Test
    public void validatorForSuperTypeCanBeUsed() {
        // Validates that a validator for a super type can be used, e.g.
        // validator for Number can be used on a Double

        TextField salaryField = new TextField();
        Binder<Person> binder = new Binder<>();
        Validator<Number> positiveNumberValidator = value -> {
            if (value.doubleValue() >= 0) {
                return Result.ok(value);
            } else {
                return Result.error("Number must be positive");
            }
        };
        binder.forField(salaryField)
                .withConverter(Double::valueOf, String::valueOf)
                .withValidator(positiveNumberValidator)
                .bind(Person::getSalaryDouble, Person::setSalaryDouble);

        Person person = new Person();
        binder.bind(person);
        salaryField.setValue("10");
        Assert.assertEquals(10, person.getSalaryDouble(), 0);
        salaryField.setValue("-1"); // Does not pass validator
        Assert.assertEquals(10, person.getSalaryDouble(), 0);
    }

    @Test
    public void convertInitialValue() {
        bindAgeWithValidatorConverterValidator();
        assertEquals("32", ageField.getValue());
    }

    @Test
    public void convertToModelValidAge() {
        bindAgeWithValidatorConverterValidator();

        ageField.setValue("33");
        assertEquals(33, p.getAge());
    }

    @Test
    public void convertToModelNegativeAgeFailsOnFirstValidator() {
        bindAgeWithValidatorConverterValidator();

        ageField.setValue("");
        assertEquals(32, p.getAge());
        assertValidationErrors(binder.validate(), "Value cannot be empty");
    }

    private void assertValidationErrors(
            List<ValidationError<?>> validationErrors,
            String... errorMessages) {
        Assert.assertEquals(errorMessages.length, validationErrors.size());
        for (int i = 0; i < errorMessages.length; i++) {
            Assert.assertEquals(errorMessages[i],
                    validationErrors.get(i).getMessage());
        }
    }

    @Test
    public void convertToModelConversionFails() {
        bindAgeWithValidatorConverterValidator();
        ageField.setValue("abc");
        assertEquals(32, p.getAge());
        assertValidationErrors(binder.validate(), "Value must be a number");
    }

    @Test
    public void convertToModelNegativeAgeFailsOnIntegerValidator() {
        bindAgeWithValidatorConverterValidator();

        ageField.setValue("-5");
        assertEquals(32, p.getAge());
        assertValidationErrors(binder.validate(), "Value must be positive");
    }

    @Test
    public void convertDataToField() {
        bindAgeWithValidatorConverterValidator();
        binder.getBean().get().setAge(12);
        binder.load(binder.getBean().get());
        Assert.assertEquals("12", ageField.getValue());
    }

    @Test
    public void convertNotValidatableDataToField() {
        bindAgeWithValidatorConverterValidator();
        binder.getBean().get().setAge(-12);
        binder.load(binder.getBean().get());
        Assert.assertEquals("-12", ageField.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertInvalidDataToField() {
        TextField field = new TextField();
        StatusBean bean = new StatusBean();
        bean.setStatus("1");
        Binder<StatusBean> binder = new Binder<StatusBean>();

        Binding<StatusBean, String, String> binding = binder.forField(field)
                .withConverter(presentation -> {
                    if (presentation.equals("OK")) {
                        return "1";
                    } else if (presentation.equals("NOTOK")) {
                        return "2";
                    }
                    throw new IllegalArgumentException(
                            "Value must be OK or NOTOK");
                }, model -> {
                    if (model.equals("1")) {
                        return "OK";
                    } else if (model.equals("2")) {
                        return "NOTOK";
                    } else {
                        throw new IllegalArgumentException(
                                "Value in model must be 1 or 2");
                    }
                });
        binding.bind(StatusBean::getStatus, StatusBean::setStatus);
        binder.bind(bean);

        bean.setStatus("3");
        binder.load(bean);
    }

}
