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
package com.vaadin.tests.components.twincolselect;

import java.util.LinkedHashMap;

import com.vaadin.tests.components.abstractlisting.AbstractMultiSelectTestUI;
import com.vaadin.ui.TwinColSelect;

public class TwinColSelectTestUI
        extends AbstractMultiSelectTestUI<TwinColSelect<Object>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Class<TwinColSelect<Object>> getTestClass() {
        return (Class) TwinColSelect.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createRows();
    }

    private void createRows() {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<>();
        options.put("0", 0);
        options.put("1", 1);
        options.put("2", 2);
        options.put("5", 5);
        options.put("10 (default)", 10);
        options.put("50", 50);

        createSelectAction("Rows", CATEGORY_STATE, options, "10 (default)",
                (c, value, data) -> c.setRows(value), null);
    }
}
