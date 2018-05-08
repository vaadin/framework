package com.vaadin.tests.components.radiobuttongroup;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.RadioButtonGroup;

/**
 * @author Vaadin Ltd
 *
 */
@Widgetset("com.vaadin.DefaultWidgetSet")
public class RadioButtonGroupFocus extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        RadioButtonGroup<String> rbg = new RadioButtonGroup<>("Radios");
        rbg.setItems("Test1", "Test2", "Test3");
        rbg.setSelectedItem("Test2");
        rbg.setItemCaptionGenerator(item -> "Option " + item);
        rbg.focus();
        RadioButtonGroup<String> rbg2 = new RadioButtonGroup<>("No selection");
        rbg2.setItems("Foo1", "Foo2", "Foo3");
        Button button = new Button("focus second group", e -> rbg2.focus());
        addComponents(rbg, rbg2, button);
    }

}
