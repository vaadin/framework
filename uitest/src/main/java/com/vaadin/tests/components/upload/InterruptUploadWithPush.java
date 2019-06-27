package com.vaadin.tests.components.upload;

import com.vaadin.annotations.Push;

@Push
public class InterruptUploadWithPush extends InterruptUpload {

    @Override
    protected Integer getTicketNumber() {
        return 11616;
    }

    @Override
    public String getDescription() {
        return "Interrupting an upload with @Push shouldn't prevent uploading that same file immediately afterwards.";
    }

}
