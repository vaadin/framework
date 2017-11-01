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
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.annotations.PropertyId;
import com.vaadin.data.BeanPropertySet.NestedBeanPropertyDefinition.PropertyFilterDefinition;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

public class BinderInstanceFieldTest {

    public static class BindAllFields extends FormLayout {
        private TextField firstName;
        private DateField birthDate;
    }

    public static class BindFieldsUsingAnnotation extends FormLayout {
        @PropertyId("firstName")
        private TextField nameField;

        @PropertyId("birthDate")
        private DateField birthDateField;
    }

    public static class BindNestedFieldsUsingAnnotation extends FormLayout {
        @PropertyId("address.streetAddress")
        private TextField streetAddressField;
    }

    public static class BindDeepNestedFieldsUsingAnnotation extends FormLayout {
        @PropertyId("first.address.streetAddress")
        private TextField firstStreetField;

        @PropertyId("second.address.streetAddress")
        private TextField secondStreetField;
    }

    public static class BindDeepNestingFieldsWithCircularStructure
            extends FormLayout {
        @PropertyId("child.name")
        private TextField childName;

        @PropertyId("child.child.name")
        private TextField grandchildName;

        @PropertyId("child.child.child.child.child.child.child.child.name")
        private TextField eighthLevelGrandchildName;

        @PropertyId("child.child.child.child.child.child.child.child.child.child.child.child.child.name")
        private TextField distantGreatGrandchildName;
    }

    public static class BindOnlyOneField extends FormLayout {
        private TextField firstName;
        private TextField noFieldInPerson;
    }

    public static class BindWithNoFieldInPerson extends FormLayout {
        private TextField firstName;
        private DateField birthDate;
        private TextField noFieldInPerson;
    }

    public static class BindFieldHasWrongType extends FormLayout {
        private String firstName;
        private DateField birthDate;
    }

    public static class BindGenericField extends FormLayout {
        private CustomField<String> firstName;
    }

    public static class BindGenericWrongTypeParameterField extends FormLayout {
        private CustomField<Boolean> firstName;
    }

    public static class BindWrongTypeParameterField extends FormLayout {
        private IntegerTextField firstName;
    }

    public static class BindOneFieldRequiresConverter extends FormLayout {
        private TextField firstName;
        private TextField age;
    }

    public static class BindGeneric<T> extends FormLayout {
        private CustomField<T> firstName;
    }

    public static class BindRaw extends FormLayout {
        private CustomField firstName;
    }

    public static class BindAbstract extends FormLayout {
        private AbstractTextField firstName;
    }

    public static class BindNonInstantiatableType extends FormLayout {
        private NoDefaultCtor firstName;
    }

    public static class BindComplextHierarchyGenericType extends FormLayout {
        private ComplexHierarchy firstName;
    }

    public static class NoDefaultCtor extends TextField {
        public NoDefaultCtor(int arg) {
        }
    }

    public static class IntegerTextField extends CustomField<Integer> {

    }

    public static class ComplexHierarchy extends Generic<Long> {

    }

    public static class Generic<T> extends ComplexGeneric<Boolean, String, T> {

    }

    public static class ComplexGeneric<U, V, S> extends CustomField<V> {

    }

    public static class CustomField<T> extends AbstractField<T> {

        private T value;

        @Override
        public T getValue() {
            return value;
        }

        @Override
        protected void doSetValue(T value) {
            this.value = value;
        }

    }

    final static class Couple {
        Person first;
        Person second;

        public Person getFirst() {
            return first;
        }

        public Person getSecond() {
            return second;
        }

        public void setFirst(Person first) {
            this.first = first;
        }

        public void setSecond(Person second) {
            this.second = second;
        }
    }

    final class NestingStructure {
        NestingStructure child;
        String name;

        public NestingStructure getChild() {
            return child;
        }

        public void setChild(NestingStructure child) {
            this.child = child;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void bindInstanceFields_bindAllFields() {
        BindAllFields form = new BindAllFields();
        Binder<Person> binder = new Binder<>(Person.class);
        binder.bindInstanceFields(form);

        Person person = new Person();
        person.setFirstName("foo");
        person.setBirthDate(LocalDate.now());

        binder.setBean(person);

        Assert.assertEquals(person.getFirstName(), form.firstName.getValue());
        Assert.assertEquals(person.getBirthDate(), form.birthDate.getValue());

        form.firstName.setValue("bar");
        form.birthDate.setValue(person.getBirthDate().plusDays(345));

        Assert.assertEquals(form.firstName.getValue(), person.getFirstName());
        Assert.assertEquals(form.birthDate.getValue(), person.getBirthDate());
    }

    @Test(expected = IllegalStateException.class)
    public void bind_instanceFields_noArgsConstructor() {
        BindAllFields form = new BindAllFields();
        Binder<Person> binder = new Binder<>();
        binder.bindInstanceFields(form);
    }

    @Test
    public void bindInstanceFields_bindOnlyOneFields() {
        BindOnlyOneField form = new BindOnlyOneField();
        Binder<Person> binder = new Binder<>(Person.class);
        binder.bindInstanceFields(form);

        Person person = new Person();
        person.setFirstName("foo");

        binder.setBean(person);

        Assert.assertEquals(person.getFirstName(), form.firstName.getValue());

        Assert.assertNull(form.noFieldInPerson);

        form.firstName.setValue("bar");

        Assert.assertEquals(form.firstName.getValue(), person.getFirstName());
    }

    @Test
    public void bindInstanceFields_bindNotHasValueField_fieldIsNull() {
        BindFieldHasWrongType form = new BindFieldHasWrongType();
        Binder<Person> binder = new Binder<>(Person.class);
        binder.bindInstanceFields(form);

        Person person = new Person();
        person.setFirstName("foo");

        binder.setBean(person);

        Assert.assertNull(form.firstName);
    }

    @Test
    public void bindInstanceFields_genericField() {
        BindGenericField form = new BindGenericField();
        Binder<Person> binder = new Binder<>(Person.class);
        binder.bindInstanceFields(form);

        Person person = new Person();
        person.setFirstName("foo");

        binder.setBean(person);

        Assert.assertEquals(person.getFirstName(), form.firstName.getValue());

        form.firstName.setValue("bar");

        Assert.assertEquals(form.firstName.getValue(), person.getFirstName());
    }

    @Test(expected = IllegalStateException.class)
    public void bindInstanceFields_genericFieldWithWrongTypeParameter() {
        BindGenericWrongTypeParameterField form = new BindGenericWrongTypeParameterField();
        Binder<Person> binder = new Binder<>(Person.class);
        binder.bindInstanceFields(form);
    }

    @Test(expected = IllegalStateException.class)
    public void bindInstanceFields_generic() {
        BindGeneric<String> form = new BindGeneric<>();
        Binder<Person> binder = new Binder<>(Person.class);
        binder.bindInstanceFields(form);
    }

    @Test(expected = IllegalStateException.class)
    public void bindInstanceFields_rawFieldType() {
        BindRaw form = new BindRaw();
        Binder<Person> binder = new Binder<>(Person.class);
        binder.bindInstanceFields(form);
    }

    @Test(expected = IllegalStateException.class)
    public void bindInstanceFields_abstractFieldType() {
        BindAbstract form = new BindAbstract();
        Binder<Person> binder = new Binder<>(Person.class);
        binder.bindInstanceFields(form);
    }

    @Test(expected = IllegalStateException.class)
    public void bindInstanceFields_noInstantiatableFieldType() {
        BindNonInstantiatableType form = new BindNonInstantiatableType();
        Binder<Person> binder = new Binder<>(Person.class);
        binder.bindInstanceFields(form);
    }

    @Test(expected = IllegalStateException.class)
    public void bindInstanceFields_wrongFieldType() {
        BindWrongTypeParameterField form = new BindWrongTypeParameterField();
        Binder<Person> binder = new Binder<>(Person.class);
        binder.bindInstanceFields(form);
    }

    @Test
    public void bindInstanceFields_complexGenericHierarchy() {
        BindComplextHierarchyGenericType form = new BindComplextHierarchyGenericType();
        Binder<Person> binder = new Binder<>(Person.class);
        binder.bindInstanceFields(form);

        Person person = new Person();
        person.setFirstName("foo");

        binder.setBean(person);

        Assert.assertEquals(person.getFirstName(), form.firstName.getValue());

        form.firstName.setValue("bar");

        Assert.assertEquals(form.firstName.getValue(), person.getFirstName());
    }

    @Test
    public void bindInstanceFields_bindNotHasValueField_fieldIsNotReplaced() {
        BindFieldHasWrongType form = new BindFieldHasWrongType();
        Binder<Person> binder = new Binder<>(Person.class);

        String name = "foo";
        form.firstName = name;

        Person person = new Person();
        person.setFirstName("foo");

        binder.setBean(person);

        Assert.assertEquals(name, form.firstName);
    }

    @Test
    public void bindInstanceFields_bindAllFieldsUsingAnnotations() {
        BindFieldsUsingAnnotation form = new BindFieldsUsingAnnotation();
        Binder<Person> binder = new Binder<>(Person.class);
        binder.bindInstanceFields(form);

        Person person = new Person();
        person.setFirstName("foo");
        person.setBirthDate(LocalDate.now());

        binder.setBean(person);

        Assert.assertEquals(person.getFirstName(), form.nameField.getValue());
        Assert.assertEquals(person.getBirthDate(),
                form.birthDateField.getValue());

        form.nameField.setValue("bar");
        form.birthDateField.setValue(person.getBirthDate().plusDays(345));

        Assert.assertEquals(form.nameField.getValue(), person.getFirstName());
        Assert.assertEquals(form.birthDateField.getValue(),
                person.getBirthDate());
    }

    @Test
    public void bindInstanceFields_bindNestedFieldUsingAnnotation() {
        BindNestedFieldsUsingAnnotation form = new BindNestedFieldsUsingAnnotation();
        Binder<Person> binder = new Binder<>(Person.class, true);
        binder.bindInstanceFields(form);

        Person person = new Person();
        Address address = new Address();
        address.setStreetAddress("Foo st.");
        person.setAddress(address);

        binder.setBean(person);

        Assert.assertEquals("Reading nested properties bound using annotation",
                person.getAddress().getStreetAddress(),
                form.streetAddressField.getValue());

        form.streetAddressField.setValue("Bar ave.");
        Assert.assertEquals("Changing nested properties bound using annotation",
                form.streetAddressField.getValue(),
                person.getAddress().getStreetAddress());
    }

    @Test
    public void bindInstanceFields_bindDeepNestedFieldsUsingAnnotation() {
        BindDeepNestedFieldsUsingAnnotation form = new BindDeepNestedFieldsUsingAnnotation();
        Binder<Couple> binder = new Binder<>(Couple.class, true);
        binder.bindInstanceFields(form);
        Person first = new Person();
        Person second = new Person();
        Address firstAddress = new Address();
        firstAddress.setStreetAddress("Foo st.");
        first.setAddress(firstAddress);
        Address secondAddress = new Address();
        second.setAddress(secondAddress);
        secondAddress.setStreetAddress("Bar ave.");
        Couple couple = new Couple();
        couple.setFirst(first);
        couple.setSecond(second);

        binder.setBean(couple);

        Assert.assertEquals("Binding deep nested properties using annotation",
                couple.first.getAddress().getStreetAddress(),
                form.firstStreetField.getValue());
        Assert.assertEquals(
                "Binding parallel deep nested properties using annotation",
                couple.second.getAddress().getStreetAddress(),
                form.secondStreetField.getValue());

        form.firstStreetField.setValue(second.getAddress().getStreetAddress());
        Assert.assertEquals("Updating value in deep nested properties",
                form.firstStreetField.getValue(),
                first.getAddress().getStreetAddress());
    }

    @Test
    public void bindInstanceFields_circular() {
        BindDeepNestingFieldsWithCircularStructure form = new BindDeepNestingFieldsWithCircularStructure();
        Binder<NestingStructure> binder = new Binder<>(NestingStructure.class,
                true);
        binder.bindInstanceFields(form);
        NestingStructure parent = new NestingStructure();
        parent.setName("parent");
        NestingStructure child = new NestingStructure();
        child.setName("child");
        parent.setChild(child);
        NestingStructure grandchild = new NestingStructure();
        grandchild.setName("grandchild");
        child.setChild(grandchild);
        NestingStructure root = grandchild;
        for (int i = 1; i < 15; i++) {
            NestingStructure ns = new NestingStructure();
            ns.setName("great " + root.getName());
            root.setChild(ns);
            root = ns;
        }
        binder.setBean(parent);
        Assert.assertEquals(child.getName(), form.childName.getValue());
        Assert.assertEquals(grandchild.getName(),
                form.grandchildName.getValue());
        Assert.assertNotNull(
                "Reading nested properties within default supported nested depth (max 10 levels)",
                form.eighthLevelGrandchildName);
        // only 10 levels of nesting properties are scanned by default
        Assert.assertNull(
                "By default, only 10 levels of nesting properties are scanned.",
                form.distantGreatGrandchildName);
    }

    @Test
    public void bindInstanceFields_customNestingLevel() {
        BindDeepNestingFieldsWithCircularStructure form = new BindDeepNestingFieldsWithCircularStructure();
        int customScanningDepth = 5;
        PropertyFilterDefinition shallowFilter = new PropertyFilterDefinition(
                customScanningDepth, Arrays.asList("java.lang"));
        Binder<NestingStructure> binder = new Binder<>(BeanPropertySet
                .get(NestingStructure.class, true, shallowFilter));
        binder.bindInstanceFields(form);
        NestingStructure parent = new NestingStructure();
        parent.setName("parent");
        NestingStructure child = new NestingStructure();
        child.setName("child");
        parent.setChild(child);
        NestingStructure grandchild = new NestingStructure();
        grandchild.setName("grandchild");
        child.setChild(grandchild);
        NestingStructure root = grandchild;
        for (int i = 1; i < 15; i++) {
            NestingStructure ns = new NestingStructure();
            ns.setName("great " + root.getName());
            root.setChild(ns);
            root = ns;
        }
        binder.setBean(parent);
        Assert.assertEquals(child.getName(), form.childName.getValue());
        Assert.assertEquals(
                "Reading 3rd level nesting works when custom scanning depth is 5",
                grandchild.getName(), form.grandchildName.getValue());
        Assert.assertNull(
                "Reading eighth level nesting doesn't work when custom scanning depth is 5",
                form.eighthLevelGrandchildName);
    }

    @Test
    public void bindInstanceFields_bindNotBoundFieldsOnly_customBindingIsNotReplaced() {
        BindAllFields form = new BindAllFields();
        Binder<Person> binder = new Binder<>(Person.class);

        TextField name = new TextField();
        form.firstName = name;
        binder.forField(form.firstName)
                .withValidator(
                        new StringLengthValidator("Name is invalid", 3, 10))
                .bind("firstName");

        binder.bindInstanceFields(form);

        Person person = new Person();
        String personName = "foo";
        person.setFirstName(personName);
        person.setBirthDate(LocalDate.now());

        binder.setBean(person);

        Assert.assertEquals(person.getFirstName(), form.firstName.getValue());
        Assert.assertEquals(person.getBirthDate(), form.birthDate.getValue());
        // the instance is not overridden
        Assert.assertEquals(name, form.firstName);

        form.firstName.setValue("aa");
        form.birthDate.setValue(person.getBirthDate().plusDays(345));

        Assert.assertEquals(personName, person.getFirstName());
        Assert.assertEquals(form.birthDate.getValue(), person.getBirthDate());

        Assert.assertFalse(binder.validate().isOk());
    }

    @Test
    public void bindInstanceFields_fieldsAreConfigured_customBindingIsNotReplaced() {
        BindWithNoFieldInPerson form = new BindWithNoFieldInPerson();
        Binder<Person> binder = new Binder<>(Person.class);

        TextField name = new TextField();
        form.firstName = name;
        binder.forField(form.firstName)
                .withValidator(
                        new StringLengthValidator("Name is invalid", 3, 10))
                .bind("firstName");
        TextField ageField = new TextField();
        form.noFieldInPerson = ageField;
        binder.forField(form.noFieldInPerson)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);

        binder.bindInstanceFields(form);

        Person person = new Person();
        String personName = "foo";
        int age = 11;
        person.setFirstName(personName);
        person.setAge(age);

        binder.setBean(person);

        Assert.assertEquals(person.getFirstName(), form.firstName.getValue());
        Assert.assertEquals(String.valueOf(person.getAge()),
                form.noFieldInPerson.getValue());
        // the instances are not overridden
        Assert.assertEquals(name, form.firstName);
        Assert.assertEquals(ageField, form.noFieldInPerson);

        form.firstName.setValue("aa");
        age += 56;
        form.noFieldInPerson.setValue(String.valueOf(age));

        Assert.assertEquals(personName, person.getFirstName());
        Assert.assertEquals(form.noFieldInPerson.getValue(),
                String.valueOf(person.getAge()));

        Assert.assertFalse(binder.validate().isOk());
    }

    @Test
    public void bindInstanceFields_preconfiguredFieldNotBoundToPropertyPreserved() {
        BindOneFieldRequiresConverter form = new BindOneFieldRequiresConverter();
        form.age = new TextField();
        form.firstName = new TextField();
        Binder<Person> binder = new Binder<>(Person.class);
        binder.forField(form.age)
                .withConverter(str -> Integer.parseInt(str) / 2,
                        integer -> Integer.toString(integer * 2))
                .bind(Person::getAge, Person::setAge);
        binder.bindInstanceFields(form);
        Person person = new Person();
        person.setFirstName("first");
        person.setAge(45);
        binder.setBean(person);
        Assert.assertEquals("90", form.age.getValue());
    }

    @Test
    public void bindInstanceFields_explicitelyBoundFieldAndNotBoundField() {
        BindOnlyOneField form = new BindOnlyOneField();
        Binder<Person> binder = new Binder<>(Person.class);

        binder.forField(new TextField()).bind("firstName");

        binder.bindInstanceFields(form);
    }

    @Test
    public void bindInstanceFields_tentativelyBoundFieldAndNotBoundField() {
        BindOnlyOneField form = new BindOnlyOneField();
        Binder<Person> binder = new Binder<>(Person.class);

        TextField field = new TextField();
        form.firstName = field;

        // This is an incomplete binding which is supposed to be configured
        // manually
        binder.forMemberField(field);

        // bindInstanceFields will not complain even though it can't bind
        // anything as there is a binding in progress (an exception will be
        // thrown later if the binding is not completed)
        binder.bindInstanceFields(form);
    }
}
