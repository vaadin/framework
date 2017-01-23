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
import com.vaadin.shared.ui.dnd.DragSourceState;

/**
 * Extension to add drag source functionality to a widget for using HTML5 drag
 * and drop.
 */
public class DragSourceExtension extends AbstractExtension {

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
        ALL("all");

        private final String value;

        EffectAllowed(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    public void extend(AbstractClientConnector target) {
        super.extend(target);
    }

    public void setEffectAllowed(EffectAllowed effect) {
        getState().effectAllowed = effect.getValue();
    }

    /**
     *
     * @param format
     * @param data
     */
    public void setData(String format, String data) {
        if (!getState().types.contains(format)) {
            getState().types.add(format);
        }
        getState().data.put(format, data);
    }

    public void clearData(String format) {
        getState().types.remove(format);
        getState().data.remove(format);
    }

    public void clearData() {
        getState().types.clear();
        getState().data.clear();
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
