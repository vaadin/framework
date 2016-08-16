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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Binder.Binding;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Slider;

/**
 * Book of Vaadin tests.
 *
 * @author Vaadin Ltd
 *
 */
public class BinderBookOfVaadinTest {

    static class TextField extends AbstractField<String> {

        String value = "";

        @Override
        public String getValue() {
            return value;
        }

        @Override
        protected void doSetValue(String value) {
            this.value = value;
        }
    }

    private static class BookPerson {
        private String lastName;
        private String email;
        private int yearOfBirth, salaryLevel;

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public BookPerson(int yearOfBirth, int salaryLevel) {
            this.yearOfBirth = yearOfBirth;
            this.salaryLevel = salaryLevel;
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

    }

    public static class Trip {
        private Date returnDate;

        public Date getReturnDate() {
            return returnDate;
        }

        public void setReturnDate(Date returnDate) {
            this.returnDate = returnDate;
        }
    }

    private Binder<BookPerson> binder;

    private TextField field;

    @Before
    public void setUp() {
        binder = new Binder<>();
        field = new TextField();
    }

    @Test
    public void simpleEmailValidator() {
        binder.forField(field)
                // Explicit validator instance
                .withValidator(new EmailValidator(
                        "This doesn't look like a valid email address"))
                .bind(BookPerson::getEmail, BookPerson::setEmail);

        field.setValue("not-email");
        List<ValidationError<?>> errors = binder.validate();
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("This doesn't look like a valid email address",
                errors.get(0).getMessage());
        Assert.assertEquals("This doesn't look like a valid email address",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("abc@vaadin.com");
        errors = binder.validate();
        Assert.assertEquals(0, errors.size());
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
        List<ValidationError<?>> errors = binder.validate();
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("Last name must contain at least three characters",
                errors.get(0).getMessage());
        Assert.assertEquals("Last name must contain at least three characters",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("long last name");
        errors = binder.validate();
        Assert.assertEquals(0, errors.size());
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
        List<ValidationError<?>> errors = binder.validate();
        // Only one error per field should be reported
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("This doesn't look like a valid email address",
                errors.get(0).getMessage());
        Assert.assertEquals("This doesn't look like a valid email address",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("abc@vaadin.com");
        errors = binder.validate();
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("Only acme.com email addresses are allowed",
                errors.get(0).getMessage());
        Assert.assertEquals("Only acme.com email addresses are allowed",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("abc@acme.com");
        errors = binder.validate();
        Assert.assertEquals(0, errors.size());
        Assert.assertNull(field.getErrorMessage());
    }

    @Test
    public void converterBookOfVaadinExample1() {
        TextField yearOfBirthField = new TextField();
        // Slider for integers between 1 and 10
        Slider salaryLevelField = new Slider("Salary level", 1, 10);

        Binding<BookPerson, String, String> b1 = binder
                .forField(yearOfBirthField);
        Binding<BookPerson, String, Integer> b2 = b1.withConverter(
                new StringToIntegerConverter("Must enter a number"));
        b2.bind(BookPerson::getYearOfBirth, BookPerson::setYearOfBirth);

        Binding<BookPerson, Double, Double> salaryBinding1 = binder
                .forField(salaryLevelField);
        Binding<BookPerson, Double, Integer> salaryBinding2 = salaryBinding1
                .withConverter(Double::intValue, Integer::doubleValue);
        salaryBinding2.bind(BookPerson::getSalaryLevel,
                BookPerson::setSalaryLevel);

        // Test that the book code works
        BookPerson bookPerson = new BookPerson(1972, 4);
        binder.bind(bookPerson);
        Assert.assertEquals(4.0, salaryLevelField.getValue().doubleValue(), 0);
        Assert.assertEquals("1,972", yearOfBirthField.getValue());

        bookPerson.setSalaryLevel(8);
        binder.load(bookPerson);
        Assert.assertEquals(8.0, salaryLevelField.getValue().doubleValue(), 0);
        bookPerson.setYearOfBirth(123);
        binder.load(bookPerson);
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

        binder.bind(new BookPerson(1900, 5));
        yearOfBirthField.setValue("abc");
        binder.validate();
        Assert.assertEquals("Please&#32;enter&#32;a&#32;number",
                yearOfBirthField.getComponentError().getFormattedHtmlMessage());
    }

    @Test
    public void crossFieldValidation() {
        Binder<Trip> binder = new Binder<>();
        PopupDateField departing = new PopupDateField("Departing");
        PopupDateField returning = new PopupDateField("Returning");

        Binding<Trip, Date, Date> returnBinding = binder.forField(returning)
                .withValidator(
                        returnDate -> !returnDate.before(departing.getValue()),
                        "Cannot return before departing");

        returnBinding.bind(Trip::getReturnDate, Trip::setReturnDate);
        departing.addValueChangeListener(event -> returnBinding.validate());

        Calendar calendar = Calendar.getInstance();
        Date past = calendar.getTime();
        calendar.add(1, Calendar.DAY_OF_YEAR);
        Date before = calendar.getTime();
        calendar.add(1, Calendar.DAY_OF_YEAR);
        Date after = calendar.getTime();

        departing.setValue(before);
        returning.setValue(after);

        List<ValidationError<?>> errors = binder.validate();
        Assert.assertTrue(errors.isEmpty());
        Assert.assertNull(departing.getComponentError());
        Assert.assertNull(returning.getComponentError());

        // update returning => validation is done against this field
        returning.setValue(past);
        errors = binder.validate();

        Assert.assertFalse(errors.isEmpty());
        Assert.assertNotNull(returning.getComponentError());
        Assert.assertNull(departing.getComponentError());

        // set correct value back
        returning.setValue(before);
        errors = binder.validate();

        Assert.assertTrue(errors.isEmpty());
        Assert.assertNull(departing.getComponentError());
        Assert.assertNull(returning.getComponentError());

        // update departing => validation is done because of listener added
        departing.setValue(after);
        errors = binder.validate();

        Assert.assertFalse(errors.isEmpty());
        Assert.assertNotNull(returning.getComponentError());
        Assert.assertNull(departing.getComponentError());

    }

}
