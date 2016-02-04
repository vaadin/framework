/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.dataprovider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.communication.data.typed.CollectionDataSource;
import com.vaadin.server.communication.data.typed.DataProvider;
import com.vaadin.server.communication.data.typed.DataSource;
import com.vaadin.server.communication.data.typed.SimpleDataProvider;
import com.vaadin.server.communication.data.typed.TypedDataGenerator;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.fieldgroup.ComplexPerson;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;

import elemental.json.JsonObject;

@Widgetset(TestingWidgetSet.NAME)
public class DummyDataProviderUI extends AbstractTestUI {

    public static class DummyDataComponent extends AbstractComponent {

        private SimpleDataProvider<ComplexPerson> dataProvider;

        public DummyDataComponent(DataSource<ComplexPerson> data) {
            dataProvider = DataProvider.create(data, this);
            dataProvider
                    .addDataGenerator(new TypedDataGenerator<ComplexPerson>() {

                        @Override
                        public void generateData(ComplexPerson data,
                                JsonObject dataObject) {
                            String name = data.getLastName() + ", "
                                    + data.getFirstName();
                            dataObject.put("name", name);
                        }

                        @Override
                        public void destroyData(ComplexPerson data) {
                        }
                    });
        }
    }

    private static class MyDataSource extends
            CollectionDataSource<ComplexPerson> {

        public MyDataSource(Collection<ComplexPerson> data) {
            super(data);
        }

        public void sort(Comparator<ComplexPerson> c) {
            Collections.sort(backend, c);
            fireDataChange();
        }

        public List<ComplexPerson> getData() {
            return Collections.unmodifiableList(backend);
        }
    }

    public static final int RANDOM_SEED = 1337;
    public static final int PERSON_COUNT = 20;
    public static final Comparator<ComplexPerson> nameComparator = new Comparator<ComplexPerson>() {

        @Override
        public int compare(ComplexPerson p1, ComplexPerson p2) {
            int fn = p1.getFirstName().compareTo(p2.getFirstName());
            int ln = p1.getLastName().compareTo(p2.getLastName());
            return fn != 0 ? fn : ln;
        }
    };

    private Random r = new Random(RANDOM_SEED);
    private MyDataSource dataSource;
    private DummyDataComponent dummy;

    @Override
    protected void setup(VaadinRequest request) {
        dataSource = new MyDataSource(createPersons(PERSON_COUNT, r));
        dummy = new DummyDataComponent(dataSource);

        Button remove = new Button("Remove third", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                dataSource.remove(dataSource.getData().get(2));
            }
        });
        Button add = new Button("Add new", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                dataSource.save(ComplexPerson.create(r));
            }
        });
        Button sort = new Button("Sort content", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                dataSource.sort(nameComparator);
            }
        });
        Button edit = new Button("Edit first", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                ComplexPerson p = dataSource.iterator().next();
                p.setFirstName("Foo");
                dataSource.save(p);
            }
        });

        // Button Ids
        remove.setId("remove");
        add.setId("add");
        sort.setId("sort");
        edit.setId("edit");

        addComponent(new HorizontalLayout(add, remove, sort, edit));
        addComponent(dummy);
    }

    public static List<ComplexPerson> createPersons(int count, Random r) {
        List<ComplexPerson> c = new ArrayList<ComplexPerson>();
        for (int i = 0; i < count; ++i) {
            c.add(ComplexPerson.create(r));
        }
        return c;
    }
}
