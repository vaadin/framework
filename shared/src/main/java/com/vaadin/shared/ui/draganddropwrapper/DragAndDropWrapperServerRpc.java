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
package com.vaadin.shared.ui.draganddropwrapper;

import com.vaadin.shared.communication.ServerRpc;

/**
 * RPC interface for calls from client to server.
 * 
 * @since 7.6.4
 * @author Vaadin Ltd
 */
public interface DragAndDropWrapperServerRpc extends ServerRpc {

    /**
     * Called to poll the server to see if any changes have been made e.g. when
     * the upload is complete.
     */
    public void poll();

}
