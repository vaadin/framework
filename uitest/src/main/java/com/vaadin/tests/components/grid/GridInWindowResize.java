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
package com.vaadin.tests.components.grid;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class GridInWindowResize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = new Grid<>();

        grid.addColumn(Person::getFirstName);
        grid.addColumn(Person::getLastName);
        grid.addColumn(person -> person.getAddress().getCity());

        grid.setItems(createPersons());
        grid.setSizeFull();

        VerticalLayout vl = new VerticalLayout(grid);
        vl.setSizeFull();
        Button resize = new Button("resize");
        VerticalLayout vl2 = new VerticalLayout(vl, resize);
        vl2.setSizeFull();

        final Window window = new Window(null, vl2);
        addWindow(window);

        window.center();
        window.setModal(true);
        window.setWidth("600px");
        window.setHeight("400px");

        resize.addClickListener(event -> window.setWidth("400px"));
    }

    private Stream<Person> createPersons() {
        return IntStream.range(0, 100).mapToObj(index -> createPerson());
    }

    private Person createPerson() {
        Person person = new Person();
        person.setFirstName("1");
        person.setFirstName("1");
        person.setAddress(new Address());
        person.getAddress().setCity("1");
        return person;
    }
}
