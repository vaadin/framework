package com.vaadin.tests.widgetset.server.upload;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.Upload;

public class AllowUploadWithoutFilenameExtension extends AbstractExtension {

    public static AllowUploadWithoutFilenameExtension wrap(Upload upload) {
        AllowUploadWithoutFilenameExtension extension = new AllowUploadWithoutFilenameExtension();
        extension.extend(upload);
        return extension;
    }
}
