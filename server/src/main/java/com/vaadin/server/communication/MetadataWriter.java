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

package com.vaadin.server.communication;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import com.vaadin.server.SystemMessages;
import com.vaadin.ui.UI;

/**
 * Serializes miscellaneous metadata to JSON.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class MetadataWriter implements Serializable {

    private int timeoutInterval = -1;

    /**
     * Writes a JSON object containing metadata related to the given UI.
     * 
     * @param ui
     *            The UI whose metadata to write.
     * @param writer
     *            The writer used.
     * @param repaintAll
     *            Whether the client should repaint everything.
     * @param analyzeLayouts
     *            Whether detected layout problems should be reported in client
     *            and server console.
     * @param async
     *            True if this message is sent by the server asynchronously,
     *            false if it is a response to a client message.
     * @param messages
     *            a {@link SystemMessages} containing client-side error
     *            messages.
     * @throws IOException
     *             If the serialization fails.
     * 
     */
    public void write(UI ui, Writer writer, boolean repaintAll, boolean async,
            SystemMessages messages) throws IOException {

        writer.write("{");

        boolean metaOpen = false;
        if (repaintAll) {
            metaOpen = true;
            writer.write("\"repaintAll\":true");
        }

        if (async) {
            if (metaOpen) {
                writer.write(", ");
            }
            metaOpen = true;
            writer.write("\"async\":true");
        }

        // meta instruction for client to enable auto-forward to
        // sessionExpiredURL after timer expires.
        if (messages != null && messages.getSessionExpiredMessage() == null
                && messages.getSessionExpiredCaption() == null
                && messages.isSessionExpiredNotificationEnabled()
                && ui.getSession().getSession() != null) {
            int newTimeoutInterval = ui.getSession().getSession()
                    .getMaxInactiveInterval();
            if (repaintAll || (timeoutInterval != newTimeoutInterval)) {
                String escapedURL = messages.getSessionExpiredURL() == null ? ""
                        : messages.getSessionExpiredURL().replace("/", "\\/");
                if (metaOpen) {
                    writer.write(",");
                }
                writer.write("\"timedRedirect\":{\"interval\":"
                        + (newTimeoutInterval + 15) + ",\"url\":\""
                        + escapedURL + "\"}");
                metaOpen = true;
            }
            timeoutInterval = newTimeoutInterval;
        }
        writer.write("}");
    }
}
