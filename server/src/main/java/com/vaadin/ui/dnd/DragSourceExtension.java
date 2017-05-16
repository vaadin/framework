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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Resource;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.dnd.DragSourceRpc;
import com.vaadin.shared.ui.dnd.DragSourceState;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.dnd.event.DragEndEvent;
import com.vaadin.ui.dnd.event.DragEndListener;
import com.vaadin.ui.dnd.event.DragStartEvent;
import com.vaadin.ui.dnd.event.DragStartListener;

/**
 * Extension to make a component drag source for HTML5 drag and drop
 * functionality.
 *
 * @param <T>
 *            Type of the component to be extended.
 * @author Vaadin Ltd
 * @since 8.1
 */
public class DragSourceExtension<T extends AbstractComponent>
        extends AbstractExtension {

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
     *            Component to be extended.
     */
    public DragSourceExtension(T target) {
        super.extend(target);

        initListeners();
    }

    /**
     * Initializes dragstart and -end event listeners for this drag source to
     * capture the active drag source for the UI.
     */
    private void initListeners() {
        // Set current extension as active drag source in the UI
        dragStartListenerHandle = addDragStartListener(
                event -> getUI().setActiveDragSource(this));

        // Remove current extension as active drag source from the UI
        dragEndListenerHandle = addDragEndListener(
                event -> getUI().setActiveDragSource(null));
    }

    @Override
    public void attach() {
        super.attach();

        registerDragSourceRpc();
    }

    /**
     * Registers the server side RPC methods invoked from client side on
     * <code>dragstart</code> and <code>dragend</code> events.
     * <p>
     * Override this method if you have custom RPC interface for transmitting
     * those events with more data. If just need to do additional things before
     * firing the events, then you should override {@link #onDragStart()} and
     * {@link #onDragEnd(DropEffect)} instead.
     */
    protected void registerDragSourceRpc() {
        registerRpc(new DragSourceRpc() {
            @Override
            public void dragStart() {
                onDragStart();
            }

            @Override
            public void dragEnd(DropEffect dropEffect) {
                onDragEnd(dropEffect);
            }
        });
    }

    /**
     * Method invoked when a <code>dragstart</code> has been sent from client
     * side. Fires the {@link DragStartEvent}.
     */
    protected void onDragStart() {
        DragStartEvent<T> event = new DragStartEvent<>(getParent(),
                getState(false).effectAllowed);
        fireEvent(event);
    }

    /**
     * Method invoked when a <code>dragend</code> has been sent from client
     * side. Fires the {@link DragEndEvent}.
     *
     * @param dropEffect
     *            the drop effect on the dragend
     */
    protected void onDragEnd(DropEffect dropEffect) {
        DragEndEvent<T> event = new DragEndEvent<>(getParent(), dropEffect);
        fireEvent(event);
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
     *            Effects to allow for this draggable element. Cannot be {@code
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
     * <p>
     * You can use different types of data to support dragging to different
     * targets. Accepted types depend on the drop target and those can be
     * platform specific. See https://developer.mozilla.org/en-US/docs/Web/API/HTML_Drag_and_Drop_API/Recommended_drag_types
     * for examples on different types.
     * <p>
     * <em>NOTE: IE11 only supports type ' text', which can be set using {@link
     * #setDataTransferText(String data)}</em>
     *
     * @return Effects that are allowed for this draggable element.
     */
    public EffectAllowed getEffectAllowed() {
        return getState(false).effectAllowed;
    }

    /**
     * Sets data for this drag source element with the given type. The data is
     * set for the client side draggable element using {@code
     * DataTransfer.setData(type, data)} method.
     * <p>
     * Note that {@code "text"} is the only cross browser supported data type.
     * Use {@link #setDataTransferText(String)} method instead if your
     * application supports IE11.
     *
     * @param type
     *         Type of the data to be set for the client side draggable element,
     *         e.g. {@code text/plain}. Cannot be {@code null}.
     * @param data
     *         Data to be set for the client side draggable element. Cannot be
     *         {@code null}.
     */
    public void setDataTransferData(String type, String data) {
        if (type == null) {
            throw new IllegalArgumentException("Data type cannot be null");
        }

        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }

        if (!getState(false).types.contains(type)) {
            getState().types.add(type);
        }
        getState().data.put(type, data);
    }

    /**
     * Returns the data stored with type {@code type} in this drag source
     * element.
     *
     * @param type
     *         Type of the requested data, e.g. {@code text/plain}.
     * @return Data of type {@code type} stored in this drag source element.
     */
    public String getDataTransferData(String type) {
        return getState(false).data.get(type);
    }

    /**
     * Returns the map of data stored in this drag source element. The returned
     * map preserves the order of storage and is unmodifiable.
     *
     * @return Unmodifiable copy of the map of data in the order the data was
     * stored.
     */
    public Map<String, String> getDataTransferData() {
        Map<String, String> data = getState(false).data;

        // Create a map of data that preserves the order of types
        LinkedHashMap<String, String> orderedData = new LinkedHashMap<>(
                data.size());
        getState(false).types
                .forEach(type -> orderedData.put(type, data.get(type)));

        return Collections.unmodifiableMap(orderedData);
    }

    /**
     * Sets data of type {@code "text"} for this drag source element. The data
     * is set for the client side draggable element using the {@code
     * DataTransfer.setData("text", data)} method.
     * <p>
     * Note that {@code "text"} is the only cross browser supported data type.
     * Use this method if your application supports IE11.
     *
     * @param data
     *         Data to be set for the client side draggable element.
     * @see #setDataTransferData(String, String)
     */
    public void setDataTransferText(String data) {
        setDataTransferData(DragSourceState.DATA_TYPE_TEXT, data);
    }

    /**
     * Returns the data stored with type {@code "text"} in this drag source
     * element.
     *
     * @return Data of type {@code "text"} stored in this drag source element.
     */
    public String getDataTransferText() {
        return getDataTransferData(DragSourceState.DATA_TYPE_TEXT);
    }

    /**
     * Clears data with the given type for this drag source element when
     * present.
     *
     * @param type
     *         Type of data to be cleared. Cannot be {@code null}.
     */
    public void clearDataTransferData(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Data type cannot be null");
        }

        getState().types.remove(type);
        getState().data.remove(type);
    }

    /**
     * Clears all data for this drag source element.
     */
    public void clearDataTransferData() {
        getState().types.clear();
        getState().data.clear();
    }

    /**
     * Set server side drag data. This data is available in the drop event and
     * can be used to transfer data between drag source and drop target if they
     * are in the same UI.
     *
     * @param data
     *            Data to transfer to drop event.
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
     * Attaches dragstart listener for the current drag source.
     * {@link DragStartListener#dragStart(DragStartEvent)} is called when
     * dragstart event happens on the client side.
     *
     * @param listener
     *            Listener to handle dragstart event.
     * @return Handle to be used to remove this listener.
     */
    public Registration addDragStartListener(DragStartListener<T> listener) {
        return addListener(DragSourceState.EVENT_DRAGSTART,
                DragStartEvent.class, listener,
                DragStartListener.DRAGSTART_METHOD);
    }

    /**
     * Attaches dragend listener for the current drag source.
     * {@link DragEndListener#dragEnd(DragEndEvent)} is called when dragend
     * event happens on the client side.
     *
     * @param listener
     *            Listener to handle dragend event.
     * @return Handle to be used to remove this listener.
     */
    public Registration addDragEndListener(DragEndListener<T> listener) {
        return addListener(DragSourceState.EVENT_DRAGEND, DragEndEvent.class,
                listener, DragEndListener.DRAGEND_METHOD);
    }

    /**
     * Set a custom drag image for the current drag source.
     *
     * @param imageResource
     *            Resource of the image to be displayed as drag image.
     */
    public void setDragImage(Resource imageResource) {
        setResource(DragSourceState.RESOURCE_DRAG_IMAGE, imageResource);
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
