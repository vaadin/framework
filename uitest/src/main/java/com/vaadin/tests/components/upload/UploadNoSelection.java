package com.vaadin.tests.components.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.upload.AllowUploadWithoutFilenameExtension;
import com.vaadin.ui.Button;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;

@Widgetset(TestingWidgetSet.NAME)
public class UploadNoSelection extends AbstractTestUIWithLog
        implements Receiver {

    static final String LOG_ID_PREFIX = "Log_row_";
    static final String UPLOAD_ID = "u";

    static final String UPLOAD_FINISHED = "Upload Finished";
    static final String RECEIVING_UPLOAD = "Receiving upload";

    static final String FILE_LENGTH_PREFIX = "File length:";
    static final String FILE_NAME_PREFIX = "File name:";

    @Override
    protected Integer getTicketNumber() {
        return 9602;
    }

    @Override
    protected String getTestDescription() {
        return "Uploading an empty selection (no file) should not be possible by "
                + "default. If the default behavior is overridden with a custom "
                + "extension the upload attempt will trigger FinishedEvent with "
                + "0-length file size and empty filename.";
    }

    @Override
    protected void setup(VaadinRequest request) {
        Upload u = new Upload("Upload", this);
        u.setId(UPLOAD_ID);
        u.setSizeUndefined();
        u.setImmediateMode(false);

        addComponent(u);

        u.addFinishedListener(event -> {
            log(UPLOAD_FINISHED);
            log(FILE_LENGTH_PREFIX + " " + event.getLength());
            log(FILE_NAME_PREFIX + " " + event.getFilename());
        });
        u.addFailedListener(event -> {
            log("Upload Failed");
            log(FILE_LENGTH_PREFIX + " " + event.getLength());
            log(FILE_NAME_PREFIX + " " + event.getFilename());
        });

        Button progButton = new Button("Upload programmatically",
                e -> u.submitUpload());
        progButton.setId("programmatic");
        addComponent(progButton);

        Button extButton = new Button("Allow upload without filename",
                e -> AllowUploadWithoutFilenameExtension.wrap(u));
        extButton.setId("extend");
        addComponent(extButton);
    }

    @Override
    public OutputStream receiveUpload(String filename, String MIMEType) {
        log(RECEIVING_UPLOAD);
        return new ByteArrayOutputStream();
    }

}
