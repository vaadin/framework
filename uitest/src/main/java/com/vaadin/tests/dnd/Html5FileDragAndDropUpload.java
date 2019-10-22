package com.vaadin.tests.dnd;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.dnd.FileParameters;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.GridDropTarget;
import com.vaadin.ui.dnd.FileDropTarget;

public class Html5FileDragAndDropUpload extends AbstractTestUIWithLog {

    private static final int FILE_SIZE_LIMIT = 1024 * 1024 * 5; // 5 MB

    private final boolean pushManually;

    public Html5FileDragAndDropUpload() {
        this(false);
    }

    public Html5FileDragAndDropUpload(boolean pushManually) {
        this.pushManually = pushManually;
    }

    @Override
    protected void setup(VaadinRequest request) {

        Grid<FileParameters> grid = new Grid<>(
                "Drop files or text on the Grid");
        grid.addColumn(FileParameters::getName).setCaption("File name");
        grid.addColumn(FileParameters::getSize).setCaption("File size");
        grid.addColumn(FileParameters::getMime).setCaption("Mime type");

        List<FileParameters> gridItems = new ArrayList<>();
        AtomicBoolean cancelled = new AtomicBoolean(false);

        ProgressBar progressBar = new ProgressBar(0.0f);

        new FileDropTarget<Grid<FileParameters>>(grid,
                event -> event.getFiles().forEach(html5File -> {
                    if (html5File.getFileSize() > FILE_SIZE_LIMIT) {
                        Notification.show(html5File.getFileName()
                                + " is too large (max 5 MB)");
                        return;
                    }
                    UI.getCurrent().setPollInterval(200);

                    html5File.setStreamVariable(new StreamVariable() {
                        @Override
                        public OutputStream getOutputStream() {
                            return new OutputStream() {
                                @Override
                                public void write(int b) throws IOException {
                                    // NOP
                                }
                            };
                        }

                        @Override
                        public boolean listenProgress() {
                            return true;
                        }

                        @Override
                        public void onProgress(StreamingProgressEvent event) {
                            float progress = (float) event.getBytesReceived()
                                    / event.getContentLength();
                            progressBar.setValue(progress);
                            log("Progress, bytesReceived="
                                    + event.getBytesReceived());
                            pushIfManual();
                        }

                        @Override
                        public void streamingStarted(
                                StreamingStartEvent event) {
                            cancelled.set(false);
                            progressBar.setValue(0.0f);
                            log("Stream started, fileName="
                                    + event.getFileName());
                            pushIfManual();
                        }

                        @Override
                        public void streamingFinished(StreamingEndEvent event) {
                            progressBar.setValue(1.0f);
                            gridItems
                                    .add(new FileParameters(event.getFileName(),
                                            event.getContentLength(),
                                            event.getMimeType()));
                            grid.setItems(gridItems);

                            log("Stream finished, fileName="
                                    + event.getFileName());
                            pushIfManual();
                            UI.getCurrent().setPollInterval(-1);
                        }

                        @Override
                        public void streamingFailed(StreamingErrorEvent event) {
                            progressBar.setValue(0.0f);
                            log("Stream failed, fileName="
                                    + event.getFileName());
                            pushIfManual();
                            UI.getCurrent().setPollInterval(-1);
                        }

                        @Override
                        public boolean isInterrupted() {
                            return cancelled.get();
                        }
                    });
                }));

        GridDropTarget<FileParameters> dropTarget = new GridDropTarget<>(grid,
                DropMode.ON_TOP);
        dropTarget.addGridDropListener(event -> {
            log("dataTransferText=" + event.getDataTransferText());
            Notification.show(event.getDataTransferText());
        });

        Button cancelButton = new Button("Cancel",
                click -> cancelled.set(true));
        VerticalLayout layout = new VerticalLayout(grid, cancelButton,
                progressBar);

        addComponent(layout);
    }

    private void pushIfManual() {
        if (pushManually) {
            push();
        }
    }

    @Override
    protected String getTestDescription() {
        return "Drop files onto the Grid to upload them or text";
    }
}
