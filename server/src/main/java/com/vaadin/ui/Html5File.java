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
package com.vaadin.ui;

import java.io.Serializable;

import com.vaadin.event.dd.DropHandler;
import com.vaadin.server.StreamVariable;

/**
 * {@link DragAndDropWrapper} can receive also files from client computer if
 * appropriate HTML 5 features are supported on client side. This class wraps
 * information about dragged file on server side.
 */
public class Html5File implements Serializable {

    private String name;
    private long size;
    private StreamVariable streamVariable;
    private String type;

    Html5File(String name, long size, String mimeType) {
        this.name = name;
        this.size = size;
        type = mimeType;
    }

    public String getFileName() {
        return name;
    }

    public long getFileSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    /**
     * Sets the {@link StreamVariable} that into which the file contents will be
     * written. Usage of StreamVariable is similar to {@link Upload} component.
     * <p>
     * If the {@link StreamVariable} is not set in the {@link DropHandler} the
     * file contents will not be sent to server.
     * <p>
     * <em>Note!</em> receiving file contents is experimental feature depending
     * on HTML 5 API's. It is supported only by modern web browsers like Firefox
     * 3.6 and above and recent webkit based browsers (Safari 5, Chrome 6) at
     * this time.
     * 
     * @param streamVariable
     *            the callback that returns stream where the implementation
     *            writes the file contents as it arrives.
     */
    public void setStreamVariable(StreamVariable streamVariable) {
        this.streamVariable = streamVariable;
    }

    public StreamVariable getStreamVariable() {
        return streamVariable;
    }

}
