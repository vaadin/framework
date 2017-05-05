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
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.DropTargetRpc;
import com.vaadin.shared.ui.dnd.DropTargetState;
import com.vaadin.ui.AbstractComponent;

/**
 * Extension to add drop target functionality to a widget for using HTML5 drag
 * and drop.
 *
 * @param <T>
 *            Type of the component to be extended.
 * @author Vaadin Ltd
 * @since 8.1
 */
public class DropTargetExtension<T extends AbstractComponent>
        extends AbstractExtension {

    /**
     * Extends {@code target} component and makes it a drop target.
     *
     * @param target
     *            Component to be extended.
     */
    public DropTargetExtension(T target) {

        registerDropTargetRpc(target);

        super.extend(target);
    }

    /**
     * Register server RPC.
     *
     * @param target
     *            Extended component.
     */
    protected void registerDropTargetRpc(T target) {
        registerRpc((DropTargetRpc) (dataTransferText, dropEffect) -> {
            DropEvent<T> event = new DropEvent<>(target, dataTransferText,
                    DropEffect.valueOf(dropEffect.toUpperCase()),
                    getUI().getActiveDragSource());

            fireEvent(event);
        });
    }

    /**
     * Sets the drop effect for the current drop target. This is set to the
     * dropEffect on {@code dragenter} and {@code dragover} events.
     * <p>
     * <em>NOTE: If the drop effect that doesn't match the dropEffect /
     * effectAllowed of the drag source, it DOES NOT prevent drop on IE and
     * Safari! For FireFox and Chrome the drop is prevented if there they don't
     * match.</em>
     * <p>
     * Default value is browser dependent and can depend on e.g. modifier keys.
     * <p>
     * From Moz Foundation: "You can modify the dropEffect property during the
     * dragenter or dragover events, if for example, a particular drop target
     * only supports certain operations. You can modify the dropEffect property
     * to override the user effect, and enforce a specific drop operation to
     * occur. Note that this effect must be one listed within the effectAllowed
     * property. Otherwise, it will be set to an alternate value that is
     * allowed."
     *
     * @param dropEffect
     *            the drop effect to be set or {@code null} to not modify
     */
    public void setDropEffect(DropEffect dropEffect) {
        if (!Objects.equals(getState(false).dropEffect, dropEffect)) {
            getState().dropEffect = dropEffect;
        }
    }

    /**
     * Returns the drop effect for the current drop target.
     *
     * @return The drop effect of this drop target or {@code null} if none set
     * @see #setDropEffect(DropEffect)
     */
    public DropEffect getDropEffect() {
        return getState(false).dropEffect;
    }

    /**
     * Sets criteria to allow drop on this drop target. The script executes when
     * something is dragged on top of the target, and the drop is not allowed in
     * case the script returns {@code false}.
     * <p>
     * <b>IMPORTANT:</b> Construct the criteria script carefully and do not
     * include untrusted sources such as user input. Always keep in mind that
     * the script is executed on the client as is.
     * <p>
     * Example:
     *
     * <pre>
     * target.setDropCriteria(
     *         // If dragged source contains a URL, allow it to be dropped
     *         "if (event.dataTransfer.types.includes('text/uri-list')) {"
     *                 + "    return true;" + "}" +
     *
     *                 // Otherwise cancel the event"
     *                 "return false;");
     * </pre>
     *
     * @param criteriaScript
     *            JavaScript to be executed when drop event happens or
     *            {@code null} to clear.
     */
    public void setDropCriteria(String criteriaScript) {
        if (!Objects.equals(getState(false).dropCriteria, criteriaScript)) {
            getState().dropCriteria = criteriaScript;
        }
    }

    /**
     * Gets the criteria script that determines whether a drop is allowed. If
     * the script returns {@code false}, then it is determined the drop is not
     * allowed.
     *
     * @return JavaScript that executes when drop event happens.
     * @see #setDropCriteria(String)
     */
    public String getDropCriteria() {
        return getState(false).dropCriteria;
    }

    /**
     * Attaches drop listener for the current drop target.
     * {@link DropListener#drop(DropEvent)} is called when drop event happens on
     * the client side.
     *
     * @param listener
     *            Listener to handle drop event.
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
