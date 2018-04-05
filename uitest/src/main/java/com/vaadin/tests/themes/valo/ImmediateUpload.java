package com.vaadin.tests.themes.valo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Upload;

public class ImmediateUpload extends AbstractTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {

        // by default is in immediate mode (since 8.0)
        Upload immediateUpload = new Upload();
        immediateUpload.setId("immediateupload");
        addComponent(immediateUpload);

        Upload upload = new Upload();
        upload.setId("upload");
        upload.setImmediateMode(false);
        addComponent(upload);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Immediate upload should hide the button";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(14238);
    }

}
