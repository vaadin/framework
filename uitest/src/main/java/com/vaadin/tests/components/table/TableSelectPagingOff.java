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

import java.util.Arrays;
import java.util.Collection;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;

public class TableSelectPagingOff extends TestBase {

    @Override
    protected void setup() {
        Table table = new Table();
        BeanItemContainer<MyBean> dataSource = new BeanItemContainer<>(
                getBeans());
        table.setContainerDataSource(dataSource);
        table.setSelectable(true);
        table.setPageLength(0);
        addComponent(table);
    }

    private Collection<MyBean> getBeans() {
        return Arrays.asList(new MyBean("a", "description a"),
                new MyBean("b", "description b"),
                new MyBean("c", "description c"),
                new MyBean("d", "description d"));
    }

    public class MyBean {

        private String name;
        private String description;

        public MyBean() {
        }

        public MyBean(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

    @Override
    protected String getDescription() {
        return "No flickering (scrollbars) should happen on select";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5746;
    }
}
