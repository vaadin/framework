/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.v7.shared.ui.upload;

import com.vaadin.shared.communication.ServerRpc;

public interface UploadServerRpc extends ServerRpc {

    /**
     * Event sent when the file name of the upload component is changed.
     *
     * @param filename
     *            The filename
     */
    void change(String filename);

    /**
     * Called to poll the server to see if any changes have been made e.g. when
     * starting upload
     *
     * @since 7.6
     */
    void poll();

}
