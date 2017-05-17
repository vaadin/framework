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
package com.vaadin.ui.dnd.event;

import java.util.Map;
import java.util.Optional;

import com.vaadin.shared.ui.dnd.DragSourceState;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.dnd.DragSourceExtension;
import com.vaadin.ui.dnd.DropTargetExtension;

/**
 * Server side drop event. Fired when an HTML5 drop happens.
 *
 * @param <T>
 *            Type of the drop target component.
 * @author Vaadin Ltd
 * @see DropTargetExtension#addDropListener(DropListener)
 * @since 8.1
 */
public class DropEvent<T extends AbstractComponent> extends Component.Event {
    private final Map<String, String> data;
    private final DragSourceExtension<? extends AbstractComponent> dragSourceExtension;
    private final AbstractComponent dragSource;
    private final DropEffect dropEffect;

    /**
     * Creates a server side drop event.
     *
     * @param target
     *         Component that received the drop.
     * @param data
     *         Map containing all types and corresponding data from the {@code
     *         DataTransfer} object.
     * @param dropEffect
     *         the desired drop effect
     * @param dragSourceExtension
     *         Drag source extension of the component that initiated the drop
     *         event.
     */
    public DropEvent(T target, Map<String, String> data, DropEffect dropEffect,
            DragSourceExtension<? extends AbstractComponent> dragSourceExtension) {
        super(target);

        this.data = data;
        this.dropEffect = dropEffect;
        this.dragSourceExtension = dragSourceExtension;
        this.dragSource = Optional.ofNullable(dragSourceExtension)
                .map(DragSourceExtension::getParent).orElse(null);
    }

    /**
     * Get data from the {@code DataTransfer} object.
     *
     * @param type
     *         Data format, e.g. {@code text/plain} or {@code text/uri-list}.
     * @return Optional data for the given format if exists in the {@code
     * DataTransfer}, otherwise {@code Optional.empty()}.
     */
    public Optional<String> getDataTransferData(String type) {
        return Optional.ofNullable(data.get(type));
    }

    /**
     * Get data of any of the types {@code "text"}, {@code "Text"} or {@code
     * "text/plain"}.
     * <p>
     * IE 11 transfers data dropped from the desktop as {@code "Text"} while
     * most other browsers transfer textual data as {@code "text/plain"}.
     *
     * @return First existing data of types in order {@code "text"}, {@code
     * "Text"} or {@code "text/plain"}, or {@code null} if none of them exist.
     */
    public String getDataTransferText() {
        // Read data type "text"
        String text = data.get(DragSourceState.DATA_TYPE_TEXT);

        // IE stores data dragged from the desktop as "Text"
        if (text == null) {
            text = data.get(DragSourceState.DATA_TYPE_TEXT_IE);
        }

        // Browsers may store the key as "text/plain"
        if (text == null) {
            text = data.get(DragSourceState.DATA_TYPE_TEXT_PLAIN);
        }

        return text;
    }

    /**
     * Get all of the transfer data from the {@code DataTransfer} object. The
     * data can be iterated to find the most relevant data as it preserves the
     * order in which the data was set to the drag source element.
     *
     * @return Map of type/data pairs, containing all the data from the {@code
     * DataTransfer} object.
     */
    public Map<String, String> getDataTransferData() {
        return data;
    }

    /**
     * Get the desired dropEffect for the drop event.
     * <p>
     * <em>NOTE: Currently you cannot trust this to work on all browsers!
     * https://github.com/vaadin/framework/issues/9247 For Chrome & IE11 it is
     * never set and always returns {@link DropEffect#NONE} even though the drop
     * succeeded!</em>
     *
     * @return the drop effect
     */
    public DropEffect getDropEffect() {
        return dropEffect;
    }

    /**
     * Returns the drag source component if the drag originated from a component
     * in the same UI as the drop target component, or an empty optional.
     *
     * @return Drag source component or an empty optional.
     */
    public Optional<AbstractComponent> getDragSourceComponent() {
        return Optional.ofNullable(dragSource);
    }

    /**
     * Returns the extension of the drag source component if the drag originated
     * from a component in the same UI as the drop target component, or an empty
     * optional.
     *
     * @return Drag source extension or an empty optional
     */
    public Optional<DragSourceExtension<? extends AbstractComponent>> getDragSourceExtension() {
        return Optional.ofNullable(dragSourceExtension);
    }

    /**
     * Gets the server side drag data. This data can be set during the drag
     * start event on the server side and can be used to transfer data between
     * drag source and drop target when they are in the same UI.
     *
     * @return Optional server side drag data if set and the drag source and the
     *         drop target are in the same UI, otherwise empty {@code Optional}.
     * @see DragSourceExtension#setDragData(Object)
     */
    public Optional<Object> getDragData() {
        return getDragSourceExtension().map(DragSourceExtension::getDragData);
    }

    /**
     * Returns the drop target component where the drop event occurred.
     *
     * @return Component on which a drag source was dropped.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getComponent() {
        return (T) super.getComponent();
    }
}
