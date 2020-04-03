package com.vaadin.tests.widgetset.client.upload;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VUpload;
import com.vaadin.client.ui.upload.UploadConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.upload.AllowUploadWithoutFilenameExtension;

@Connect(AllowUploadWithoutFilenameExtension.class)
public class AllowUploadWithoutFilenameConnector
        extends AbstractExtensionConnector {

    @Override
    protected void extend(ServerConnector target) {
        UploadConnector connector = ((UploadConnector) target);
        allowUploadWithoutFilename(connector.getWidget());
    }

    private native void allowUploadWithoutFilename(VUpload upload)
    /*-{
        upload.@com.vaadin.client.ui.VUpload::allowUploadWithoutFilename = true;
    }-*/;

}
