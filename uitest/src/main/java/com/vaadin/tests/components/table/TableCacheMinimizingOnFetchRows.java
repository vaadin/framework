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

import java.io.Serializable;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.data.util.BeanContainer;
import com.vaadin.v7.ui.Table;

@SuppressWarnings("serial")
public class TableCacheMinimizingOnFetchRows extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setMargin(true);

        final Table table = new Table("Beans of All Sorts");

        BeanContainer<String, Bean> beans = new BeanContainer<String, Bean>(
                Bean.class) {
            @Override
            public List<String> getItemIds(int startIndex, int numberOfIds) {

                // numberOfIds should be about 60 after scrolling down the table
                log.log("requested " + numberOfIds + " rows");

                return super.getItemIds(startIndex, numberOfIds);
            }
        };
        beans.setBeanIdProperty("name");

        for (int i = 0; i < 10000; i++) {
            beans.addBean(new Bean("Common bean" + i, i));
        }

        table.setContainerDataSource(beans);
        table.setPageLength(20);
        table.setVisibleColumns(new Object[] { "name", "value" });
        table.setWidth("800px");

        Button button = new Button("scroll down");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.setCurrentPageFirstItemIndex(table.size());
            }
        });

        addComponent(table);
        addComponent(button);
    }

    public class Bean implements Serializable {

        String name;
        int value;

        public Bean(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    @Override
    protected String getTestDescription() {
        return "Ensure that when scrolling from top to bottom in a big table with 10000 items, not all rows in the range are cached";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13576;
    }
}
