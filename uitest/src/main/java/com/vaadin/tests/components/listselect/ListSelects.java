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
package com.vaadin.tests.components.listselect;

import java.util.LinkedHashMap;

import com.vaadin.tests.components.select.AbstractSelectTestCase;
import com.vaadin.v7.ui.ListSelect;

public class ListSelects extends AbstractSelectTestCase<ListSelect> {

    private Command<ListSelect, Integer> colsCommand = new Command<ListSelect, Integer>() {
        @Override
        public void execute(ListSelect c, Integer value, Object data) {
            c.setColumns(value);
        }
    };

    private Command<ListSelect, Integer> rowsCommand = new Command<ListSelect, Integer>() {
        @Override
        public void execute(ListSelect c, Integer value, Object data) {
            c.setRows(value);
        }
    };

    @Override
    protected Class<ListSelect> getTestClass() {
        return ListSelect.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createRowsAction(CATEGORY_FEATURES);
        createColsAction(CATEGORY_FEATURES);
    }

    private void createRowsAction(String category) {
        LinkedHashMap<String, Integer> options = createIntegerOptions(20);
        createSelectAction("Rows", category, options, "0", rowsCommand);
    }

    private void createColsAction(String category) {
        LinkedHashMap<String, Integer> options = createIntegerOptions(20);
        createSelectAction("Columns", category, options, "0", colsCommand);
    }
}
