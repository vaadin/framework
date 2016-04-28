package com.vaadin.tests.components.upload;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Upload;

public class DisabledUploadButton extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Upload upload = new Upload();

        addComponent(upload);

        addButton("Set readonly", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                upload.setReadOnly(true);
            }
        });

        addButton("Set disabled", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                upload.setEnabled(false);
            }
        });
    }

    @Override
    public String getDescription() {
        return "Upload button should be disabled when upload "
                + "is set to readonly and/or disabled";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14655;
    }
}
