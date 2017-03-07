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

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

public class ButtonMouseDetails extends TestBase {

    private Label out = new Label("", ContentMode.PREFORMATTED);

    private int clickCounter = 1;

    private Button.ClickListener clickListener = new Button.ClickListener() {

        @Override
        public void buttonClick(ClickEvent event) {
            StringBuilder str = new StringBuilder(out.getValue().toString());
            str.append(clickCounter + ":\t");

            // Modifier keys
            str.append("ctrl=" + event.isCtrlKey() + ",\t");
            str.append("alt=" + event.isAltKey() + ",\t");
            str.append("meta=" + event.isMetaKey() + ",\t");
            str.append("shift=" + event.isShiftKey() + ",\t");

            // Coordinates
            str.append("X=" + event.getRelativeX() + ",\t");
            str.append("Y=" + event.getRelativeY() + ",\t");
            str.append("clientX=" + event.getClientX() + ",\t");
            str.append("clientY=" + event.getClientY());

            str.append("\n");

            out.setValue(str.toString());
            clickCounter++;
        }
    };

    @Override
    protected void setup() {

        getLayout().setSpacing(true);

        Button button = new Button("CLICK ME!", clickListener);
        addComponent(button);

        addComponent(out);
    }

    @Override
    protected String getDescription() {
        return "Clicking a button should returns some additional information about the click";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6605;
    }

}
