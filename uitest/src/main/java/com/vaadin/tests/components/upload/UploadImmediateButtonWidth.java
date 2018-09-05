package com.vaadin.tests.components.upload;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

// We're explicitly testing only immediate uploads here because non-immediate
// width issues still require planning before we can provide a fix.
public class UploadImmediateButtonWidth extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        // Let's use a separate layout without margins to make the
        // button widths not dependent on the selected theme.
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        layout.setSpacing(false);
        layout.setWidth("500px");

        layout.addComponent(getImmediateUpload("upload1", "300px"));
        layout.addComponent(getImmediateUpload("upload2", "50%"));
        layout.addComponent(getImmediateUpload("upload3", ""));

        addComponent(layout);
    }

    private Upload getImmediateUpload(String id, String width) {
        Upload upload = new Upload();

        upload.setId(id);
        upload.setWidth(width);
        upload.setImmediateMode(true);

        return upload;
    }

    @Override
    protected String getTestDescription() {
        return "Width of the upload button should obey setWidth() when using immediate";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14485;
    }

}
