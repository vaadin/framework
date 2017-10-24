package com.vaadin.tests.components.combobox;

import java.io.File;

import com.vaadin.server.FileResource;
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
                return new FileResource(file);
                // InputStream is = new FileInputStream(file);
                // StreamResource stream = new StreamResource(() -> is,
                // file.getName());
                // stream.setMIMEType(FileTypeResolver.getMIMEType(file));
                // System.out
                // .println("ComboBoxItemIconConnectorResource: mime type "
                // + FileTypeResolver.getMIMEType(file));
                // return stream;
            } catch (Exception e) {
                return null;
            }
        });

        cb.setValue("Hungary");

        addComponent(cb);
    }

}
