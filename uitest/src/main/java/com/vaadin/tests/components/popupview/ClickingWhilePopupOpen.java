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
package com.vaadin.tests.components.popupview;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;

public class ClickingWhilePopupOpen extends TestBase {

    @Override
    protected void setup() {
        Label popup = new Label("Popup");
        popup.setSizeUndefined();
        addComponent(new PopupView("Click here to open the popup", popup));
    }

    @Override
    protected String getDescription() {
        return "Clicking the popup view when the popup is already open throws a client-side IllegalStateException";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8786;
    }

}
