package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class InitialFocus extends AbstractTestUI {

    public static final String FOCUS_NAME_BUTTON_ID = "focusNameButton";
    public static final String FOCUS_GENDER_BUTTON_ID = "focusGenderButton";
    public static final String NAME_FIELD_ID = "nameField";
    public static final String GENDER_FIELD_ID = "genderField";

    @Override
    protected void setup(VaadinRequest request) {
        Button focusNameButton = new Button("Open Window and focus Name");
        focusNameButton.setId(FOCUS_NAME_BUTTON_ID);
        focusNameButton.addClickListener(event -> {
            MyDialog myDialog = new MyDialog();
            myDialog.setClosable(true);
            myDialog.center();
            getUI().addWindow(myDialog);
            myDialog.bringToFront();
            myDialog.focusNameField();
        });
        addComponent(focusNameButton);

        Button focusGenderButton = new Button("Open Window and focus Gender");
        focusGenderButton.setId(FOCUS_GENDER_BUTTON_ID);
        focusGenderButton.addClickListener(event -> {
            MyDialog myDialog = new MyDialog();
            myDialog.setClosable(true);
            myDialog.center();
            getUI().addWindow(myDialog);
            myDialog.bringToFront();
            myDialog.focusGenderField();
        });
        addComponent(focusGenderButton);
    }

    private static class MyDialog extends Window {
        private TextField nameField;
        private ComboBox genderField;

        private MyDialog() {
            super("MyDialog");
            setWidth("400px");
            setHeight("300px");
            VerticalLayout hl = new VerticalLayout();
            hl.setSizeFull();
            nameField = new TextField("Name");
            nameField.setId(NAME_FIELD_ID);
            hl.addComponent(this.nameField);

            genderField = new ComboBox("Gender");
            genderField.setId(GENDER_FIELD_ID);
            hl.addComponentsAndExpand(genderField);

            this.setContent(hl);
        }

        private void focusNameField() {
            nameField.focus();
        }

        private void focusGenderField() {
            genderField.focus();
        }
    }
}
