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
 * Criterion that wraps another criterion and inverts its return value.
 * 
 * @since 6.3
 * 
 */
public class Not extends ClientSideCriterion {

    private static final long serialVersionUID = 1131422338558613244L;
    private AcceptCriterion acceptCriterion;

    public Not(ClientSideCriterion acceptCriterion) {
        this.acceptCriterion = acceptCriterion;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        acceptCriterion.paint(target);
    }

    @Override
    public boolean accept(DragAndDropEvent dragEvent) {
        return !acceptCriterion.accept(dragEvent);
    }

}
