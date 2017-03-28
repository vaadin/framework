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
import com.vaadin.shared.ui.dnd.DropEffect;
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

    private Registration dragStartListenerHandle;
    private Registration dragEndListenerHandle;

    /**
     * Stores the server side drag data that is available for the drop target if
     * it is in the same UI.
     */
    private Object dragData;

    /**
     * Extends {@code target} component and makes it a drag source.
     *
     * @param target
     *         Component to be extended.
     */
    public DragSourceExtension(T target) {

        registerDragSourceRpc(target);

        super.extend(target);

        initListeners();
    }

    /**
     * Initializes the event listeners this drag source is using.
     */
    protected void initListeners() {

        // Set current extension as active drag source in the UI
        dragStartListenerHandle = addDragStartListener(
                event -> getUI().setActiveDragSource(this));

        // Remove current extension as active drag source from the UI
        dragEndListenerHandle = addDragEndListener(
                event -> getUI().setActiveDragSource(null));
    }

    /**
     * Register server RPC.
     *
     * @param target
     *         Extended component.
     */
    protected void registerDragSourceRpc(T target) {
        registerRpc(new DragSourceRpc() {
            @Override
            public void dragStart() {
                DragStartEvent<T> event = new DragStartEvent<>(target,
                        getState(false).effectAllowed);
                fireEvent(event);
            }

            @Override
            public void dragEnd(DropEffect dropEffect) {
                DragEndEvent<T> event = new DragEndEvent<>(target, dropEffect);
                fireEvent(event);
            }
        });
    }

    @Override
    public void remove() {
        super.remove();

        // Remove listeners attached on construction
        dragStartListenerHandle.remove();
        dragEndListenerHandle.remove();
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
     * Set server side drag data. This data is available in the drop event and
     * can be used to transfer data between drag source and drop target if they
     * are in the same UI.
     *
     * @param data
     *         Data to transfer to drop event.
     */
    public void setDragData(Object data) {
        dragData = data;
    }

    /**
     * Get server side drag data. This data is available in the drop event and
     * can be used to transfer data between drag source and drop target if they
     * are in the same UI.
     *
     * @return Server side drag data if set, otherwise {@literal null}.
     */
    public Object getDragData() {
        return dragData;
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
