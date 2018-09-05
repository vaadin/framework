package com.vaadin.tests.components.textfield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.TextField;

public class TextFieldWithDataSourceAndInputPrompt
        extends AbstractReindeerTestUI {
    public static class Pojo {
        private String string;

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        TextField textField = new TextField("TextField with null value");
        textField.setInputPrompt("Me is input prompt");
        textField.setNullRepresentation(null);
        textField.setValue(null);
        addComponent(textField);

        TextField textField2 = new TextField(
                "TextField with null data source value");
        textField2.setInputPrompt("Me is input prompt");
        textField2.setNullRepresentation(null);
        BeanItem<Pojo> beanItem = new BeanItem<>(new Pojo());
        textField2.setPropertyDataSource(beanItem.getItemProperty("string"));
        addComponent(textField2);
    }

    @Override
    protected String getTestDescription() {
        return "Input prompt should be shown when data source provides null";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11021;
    }

}
