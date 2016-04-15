/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.tests.components.draganddropwrapper;

import java.io.OutputStream;

import org.apache.commons.io.output.NullOutputStream;

import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Label;

public class DragAndDropBatchUpload extends AbstractTestUI {

    private int batchId = 0;
    private Label console = new Label("No activity detected yet",
            ContentMode.HTML);

    @Override
    protected void setup(VaadinRequest request) {
        final DragAndDropWrapper dndWrapper = new DragAndDropWrapper(
                new Button("Upload here by drag and dropping"));
        dndWrapper.setDropHandler(new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                Transferable transferable = event.getTransferable();
                String consoleString = "<b>Drop batch number " + (++batchId)
                        + "</b>";
                if (transferable instanceof WrapperTransferable) {
                    WrapperTransferable wTransferable = (WrapperTransferable) transferable;
                    Html5File[] files = wTransferable.getFiles();

                    if (files != null) {
                        consoleString += "<br>" + files.length + " file(s):";
                        for (Html5File file : files) {
                            consoleString += "<br>" + file.getFileName();
                            file.setStreamVariable(new StreamVariable() {

                                @Override
                                public void streamingStarted(
                                        StreamingStartEvent event) {
                                }

                                @Override
                                public void streamingFinished(
                                        StreamingEndEvent event) {
                                }

                                @Override
                                public void streamingFailed(
                                        StreamingErrorEvent event) {
                                }

                                @Override
                                public void onProgress(
                                        StreamingProgressEvent event) {
                                }

                                @Override
                                public boolean listenProgress() {
                                    return false;
                                }

                                @Override
                                public boolean isInterrupted() {
                                    return false;
                                }

                                @Override
                                public OutputStream getOutputStream() {
                                    return new NullOutputStream();
                                }
                            });
                        }
                    } else {
                        consoleString += "<br>No files detected...";
                    }
                    console.setValue(consoleString);
                } else {
                    console.setValue(consoleString
                            + "<br>Something else than files were dragged");
                }

            }
        });

        addComponent(dndWrapper);
        addComponent(console);
    }

    @Override
    protected String getTestDescription() {
        return "Starting to upload a new batch before the old one must not reuse receivers";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12330;
    }

}
