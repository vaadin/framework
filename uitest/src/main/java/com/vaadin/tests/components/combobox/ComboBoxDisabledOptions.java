package com.vaadin.tests.components.combobox;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.UI;

/**
 * Created by lorenzob on 25/11/16.
 */
@Theme("valo")
public class ComboBoxDisabledOptions extends UI {

    @Override
    protected void init(VaadinRequest request) {
        ComboBox comboBox = new ComboBox();
        comboBox.addItem("Superman");
        comboBox.addItem("Ironman");
        comboBox.addItem("Hulk");
        comboBox.addItem("Thor");
        comboBox.addItem("Batman");
        comboBox.addItem("Spiderman");
        comboBox.addItem("Wolverine");
        comboBox.addItem("Green Lantern");
        comboBox.addItem("Flash");
        comboBox.addItem("Daredevil");
        comboBox.addItem("Deadpool");
        comboBox.setItemEnabled("Ironman", false);
        comboBox.setItemEnabled("Batman", false);
        comboBox.setItemEnabled("Flash", false);
        comboBox.setItemEnabled("Daredevil", false);
        setContent(comboBox);
    }

}
