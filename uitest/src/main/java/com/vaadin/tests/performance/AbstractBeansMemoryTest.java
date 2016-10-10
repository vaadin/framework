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
package com.vaadin.tests.performance;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 * @author Vaadin Ltd
 *
 */
public abstract class AbstractBeansMemoryTest<T extends AbstractComponent>
        extends AbstractTestUI {

    private int dataSize;
    private boolean isInMemory;
    private boolean isDataOnly;

    @Override
    protected void setup(VaadinRequest request) {
        T component = createComponent();
        setData(null, 1000000, component, true);
        addComponent(createMenu(component));

        addComponent(component);
    }

    protected abstract T createComponent();

    private Random random = new Random();

    protected List<Person> createBeans(int size) {
        return IntStream.range(0, size).mapToObj(this::createPerson)
                .collect(Collectors.toList());
    }

    protected Person createPerson(int index) {
        random.setSeed(index);
        Person person = new Person();
        person.setFirstName("First Name " + random.nextInt());
        person.setLastName("Last Name " + random.nextInt());
        person.setAge(random.nextInt());
        person.setBirthDate(new Date(random.nextLong()));
        person.setDeceased(random.nextBoolean());
        person.setEmail(random.nextInt() + "user@example.com");
        person.setRent(new BigDecimal(random.nextLong()));
        person.setSalary(random.nextInt());
        person.setSalaryDouble(random.nextDouble());
        person.setSex(Sex.values()[random.nextInt(Sex.values().length)]);

        Address address = new Address();
        person.setAddress(address);
        address.setCity("city " + random.nextInt());
        address.setPostalCode(random.nextInt());
        address.setStreetAddress("street address " + random.nextInt());
        address.setCountry(
                Country.values()[random.nextInt(Country.values().length)]);
        return person;
    }

    protected abstract void setInMemoryContainer(T component,
            List<Person> data);

    protected abstract void setBackendContainer(T component, List<Person> data);

    private void setData(MenuItem item, int size, T component,
            boolean memoryContainer) {
        if (item != null) {
            MenuItem parent = item.getParent();
            parent.getChildren().stream().filter(itm -> !itm.equals(item))
                    .forEach(itm -> itm.setChecked(false));
        }
        dataSize = size;
        isInMemory = memoryContainer;
        List<Person> persons = createBeans(size);
        if (isDataOnly) {
            component.setData(persons);
            persons = Collections.emptyList();
        } else {
            component.setData(null);
        }
        if (isInMemory) {
            setInMemoryContainer(component, persons);
        } else {
            setBackendContainer(component, persons);
        }
    }

    private Component createMenu(T component) {
        MenuBar menu = new MenuBar();
        createContainerSizeMenu(menu.addItem("Size", null), component);
        createContainerMenu(menu.addItem("Data source", null), component);
        menu.addItem("Create only data",
                item -> toggleDataOnly(item, component)).setCheckable(true);

        return menu;
    }

    private void toggleDataOnly(MenuItem item, T component) {
        isDataOnly = item.isChecked();
        setData(null, dataSize, component, isInMemory);
    }

    private void createContainerMenu(MenuItem menu, T component) {
        MenuItem menuItem = menu.addItem("Use in-memory container",
                item -> setData(item, dataSize, component, true));
        menuItem.setCheckable(true);
        menuItem.setChecked(true);
        menuItem = menu.addItem("Use backend container",
                item -> setData(item, dataSize, component, false));
        menuItem.setCheckable(true);
    }

    private void createContainerSizeMenu(MenuItem menu, T component) {
        List<MenuItem> items = IntStream.of(1, 100000, 500000, 1000000)
                .mapToObj(size -> addContainerSizeMenu(size, menu, component))
                .collect(Collectors.toList());
        items.get(items.size() - 1).setChecked(true);
    }

    private MenuItem addContainerSizeMenu(int size, MenuItem menu,
            T component) {
        MenuItem item = menu.addItem("Set data source size to " + size,
                itm -> setData(itm, size, component, isInMemory));
        item.setCheckable(true);
        return item;
    }
}
