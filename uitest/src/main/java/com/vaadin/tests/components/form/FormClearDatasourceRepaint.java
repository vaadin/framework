package com.vaadin.tests.components.form;

import com.vaadin.data.util.BeanItem;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;

public class FormClearDatasourceRepaint extends TestBase {

    public static class MyBean {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class MySecondBean extends MyBean {

        private String value2;

        public String getValue2() {
            return value2;
        }

        public void setValue2(String value) {
            value2 = value;
        }
    }

    @Override
    protected void setup() {

        final Form form = new Form();
        form.setFooter(null);
        form.setItemDataSource(new BeanItem<MySecondBean>(new MySecondBean()));
        addComponent(form);

        addComponent(new Button("Clear datasource", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                form.setItemDataSource(null);
            }
        }));

        addComponent(new Button("Change data source",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        form.setItemDataSource(new BeanItem<MyBean>(
                                new MyBean()));
                    }
                }));

    }

    @Override
    protected String getDescription() {
        return "The form should adjust its size when clearing and setting data sources";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7626;
    }

}
