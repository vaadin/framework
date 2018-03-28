package com.vaadin.tests.components.upload;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Upload;

/**
 * Test UI for browser-dependent tootlip for Upload component.
 *
 * @author Vaadin Ltd
 */
public class UploadTitleWithTooltip extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Upload upload = new Upload();
        upload.setDescription("tooltip");
        upload.setImmediateMode(false);

        addComponent(upload);
    }

    @Override
    protected String getTestDescription() {
        return "Browser dependent title should not be visible for upload if Vaadin tooltip is used";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14482;
    }

}
