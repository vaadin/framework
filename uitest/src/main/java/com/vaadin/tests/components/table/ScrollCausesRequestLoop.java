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
package com.vaadin.tests.components.table;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;

public class ScrollCausesRequestLoop extends AbstractTestCase {

    @Override
    public void init() {
        setMainWindow(new LegacyWindow("", new TestView()));
    }

    @Override
    protected String getDescription() {
        return "Scrolling a table causes an infinite loop of UIDL requests in some cases";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8040;
    }

    private static class TestView extends HorizontalLayout {

        TestView() {
            Table table = new Table();
            List<Person> data = createData();
            BeanItemContainer<Person> container = new BeanItemContainer<Person>(
                    Person.class, data) {

                @Override
                public Person getIdByIndex(int index) {
                    try {
                        // Simulate some loading delay with some exaggeration
                        // to make easier to reproduce
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                    return super.getIdByIndex(index);
                }
            };
            table.setContainerDataSource(container);
            addComponent(table);
        }
    }

    private static List<Person> createData() {
        int count = 500;
        List<Person> data = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            data.add(new Person("Person", "" + i, "Email", "Phone", "Street",
                    12345, "City"));
        }
        return data;
    }
}
