package com.vaadin.tests.components.upload;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Upload;

public class UploadChangeListener extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        Upload upload = new Upload();
        upload.setImmediateMode(false);
        upload.addChangeListener(e -> {
            log("change");
        });
        upload.addFinishedListener(e -> {
            log("finished");
        });
        addComponent(upload);
    }

    @Override
    protected String getTestDescription() {
        return "Change listener should still work after first upload.";
    };

    @Override
    protected Integer getTicketNumber() {
        return 10420;
    }
}
