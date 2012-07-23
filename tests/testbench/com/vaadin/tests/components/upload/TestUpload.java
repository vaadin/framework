package com.vaadin.tests.components.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;

public class TestUpload extends ComponentTestCase<Upload> implements Receiver {

    @Override
    protected Integer getTicketNumber() {
        return 3525;
    }

    @Override
    protected Class<Upload> getTestClass() {
        return Upload.class;
    }

    @Override
    protected void initializeComponents() {
        Upload u;

        u = new Upload("Undefined wide upload", this);
        u.setSizeUndefined();
        addTestComponent(u);

        u.addListener(new Upload.FinishedListener() {
            @Override
            public void uploadFinished(FinishedEvent event) {
                getMainWindow().showNotification("Done");
            }
        });

        u = new Upload("300px wide upload", this);
        u.setWidth("300px");
        addTestComponent(u);

    }

    @Override
    protected List<Component> createActions() {
        List<Component> actions = new ArrayList<Component>();
        actions.add(createEnabledAction(true));

        return actions;
    }

    @Override
    public OutputStream receiveUpload(String filename, String MIMEType) {
        getMainWindow().showNotification("Receiving upload");
        return new ByteArrayOutputStream();
    }

}
