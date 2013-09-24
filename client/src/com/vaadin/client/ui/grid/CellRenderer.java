/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.client.ui.grid;

/**
 * An interface that defines how the cells in a {@link RowContainer} should look
 * like.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 * @see RowContainer#setCellRenderer(CellRenderer)
 */
public interface CellRenderer {
    /** A {@link CellRenderer} that doesn't render anything. */
    public static final CellRenderer NULL_RENDERER = new CellRenderer() {
        @Override
        public void renderCell(final Cell cell) {
        }
    };

    /**
     * Renders a cell contained in a row container.
     * 
     * @param cell
     *            the cell that can be manipulated to modify the contents of the
     *            cell being rendered. Never <code>null</code>.
     */
    public void renderCell(Cell cell);
}
