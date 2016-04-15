package com.vaadin.tests.components.table;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class ColumnWidthsAfterChangeTableColumnsCountOrOrder extends
        AbstractTestUI {

    protected static final String BUTTON_CHANGE_ORDER_AND_WIDTH_ID = "buttonChangeOrderAndWidth";
    protected static final String BUTTON_CHANGE_COLUMN_COUNT_AND_WIDTH = "buttonChangeColumnCountAndWidth";
    protected static final int NEW_COLUMN_WIDTH = 17;

    @Override
    protected void setup(VaadinRequest request) {
        List<TestBean> beanList = new ArrayList<TestBean>();
        beanList.add(new TestBean(1, "name1", "descr1"));
        beanList.add(new TestBean(2, "name2", "descr2"));
        beanList.add(new TestBean(3, "name3", "descr3"));
        beanList.add(new TestBean(4, "name4", "descr4"));
        beanList.add(new TestBean(5, "name5", "descr5"));

        BeanItemContainer<TestBean> container = new BeanItemContainer<TestBean>(
                TestBean.class, beanList);

        VerticalLayout layout = new VerticalLayout();

        final Table table = new Table("Test Table");
        table.setContainerDataSource(container);
        table.setVisibleColumns(new Object[] { "id", "name", "descr" });
        layout.addComponent(table);

        Button buttonChangeOrderAndWidth = new Button("Change order and width",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        table.setVisibleColumns(new Object[] { "name", "descr",
                                "id" });
                        table.setColumnWidth("descr", NEW_COLUMN_WIDTH);
                    }
                });
        buttonChangeOrderAndWidth.setId(BUTTON_CHANGE_ORDER_AND_WIDTH_ID);

        Button buttonChangeColumnCountAndWidth = new Button(
                "Change columns count and width", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        table.setVisibleColumns(new Object[] { "name", "descr" });
                        table.setColumnWidth("descr", NEW_COLUMN_WIDTH);
                    }
                });
        buttonChangeColumnCountAndWidth
                .setId(BUTTON_CHANGE_COLUMN_COUNT_AND_WIDTH);

        layout.addComponent(buttonChangeOrderAndWidth);
        layout.addComponent(buttonChangeColumnCountAndWidth);

        addComponents(layout);
    }

    public class TestBean {
        private int id;
        private String name;
        private String descr;

        public TestBean(int id, String name, String descr) {
            this.id = id;
            this.name = name;
            this.descr = descr;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescr() {
            return descr;
        }

        public void setDescr(String descr) {
            this.descr = descr;
        }

        @Override
        public String toString() {
            return "TestBean{" + "id=" + id + ", name='" + name + '\''
                    + ", descr='" + descr + '\'' + '}';
        }
    }

    @Override
    protected String getTestDescription() {
        return "Tests that properties of columns are changed correctly after changing column's order/count";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9781;
    }

}
