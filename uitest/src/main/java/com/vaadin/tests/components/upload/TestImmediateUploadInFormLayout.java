package com.vaadin.tests.components.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;

public class TestImmediateUploadInFormLayout extends
        ComponentTestCase<FormLayout> implements Receiver {

    @Override
    protected String getDescription() {
        return "On Firefox 3.5 and Opera 10.10, clicking on an immediate upload in a wide FormLayout has no effect";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4359;
    }

    @Override
    protected Class<FormLayout> getTestClass() {
        return FormLayout.class;
    }

    @Override
    protected void initializeComponents() {

        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("100%");
        Upload u = new Upload("Upload in FormLayout", this);
        u.setImmediate(true);
        formLayout.addComponent(u);
        addTestComponent(formLayout);

    }

    @Override
    protected List<Component> createActions() {
        return Collections.emptyList();
    }

    @Override
    public OutputStream receiveUpload(String filename, String MIMEType) {
        getMainWindow().showNotification("Receiving upload");
        return new ByteArrayOutputStream();
    }
}
