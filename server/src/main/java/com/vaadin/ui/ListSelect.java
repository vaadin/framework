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

package com.vaadin.ui;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;

/**
 * This is a simple list select without, for instance, support for new items,
 * lazyloading, and other advanced features.
 */
@SuppressWarnings("serial")
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
        // Adds the number of rows
        if (rows != 0) {
            target.addAttribute("rows", rows);
        }
        super.paintContent(target);
    }
}
