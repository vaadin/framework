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
package com.vaadin.tests.components.popupview;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.PopupView;

public class ReopenPopupView extends AbstractTestUI {
    private final Log log = new Log(5);

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(log);
        addComponent(new PopupView("PopupView", new Button("Button",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        log.log("Button clicked");
                    }
                })));
    }

    @Override
    protected String getTestDescription() {
        return "Clicking a button in a PopupView should work every time";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8804);
    }

}
