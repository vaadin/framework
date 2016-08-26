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
package com.vaadin.v7.client.widget.escalator.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired when the row height changed in the Escalator's header, body or
 * footer.
 *
 * @since 7.7
 * @author Vaadin Ltd
 */
public class RowHeightChangedEvent extends GwtEvent<RowHeightChangedHandler> {

    /**
     * Handler type.
     */
    public final static Type<RowHeightChangedHandler> TYPE = new Type<RowHeightChangedHandler>();

    public static final Type<RowHeightChangedHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<RowHeightChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RowHeightChangedHandler handler) {
        handler.onRowHeightChanged(this);
    }

}
