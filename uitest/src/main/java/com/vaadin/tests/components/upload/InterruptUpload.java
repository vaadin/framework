package com.vaadin.tests.components.upload;

import java.io.OutputStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window;

public class InterruptUpload extends AbstractTestUI {

    private Upload sample;
    private UploadInfoWindow uploadInfoWindow;

    @Override
    protected void setup(VaadinRequest request) {
        LineBreakCounter lineBreakCounter = new LineBreakCounter();
        lineBreakCounter.setSlow(true);

        sample = new Upload(null, lineBreakCounter);
        sample.setImmediate(true);
        sample.setButtonCaption("Upload File");

        uploadInfoWindow = new UploadInfoWindow(sample, lineBreakCounter);

        sample.addStartedListener(new StartedListener() {
            @Override
            public void uploadStarted(StartedEvent event) {
                if (uploadInfoWindow.getParent() == null) {
                    UI.getCurrent().addWindow(uploadInfoWindow);
                }
                uploadInfoWindow.setClosable(false);

            }
        });
        sample.addFinishedListener(new FinishedListener() {
            @Override
            public void uploadFinished(FinishedEvent event) {
                uploadInfoWindow.setClosable(true);
            }
        });

        addComponent(sample);
    }

    private static class UploadInfoWindow extends Window
            implements Upload.StartedListener, Upload.ProgressListener,
            Upload.FailedListener, Upload.SucceededListener,
            Upload.FinishedListener {
        private final Label state = new Label();
        private final Label result = new Label();
        private final Label fileName = new Label();
        private final Label textualProgress = new Label();

        private final ProgressBar progressBar = new ProgressBar();
        private final Button cancelButton;
        private final LineBreakCounter counter;

        private UploadInfoWindow(final Upload upload,
                final LineBreakCounter lineBreakCounter) {
            super("Status");
            counter = lineBreakCounter;

            addStyleName("upload-info");

            setResizable(false);
            setDraggable(false);

            final FormLayout uploadInfoLayout = new FormLayout();
            setContent(uploadInfoLayout);
            uploadInfoLayout.setMargin(true);

            final HorizontalLayout stateLayout = new HorizontalLayout();
            stateLayout.setSpacing(true);
            stateLayout.addComponent(state);

            cancelButton = new Button("Cancel");
            cancelButton.addClickListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    upload.interruptUpload();
                }
            });
            cancelButton.setVisible(false);
            cancelButton.setStyleName("small");
            stateLayout.addComponent(cancelButton);

            stateLayout.setCaption("Current state");
            state.setValue("Idle");
            uploadInfoLayout.addComponent(stateLayout);

            fileName.setCaption("File name");
            uploadInfoLayout.addComponent(fileName);

            result.setCaption("Line breaks counted");
            uploadInfoLayout.addComponent(result);

            progressBar.setCaption("Progress");
            progressBar.setVisible(false);
            uploadInfoLayout.addComponent(progressBar);

            textualProgress.setVisible(false);
            uploadInfoLayout.addComponent(textualProgress);

            upload.addStartedListener(this);
            upload.addProgressListener(this);
            upload.addFailedListener(this);
            upload.addSucceededListener(this);
            upload.addFinishedListener(this);

        }

        @Override
        public void uploadFinished(final FinishedEvent event) {
            state.setValue("Idle");
            progressBar.setVisible(false);
            textualProgress.setVisible(false);
            cancelButton.setVisible(false);
            UI.getCurrent().setPollInterval(-1);
        }

        @Override
        public void uploadStarted(final StartedEvent event) {
            // this method gets called immediately after upload is started
            progressBar.setValue(0f);
            progressBar.setVisible(true);
            UI.getCurrent().setPollInterval(500);
            textualProgress.setVisible(true);
            // updates to client
            state.setValue("Uploading");
            fileName.setValue(event.getFilename());

            cancelButton.setVisible(true);
        }

        @Override
        public void updateProgress(final long readBytes,
                final long contentLength) {
            // this method gets called several times during the update
            progressBar.setValue(readBytes / (float) contentLength);
            textualProgress.setValue(
                    "Processed " + readBytes + " bytes of " + contentLength);
            result.setValue(counter.getLineBreakCount() + " (counting...)");
        }

        @Override
        public void uploadSucceeded(final SucceededEvent event) {
            result.setValue(counter.getLineBreakCount() + " (total)");
        }

        @Override
        public void uploadFailed(final FailedEvent event) {
            result.setValue(
                    counter.getLineBreakCount() + " (counting interrupted at "
                            + Math.round(100 * progressBar.getValue()) + "%)");
        }
    }

    private static class LineBreakCounter implements Receiver {
        private int counter;
        private int total;
        private boolean sleep;

        /**
         * return an OutputStream that simply counts lineends
         */
        @Override
        public OutputStream receiveUpload(final String filename,
                final String MIMEType) {
            counter = 0;
            total = 0;
            return new OutputStream() {
                private static final int searchedByte = '\n';

                @Override
                public void write(final int b) {
                    total++;
                    if (b == searchedByte) {
                        counter++;
                    }
                    if (sleep && total % 1000 == 0) {
                        try {
                            Thread.sleep(100);
                        } catch (final InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }

        private int getLineBreakCount() {
            return counter;
        }

        private void setSlow(boolean value) {
            sleep = value;
        }
    }

    @Override
    protected Integer getTicketNumber() {
        return 9635;
    }

    @Override
    public String getDescription() {
        return "Interrupting an upload shouldn't prevent uploading that same file immediately afterwards.";
    }

}
