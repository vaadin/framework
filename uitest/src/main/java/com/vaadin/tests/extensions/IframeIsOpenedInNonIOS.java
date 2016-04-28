package com.vaadin.tests.extensions;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import java.io.*;

public class IframeIsOpenedInNonIOS extends AbstractTestUI {

    public static final String FILE_CONTENT = "New text file";
    public static final String FILE_NAME = "textfile.txt";

    @Override
    protected void setup(VaadinRequest request) {
        final Label errorLabel = new Label("No error");
        Button button = new Button("Download");
        FileDownloader downloader = new FileDownloader(new StreamResource(
                new StreamResource.StreamSource() {
                    @Override
                    public InputStream getStream() {
                        return createSomeFile();
                    }
                }, FILE_NAME));
        downloader.extend(button);

        addComponents(errorLabel, button);
    }

    private InputStream createSomeFile() {
        return new ByteArrayInputStream(FILE_CONTENT.getBytes());
    }

    @Override
    protected Integer getTicketNumber() {
        return 15366;
    }

    @Override
    public String getDescription() {
        return "IFrame with a file is not shown in iOS";
    }
}
