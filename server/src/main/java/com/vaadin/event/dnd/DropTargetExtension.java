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
import java.util.Optional;

import com.vaadin.server.AbstractExtension;
import com.vaadin.server.ClientConnector;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.DropTargetRpc;
import com.vaadin.shared.ui.dnd.DropTargetState;
import com.vaadin.ui.AbstractComponent;

/**
 * Extension to add drop target functionality to a widget for using HTML5 drag
 * and drop.
 *
 * @param <T>
 *         Type of the component to be extended.
 */
public class DropTargetExtension<T extends AbstractComponent> extends
        AbstractExtension {

    /**
     * Extends {@code target} component and makes it a drop target.
     *
     * @param target
     *         Component to be extended.
     */
    public DropTargetExtension(T target) {

        registerDropTargetRpc(target);

        super.extend(target);
    }

    /**
     * Register server RPC.
     *
     * @param target
     *         Extended component.
     */
    protected void registerDropTargetRpc(T target) {
        registerRpc((DropTargetRpc) (types, data, dropEffect, dragSourceId) -> {
            DropEvent<T> event = new DropEvent<>(target, types, data,
                    dropEffect, getDragSource(dragSourceId).orElse(null));

            fireEvent(event);
        });
    }

    /**
     * Get drag source extension.
     *
     * @param dragSourceId
     *         Connector id of the extension.
     * @return Drag source extension if exists with the given connector id,
     * otherwise an empty optional.
     */
    protected Optional<DragSourceExtension<AbstractComponent>> getDragSource(
            String dragSourceId) {
        DragSourceExtension<AbstractComponent> dragSource = null;

        ClientConnector connector = getUI().getConnectorTracker()
                .getConnector(dragSourceId);
        if (connector != null && connector instanceof DragSourceExtension) {
            dragSource = (DragSourceExtension<AbstractComponent>) connector;
        }

        return Optional.ofNullable(dragSource);
    }

    /**
     * Sets the drop effect for the current drop target. Used for the client
     * side {@code DataTransfer.dropEffect} parameter.
     * <p>
     * Default value is browser dependent and can depend on e.g. modifier keys.
     *
     * @param dropEffect
     *         The drop effect to be set. Cannot be {@code null}.
     */
    public void setDropEffect(DropEffect dropEffect) {
        if (dropEffect == null) {
            throw new IllegalArgumentException("Drop effect cannot be null.");
        }

        if (!Objects.equals(getState(false).dropEffect, dropEffect)) {
            getState().dropEffect = dropEffect;
        }
    }

    /**
     * Returns the drop effect for the current drop target.
     *
     * @return The drop effect of this drop target.
     */
    public DropEffect getDropEffect() {
        return getState(false).dropEffect;
    }

    /**
     * Sets criteria to allow dragover event on the current drop target. The
     * script executes when dragover event happens and stops the event in case
     * the script returns {@code false}.
     * <p>
     * <b>IMPORTANT:</b> Construct the criteria script carefully and do not
     * include untrusted sources such as user input. Always keep in mind that
     * the script is executed on the client as is.
     * <p>
     * Example:
     * <pre>
     *     target.setDropCriteria(
     *         // If dragged source contains a URL, allow it to be dragged over
     *         "if (event.dataTransfer.types.includes('text/uri-list')) {" +
     *         "    return true;" +
     *         "}" +
     *
     *         // Otherwise cancel the event"
     *         "return false;");
     * </pre>
     *
     * @param criteriaScript
     *         JavaScript to be executed when dragover event happens or {@code
     *         null} to clear.
     */
    public void setDragOverCriteria(String criteriaScript) {
        if (!Objects.equals(getState(false).dragOverCriteria, criteriaScript)) {
            getState().dragOverCriteria = criteriaScript;
        }
    }

    /**
     * Returns the criteria for allowing dragover event on the current drop
     * target.
     *
     * @return JavaScript that executes when dragover event happens.
     */
    public String getDragOverCriteria() {
        return getState(false).dragOverCriteria;
    }

    /**
     * Sets criteria to allow drop event on the current drop target. The script
     * executes when drop event happens and stops the event in case the script
     * returns {@code false}.
     * <p>
     * <b>IMPORTANT:</b> Construct the criteria script carefully and do not
     * include untrusted sources such as user input. Always keep in mind that
     * the script is executed on the client as is.
     * <p>
     * Example:
     * <pre>
     *     target.setDropCriteria(
     *         // If dragged source contains a URL, allow it to be dropped
     *         "if (event.dataTransfer.types.includes('text/uri-list')) {" +
     *         "    return true;" +
     *         "}" +
     *
     *         // Otherwise cancel the event"
     *         "return false;");
     * </pre>
     *
     * @param criteriaScript
     *         JavaScript to be executed when drop event happens or {@code null}
     *         to clear.
     */
    public void setDropCriteria(String criteriaScript) {
        if (!Objects.equals(getState(false).dropCriteria, criteriaScript)) {
            getState().dropCriteria = criteriaScript;
        }
    }

    /**
     * Returns the criteria for allowing drop event on the current drop target.
     *
     * @return JavaScript that executes when drop event happens.
     */
    public String getDropCriteria() {
        return getState(false).dropCriteria;
    }

    /**
     * Attaches drop listener for the current drop target. {@link
     * DropListener#drop(DropEvent)} is called when drop event happens on the
     * client side.
     *
     * @param listener
     *         Listener to handle drop event.
     * @return Handle to be used to remove this listener.
     */
    public Registration addDropListener(DropListener<T> listener) {
        return addListener(DropEvent.class, listener, DropListener.DROP_METHOD);
    }

    @Override
    protected DropTargetState getState() {
        return (DropTargetState) super.getState();
    }

    @Override
    protected DropTargetState getState(boolean markAsDirty) {
        return (DropTargetState) super.getState(markAsDirty);
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
