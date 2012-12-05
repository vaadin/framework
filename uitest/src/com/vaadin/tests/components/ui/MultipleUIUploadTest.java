package com.vaadin.tests.components.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

public class MultipleUIUploadTest extends AbstractTestUI {

    private MemoryBuffer buffer = new MemoryBuffer();
    private Upload upload;

    @Override
    protected String getTestDescription() {
        return "Using Upload with multiple UIs causes NPE."
                + " Open test in first browser window and open the file selection window."
                + " Then open test in second browser window (without ?restartApplication) and click the notification button."
                + " Then go back to the first window, select a file, and click Upload."
                + " Click notification button to ensure the upload was received successfully.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10112;
    }

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        upload = new Upload(null, buffer);
        upload.setId("upload");
        layout.addComponent(upload);

        Button button = new Button("show notification");
        button.setId("notify");
        button.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Notification.show("uploaded: " + buffer.getFileName());
            }
        });
        layout.addComponent(button);

    }

    public class MemoryBuffer implements StreamResource.StreamSource,
            Upload.Receiver {
        ByteArrayOutputStream outputBuffer = null;

        String mimeType;

        String fileName;

        public MemoryBuffer() {

        }

        @Override
        public InputStream getStream() {
            if (outputBuffer == null) {
                return null;
            }
            return new ByteArrayInputStream(outputBuffer.toByteArray());
        }

        /**
         * @see com.vaadin.ui.Upload.Receiver#receiveUpload(String, String)
         */
        @Override
        public OutputStream receiveUpload(String filename, String MIMEType) {
            fileName = filename;
            mimeType = MIMEType;
            outputBuffer = new ByteArrayOutputStream() {
                @Override
                public synchronized void write(byte[] b, int off, int len) {
                    super.write(b, off, len);
                }

            };
            return outputBuffer;
        }

        /**
         * Returns the fileName.
         * 
         * @return String
         */
        public String getFileName() {
            return fileName;
        }

        /**
         * Returns the mimeType.
         * 
         * @return String
         */
        public String getMimeType() {
            return mimeType;
        }

    }

}
