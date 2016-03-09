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
 * @since 7.4
 * @author Vaadin Ltd
 */
public class GridStaticSectionState implements Serializable {

    public static class CellState implements Serializable {
        public String text = "";

        public String html = "";

        public Connector connector = null;

        public GridStaticCellType type = GridStaticCellType.TEXT;

        public String columnId;

        public String styleName = null;
    }

    public static class RowState implements Serializable {
        public List<CellState> cells = new ArrayList<CellState>();

        public boolean defaultRow = false;

        /**
         * Map from column id set to cell state for merged state.
         */
        public Map<Set<String>, CellState> cellGroups = new HashMap<Set<String>, CellState>();

        /**
         * The style name for the row. Null if none.
         */
        public String styleName = null;
    }

    public List<RowState> rows = new ArrayList<RowState>();

    public boolean visible = true;
}
