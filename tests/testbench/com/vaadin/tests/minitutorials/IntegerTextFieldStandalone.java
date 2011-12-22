package com.vaadin.tests.minitutorials;

import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextField;

public class IntegerTextFieldStandalone extends AbstractTestRoot {

    @Override
    protected void setup(WrappedRequest request) {
        final TextField textField = new TextField("Text field");
        textField.setConverter(new StringToIntegerConverter());

        Button submitButton = new Button("Submit value", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                String uiValue = textField.getValue();
                try {
                    Integer convertedValue = (Integer) textField
                            .getConvertedValue();
                    Root.getCurrentRoot().showNotification(
                            "UI value (String): " + uiValue
                                    + "<br />Converted value (Integer): "
                                    + convertedValue);
                } catch (ConversionException e) {
                    e.printStackTrace();
                    Root.getCurrentRoot().showNotification(
                            "Could not convert value: " + uiValue);
                }
            }
        });

        addComponent(new Label("Text field type: " + textField.getType()));
        addComponent(new Label("Converterd text field type: "
                + textField.getConverter().getModelType()));
        addComponent(textField);
        addComponent(submitButton);
    }

    @Override
    protected String getTestDescription() {
        return "Mini tutorial for https://vaadin.com/wiki/-/wiki/Main/Creating%20a%20TextField%20for%20Integer%20only%20input%20when%20not%20using%20a%20data%20source";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
