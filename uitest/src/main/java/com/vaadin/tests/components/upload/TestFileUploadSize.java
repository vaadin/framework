package com.vaadin.tests.components.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;

public class TestFileUploadSize extends TestBase implements Receiver {

    private Label label = new Label("No finished uploads.");
    private Label receivedSize = new Label("-");
    private Label expectedSize = new Label("-");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    @Override
    protected void setup() {
        getLayout().setMargin(new MarginInfo(true, false, false, false));
        getLayout().setSpacing(true);

        Upload u = new Upload("Upload", new Upload.Receiver() {

            @Override
            public OutputStream receiveUpload(String filename, String mimeType) {
                return baos;
            }
        });
        u.setId("UPL");
        u.addStartedListener(new Upload.StartedListener() {

            @Override
            public void uploadStarted(StartedEvent event) {
                expectedSize.setValue(String.valueOf(event.getContentLength()));
            }
        });
        u.addFinishedListener(new Upload.FinishedListener() {

            @Override
            public void uploadFinished(FinishedEvent event) {
                label.setValue("Upload finished. Name: " + event.getFilename());
                receivedSize.setValue(String.valueOf(baos.size()));
                baos.reset();
            }
        });

        expectedSize.setId("expected");
        receivedSize.setId("received");

        GridLayout grid = new GridLayout(2, 2);
        grid.addComponent(new Label("Expected size:"), 0, 0);
        grid.addComponent(new Label("Received size:"), 0, 1);
        grid.addComponent(expectedSize, 1, 0);
        grid.addComponent(receivedSize, 1, 1);

        addComponent(label);
        addComponent(grid);
        addComponent(u);
    }

    @Override
    public OutputStream receiveUpload(String filename, String MIMEType) {
        Notification.show("Receiving upload");
        return new ByteArrayOutputStream();
    }

    @Override
    protected Integer getTicketNumber() {
        return 9548;
    }

    @Override
    protected String getDescription() {
        return "Multibyte characters in filenames should not cause the upload size to be computed incorrectly";
    }

}
