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

import com.vaadin.event.Action;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.v7.ui.TextField;

@SuppressWarnings("serial")
public class ActionsWithoutKeyCode extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        TextField tf = new TextField();
        tf.setWidth("100%");
        tf.setInputPrompt(
                "Enter text with å,ä or ä or press windows key while textfield is focused");
        addComponent(tf);

        addActionHandler(new Action.Handler() {

            private Action[] actions;
            {
                actions = new Action[] { new Action("test1") };
            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                return actions;
            }

            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {
                log("action " + action.getCaption() + " triggered by "
                        + sender.getClass().getSimpleName() + " on "
                        + target.getClass().getSimpleName());
            }
        });
    }

}
