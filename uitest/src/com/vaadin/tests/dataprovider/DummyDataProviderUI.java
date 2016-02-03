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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
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
import com.vaadin.ui.proto.ListBox;
import com.vaadin.ui.proto.ListBox.NameProvider;

import elemental.json.JsonObject;

@Widgetset(TestingWidgetSet.NAME)
public class DummyDataProviderUI extends AbstractTestUI {

    abstract static class MyRunnable implements Runnable, Serializable {

        private final String name;

        public MyRunnable(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

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
    private ListBox<MyRunnable> listBox;
    private List<ComplexPerson> persons;

    @Override
    protected void setup(VaadinRequest request) {

        persons = createPersons(PERSON_COUNT, r);
        dataSource = new MyDataSource(persons);
        dummy = new DummyDataComponent(dataSource);

        Collection<MyRunnable> actions = new LinkedHashSet<MyRunnable>();
        actions.add(new MyRunnable("remove") {

            @Override
            public void run() {
                dataSource.remove(dataSource.getData().get(2));
            }
        });
        actions.add(new MyRunnable("add") {

            @Override
            public void run() {
                dataSource.save(ComplexPerson.create(r));
            }
        });
        actions.add(new MyRunnable("sort") {

            @Override
            public void run() {
                dataSource.sort(nameComparator);
            }
        });
        actions.add(new MyRunnable("edit") {

            @Override
            public void run() {
                ComplexPerson p = persons.get(0);
                p.setFirstName("Foo");
                dataSource.save(p);
            }
        });

        listBox = new ListBox<MyRunnable>(actions,
                new NameProvider<MyRunnable>() {

                    @Override
                    public String getName(MyRunnable value) {
                        return value.getName();
                    }
                });

        Button execute = new Button("Execute", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                listBox.getSelected().run();
            }
        });
        addComponent(new HorizontalLayout(listBox, execute));
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
