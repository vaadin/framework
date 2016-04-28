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
package com.vaadin.event.dd.acceptcriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;

/**
 * A compound criterion that accepts the drag if all of its criteria accepts the
 * drag.
 * 
 * @see Or
 * 
 * @since 6.3
 * 
 */
public class And extends ClientSideCriterion {

    private static final long serialVersionUID = -5242574480825471748L;
    protected ClientSideCriterion[] criteria;

    /**
     * 
     * @param criteria
     *            criteria of which the And criterion will be composed
     */
    public And(ClientSideCriterion... criteria) {
        this.criteria = criteria;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        for (ClientSideCriterion crit : criteria) {
            crit.paint(target);
        }
    }

    @Override
    public boolean accept(DragAndDropEvent dragEvent) {
        for (ClientSideCriterion crit : criteria) {
            if (!crit.accept(dragEvent)) {
                return false;
            }
        }
        return true;
    }

}
