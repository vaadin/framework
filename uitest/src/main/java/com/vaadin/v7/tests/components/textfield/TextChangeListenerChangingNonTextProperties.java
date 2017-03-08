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
package com.vaadin.v7.tests.components.textfield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.v7.event.FieldEvents.TextChangeEvent;
import com.vaadin.v7.event.FieldEvents.TextChangeListener;
import com.vaadin.v7.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.v7.ui.TextField;

public class TextChangeListenerChangingNonTextProperties extends TestBase {

    int index = 0;
    String[] styles = { "red", "green", "blue", "cyan", "magenta" };

    private String getNextStyle() {
        return styles[++index % styles.length];
    }

    @Override
    protected void setup() {
        final TextField tf2 = new TextField("Updates width") {
            @Override
            public void attach() {
                super.attach();
                TestUtils.injectCSS(getUI(), ".red { background:red;} "
                        + ".green { background:green;} .blue { background:blue;} .cyan { background:cyan;} .magenta { background:magenta;}");
            }
        };
        tf2.setTextChangeEventMode(TextChangeEventMode.EAGER);
        tf2.addTextChangeListener(new TextChangeListener() {
            @Override
            public void textChange(TextChangeEvent event) {
                tf2.setStyleName(getNextStyle());
            }

        });
        tf2.setImmediate(true);

        addComponent(tf2);
    }

    @Override
    protected String getDescription() {
        return "The color (style name) of field changes on each text change event. This should not disturb typing.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(6588);
    }

}
