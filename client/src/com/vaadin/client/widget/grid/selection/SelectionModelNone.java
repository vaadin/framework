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
package com.vaadin.client.widget.grid.selection;

import java.util.Collection;
import java.util.Collections;

import com.vaadin.client.data.DataSource.RowHandle;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widgets.Grid;

/**
 * No-row selection model.
 * 
 * @author Vaadin Ltd
 * @since 7.4
 */
public class SelectionModelNone<T> extends AbstractRowHandleSelectionModel<T>
        implements SelectionModel.None<T> {

    @Override
    public boolean isSelected(T row) {
        return false;
    }

    @Override
    public Renderer<Boolean> getSelectionColumnRenderer() {
        return null;
    }

    @Override
    public void setGrid(Grid<T> grid) {
        // noop
    }

    @Override
    public void reset() {
        // noop
    }

    @Override
    public Collection<T> getSelectedRows() {
        return Collections.emptySet();
    }

    @Override
    protected boolean selectByHandle(RowHandle<T> handle)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This selection model "
                + "does not support selection");
    }

    @Override
    protected boolean deselectByHandle(RowHandle<T> handle)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This selection model "
                + "does not support deselection");
    }

}
