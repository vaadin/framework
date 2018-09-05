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

import java.util.logging.Logger;

import com.vaadin.client.UIDL;
import com.vaadin.event.dd.acceptcriteria.Not;
import com.vaadin.shared.ui.dd.AcceptCriterion;
import com.vaadin.ui.dnd.DropTargetExtension;

/**
 *
 * @author Vaadin Ltd
 * @deprecated Replaced in 8.1 with
 *             {@link DropTargetExtension#setDropCriteria(String)}
 */
@Deprecated
@AcceptCriterion(Not.class)
public final class VNot extends VAcceptCriterion {
    private boolean b1;
    private VAcceptCriterion crit1;

    @Override
    public void accept(VDragEvent drag, UIDL configuration,
            VAcceptCallback callback) {
        if (crit1 == null) {
            crit1 = getCriteria(drag, configuration, 0);
            if (crit1 == null) {
                getLogger().info("Not criteria didn't found a child criteria");
                return;
            }
        }

        b1 = false;

        VAcceptCallback accept1cb = event -> b1 = true;

        crit1.accept(drag, configuration.getChildUIDL(0), accept1cb);
        if (!b1) {
            callback.accepted(drag);
        }
    }

    private VAcceptCriterion getCriteria(VDragEvent drag, UIDL configuration,
            int i) {
        UIDL childUIDL = configuration.getChildUIDL(i);
        return VAcceptCriteria.get(childUIDL.getStringAttribute("name"));
    }

    @Override
    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return false; // TODO enforce on server side
    }

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        return false; // not used
    }

    private static Logger getLogger() {
        return Logger.getLogger(VNot.class.getName());
    }
}
