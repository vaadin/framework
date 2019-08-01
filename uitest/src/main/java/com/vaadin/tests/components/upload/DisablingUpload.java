package com.vaadin.tests.components.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;

@Push
public class DisablingUpload extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        Upload ul = new Upload(
                "Uploading anything will disable the Upload on SucceededListener",
                new Upload.Receiver() {
                    @Override
                    public OutputStream receiveUpload(String s, String s1) {
                        return new ByteArrayOutputStream();
                    }
                });
        Button button = new Button("Disable upload from Button click", e -> {
            ul.setEnabled(!ul.isEnabled());
        });
        button.setId("button-id");

        ul.addSucceededListener(e -> {
            ul.setEnabled(false);
            log("File has been uploaded.");
        });

        ul.addStartedListener(e -> {
            log("File upload starts");
        });

        Button pushButton = new Button("Set the Push Mode");
        pushButton.setId("push-button");

        Button stateButton = new Button("" + ul.isEnabled());
        stateButton.setId("state-button");

        stateButton.addClickListener(event -> {
            stateButton.setCaption("" + ul.isEnabled());
        });
        pushButton.addClickListener(event -> {
            if (UI.getCurrent().getPushConfiguration().getPushMode()
                    .isEnabled()) {
                UI.getCurrent().getPushConfiguration()
                        .setPushMode(PushMode.DISABLED);
                pushButton.setCaption("enable push mode");
            } else {
                UI.getCurrent().getPushConfiguration()
                        .setPushMode(PushMode.AUTOMATIC);
                pushButton.setCaption("disable push mode");
            }
        });

        addComponents(ul, button, pushButton, stateButton);
    }
}
