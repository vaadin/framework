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
package com.vaadin.shared.ui.dnd;

import java.io.Serializable;

/**
 * Contains parameters of a file. Used for transferring information about
 * dropped files from the client to the server.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public class FileParameters implements Serializable {
    private String name;
    private long size;
    private String mime;

    /**
     * Creates a file parameters object.
     */
    public FileParameters() {
    }

    /**
     * Creates a file parameters object.
     *
     * @param name
     *         Name of the file.
     * @param size
     *         Size of the file.
     * @param mime
     *         Mime type of the file.
     */
    public FileParameters(String name, long size, String mime) {
        this.name = name;
        this.size = size;
        this.mime = mime;
    }

    /**
     * Gets the file name.
     *
     * @return Name of the file.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the file name.
     *
     * @param name
     *         Name of the file.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the file size.
     *
     * @return Size of the file.
     */
    public long getSize() {
        return size;
    }

    /**
     * Sets the file size.
     *
     * @param size
     *         Size of the file.
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Gets the mime type.
     *
     * @return Mime type of the file.
     */
    public String getMime() {
        return mime;
    }

    /**
     * Sets the mime type.
     *
     * @param mime
     *         Mime type of the file.
     */
    public void setMime(String mime) {
        this.mime = mime;
    }
}
