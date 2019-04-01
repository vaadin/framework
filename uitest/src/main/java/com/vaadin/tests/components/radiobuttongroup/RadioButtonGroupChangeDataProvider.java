package com.vaadin.tests.components.radiobuttongroup;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.Button;


public class RadioButtonGroupChangeDataProvider extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        RadioButtonGroup<String> radio = new RadioButtonGroup<>();
        radio.setItems("aaa", "bbb", "ccc", "ddd");
        radio.setId("radioButton");

        addComponent(radio);
        Button changeProvider = new Button(
                "New Data Provider - without selected item",
                e -> radio.setItems("111", "222", "333", "444"));
        changeProvider.setId("changeProvider");
        addComponent(changeProvider);
    }
}
