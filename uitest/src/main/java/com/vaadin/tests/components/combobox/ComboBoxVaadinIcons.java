package com.vaadin.tests.components.combobox;

import java.util.Arrays;

import com.vaadin.annotations.Widgetset;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ComboBoxVaadinIcons extends AbstractTestUI {

    public static final VaadinIcons[] icons = { VaadinIcons.AMBULANCE,
            VaadinIcons.PAPERPLANE, VaadinIcons.AIRPLANE };

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(Arrays.stream(icons).map(VaadinIcons::name));
        comboBox.setItemIconGenerator(VaadinIcons::valueOf);
        addComponent(comboBox);
    }

}
