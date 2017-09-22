package com.vaadin.tests.components.textarea;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class TextAreaTextFieldCursorPosition extends AbstractTestUIWithLog {

    public static final String GET_POSITION = "getposition";
    public static final String INSERT = "insert";

    @Override
    protected void setup(VaadinRequest request) {
        TextArea textArea = new TextArea("Simple Text Area");
        textArea.setValue("I am just a piece of random text");
        textArea.setWidth("500px");
        addComponent(textArea);
        TextField textField = new TextField("Simple Text field");
        textField.setValue("I am just a piece of random text");
        textField.setWidth("500px");
        addComponent(textField);

        Button posButton = new Button("Get Position");
        posButton.setId(GET_POSITION);
        posButton.addClickListener(c -> {
            log("TextArea position: " + textArea.getCursorPosition());
            log("TextField position: " + textField.getCursorPosition());
        });
        addComponent(posButton);

        Button insertButton = new Button("Insert");
        insertButton.setId(INSERT);
        insertButton.addClickListener(c -> {
            insert(textArea);
            insert(textField);
        });
        addComponent(insertButton);
    }

    private void insert(AbstractTextField field) {
        String value = field.getValue();
        if (field.getCursorPosition() != -1) {
            int pos = field.getCursorPosition();
            log("Insert position: " + field.getCursorPosition());
            value = value.substring(0, pos) + "-insertedtext-"
                    + value.substring(pos, value.length());
        } else {
            value += "-appendedtext";
        }
        field.setValue(value);

    }

}
