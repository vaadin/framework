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
package com.vaadin.ui.components.grid;

import com.vaadin.ui.Grid;

/**
 * Represents the header section of a Grid.
 */
public abstract class Header extends StaticSection<Header.Row> {

    public class Row extends StaticSection.StaticRow<Row.Cell>
            implements Grid.HeaderRow {

        public class Cell extends StaticSection.StaticCell implements
                Grid.HeaderCell {
            protected Cell() {
                super(Row.this);
            }
        }

        /**
         * @param section
         */
        protected Row() {
            super(Header.this);
        }

        @Override
        protected Cell createCell() {
            return new Cell();
        }

        @Override
        protected String getCellTagName() {
            return "th";
        }
    }

    @Override
    public Row createRow() {
        return new Row();
    }
}
