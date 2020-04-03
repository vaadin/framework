package com.vaadin.tests.components.ui;

import java.io.IOException;
import java.io.OutputStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.ChangeListener;
import com.vaadin.ui.VerticalLayout;

public class MultiFileUploadTest extends AbstractTestUIWithLog {

    private ChangeListener changeListener = event -> {
        if (event.getFilename().isEmpty()) {
            removeUpload(event.getSource());
        } else {
            addUpload();
        }
    };
    private VerticalLayout uploadsLayout = new VerticalLayout();

    @Override
    protected void setup(VaadinRequest request) {
        getPage().getStyles()
                .add(".v-upload-hidden-button .v-button {display:none};");
        addUpload();
        addComponent(uploadsLayout);
        addComponent(new Button("Upload files", event -> {
            for (Upload u : getUploads()) {
                u.submitUpload();
            }
        }));
    }

    protected Iterable<Upload> getUploads() {
        return (Iterable) uploadsLayout;
    }

    protected void removeUpload(Upload source) {
        uploadsLayout.removeComponent(source);

    }

    protected void addUpload() {
        Upload upload = createUpload();
        upload.addSucceededListener(event -> {
            log("Upload of " + event.getFilename() + " complete");
            uploadsLayout.removeComponent(event.getUpload());
        });

        upload.addFailedListener(
                event -> log("Upload of " + event.getFilename() + " FAILED"));

        upload.setReceiver((filename, mimeType) -> {
            return new OutputStream() {
                @Override
                public void write(int arg0) throws IOException {

                }
            };
        });
        upload.setStyleName("hidden-button");
        uploadsLayout.addComponent(upload);

    }

    private Upload createUpload() {
        Upload upload = new Upload();
        upload.setImmediateMode(false);
        upload.addChangeListener(changeListener);
        return upload;
    }

    @Override
    protected String getTestDescription() {
        return "Tests that an Upload change event can be used to create a multiple file upload component";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13222;
    }

}
