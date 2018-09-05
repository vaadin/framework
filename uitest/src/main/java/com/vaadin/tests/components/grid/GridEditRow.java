package com.vaadin.tests.components.grid;

import java.util.ArrayList;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridEditRow extends AbstractTestUI {

    private ArrayList<TestBean> items = new ArrayList<>();
    private TestBean bean = null;
    private int counter = 0;

    @SuppressWarnings("unchecked")
    @Override
    protected void setup(VaadinRequest request) {

        Grid<TestBean> grid = new Grid<TestBean>(TestBean.class);
        grid.setDataProvider(new ListDataProvider<>(items));
        grid.setWidth("100%");
        grid.setHeight("400px");
        grid.getEditor().setEnabled(true);
        grid.getColumn("name").setEditorComponent(new TextField());
        grid.getColumn("value").setEditorComponent(new TextField());

        getLayout().addComponent(new Button("Add", event -> {
            bean = new TestBean();
            items.add(bean);
            grid.getDataProvider().refreshAll();
        }));
        getLayout().addComponent(new Button("Add, Select & Edit", event -> {
            bean = new TestBean();
            items.add(bean);
            grid.getDataProvider().refreshAll();
            grid.select(bean);
            int row = ((ArrayList<TestBean>) ((ListDataProvider<TestBean>) grid
                    .getDataProvider()).getItems()).indexOf(bean);
            grid.getEditor().editRow(row);
        }));
        getLayout().addComponent(new Button("Edit", event -> {
            int row = ((ArrayList<TestBean>) ((ListDataProvider<TestBean>) grid
                    .getDataProvider()).getItems()).indexOf(bean);
            grid.getEditor().editRow(row);
        }));
        getLayout().addComponent(new Button("Select & Edit", event -> {
            grid.select(bean);
            int row = ((ArrayList<TestBean>) ((ListDataProvider<TestBean>) grid
                    .getDataProvider()).getItems()).indexOf(bean);
            grid.getEditor().editRow(row);
        }));

        getLayout().addComponent(grid);
    }

    @Override
    protected Integer getTicketNumber() {
        return 10558;
    }

    @Override
    protected String getTestDescription() {
        return "Calling editRow shouldn't cause any other rows to be emptied.";
    }

    public class TestBean {
        private String name = "name" + counter;
        private String value = "value" + counter;

        public TestBean() {
            ++counter;
        }

        public String getName() {
            return name;
        }

        public void setName(String inName) {
            name = inName;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String inValue) {
            value = inValue;
        }

    }

}
