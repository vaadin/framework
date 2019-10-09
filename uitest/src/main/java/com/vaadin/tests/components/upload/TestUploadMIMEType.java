package com.vaadin.tests.components.upload;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class TestUploadMIMEType extends AbstractTestUI {

    public static final String TEST_MIME_TYPE = "application/pdf";
    private UploadReceiver receiver = new UploadReceiver();

    public static class UploadReceiver implements Receiver {

        private String filename;

        @Override
        public OutputStream receiveUpload(String filename, String MIMEType) {
            this.filename = filename;
            return new ByteArrayOutputStream();
        }

        public String getFilename() {
            return filename;
        }

    }

    @Override
    public String getDescription() {
        return "MIME types for an Upload component should be preserved after the first upload";
    }

    @Override
    protected void setup(VaadinRequest request) {
        Upload upload = new Upload("Upload a file", receiver);
        upload.setAcceptMimeTypes(TEST_MIME_TYPE);
        addComponent(upload);
    }

    @Override
    protected Integer getTicketNumber() {
        return 119698;
    }

}
