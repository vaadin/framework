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
package com.vaadin.client.extensions;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.FileDropTargetClientRpc;
import com.vaadin.shared.ui.dnd.FileDropTargetRpc;
import com.vaadin.shared.ui.dnd.FileDropTargetState;
import com.vaadin.shared.ui.dnd.FileParameters;
import com.vaadin.ui.dnd.FileDropTarget;

import elemental.events.Event;
import elemental.html.File;
import elemental.html.FileList;

/**
 * Extension to add file drop target functionality to a widget. It allows
 * dropping files onto the widget and uploading the dropped files to the server.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(FileDropTarget.class)
public class FileDropTargetConnector extends DropTargetExtensionConnector {

    /**
     * Contains files and their IDs that are waiting to be uploaded.
     */
    private Map<String, File> filesToUpload = new HashMap<>();

    /**
     * Contains file IDs and upload URLs.
     */
    private Map<String, String> uploadUrls = new HashMap<>();

    /**
     * Counting identifier for the files to be uploaded.
     */
    private int fileId = 0;

    /**
     * Indicates whether a file is being uploaded.
     */
    private boolean uploading = false;

    /**
     * Constructs file drop target connector.
     */
    public FileDropTargetConnector() {
        registerRpc(FileDropTargetClientRpc.class,
                (FileDropTargetClientRpc) urls -> {
                    uploadUrls.putAll(urls);
                    uploadNextFile();
                });
    }

    /**
     * Uploads a file from the waiting list in case there are no files being
     * uploaded.
     */
    private void uploadNextFile() {
        Scheduler.get().scheduleDeferred(() -> {
            if (!uploading && uploadUrls.size() > 0) {
                uploading = true;
                String nextId = uploadUrls.keySet().stream().findAny().get();

                String url = uploadUrls.remove(nextId);
                File file = filesToUpload.remove(nextId);

                FileUploadXHR xhr = (FileUploadXHR) FileUploadXHR.create();
                xhr.setOnReadyStateChange(xmlHttpRequest -> {
                    if (xmlHttpRequest.getReadyState() == XMLHttpRequest.DONE) {
                        // Poll server for changes
                        getRpcProxy(FileDropTargetRpc.class).poll();
                        uploading = false;
                        uploadNextFile();
                        xmlHttpRequest.clearOnReadyStateChange();
                    }
                });
                xhr.open("POST", getConnection().translateVaadinUri(url));
                xhr.postFile(file);
            }
        });
    }

    @Override
    protected void onDrop(Event event) {
        DataTransfer dataTransfer = ((NativeEvent) event).getDataTransfer();
        FileList files = getFiles(dataTransfer);

        if (files != null) {
            Map<String, FileParameters> fileParams = new HashMap<>();
            for (int i = 0; i < files.getLength(); i++) {
                File file = files.item(i);

                // Make sure the item is indeed a file and not a folder
                if (isFile(file, i, dataTransfer)) {
                    String id = String.valueOf(++this.fileId);

                    filesToUpload.put(id, file);
                    fileParams.put(id, new FileParameters(file.getName(),
                            (long) file.getSize(), file.getType()));
                }
            }

            // Request a list of upload URLs for the dropped files
            if (fileParams.size() > 0) {
                getRpcProxy(FileDropTargetRpc.class).drop(fileParams);
            }

            event.preventDefault();
            event.stopPropagation();
        }
    }

    @Override
    public FileDropTargetState getState() {
        return (FileDropTargetState) super.getState();
    }

    /**
     * Returns the files parameter of the dataTransfer object.
     *
     * @param dataTransfer
     *         DataTransfer object to retrieve files from.
     * @return {@code DataTransfer.files} parameter of the given dataTransfer
     * object.
     */
    private native FileList getFiles(DataTransfer dataTransfer)
    /*-{
        return dataTransfer.files;
    }-*/;

    /**
     * Checks whether the file on the given index is indeed a file or a folder.
     *
     * @param file
     *         File object to prove it is not a folder.
     * @param fileIndex
     *         Index of the file object.
     * @param dataTransfer
     *         DataTransfer object that contains the list of files.
     * @return {@code true} if the given file at the given index is not a
     * folder, {@code false} otherwise.
     */
    private native boolean isFile(File file, int fileIndex,
            DataTransfer dataTransfer)
    /*-{
        // Chrome >= v21 and Opera >= v?
        if (dataTransfer.items) {
            var item = dataTransfer.items[fileIndex];
            if (typeof item.webkitGetAsEntry == "function") {
                var entry = item.webkitGetAsEntry();
                if (typeof entry !== "undefined" && entry !== null) {
                    return entry.isFile;
                }
            }
        }

        // Zero sized files without a type are also likely to be folders
        if (file.size == 0 && !file.type) {
            return false;
        }

        // TODO Make it detect folders on all browsers

        return true;
    }-*/;

    /**
     * XHR that is used for uploading a file to the server.
     */
    private static class FileUploadXHR extends XMLHttpRequest {

        protected FileUploadXHR() {
        }

        public final native void postFile(File file) /*-{
            this.setRequestHeader('Content-Type', 'multipart/form-data');
            this.send(file);
        }-*/;

    }
}
