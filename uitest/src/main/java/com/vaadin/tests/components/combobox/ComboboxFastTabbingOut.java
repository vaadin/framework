package com.vaadin.tests.components.combobox;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

import java.util.ArrayList;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ComboboxFastTabbingOut extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> combobox = new ComboBox<>(
                "Press any letter and tab out fast. The pop-up should stay closed");
        ArrayList<String> values = new ArrayList<>();
        values.add("AMERICAN SAMOA");
        values.add("ANTIGUA AND BARBUDA");
        values.add("Bali");
        combobox.setItems(values);

        ComboBox<String> combobox2 = new ComboBox<>(
                "Focusing after tabbing from another CB should not open the pop-up");

        combobox2.setItems("AMERICAN SAMOA", "ANTIGUA AND BARBUDA", "Lake 1",
                "Lake 2");
        addComponent(combobox);
        addComponent(combobox2);
    }

    @Override
    protected String getTestDescription() {
        return "On tabbing out fast, the popup window stays closed";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11354;
    }

}
