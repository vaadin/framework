package com.vaadin.tests.components.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
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

    @Override
    protected Class<Upload> getTestClass() {
        return Upload.class;
    }

    int counter = 0;
    private Label l;

    @Override
    protected void initializeComponents() {

        final Label labe = new Label();

        addComponent(labe);

        final Upload u;
        u = new Upload(null, this);
        u.setImmediate(true);
        addTestComponent(u);

        l = new Label(getUploadcount());
        addComponent(l);

        u.addListener(new Upload.StartedListener() {

            @Override
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
            @Override
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
        actions.add(createButtonAction("Toggle Enabled",
                new Command<Upload, Boolean>() {

                    @Override
                    public void execute(Upload c, Boolean value, Object data) {
                        c.setEnabled(!c.isEnabled());
                    }
                }));

        return actions;
    }

    @Override
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
