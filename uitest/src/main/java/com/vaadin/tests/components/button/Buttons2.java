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
package com.vaadin.tests.components.button;

import java.util.LinkedHashMap;

import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.ui.themes.Reindeer;

public class Buttons2<T extends Button> extends AbstractComponentTest<T>
        implements ClickListener {

    private Command<T, Boolean> disableOnClickCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            c.setDisableOnClick(value);
        }
    };

    private Command<T, Boolean> clickListenerCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            if (value) {
                c.addClickListener(Buttons2.this);
            } else {
                c.removeClickListener(Buttons2.this);
            }

        }
    };

    @SuppressWarnings("unchecked")
    @Override
    protected Class<T> getTestClass() {
        return (Class<T>) Button.class;
    }

    @Override
    protected void createActions() {
        super.createActions();

        createBooleanAction("Disable on click", CATEGORY_FEATURES, false,
                disableOnClickCommand);
        addClickListener(CATEGORY_LISTENERS);
    }

    @Override
    protected void createComponentStyleNames(
            LinkedHashMap<String, String> options) {
        options.put("Reindeer default", Reindeer.BUTTON_DEFAULT);
        options.put("Reindeer small", Reindeer.BUTTON_SMALL);
        options.put("Reindeer link", Reindeer.BUTTON_LINK);
    }

    private void addClickListener(String category) {
        createBooleanAction("Click listener", category, false,
                clickListenerCommand);

    }

    @Override
    public void buttonClick(ClickEvent event) {
        log(event.getClass().getSimpleName());
    }
}
