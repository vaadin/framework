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
package com.vaadin.ui;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.event.dnd.DropTargetExtension;
import com.vaadin.event.dnd.FileDropEvent;
import com.vaadin.event.dnd.FileDropHandler;
import com.vaadin.server.ServletPortletHelper;
import com.vaadin.server.StreamVariable;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.ui.dnd.FileDropTargetClientRpc;
import com.vaadin.shared.ui.dnd.FileDropTargetRpc;
import com.vaadin.shared.ui.dnd.FileDropTargetState;

/**
 * Extension to add drop target functionality to a widget for accepting and
 * uploading files.
 *
 * @param <T>
 *         Type of the component to be extended.
 * @author Vaadin Ltd
 * @since 8.1
 */
public class FileDropTarget<T extends AbstractComponent> extends
        DropTargetExtension<T> {

    /**
     * Handles the file drop event.
     */
    private final FileDropHandler<T> fileDropHandler;

    /**
     * Extends {@code target} component and makes it a file drop target. A file
     * drop handler needs to be added to handle the file drop event.
     *
     * @param target
     *         Component to be extended.
     * @param fileDropHandler
     *         File drop handler that handles the file drop event.
     * @see FileDropEvent
     */
    public FileDropTarget(T target, FileDropHandler<T> fileDropHandler) {
        super(target);

        this.fileDropHandler = fileDropHandler;
    }

    @Override
    protected void registerDropTargetRpc(T target) {
        super.registerDropTargetRpc(target);

        registerRpc((FileDropTargetRpc) fileParams -> {

            List<Html5File> files = new ArrayList<>();
            Map<String, String> urls = new HashMap<>();

            fileParams.forEach((id, fileParameters) -> {
                Html5File html5File = new Html5File(fileParameters.getName(),
                        fileParameters.getSize(), fileParameters.getMime());
                String url = createUrl(html5File, id);

                files.add(html5File);
                urls.put(id, url);
            });

            getRpcProxy(FileDropTargetClientRpc.class).sendUploadUrl(urls);

            FileDropEvent<T> event = new FileDropEvent<>(target, files);
            fileDropHandler.drop(event);
        });
    }

    /**
     * Creates an upload URL for the given file and file ID.
     *
     * @param file
     *         File to be uploaded.
     * @param id
     *         Generated ID for the file.
     * @return Upload URL for uploading the file to the server.
     */
    private String createUrl(Html5File file, String id) {
        return getStreamVariableTargetUrl("rec-" + id,
                new FileReceiver(id, file));
    }

    private String getStreamVariableTargetUrl(String name,
            StreamVariable value) {
        String connectorId = getConnectorId();
        UI ui = getUI();
        int uiId = ui.getUIId();
        String key = uiId + "/" + connectorId + "/" + name;

        ConnectorTracker connectorTracker = ui.getConnectorTracker();
        connectorTracker.addStreamVariable(connectorId, name, value);
        String secKey = connectorTracker.getSeckey(value);

        return ApplicationConstants.APP_PROTOCOL_PREFIX
                + ServletPortletHelper.UPLOAD_URL_PREFIX + key + "/" + secKey;
    }

    @Override
    protected FileDropTargetState getState() {
        return (FileDropTargetState) super.getState();
    }


    @Override
    protected FileDropTargetState getState(boolean markAsDirty) {
        return (FileDropTargetState) super.getState(markAsDirty);
    }

    /**
     * Returns the component this extension is attached to.
     *
     * @return Extended component.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getParent() {
        return (T) super.getParent();
    }

    private class FileReceiver implements StreamVariable {

        private final String id;
        private Html5File file;

        public FileReceiver(String id, Html5File file) {
            this.id = id;
            this.file = file;
        }

        private boolean listenProgressOfUploadedFile;

        @Override
        public OutputStream getOutputStream() {
            if (file.getStreamVariable() == null) {
                return null;
            }
            return file.getStreamVariable().getOutputStream();
        }

        @Override
        public boolean listenProgress() {
            return file.getStreamVariable().listenProgress();
        }

        @Override
        public void onProgress(StreamingProgressEvent event) {
            file.getStreamVariable()
                    .onProgress(new ReceivingEventWrapper(event));
        }

        @Override
        public void streamingStarted(StreamingStartEvent event) {
            listenProgressOfUploadedFile = file.getStreamVariable() != null;
            if (listenProgressOfUploadedFile) {
                file.getStreamVariable()
                        .streamingStarted(new ReceivingEventWrapper(event));
            }
        }

        @Override
        public void streamingFinished(StreamingEndEvent event) {
            if (listenProgressOfUploadedFile) {
                file.getStreamVariable()
                        .streamingFinished(new ReceivingEventWrapper(event));
            }
        }

        @Override
        public void streamingFailed(final StreamingErrorEvent event) {
            if (listenProgressOfUploadedFile) {
                file.getStreamVariable()
                        .streamingFailed(new ReceivingEventWrapper(event));
            }
        }

        @Override
        public boolean isInterrupted() {
            return file.getStreamVariable().isInterrupted();
        }

        /*
         * With XHR2 file posts we can't provide as much information from the
         * terminal as with multipart request. This helper class wraps the
         * terminal event and provides the lacking information from the
         * FileParameters.
         */
        class ReceivingEventWrapper implements StreamingErrorEvent,
                StreamingEndEvent, StreamingStartEvent, StreamingProgressEvent {

            private final StreamingEvent wrappedEvent;

            ReceivingEventWrapper(StreamingEvent e) {
                wrappedEvent = e;
            }

            @Override
            public String getMimeType() {
                return file.getType();
            }

            @Override
            public String getFileName() {
                return file.getFileName();
            }

            @Override
            public long getContentLength() {
                return file.getFileSize();
            }

            public StreamVariable getReceiver() {
                return FileReceiver.this;
            }

            @Override
            public Exception getException() {
                if (wrappedEvent instanceof StreamingErrorEvent) {
                    return ((StreamingErrorEvent) wrappedEvent).getException();
                }
                return null;
            }

            @Override
            public long getBytesReceived() {
                return wrappedEvent.getBytesReceived();
            }

            /**
             * Calling this method has no effect. DD files are receive only once
             * anyway.
             */
            @Override
            public void disposeStreamVariable() {

            }
        }

    }
}
