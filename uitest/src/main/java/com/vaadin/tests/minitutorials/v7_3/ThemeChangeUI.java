package com.vaadin.tests.minitutorials.v7_3;

import java.util.Arrays;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.ComboBox;

public class ThemeChangeUI extends UI {

    private String[] themes = { "valo", "reindeer", "runo", "chameleon" };

    @Override
    protected void init(VaadinRequest request) {
        ComboBox themePicker = new ComboBox("Theme", Arrays.asList(themes));
        themePicker.setValue(getTheme());

        themePicker.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                String theme = (String) event.getProperty().getValue();
                setTheme(theme);
            }
        });

        setContent(themePicker);
    }
}
