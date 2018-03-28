package com.vaadin.tests.components.combobox;

import java.util.Map;

import com.vaadin.v7.ui.ComboBox;

/**
 * A combo box component with delay. Can be useful to use while testing UI.
 */
public class SlowComboBox extends ComboBox {
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.changeVariables(source, variables);
    }
}
