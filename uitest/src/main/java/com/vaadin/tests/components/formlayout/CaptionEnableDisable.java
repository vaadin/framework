package com.vaadin.tests.components.formlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

public class CaptionEnableDisable extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        setContent(createFormLayout());

    }

    public FormLayout createFormLayout() {
        FormLayout layout = new FormLayout();
        final TextField textField = new TextField("TextField");
        textField.setEnabled(false);
        layout.addComponent(textField);

        final ComboBox combobox = new ComboBox("Combobox");
        combobox.setEnabled(false);
        layout.addComponent(combobox);

        final NativeSelect nativeSelect = new NativeSelect("NativeSelect");
        nativeSelect.setEnabled(false);
        layout.addComponent(nativeSelect);

        final CheckBox checkBox = new CheckBox("Checkbox");
        checkBox.setEnabled(false);
        layout.addComponent(checkBox);

        layout.addComponent(new Button("Toggle components enabled",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        combobox.setEnabled(!combobox.isEnabled());
                        textField.setEnabled(!textField.isEnabled());
                        checkBox.setEnabled(!checkBox.isEnabled());
                        nativeSelect.setEnabled(!nativeSelect.isEnabled());
                    }
                }));
        return layout;
    }

    @Override
    protected String getTestDescription() {
        return "";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12062;
    }

}
