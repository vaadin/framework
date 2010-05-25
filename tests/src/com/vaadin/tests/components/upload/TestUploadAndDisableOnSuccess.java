package com.vaadin.tests.components.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;

public class TestUploadAndDisableOnSuccess extends ComponentTestCase<Upload>
        implements Receiver {
    @Override
    protected String getDescription() {
        return "If upload is detached and attached during upload, the client side componenent never receives information that the upload has finished. Second update will not be successful.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4605;
    }

    int counter = 0;
    private Label l;

    @Override
    protected void setup() {
        super.setup();

        final Label labe = new Label();

        addComponent(labe);

        final Upload u;
        u = new Upload(null, this);
        u.setImmediate(true);
        addTestComponent(u);

        l = new Label(getUploadcount());
        addComponent(l);

        u.addListener(new Upload.StartedListener() {

            public void uploadStarted(StartedEvent event) {
                /*
                 * Remove component before upload from the same vertical layout.
                 * Causes upload to be detached/attached -> upload loses it
                 * target iframes onload listener -> puts VUpload inappropriate
                 * state.
                 */
                getLayout().removeComponent(labe);
            }
        });

        u.addListener(new Upload.FinishedListener() {
            public void uploadFinished(FinishedEvent event) {
                getMainWindow().showNotification("Done");
                l.setValue(getUploadcount());
            }
        });

    }

    private String getUploadcount() {
        return counter++ + " uploads";
    }

    @Override
    protected List<Component> createActions() {
        List<Component> actions = new ArrayList<Component>();
        Button enabled = new Button("Toggle Enabled", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                for (Upload c : getTestComponents()) {
                    c.setEnabled(!c.isEnabled());
                }
            }
        });
        actions.add(enabled);

        return actions;
    }

    public OutputStream receiveUpload(String filename, String MIMEType) {
        // sleep to ensure change before upload is complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ByteArrayOutputStream();
    }

}
