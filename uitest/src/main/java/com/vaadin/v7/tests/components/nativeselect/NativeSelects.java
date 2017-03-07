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
package com.vaadin.v7.tests.components.nativeselect;

import java.util.LinkedHashMap;

import com.vaadin.tests.components.select.AbstractSelectTestCase;
import com.vaadin.v7.ui.NativeSelect;

public class NativeSelects extends AbstractSelectTestCase<NativeSelect> {

    @Override
    protected Class<NativeSelect> getTestClass() {
        return NativeSelect.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createColumnSelectAction();
    }

    private void createColumnSelectAction() {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("-", 0);
        for (int i = 1; i <= 10; i++) {
            options.put(String.valueOf(i), i);
        }
        options.put("50", 50);
        options.put("100", 100);
        options.put("1000", 1000);

        super.createSelectAction("Columns", CATEGORY_DATA_SOURCE, options, "-",
                columnsAction);

    }

    private Command<NativeSelect, Integer> columnsAction = new Command<NativeSelect, Integer>() {

        @Override
        public void execute(NativeSelect c, Integer value, Object data) {
            c.setColumns(value);
        }
    };
}
