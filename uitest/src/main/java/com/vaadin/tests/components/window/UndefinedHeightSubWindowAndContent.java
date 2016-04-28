package com.vaadin.tests.components.window;

import com.vaadin.data.Validator;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class UndefinedHeightSubWindowAndContent extends TestBase {

    @Override
    protected void setup() {
        Window subWindow = new Window("No scrollbars!");
        subWindow.setWidth("300px");
        subWindow.center();
        subWindow.setModal(true);
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        subWindow.setContent(layout);

        final Form form = new Form();
        form.setFooter(null);
        form.setImmediate(true);
        form.setValidationVisible(true);
        form.setCaption("This is a form");
        form.setDescription("How do you do?");
        final TextField field1 = new TextField("Write here");
        field1.setImmediate(true);
        field1.addValidator(new Validator() {

            @Override
            public void validate(Object value) throws InvalidValueException {
                if (!isValid(value)) {
                    throw new InvalidValueException("FAIL!");
                }
            }

            public boolean isValid(Object value) {
                return field1.getValue().equals("valid");
            }
        });
        form.addField("Field 1", field1);
        layout.addComponent(form);

        getMainWindow().addWindow(subWindow);
        subWindow.bringToFront();
    }

    @Override
    protected String getDescription() {
        return "When both window and its content have undefined height, window must not reserve space for a scroll bar when it is not needed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8852;
    }

}
