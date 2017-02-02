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
package com.vaadin.event.dnd;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.dnd.DragSourceRpc;
import com.vaadin.shared.ui.dnd.DragSourceState;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.ui.AbstractComponent;

/**
 * Extension to add drag source functionality to a widget for using HTML5 drag
 * and drop.
 */
public class DragSourceExtension extends AbstractExtension {

    /**
     * Constructor for {@link DragSourceExtension}
     */
    public DragSourceExtension() {
        registerRpc(new DragSourceRpc() {
            @Override
            public void dragStart() {
                DragStartEvent event = new DragStartEvent(
                        (AbstractComponent) getParent(), getState().types,
                        getState().data, getState().effectAllowed);
                fireEvent(event);
            }

            @Override
            public void dragEnd() {
                DragEndEvent event = new DragEndEvent(
                        (AbstractComponent) getParent(), getState().types,
                        getState().data, getState().effectAllowed);
                fireEvent(event);
            }
        });
    }

    @Override
    public void extend(AbstractClientConnector target) {
        super.extend(target);
    }

    /**
     * Sets the allowed effects for the current drag source element. Used to set
     * client side {@code DataTransfer.effectAllowed} parameter for the drag
     * event.
     *
     * @param effect
     *         Effects to allow for this draggable element.
     */
    public void setEffectAllowed(EffectAllowed effect) {
        getState().effectAllowed = effect;
    }

    /**
     * Sets the data for this drag source element. Used to set data for client
     * side drag element using {@code DataTransfer.setData()}. To be used as a
     * map, key-value pairs are stored. Order of entries are preserved.
     *
     * @param format
     *         data type to store, e.g. {@code text/plain} or {@code
     *         text/uri-list}.
     * @param data
     *         data to store for the data type.
     */
    public void setTransferData(String format, String data) {
        if (!getState().types.contains(format)) {
            getState().types.add(format);
        }
        getState().data.put(format, data);
    }

    /**
     * Clears data with the given type for this drag source element when
     * present.
     *
     * @param format
     *         type of data to be cleared.
     */
    public void clearTransferData(String format) {
        getState().types.remove(format);
        getState().data.remove(format);
    }

    /**
     * Clears all data for this drag source element.
     */
    public void clearTransferData() {
        getState().types.clear();
        getState().data.clear();
    }

    /**
     * Attaches dragstart listener for the current drag source. {@link
     * DragStartListener#dragStart(DragStartEvent)} is called when dragstart
     * event happens on the client side.
     *
     * @param listener
     *         Listener to handle dragstart event.
     * @return Handle to be used to remove this listener.
     */
    public Registration addDragStartListener(DragStartListener listener) {
        return addListener(DragStartEvent.class, listener,
                DragStartListener.DRAGSTART_METHOD);
    }

    /**
     * Attaches dragend listener for the current drag source. {@link
     * DragEndListener#dragEnd(DragEndEvent)} is called when dragend
     * event happens on the client side.
     *
     * @param listener
     *         Listener to handle dragend event.
     * @return Handle to be used to remove this listener.
     */
    public Registration addDragEndListener(DragEndListener listener) {
        return addListener(DragSourceState.EVENT_DRAGEND, DragEndEvent.class,
                listener, DragEndListener.DRAGEND_METHOD);
    }

    @Override
    protected DragSourceState getState() {
        return (DragSourceState) super.getState();
    }

    @Override
    protected DragSourceState getState(boolean markAsDirty) {
        return (DragSourceState) super.getState(markAsDirty);
    }
}
