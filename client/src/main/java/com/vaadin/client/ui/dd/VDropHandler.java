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
package com.vaadin.client.ui.dd;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;

/**
 * Vaadin Widgets that want to receive something via drag and drop implement
 * this interface.
 */
public interface VDropHandler {

    /**
     * Called by DragAndDropManager when a drag operation is in progress and the
     * cursor enters the area occupied by this Paintable.
     * 
     * @param dragEvent
     *            DragEvent which contains the transferable and other
     *            information for the operation
     */
    public void dragEnter(VDragEvent dragEvent);

    /**
     * Called by DragAndDropManager when a drag operation is in progress and the
     * cursor leaves the area occupied by this Paintable.
     * 
     * @param dragEvent
     *            DragEvent which contains the transferable and other
     *            information for the operation
     */
    public void dragLeave(VDragEvent dragEvent);

    /**
     * Called by DragAndDropManager when a drag operation was in progress and a
     * drop was performed on this Paintable.
     * 
     * 
     * @param dragEvent
     *            DragEvent which contains the transferable and other
     *            information for the operation
     * 
     * @return true if the Tranferrable of this drag event needs to be sent to
     *         the server, false if drop is rejected or no server side event
     *         should be sent
     */
    public boolean drop(VDragEvent drag);

    /**
     * When drag is over current drag handler.
     * 
     * With drag implementation by {@link VDragAndDropManager} will be called
     * when mouse is moved. HTML5 implementations call this continuously even
     * though mouse is not moved.
     * 
     * @param currentDrag
     */
    public void dragOver(VDragEvent currentDrag);

    /**
     * Returns the ComponentConnector with which this DropHandler is associated
     */
    public ComponentConnector getConnector();

    /**
     * Returns the application connection to which this {@link VDropHandler}
     * belongs to. DragAndDropManager uses this fucction to send Transferable to
     * server side.
     */
    public ApplicationConnection getApplicationConnection();

}
