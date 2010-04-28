package com.vaadin.tests.components.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
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

public class TestUploadAndDisableOnSuccess extends ComponentTestCase implements
        Receiver {
    @Override
    protected String getDescription() {
        return "Possible timing issue, when upload is disabled on success.";
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
        final Upload u;

        u = new Upload("Undefined wide upload", this);
        u.setImmediate(true);
        addTestComponent(u);

        l = new Label(getUploadcount());
        addComponent(l);

        // TODO incomplete test, still hard to repeat the issue

        u.addListener(new Upload.StartedListener() {

            public void uploadStarted(StartedEvent event) {
                addComponent(new Label("SluggishLabel") {
                    @Override
                    public void paintContent(PaintTarget target)
                            throws PaintException {
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        super.paintContent(target);
                    }
                });

            }
        });

        u.addListener(new Upload.FinishedListener() {
            public void uploadFinished(FinishedEvent event) {
                getMainWindow().showNotification("Done");
                u.setEnabled(false);
                Label l2 = new Label(getUploadcount());
                getLayout().replaceComponent(l, l2);
                l = l2;
            }
        });

    }

    private String getUploadcount() {
        return counter++ + " Downloads";
    }

    @Override
    protected List<Component> createActions() {
        List<Component> actions = new ArrayList<Component>();

        Button enabled = new Button("Toggle Enabled", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                for (Component c : getTestComponents()) {
                    c.setEnabled(!c.isEnabled());
                }

            }
        });
        actions.add(enabled);

        return actions;
    }

    public OutputStream receiveUpload(String filename, String MIMEType) {
        getMainWindow().showNotification("Receiving upload");
        return new ByteArrayOutputStream();
    }

}
