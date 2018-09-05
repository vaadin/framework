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
package com.vaadin.shared.ui.dnd;

import java.util.Map;

import com.vaadin.shared.communication.ServerRpc;

/**
 * RPC for requesting upload URLs for files dropped on the file drop target.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public interface FileDropTargetRpc extends ServerRpc {

    /**
     * Called when files are dropped onto the file drop target.
     *
     * @param fileParams
     *            Generated file IDs and file parameters of dropped files.
     */
    public void drop(Map<String, FileParameters> fileParams);

    /**
     * Called to poll the server for changes when the upload is complete.
     */
    public void poll();
}
