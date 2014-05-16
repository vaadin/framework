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
package com.vaadin.event.dd;

import java.io.Serializable;

import com.vaadin.event.Transferable;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;

/**
 * DropHandlers contain the actual business logic for drag and drop operations.
 * <p>
 * The {@link #drop(DragAndDropEvent)} method is used to receive the transferred
 * data and the {@link #getAcceptCriterion()} method contains the (possibly
 * client side verifiable) criterion whether the dragged data will be handled at
 * all.
 * 
 * @since 6.3
 * 
 */
public interface DropHandler extends Serializable {

    /**
     * Drop method is called when the end user has finished the drag operation
     * on a {@link DropTarget} and {@link DragAndDropEvent} has passed
     * {@link AcceptCriterion} defined by {@link #getAcceptCriterion()} method.
     * The actual business logic of drag and drop operation is implemented into
     * this method.
     * 
     * @param event
     *            the event related to this drop
     */
    public void drop(DragAndDropEvent event);

    /**
     * Returns the {@link AcceptCriterion} used to evaluate whether the
     * {@link Transferable} will be handed over to
     * {@link DropHandler#drop(DragAndDropEvent)} method. If client side can't
     * verify the {@link AcceptCriterion}, the same criteria may be tested also
     * prior to actual drop - during the drag operation.
     * <p>
     * Based on information from {@link AcceptCriterion} components may display
     * some hints for the end user whether the drop will be accepted or not.
     * <p>
     * Vaadin contains a variety of criteria built in that can be composed to
     * more complex criterion. If the build in criteria are not enough,
     * developer can use a {@link ServerSideCriterion} or build own custom
     * criterion with client side counterpart.
     * <p>
     * If developer wants to handle everything in the
     * {@link #drop(DragAndDropEvent)} method, {@link AcceptAll} instance can be
     * returned.
     * 
     * @return the {@link AcceptCriterion}
     */
    public AcceptCriterion getAcceptCriterion();

}
