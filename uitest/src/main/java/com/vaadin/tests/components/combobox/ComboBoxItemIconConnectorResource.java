package com.vaadin.tests.components.combobox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.vaadin.server.ConnectorResource;
import com.vaadin.server.DownloadStream;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.util.FileTypeResolver;

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
                // return new ConnectorResource() {
                //
                // @Override
                // public String getMIMEType() {
                // return FileTypeResolver.getMIMEType(file);
                // }
                //
                // @Override
                // public DownloadStream getStream() {
                // try {
                // return new DownloadStream(new FileInputStream(file),
                // getMIMEType(), getFilename());
                // } catch (FileNotFoundException e) {
                // e.printStackTrace();
                // return null;
                // }
                // }
                //
                // @Override
                // public String getFilename() {
                // return file.getName();
                // }
                // };
                ConnectorResource stream = new StreamResource(null, null) {
                    @Override
                    public DownloadStream getStream() {
                        try {
                            return new DownloadStream(new FileInputStream(file),
                                    getMIMEType(), getFilename());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public String getMIMEType() {
                        return FileTypeResolver.getMIMEType(file);
                    }

                    @Override
                    public String getFilename() {
                        return file.getName();
                    }
                };
                // stream.setMIMEType(FileTypeResolver.getMIMEType(file));
                return stream;
            } catch (Exception e) {
                return null;
            }
        });

        cb.setValue("Hungary");

        addComponent(cb);
    }

}
