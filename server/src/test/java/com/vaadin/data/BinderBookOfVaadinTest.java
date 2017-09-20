/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Binder.Binding;
import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.data.BindingValidationStatus.Status;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextField;

/**
 * Book of Vaadin tests.
 *
 * @author Vaadin Ltd
 *
 */
public class BinderBookOfVaadinTest {

    private static class BookPerson {
        private String lastName;
        private String email, phone, title;
        private int yearOfBirth, salaryLevel;

        public BookPerson(int yearOfBirth, int salaryLevel) {
            this.yearOfBirth = yearOfBirth;
            this.salaryLevel = salaryLevel;
        }

        public BookPerson(BookPerson origin) {
            this(origin.yearOfBirth, origin.salaryLevel);
            lastName = origin.lastName;
            email = origin.email;
            phone = origin.phone;
            title = origin.title;
        }

        public BookPerson(String name, int yearOfBirth) {
            lastName = name;
            this.yearOfBirth = yearOfBirth;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int getYearOfBirth() {
            return yearOfBirth;
        }

        public void setYearOfBirth(int yearOfBirth) {
            this.yearOfBirth = yearOfBirth;
        }

        public int getSalaryLevel() {
            return salaryLevel;
        }

        public void setSalaryLevel(int salaryLevel) {
            this.salaryLevel = salaryLevel;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }

    public static class Trip {
        private LocalDate returnDate;

        public LocalDate getReturnDate() {
            return returnDate;
        }

        public void setReturnDate(LocalDate returnDate) {
            this.returnDate = returnDate;
        }
    }

    private Binder<BookPerson> binder;

    private TextField field;
    private TextField phoneField;
    private TextField emailField;

    @Before
    public void setUp() {
        binder = new Binder<>();
        field = new TextField();
        phoneField = new TextField();
        emailField = new TextField();
        // make sure the test is not locale dependent
        field.setLocale(Locale.US);
        phoneField.setLocale(Locale.US);
        emailField.setLocale(Locale.US);
    }

    @Test
    public void loadingFromBusinessObjects() {
        // this test is just to make sure the code snippet in the book compiles
        binder.readBean(new BookPerson(1969, 50000));

        BinderValidationStatus<BookPerson> status = binder.validate();

        if (status.hasErrors()) {
            Notification.show("Validation error count: "
                    + status.getValidationErrors().size());
        }
    }

    @Test
    public void handlingCheckedException() {
        // another test just to verify that book examples actually compile
        try {
            binder.writeBean(new BookPerson(2000, 50000));
        } catch (ValidationException e) {
            Notification.show("Validation error count: "
                    + e.getValidationErrors().size());
        }
    }

    @Test
    public void simpleEmailValidator() {
        binder.forField(field)
                // Explicit validator instance
                .withValidator(new EmailValidator(
                        "This doesn't look like a valid email address"))
                .bind(BookPerson::getEmail, BookPerson::setEmail);

        field.setValue("not-email");
        BinderValidationStatus<?> status = binder.validate();
        Assert.assertEquals(1, status.getFieldValidationErrors().size());
        Assert.assertEquals("This doesn't look like a valid email address",
                status.getFieldValidationErrors().get(0).getMessage().get());
        Assert.assertEquals("This doesn't look like a valid email address",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("abc@vaadin.com");
        status = binder.validate();
        Assert.assertEquals(0, status.getBeanValidationErrors().size());
        Assert.assertNull(field.getErrorMessage());
    }

    @Test
    public void nameLengthTest() {
        binder.forField(field)
                // Validator defined based on a lambda and an error message
                .withValidator(name -> name.length() >= 3,
                        "Last name must contain at least three characters")
                .bind(BookPerson::getLastName, BookPerson::setLastName);

        field.setValue("a");
        BinderValidationStatus<?> status = binder.validate();
        Assert.assertEquals(1, status.getFieldValidationErrors().size());
        Assert.assertEquals("Last name must contain at least three characters",
                status.getFieldValidationErrors().get(0).getMessage().get());
        Assert.assertEquals("Last name must contain at least three characters",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("long last name");
        status = binder.validate();
        Assert.assertEquals(0, status.getFieldValidationErrors().size());
        Assert.assertNull(field.getErrorMessage());
    }

    @Test
    public void chainedEmailValidator() {
        binder.forField(field)
                // Explicit validator instance
                .withValidator(new EmailValidator(
                        "This doesn't look like a valid email address"))
                .withValidator(email -> email.endsWith("@acme.com"),
                        "Only acme.com email addresses are allowed")
                .bind(BookPerson::getEmail, BookPerson::setEmail);

        field.setValue("not-email");
        BinderValidationStatus<?> status = binder.validate();
        // Only one error per field should be reported
        Assert.assertEquals(1, status.getFieldValidationErrors().size());
        Assert.assertEquals("This doesn't look like a valid email address",
                status.getFieldValidationErrors().get(0).getMessage().get());
        Assert.assertEquals("This doesn't look like a valid email address",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("abc@vaadin.com");
        status = binder.validate();
        Assert.assertEquals(1, status.getFieldValidationErrors().size());
        Assert.assertEquals("Only acme.com email addresses are allowed",
                status.getFieldValidationErrors().get(0).getMessage().get());
        Assert.assertEquals("Only acme.com email addresses are allowed",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("abc@acme.com");
        status = binder.validate();
        Assert.assertEquals(0, status.getFieldValidationErrors().size());
        Assert.assertNull(field.getErrorMessage());
    }

    @Test
    public void converterBookOfVaadinExample1() {
        TextField yearOfBirthField = new TextField();
        yearOfBirthField.setLocale(Locale.US);
        // Slider for integers between 1 and 10
        Slider salaryLevelField = new Slider("Salary level", 1, 10);

        BindingBuilder<BookPerson, String> b1 = binder
                .forField(yearOfBirthField);
        BindingBuilder<BookPerson, Integer> b2 = b1.withConverter(
                new StringToIntegerConverter("Must enter a number"));
        b2.bind(BookPerson::getYearOfBirth, BookPerson::setYearOfBirth);

        BindingBuilder<BookPerson, Double> salaryBinding1 = binder
                .forField(salaryLevelField);
        BindingBuilder<BookPerson, Integer> salaryBinding2 = salaryBinding1
                .withConverter(Double::intValue, Integer::doubleValue);
        salaryBinding2.bind(BookPerson::getSalaryLevel,
                BookPerson::setSalaryLevel);

        // Test that the book code works
        BookPerson bookPerson = new BookPerson(1972, 4);
        binder.setBean(bookPerson);
        Assert.assertEquals(4.0, salaryLevelField.getValue().doubleValue(), 0);
        Assert.assertEquals("1,972", yearOfBirthField.getValue());

        bookPerson.setSalaryLevel(8);
        binder.readBean(bookPerson);
        Assert.assertEquals(8.0, salaryLevelField.getValue().doubleValue(), 0);
        bookPerson.setYearOfBirth(123);
        binder.readBean(bookPerson);
        Assert.assertEquals("123", yearOfBirthField.getValue());

        yearOfBirthField.setValue("2016");
        salaryLevelField.setValue(1.0);
        Assert.assertEquals(2016, bookPerson.getYearOfBirth());
        Assert.assertEquals(1, bookPerson.getSalaryLevel());
    }

    @Test
    public void converterBookOfVaadinExample2() {
        TextField yearOfBirthField = new TextField();

        binder.forField(yearOfBirthField)
                .withConverter(Integer::valueOf, String::valueOf,
                        // Text to use instead of the NumberFormatException
                        // message
                        "Please enter a number")
                .bind(BookPerson::getYearOfBirth, BookPerson::setYearOfBirth);

        binder.setBean(new BookPerson(1900, 5));
        yearOfBirthField.setValue("abc");
        binder.validate();
        Assert.assertEquals("Please&#32;enter&#32;a&#32;number",
                yearOfBirthField.getComponentError().getFormattedHtmlMessage());
    }

    @Test
    public void crossFieldValidation_validateUsingBinder() {
        Binder<Trip> binder = new Binder<>();
        DateField departing = new DateField("Departing");
        DateField returning = new DateField("Returning");

        Binding<Trip, LocalDate> returnBinding = binder.forField(returning)
                .withValidator(
                        returnDate -> !returnDate
                                .isBefore(departing.getValue()),
                        "Cannot return before departing")
                .bind(Trip::getReturnDate, Trip::setReturnDate);

        departing.addValueChangeListener(event -> returnBinding.validate());

        LocalDate past = LocalDate.now();
        LocalDate before = past.plusDays(1);
        LocalDate after = before.plusDays(1);

        departing.setValue(before);
        returning.setValue(after);

        BinderValidationStatus<Trip> status = binder.validate();
        Assert.assertTrue(status.getBeanValidationErrors().isEmpty());
        Assert.assertNull(departing.getComponentError());
        Assert.assertNull(returning.getComponentError());

        // update returning => validation is done against this field
        returning.setValue(past);
        status = binder.validate();

        Assert.assertFalse(status.getFieldValidationErrors().isEmpty());
        Assert.assertNotNull(returning.getComponentError());
        Assert.assertNull(departing.getComponentError());

        // set correct value back
        returning.setValue(before);
        status = binder.validate();

        Assert.assertTrue(status.getFieldValidationErrors().isEmpty());
        Assert.assertNull(departing.getComponentError());
        Assert.assertNull(returning.getComponentError());

        // update departing => validation is done because of listener added
        departing.setValue(after);
        status = binder.validate();

        Assert.assertFalse(status.getFieldValidationErrors().isEmpty());
        Assert.assertNotNull(returning.getComponentError());
        Assert.assertNull(departing.getComponentError());

    }

    @Test
    public void crossFieldValidation_validateUsingBinding() {
        Binder<Trip> binder = new Binder<>();
        DateField departing = new DateField("Departing");
        DateField returning = new DateField("Returning");

        Binding<Trip, LocalDate> returnBinding = binder.forField(returning)
                .withValidator(
                        returnDate -> !returnDate
                                .isBefore(departing.getValue()),
                        "Cannot return before departing")
                .bind(Trip::getReturnDate, Trip::setReturnDate);

        departing.addValueChangeListener(event -> returnBinding.validate());

        LocalDate past = LocalDate.now();
        LocalDate before = past.plusDays(1);
        LocalDate after = before.plusDays(1);

        departing.setValue(before);
        returning.setValue(after);

        BindingValidationStatus<LocalDate> result = returnBinding.validate();
        Assert.assertFalse(result.isError());
        Assert.assertNull(departing.getComponentError());

        // update returning => validation is done against this field
        returning.setValue(past);
        result = returnBinding.validate();

        Assert.assertTrue(result.isError());
        Assert.assertNotNull(returning.getComponentError());

        // set correct value back
        returning.setValue(before);
        result = returnBinding.validate();

        Assert.assertFalse(result.isError());
        Assert.assertNull(departing.getComponentError());

        // update departing => validation is done because of listener added
        departing.setValue(after);
        result = returnBinding.validate();

        Assert.assertTrue(result.isError());
        Assert.assertNotNull(returning.getComponentError());
    }

    @Test
    public void withStatusLabelExample() {
        Label emailStatus = new Label();

        String msg = "This doesn't look like a valid email address";
        binder.forField(field).withValidator(new EmailValidator(msg))
                .withStatusLabel(emailStatus)
                .bind(BookPerson::getEmail, BookPerson::setEmail);

        field.setValue("foo");
        binder.validate();

        Assert.assertTrue(emailStatus.isVisible());
        Assert.assertEquals(msg, emailStatus.getValue());

        field.setValue("foo@vaadin.com");
        binder.validate();

        Assert.assertFalse(emailStatus.isVisible());
        Assert.assertEquals("", emailStatus.getValue());
    }

    @Test
    public void withBindingStatusHandlerExample() {
        Label nameStatus = new Label();
        AtomicReference<BindingValidationStatus<?>> statusCapture = new AtomicReference<>();

        String msg = "Full name must contain at least three characters";
        binder.forField(field).withValidator(name -> name.length() >= 3, msg)
                .withValidationStatusHandler(status -> {
                    nameStatus.setValue(status.getMessage().orElse(""));
                    // Only show the label when validation has failed
                    boolean error = status.getStatus() == Status.ERROR;
                    nameStatus.setVisible(error);
                    statusCapture.set(status);
                }).bind(BookPerson::getLastName, BookPerson::setLastName);

        field.setValue("aa");
        binder.validate();

        Assert.assertTrue(nameStatus.isVisible());
        Assert.assertEquals(msg, nameStatus.getValue());
        Assert.assertNotNull(statusCapture.get());
        BindingValidationStatus<?> status = statusCapture.get();
        Assert.assertEquals(Status.ERROR, status.getStatus());
        Assert.assertEquals(msg, status.getMessage().get());
        Assert.assertEquals(field, status.getField());

        field.setValue("foo");
        binder.validate();

        Assert.assertFalse(nameStatus.isVisible());
        Assert.assertEquals("", nameStatus.getValue());
        Assert.assertNotNull(statusCapture.get());
        status = statusCapture.get();
        Assert.assertEquals(Status.OK, status.getStatus());
        Assert.assertFalse(status.getMessage().isPresent());
        Assert.assertEquals(field, status.getField());
    }

    @Test
    public void binder_saveIfValid() {
        Binder<BookPerson> binder = new Binder<>(BookPerson.class);

        // Phone or email has to be specified for the bean
        Validator<BookPerson> phoneOrEmail = Validator.from(
                personBean -> !"".equals(personBean.getPhone())
                        || !"".equals(personBean.getEmail()),
                "A person must have either a phone number or an email address");
        binder.withValidator(phoneOrEmail);

        binder.forField(emailField).bind("email");
        binder.forField(phoneField).bind("phone");

        // Person person = // e.g. JPA entity or bean from Grid
        BookPerson person = new BookPerson(1900, 5);
        person.setEmail("Old Email");
        // Load person data to a form
        binder.readBean(person);

        Button saveButton = new Button("Save", event -> {
            // Using saveIfValid to avoid the try-catch block that is
            // needed if using the regular save method
            if (binder.writeBeanIfValid(person)) {
                // Person is valid and updated
                // TODO Store in the database
            }
        });

        emailField.setValue("foo@bar.com");
        Assert.assertTrue(binder.writeBeanIfValid(person));
        // Person updated
        Assert.assertEquals("foo@bar.com", person.getEmail());

        emailField.setValue("");
        Assert.assertFalse(binder.writeBeanIfValid(person));
        // Person updated because phone and email are both empty
        Assert.assertEquals("foo@bar.com", person.getEmail());
    }

    @Test
    public void manyConvertersAndValidators() throws ValidationException {
        TextField yearOfBirthField = new TextField();
        binder.forField(yearOfBirthField)
                // Validator will be run with the String value of the field
                .withValidator(text -> text.length() == 4,
                        "Doesn't look like a year")
                // Converter will only be run for strings with 4 characters
                .withConverter(
                        new StringToIntegerConverter("Must enter a number"))
                // Validator will be run with the converted value
                .withValidator(year -> year >= 1900 && year <= 2000,
                        "Person must be born in the 20th century")
                .bind(BookPerson::getYearOfBirth, BookPerson::setYearOfBirth);

        yearOfBirthField.setValue("abc");
        Assert.assertEquals("Doesn't look like a year", binder.validate()
                .getFieldValidationErrors().get(0).getMessage().get());
        yearOfBirthField.setValue("abcd");
        Assert.assertEquals("Must enter a number", binder.validate()
                .getFieldValidationErrors().get(0).getMessage().get());
        yearOfBirthField.setValue("1200");
        Assert.assertEquals("Person must be born in the 20th century",
                binder.validate().getFieldValidationErrors().get(0).getMessage()
                        .get());

        yearOfBirthField.setValue("1950");
        Assert.assertFalse(binder.validate().hasErrors());
        BookPerson person = new BookPerson(1500, 12);
        binder.writeBean(person);
        Assert.assertEquals(1950, person.getYearOfBirth());
    }

    class MyConverter implements Converter<String, Integer> {
        @Override
        public Result<Integer> convertToModel(String fieldValue,
                ValueContext context) {
            // Produces a converted value or an error
            try {
                // ok is a static helper method that creates a Result
                return Result.ok(Integer.valueOf(fieldValue));
            } catch (NumberFormatException e) {
                // error is a static helper method that creates a Result
                return Result.error("Please enter a number");
            }
        }

        @Override
        public String convertToPresentation(Integer integer,
                ValueContext context) {
            // Converting to the field type should always succeed,
            // so there is no support for returning an error Result.
            return String.valueOf(integer);
        }
    }

    @Test
    public void bindUsingCustomConverter() {
        Binder<BookPerson> binder = new Binder<>();
        TextField yearOfBirthField = new TextField();

        // Using the converter
        binder.forField(yearOfBirthField).withConverter(new MyConverter())
                .bind(BookPerson::getYearOfBirth, BookPerson::setYearOfBirth);

        BookPerson p = new BookPerson(1500, 12);
        binder.setBean(p);

        yearOfBirthField.setValue("abc");
        Assert.assertTrue(binder.validate().hasErrors());
        Assert.assertEquals("Please enter a number", binder.validate()
                .getFieldValidationErrors().get(0).getMessage().get());

        yearOfBirthField.setValue("123");
        Assert.assertTrue(binder.validate().isOk());

        p.setYearOfBirth(12500);
        binder.readBean(p);
        Assert.assertEquals("12500", yearOfBirthField.getValue());
        Assert.assertTrue(binder.validate().isOk());
    }

    @Test
    public void withBinderStatusLabelExample() {
        Label formStatusLabel = new Label();

        Binder<BookPerson> binder = new Binder<>(BookPerson.class);

        binder.setStatusLabel(formStatusLabel);

        final String message = "Too young, son";
        final String message2 = "Y2K error";
        TextField yearOfBirth = new TextField();
        BookPerson p = new BookPerson(1500, 12);
        binder.forField(yearOfBirth)
                .withConverter(new StringToIntegerConverter("err"))
                .bind(BookPerson::getYearOfBirth, BookPerson::setYearOfBirth);
        binder.withValidator(bean -> bean.yearOfBirth < 2000, message)
                .withValidator(bean -> bean.yearOfBirth != 2000, message2);

        binder.setBean(p);

        // first bean validator fails and passes error message to status label
        yearOfBirth.setValue("2001");

        BinderValidationStatus<?> status = binder.validate();
        Assert.assertEquals(0, status.getFieldValidationErrors().size());
        Assert.assertEquals(1, status.getBeanValidationErrors().size());
        Assert.assertEquals(message,
                status.getBeanValidationErrors().get(0).getErrorMessage());

        Assert.assertEquals(message, formStatusLabel.getValue());

        // value is correct, status label is cleared
        yearOfBirth.setValue("1999");

        status = binder.validate();
        Assert.assertFalse(status.hasErrors());

        Assert.assertEquals("", formStatusLabel.getValue());

        // both bean validators fail, should be two error messages chained
        yearOfBirth.setValue("2000");

        status = binder.validate();
        Assert.assertEquals(2, status.getBeanValidationResults().size());
        Assert.assertEquals(0, status.getFieldValidationErrors().size());
        Assert.assertEquals(2, status.getBeanValidationErrors().size());

        // only first error is shown
        Assert.assertEquals(message, formStatusLabel.getValue());
    }

    @Test
    public void withBinderStatusHandlerExample() {
        Label formStatusLabel = new Label();

        BinderValidationStatusHandler<BookPerson> defaultHandler = binder
                .getValidationStatusHandler();

        binder.setValidationStatusHandler(status -> {
            // create an error message on failed bean level validations
            List<ValidationResult> errors = status.getBeanValidationErrors();
            String errorMessage = errors.stream()
                    .map(ValidationResult::getErrorMessage)
                    .collect(Collectors.joining("\n"));
            // show error in a label
            formStatusLabel.setValue(errorMessage);
            formStatusLabel.setVisible(!errorMessage.isEmpty());

            // Let the default handler show messages for each field
            defaultHandler.statusChange(status);
        });

        final String bindingMessage = "uneven";
        final String message = "Too young, son";
        final String message2 = "Y2K error";
        TextField yearOfBirth = new TextField();
        BookPerson p = new BookPerson(1500, 12);
        binder.forField(yearOfBirth)
                .withConverter(new StringToIntegerConverter("err"))
                .withValidator(value -> value % 2 == 0, bindingMessage)
                .bind(BookPerson::getYearOfBirth, BookPerson::setYearOfBirth);
        binder.withValidator(bean -> bean.yearOfBirth < 2000, message)
                .withValidator(bean -> bean.yearOfBirth != 2000, message2);

        binder.setBean(p);

        // first binding validation fails, no bean level validation is done
        yearOfBirth.setValue("2001");
        BinderValidationStatus<?> status = binder.validate();
        Assert.assertEquals(1, status.getFieldValidationErrors().size());
        Assert.assertEquals(bindingMessage,
                status.getFieldValidationErrors().get(0).getMessage().get());

        Assert.assertEquals("", formStatusLabel.getValue());

        // first bean validator fails and passes error message to status label
        yearOfBirth.setValue("2002");

        status = binder.validate();
        Assert.assertEquals(0, status.getFieldValidationErrors().size());
        Assert.assertEquals(1, status.getBeanValidationErrors().size());
        Assert.assertEquals(message,
                status.getBeanValidationErrors().get(0).getErrorMessage());

        Assert.assertEquals(message, formStatusLabel.getValue());

        // value is correct, status label is cleared
        yearOfBirth.setValue("1998");

        status = binder.validate();
        Assert.assertTrue(status.isOk());
        Assert.assertFalse(status.hasErrors());
        Assert.assertEquals(0, status.getFieldValidationErrors().size());
        Assert.assertEquals(0, status.getBeanValidationErrors().size());

        Assert.assertEquals("", formStatusLabel.getValue());

        // both bean validators fail, should be two error messages chained
        yearOfBirth.setValue("2000");

        status = binder.validate();
        Assert.assertEquals(0, status.getFieldValidationErrors().size());
        Assert.assertEquals(2, status.getBeanValidationErrors().size());

        Assert.assertEquals(message + "\n" + message2,
                formStatusLabel.getValue());

    }

    @Test
    public void statusChangeListener_binderIsNotBound() {
        Button saveButton = new Button();
        Button resetButton = new Button();

        AtomicBoolean eventIsFired = new AtomicBoolean(false);

        binder.addStatusChangeListener(event -> {
            boolean isValid = event.getBinder().isValid();
            boolean hasChanges = event.getBinder().hasChanges();
            eventIsFired.set(true);

            saveButton.setEnabled(hasChanges && isValid);
            resetButton.setEnabled(hasChanges);
        });
        binder.forField(field)
                .withValidator(new StringLengthValidator("", 1, 3))
                .bind(BookPerson::getLastName, BookPerson::setLastName);
        // no changes
        Assert.assertFalse(saveButton.isEnabled());
        Assert.assertFalse(resetButton.isEnabled());
        verifyEventIsFired(eventIsFired);

        BookPerson person = new BookPerson(2000, 1);
        binder.readBean(person);
        // no changes
        Assert.assertFalse(saveButton.isEnabled());
        Assert.assertFalse(resetButton.isEnabled());
        verifyEventIsFired(eventIsFired);

        field.setValue("a");
        // binder is not bound, no event fired
        // no changes: see #375. There should be a change and enabled state
        Assert.assertTrue(saveButton.isEnabled());
        Assert.assertTrue(resetButton.isEnabled());
        Assert.assertTrue(eventIsFired.get());

        binder.writeBeanIfValid(person);
        // no changes
        Assert.assertFalse(saveButton.isEnabled());
        Assert.assertFalse(resetButton.isEnabled());
        verifyEventIsFired(eventIsFired);

        binder.validate();
        // no changes
        Assert.assertFalse(saveButton.isEnabled());
        Assert.assertFalse(resetButton.isEnabled());
        verifyEventIsFired(eventIsFired);

        field.setValue("");
        // binder is not bound, no event fired
        // no changes: see #375. There should be a change and disabled state for
        // save button because of failed validation
        Assert.assertFalse(saveButton.isEnabled());
        Assert.assertTrue(resetButton.isEnabled());
        Assert.assertTrue(eventIsFired.get());
    }

    @Test
    public void statusChangeListener_binderIsBound() {
        Button saveButton = new Button();
        Button resetButton = new Button();

        AtomicBoolean eventIsFired = new AtomicBoolean(false);

        binder.addStatusChangeListener(event -> {
            boolean isValid = event.getBinder().isValid();
            boolean hasChanges = event.getBinder().hasChanges();
            eventIsFired.set(true);

            saveButton.setEnabled(hasChanges && isValid);
            resetButton.setEnabled(hasChanges);
        });
        binder.forField(field)
                .withValidator(new StringLengthValidator("", 1, 3))
                .bind(BookPerson::getLastName, BookPerson::setLastName);
        // no changes
        Assert.assertFalse(saveButton.isEnabled());
        Assert.assertFalse(resetButton.isEnabled());
        verifyEventIsFired(eventIsFired);

        BookPerson person = new BookPerson(2000, 1);
        binder.setBean(person);
        // no changes
        Assert.assertFalse(saveButton.isEnabled());
        Assert.assertFalse(resetButton.isEnabled());
        verifyEventIsFired(eventIsFired);

        field.setValue("a");
        // there are valid changes
        verifyEventIsFired(eventIsFired);

        field.setValue("");
        // there are invalid changes
        Assert.assertFalse(saveButton.isEnabled());
        Assert.assertTrue(resetButton.isEnabled());
        verifyEventIsFired(eventIsFired);

        // set valid value
        field.setValue("a");
        verifyEventIsFired(eventIsFired);
        binder.writeBeanIfValid(person);
        // there are no changes.
        Assert.assertFalse(saveButton.isEnabled());
        Assert.assertFalse(resetButton.isEnabled());
        verifyEventIsFired(eventIsFired);
    }

    @Test
    public void statusChangeListener_multipleRequiredFields() {
        Button saveButton = new Button();

        binder.addStatusChangeListener(event -> {
            boolean isValid = event.getBinder().isValid();
            boolean hasChanges = event.getBinder().hasChanges();

            saveButton.setEnabled(hasChanges && isValid);
        });

        binder.forField(field).asRequired("").bind(BookPerson::getLastName,
                BookPerson::setLastName);
        binder.forField(emailField).asRequired("").bind(BookPerson::getEmail,
                BookPerson::setEmail);

        Assert.assertFalse(saveButton.isEnabled());
        field.setValue("not empty");
        Assert.assertFalse(saveButton.isEnabled());
        emailField.setValue("not empty");
        Assert.assertTrue(saveButton.isEnabled());
        field.clear();
        Assert.assertFalse(saveButton.isEnabled());
    }

    @Test
    public void writeBean_throwsValidationException_bookExampleShouldCompile() {
        // The person to edit
        // Would be loaded from the backend in a real application
        BookPerson person = new BookPerson("John Doe", 1957);

        // Updates the value in each bound field component
        binder.readBean(person);

        Button saveButton = new Button("Save", event -> {
            try {
                binder.writeBean(person);
                // A real application would also save the updated person
                // using the application's backend
            } catch (ValidationException e) {
                Notification.show("Person could not be saved, "
                        + "please check error messages for each field.");
            }
        });

        // Updates the fields again with the previously saved values
        Button resetButton = new Button("Reset",
                event -> binder.readBean(person));
    }

    private void verifyEventIsFired(AtomicBoolean flag) {
        Assert.assertTrue(flag.get());
        flag.set(false);
    }
}
