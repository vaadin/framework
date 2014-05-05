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
/**
 * 
 */
package com.vaadin.client.ui.dd;

import com.vaadin.client.UIDL;
import com.vaadin.event.dd.acceptcriteria.And;
import com.vaadin.shared.ui.dd.AcceptCriterion;

@AcceptCriterion(And.class)
final public class VAnd extends VAcceptCriterion implements VAcceptCallback {
    private boolean b1;

    static VAcceptCriterion getCriteria(VDragEvent drag, UIDL configuration,
            int i) {
        UIDL childUIDL = configuration.getChildUIDL(i);
        return VAcceptCriteria.get(childUIDL.getStringAttribute("name"));
    }

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        int childCount = configuration.getChildCount();
        for (int i = 0; i < childCount; i++) {
            VAcceptCriterion crit = getCriteria(drag, configuration, i);
            b1 = false;
            crit.accept(drag, configuration.getChildUIDL(i), this);
            if (!b1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void accepted(VDragEvent event) {
        b1 = true;
    }

}
