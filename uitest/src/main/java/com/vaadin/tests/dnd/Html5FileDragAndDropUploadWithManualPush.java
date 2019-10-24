package com.vaadin.tests.dnd;

import com.vaadin.annotations.Push;
import com.vaadin.shared.communication.PushMode;

@Push(PushMode.MANUAL)
public class Html5FileDragAndDropUploadWithManualPush
        extends Html5FileDragAndDropUpload {
    public Html5FileDragAndDropUploadWithManualPush() {
        super(true);
    }
}
