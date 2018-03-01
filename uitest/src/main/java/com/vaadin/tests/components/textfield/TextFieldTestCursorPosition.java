package com.vaadin.tests.components.textfield;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class TextFieldTestCursorPosition extends AbstractTestUI {

    static final String DEFAULT_TEXT = "So we have some text to select";
    static final String BUTTON_SETPOSITION= "buttonPos";
    static final String BUTTON_SETRANGE = "rS";
    static final String RANGE_LENGTH_TF = "rLTF";
    static final String CURSOR_POS_TF = "cpTF";
    static final int valueLength=DEFAULT_TEXT.length();

    final  TextField textField = new TextField("Set cursor position after the last character");
    final TextField textField1=new TextField("Set Selection range");

    @Override
    protected void setup(VaadinRequest request) {
        textField.setValue(DEFAULT_TEXT);
        textField.setId(CURSOR_POS_TF);
        textField.setWidth("500px");

        Button posButton = new Button("Set Position to the last character");
        posButton.setId(BUTTON_SETPOSITION);
        posButton.addClickListener(c -> {
            textField.setCursorPosition(valueLength);
        });
        addComponent(textField);
        addComponent(posButton);


        textField1.setId(RANGE_LENGTH_TF);
        textField1.setValue(DEFAULT_TEXT);
        textField1.setWidth("500px");

        Button selButton = new Button("Set selection range");
        selButton.setId(BUTTON_SETRANGE);
        selButton.addClickListener(c -> {
            textField1.setSelection(valueLength/2,valueLength);
        });
        addComponent(textField1);
        addComponent(selButton);
    }
}
