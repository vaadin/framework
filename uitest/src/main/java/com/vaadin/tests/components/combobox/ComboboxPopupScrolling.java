package com.vaadin.tests.components.combobox;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

@Theme("valo")
public class ComboboxPopupScrolling extends AbstractTestUIWithLog {
    @Override
    protected void setup(VaadinRequest request) {
        ComboBox combobox = new ComboBox("100px wide combobox");
        combobox.setWidth("100px");
        combobox.addItem("AMERICAN SAMOA");
        combobox.addItem("ANTIGUA AND BARBUDA");

        ComboBox combobox2 = new ComboBox("250px wide combobox");
        combobox2.setWidth("250px");
        combobox2.addItem("AMERICAN SAMOA");
        combobox2.addItem("ANTIGUA AND BARBUDA");

        ComboBox combobox3 = new ComboBox("Undefined wide combobox");
        combobox3.setWidth(null);
        combobox3.addItem("AMERICAN SAMOA");
        combobox3.addItem("ANTIGUA AND BARBUDA");

        ComboBox combobox4 = new ComboBox("Another 100px wide combobox");
        combobox4.setWidth("100px");
        for (int i = 0; i < 10; i++) {
            combobox4.addItem("AMERICAN SAMOA " + i);
            combobox4.addItem("ANTIGUA AND BARBUDA " + i);
        }

        HorizontalLayout hl = new HorizontalLayout(combobox, combobox2,
                combobox3, combobox4);
        addComponent(hl);
    }

}