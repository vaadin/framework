package com.vaadin.tests.components.combobox;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxItemIconConnectorResource extends AbstractTestUI {

    @Override
    protected Integer getTicketNumber() {
        return 9041;
    }

    @Override
    protected String getTestDescription() {
        return "All items in the ComboBoxes should have icons, when using a ConnectorResource";
    }

    @Override
    @SuppressWarnings("resource")
    protected void setup(VaadinRequest request) {
        ComboBox<String> cb = new ComboBox<>();
        cb.setItems("Finland", "Australia", "Hungary");

        cb.setItemIconGenerator(item -> {
            try {
                File file = new File("src/main/webapp/VAADIN/themes"
                        + "/tests-tickets/icons/"
                        + item.substring(0, 2).toLowerCase() + ".gif");
                InputStream is = new FileInputStream(file);
                return new StreamResource(() -> is, file.getName());
            } catch (Exception e) {
                return null;
            }
        });

        cb.setValue("Hungary");

        addComponent(cb);
    }

}
