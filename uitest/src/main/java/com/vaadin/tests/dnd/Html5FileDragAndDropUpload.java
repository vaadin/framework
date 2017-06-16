/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.dnd;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.dnd.FileParameters;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.GridDropTarget;
import com.vaadin.ui.dnd.FileDropTarget;

public class Html5FileDragAndDropUpload extends AbstractTestUIWithLog {

    private static final int FILE_SIZE_LIMIT = 1024 * 1024 * 5; // 5 MB

    @Override
    protected void setup(VaadinRequest request) {

        Grid<FileParameters> grid = new Grid<>("Drop files or text on the Grid");
        grid.addColumn(FileParameters::getName).setCaption("File name");
        grid.addColumn(FileParameters::getSize).setCaption("File size");
        grid.addColumn(FileParameters::getMime).setCaption("Mime type");

        List<FileParameters> gridItems = new ArrayList<>();

        new FileDropTarget<Grid<FileParameters>>(grid, event -> {
            event.getFiles().forEach(html5File -> {
                if (html5File.getFileSize() > FILE_SIZE_LIMIT) {
                    Notification.show(html5File.getFileName()
                            + " is too large (max 5 MB)");
                    return;
                }

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
                        log("Progress, bytesReceived=" + event
                                .getBytesReceived());
                    }

                    @Override
                    public void streamingStarted(StreamingStartEvent event) {
                        log("Stream started, fileName=" + event.getFileName());
                    }

                    @Override
                    public void streamingFinished(StreamingEndEvent event) {
                        gridItems.add(new FileParameters(event.getFileName(),
                                event.getContentLength(), event.getMimeType()));
                        grid.setItems(gridItems);

                        log("Stream finished, fileName=" + event.getFileName());
                    }

                    @Override
                    public void streamingFailed(StreamingErrorEvent event) {
                        log("Stream failed, fileName=" + event.getFileName());
                    }

                    @Override
                    public boolean isInterrupted() {
                        return false;
                    }
                });
            });
        });

        GridDropTarget<FileParameters> dropTarget = new GridDropTarget<>(grid,
                DropMode.ON_TOP);
        dropTarget.addGridDropListener(event -> {
            log("dataTransferText=" + event.getDataTransferText());
            Notification.show(event.getDataTransferText());
        });

        Layout layout = new VerticalLayout(grid);

        addComponent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "Drop files onto the Grid to upload them or text";
    }
}
