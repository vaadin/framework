package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ComboboxPopupScrolling extends AbstractTestUIWithLog {
    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> combobox = new ComboBox<>("100px wide combobox");
        combobox.setWidth("100px");
        combobox.setItems("AMERICAN SAMOA", "ANTIGUA AND BARBUDA");

        ComboBox<String> combobox2 = new ComboBox<>("250px wide combobox");
        combobox2.setWidth("250px");
        combobox2.setItems("AMERICAN SAMOA", "ANTIGUA AND BARBUDA");

        ComboBox<String> combobox3 = new ComboBox<>("Undefined wide combobox");
        combobox3.setWidth(null);
        combobox3.setItems("AMERICAN SAMOA", "ANTIGUA AND BARBUDA");

        ComboBox<String> combobox4 = new ComboBox<>(
                "Another 100px wide combobox");
        combobox4.setWidth("100px");
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            items.add("AMERICAN SAMOA " + i);
            items.add("ANTIGUA AND BARBUDA " + i);
        }
        combobox4.setItems(items);

        HorizontalLayout hl = new HorizontalLayout(combobox, combobox2,
                combobox3, combobox4);
        addComponent(hl);

        Label spacer = new Label();
        spacer.setHeight("800px");
        addComponent(spacer);
    }

}