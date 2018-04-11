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

package com.vaadin.shared.ui.ui;

/**
 * Transport modes for Push.
 *
 * @since 7.1
 * @author Vaadin Ltd
 */
public enum Transport {
    /**
     * Websockets.
     */
    WEBSOCKET("websocket"),
    /**
     * Websockets for server to client, XHR for client to server.
     *
     * @since 7.6
     */
    WEBSOCKET_XHR("websocket-xhr"),
    /**
     * HTTP streaming.
     *
     * @deprecated Use the more reliable {@link Transport#LONG_POLLING} instead.
     */
    @Deprecated
    STREAMING("streaming"),
    /**
     * HTTP long polling.
     */
    LONG_POLLING("long-polling");

    private String identifier;

    private Transport(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns a Transport by its identifier. Returns null if no value is found
     * for the given identifier.
     *
     * @since 7.3.10
     */
    public static Transport getByIdentifier(String identifier) {
        for (Transport t : values()) {
            if (t.getIdentifier().equals(identifier)) {
                return t;
            }
        }
        return null;
    }
}
