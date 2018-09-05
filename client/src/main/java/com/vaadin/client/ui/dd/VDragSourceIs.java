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
package com.vaadin.client.ui.dd;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.UIDL;
import com.vaadin.event.dd.acceptcriteria.SourceIs;
import com.vaadin.shared.ui.dd.AcceptCriterion;
import com.vaadin.ui.dnd.DropTargetExtension;

/**
 *
 * @since 6.3
 * @author Vaadin Ltd
 * @deprecated Replaced in 8.1 with
 *             {@link DropTargetExtension#setDropCriteria(String)}
 */
@Deprecated
@AcceptCriterion(SourceIs.class)
public final class VDragSourceIs extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        try {
            ComponentConnector component = drag.getTransferable()
                    .getDragSource();
            int c = configuration.getIntAttribute("c");
            for (int i = 0; i < c; i++) {
                String requiredPid = configuration
                        .getStringAttribute("component" + i);
                VDropHandler currentDropHandler = VDragAndDropManager.get()
                        .getCurrentDropHandler();
                ComponentConnector paintable = (ComponentConnector) ConnectorMap
                        .get(currentDropHandler.getApplicationConnection())
                        .getConnector(requiredPid);
                if (paintable == component) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
}
