package com.vaadin.tests.minitutorials;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextField;

public class IntegerTextFieldDataSource extends AbstractTestRoot {

    public class MyBean {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int integer) {
            value = integer;
        }
    }

    @Override
    protected void setup(WrappedRequest request) {
        final MyBean myBean = new MyBean();
        BeanItem<MyBean> beanItem = new BeanItem<MyBean>(myBean);

        final Property<Integer> integerProperty = (Property<Integer>) beanItem
                .getItemProperty("value");
        final TextField textField = new TextField("Text field", integerProperty);

        Button submitButton = new Button("Submit value", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                String uiValue = textField.getValue();
                Integer propertyValue = integerProperty.getValue();
                int dataModelValue = myBean.getValue();

                Root.getCurrentRoot().showNotification(
                        "UI value (String): " + uiValue
                                + "<br />Property value (Integer): "
                                + propertyValue
                                + "<br />Data model value (int): "
                                + dataModelValue);
            }
        });

        addComponent(new Label("Text field type: " + textField.getType()));
        addComponent(new Label("Text field type: " + integerProperty.getType()));
        addComponent(textField);
        addComponent(submitButton);
    }

    @Override
    protected String getTestDescription() {
        return "Mini tutorial for https://vaadin.com/wiki/-/wiki/Main/Creating%20a%20TextField%20for%20Integer%20only%20input%20using%20a%20data%20source";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
