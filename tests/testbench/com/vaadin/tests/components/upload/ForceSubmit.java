package com.vaadin.tests.components.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;

public class ForceSubmit extends TestBase implements Receiver {

    @Override
    protected Integer getTicketNumber() {
        return 6630;
    }

    @Override
    public OutputStream receiveUpload(String filename, String MIMEType) {
        return new ByteArrayOutputStream();
    }

    @Override
    protected void setup() {

        final TextField textField = new TextField("Test field");
        addComponent(textField);

        final Upload u;

        u = new Upload("Upload", this);

        u.setButtonCaption(null);

        addComponent(u);

        u.addListener(new Upload.FinishedListener() {
            @Override
            public void uploadFinished(FinishedEvent event) {
                String filename = event.getFilename();
                long length = event.getLength();
                getMainWindow().showNotification(
                        "Done. Filename : " + filename + " Lenght: " + length);
            }
        });

        u.addListener(new Upload.FailedListener() {
            @Override
            public void uploadFailed(FailedEvent event) {
                getMainWindow().showNotification("Failed. No file selected?");
            }
        });

        u.addListener(new Upload.StartedListener() {
            @Override
            public void uploadStarted(StartedEvent event) {
                getMainWindow().showNotification(
                        "Started upload. TF value :" + textField.getValue());
            }
        });

        Button button = new Button(
                "I'm an external button (not the uploads builtin), hit me to start upload.");
        button.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                u.submitUpload();
            }
        });

        addComponent(button);

    }

    @Override
    protected String getDescription() {
        return "Some wireframists are just so web 1.0. If requirements "
                + "say the upload must not start until the whole form "
                + "is 'Oukeyd', that is what we gotta do. In these cases "
                + "developers most probably also want to hide the uploads"
                + " internal button by setting its caption to null.";
    }

}
