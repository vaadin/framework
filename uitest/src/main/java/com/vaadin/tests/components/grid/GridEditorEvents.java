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

import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.TextField;

/**
 * @author Vaadin Ltd
 *
 */
public class GridEditorEvents extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = new Grid<>();
        Person person1 = new Person();
        person1.setFirstName("");

        Person person2 = new Person();
        person2.setFirstName("foo");

        grid.setItems(person1, person2);
        Column<Person, String> column = grid.addColumn(Person::getFirstName);

        Binder<Person> binder = grid.getEditor().getBinder();
        grid.getEditor().setEnabled(true);

        TextField field = new TextField();
        Binding<Person, String> binding = binder.bind(field,
                Person::getFirstName, Person::setFirstName);
        column.setEditorBinding(binding);

        grid.getEditor().addCancelListener(event -> log("editor is canceled"));
        grid.getEditor().addSaveListener(event -> log("editor is saved"));
        addComponent(grid);
    }

}
