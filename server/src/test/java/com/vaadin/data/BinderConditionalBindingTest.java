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

import com.vaadin.tests.data.bean.Person;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class BinderConditionalBindingTest
		extends BinderTestBase<Binder<Person>, Person> {

	@Before
	public void setUp() {
		binder = new Binder<>();
		item = new Person();
	}

	@Test
	public void getBindings_enabled_and_disabled() {
		Binder.Binding<Person, String> nameBinding = binder.forField(nameField)
				.bind(Person::getFirstName, Person::setFirstName);
		nameBinding.setEnabled(() -> nameField.isVisible());
		Binder.Binding<Person, Integer> ageBinding = binder.forField(ageField)
				.withConverter(stringToInteger)
				.bind(Person::getAge, Person::setAge);

		nameField.setVisible(false);
		List<Binder.Binding> expectedBindings = Arrays.asList(nameBinding, ageBinding);
		assertEquals(expectedBindings, binder.getBindings());
	}

	@Test
	public void getBindingsEnabled() {
		Binder.Binding<Person, String> nameBinding = binder.forField(nameField)
				.bind(Person::getFirstName, Person::setFirstName);
		nameBinding.setEnabled(() -> nameField.isVisible());
		Binder.Binding<Person, Integer> ageBinding = binder.forField(ageField)
				.withConverter(stringToInteger)
				.bind(Person::getAge, Person::setAge);

		nameField.setVisible(false);
		List<Binder.Binding> expectedBindings = Arrays.asList(ageBinding);
		assertEquals(expectedBindings, binder.getBindingsEnabled());
	}

	@Test
	public void binding_enabled_by_default() {
		binder.forField(nameField)
				.asRequired()
				.bind(Person::getFirstName, Person::setFirstName);

		assertEquals(binder.getBindings(), binder.getBindingsEnabled());
	}

	@Test
	public void asRequired_binding_disabled() {
		binder.forField(nameField)
				.asRequired()
				.bind(Person::getFirstName, Person::setFirstName)
				.setEnabled(() -> nameField.isVisible());

		nameField.setVisible(true);
		nameField.setValue("");
		assertFalse(binder.isValid());
		assertEquals(binder.getBindings(), binder.getBindingsEnabled());

		nameField.setVisible(false);
		assertTrue(binder.isValid());
		assertEquals(0, binder.getBindingsEnabled().size());
		assertEquals(1, binder.getBindings().size());
	}

	@Test
	public void withValidator_binding_disabled() {
		binder.forField(nameField)
				.withValidator((value, context) -> {
					if (StringUtils.isBlank(value)) {
						return ValidationResult.error("Error!");
					}
					return ValidationResult.ok();
				})
				.bind(Person::getFirstName, Person::setFirstName)
				.setEnabled(() -> nameField.isVisible());

		nameField.setVisible(true);
		nameField.setValue("   ");
		assertFalse(binder.isValid());
		assertEquals(binder.getBindings(), binder.getBindingsEnabled());

		nameField.setVisible(false);
		assertTrue(binder.isValid());
		assertEquals(0, binder.getBindingsEnabled().size());
		assertEquals(1, binder.getBindings().size());
	}

	@Test
	public void withConverter_binding_disabled() {
		binder.forField(ageField)
				.withConverter(stringToInteger)
				.bind(Person::getAge, Person::setAge)
				.setEnabled(() -> ageField.isEnabled());

		ageField.setEnabled(true);
		ageField.setValue("not an integer");
		assertFalse(binder.isValid());
		assertEquals(binder.getBindings(), binder.getBindingsEnabled());

		ageField.setEnabled(false);
		ageField.setValue("still not an integer");
		assertTrue(binder.isValid());
		assertEquals(0, binder.getBindingsEnabled().size());
		assertEquals(1, binder.getBindings().size());
	}

	@Test
	public void withNullRepresentation_binding_disabled() throws ValidationException {
		binder.forField(nameField)
				.withNullRepresentation("")
				.bind(Person::getFirstName, Person::setFirstName)
				.setEnabled(() -> nameField.isEnabled());

		nameField.setEnabled(true);
		Person person = new Person();
		person.setFirstName("something that will be deleted");
		binder.writeBean(person);
		assertNull(person.getFirstName());
		assertEquals(binder.getBindings(), binder.getBindingsEnabled());

		nameField.setEnabled(false);
		person.setFirstName("something that will NOT be deleted");
		binder.writeBean(person);
		assertNotNull(person.getFirstName());
		assertEquals(0, binder.getBindingsEnabled().size());
		assertEquals(1, binder.getBindings().size());
	}

	@Test
	public void setBean_binding_disabled() {
		binder.forField(nameField)
				.bind(Person::getFirstName, Person::setFirstName)
				.setEnabled(() -> nameField.isVisible());

		Person person = new Person();
		binder.setBean(person);

		nameField.setVisible(true);
		String firstName = "John Rambo";
		nameField.setValue(firstName);
		assertEquals(firstName, person.getFirstName());

		nameField.setVisible(false);
		person.setFirstName(null);
		nameField.setValue("Van Damme");
		assertNull(person.getFirstName());
	}

	@Test
	public void readBean_binding_disabled() {
		binder.forField(nameField)
				.bind(Person::getFirstName, Person::setFirstName)
				.setEnabled(() -> nameField.isEnabled());

		binder.forField(ageField)
				.withConverter(stringToInteger)
				.bind(Person::getAge, Person::setAge);

		Person person = new Person();
		String firstName = "Chuck Liddell";
		person.setFirstName(firstName);
		Integer age = 44;
		person.setAge(age);

		nameField.setEnabled(true);

		binder.readBean(person);
		assertEquals(firstName, nameField.getValue());
		assertEquals(age.toString(), ageField.getValue());

		nameField.setEnabled(false);
		String changedFirstName = "Kimo";
		person.setFirstName(changedFirstName);
		Integer changedAge = 30;
		person.setAge(changedAge);

		binder.readBean(person);
		assertEquals(firstName, nameField.getValue());
		assertEquals(changedAge.toString(), ageField.getValue());
	}

	@Test
	public void writeBean_binding_disabled() throws ValidationException {
		binder.forField(ageField)
				.withConverter(stringToInteger)
				.bind(Person::getAge, Person::setAge);

		binder.forField(nameField)
				.bind(Person::getFirstName, Person::setFirstName)
				.setEnabled(() -> nameField.isEnabled());

		nameField.setEnabled(true);
		String firstName = "Chuck Liddell";
		nameField.setValue(firstName);
		Integer age = 44;
		ageField.setValue(age.toString());

		Person person = new Person();
		binder.writeBean(person);
		assertEquals(firstName, person.getFirstName());
		assertEquals(age.intValue(), person.getAge());

		nameField.setEnabled(false);
		String changedFirstName = "Kimo";
		nameField.setValue(changedFirstName);
		Integer changedAge = 30;
		ageField.setValue(changedAge.toString());

		binder.writeBean(person);
		assertEquals(firstName, person.getFirstName());
		assertEquals(changedAge.intValue(), person.getAge());
	}

	@Test
	public void writeBeanIfValid_binding_disabled() {

		binder.forField(nameField)
				.bind(Person::getFirstName, Person::setFirstName)
				.setEnabled(() -> nameField.isEnabled());

		binder.forField(ageField)
				.withConverter(stringToInteger)
				.bind(Person::getAge, Person::setAge);

		nameField.setEnabled(true);
		String firstName = "Arnold";
		nameField.setValue(firstName);
		Integer age = 44;
		ageField.setValue(age.toString());

		Person person = new Person();
		binder.writeBeanIfValid(person);
		assertEquals(firstName, person.getFirstName());
		assertEquals(age.intValue(), person.getAge());

		nameField.setEnabled(false);
		String changedFirstName = "Terminator";
		nameField.setValue(changedFirstName);
		Integer changedAge = 30;
		ageField.setValue(changedAge.toString());

		binder.writeBeanIfValid(person);
		assertEquals(firstName, person.getFirstName());
		assertEquals(changedAge.intValue(), person.getAge());
	}

	@Test
	public void addValueChangeListener_binding_disabled() {

		binder.forField(ageField)
				.withConverter(stringToInteger)
				.bind(Person::getAge, Person::setAge)
				.setEnabled(() -> ageField.isVisible());

		AtomicInteger valueChangeEventCount = new AtomicInteger(0);
		binder.addValueChangeListener(event -> valueChangeEventCount.incrementAndGet());

		ageField.setVisible(true);
		ageField.setValue("20");
		assertEquals(1, valueChangeEventCount.get());

		ageField.setVisible(false);
		ageField.setValue("36");
		assertEquals(1, valueChangeEventCount.get());
	}

	@Test
	public void handleFieldValueChange_setBean_binding_disabled() {
		Binder.Binding<Person, String> binding = binder.forField(nameField)
				.bind(Person::getFirstName, Person::setFirstName);
		binding.setEnabled(() -> nameField.isVisible());

		Person person = new Person();
		binder.setBean(person);

		nameField.setVisible(true);
		String initialFirstName = "John";
		nameField.setValue(initialFirstName);
		binder.handleFieldValueChange(binding, null);
		assertEquals(initialFirstName, person.getFirstName());

		nameField.setVisible(false);
		nameField.setValue("Rambo");
		binder.handleFieldValueChange(binding, null);
		assertEquals(initialFirstName, person.getFirstName());
	}

	@Test
	public void handleFieldValueChange_readBean_binding_disabled() throws ValidationException {
		Binder.Binding<Person, String> binding = binder.forField(nameField)
				.bind(Person::getFirstName, Person::setFirstName);
		binding.setEnabled(() -> nameField.isVisible());

		Person person = new Person();
		binder.readBean(person);

		nameField.setVisible(true);
		String initialFirstName = "John";
		nameField.setValue(initialFirstName);
		binder.handleFieldValueChange(binding, null);
		binder.writeBean(person);
		assertEquals(initialFirstName, person.getFirstName());

		nameField.setVisible(false);
		nameField.setValue("Rambo");
		binder.handleFieldValueChange(binding, null);
		binder.writeBean(person);
		assertEquals(initialFirstName, person.getFirstName());
	}

	@Test
	public void setReadOnly_binding_disabled() {
		Binder.Binding<Person, String> bindingNameField = binder.forField(nameField)
				.bind(Person::getFirstName, Person::setFirstName);
		bindingNameField.setEnabled(() -> nameField.isVisible());

		Binder.Binding<Person, Integer> bindingAgeField = binder.forField(ageField)
				.withConverter(stringToInteger)
				.bind(Person::getAge, Person::setAge);

		nameField.setVisible(false);
		binder.setReadOnly(true);

		assertFalse(bindingNameField.isReadOnly());
		assertTrue(bindingAgeField.isReadOnly());
	}

	@Test
	public void removeBinding_field_even_if_disabled() {
		binder.forField(nameField)
				.bind(Person::getFirstName, Person::setFirstName)
				.setEnabled(() -> nameField.isVisible());

		nameField.setVisible(false);
		binder.removeBinding(nameField);

		assertTrue(binder.getBindings().isEmpty());
	}

	@Test
	public void removeBinding_property_even_if_disabled() {
		String propertyName = "firstName";
		binder = new Binder<>(Person.class);
		binder.forField(nameField)
				.bind(propertyName)
				.setEnabled(() -> nameField.isVisible());

		nameField.setVisible(false);
		binder.removeBinding(propertyName);

		assertTrue(binder.getBindings().isEmpty());
	}

	@Test
	public void removeBinding_even_if_disabled() {
		Binder.Binding<Person, String> binding = binder.forField(nameField)
				.bind(Person::getFirstName, Person::setFirstName);
		binding.setEnabled(() -> nameField.isVisible());

		nameField.setVisible(false);
		binder.removeBinding(binding);

		assertTrue(binder.getBindings().isEmpty());
	}
}
