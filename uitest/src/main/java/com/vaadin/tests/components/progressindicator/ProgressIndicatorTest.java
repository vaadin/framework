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
package com.vaadin.tests.components.progressindicator;

import java.util.LinkedHashMap;

import com.vaadin.tests.components.abstractfield.AbstractFieldTest;
import com.vaadin.ui.ProgressIndicator;

public class ProgressIndicatorTest extends AbstractFieldTest<ProgressIndicator> {
    ProgressIndicator progress = new ProgressIndicator();
    Command<ProgressIndicator, Float> setValueCommand = new Command<ProgressIndicator, Float>() {

        @Override
        public void execute(ProgressIndicator c, Float value, Object data) {
            c.setValue(value);
        }
    };
    private Command<ProgressIndicator, Integer> setPollingIntervalCommand = new Command<ProgressIndicator, Integer>() {
        @Override
        public void execute(ProgressIndicator c, Integer value, Object data) {
            c.setPollingInterval(value);
        }
    };

    private Command<ProgressIndicator, Boolean> setIndeterminateCommand = new Command<ProgressIndicator, Boolean>() {
        @Override
        public void execute(ProgressIndicator c, Boolean value, Object data) {
            c.setIndeterminate(value);
        }
    };

    @Override
    protected void createActions() {
        super.createActions();
        createSetValueAction();
        createPollingIntervalAction();
        createIndeterminateToggle();
    }

    private void createIndeterminateToggle() {
        createBooleanAction("Indeterminate", CATEGORY_FEATURES, false,
                setIndeterminateCommand);

    }

    private void createPollingIntervalAction() {
        LinkedHashMap<String, Integer> valueOptions = new LinkedHashMap<String, Integer>();
        for (int i = 100; i <= 3000; i += 200) {
            valueOptions.put(String.valueOf(i), i);
        }
        createSelectAction("Polling interval", CATEGORY_FEATURES, valueOptions,
                "1500", setPollingIntervalCommand);

    }

    private void createSetValueAction() {
        LinkedHashMap<String, Float> valueOptions = new LinkedHashMap<String, Float>();
        for (float f = 0.0f; f <= 1.0f; f += 0.1) {
            valueOptions.put(String.valueOf(f), f);
        }
        createSelectAction("Value", CATEGORY_FEATURES, valueOptions, "0.0",
                setValueCommand);

    }

    @Override
    protected Class<ProgressIndicator> getTestClass() {
        return ProgressIndicator.class;
    }

}
