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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.JsonPaintTarget;
import com.vaadin.server.LegacyCommunicationManager;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.UI;

/**
 * Serializes resources to JSON. Currently only used for {@link CustomLayout}
 * templates.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class ResourceWriter implements Serializable {

    /**
     * Writes a JSON object containing registered resources.
     * 
     * @param ui
     *            The {@link UI} whose resources to write.
     * @param writer
     *            The {@link Writer} to use.
     * @param target
     *            The {@link JsonPaintTarget} containing the resources.
     * @throws IOException
     */
    public void write(UI ui, Writer writer, JsonPaintTarget target)
            throws IOException {

        // TODO PUSH Refactor so that this is not needed
        LegacyCommunicationManager manager = ui.getSession()
                .getCommunicationManager();

        // Precache custom layouts

        // TODO We should only precache the layouts that are not
        // cached already (plagiate from usedPaintableTypes)

        writer.write("{");
        int resourceIndex = 0;
        for (final Iterator<Object> i = target.getUsedResources().iterator(); i
                .hasNext();) {
            final String resource = (String) i.next();
            InputStream is = null;
            try {
                is = ui.getSession()
                        .getService()
                        .getThemeResourceAsStream(ui, manager.getTheme(ui),
                                resource);
            } catch (final Exception e) {
                // FIXME: Handle exception
                getLogger().log(Level.FINER,
                        "Failed to get theme resource stream.", e);
            }
            if (is != null) {

                writer.write((resourceIndex++ > 0 ? ", " : "") + "\""
                        + resource + "\" : ");
                final StringBuffer layout = new StringBuffer();

                try {
                    final InputStreamReader r = new InputStreamReader(is,
                            "UTF-8");
                    final char[] buffer = new char[20000];
                    int charsRead = 0;
                    while ((charsRead = r.read(buffer)) > 0) {
                        layout.append(buffer, 0, charsRead);
                    }
                    r.close();
                } catch (final java.io.IOException e) {
                    // FIXME: Handle exception
                    getLogger().log(Level.INFO, "Resource transfer failed", e);
                }
                writer.write("\""
                        + JsonPaintTarget.escapeJSON(layout.toString()) + "\"");
            } else {
                // FIXME: Handle exception
                getLogger().severe("CustomLayout not found: " + resource);
            }
        }
        writer.write("}");
    }

    private static final Logger getLogger() {
        return Logger.getLogger(ResourceWriter.class.getName());
    }
}
