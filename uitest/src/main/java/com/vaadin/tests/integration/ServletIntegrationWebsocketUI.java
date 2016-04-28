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
package com.vaadin.tests.integration;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.shared.ui.ui.UIState.PushConfigurationState;

/**
 * Server test which uses websockets
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
@Push(transport = Transport.WEBSOCKET)
public class ServletIntegrationWebsocketUI extends ServletIntegrationUI {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.tests.integration.IntegrationTestUI#init(com.vaadin.server
     * .VaadinRequest)
     */
    @Override
    protected void init(VaadinRequest request) {
        super.init(request);
        // Ensure no fallback is used
        getPushConfiguration().setParameter(
                PushConfigurationState.FALLBACK_TRANSPORT_PARAM, "none");

    }

}
