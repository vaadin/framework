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

public class DragSourceExtension extends AbstractExtension {

    @Override
    public void extend(AbstractClientConnector target) {
        super.extend(target);
    }

    public void setEffectAllowed(DragEffect effect) {
        getState().effectAllowed = effect.getValue();
    }

    public void setData(String format, String data) {
        getState().data.put(format, data);
    }

    public void clearData(String format) {
        getState().data.remove(format);
    }

    public void clearData() {
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

    public enum DragEffect {
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

        private String value;

        DragEffect(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
