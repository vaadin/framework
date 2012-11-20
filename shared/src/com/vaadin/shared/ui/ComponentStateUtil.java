package com.vaadin.shared.ui;

import java.util.HashSet;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.communication.SharedState;

public final class ComponentStateUtil {

    private ComponentStateUtil() {
        // Util class is not instantiable
    }

    public static final boolean isUndefinedWidth(AbstractComponentState state) {
        return state.width == null || "".equals(state.width);
    }

    public static final boolean isUndefinedHeight(AbstractComponentState state) {
        return state.height == null || "".equals(state.height);
    }

    public static final boolean hasDescription(AbstractComponentState state) {
        return state.description != null && !"".equals(state.description);
    }

    public static final boolean hasStyles(AbstractComponentState state) {
        return state.styles != null && !state.styles.isEmpty();
    }

    public static final boolean isRelativeWidth(AbstractComponentState state) {
        return state.width != null && state.width.endsWith("%");
    }

    public static final boolean isRelativeHeight(AbstractComponentState state) {
        return state.height != null && state.height.endsWith("%");
    }

    /**
     * Removes an event listener id.
     * 
     * @param eventListenerId
     *            The event identifier to remove
     */
    public static final void removeRegisteredEventListener(SharedState state,
            String eventIdentifier) {
        if (state.registeredEventListeners == null) {
            return;
        }
        state.registeredEventListeners.remove(eventIdentifier);
        if (state.registeredEventListeners.size() == 0) {
            state.registeredEventListeners = null;
        }
    }

    /**
     * Adds an event listener id.
     * 
     * @param eventListenerId
     *            The event identifier to add
     */
    public static final void addRegisteredEventListener(SharedState state,
            String eventListenerId) {
        if (state.registeredEventListeners == null) {
            state.registeredEventListeners = new HashSet<String>();
        }
        state.registeredEventListeners.add(eventListenerId);
    }
}
