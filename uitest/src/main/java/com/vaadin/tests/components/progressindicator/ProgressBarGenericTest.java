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
package com.vaadin.tests.components.progressindicator;

import java.util.LinkedHashMap;

import com.vaadin.tests.components.abstractfield.LegacyAbstractFieldTest;
import com.vaadin.v7.ui.ProgressBar;

public class ProgressBarGenericTest
        extends LegacyAbstractFieldTest<ProgressBar> {

    private Command<ProgressBar, Boolean> indeterminate = new Command<ProgressBar, Boolean>() {

        @Override
        public void execute(ProgressBar c, Boolean value, Object data) {
            c.setIndeterminate(value);
        }
    };

    @Override
    protected Class<ProgressBar> getTestClass() {
        return ProgressBar.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createBooleanAction("Indeterminate", CATEGORY_FEATURES, false,
                indeterminate, null);
        createValueSelection(CATEGORY_FEATURES);
        createPrimaryStyleNameSelect();
    }

    /**
     * @since
     */
    protected void createPrimaryStyleNameSelect() {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        String primaryStyle = getComponent().getPrimaryStyleName();
        options.put(primaryStyle, primaryStyle);
        options.put(primaryStyle + "-foo", primaryStyle + "-foo");
        options.put("foo", "foo");
        createSelectAction("Primary style name", CATEGORY_DECORATIONS, options,
                primaryStyle, primaryStyleNameCommand);

    }

    private void createValueSelection(String categorySelection) {
        LinkedHashMap<String, Object> options = new LinkedHashMap<>();
        options.put("null", null);
        for (float f = 0; f <= 1; f += 0.1) {
            options.put("" + f, f);
        }
        createSelectAction("Value", categorySelection, options, null,
                setValueCommand);
    }
}
