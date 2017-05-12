/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.ui.dnd;

import java.io.Serializable;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.dnd.event.FileDropEvent;

/**
 * Handles the drop event on a file drop target.
 *
 * @param <T>
 *         Type of the file drop target component.
 * @author Vaadin Ltd
 * @see FileDropEvent
 * @see com.vaadin.ui.dnd.FileDropTarget
 * @since 8.1
 */
public interface FileDropHandler<T extends AbstractComponent> extends
        Serializable {

    /**
     * Handles the drop event. The method is called when files are dropped onto
     * the file drop target this handler is registered to.
     *
     * @param event
     *         The file drop event containing the list of files that were
     *         dropped onto the component.
     */
    public void drop(FileDropEvent<T> event);
}
