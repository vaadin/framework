package com.vaadin.tests.components.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Upload;

/**
 * Test UI for case where Upload is in a TabSheet and Tab is changed directly
 * after Upload Succeed
 */
@Push
public class UploadInTabsheet extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final TabSheet t = new TabSheet();
        final Upload upload = new Upload("Upload", new Upload.Receiver() {
            @Override
            public OutputStream receiveUpload(String filename, String mimeType) {
                return new ByteArrayOutputStream();
            }
        });
        upload.setImmediate(false);
        upload.addSucceededListener(new Upload.SucceededListener() {
            @Override
            public void uploadSucceeded(Upload.SucceededEvent event) {
                upload.getUI().access(new Runnable() {
                    @Override
                    public void run() {
                        t.setSelectedTab(1);
                    }
                });
            }
        });
        upload.setWidthUndefined();

        t.addComponent(upload);
        t.addComponent(new Label("Second tab"));

        addComponent(t);
    }

    @Override
    protected Integer getTicketNumber() {
        return 8728;
    }

}
