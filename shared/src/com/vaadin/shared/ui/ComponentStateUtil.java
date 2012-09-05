package com.vaadin.shared.ui;

import java.util.HashSet;

import com.vaadin.shared.ComponentState;

public final class ComponentStateUtil {

    private ComponentStateUtil() {
        // Util class is not instantiable
    }

    public static final boolean isUndefinedWidth(ComponentState state) {
        return state.width == null || "".equals(state.width);
    }

    public static final boolean isUndefinedHeight(ComponentState state) {
        return state.height == null || "".equals(state.height);
    }

    public static final boolean hasDescription(ComponentState state) {
        return state.description != null && !"".equals(state.description);
    }

    public static final boolean hasStyles(ComponentState state) {
        return state.styles != null && !state.styles.isEmpty();
    }

    /**
     * Removes an event listener id.
     * 
     * @param eventListenerId
     *            The event identifier to remove
     */
    public static final void removeRegisteredEventListener(
            ComponentState state, String eventIdentifier) {
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
    public static final void addRegisteredEventListener(ComponentState state,
            String eventListenerId) {
        if (state.registeredEventListeners == null) {
            state.registeredEventListeners = new HashSet<String>();
        }
        state.registeredEventListeners.add(eventListenerId);
    }
}
