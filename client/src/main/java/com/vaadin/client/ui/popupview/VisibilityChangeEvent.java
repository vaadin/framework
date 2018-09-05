/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.client.ui.popupview;

import com.google.gwt.event.shared.GwtEvent;

public class VisibilityChangeEvent extends GwtEvent<VisibilityChangeHandler> {

    private static Type<VisibilityChangeHandler> type;

    private boolean visible;

    public VisibilityChangeEvent(final boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public Type<VisibilityChangeHandler> getAssociatedType() {
        return getType();
    }

    public static Type<VisibilityChangeHandler> getType() {
        if (type == null) {
            type = new Type<>();
        }
        return type;
    }

    @Override
    protected void dispatch(final VisibilityChangeHandler handler) {
        handler.onVisibilityChange(this);
    }
}
