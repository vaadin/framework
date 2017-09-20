package com.vaadin.tests.components.upload;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.Upload;

import java.io.ByteArrayOutputStream;

/**
 * Test UI for case where Upload is in a TabSheet and Tab is changed directly
 * after Upload Succeed
 */
@Push
public class UploadInTabsheetV7 extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet t = new TabSheet();
        Upload upload = new Upload("Upload", (filename, mimeType) -> new
            ByteArrayOutputStream());
        upload.setImmediate(false);
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
