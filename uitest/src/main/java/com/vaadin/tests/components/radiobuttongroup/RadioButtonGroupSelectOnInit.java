package com.vaadin.tests.components.radiobuttongroup;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.RadioButtonGroup;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class RadioButtonGroupSelectOnInit extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        RadioButtonGroup<String> rbg = new RadioButtonGroup<>();
        rbg.setItems("Foo", "Bar", "Baz");
        rbg.setSelectedItem("Bar");

        addComponent(rbg);
        addComponent(new Button("Deselect", e -> rbg.setSelectedItem(null)));
        addComponent(new Button("Select Bar", e -> rbg.setSelectedItem("Bar")));
        addComponent(
                new Button("Refresh", e -> rbg.getDataProvider().refreshAll()));
    }
}
