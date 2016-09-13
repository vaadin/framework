package com.vaadin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Binder.Binding;
import com.vaadin.data.ValidationStatus.Status;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.validator.NotEmptyValidator;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class BinderTest {

    private static final String EMPTY_ERROR_MESSAGE = "Value cannot be empty";

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
            EMPTY_ERROR_MESSAGE);
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
    public void save_unbound_noChanges() throws ValidationException {
        Binder<Person> binder = new Binder<>();
        Person person = new Person();

        int age = 10;
        person.setAge(age);

        binder.save(person);

        Assert.assertEquals(age, person.getAge());
    }

    @Test
    public void save_bound_beanIsUpdated() throws ValidationException {
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
    public void save_null_beanIsUpdated() throws ValidationException {
        Binder<Person> binder = new Binder<>();
        binder.forField(nameField).withConverter(fieldValue -> {
            if ("null".equals(fieldValue)) {
                return null;
            } else {
                return fieldValue;
            }
        }, model -> {
            return model;
        }).bind(Person::getFirstName, Person::setFirstName);

        Person person = new Person();
        person.setFirstName("foo");

        nameField.setValue("null");

        binder.save(person);

        Assert.assertNull(person.getFirstName());
    }

    @Test(expected = ValidationException.class)
    public void save_fieldValidationErrors() throws ValidationException {
        Binder<Person> binder = new Binder<>();
        String msg = "foo";
        binder.forField(nameField).withValidator(new NotEmptyValidator<>(msg))
                .bind(Person::getFirstName, Person::setFirstName);

        Person person = new Person();
        String firstName = "foo";
        person.setFirstName(firstName);
        nameField.setValue("");
        try {
            binder.save(person);
        } finally {
            // Bean should not have been updated
            Assert.assertEquals(firstName, person.getFirstName());
        }
    }

    @Test(expected = ValidationException.class)
    public void save_beanValidationErrors() throws ValidationException {
        Binder<Person> binder = new Binder<>();
        binder.forField(nameField).withValidator(new NotEmptyValidator<>("a"))
                .bind(Person::getFirstName, Person::setFirstName);

        binder.withValidator(Validator.from(person -> false, "b"));

        Person person = new Person();
        nameField.setValue("foo");
        try {
            binder.save(person);
        } finally {
            // Bean should have been updated for item validation but reverted
            Assert.assertNull(person.getFirstName());
        }
    }

    @Test
    public void save_fieldsAndBeanLevelValidation() throws ValidationException {
        Binder<Person> binder = new Binder<>();
        binder.forField(nameField).withValidator(new NotEmptyValidator<>("a"))
                .bind(Person::getFirstName, Person::setFirstName);

        binder.withValidator(
                Validator.from(person -> person.getLastName() != null, "b"));

        Person person = new Person();
        person.setLastName("bar");
        nameField.setValue("foo");
        binder.save(person);
        Assert.assertEquals(nameField.getValue(), person.getFirstName());
        Assert.assertEquals("bar", person.getLastName());
    }

    @Test
    public void saveIfValid_fieldValidationErrors() {
        Binder<Person> binder = new Binder<>();
        String msg = "foo";
        binder.forField(nameField).withValidator(new NotEmptyValidator<>(msg))
                .bind(Person::getFirstName, Person::setFirstName);

        Person person = new Person();
        person.setFirstName("foo");
        nameField.setValue("");
        Assert.assertFalse(binder.saveIfValid(person));
        Assert.assertEquals("foo", person.getFirstName());
    }

    @Test
    public void saveIfValid_noValidationErrors() {
        Binder<Person> binder = new Binder<>();
        String msg = "foo";
        binder.forField(nameField).withValidator(new NotEmptyValidator<>(msg))
                .bind(Person::getFirstName, Person::setFirstName);

        Person person = new Person();
        person.setFirstName("foo");
        nameField.setValue("bar");

        Assert.assertTrue(binder.saveIfValid(person));
        Assert.assertEquals("bar", person.getFirstName());
    }

    @Test
    public void saveIfValid_beanValidationErrors() {
        Binder<Person> binder = new Binder<>();
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);

        String msg = "foo";
        binder.withValidator(Validator.<Person> from(
                prsn -> prsn.getAddress() != null || prsn.getEmail() != null,
                msg));

        Person person = new Person();
        person.setFirstName("foo");
        nameField.setValue("");
        Assert.assertFalse(binder.saveIfValid(person));

        Assert.assertEquals("foo", person.getFirstName());
    }

    @Test
    public void save_validationErrors_exceptionContainsErrors()
            throws ValidationException {
        Binder<Person> binder = new Binder<>();
        String msg = "foo";
        Binding<Person, String, String> nameBinding = binder.forField(nameField)
                .withValidator(new NotEmptyValidator<>(msg));
        nameBinding.bind(Person::getFirstName, Person::setFirstName);

        Binding<Person, String, Integer> ageBinding = binder.forField(ageField)
                .withConverter(stringToInteger).withValidator(notNegative);
        ageBinding.bind(Person::getAge, Person::setAge);

        Person person = new Person();
        nameField.setValue("");
        ageField.setValue("-1");
        try {
            binder.save(person);
            Assert.fail();
        } catch (ValidationException exception) {
            List<ValidationStatus<?>> validationErrors = exception
                    .getFieldValidationErrors();
            Assert.assertEquals(2, validationErrors.size());
            ValidationStatus<?> error = validationErrors.get(0);
            Assert.assertEquals(nameField, error.getField());
            Assert.assertEquals(msg, error.getMessage().get());

            error = validationErrors.get(1);
            Assert.assertEquals(ageField, error.getField());
            Assert.assertEquals("Value must be positive",
                    error.getMessage().get());
        }
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

        BinderValidationStatus<Person> status = binder.validate();

        Assert.assertTrue(status.isOk());
    }

    @Test
    public void bound_validatorsAreOK_noErrors() {
        Binder<Person> binder = new Binder<>();
        Binding<Person, String, String> binding = binder.forField(nameField);
        binding.withValidator(Validator.alwaysPass()).bind(Person::getFirstName,
                Person::setFirstName);

        nameField.setComponentError(new UserError(""));
        BinderValidationStatus<Person> status = binder.validate();

        Assert.assertTrue(status.isOk());
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

        BinderValidationStatus<Person> status = binder.validate();
        List<ValidationStatus<?>> errors = status.getFieldValidationErrors();

        Assert.assertEquals(1, errors.size());

        ValidationStatus<?> validationStatus = errors.stream().findFirst()
                .get();
        String msg = validationStatus.getMessage().get();
        Assert.assertEquals(msg1, msg);

        HasValue<?> field = validationStatus.getField();

        Assert.assertEquals(nameField, field);

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
        assertValidationErrors(binder.validate(), EMPTY_ERROR_MESSAGE);
    }

    private void assertValidationErrors(
            List<ValidationStatus<?>> validationErrors,
            String... errorMessages) {
        Assert.assertEquals(errorMessages.length, validationErrors.size());
        for (int i = 0; i < errorMessages.length; i++) {
            Assert.assertEquals(errorMessages[i],
                    validationErrors.get(i).getMessage().get());
        }
    }

    private void assertValidationErrors(BinderValidationStatus<Person> status,
            String... errorMessages) {
        assertValidationErrors(status.getFieldValidationErrors(),
                errorMessages);
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
        Binder<StatusBean> binder = new Binder<>();

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

    @Test
    public void bindingWithStatusHandler_handlerGetsEvents() {
        AtomicReference<ValidationStatus<?>> statusCapture = new AtomicReference<>();
        Binding<Person, String, String> binding = binder.forField(nameField)
                .withValidator(notEmpty).withValidationStatusHandler(evt -> {
                    Assert.assertNull(statusCapture.get());
                    statusCapture.set(evt);
                });
        binding.bind(Person::getFirstName, Person::setFirstName);

        nameField.setValue("");

        // First validation fails => should be event with ERROR status and
        // message
        binder.validate();

        Assert.assertNotNull(statusCapture.get());
        ValidationStatus<?> evt = statusCapture.get();
        Assert.assertEquals(Status.ERROR, evt.getStatus());
        Assert.assertEquals(EMPTY_ERROR_MESSAGE, evt.getMessage().get());
        Assert.assertEquals(nameField, evt.getField());

        nameField.setValue("foo");

        statusCapture.set(null);
        // Second validation succeeds => should be event with OK status and
        // no message
        binder.validate();

        evt = statusCapture.get();
        Assert.assertNotNull(evt);
        Assert.assertEquals(Status.OK, evt.getStatus());
        Assert.assertFalse(evt.getMessage().isPresent());
        Assert.assertEquals(nameField, evt.getField());
    }

    @Test
    public void bindingWithStatusHandler_defaultStatusHandlerIsReplaced() {
        Binding<Person, String, String> binding = binder.forField(nameField)
                .withValidator(notEmpty).withValidationStatusHandler(evt -> {
                });
        binding.bind(Person::getFirstName, Person::setFirstName);

        Assert.assertNull(nameField.getComponentError());

        nameField.setValue("");

        // First validation fails => should be event with ERROR status and
        // message
        binding.validate();

        // default behavior should update component error for the nameField
        Assert.assertNull(nameField.getComponentError());
    }

    @Test
    public void bindingWithStatusLabel_labelIsUpdatedAccordingStatus() {
        Label label = new Label();

        Binding<Person, String, String> binding = binder.forField(nameField)
                .withValidator(notEmpty).withStatusLabel(label);
        binding.bind(Person::getFirstName, Person::setFirstName);

        nameField.setValue("");

        // First validation fails => should be event with ERROR status and
        // message
        binding.validate();

        Assert.assertTrue(label.isVisible());
        Assert.assertEquals(EMPTY_ERROR_MESSAGE, label.getValue());

        nameField.setValue("foo");

        // Second validation succeeds => should be event with OK status and
        // no message
        binding.validate();

        Assert.assertFalse(label.isVisible());
        Assert.assertEquals("", label.getValue());
    }

    @Test
    public void bindingWithStatusLabel_defaultStatusHandlerIsReplaced() {
        Label label = new Label();

        Binding<Person, String, String> binding = binder.forField(nameField)
                .withValidator(notEmpty).withStatusLabel(label);
        binding.bind(Person::getFirstName, Person::setFirstName);

        Assert.assertNull(nameField.getComponentError());

        nameField.setValue("");

        // First validation fails => should be event with ERROR status and
        // message
        binding.validate();

        // default behavior should update component error for the nameField
        Assert.assertNull(nameField.getComponentError());
    }

    @Test(expected = IllegalStateException.class)
    public void bindingWithStatusHandler_addAfterBound() {
        Binding<Person, String, String> binding = binder.forField(nameField)
                .withValidator(notEmpty);
        binding.bind(Person::getFirstName, Person::setFirstName);

        binding.withValidationStatusHandler(evt -> Assert.fail());
    }

    @Test(expected = IllegalStateException.class)
    public void bindingWithStatusLabel_addAfterBound() {
        Label label = new Label();

        Binding<Person, String, String> binding = binder.forField(nameField)
                .withValidator(notEmpty);
        binding.bind(Person::getFirstName, Person::setFirstName);

        binding.withStatusLabel(label);
    }

    @Test(expected = IllegalStateException.class)
    public void bindingWithStatusLabel_setAfterHandler() {
        Label label = new Label();

        Binding<Person, String, String> binding = binder.forField(nameField);

        binding.withValidationStatusHandler(event -> {
        });

        binding.withStatusLabel(label);
    }

    @Test(expected = IllegalStateException.class)
    public void bindingWithStatusHandler_setAfterLabel() {
        Label label = new Label();

        Binding<Person, String, String> binding = binder.forField(nameField);

        binding.withStatusLabel(label);

        binding.withValidationStatusHandler(event -> {
        });
    }

    @Test(expected = IllegalStateException.class)
    public void bingingWithStatusHandler_setAfterOtherHandler() {

        Binding<Person, String, String> binding = binder.forField(nameField);

        binding.withValidationStatusHandler(event -> {
        });

        binding.withValidationStatusHandler(event -> {
        });
    }

    @Test
    public void validate_failedBeanValidatorWithoutFieldValidators() {
        Binder<Person> binder = new Binder<>();
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);

        String msg = "foo";
        binder.withValidator(Validator.from(bean -> false, msg));
        Person person = new Person();
        binder.bind(person);

        List<ValidationStatus<?>> errors = binder.validate()
                .getFieldValidationErrors();
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void validate_failedBeanValidatorWithFieldValidator() {
        String msg = "foo";

        Binder<Person> binder = new Binder<>();
        Binding<Person, String, String> binding = binder.forField(nameField)
                .withValidator(new NotEmptyValidator<>(msg));
        binding.bind(Person::getFirstName, Person::setFirstName);

        binder.withValidator(Validator.from(bean -> false, msg));
        Person person = new Person();
        binder.bind(person);

        List<ValidationStatus<?>> errors = binder.validate()
                .getFieldValidationErrors();
        Assert.assertEquals(1, errors.size());
        ValidationStatus<?> error = errors.get(0);
        Assert.assertEquals(msg, error.getMessage().get());
        Assert.assertEquals(nameField, error.getField());
    }

    @Test
    public void validate_failedBothBeanValidatorAndFieldValidator() {
        String msg1 = "foo";

        Binder<Person> binder = new Binder<>();
        Binding<Person, String, String> binding = binder.forField(nameField)
                .withValidator(new NotEmptyValidator<>(msg1));
        binding.bind(Person::getFirstName, Person::setFirstName);

        String msg2 = "bar";
        binder.withValidator(Validator.from(bean -> false, msg2));
        Person person = new Person();
        binder.bind(person);

        List<ValidationStatus<?>> errors = binder.validate()
                .getFieldValidationErrors();
        Assert.assertEquals(1, errors.size());

        ValidationStatus<?> error = errors.get(0);

        Assert.assertEquals(msg1, error.getMessage().get());
        Assert.assertEquals(nameField, error.getField());
    }

    @Test
    public void validate_okBeanValidatorWithoutFieldValidators() {
        Binder<Person> binder = new Binder<>();
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);

        String msg = "foo";
        binder.withValidator(Validator.from(bean -> true, msg));
        Person person = new Person();
        binder.bind(person);

        Assert.assertFalse(binder.validate().hasErrors());
        Assert.assertTrue(binder.validate().isOk());
    }

    @Test
    public void binder_saveIfValid() {
        String msg1 = "foo";
        Binder<Person> binder = new Binder<>();
        Binding<Person, String, String> binding = binder.forField(nameField)
                .withValidator(new NotEmptyValidator<>(msg1));
        binding.bind(Person::getFirstName, Person::setFirstName);

        String beanValidatorErrorMessage = "bar";
        binder.withValidator(
                Validator.from(bean -> false, beanValidatorErrorMessage));
        Person person = new Person();
        String firstName = "first name";
        person.setFirstName(firstName);
        binder.load(person);

        nameField.setValue("");
        Assert.assertFalse(binder.saveIfValid(person));
        // check that field level-validation failed and bean is not updated
        Assert.assertEquals(firstName, person.getFirstName());

        nameField.setValue("new name");

        Assert.assertFalse(binder.saveIfValid(person));
        // Bean is updated but reverted
        Assert.assertEquals(firstName, person.getFirstName());
    }

    @Test
    public void updateBoundField_bindingValdationFails_beanLevelValidationIsNotRun() {
        bindAgeWithValidatorConverterValidator();
        bindName();

        AtomicBoolean beanLevelValidationRun = new AtomicBoolean();
        binder.withValidator(Validator.<Person> from(
                bean -> beanLevelValidationRun.getAndSet(true), ""));

        ageField.setValue("not a number");

        Assert.assertFalse(beanLevelValidationRun.get());

        nameField.setValue("foo");
        Assert.assertFalse(beanLevelValidationRun.get());
    }

    @Test
    public void updateBoundField_bindingValdationSuccess_beanLevelValidationIsRun() {
        bindAgeWithValidatorConverterValidator();
        bindName();

        AtomicBoolean beanLevelValidationRun = new AtomicBoolean();
        binder.withValidator(Validator.<Person> from(
                bean -> beanLevelValidationRun.getAndSet(true), ""));

        ageField.setValue(String.valueOf(12));

        Assert.assertTrue(beanLevelValidationRun.get());
    }

    @Test
    public void binderWithStatusHandler_fieldValidationNoBeanValidation_handlerGetsStatusUpdates() {
        AtomicReference<BinderValidationStatus<?>> statusCapture = new AtomicReference<>();
        binder.forField(nameField).withValidator(notEmpty)
                .withValidationStatusHandler(evt -> {
                    Assert.fail(
                            "Using a custom status change handler so no change should end up here");
                }).bind(Person::getFirstName, Person::setFirstName);
        binder.forField(ageField).withConverter(stringToInteger)
                .withValidator(notNegative).withValidationStatusHandler(evt -> {
                    Assert.fail(
                            "Using a custom status change handler so no change should end up here");
                }).bind(Person::getAge, Person::setAge);

        binder.setValidationStatusHandler(r -> {
            statusCapture.set(r);
        });
        binder.bind(p);
        Assert.assertNull(nameField.getComponentError());

        nameField.setValue("");
        ageField.setValue("5");

        // First binding validation fails => should be result with ERROR status
        // and message
        BinderValidationStatus<Person> status2 = binder.validate();
        BinderValidationStatus<?> status = statusCapture.get();
        Assert.assertSame(status2, status);

        Assert.assertNull(nameField.getComponentError());

        List<ValidationStatus<?>> bindingStatuses = status
                .getFieldValidationStatuses();
        Assert.assertNotNull(bindingStatuses);
        Assert.assertEquals(1, status.getFieldValidationErrors().size());
        Assert.assertEquals(2, bindingStatuses.size());

        ValidationStatus<?> r = bindingStatuses.get(0);
        Assert.assertTrue(r.isError());
        Assert.assertEquals(EMPTY_ERROR_MESSAGE, r.getMessage().get());
        Assert.assertEquals(nameField, r.getField());

        r = bindingStatuses.get(1);
        Assert.assertFalse(r.isError());
        Assert.assertFalse(r.getMessage().isPresent());
        Assert.assertEquals(ageField, r.getField());

        Assert.assertEquals(0, status.getBeanValidationResults().size());
        Assert.assertEquals(0, status.getBeanValidationErrors().size());

        nameField.setValue("foo");
        ageField.setValue("");

        statusCapture.set(null);
        // Second validation succeeds => should be result with OK status and
        // no message, and error result for age
        binder.validate();

        status = statusCapture.get();
        bindingStatuses = status.getFieldValidationStatuses();
        Assert.assertEquals(1, status.getFieldValidationErrors().size());
        Assert.assertEquals(2, bindingStatuses.size());

        r = bindingStatuses.get(0);
        Assert.assertFalse(r.isError());
        Assert.assertFalse(r.getMessage().isPresent());
        Assert.assertEquals(nameField, r.getField());

        r = bindingStatuses.get(1);
        Assert.assertTrue(r.isError());
        Assert.assertEquals("Value must be a number", r.getMessage().get());
        Assert.assertEquals(ageField, r.getField());

        Assert.assertEquals(0, status.getBeanValidationResults().size());
        Assert.assertEquals(0, status.getBeanValidationErrors().size());

        statusCapture.set(null);
        // binding validations pass, binder validation fails
        ageField.setValue("0");
        binder.validate();

        status = statusCapture.get();
        bindingStatuses = status.getFieldValidationStatuses();
        Assert.assertEquals(0, status.getFieldValidationErrors().size());
        Assert.assertEquals(2, bindingStatuses.size());

        Assert.assertEquals(0, status.getBeanValidationResults().size());
        Assert.assertEquals(0, status.getBeanValidationErrors().size());
    }

    @Test
    public void binderWithStatusHandler_fieldAndBeanLevelValidation_handlerGetsStatusUpdates() {
        AtomicReference<BinderValidationStatus<?>> statusCapture = new AtomicReference<>();
        binder.forField(nameField).withValidator(notEmpty)
                .withValidationStatusHandler(evt -> {
                    Assert.fail(
                            "Using a custom status change handler so no change should end up here");
                }).bind(Person::getFirstName, Person::setFirstName);
        binder.forField(ageField).withConverter(stringToInteger)
                .withValidator(notNegative).withValidationStatusHandler(evt -> {
                    Assert.fail(
                            "Using a custom status change handler so no change should end up here");
                }).bind(Person::getAge, Person::setAge);
        binder.withValidator(
                bean -> !bean.getFirstName().isEmpty() && bean.getAge() > 0
                        ? Result.ok(bean)
                        : Result.error("Need first name and age"));

        binder.setValidationStatusHandler(r -> {
            statusCapture.set(r);
        });
        binder.bind(p);
        Assert.assertNull(nameField.getComponentError());

        nameField.setValue("");
        ageField.setValue("5");

        // First binding validation fails => should be result with ERROR status
        // and message
        BinderValidationStatus<Person> status2 = binder.validate();
        BinderValidationStatus<?> status = statusCapture.get();
        Assert.assertSame(status2, status);

        Assert.assertNull(nameField.getComponentError());

        List<ValidationStatus<?>> bindingStatuses = status
                .getFieldValidationStatuses();
        Assert.assertNotNull(bindingStatuses);
        Assert.assertEquals(1, status.getFieldValidationErrors().size());
        Assert.assertEquals(2, bindingStatuses.size());

        ValidationStatus<?> r = bindingStatuses.get(0);
        Assert.assertTrue(r.isError());
        Assert.assertEquals(EMPTY_ERROR_MESSAGE, r.getMessage().get());
        Assert.assertEquals(nameField, r.getField());

        r = bindingStatuses.get(1);
        Assert.assertFalse(r.isError());
        Assert.assertFalse(r.getMessage().isPresent());
        Assert.assertEquals(ageField, r.getField());

        Assert.assertEquals(0, status.getBeanValidationResults().size());
        Assert.assertEquals(0, status.getBeanValidationErrors().size());

        nameField.setValue("foo");
        ageField.setValue("");

        statusCapture.set(null);
        // Second validation succeeds => should be result with OK status and
        // no message, and error result for age
        binder.validate();

        status = statusCapture.get();
        bindingStatuses = status.getFieldValidationStatuses();
        Assert.assertEquals(1, status.getFieldValidationErrors().size());
        Assert.assertEquals(2, bindingStatuses.size());

        r = bindingStatuses.get(0);
        Assert.assertFalse(r.isError());
        Assert.assertFalse(r.getMessage().isPresent());
        Assert.assertEquals(nameField, r.getField());

        r = bindingStatuses.get(1);
        Assert.assertTrue(r.isError());
        Assert.assertEquals("Value must be a number", r.getMessage().get());
        Assert.assertEquals(ageField, r.getField());

        Assert.assertEquals(0, status.getBeanValidationResults().size());
        Assert.assertEquals(0, status.getBeanValidationErrors().size());

        statusCapture.set(null);
        // binding validations pass, binder validation fails
        ageField.setValue("0");
        binder.validate();

        status = statusCapture.get();
        bindingStatuses = status.getFieldValidationStatuses();
        Assert.assertEquals(0, status.getFieldValidationErrors().size());
        Assert.assertEquals(2, bindingStatuses.size());

        Assert.assertEquals(1, status.getBeanValidationResults().size());
        Assert.assertEquals(1, status.getBeanValidationErrors().size());

        Assert.assertEquals("Need first name and age",
                status.getBeanValidationErrors().get(0).getMessage().get());
    }

    @Test
    public void binderWithStatusHandler_defaultStatusHandlerIsReplaced() {
        Binding<Person, String, String> binding = binder.forField(nameField)
                .withValidator(notEmpty).withValidationStatusHandler(evt -> {
                });
        binding.bind(Person::getFirstName, Person::setFirstName);

        Assert.assertNull(nameField.getComponentError());

        nameField.setValue("");

        // First validation fails => should be event with ERROR status and
        // message
        binding.validate();

        // no component error since default handler is replaced
        Assert.assertNull(nameField.getComponentError());
    }

    @Test
    public void binderWithStatusLabel_defaultStatusHandlerIsReplaced() {
        Label label = new Label();

        Binding<Person, String, String> binding = binder.forField(nameField)
                .withValidator(notEmpty).withStatusLabel(label);
        binding.bind(Person::getFirstName, Person::setFirstName);

        Assert.assertNull(nameField.getComponentError());

        nameField.setValue("");

        // First validation fails => should be event with ERROR status and
        // message
        binding.validate();

        // default behavior should update component error for the nameField
        Assert.assertNull(nameField.getComponentError());
    }

    @Test(expected = IllegalStateException.class)
    public void binderWithStatusHandler_addAfterBound() {
        Binding<Person, String, String> binding = binder.forField(nameField)
                .withValidator(notEmpty);
        binding.bind(Person::getFirstName, Person::setFirstName);

        binding.withValidationStatusHandler(evt -> Assert.fail());
    }

    @Test(expected = IllegalStateException.class)
    public void binderWithStatusLabel_addAfterBound() {
        Label label = new Label();

        Binding<Person, String, String> binding = binder.forField(nameField)
                .withValidator(notEmpty);
        binding.bind(Person::getFirstName, Person::setFirstName);

        binding.withStatusLabel(label);
    }

    @Test(expected = IllegalStateException.class)
    public void binderWithStatusLabel_setAfterHandler() {
        Label label = new Label();

        Binding<Person, String, String> binding = binder.forField(nameField);
        binding.bind(Person::getFirstName, Person::setFirstName);

        binder.setValidationStatusHandler(event -> {
        });

        binder.setStatusLabel(label);
    }

    @Test(expected = IllegalStateException.class)
    public void binderWithStatusHandler_setAfterLabel() {
        Label label = new Label();

        Binding<Person, String, String> binding = binder.forField(nameField);
        binding.bind(Person::getFirstName, Person::setFirstName);

        binder.setStatusLabel(label);

        binder.setValidationStatusHandler(event -> {
        });
    }

    @Test(expected = NullPointerException.class)
    public void binderWithNullStatusHandler_throws() {
        binder.setValidationStatusHandler(null);
    }

    @Test
    public void binderWithStatusHandler_replaceHandler() {
        AtomicReference<BinderValidationStatus<?>> capture = new AtomicReference<>();

        Binding<Person, String, String> binding = binder.forField(nameField);
        binding.bind(Person::getFirstName, Person::setFirstName);

        binder.setValidationStatusHandler(results -> {
            Assert.fail();
        });

        binder.setValidationStatusHandler(results -> {
            capture.set(results);
        });

        nameField.setValue("foo");
        binder.validate();

        List<ValidationStatus<?>> results = capture.get()
                .getFieldValidationStatuses();
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertFalse(results.get(0).isError());
    }

    @Test
    public void binderHasChanges() throws ValidationException {
        binder.forField(nameField)
                .withValidator(Validator.from(name -> !"".equals(name),
                        "Name can't be empty"))
                .bind(Person::getFirstName, Person::setFirstName);
        Assert.assertFalse(binder.hasChanges());
        binder.bind(p);
        Assert.assertFalse(binder.hasChanges());

        nameField.setValue("foo");
        Assert.assertTrue(binder.hasChanges());
        binder.load(p);
        Assert.assertFalse(binder.hasChanges());

        nameField.setValue("bar");
        binder.saveIfValid(new Person());
        Assert.assertFalse(binder.hasChanges());

        nameField.setValue("baz");
        binder.save(new Person());
        Assert.assertFalse(binder.hasChanges());

        nameField.setValue("");
        binder.saveIfValid(new Person());
        Assert.assertTrue(binder.hasChanges());
    }

    @Test
    public void binderBindAndLoad_clearsErrors() {
        Binding<Person, String, String> binding = binder.forField(nameField)
                .withValidator(notEmpty);
        binding.bind(Person::getFirstName, Person::setFirstName);
        binder.withValidator(bean -> bean.getFirstName().contains("error")
                ? Result.error("error") : Result.ok(bean));
        Person person = new Person();
        person.setFirstName("");
        binder.bind(person);

        // initial value is invalid but no error
        Assert.assertNull(nameField.getComponentError());

        // make error show
        nameField.setValue("foo");
        nameField.setValue("");
        Assert.assertNotNull(nameField.getComponentError());

        // bind to another person to see that error is cleared
        person = new Person();
        person.setFirstName("");
        binder.bind(person);
        // error has been cleared
        Assert.assertNull(nameField.getComponentError());

        // make show error
        nameField.setValue("foo");
        nameField.setValue("");
        Assert.assertNotNull(nameField.getComponentError());

        // load should also clear error
        binder.load(person);
        Assert.assertNull(nameField.getComponentError());

        // bind a new field that has invalid value in bean
        TextField lastNameField = new TextField();
        person.setLastName("");
        Binding<Person, String, String> binding2 = binder
                .forField(lastNameField).withValidator(notEmpty);
        binding2.bind(Person::getLastName, Person::setLastName);

        // should not have error shown
        Assert.assertNull(lastNameField.getComponentError());

        // add status label to show bean level error
        Label statusLabel = new Label();
        binder.setStatusLabel(statusLabel);
        nameField.setValue("error");

        // no error shown yet because second field validation doesn't pass
        Assert.assertEquals("", statusLabel.getValue());

        // make second field validation pass to get bean validation error
        lastNameField.setValue("foo");
        Assert.assertEquals("error", statusLabel.getValue());

        // reload bean to clear error
        binder.load(person);
        Assert.assertEquals("", statusLabel.getValue());

        // unbind() should clear all errors and status label
        nameField.setValue("");
        lastNameField.setValue("");
        Assert.assertNotNull(nameField.getComponentError());
        Assert.assertNotNull(lastNameField.getComponentError());
        statusLabel.setComponentError(new UserError("ERROR"));

        binder.unbind();
        Assert.assertNull(nameField.getComponentError());
        Assert.assertNull(lastNameField.getComponentError());
        Assert.assertEquals("", statusLabel.getValue());
    }

    @Test
    public void binderLoad_withCrossFieldValidation_clearsErrors() {
        TextField lastNameField = new TextField();
        final Predicate<String> lengthPredicate = v -> v.length() > 2;

        Binding<Person, String, String> firstNameBinding = binder
                .forField(nameField).withValidator(lengthPredicate, "length");
        firstNameBinding.bind(Person::getFirstName, Person::setFirstName);

        Binding<Person, String, String> lastNameBinding = binder
                .forField(lastNameField)
                .withValidator(v -> !nameField.getValue().isEmpty()
                        || lengthPredicate.test(v), "err")
                .withValidator(lengthPredicate, "length");
        lastNameBinding.bind(Person::getLastName, Person::setLastName);

        // this will be triggered as a new bean is bound with binder.bind(),
        // causing a validation error to be visible until reset is done
        nameField.addValueChangeListener(v -> lastNameBinding.validate());

        Person person = new Person();
        binder.bind(person);

        Assert.assertNull(nameField.getComponentError());
        Assert.assertNull(lastNameField.getComponentError());

        nameField.setValue("x");

        Assert.assertNotNull(nameField.getComponentError());
        Assert.assertNotNull(lastNameField.getComponentError());

        binder.bind(person);

        Assert.assertNull(nameField.getComponentError());
        Assert.assertNull(lastNameField.getComponentError());
    }
}
