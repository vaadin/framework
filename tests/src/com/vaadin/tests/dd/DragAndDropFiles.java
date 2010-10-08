package com.vaadin.tests.dd;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.output.NullOutputStream;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.Receiver;
import com.vaadin.terminal.ReceiverOwner.ReceivingEndedEvent;
import com.vaadin.terminal.ReceiverOwner.ReceivingFailedEvent;
import com.vaadin.terminal.ReceiverOwner.ReceivingProgressedEvent;
import com.vaadin.terminal.ReceiverOwner.ReceivingStartedEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Html5File.Html5FileUploadListener;
import com.vaadin.ui.Label;

public class DragAndDropFiles extends TestBase {

    @Override
    protected void setup() {
        CssLayout cssLayout = new CssLayout() {
            @Override
            protected String getCss(Component c) {
                return "display: block; padding:20px; border: 2px dotted black; background: #aaa;";
            }
        };
        Component l = new Label("Drag file on me");
        l.setSizeUndefined();
        cssLayout.addComponent(l);
        DragAndDropWrapper dragAndDropWrapper = new DragAndDropWrapper(
                cssLayout);
        dragAndDropWrapper.setSizeUndefined();
        dragAndDropWrapper.setDropHandler(new DropHandler() {

            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            public void drop(DragAndDropEvent event) {
                WrapperTransferable transferable = (WrapperTransferable) event
                        .getTransferable();
                Html5File[] files = transferable.getFiles();
                if (files != null) {

                    for (int i = 0; i < files.length; i++) {
                        Html5File file = files[i];
                        // Max 1 MB files are uploaded
                        if (file.getFileSize() > 1024 * 1024) {
                            getMainWindow()
                                    .showNotification(
                                            "File "
                                                    + file.getFileName()
                                                    + " was too large, not transferred to the server side.");
                            continue;
                        }

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        Receiver receiver = new Receiver() {
                            public OutputStream receiveUpload(String filename,
                                    String MIMEType) {
                                System.err.println("receiveUpload " + filename);

                                return new NullOutputStream();
                            }
                        };
                        file.setReceiver(receiver);
                        Html5FileUploadListener listener = new Html5FileUploadListener() {

                            public void uploadStarted(
                                    ReceivingStartedEvent event) {
                                getMainWindow().showNotification(
                                        "Started uploading "
                                                + event.getFileName());

                            }

                            public void uploadFinished(ReceivingEndedEvent event) {
                                getMainWindow().showNotification(
                                        "Finished uploading "
                                                + event.getFileName());
                            }

                            public void uploadFailed(ReceivingFailedEvent event) {
                                getMainWindow().showNotification(
                                        "Failed uploading "
                                                + event.getFileName());

                            }

                            public void onProgress(
                                    ReceivingProgressedEvent event) {
                                System.err.println("Progress"
                                        + event.getBytesReceived());
                            }
                        };
                        file.setUploadListener(listener);
                    }
                }

            }
        });

        addComponent(dragAndDropWrapper);
    }

    /*
     * TODO implement 'handbrake' for testing, progresss listener, interrupting.
     */
    @Override
    protected String getDescription() {
        return "Should work. Over 1 MB files will not be posted. TODO implement 'handbrake' for testing, progresss listener, interrupting.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
