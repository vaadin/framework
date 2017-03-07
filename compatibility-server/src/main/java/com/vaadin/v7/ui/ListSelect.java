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

package com.vaadin.v7.ui;

import java.util.Collection;

import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.v7.data.Container;

/**
 * This is a simple list select without, for instance, support for new items,
 * lazyloading, and other advanced features.
 *
 * @deprecated As of 8.0 replaced by {@link com.vaadin.ui.ListSelect} based on
 *             the new data binding API
 */
@SuppressWarnings("serial")
@Deprecated
public class ListSelect extends AbstractSelect {

    private int rows = 0;

    public ListSelect() {
        super();
    }

    public ListSelect(String caption, Collection<?> options) {
        super(caption, options);
    }

    public ListSelect(String caption, Container dataSource) {
        super(caption, dataSource);
    }

    public ListSelect(String caption) {
        super(caption);
    }

    // Columns are no longer used for width.

    public int getRows() {
        return rows;
    }

    /**
     * Sets the number of rows in the editor. If the number of rows is set 0,
     * the actual number of displayed rows is determined implicitly by the
     * adapter.
     *
     * @param rows
     *            the number of rows to set.
     */
    public void setRows(int rows) {
        if (rows < 0) {
            rows = 0;
        }
        if (this.rows != rows) {
            this.rows = rows;
            markAsDirty();
        }
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        // Width is no longer based on columns
        // Adds the number of rows
        if (rows != 0) {
            target.addAttribute("rows", rows);
        }
        super.paintContent(target);
    }
}
