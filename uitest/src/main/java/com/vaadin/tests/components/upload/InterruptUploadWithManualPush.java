package com.vaadin.tests.components.upload;

import com.vaadin.annotations.Push;
import com.vaadin.shared.communication.PushMode;

@Push(PushMode.MANUAL)
public class InterruptUploadWithManualPush extends InterruptUpload {

    public InterruptUploadWithManualPush() {
        super(true);
    }

    @Override
    protected Integer getTicketNumber() {
        return 11616;
    }

    @Override
    public String getDescription() {
        return "Interrupting an upload with @Push shouldn't prevent uploading that same file immediately afterwards.";
    }

}
