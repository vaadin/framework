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
package com.vaadin.tests.actions;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class ActionsOnInvisibleComponents extends AbstractTestUIWithLog {

    private static final long serialVersionUID = -5993467736906948993L;

    @Override
    protected void setup(VaadinRequest request) {
        getContent().setId("test-root");
        log("'A' triggers a click on an invisible button");
        log("'B' triggers a click on a disabled button");
        log("'C' triggers a click on a visible and enabled button");

        Button invisibleButton = new Button("Invisible button with shortcut");
        invisibleButton.setClickShortcut(KeyCode.A);
        invisibleButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log("Click event for invisible button");
            }
        });

        invisibleButton.setVisible(false);
        addComponent(invisibleButton);

        Button disabledButton = new Button("Disabled button with shortcut");
        disabledButton.setClickShortcut(KeyCode.B);
        disabledButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log("Click event for disabled button");
            }
        });

        disabledButton.setEnabled(false);
        addComponent(disabledButton);

        Button enabledButton = new Button("Enabled button with shortcut");
        enabledButton.setClickShortcut(KeyCode.C);
        enabledButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log("Click event for enabled button");
            }
        });

        addComponent(enabledButton);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Test to ensure actions are not performed on disabled/invisible components";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 12743;
    }

}
