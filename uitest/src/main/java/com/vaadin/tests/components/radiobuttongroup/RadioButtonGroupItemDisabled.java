package com.vaadin.tests.components.radiobuttongroup;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.RadioButtonGroup;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class RadioButtonGroupItemDisabled extends AbstractTestUI {

    public static final SerializablePredicate<Integer> ENABLED_PROVIDER = i -> i != 3;

    @Override
    protected void setup(VaadinRequest request) {
        RadioButtonGroup<Integer> rbg = new RadioButtonGroup<>();
        rbg.setItems(1, 2, 3, 4);
        rbg.setItemEnabledProvider(ENABLED_PROVIDER);
        addComponent(rbg);
    }
}
