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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;

/**
 * @author Vaadin Ltd
 *
 */
public abstract class AbstractBeansMemoryTest<T extends AbstractComponent>
        extends UI {

    private int dataSize;
    private boolean isInMemory;
    private boolean isDataOnly;

    private Label logLabel;
    private Label memoryLabel;

    @Override
    protected void init(VaadinRequest request) {
        String items = request.getParameter("items");
        int itemsCount = 1;
        if (items != null) {
            itemsCount = Integer.parseInt(items);
        }

        VerticalLayout layout = new VerticalLayout();
        setContent(layout);
        layout.addComponent(new Label(getClass().getSimpleName()));

        memoryLabel = new Label();
        memoryLabel.setId("memory");
        layout.addComponent(memoryLabel);

        logLabel = new Label();
        logLabel.setId("log");
        layout.addComponent(logLabel);

        T component = createComponent();
        setData(null, itemsCount, component, true);
        layout.addComponent(createMenu(component));

        layout.addComponent(component);

        Button close = new Button("Close UI", event -> close());
        close.setId("close");
        layout.addComponent(close);
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

    @Override
    public VerticalLayout getContent() {
        return (VerticalLayout) super.getContent();
    }

    @SuppressWarnings("restriction")
    private void setData(MenuItem item, int size, T component,
            boolean memoryContainer) {
        if (item != null) {
            MenuItem parent = item.getParent();
            parent.getChildren().stream().filter(itm -> !itm.equals(item))
                    .forEach(itm -> itm.setChecked(false));
            logLabel.setValue(item.getText());
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

        HasComponents container = component.getParent();
        setParent(component, null);
        memoryLabel.setValue(
                String.valueOf(ObjectSizeCalculator.getObjectSize(component)));

        setParent(component, container);
    }

    private void setParent(Component component, Component parent) {
        try {
            Field field = AbstractComponent.class.getDeclaredField("parent");
            field.setAccessible(true);
            field.set(component, parent);
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Component createMenu(T component) {
        MenuBar menu = new MenuBar();
        createContainerSizeMenu(menu.addItem("Size", null), component);
        createContainerMenu(menu.addItem("Data provider", null), component);
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
        List<MenuItem> items = IntStream.of(1, 10000, 100000, 500000, 1000000)
                .mapToObj(size -> addContainerSizeMenu(size, menu, component))
                .collect(Collectors.toList());
        if (dataSize == 1) {
            items.get(0).setChecked(true);
        }
    }

    private MenuItem addContainerSizeMenu(int size, MenuItem menu,
            T component) {
        MenuItem item = menu.addItem("Set data provider size to " + size,
                itm -> setData(itm, size, component, isInMemory));
        item.setCheckable(true);
        return item;
    }
}
