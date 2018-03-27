package com.vaadin.tests.components.checkboxgroup;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBoxGroup;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class CheckBoxGroupItemDisabled extends AbstractTestUI {

    public static final SerializablePredicate<Integer> ENABLED_PROVIDER = i -> i != 3;

    @Override
    protected void setup(VaadinRequest request) {
        CheckBoxGroup<Integer> cbg = new CheckBoxGroup<>();
        cbg.setItems(1, 2, 3, 4);
        cbg.setItemEnabledProvider(ENABLED_PROVIDER);
        addComponent(cbg);
    }
}
