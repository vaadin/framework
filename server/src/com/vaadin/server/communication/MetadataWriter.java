/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.List;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.ComponentSizeValidator;
import com.vaadin.server.ComponentSizeValidator.InvalidLayout;
import com.vaadin.server.SystemMessages;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

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
     * @param hilightedConnector
     *            The connector that should be highlighted on the client or null
     *            if none.
     * @param messages
     *            a {@link SystemMessages} containing client-side error
     *            messages.
     * @throws IOException
     *             If the serialization fails.
     * 
     */
    public void write(UI ui, Writer writer, boolean repaintAll,
            boolean analyzeLayouts, ClientConnector hilightedConnector,
            SystemMessages messages) throws IOException {

        List<InvalidLayout> invalidComponentRelativeSizes = null;

        if (analyzeLayouts) {
            invalidComponentRelativeSizes = ComponentSizeValidator
                    .validateComponentRelativeSizes(ui.getContent(), null, null);

            // Also check any existing subwindows
            if (ui.getWindows() != null) {
                for (Window subWindow : ui.getWindows()) {
                    invalidComponentRelativeSizes = ComponentSizeValidator
                            .validateComponentRelativeSizes(
                                    subWindow.getContent(),
                                    invalidComponentRelativeSizes, null);
                }
            }
        }

        writer.write("{");

        boolean metaOpen = false;
        if (repaintAll) {
            metaOpen = true;
            writer.write("\"repaintAll\":true");
            if (analyzeLayouts) {
                writer.write(", \"invalidLayouts\":");
                writer.write("[");
                if (invalidComponentRelativeSizes != null) {
                    boolean first = true;
                    for (InvalidLayout invalidLayout : invalidComponentRelativeSizes) {
                        if (!first) {
                            writer.write(",");
                        } else {
                            first = false;
                        }
                        invalidLayout.reportErrors(new PrintWriter(writer),
                                System.err);
                    }
                }
                writer.write("]");
            }
            if (hilightedConnector != null) {
                writer.write(", \"hl\":\"");
                writer.write(hilightedConnector.getConnectorId());
                writer.write("\"");
            }
        }

        // meta instruction for client to enable auto-forward to
        // sessionExpiredURL after timer expires.
        if (messages != null && messages.getSessionExpiredMessage() == null
                && messages.getSessionExpiredCaption() == null
                && messages.isSessionExpiredNotificationEnabled()) {
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
