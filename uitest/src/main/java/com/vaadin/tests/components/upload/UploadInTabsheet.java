package com.vaadin.tests.components.upload;

import java.io.ByteArrayOutputStream;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Upload;

/**
 * Test UI for case where Upload is in a TabSheet and Tab is changed directly
 * after Upload Succeed
 */
@Push
public class UploadInTabsheet extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet t = new TabSheet();
        Upload upload = new Upload("Upload", (filename, mimeType) -> new
            ByteArrayOutputStream());
        upload.setImmediateMode(false);
        upload.addSucceededListener(event -> upload.getUI().access(()->{
                t.setSelectedTab(1);
            }));
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
