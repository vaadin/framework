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
package com.vaadin.v7.tests.components.textarea;

import java.util.LinkedHashMap;

import com.vaadin.v7.tests.components.textfield.AbstractTextFieldTest;
import com.vaadin.v7.ui.TextArea;

public class TextAreaTest extends AbstractTextFieldTest<TextArea> {

    private Command<TextArea, Boolean> wordwrapCommand = new Command<TextArea, Boolean>() {
        @Override
        public void execute(TextArea c, Boolean value, Object data) {
            c.setWordwrap(value);
        }
    };

    private Command<TextArea, Integer> rowsCommand = new Command<TextArea, Integer>() {
        @Override
        public void execute(TextArea c, Integer value, Object data) {
            c.setRows(value);
        }
    };

    @Override
    protected Class<TextArea> getTestClass() {
        return TextArea.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createWordwrapAction(CATEGORY_FEATURES);
        createRowsAction(CATEGORY_FEATURES);
    }

    private void createRowsAction(String category) {
        LinkedHashMap<String, Integer> options = createIntegerOptions(20);
        createSelectAction("Rows", category, options, "3", rowsCommand);
    }

    private void createWordwrapAction(String category) {
        createBooleanAction("Wordwrap", category, false, wordwrapCommand);
    }

}
