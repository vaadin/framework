package com.vaadin.tests.components.radiobuttongroup;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.RadioButtonGroup;

import java.util.Arrays;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class RadioButtonGroupAfterVisibilityChange extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        RadioButtonGroup<Boolean> radio = new RadioButtonGroup<>("Radio",
                Arrays.asList(true, false));
        radio.setId("radioButton");
        addComponent(radio);

        Button hideButton = new Button("Hide");
        hideButton.setId("hideB");
        hideButton.addClickListener(event1 -> radio.setVisible(false));
        addComponent(hideButton);

        Button setAndShowButton = new Button("Set and Show");
        setAndShowButton.setId("setAndShow");
        setAndShowButton.addClickListener(event1 -> {
            radio.setValue(true);
            radio.setVisible(true);
        });
        addComponent(setAndShowButton);
    }
}
