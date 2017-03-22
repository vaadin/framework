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

import java.util.Objects;

import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.dnd.DragSourceRpc;
import com.vaadin.shared.ui.dnd.DragSourceState;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.ui.AbstractComponent;

/**
 * Extension to make a component drag source for HTML5 drag and drop
 * functionality.
 *
 * @param <T>
 *         Type of the component to be extended.
 * @author Vaadin Ltd
 * @since 8.1
 */
public class DragSourceExtension<T extends AbstractComponent> extends
        AbstractExtension {

    /**
     * Extends {@code target} component and makes it a drag source.
     *
     * @param target
     *         Component to be extended.
     */
    public DragSourceExtension(T target) {
        registerRpc(new DragSourceRpc() {
            @Override
            public void dragStart() {
                DragStartEvent<T> event = new DragStartEvent<>(target,
                        getState(false).dataTransferText,
                        getState(false).effectAllowed);
                fireEvent(event);
            }

            @Override
            public void dragEnd() {
                DragEndEvent<T> event = new DragEndEvent<>(target,
                        getState(false).dataTransferText,
                        getState(false).effectAllowed);
                fireEvent(event);
            }
        });

        super.extend(target);
    }

    /**
     * Sets the allowed effects for the current drag source element. Used for
     * setting client side {@code DataTransfer.effectAllowed} parameter for the
     * drag event.
     * <p>
     * By default the value is {@link EffectAllowed#UNINITIALIZED} which is
     * equivalent to {@link EffectAllowed#ALL}.
     *
     * @param effect
     *         Effects to allow for this draggable element. Cannot be {@code
     *         null}.
     */
    public void setEffectAllowed(EffectAllowed effect) {
        if (effect == null) {
            throw new IllegalArgumentException("Allowed effect cannot be null");
        }
        if (!Objects.equals(getState(false).effectAllowed, effect)) {
            getState().effectAllowed = effect;
        }
    }

    /**
     * Returns the allowed effects for the current drag source element. Used to
     * set client side {@code DataTransfer.effectAllowed} parameter for the drag
     * event.
     *
     * @return Effects that are allowed for this draggable element.
     */
    public EffectAllowed getEffectAllowed() {
        return getState(false).effectAllowed;
    }

    /**
     * Sets data for this drag source element. The data is set for the client
     * side draggable element using the {@code DataTransfer.setData("text",
     * data)} method.
     *
     * @param data
     *         Data to be set for the client side draggable element.
     */
    public void setDataTransferText(String data) {
        getState().dataTransferText = data;
    }

    /**
     * Returns the data stored with type {@code "text"} in this drag source
     * element.
     *
     * @return Data of type {@code "text"} stored in this drag source element.
     */
    public String getDataTransferText() {
        return getState(false).dataTransferText;
    }

    /**
     * Clears data of type {@code "text"} in this drag source element.
     */
    public void clearDataTransferText() {
        getState().dataTransferText = null;
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
    public Registration addDragStartListener(DragStartListener<T> listener) {
        return addListener(DragSourceState.EVENT_DRAGSTART,
                DragStartEvent.class, listener,
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
    public Registration addDragEndListener(DragEndListener<T> listener) {
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

    /**
     * Returns the component this extension is attached to.
     *
     * @return Extended component.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getParent() {
        return (T) super.getParent();
    }
}
