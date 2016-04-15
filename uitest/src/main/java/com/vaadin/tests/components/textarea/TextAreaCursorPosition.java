package com.vaadin.tests.components.textarea;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class TextAreaCursorPosition extends TestBase {

    private TextField cursorPosition = new TextField("Cursor position");

    @Override
    public void setup() {
        Label label = new Label(
                "Test of calculation of cursor position of TextArea");
        TextArea textArea = new TextArea();
        addListener(textArea);
        addComponent(label);
        addComponent(textArea);
        addComponent(cursorPosition);
        cursorPosition.setValue("?");
        addComponent(new Button("Force position update"));
    }

    public void addListener(AbstractField newField) {
        AbstractTextField newTextField = (AbstractTextField) newField;
        newTextField.setTextChangeEventMode(TextChangeEventMode.EAGER);

        newTextField.addListener(new TextChangeListener() {
            @Override
            public void textChange(TextChangeEvent event) {
                AbstractTextField component = (AbstractTextField) event
                        .getComponent();
                cursorPosition.setValue(String.valueOf(component
                        .getCursorPosition()));
            }
        });
    }

    @Override
    protected String getDescription() {
        return "Writing something in the field updates the cursor position field. The position field can also be updated using the button.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7726;
    }

}
