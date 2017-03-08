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
import java.util.Date;
import java.util.List;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;

/**
 * Test for #8291 and #7666: NegativeArraySizeException when Table scrolled to
 * the end and its size reduced.
 */
public class TableReduceContainerSize extends TestBase {

    @Override
    protected void setup() {
        addComponent(new TestView());
    }

    private static class DecimateFilter implements Filter {
        @Override
        public boolean passesFilter(Object itemId, Item item)
                throws UnsupportedOperationException {
            return ((((TestObject) itemId).property3 % 10) == 0);
        }

        @Override
        public boolean appliesToProperty(Object propertyId) {
            return true;
        }
    }

    private static class TestView extends HorizontalLayout {

        private Filter filter = null;

        private boolean reduceData;

        private TestView() {
            final Table table = new Table();
            List<TestObject> data = createData(1000);
            final BeanItemContainer<TestObject> container = new BeanItemContainer<TestObject>(
                    TestObject.class, data) {

                @Override
                public int size() {
                    if (reduceData) {
                        return 100;
                    } else {
                        return super.size();
                    }
                }
            };
            table.setContainerDataSource(container);
            addComponent(table);
            final Label label = new Label();
            addComponent(label);
            Button button = new Button("Click");
            button.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    try {
                        reduceData = !reduceData;
                        table.refreshRowCache();
                        label.setValue("Index: "
                                + table.getCurrentPageFirstItemIndex());
                    } catch (Exception e) {
                        label.setValue(
                                "Exception: " + e.getClass().getSimpleName());
                    }
                }
            });
            addComponent(button);
            Button button2 = new Button("Filter");
            button2.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    try {
                        if (filter != null) {
                            container.removeAllContainerFilters();
                            filter = null;
                        } else {
                            filter = new DecimateFilter();
                            container.addContainerFilter(filter);
                        }
                        table.refreshRowCache();
                        label.setValue("Index: "
                                + table.getCurrentPageFirstItemIndex());
                    } catch (Exception e) {
                        label.setValue(
                                "Exception: " + e.getClass().getSimpleName());
                    }
                }
            });
            addComponent(button2);
        }
    }

    private static List<TestObject> createData(int count) {
        ArrayList<TestObject> data = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            data.add(new TestObject("string-" + i, new Date(), i));
        }
        return data;
    }

    public static class TestObject {

        private String property1;
        private Date property2;
        private Integer property3;

        public TestObject(String property1, Date property2, Integer property3) {
            this.property1 = property1;
            this.property2 = property2;
            this.property3 = property3;
        }

        public String getProperty1() {
            return property1;
        }

        public Date getProperty2() {
            return property2;
        }

        public Integer getProperty3() {
            return property3;
        }

    }

    @Override
    protected String getDescription() {
        return "Table throws NegativeArraySizeException if container size is reduced to less than current scroll position";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8291;
    }

}
