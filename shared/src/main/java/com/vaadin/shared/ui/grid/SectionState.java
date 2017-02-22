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
package com.vaadin.shared.ui.grid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.shared.Connector;

/**
 * Shared state for Grid headers and footers.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class SectionState implements Serializable {

    /** The state of a header or footer row. */
    public static class RowState implements Serializable {

        /** The map from column ids to the cells in this row. */
        public Map<String, CellState> cells = new HashMap<>();

        /** The map from a joint cell to column id sets in this row. */
        public Map<CellState, Set<String>> cellGroups = new HashMap<>();

        /**
         * Whether this row is the default header row. Always false for footer
         * rows.
         */
        public boolean defaultHeader = false;

        /**
         * The style name for the row. Null if none.
         */
        public String styleName = null;
    }

    /** The state of a header or footer cell. */
    public static class CellState implements Serializable {

        public GridStaticCellType type = GridStaticCellType.TEXT;

        /** The style name for this cell. Null if none. */
        public String styleName = null;

        /** The textual caption of this cell. */
        public String text;

        /** The html content of this cell. */
        public String html;

        /**
         * The connector for the component that is set to be displayed in this
         * cell. Null if none.
         */
        public Connector connector = null;

        /** The id of the column that this cell belongs to. */
        public String columnId;
    }

    /** The rows in this section. */
    public List<RowState> rows = new ArrayList<>();
}
