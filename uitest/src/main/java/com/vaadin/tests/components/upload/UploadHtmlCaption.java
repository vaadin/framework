package com.vaadin.tests.components.upload;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Upload;

public class UploadHtmlCaption extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Upload upload = new Upload();
        upload.setImmediateMode(false);
        upload.setButtonCaption("<b>Submit button</b>");
        upload.setCaption("This is the caption of the <b>entire</b> component");
        addComponent(upload);

        Button toggleButtonCaption = new Button("Toggle button caption",
                e -> upload.setButtonCaptionAsHtml(
                        !upload.isButtonCaptionAsHtml()));
        toggleButtonCaption.setId("toggleButtonCaption");
        addComponent(toggleButtonCaption);

        Button toggleComponentCaption = new Button("Toggle component caption",
                e -> upload.setCaptionAsHtml(!upload.isCaptionAsHtml()));
        toggleComponentCaption.setId("toggleComponentCaption");
        addComponent(toggleComponentCaption);
    }

    @Override
    protected Integer getTicketNumber() {
        return 11810;
    }

    @Override
    protected String getTestDescription() {
        return "It should be possible to set component caption and submit button caption "
                + "to allow HTML display mode independently of each other.";
    }
}
