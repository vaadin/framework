package com.vaadin.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import com.vaadin.data.Binder.Binding;
import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.NotEmptyValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.TextField;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.CoreMatchers;

public class BinderTest extends BinderTestBase<Binder<Person>, Person> {

    @Rule
    /*
     * transient to avoid interfering with serialization tests that capture a
     * test instance in a closure
     */
    public transient ExpectedException exceptionRule = ExpectedException.none();

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

        assertEquals(age, person.getAge());
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

        assertEquals(fieldValue, person.getFirstName());
    }

    @Test
    public void save_bound_beanAsDraft() {
        Binder<Person> binder = new Binder<>();
        binder.forField(nameField)
            .withValidator((value,context) -> {
                if (value.equals("Mike")) {
                    return ValidationResult.ok();
                } else {
                    return ValidationResult.error("value must be Mike");
                }
            })
            .bind(Person::getFirstName, Person::setFirstName);
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);

        Person person = new Person();

        String fieldValue = "John";
        nameField.setValue(fieldValue);

        int age = 10;
        ageField.setValue("10");

        person.setFirstName("Mark");

        binder.writeBeanAsDraft(person);

        // name is not written to draft as validation / conversion
        // does not pass
        assertNotEquals(fieldValue, person.getFirstName());
        // age is written to draft even if firstname validation
        // fails
        assertEquals(age, person.getAge());

        binder.writeBeanAsDraft(person,true);
        // name is now written despite validation as write was forced
        assertEquals(fieldValue, person.getFirstName());
    }

    @Test
    public void save_bound_bean_disable_validation_binding() throws ValidationException {
        Binder<Person> binder = new Binder<>();
        Binding<Person, String> nameBinding = binder.forField(nameField)
            .withValidator((value,context) -> {
                if (value.equals("Mike")) {
                    return ValidationResult.ok();
                } else {
                    return ValidationResult.error("value must be Mike");
                }
            })
            .bind(Person::getFirstName, Person::setFirstName);
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);

        Person person = new Person();

        String fieldValue = "John";
        nameField.setValue(fieldValue);

        int age = 10;
        ageField.setValue("10");

        person.setFirstName("Mark");

        nameBinding.setValidatorsDisabled(true);
        binder.writeBean(person);

        // name is now written as validation was disabled
        assertEquals(fieldValue, person.getFirstName());
        assertEquals(age, person.getAge());
    }

    @Test
    public void save_bound_bean_disable_validation_binder() throws ValidationException {
        Binder<Person> binder = new Binder<>();
        binder.forField(nameField)
            .withValidator((value,context) -> {
                if (value.equals("Mike")) {
                    return ValidationResult.ok();
                } else {
                    return ValidationResult.error("value must be Mike");
                }
            })
            .bind(Person::getFirstName, Person::setFirstName);
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);

        Person person = new Person();

        String fieldValue = "John";
        nameField.setValue(fieldValue);

        int age = 10;
        ageField.setValue("10");

        person.setFirstName("Mark");

        binder.setValidatorsDisabled(true);
        binder.writeBean(person);

        // name is now written as validation was disabled
        assertEquals(fieldValue, person.getFirstName());
        assertEquals(age, person.getAge());
    }

    @Test
    public void load_bound_fieldValueIsUpdated() {
        binder.bind(nameField, Person::getFirstName, Person::setFirstName);

        Person person = new Person();

        String name = "bar";
        person.setFirstName(name);
        binder.readBean(person);

        assertEquals(name, nameField.getValue());
    }

    @Test
    public void load_unbound_noChanges() {
        nameField.setValue("");

        Person person = new Person();

        String name = "bar";
        person.setFirstName(name);
        binder.readBean(person);

        assertEquals("", nameField.getValue());
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
        assertEquals(
                "Null value from bean was not converted to explicit null representation",
                nullRepresentation, nameField.getValue());

        // Verify that changes are applied to bean
        nameField.setValue(realName);
        assertEquals(
                "Bean was not correctly updated from a change in the field",
                realName, namelessPerson.getFirstName());

        // Verify conversion back to null
        nameField.setValue(nullRepresentation);
        assertEquals(
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
        assertEquals("null", namelessPerson.getFirstName());

        // Change value, see that textfield is not empty and bean is updated.
        nullTextField.setValue("");
        assertFalse(nullTextField.isEmpty());
        assertEquals("First name of person was not properly updated", "",
                namelessPerson.getFirstName());

        // Verify that default null representation does not map back to null
        nullTextField.setValue("null");
        assertTrue(nullTextField.isEmpty());
        assertEquals("Default one-way null representation failed.", "null",
                namelessPerson.getFirstName());
    }

    @Test
    public void binding_with_null_representation_value_not_null() {
        String nullRepresentation = "Some arbitrary text";

        binder.forField(nameField).withNullRepresentation(nullRepresentation)
                .bind(Person::getFirstName, Person::setFirstName);

        assertFalse("First name in item should not be null",
                Objects.isNull(item.getFirstName()));
        binder.setBean(item);

        assertEquals("Field value was not set correctly", item.getFirstName(),
                nameField.getValue());
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

        assertEquals(customNullConverter.toString(), ageField.getValue());

        Integer salary = 11;
        ageField.setValue(salary.toString());
        assertEquals(11, salary.intValue());
    }

    @Test
    public void withConverter_writeBackValue() {
        TextField rentField = new TextField();
        rentField.setValue("");
        binder.forField(rentField).withConverter(new EuroConverter(""))
                .withNullRepresentation(BigDecimal.valueOf(0d))
                .bind(Person::getRent, Person::setRent);
        binder.setBean(item);
        rentField.setValue("10");

        assertEquals("€ 10.00", rentField.getValue());
    }

    @Test
    public void withConverter_writeBackValueDisabled() {
        TextField rentField = new TextField();
        rentField.setValue("");
        Binding<Person, BigDecimal> binding = binder.forField(rentField)
                .withConverter(new EuroConverter(""))
                .withNullRepresentation(BigDecimal.valueOf(0d))
                .bind(Person::getRent, Person::setRent);
        binder.setBean(item);
        binding.setConvertBackToPresentation(false);
        rentField.setValue("10");

        assertNotEquals("€ 10.00", rentField.getValue());
    }

    @Test
    public void beanBinder_nullRepresentationIsNotDisabled() {
        Binder<Person> binder = new Binder<>(Person.class);
        binder.forField(nameField).bind("firstName");

        Person person = new Person();
        binder.setBean(person);

        assertEquals("", nameField.getValue());
    }

    @Test
    public void beanBinder_withConverter_nullRepresentationIsNotDisabled() {
        String customNullPointerRepresentation = "foo";
        Binder<Person> binder = new Binder<>(Person.class);
        binder.forField(nameField)
                .withConverter(value -> value,
                        value -> value == null ? customNullPointerRepresentation
                                : value)
                .bind("firstName");

        Person person = new Person();
        binder.setBean(person);

        assertEquals(customNullPointerRepresentation, nameField.getValue());
    }

    @Test
    public void withValidator_doesNotDisablesDefaulNullRepresentation() {
        String nullRepresentation = "foo";
        binder.forField(nameField).withNullRepresentation(nullRepresentation)
                .withValidator(new NotEmptyValidator<>(""))
                .bind(Person::getFirstName, Person::setFirstName);
        item.setFirstName(null);
        binder.setBean(item);

        assertEquals(nullRepresentation, nameField.getValue());

        String newValue = "bar";
        nameField.setValue(newValue);
        assertEquals(newValue, item.getFirstName());
    }

    @Test
    public void setRequired_withErrorMessage_fieldGetsRequiredIndicatorAndValidator() {
        TextField textField = new TextField();
        assertFalse(textField.isRequiredIndicatorVisible());

        BindingBuilder<Person, String> bindingBuilder = binder.forField(textField);
        assertFalse(textField.isRequiredIndicatorVisible());

        bindingBuilder.asRequired("foobar");
        assertTrue(textField.isRequiredIndicatorVisible());

        Binding<Person, String> binding = bindingBuilder.bind(Person::getFirstName, Person::setFirstName);
        binder.setBean(item);
        assertNull(textField.getErrorMessage());

        textField.setValue(textField.getEmptyValue());
        ErrorMessage errorMessage = textField.getErrorMessage();
        assertNotNull(errorMessage);
        assertEquals("foobar", errorMessage.getFormattedHtmlMessage());

        textField.setValue("value");
        assertNull(textField.getErrorMessage());
        assertTrue(textField.isRequiredIndicatorVisible());

        binding.setAsRequiredEnabled(false);
        assertFalse(textField.isRequiredIndicatorVisible());
    }

    @Test
    public void readNullBeanRemovesError() {
        TextField textField = new TextField();
        binder.forField(textField).asRequired("foobar")
                .bind(Person::getFirstName, Person::setFirstName);
        assertTrue(textField.isRequiredIndicatorVisible());
        assertNull(textField.getErrorMessage());

        binder.readBean(item);
        assertNull(textField.getErrorMessage());

        textField.setValue(textField.getEmptyValue());
        assertTrue(textField.isRequiredIndicatorVisible());
        assertNotNull(textField.getErrorMessage());

        binder.readBean(null);
        assertTrue(textField.isRequiredIndicatorVisible());
        assertNull(textField.getErrorMessage());
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
            assertSame(Locale.CANADA, context.getLocale().get());
            return "foobar";
        });
        assertTrue(textField.isRequiredIndicatorVisible());

        binding.bind(Person::getFirstName, Person::setFirstName);
        binder.setBean(item);
        assertNull(textField.getErrorMessage());
        assertEquals(0, invokes.get());

        textField.setValue(textField.getEmptyValue());
        ErrorMessage errorMessage = textField.getErrorMessage();
        assertNotNull(errorMessage);
        assertEquals("foobar", errorMessage.getFormattedHtmlMessage());
        // validation is done for all changed bindings once.
        assertEquals(1, invokes.get());

        textField.setValue("value");
        assertNull(textField.getErrorMessage());
        assertTrue(textField.isRequiredIndicatorVisible());
    }

    @Test
    public void setRequired_withCustomValidator_fieldGetsRequiredIndicatorAndValidator() {
        TextField textField = new TextField();
        textField.setLocale(Locale.CANADA);
        assertFalse(textField.isRequiredIndicatorVisible());

        BindingBuilder<Person, String> binding = binder.forField(textField);
        assertFalse(textField.isRequiredIndicatorVisible());
        AtomicInteger invokes = new AtomicInteger();

        Validator<String> customRequiredValidator = (value, context) -> {
            invokes.incrementAndGet();
            if (StringUtils.isBlank(value)) {
                return ValidationResult.error("Input is required.");
            }
            return ValidationResult.ok();
        };
        binding.asRequired(customRequiredValidator);
        assertTrue(textField.isRequiredIndicatorVisible());

        binding.bind(Person::getFirstName, Person::setFirstName);
        binder.setBean(item);
        assertNull(textField.getErrorMessage());
        assertEquals(1, invokes.get());

        textField.setValue("        ");
        ErrorMessage errorMessage = textField.getErrorMessage();
        assertNotNull(errorMessage);
        assertEquals("Input&#32;is&#32;required&#46;",
                errorMessage.getFormattedHtmlMessage());
        // validation is done for all changed bindings once.
        assertEquals(2, invokes.get());

        textField.setValue("value");
        assertNull(textField.getErrorMessage());
        assertTrue(textField.isRequiredIndicatorVisible());
    }

    @Test
    public void setRequired_withCustomValidator_modelConverterBeforeValidator() {
        TextField textField = new TextField();
        textField.setLocale(Locale.CANADA);
        assertFalse(textField.isRequiredIndicatorVisible());

        Converter<String, String> stringBasicPreProcessingConverter = new Converter<String, String>() {
            @Override
            public Result<String> convertToModel(String value,
                    ValueContext context) {
                if (StringUtils.isBlank(value)) {
                    return Result.ok(null);
                }
                return Result.ok(StringUtils.trim(value));
            }

            @Override
            public String convertToPresentation(String value,
                    ValueContext context) {
                if (value == null) {
                    return "";
                }
                return value;
            }
        };

        AtomicInteger invokes = new AtomicInteger();
        Validator<String> customRequiredValidator = (value, context) -> {
            invokes.incrementAndGet();
            if (value == null) {
                return ValidationResult.error("Input required.");
            }
            return ValidationResult.ok();
        };

        binder.forField(textField)
                .withConverter(stringBasicPreProcessingConverter)
                .asRequired(customRequiredValidator)
                .bind(Person::getFirstName, Person::setFirstName);

        binder.setBean(item);
        assertNull(textField.getErrorMessage());
        assertEquals(1, invokes.get());

        textField.setValue("        ");
        ErrorMessage errorMessage = textField.getErrorMessage();
        assertNotNull(errorMessage);
        assertEquals("Input&#32;required&#46;",
                errorMessage.getFormattedHtmlMessage());
        // validation is done for all changed bindings once.
        assertEquals(2, invokes.get());

        textField.setValue("value");
        assertNull(textField.getErrorMessage());
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
        assertEquals(2, invokes.get());

        lastNameField.setValue("");
        assertEquals(2, invokes.get());

        firstNameField.setValue("");
        assertEquals(3, invokes.get());

        binder.removeBean();
        Person person = new Person();
        person.setFirstName("a");
        person.setLastName("a");
        binder.readBean(person);
        // reading from a bean causes 2:
        assertEquals(5, invokes.get());

        lastNameField.setValue("");
        assertEquals(5, invokes.get());

        firstNameField.setValue("");
        assertEquals(6, invokes.get());
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
    public void setReadonlyShouldIgnoreBindingsWithNullSetter() {
        binder.bind(nameField, Person::getFirstName, null);
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);

        binder.setReadOnly(true);
        assertTrue("Name field should be ignored but should be readonly",
                nameField.isReadOnly());
        assertTrue("Age field should be readonly", ageField.isReadOnly());

        binder.setReadOnly(false);
        assertTrue("Name field should be ignored and should remain readonly",
                nameField.isReadOnly());
        assertFalse("Age field should not be readonly", ageField.isReadOnly());

        nameField.setReadOnly(false);
        binder.setReadOnly(false);
        assertFalse("Name field should be ignored and remain not readonly",
                nameField.isReadOnly());
        assertFalse("Age field should not be readonly", ageField.isReadOnly());

        binder.setReadOnly(true);
        assertFalse("Name field should be ignored and remain not readonly",
                nameField.isReadOnly());
        assertTrue("Age field should be readonly", ageField.isReadOnly());
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

        assertTrue(binder.isValid());

        nameField.setValue("fail field validation");
        assertFalse(binder.isValid());

        nameField.setValue("");
        assertTrue(binder.isValid());

        nameField.setValue("fail bean validation");
        assertFalse(binder.isValid());
    }

    @Test
    public void isValidTest_unbound_binder() {
        binder.forField(nameField)
                .withValidator(Validator.from(
                        name -> !name.equals("fail field validation"), ""))
                .bind(Person::getFirstName, Person::setFirstName);

        assertTrue(binder.isValid());

        nameField.setValue("fail field validation");
        assertFalse(binder.isValid());

        nameField.setValue("");
        assertTrue(binder.isValid());
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
        assertEquals(0, binder.getFields().count());
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        assertStreamEquals(Stream.of(nameField), binder.getFields());
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);
        assertStreamEquals(Stream.of(nameField, ageField), binder.getFields());
    }

    private void assertStreamEquals(Stream<?> s1, Stream<?> s2) {
        assertArrayEquals(s1.toArray(), s2.toArray());
    }

    @Test
    public void multiple_calls_to_same_binding_builder() {
        String stringLength = "String length failure";
        String conversion = "Conversion failed";
        String ageLimit = "Age not in valid range";
        BindingValidationStatus validation;

        binder = new Binder<>(Person.class);
        BindingBuilder builder = binder.forField(ageField);
        builder.withValidator(new StringLengthValidator(stringLength, 0, 3));
        builder.withConverter(new StringToIntegerConverter(conversion));
        builder.withValidator(new IntegerRangeValidator(ageLimit, 3, 150));
        Binding<Person, ?> bind = builder.bind("age");

        binder.setBean(item);

        ageField.setValue("123123");
        validation = bind.validate();
        assertTrue(validation.isError());
        assertEquals(stringLength, validation.getMessage().get());

        ageField.setValue("age");
        validation = bind.validate();
        assertTrue(validation.isError());
        assertEquals(conversion, validation.getMessage().get());

        ageField.setValue("256");
        validation = bind.validate();
        assertTrue(validation.isError());
        assertEquals(ageLimit, validation.getMessage().get());

        ageField.setValue("30");
        validation = bind.validate();
        assertFalse(validation.isError());
        assertEquals(30, item.getAge());
    }

    @Test
    public void remove_field_binding() {
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter("Can't convert"))
                .bind(Person::getAge, Person::setAge);

        // Test that the binding does work
        assertTrue("Field not initially empty", ageField.isEmpty());
        binder.setBean(item);
        assertEquals("Binding did not work", String.valueOf(item.getAge()),
                ageField.getValue());
        binder.setBean(null);
        assertTrue("Field not cleared", ageField.isEmpty());

        // Remove the binding
        binder.removeBinding(ageField);

        // Test that it does not work anymore
        binder.setBean(item);
        assertNotEquals("Binding was not removed",
                String.valueOf(item.getAge()), ageField.getValue());
    }

    @Test
    public void remove_propertyname_binding() {
        // Use a bean aware binder
        Binder<Person> binder = new Binder<>(Person.class);

        binder.bind(nameField, "firstName");

        // Test that the binding does work
        assertTrue("Field not initially empty", nameField.isEmpty());
        binder.setBean(item);
        assertEquals("Binding did not work", item.getFirstName(),
                nameField.getValue());
        binder.setBean(null);
        assertTrue("Field not cleared", nameField.isEmpty());

        // Remove the binding
        binder.removeBinding("firstName");

        // Test that it does not work anymore
        binder.setBean(item);
        assertNotEquals("Binding was not removed", item.getFirstName(),
                nameField.getValue());
    }

    @Test
    public void remove_binding() {
        Binding<Person, Integer> binding = binder.forField(ageField)
                .withConverter(new StringToIntegerConverter("Can't convert"))
                .bind(Person::getAge, Person::setAge);

        // Test that the binding does work
        assertTrue("Field not initially empty", ageField.isEmpty());
        binder.setBean(item);
        assertEquals("Binding did not work", String.valueOf(item.getAge()),
                ageField.getValue());
        binder.setBean(null);
        assertTrue("Field not cleared", ageField.isEmpty());

        // Remove the binding
        binder.removeBinding(binding);

        // Test that it does not work anymore
        binder.setBean(item);
        assertNotEquals("Binding was not removed",
                String.valueOf(item.getAge()), ageField.getValue());
    }

    @Test
    public void remove_binding_fromFieldValueChangeListener() {
        // Add listener before bind to make sure it will be executed first.
        nameField.addValueChangeListener(e -> {
            if (e.getValue() == "REMOVE") {
                binder.removeBinding(nameField);
            }
        });

        binder.bind(nameField, Person::getFirstName, Person::setFirstName);

        binder.setBean(item);

        nameField.setValue("REMOVE");

        // Removed binding should not update bean.
        assertNotEquals("REMOVE", item.getFirstName());
    }

    @Test
    public void beanvalidation_two_fields_not_equal() {
        TextField lastNameField = new TextField();
        setBeanValidationFirstNameNotEqualsLastName(nameField, lastNameField);

        item.setLastName("Valid");
        binder.setBean(item);

        assertFalse("Should not have changes initially", binder.hasChanges());
        assertTrue("Should be ok initially", binder.validate().isOk());
        assertNotEquals("First name and last name are not same initially",
                item.getFirstName(), item.getLastName());

        nameField.setValue("Invalid");

        assertFalse("First name change not handled", binder.hasChanges());
        assertTrue(
                "Changing first name to something else than last name should be ok",
                binder.validate().isOk());

        lastNameField.setValue("Invalid");

        assertTrue("Last name should not be saved yet", binder.hasChanges());
        assertFalse("Binder validation should fail with pending illegal value",
                binder.validate().isOk());
        assertNotEquals("Illegal last name should not be stored to bean",
                item.getFirstName(), item.getLastName());

        nameField.setValue("Valid");

        assertFalse("With new first name both changes should be saved",
                binder.hasChanges());
        assertTrue("Everything should be ok for 'Valid Invalid'",
                binder.validate().isOk());
        assertNotEquals("First name and last name should never match.",
                item.getFirstName(), item.getLastName());
    }

    @Test
    public void beanvalidation_initially_broken_bean() {
        TextField lastNameField = new TextField();
        setBeanValidationFirstNameNotEqualsLastName(nameField, lastNameField);

        item.setLastName(item.getFirstName());
        binder.setBean(item);

        assertFalse(binder.isValid());
        assertFalse(binder.validate().isOk());
    }

    @Test(expected = IllegalStateException.class)
    public void beanvalidation_isValid_throws_with_readBean() {
        TextField lastNameField = new TextField();
        setBeanValidationFirstNameNotEqualsLastName(nameField, lastNameField);

        binder.readBean(item);

        assertTrue(binder.isValid());
    }

    @Test(expected = IllegalStateException.class)
    public void beanvalidation_validate_throws_with_readBean() {
        TextField lastNameField = new TextField();
        setBeanValidationFirstNameNotEqualsLastName(nameField, lastNameField);

        binder.readBean(item);

        assertTrue(binder.validate().isOk());
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
                assertTrue("Expecting error", statusChange.isError());
            } else {
                assertFalse("Unexpected error", statusChange.isError());
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

        binder.setValidationStatusHandler(
                status -> status.notifyBindingValidationStatusHandlers());

        String initialName = item.getFirstName();
        int initialAge = item.getAge();

        binder.setBean(item);

        // Test specific error handling.
        bindingHandler.expectingError = true;
        nameField.setValue("");

        // Test default error handling.
        ageField.setValue("foo");
        assertTrue("Component error does not contain error message",
                ageField.getComponentError().getFormattedHtmlMessage()
                        .contains(ageError));

        // Restore values and test no errors.
        ageField.setValue(String.valueOf(initialAge));
        assertNull("There should be no component error",
                ageField.getComponentError());

        bindingHandler.expectingError = false;
        nameField.setValue(initialName);

        // Assert that the handler was called.
        assertEquals(
                "Unexpected callCount to binding validation status handler", 6,
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

        assertEquals("Binding still affects bean even after unbind",
                ageBeforeUnbind, String.valueOf(item.getAge()));
    }

    @Test
    public void info_validator_not_considered_error() {
        String infoMessage = "Young";
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter("Can't convert"))
                .withValidator(i -> i > 5, infoMessage, ErrorLevel.INFO)
                .bind(Person::getAge, Person::setAge);

        binder.setBean(item);
        ageField.setValue("3");
        assertEquals(infoMessage,
                ageField.getComponentError().getFormattedHtmlMessage());
        assertEquals(ErrorLevel.INFO,
                ageField.getComponentError().getErrorLevel());

        assertEquals(3, item.getAge());
    }

    @Test
    public void two_asRequired_fields_without_initial_values() {
        binder.forField(nameField).asRequired("Empty name").bind(p -> "",
                (p, s) -> {
                });
        binder.forField(ageField).asRequired("Empty age").bind(p -> "",
                (p, s) -> {
                });

        binder.setBean(item);
        assertNull("Initially there should be no errors",
                nameField.getComponentError());
        assertNull("Initially there should be no errors",
                ageField.getComponentError());

        nameField.setValue("Foo");
        assertNull("Name with a value should not be an error",
                nameField.getComponentError());
        assertNull(
                "Age field should not be in error, since it has not been modified.",
                ageField.getComponentError());

        nameField.setValue("");
        assertNotNull("Empty name should now be in error.",
                nameField.getComponentError());
        assertNull("Age field should still be ok.",
                ageField.getComponentError());
    }

    @Test
    public void refreshValueFromBean() {
        Binding<Person, String> binding = binder.bind(nameField,
                Person::getFirstName, Person::setFirstName);

        binder.readBean(item);

        assertEquals("Name should be read from the item", item.getFirstName(),
                nameField.getValue());

        nameField.setValue("foo");

        assertNotEquals("Name should be different from the item",
                item.getFirstName(), nameField.getValue());

        binding.read(item);

        assertEquals("Name should be read again from the item",
                item.getFirstName(), nameField.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void remove_binding_from_different_binder() {
        Binder<Person> anotherBinder = new Binder<>();
        Binding<Person, String> binding = anotherBinder.bind(nameField,
                Person::getFirstName, Person::setFirstName);
        binder.removeBinding(binding);
    }

    @Test(expected = IllegalStateException.class)
    public void bindWithNullSetterSetReadWrite() {
        Binding<Person, String> binding = binder.bind(nameField,
                Person::getFirstName, null);
        binding.setReadOnly(false);
    }

    @Test
    public void bindWithNullSetterShouldMarkFieldAsReadonly() {
        Binding<Person, String> nameBinding = binder.bind(nameField,
                Person::getFirstName, null);
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);

        assertTrue("Name field should be readonly", nameField.isReadOnly());
        assertFalse("Age field should not be readonly", ageField.isReadOnly());
        assertTrue("Binding should be marked readonly",
                nameBinding.isReadOnly());
    }

    @Test
    public void setReadOnly_binding() {
        Binding<Person, String> binding = binder.bind(nameField,
                Person::getFirstName, Person::setFirstName);

        assertFalse("Binding should not be readonly", binding.isReadOnly());
        assertFalse("Name field should not be readonly",
                nameField.isReadOnly());

        binding.setReadOnly(true);
        assertTrue("Binding should be readonly", binding.isReadOnly());
        assertTrue("Name field should be readonly", nameField.isReadOnly());
    }

    @Test
    public void conversionWithLocaleBasedErrorMessage() {
        String fiError = "VIRHE";
        String otherError = "ERROR";

        binder.forField(ageField).withConverter(new StringToIntegerConverter(
                context -> context.getLocale().map(Locale::getLanguage)
                        .orElse("en").equals("fi") ? fiError : otherError))
                .bind(Person::getAge, Person::setAge);

        binder.setBean(item);

        ageField.setValue("not a number");

        assertEquals(otherError,
                ageField.getErrorMessage().getFormattedHtmlMessage());
        ageField.setLocale(new Locale("fi"));
        // Re-validate to get the error message with correct locale
        binder.validate();
        assertEquals(fiError,
                ageField.getErrorMessage().getFormattedHtmlMessage());
    }

    @Test
    public void valueChangeListenerOrder() {
        AtomicBoolean beanSet = new AtomicBoolean();
        nameField.addValueChangeListener(e -> {
            if (!beanSet.get()) {
                assertEquals("Value in bean updated earlier than expected",
                        e.getOldValue(), item.getFirstName());
            }
        });
        binder.bind(nameField, Person::getFirstName, Person::setFirstName);
        nameField.addValueChangeListener(e -> {
            if (!beanSet.get()) {
                assertEquals("Value in bean not updated when expected",
                        e.getValue(), item.getFirstName());
            }
        });

        beanSet.set(true);
        binder.setBean(item);
        beanSet.set(false);

        nameField.setValue("Foo");
    }

    @Test
    public void nonSymetricValue_setBean_writtenToBean() {
        binder.bind(nameField, Person::getLastName, Person::setLastName);

        assertNull(item.getLastName());

        binder.setBean(item);

        assertEquals("", item.getLastName());
    }

    @Test
    public void nonSymmetricValue_readBean_beanNotTouched() {
        binder.bind(nameField, Person::getLastName, Person::setLastName);
        binder.addValueChangeListener(
                event -> fail("No value change event should be fired"));

        assertNull(item.getLastName());

        binder.readBean(item);

        assertNull(item.getLastName());
    }

    @Test
    public void symetricValue_setBean_beanNotUpdated() {
        binder.bind(nameField, Person::getFirstName, Person::setFirstName);

        binder.setBean(new Person() {
            @Override
            public String getFirstName() {
                return "First";
            }

            @Override
            public void setFirstName(String firstName) {
                fail("Setter should not be called");
            }
        });
    }

    @Test
    public void nullRejetingField_nullValue_wrappedExceptionMentionsNullRepresentation() {
        TextField field = createNullAnd42RejectingFieldWithEmptyValue("");

        Binder<AtomicReference<Integer>> binder = createIntegerConverterBinder(
                field);

        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("null representation");
        exceptionRule.expectCause(CoreMatchers.isA(NullPointerException.class));

        binder.readBean(new AtomicReference<>());
    }


    @Test
    public void nullRejetingField_otherRejectedValue_originalExceptionIsThrown() {
        TextField field = createNullAnd42RejectingFieldWithEmptyValue("");

        Binder<AtomicReference<Integer>> binder = createIntegerConverterBinder(
                field);

        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("42");

        binder.readBean(new AtomicReference<>(Integer.valueOf(42)));
    }

    @Test(expected = NullPointerException.class)
    public void nullAcceptingField_nullValue_originalExceptionIsThrown() {
        /*
         * Edge case with a field that throws for null but has null as the empty
         * value. This is most likely the case if the field doesn't explicitly
         * reject null values but is instead somehow broken so that any value is
         * rejected.
         */
        TextField field = createNullAnd42RejectingFieldWithEmptyValue(null);

        Binder<AtomicReference<Integer>> binder = createIntegerConverterBinder(
                field);

        binder.readBean(new AtomicReference<>(null));
    }

    // See: https://github.com/vaadin/framework/issues/12356
    @Test
    public void validationShouldNotRunTwice() {
        TextField salaryField = new TextField();
        count = 0;
        item.setSalaryDouble(100d);
        binder.forField(ageField)
            .withConverter(new StringToDoubleConverter(""))
            .bind(Person::getSalaryDouble, Person::setSalaryDouble);
        binder.setBean(item);
        binder.addValueChangeListener(event -> {
        	count++;
        });

        ageField.setValue("1000");
        assertTrue(binder.isValid());

        ageField.setValue("salary");
        assertFalse(binder.isValid());

        ageField.setValue("2000");

        // Without fix for #12356 count will be 5
        assertEquals(3, count);

        assertEquals(new Double(2000), item.getSalaryDouble());
    }

    private TextField createNullAnd42RejectingFieldWithEmptyValue(
            String emptyValue) {
        return new TextField() {
            @Override
            public void setValue(String value) {
                if (value == null) {
                    throw new NullPointerException("Null value");
                } else if ("42".equals(value)) {
                    throw new IllegalArgumentException("42 is not allowed");
                }
                super.setValue(value);
            }

            @Override
            public String getEmptyValue() {
                return emptyValue;
            }
        };
    }

    private Binder<AtomicReference<Integer>> createIntegerConverterBinder(
            TextField field) {
        Binder<AtomicReference<Integer>> binder = new Binder<>();
        binder.forField(field)
                .withConverter(new StringToIntegerConverter("Must have number"))
                .bind(AtomicReference::get, AtomicReference::set);
        return binder;
    }

    /**
     * A converter that adds/removes the euro sign and formats currencies with
     * two decimal places.
     */
    public class EuroConverter extends StringToBigDecimalConverter {

        public EuroConverter() {
            super("defaultErrorMessage");
        }

        public EuroConverter(String errorMessage) {
            super(errorMessage);
        }

        @Override
        public Result<BigDecimal> convertToModel(String value,
                ValueContext context) {
            if (value.isEmpty()) {
                return Result.ok(null);
            }
            value = value.replaceAll("[€\\s]", "").trim();
            if (value.isEmpty()) {
                value = "0";
            }
            return super.convertToModel(value, context);
        }

        @Override
        public String convertToPresentation(BigDecimal value,
                ValueContext context) {
            if (value == null) {
                return convertToPresentation(BigDecimal.ZERO, context);
            }
            return "€ " + super.convertToPresentation(value, context);
        }

        @Override
        protected NumberFormat getFormat(Locale locale) {
            // Always display currency with two decimals
            NumberFormat format = super.getFormat(locale);
            if (format instanceof DecimalFormat) {
                ((DecimalFormat) format).setMaximumFractionDigits(2);
                ((DecimalFormat) format).setMinimumFractionDigits(2);
            }
            return format;
        }
    }
}
