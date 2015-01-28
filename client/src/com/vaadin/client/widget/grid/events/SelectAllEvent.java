/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.client.widget.grid.events;

import com.google.gwt.event.shared.GwtEvent;
import com.vaadin.client.widget.grid.selection.SelectionModel;

/**
 * A select all event, fired by the Grid when it needs all rows in data source
 * to be selected.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class SelectAllEvent<T> extends GwtEvent<SelectAllHandler<T>> {

    /**
     * Handler type.
     */
    private final static Type<SelectAllHandler<?>> TYPE = new Type<SelectAllHandler<?>>();;

    private SelectionModel.Multi<T> selectionModel;

    public SelectAllEvent(SelectionModel.Multi<T> selectionModel) {
        this.selectionModel = selectionModel;
    }

    public static final Type<SelectAllHandler<?>> getType() {
        return TYPE;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Type<SelectAllHandler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    @Override
    protected void dispatch(SelectAllHandler<T> handler) {
        handler.onSelectAll(this);
    }

    public SelectionModel.Multi<T> getSelectionModel() {
        return selectionModel;
    }
}
