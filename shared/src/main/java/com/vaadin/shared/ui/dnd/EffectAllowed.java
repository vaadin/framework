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
package com.vaadin.shared.ui.dnd;

/**
 * Used to specify the effect that is allowed for a drag operation.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public enum EffectAllowed {
    /**
     * The item may not be dropped.
     */
    NONE("none"),

    /**
     * A copy of the source item may be made at the new location.
     */
    COPY("copy"),

    /**
     * An item may be moved to a new location.
     */
    MOVE("move"),

    /**
     * A link may be established to the source at the new location.
     */
    LINK("link"),

    /**
     * A copy or move operation is permitted.
     */
    COPY_MOVE("copyMove"),

    /**
     * A copy or link operation is permitted.
     */
    COPY_LINK("copyLink"),

    /**
     * A link or move operation is permitted.
     */
    LINK_MOVE("linkMove"),

    /**
     * All operations are permitted.
     */
    ALL("all"),

    /**
     * Default state, equivalent to ALL
     */
    UNINITIALIZED("uninitialized");

    private final String value;

    EffectAllowed(String value) {
        this.value = value;
    }

    /**
     * Get the string value that is accepted by the client side drag event.
     *
     * @return String value accepted by the client side drag event.
     */
    public String getValue() {
        return value;
    }
}
