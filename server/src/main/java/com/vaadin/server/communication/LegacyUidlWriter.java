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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.LegacyPaint;
import com.vaadin.server.PaintTarget;
import com.vaadin.ui.Component;
import com.vaadin.ui.LegacyComponent;
import com.vaadin.ui.UI;

/**
 * Serializes legacy UIDL changes to JSON.
 *
 * @author Vaadin Ltd
 * @since 7.1
 */
public class LegacyUidlWriter implements Serializable {

    /**
     * Writes a JSON array containing the changes of all dirty
     * {@link LegacyComponent}s in the given UI.
     *
     * @param ui
     *            The {@link UI} whose legacy changes to write
     * @param writer
     *            The {@link Writer} to write the JSON with
     * @param target
     *            The {@link PaintTarget} to use
     * @throws IOException
     *             If the serialization fails.
     */
    public void write(UI ui, Writer writer, PaintTarget target)
            throws IOException {

        Collection<ClientConnector> dirtyVisibleConnectors = ui
                .getConnectorTracker().getDirtyVisibleConnectors();

        List<Component> legacyComponents = new ArrayList<Component>(
                dirtyVisibleConnectors.size());
        for (ClientConnector connector : dirtyVisibleConnectors) {
            // All Components that want to use paintContent must implement
            // LegacyComponent
            if (connector instanceof LegacyComponent) {
                legacyComponents.add((Component) connector);
            }
        }
        sortByHierarchy(legacyComponents);

        writer.write("[");
        for (Component c : legacyComponents) {
            getLogger().fine(
                    "Painting LegacyComponent " + c.getClass().getName() + "@"
                            + Integer.toHexString(c.hashCode()));
            target.startTag("change");
            final String pid = c.getConnectorId();
            target.addAttribute("pid", pid);
            LegacyPaint.paint(c, target);
            target.endTag("change");
        }
        writer.write("]");
    }

    private void sortByHierarchy(List<Component> paintables) {
        // Vaadin 6 requires parents to be painted before children as component
        // containers rely on that their updateFromUIDL method has been called
        // before children start calling e.g. updateCaption
        Collections.sort(paintables, new Comparator<Component>() {
            @Override
            public int compare(Component c1, Component c2) {
                int depth1 = 0;
                while (c1.getParent() != null) {
                    depth1++;
                    c1 = c1.getParent();
                }
                int depth2 = 0;
                while (c2.getParent() != null) {
                    depth2++;
                    c2 = c2.getParent();
                }
                if (depth1 < depth2) {
                    return -1;
                }
                if (depth1 > depth2) {
                    return 1;
                }
                return 0;
            }
        });
    }

    private static final Logger getLogger() {
        return Logger.getLogger(LegacyUidlWriter.class.getName());
    }
}
