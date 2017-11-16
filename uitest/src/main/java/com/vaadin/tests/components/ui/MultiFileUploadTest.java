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
package com.vaadin.tests.components.ui;

import java.io.IOException;
import java.io.OutputStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.ChangeListener;
import com.vaadin.ui.VerticalLayout;

public class MultiFileUploadTest extends AbstractTestUIWithLog {

    private ChangeListener changeListener = event -> {
        if (event.getFilename().isEmpty()) {
            removeUpload(event.getSource());
        } else {
            addUpload();
        }
    };
    private VerticalLayout uploadsLayout = new VerticalLayout();

    @Override
    protected void setup(VaadinRequest request) {
        getPage().getStyles()
                .add(".v-upload-hidden-button .v-button {display:none};");
        addUpload();
        addComponent(uploadsLayout);
        addComponent(new Button("Upload files", event -> {
            for (Upload u : getUploads()) {
                u.submitUpload();
            }
        }));
    }

    protected Iterable<Upload> getUploads() {
        return (Iterable) uploadsLayout;
    }

    protected void removeUpload(Upload source) {
        uploadsLayout.removeComponent(source);

    }

    protected void addUpload() {
        Upload upload = createUpload();
        upload.addSucceededListener(event -> {
            log("Upload of " + event.getFilename() + " complete");
            uploadsLayout.removeComponent(event.getUpload());
        });

        upload.addFailedListener(
                event -> log("Upload of " + event.getFilename() + " FAILED"));

        upload.setReceiver((filename, mimeType) -> {
            return new OutputStream() {
                @Override
                public void write(int arg0) throws IOException {

                }
            };
        });
        upload.setStyleName("hidden-button");
        uploadsLayout.addComponent(upload);

    }

    private Upload createUpload() {
        Upload upload = new Upload();
        upload.addChangeListener(changeListener);
        return upload;
    }

    @Override
    protected String getTestDescription() {
        return "Tests that an Upload change event can be used to create a multiple file upload component";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13222;
    }

}
