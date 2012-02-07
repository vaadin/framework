package com.vaadin.tests.components.table;

import java.util.Arrays;
import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;

public class TableSelectPagingOff extends TestBase {

    @Override
    protected void setup() {
        Table table = new Table();
        BeanItemContainer<MyBean> dataSource = new BeanItemContainer<MyBean>(
                getBeans());
        table.setContainerDataSource(dataSource);
        table.setSelectable(true);
        table.setPageLength(0);
        addComponent(table);
    }

    private Collection<MyBean> getBeans() {
        return Arrays.asList(new MyBean("a", "description a"), new MyBean("b",
                "description b"), new MyBean("c", "description c"), new MyBean(
                "d", "description d"));
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
