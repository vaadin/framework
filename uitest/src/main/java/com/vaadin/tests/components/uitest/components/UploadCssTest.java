package com.vaadin.tests.components.uitest.components;

import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.Upload;

public class UploadCssTest {

    private int debugIdCounter = 0;

    public UploadCssTest(TestSampler parent) {
        Upload up = new Upload();
        up.setId("upload" + debugIdCounter++);
        parent.addComponent(up);

        up = new Upload();
        up.setId("upload" + debugIdCounter++);
        up.setImmediate(true);
        parent.addComponent(up);
    }
}
