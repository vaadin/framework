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

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.ui.Grid;

/**
 * @author Vaadin Ltd
 *
 */
public class CompatibilityGridMemory extends AbstractBeansMemoryTest<Grid> {

    public static final String PATH = "/grid-compatibility-memory/";

    /**
     * The main servlet for the application.
     */
    @WebServlet(urlPatterns = PATH
            + "*", name = "CompatibilityGridServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = CompatibilityGridMemory.class, productionMode = false, widgetset = "com.vaadin.v7.Vaadin7WidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    private static class ColumnGenerator
            extends PropertyValueGenerator<String> {

        private final Function<Address, Object> getter;

        ColumnGenerator(Function<Address, Object> getter) {
            this.getter = getter;
        }

        @Override
        public String getValue(Item item, Object itemId, Object propertyId) {
            Address address = ((Person) itemId).getAddress();
            return Optional.ofNullable(address).map(getter)
                    .map(Object::toString).orElse(null);
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }
    }

    @Override
    protected Grid createComponent() {
        return new Grid();
    }

    @Override
    protected void setInMemoryContainer(Grid grid, List<Person> persons) {
        BeanItemContainer<Person> container = new BeanItemContainer<>(
                Person.class);
        container.addAll(persons);
        GeneratedPropertyContainer generated = new GeneratedPropertyContainer(
                container);
        generated.addGeneratedProperty("street",
                new ColumnGenerator(Address::getStreetAddress));
        generated.addGeneratedProperty("zip",
                new ColumnGenerator(Address::getPostalCode));
        generated.addGeneratedProperty("city",
                new ColumnGenerator(Address::getCity));
        grid.setContainerDataSource(generated);
        configureColumns(grid);
    }

    @Override
    protected void setBackendContainer(Grid component, List<Person> data) {
        throw new UnsupportedOperationException();
    }

    private void configureColumns(Grid grid) {
        grid.setColumns("firstName", "lastName", "street", "zip", "city");
    }
}
