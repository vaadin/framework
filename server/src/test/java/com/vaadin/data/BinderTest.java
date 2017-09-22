package com.vaadin.data;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Binder.Binding;
import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.NotEmptyValidator;
import com.vaadin.server.ErrorMessage;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.TextField;

import static org.junit.Assert.*;

public class BinderTest extends BinderTestBase<Binder<Person>, Person> {

    @Before
    public void setUp() {
        binder = new Binder<>();
        item = new Person();
        item.setFirstName("Johannes");
        item.setAge(32);
    }

    @Test
    public void bindNullBean_noBeanPresent() {
        binder.setBean(item);
        assertNotNull(binder.getBean());

        binder.setBean(null);
        assertNull(binder.getBean());
    }

    @Test
    public void bindNullBean_FieldsAreCleared() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);
        binder.setBean(item);
        assertEquals("No name field value", "Johannes", nameField.getValue());
        assertEquals("No age field value", "32", ageField.getValue());

        binder.setBean(null);
        assertEquals("Name field not empty", "", nameField.getValue());
        assertEquals("Age field not empty", "", ageField.getValue());
    }

    @Test
    public void clearForReadBean_boundFieldsAreCleared() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);
        binder.readBean(item);

        assertEquals("No name field value", "Johannes", nameField.getValue());
        assertEquals("No age field value", "32", ageField.getValue());

        binder.readBean(null);
        assertEquals("Name field not empty", "", nameField.getValue());
        assertEquals("Age field not empty", "", ageField.getValue());
    }

    @Test
    public void clearReadOnlyField_shouldClearField() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);

        // Make name field read only
        nameField.setReadOnly(true);

        binder.setBean(item);
        assertEquals("No name field value", "Johannes", nameField.getValue());

        binder.setBean(null);

        assertEquals("ReadOnly field not empty", "", nameField.getValue());
    }

    @Test
    public void clearBean_setsHasChangesToFalse() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);

        // Make name field read only
        nameField.setReadOnly(true);

        binder.readBean(item);
        assertEquals("No name field value", "Johannes", nameField.getValue());
        nameField.setValue("James");

        assertTrue("Binder did not have value changes", binder.hasChanges());

        binder.readBean(null);

        assertFalse("Binder has changes after clearing all fields",
                binder.hasChanges());

    }

    @Test
    public void clearReadOnlyBinder_shouldClearFields() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);

        binder.setReadOnly(true);

        binder.setBean(item);

        binder.setBean(null);
        assertEquals("ReadOnly name field not empty", "", nameField.getValue());
        assertEquals("ReadOnly age field not empty", "", ageField.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void bindNullField_throws() {
        binder.forField(null);
    }

    @Test(expected = NullPointerException.class)
    public void bindNullGetter_throws() {
        binder.bind(nameField, null, Person::setFirstName);
    }

    @Test
    public void fieldBound_bindItem_fieldValueUpdated() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        binder.setBean(item);
        assertEquals("Johannes", nameField.getValue());
    }

    @Test
    public void fieldBoundWithShortcut_bindBean_fieldValueUpdated() {
        bindName();
        assertEquals("Johannes", nameField.getValue());
    }

    @Test
    public void beanBound_updateFieldValue_beanValueUpdated() {
        binder.setBean(item);
        binder.bind(nameField, Person::getFirstName, Person::setFirstName);

        assertEquals("Johannes", nameField.getValue());
        nameField.setValue("Artur");
        assertEquals("Artur", item.getFirstName());
    }

    @Test
    public void bound_getBean_returnsBoundBean() {
        assertNull(binder.getBean());
        binder.setBean(item);
        assertSame(item, binder.getBean());
    }

    @Test
    public void unbound_getBean_returnsNothing() {
        binder.setBean(item);
        binder.removeBean();
        assertNull(binder.getBean());
    }

    @Test
    public void bound_changeFieldValue_beanValueUpdated() {
        bindName();
        nameField.setValue("Henri");
        assertEquals("Henri", item.getFirstName());
    }

    @Test
    public void unbound_changeFieldValue_beanValueNotUpdated() {
        bindName();
        nameField.setValue("Henri");
        binder.removeBean();
        nameField.setValue("Aleksi");
        assertEquals("Henri", item.getFirstName());
    }

    @Test
    public void bindNullSetter_valueChangesIgnored() {
        binder.bind(nameField, Person::getFirstName, null);
        binder.setBean(item);
        nameField.setValue("Artur");
        assertEquals(item.getFirstName(), "Johannes");
    }

    @Test
    public void bound_bindToAnotherBean_stopsUpdatingOriginal() {
        bindName();
        nameField.setValue("Leif");

        Person p2 = new Person();
        p2.setFirstName("Marlon");
        binder.setBean(p2);
        assertEquals("Marlon", nameField.getValue());
        assertEquals("Leif", item.getFirstName());
        assertSame(p2, binder.getBean());

        nameField.setValue("Ilia");
        assertEquals("Ilia", p2.getFirstName());
        assertEquals("Leif", item.getFirstName());
    }

    @Test
    public void save_unbound_noChanges() throws ValidationException {
        Binder<Person> binder = new Binder<>();
        Person person = new Person();

        int age = 10;
        person.setAge(age);

        binder.writeBean(person);

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

        binder.writeBean(person);

        Assert.assertEquals(fieldValue, person.getFirstName());
    }

    @Test
    public void load_bound_fieldValueIsUpdated() {
        binder.bind(nameField, Person::getFirstName, Person::setFirstName);

        Person person = new Person();

        String name = "bar";
        person.setFirstName(name);
        binder.readBean(person);

        Assert.assertEquals(name, nameField.getValue());
    }

    @Test
    public void load_unbound_noChanges() {
        nameField.setValue("");

        Person person = new Person();

        String name = "bar";
        person.setFirstName(name);
        binder.readBean(person);

        Assert.assertEquals("", nameField.getValue());
    }

    protected void bindName() {
        binder.bind(nameField, Person::getFirstName, Person::setFirstName);
        binder.setBean(item);
    }

    @Test
    public void binding_with_null_representation() {
        String nullRepresentation = "Some arbitrary text";
        String realName = "John";
        Person namelessPerson = new Person(null, "Doe", "", 25, Sex.UNKNOWN,
                null);

        binder.forField(nameField).withNullRepresentation(nullRepresentation)
                .bind(Person::getFirstName, Person::setFirstName);

        // Bind a person with null value and check that null representation is
        // used
        binder.setBean(namelessPerson);
        Assert.assertEquals(
                "Null value from bean was not converted to explicit null representation",
                nullRepresentation, nameField.getValue());

        // Verify that changes are applied to bean
        nameField.setValue(realName);
        Assert.assertEquals(
                "Bean was not correctly updated from a change in the field",
                realName, namelessPerson.getFirstName());

        // Verify conversion back to null
        nameField.setValue(nullRepresentation);
        Assert.assertEquals(
                "Two-way null representation did not change value back to null",
                null, namelessPerson.getFirstName());
    }

    @Test
    public void binding_with_default_null_representation() {
        TextField nullTextField = new TextField() {
            @Override
            public String getEmptyValue() {
                return "null";
            }
        };

        Person namelessPerson = new Person(null, "Doe", "", 25, Sex.UNKNOWN,
                null);
        binder.bind(nullTextField, Person::getFirstName, Person::setFirstName);
        binder.setBean(namelessPerson);

        assertTrue(nullTextField.isEmpty());
        Assert.assertEquals(null, namelessPerson.getFirstName());

        // Change value, see that textfield is not empty and bean is updated.
        nullTextField.setValue("");
        assertFalse(nullTextField.isEmpty());
        Assert.assertEquals("First name of person was not properly updated", "",
                namelessPerson.getFirstName());

        // Verify that default null representation does not map back to null
        nullTextField.setValue("null");
        assertTrue(nullTextField.isEmpty());
        Assert.assertEquals("Default one-way null representation failed.",
                "null", namelessPerson.getFirstName());
    }

    @Test
    public void binding_with_null_representation_value_not_null() {
        String nullRepresentation = "Some arbitrary text";

        binder.forField(nameField).withNullRepresentation(nullRepresentation)
                .bind(Person::getFirstName, Person::setFirstName);

        assertFalse("First name in item should not be null",
                Objects.isNull(item.getFirstName()));
        binder.setBean(item);

        Assert.assertEquals("Field value was not set correctly",
                item.getFirstName(), nameField.getValue());
    }

    @Test
    public void withConverter_disablesDefaulNullRepresentation() {
        Integer customNullConverter = 0;
        binder.forField(ageField).withNullRepresentation("foo")
                .withConverter(new StringToIntegerConverter(""))
                .withConverter(age -> age,
                        age -> age == null ? customNullConverter : age)
                .bind(Person::getSalary, Person::setSalary);
        binder.setBean(item);

        Assert.assertEquals(customNullConverter.toString(),
                ageField.getValue());

        Integer salary = 11;
        ageField.setValue(salary.toString());
        Assert.assertEquals(11, salary.intValue());
    }

    @Test
    public void beanBinder_nullRepresentationIsNotDisabled() {
        Binder<Person> binder = new Binder<>(Person.class);
        binder.forField(nameField).bind("firstName");

        Person person = new Person();
        binder.setBean(person);

        Assert.assertEquals("", nameField.getValue());
    }

    @Test
    public void beanBinder_withConverter_nullRepresentationIsNotDisabled() {
        String customNullPointerRepresentation = "foo";
        Binder<Person> binder = new Binder<>(Person.class);
        binder.forField(nameField)
                .withConverter(value -> value, value -> value == null
                        ? customNullPointerRepresentation : value)
                .bind("firstName");

        Person person = new Person();
        binder.setBean(person);

        Assert.assertEquals(customNullPointerRepresentation,
                nameField.getValue());
    }

    @Test
    public void withValidator_doesNotDisablesDefaulNullRepresentation() {
        String nullRepresentation = "foo";
        binder.forField(nameField).withNullRepresentation(nullRepresentation)
                .withValidator(new NotEmptyValidator<>(""))
                .bind(Person::getFirstName, Person::setFirstName);
        item.setFirstName(null);
        binder.setBean(item);

        Assert.assertEquals(nullRepresentation, nameField.getValue());

        String newValue = "bar";
        nameField.setValue(newValue);
        Assert.assertEquals(newValue, item.getFirstName());
    }

    @Test
    public void setRequired_withErrorMessage_fieldGetsRequiredIndicatorAndValidator() {
        TextField textField = new TextField();
        assertFalse(textField.isRequiredIndicatorVisible());

        BindingBuilder<Person, String> binding = binder.forField(textField);
        assertFalse(textField.isRequiredIndicatorVisible());

        binding.asRequired("foobar");
        assertTrue(textField.isRequiredIndicatorVisible());

        binding.bind(Person::getFirstName, Person::setFirstName);
        binder.setBean(item);
        Assert.assertNull(textField.getErrorMessage());

        textField.setValue(textField.getEmptyValue());
        ErrorMessage errorMessage = textField.getErrorMessage();
        Assert.assertNotNull(errorMessage);
        Assert.assertEquals("foobar", errorMessage.getFormattedHtmlMessage());

        textField.setValue("value");
        Assert.assertNull(textField.getErrorMessage());
        assertTrue(textField.isRequiredIndicatorVisible());
    }

    @Test
    public void readNullBeanRemovesError() {
        TextField textField = new TextField();
        binder.forField(textField).asRequired("foobar")
                .bind(Person::getFirstName, Person::setFirstName);
        Assert.assertTrue(textField.isRequiredIndicatorVisible());
        Assert.assertNull(textField.getErrorMessage());

        binder.readBean(item);
        Assert.assertNull(textField.getErrorMessage());

        textField.setValue(textField.getEmptyValue());
        Assert.assertTrue(textField.isRequiredIndicatorVisible());
        Assert.assertNotNull(textField.getErrorMessage());

        binder.readBean(null);
        assertTrue(textField.isRequiredIndicatorVisible());
        Assert.assertNull(textField.getErrorMessage());
    }

    @Test
    public void setRequired_withErrorMessageProvider_fieldGetsRequiredIndicatorAndValidator() {
        TextField textField = new TextField();
        textField.setLocale(Locale.CANADA);
        assertFalse(textField.isRequiredIndicatorVisible());

        BindingBuilder<Person, String> binding = binder.forField(textField);
        assertFalse(textField.isRequiredIndicatorVisible());
        AtomicInteger invokes = new AtomicInteger();

        binding.asRequired(context -> {
            invokes.incrementAndGet();
            Assert.assertSame(Locale.CANADA, context.getLocale().get());
            return "foobar";
        });
        assertTrue(textField.isRequiredIndicatorVisible());

        binding.bind(Person::getFirstName, Person::setFirstName);
        binder.setBean(item);
        Assert.assertNull(textField.getErrorMessage());
        Assert.assertEquals(0, invokes.get());

        textField.setValue(textField.getEmptyValue());
        ErrorMessage errorMessage = textField.getErrorMessage();
        Assert.assertNotNull(errorMessage);
        Assert.assertEquals("foobar", errorMessage.getFormattedHtmlMessage());
        // validation is done for the whole bean at once.
        Assert.assertEquals(1, invokes.get());

        textField.setValue("value");
        Assert.assertNull(textField.getErrorMessage());
        assertTrue(textField.isRequiredIndicatorVisible());
    }

    @Test
    public void validationStatusHandler_onlyRunForChangedField() {
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();

        AtomicInteger invokes = new AtomicInteger();

        binder.forField(firstNameField)
                .withValidator(new NotEmptyValidator<>(""))
                .withValidationStatusHandler(
                        validationStatus -> invokes.addAndGet(1))
                .bind(Person::getFirstName, Person::setFirstName);
        binder.forField(lastNameField)
                .withValidator(new NotEmptyValidator<>(""))
                .bind(Person::getLastName, Person::setLastName);

        binder.setBean(item);
        // setting the bean causes 2:
        Assert.assertEquals(2, invokes.get());

        lastNameField.setValue("");
        Assert.assertEquals(2, invokes.get());

        firstNameField.setValue("");
        Assert.assertEquals(3, invokes.get());

        binder.removeBean();
        Person person = new Person();
        person.setFirstName("a");
        person.setLastName("a");
        binder.readBean(person);
        // reading from a bean causes 2:
        Assert.assertEquals(5, invokes.get());

        lastNameField.setValue("");
        Assert.assertEquals(5, invokes.get());

        firstNameField.setValue("");
        Assert.assertEquals(6, invokes.get());
    }

    @Test(expected = IllegalStateException.class)
    public void noArgsConstructor_stringBind_throws() {
        binder.bind(new TextField(), "firstName");
    }

    @Test
    public void setReadOnly_unboundBinder() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);

        binder.forField(ageField);

        binder.setReadOnly(true);

        assertTrue(nameField.isReadOnly());
        assertFalse(ageField.isReadOnly());

        binder.setReadOnly(false);

        assertFalse(nameField.isReadOnly());
        assertFalse(ageField.isReadOnly());
    }

    @Test
    public void setReadOnly_boundBinder() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);

        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);

        binder.setBean(new Person());

        binder.setReadOnly(true);

        assertTrue(nameField.isReadOnly());
        assertTrue(ageField.isReadOnly());

        binder.setReadOnly(false);

        assertFalse(nameField.isReadOnly());
        assertFalse(ageField.isReadOnly());
    }

    @Test
    public void setReadOnly_binderLoadedByReadBean() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);

        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);

        binder.readBean(new Person());

        binder.setReadOnly(true);

        assertTrue(nameField.isReadOnly());
        assertTrue(ageField.isReadOnly());

        binder.setReadOnly(false);

        assertFalse(nameField.isReadOnly());
        assertFalse(ageField.isReadOnly());
    }

    @Test
    public void isValidTest_bound_binder() {
        binder.forField(nameField)
                .withValidator(Validator.from(
                        name -> !name.equals("fail field validation"), ""))
                .bind(Person::getFirstName, Person::setFirstName);

        binder.withValidator(Validator.from(
                person -> !person.getFirstName().equals("fail bean validation"),
                ""));

        binder.setBean(item);

        Assert.assertTrue(binder.isValid());

        nameField.setValue("fail field validation");
        Assert.assertFalse(binder.isValid());

        nameField.setValue("");
        Assert.assertTrue(binder.isValid());

        nameField.setValue("fail bean validation");
        Assert.assertFalse(binder.isValid());
    }

    @Test
    public void isValidTest_unbound_binder() {
        binder.forField(nameField)
                .withValidator(Validator.from(
                        name -> !name.equals("fail field validation"), ""))
                .bind(Person::getFirstName, Person::setFirstName);

        Assert.assertTrue(binder.isValid());

        nameField.setValue("fail field validation");
        Assert.assertFalse(binder.isValid());

        nameField.setValue("");
        Assert.assertTrue(binder.isValid());
    }

    @Test(expected = IllegalStateException.class)
    public void isValidTest_unbound_binder_throws_with_bean_level_validation() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        binder.withValidator(Validator.from(
                person -> !person.getFirstName().equals("fail bean validation"),
                ""));
        binder.isValid();
    }

    @Test
    public void getFields_returnsFields() {
        Assert.assertEquals(0, binder.getFields().count());
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        assertStreamEquals(Stream.of(nameField), binder.getFields());
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);
        assertStreamEquals(Stream.of(nameField, ageField), binder.getFields());
    }

    private void assertStreamEquals(Stream<?> s1, Stream<?> s2) {
        Assert.assertArrayEquals(s1.toArray(), s2.toArray());
    }

    /**
     * Access to old step in binding chain that already has a converter applied
     * to it is expected to prevent modifications.
     */
    @Test(expected = IllegalStateException.class)
    public void multiple_calls_to_same_binder_throws() {
        BindingBuilder<Person, String> forField = binder.forField(nameField);
        forField.withConverter(new StringToDoubleConverter("Failed"));
        forField.bind(Person::getFirstName, Person::setFirstName);
    }

    @Test
    public void remove_field_binding() {
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter("Can't convert"))
                .bind(Person::getAge, Person::setAge);

        // Test that the binding does work
        Assert.assertTrue("Field not initially empty", ageField.isEmpty());
        binder.setBean(item);
        Assert.assertEquals("Binding did not work",
                String.valueOf(item.getAge()), ageField.getValue());
        binder.setBean(null);
        Assert.assertTrue("Field not cleared", ageField.isEmpty());

        // Remove the binding
        binder.removeBinding(ageField);

        // Test that it does not work anymore
        binder.setBean(item);
        Assert.assertNotEquals("Binding was not removed",
                String.valueOf(item.getAge()), ageField.getValue());
    }

    @Test
    public void remove_propertyname_binding() {
        // Use a bean aware binder
        Binder<Person> binder = new Binder<>(Person.class);

        binder.bind(nameField, "firstName");

        // Test that the binding does work
        Assert.assertTrue("Field not initially empty", nameField.isEmpty());
        binder.setBean(item);
        Assert.assertEquals("Binding did not work", item.getFirstName(),
                nameField.getValue());
        binder.setBean(null);
        Assert.assertTrue("Field not cleared", nameField.isEmpty());

        // Remove the binding
        binder.removeBinding("firstName");

        // Test that it does not work anymore
        binder.setBean(item);
        Assert.assertNotEquals("Binding was not removed", item.getFirstName(),
                nameField.getValue());
    }

    @Test
    public void remove_binding() {
        Binding<Person, Integer> binding = binder.forField(ageField)
                .withConverter(new StringToIntegerConverter("Can't convert"))
                .bind(Person::getAge, Person::setAge);

        // Test that the binding does work
        Assert.assertTrue("Field not initially empty", ageField.isEmpty());
        binder.setBean(item);
        Assert.assertEquals("Binding did not work",
                String.valueOf(item.getAge()), ageField.getValue());
        binder.setBean(null);
        Assert.assertTrue("Field not cleared", ageField.isEmpty());

        // Remove the binding
        binder.removeBinding(binding);

        // Test that it does not work anymore
        binder.setBean(item);
        Assert.assertNotEquals("Binding was not removed",
                String.valueOf(item.getAge()), ageField.getValue());
    }

    @Test
    public void beanvalidation_two_fields_not_equal() {
        TextField lastNameField = new TextField();
        setBeanValidationFirstNameNotEqualsLastName(nameField, lastNameField);

        item.setLastName("Valid");
        binder.setBean(item);

        Assert.assertFalse("Should not have changes initially",
                binder.hasChanges());
        Assert.assertTrue("Should be ok initially", binder.validate().isOk());
        Assert.assertNotEquals(
                "First name and last name are not same initially",
                item.getFirstName(), item.getLastName());

        nameField.setValue("Invalid");

        Assert.assertFalse("First name change not handled",
                binder.hasChanges());
        Assert.assertTrue(
                "Changing first name to something else than last name should be ok",
                binder.validate().isOk());

        lastNameField.setValue("Invalid");

        Assert.assertTrue("Last name should not be saved yet",
                binder.hasChanges());
        Assert.assertFalse(
                "Binder validation should fail with pending illegal value",
                binder.validate().isOk());
        Assert.assertNotEquals("Illegal last name should not be stored to bean",
                item.getFirstName(), item.getLastName());

        nameField.setValue("Valid");

        Assert.assertFalse("With new first name both changes should be saved",
                binder.hasChanges());
        Assert.assertTrue("Everything should be ok for 'Valid Invalid'",
                binder.validate().isOk());
        Assert.assertNotEquals("First name and last name should never match.",
                item.getFirstName(), item.getLastName());
    }

    @Test
    public void beanvalidation_initially_broken_bean() {
        TextField lastNameField = new TextField();
        setBeanValidationFirstNameNotEqualsLastName(nameField, lastNameField);

        item.setLastName(item.getFirstName());
        binder.setBean(item);

        Assert.assertFalse(binder.isValid());
        Assert.assertFalse(binder.validate().isOk());
    }

    @Test(expected = IllegalStateException.class)
    public void beanvalidation_isValid_throws_with_readBean() {
        TextField lastNameField = new TextField();
        setBeanValidationFirstNameNotEqualsLastName(nameField, lastNameField);

        binder.readBean(item);

        Assert.assertTrue(binder.isValid());
    }

    @Test(expected = IllegalStateException.class)
    public void beanvalidation_validate_throws_with_readBean() {
        TextField lastNameField = new TextField();
        setBeanValidationFirstNameNotEqualsLastName(nameField, lastNameField);

        binder.readBean(item);

        Assert.assertTrue(binder.validate().isOk());
    }

    protected void setBeanValidationFirstNameNotEqualsLastName(
            TextField firstNameField, TextField lastNameField) {
        binder.bind(firstNameField, Person::getFirstName, Person::setFirstName);
        binder.forField(lastNameField)
                .withValidator(t -> !"foo".equals(t),
                        "Last name cannot be 'foo'")
                .bind(Person::getLastName, Person::setLastName);

        binder.withValidator(p -> !p.getFirstName().equals(p.getLastName()),
                "First name and last name can't be the same");
    }
  
    static class MyBindingHandler implements BindingValidationStatusHandler {

        boolean expectingError = false;
        int callCount = 0;

        @Override
        public void statusChange(BindingValidationStatus<?> statusChange) {
            ++callCount;
            if (expectingError) {
                Assert.assertTrue("Expecting error", statusChange.isError());
            } else {
                Assert.assertFalse("Unexpected error", statusChange.isError());
            }
        }
    }

    @Test
    public void execute_binding_status_handler_from_binder_status_handler() {
        MyBindingHandler bindingHandler = new MyBindingHandler();
        binder.forField(nameField)
                .withValidator(t -> !t.isEmpty(), "No empty values.")
                .withValidationStatusHandler(bindingHandler)
                .bind(Person::getFirstName, Person::setFirstName);

        String ageError = "CONVERSIONERROR";
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(ageError))
                .bind(Person::getAge, Person::setAge);

        binder.setValidationStatusHandler(status -> {
            status.notifyBindingValidationStatusHandlers();
        });

        String initialName = item.getFirstName();
        int initialAge = item.getAge();

        binder.setBean(item);

        // Test specific error handling.
        bindingHandler.expectingError = true;
        nameField.setValue("");

        // Test default error handling.
        ageField.setValue("foo");
        Assert.assertTrue("Component error does not contain error message",
                ageField.getComponentError().getFormattedHtmlMessage()
                        .contains(ageError));

        // Restore values and test no errors.
        ageField.setValue(String.valueOf(initialAge));
        Assert.assertNull("There should be no component error",
                ageField.getComponentError());

        bindingHandler.expectingError = false;
        nameField.setValue(initialName);

        // Assert that the handler was called.
        Assert.assertEquals(
                "Unexpected callCount to binding validation status handler", 4,
                bindingHandler.callCount);
    }
      
    @Test
    public void removed_binding_not_updates_value() {
        Binding<Person, Integer> binding = binder.forField(ageField)
            .withConverter(new StringToIntegerConverter("Can't convert"))
            .bind(Person::getAge, Person::setAge);

        binder.setBean(item);

        String modifiedAge = String.valueOf(item.getAge() + 10);
        String ageBeforeUnbind = String.valueOf(item.getAge());

        binder.removeBinding(binding);

        ageField.setValue(modifiedAge);

        Assert.assertEquals("Binding still affects bean even after unbind",
            ageBeforeUnbind, String.valueOf(item.getAge()));
    }
}
