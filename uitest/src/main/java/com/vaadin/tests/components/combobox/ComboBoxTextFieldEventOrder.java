package com.vaadin.tests.components.combobox;

import java.util.Arrays;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.Select;
import com.vaadin.v7.ui.TextField;

public class ComboBoxTextFieldEventOrder extends TestBase {

    @Override
    protected void setup() {
        TextField textField = new TextField("text field");
        textField.setImmediate(true);
        final Select select = new Select("select",
                Arrays.asList("1", "2", "3", "4"));
        textField.addValueChangeListener(event ->
            // or just select.requestRepaint()
            select.addItem(Long.valueOf(select.size() + 1).toString()));
        addComponent(textField);
        addComponent(select);
    }

    @Override
    protected String getDescription() {
        return "Entering a text in a TextField and then clicking on the button in a ComboBox should cause the TextField value change to be sent first and the ComboBox filtering afterwards. Failure to do so will cause errors if the value change listener modifies the ComboBox";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7481;
    }
}
