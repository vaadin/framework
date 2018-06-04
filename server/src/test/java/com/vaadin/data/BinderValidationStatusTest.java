package com.vaadin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Binder.Binding;
import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.data.BindingValidationStatus.Status;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Label;

public class BinderValidationStatusTest
        extends BinderTestBase<Binder<Person>, Person> {

    protected final static BindingValidationStatusHandler NOOP = event -> {
    };

    @Before
    public void setUp() {
        binder = new Binder<>();
        item = new Person();
        item.setFirstName("Johannes");
        item.setAge(32);
    }

    //
    // Binding-level status handler
    //

    @Test
    public void bindingWithStatusHandler_handlerGetsEvents() {
        AtomicReference<BindingValidationStatus<?>> statusCapture = new AtomicReference<>();
        BindingBuilder<Person, String> binding = binder.forField(nameField)
                .withValidator(notEmpty).withValidationStatusHandler(evt -> {
                    assertNull(statusCapture.get());
                    statusCapture.set(evt);
                });
        binding.bind(Person::getFirstName, Person::setFirstName);

        nameField.setValue("");

        // First validation fails => should be event with ERROR status and
        // message
        binder.validate();

        assertNotNull(statusCapture.get());
        BindingValidationStatus<?> evt = statusCapture.get();
        assertEquals(Status.ERROR, evt.getStatus());
        assertEquals(EMPTY_ERROR_MESSAGE, evt.getMessage().get());
        assertEquals(nameField, evt.getField());

        statusCapture.set(null);
        nameField.setValue("foo");

        statusCapture.set(null);
        // Second validation succeeds => should be event with OK status and
        // no message
        binder.validate();

        evt = statusCapture.get();
        assertNotNull(evt);
        assertEquals(Status.OK, evt.getStatus());
        assertFalse(evt.getMessage().isPresent());
        assertEquals(nameField, evt.getField());
    }

    @Test
    public void bindingWithStatusHandler_defaultStatusHandlerIsReplaced() {
        Binding<Person, String> binding = binder.forField(nameField)
                .withValidator(notEmpty).withValidationStatusHandler(evt -> {
                }).bind(Person::getFirstName, Person::setFirstName);

        assertNull(nameField.getComponentError());

        nameField.setValue("");

        // First validation fails => should be event with ERROR status and
        // message
        binding.validate();

        // default behavior should update component error for the nameField
        assertNull(nameField.getComponentError());
    }

    @Test
    public void bindingWithStatusLabel_labelIsUpdatedAccordingStatus() {
        Label label = new Label();

        Binding<Person, String> binding = binder.forField(nameField)
                .withValidator(notEmpty).withStatusLabel(label)
                .bind(Person::getFirstName, Person::setFirstName);

        nameField.setValue("");

        // First validation fails => should be event with ERROR status and
        // message
        binding.validate();

        assertTrue(label.isVisible());
        assertEquals(EMPTY_ERROR_MESSAGE, label.getValue());

        nameField.setValue("foo");

        // Second validation succeeds => should be event with OK status and
        // no message
        binding.validate();

        assertFalse(label.isVisible());
        assertEquals("", label.getValue());
    }

    @Test
    public void bindingWithStatusLabel_defaultStatusHandlerIsReplaced() {
        Label label = new Label();

        Binding<Person, String> binding = binder.forField(nameField)
                .withValidator(notEmpty).withStatusLabel(label)
                .bind(Person::getFirstName, Person::setFirstName);

        assertNull(nameField.getComponentError());

        nameField.setValue("");

        // First validation fails => should be event with ERROR status and
        // message
        binding.validate();

        // default behavior should update component error for the nameField
        assertNull(nameField.getComponentError());
    }

    @Test(expected = IllegalStateException.class)
    public void bindingWithStatusHandler_addAfterBound() {
        BindingBuilder<Person, String> binding = binder.forField(nameField)
                .withValidator(notEmpty);
        binding.bind(Person::getFirstName, Person::setFirstName);

        binding.withValidationStatusHandler(evt -> fail());
    }

    @Test(expected = IllegalStateException.class)
    public void bindingWithStatusLabel_addAfterBound() {
        Label label = new Label();

        BindingBuilder<Person, String> binding = binder.forField(nameField)
                .withValidator(notEmpty);
        binding.bind(Person::getFirstName, Person::setFirstName);

        binding.withStatusLabel(label);
    }

    @Test(expected = IllegalStateException.class)
    public void bindingWithStatusLabel_setAfterHandler() {
        Label label = new Label();

        BindingBuilder<Person, String> binding = binder.forField(nameField);

        binding.withValidationStatusHandler(NOOP);

        binding.withStatusLabel(label);
    }

    @Test(expected = IllegalStateException.class)
    public void bindingWithStatusHandler_setAfterLabel() {
        Label label = new Label();

        BindingBuilder<Person, String> binding = binder.forField(nameField);

        binding.withStatusLabel(label);

        binding.withValidationStatusHandler(NOOP);
    }

    @Test(expected = IllegalStateException.class)
    public void bindingWithStatusHandler_setAfterOtherHandler() {

        BindingBuilder<Person, String> binding = binder.forField(nameField);

        binding.withValidationStatusHandler(NOOP);

        binding.withValidationStatusHandler(NOOP);
    }

    //
    // Binder-level status handler
    //

    @Test
    public void binderWithStatusHandler_fieldValidationNoBeanValidation_handlerGetsStatusUpdates() {
        AtomicReference<BinderValidationStatus<?>> statusCapture = new AtomicReference<>();
        binder.forField(nameField).withValidator(notEmpty)
                .withValidationStatusHandler(evt -> fail(
                        "Using a custom status change handler so no change should end up here"))
                .bind(Person::getFirstName, Person::setFirstName);
        binder.forField(ageField).withConverter(stringToInteger)
                .withValidator(notNegative)
                .withValidationStatusHandler(evt -> fail(
                        "Using a custom status change handler so no change should end up here"))
                .bind(Person::getAge, Person::setAge);

        binder.setValidationStatusHandler(r -> statusCapture.set(r));
        binder.setBean(item);
        assertNull(nameField.getComponentError());

        nameField.setValue("");
        ageField.setValue("5");

        // First binding validation fails => should be result with ERROR status
        // and message
        BinderValidationStatus<Person> status2 = binder.validate();
        BinderValidationStatus<?> status = statusCapture.get();
        assertSame(status2, status);

        assertNull(nameField.getComponentError());

        List<BindingValidationStatus<?>> bindingStatuses = status
                .getFieldValidationStatuses();
        assertNotNull(bindingStatuses);
        assertEquals(1, status.getFieldValidationErrors().size());
        assertEquals(2, bindingStatuses.size());

        BindingValidationStatus<?> r = bindingStatuses.get(0);
        assertTrue(r.isError());
        assertEquals(EMPTY_ERROR_MESSAGE, r.getMessage().get());
        assertEquals(nameField, r.getField());

        r = bindingStatuses.get(1);
        assertFalse(r.isError());
        assertFalse(r.getMessage().isPresent());
        assertEquals(ageField, r.getField());

        assertEquals(0, status.getBeanValidationResults().size());
        assertEquals(0, status.getBeanValidationErrors().size());

        nameField.setValue("foo");
        ageField.setValue("");

        statusCapture.set(null);
        // Second validation succeeds => should be result with OK status and
        // no message, and error result for age
        binder.validate();

        status = statusCapture.get();
        bindingStatuses = status.getFieldValidationStatuses();
        assertEquals(1, status.getFieldValidationErrors().size());
        assertEquals(2, bindingStatuses.size());

        r = bindingStatuses.get(0);
        assertFalse(r.isError());
        assertFalse(r.getMessage().isPresent());
        assertEquals(nameField, r.getField());

        r = bindingStatuses.get(1);
        assertTrue(r.isError());
        assertEquals("Value must be a number", r.getMessage().get());
        assertEquals(ageField, r.getField());

        assertEquals(0, status.getBeanValidationResults().size());
        assertEquals(0, status.getBeanValidationErrors().size());

        statusCapture.set(null);
        // binding validations pass, binder validation fails
        ageField.setValue("0");
        binder.validate();

        status = statusCapture.get();
        bindingStatuses = status.getFieldValidationStatuses();
        assertEquals(0, status.getFieldValidationErrors().size());
        assertEquals(2, bindingStatuses.size());

        assertEquals(0, status.getBeanValidationResults().size());
        assertEquals(0, status.getBeanValidationErrors().size());
    }

    @Test
    public void binderWithStatusHandler_fieldAndBeanLevelValidation_handlerGetsStatusUpdates() {
        AtomicReference<BinderValidationStatus<?>> statusCapture = new AtomicReference<>();
        binder.forField(nameField).withValidator(notEmpty)
                .withValidationStatusHandler(evt -> fail(
                        "Using a custom status change handler so no change should end up here"))
                .bind(Person::getFirstName, Person::setFirstName);
        binder.forField(ageField).withConverter(stringToInteger)
                .withValidator(notNegative)
                .withValidationStatusHandler(evt -> fail(
                        "Using a custom status change handler so no change should end up here"))
                .bind(Person::getAge, Person::setAge);
        binder.withValidator(
                bean -> !bean.getFirstName().isEmpty() && bean.getAge() > 0,
                "Need first name and age");

        binder.setValidationStatusHandler(r -> statusCapture.set(r));
        binder.setBean(item);
        assertNull(nameField.getComponentError());

        nameField.setValue("");
        ageField.setValue("5");

        // First binding validation fails => should be result with ERROR status
        // and message
        BinderValidationStatus<Person> status2 = binder.validate();
        BinderValidationStatus<?> status = statusCapture.get();
        assertSame(status2, status);

        assertNull(nameField.getComponentError());

        List<BindingValidationStatus<?>> bindingStatuses = status
                .getFieldValidationStatuses();
        assertNotNull(bindingStatuses);
        assertEquals(1, status.getFieldValidationErrors().size());
        assertEquals(2, bindingStatuses.size());

        BindingValidationStatus<?> r = bindingStatuses.get(0);
        assertTrue(r.isError());
        assertEquals(EMPTY_ERROR_MESSAGE, r.getMessage().get());
        assertEquals(nameField, r.getField());

        r = bindingStatuses.get(1);
        assertFalse(r.isError());
        assertFalse(r.getMessage().isPresent());
        assertEquals(ageField, r.getField());

        assertEquals(0, status.getBeanValidationResults().size());
        assertEquals(0, status.getBeanValidationErrors().size());

        nameField.setValue("foo");
        ageField.setValue("");

        statusCapture.set(null);
        // Second validation succeeds => should be result with OK status and
        // no message, and error result for age
        binder.validate();

        status = statusCapture.get();
        bindingStatuses = status.getFieldValidationStatuses();
        assertEquals(1, status.getFieldValidationErrors().size());
        assertEquals(2, bindingStatuses.size());

        r = bindingStatuses.get(0);
        assertFalse(r.isError());
        assertFalse(r.getMessage().isPresent());
        assertEquals(nameField, r.getField());

        r = bindingStatuses.get(1);
        assertTrue(r.isError());
        assertEquals("Value must be a number", r.getMessage().get());
        assertEquals(ageField, r.getField());

        assertEquals(0, status.getBeanValidationResults().size());
        assertEquals(0, status.getBeanValidationErrors().size());

        statusCapture.set(null);
        // binding validations pass, binder validation fails
        ageField.setValue("0");
        binder.validate();

        status = statusCapture.get();
        bindingStatuses = status.getFieldValidationStatuses();
        assertEquals(0, status.getFieldValidationErrors().size());
        assertEquals(2, bindingStatuses.size());

        assertEquals(1, status.getBeanValidationResults().size());
        assertEquals(1, status.getBeanValidationErrors().size());

        assertEquals("Need first name and age",
                status.getBeanValidationErrors().get(0).getErrorMessage());
    }

    @Test
    public void binderWithStatusHandler_defaultStatusHandlerIsReplaced() {
        Binding<Person, String> binding = binder.forField(nameField)
                .withValidator(notEmpty).withValidationStatusHandler(evt -> {
                }).bind(Person::getFirstName, Person::setFirstName);

        assertNull(nameField.getComponentError());

        nameField.setValue("");

        // First validation fails => should be event with ERROR status and
        // message
        binding.validate();

        // no component error since default handler is replaced
        assertNull(nameField.getComponentError());
    }

    @Test
    public void binderWithStatusLabel_defaultStatusHandlerIsReplaced() {
        Label label = new Label();

        Binding<Person, String> binding = binder.forField(nameField)
                .withValidator(notEmpty).withStatusLabel(label)
                .bind(Person::getFirstName, Person::setFirstName);

        assertNull(nameField.getComponentError());

        nameField.setValue("");

        // First validation fails => should be event with ERROR status and
        // message
        binding.validate();

        // default behavior should update component error for the nameField
        assertNull(nameField.getComponentError());
    }

    @Test(expected = IllegalStateException.class)
    public void binderWithStatusHandler_addAfterBound() {
        BindingBuilder<Person, String> binding = binder.forField(nameField)
                .withValidator(notEmpty);
        binding.bind(Person::getFirstName, Person::setFirstName);

        binding.withValidationStatusHandler(evt -> fail());
    }

    @Test(expected = IllegalStateException.class)
    public void binderWithStatusLabel_addAfterBound() {
        Label label = new Label();

        BindingBuilder<Person, String> binding = binder.forField(nameField)
                .withValidator(notEmpty);
        binding.bind(Person::getFirstName, Person::setFirstName);

        binding.withStatusLabel(label);
    }

    @Test(expected = IllegalStateException.class)
    public void binderWithStatusLabel_setAfterHandler() {
        Label label = new Label();

        BindingBuilder<Person, String> binding = binder.forField(nameField);
        binding.bind(Person::getFirstName, Person::setFirstName);

        binder.setValidationStatusHandler(event -> {
        });

        binder.setStatusLabel(label);
    }

    @Test(expected = IllegalStateException.class)
    public void binderWithStatusHandler_setAfterLabel() {
        Label label = new Label();

        BindingBuilder<Person, String> binding = binder.forField(nameField);
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

        BindingBuilder<Person, String> binding = binder.forField(nameField);
        binding.bind(Person::getFirstName, Person::setFirstName);

        binder.setValidationStatusHandler(results -> fail());

        binder.setValidationStatusHandler(results -> capture.set(results));

        nameField.setValue("foo");
        binder.validate();

        List<BindingValidationStatus<?>> results = capture.get()
                .getFieldValidationStatuses();
        assertNotNull(results);
        assertEquals(1, results.size());
        assertFalse(results.get(0).isError());
    }

    @Test
    public void binderValidationStatus_nullBindingStatuses() {
        boolean nonEmptyNPEThrown = false;
        try {
            BinderValidationStatus<Person> bvs = new BinderValidationStatus<>(
                    new Binder<Person>(), null, new ArrayList<>());
        } catch (NullPointerException npe) {
            nonEmptyNPEThrown = npe.getMessage() != null;
        }
        assertTrue(nonEmptyNPEThrown);
    }
}
