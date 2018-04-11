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
package com.vaadin.ui.dnd.event;

import java.lang.reflect.Method;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.dnd.DropTargetExtension;
import com.vaadin.util.ReflectTools;

/**
 * Interface to be implemented when creating a drop listener on a drop target
 * for HTML5 drag and drop.
 *
 * @param <T>
 *            Type of the drop target component.
 * @author Vaadin Ltd
 * @see DropTargetExtension#addDropListener(DropListener)
 * @since 8.1
 */
@FunctionalInterface
public interface DropListener<T extends AbstractComponent>
        extends ConnectorEventListener {
    static final Method DROP_METHOD = ReflectTools
            .getMethod(DropListener.class);

    /**
     * Called when drop event is fired.
     *
     * @param event
     *            Server side drop event.
     */
    void drop(DropEvent<T> event);
}
