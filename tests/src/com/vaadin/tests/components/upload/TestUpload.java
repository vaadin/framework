package com.vaadin.tests.components.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;

public class TestUpload extends ComponentTestCase<Upload> implements Receiver {

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        return 3525;
    }

    @Override
    protected void setup() {
        super.setup();
        Upload u;

        u = new Upload("Undefined wide upload", this);
        u.setSizeUndefined();
        addTestComponent(u);

        u.addListener(new Upload.FinishedListener() {
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

        CheckBox enabled = new CheckBox("Enabled", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                for (Upload c : getTestComponents()) {
                    c.setEnabled(event.getButton().booleanValue());
                }

            }
        });
        enabled.setValue(true);
        enabled.setImmediate(true);
        actions.add(enabled);

        return actions;
    }

    public OutputStream receiveUpload(String filename, String MIMEType) {
        getMainWindow().showNotification("Receiving upload");
        return new ByteArrayOutputStream();
    }

}
