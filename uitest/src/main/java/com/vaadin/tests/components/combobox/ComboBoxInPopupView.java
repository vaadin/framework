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
package com.vaadin.tests.components.combobox;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.PopupVisibilityEvent;
import com.vaadin.ui.PopupView.PopupVisibilityListener;
import com.vaadin.v7.ui.ComboBox;

public class ComboBoxInPopupView extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return 2508;
    }

    @Override
    protected String getDescription() {
        return "Testcase for ComboBox in PopupView. Make the window narrower than the popup: the focused (2) one wraps button to second row AND seems narrower than (1), the unfocused one (1) works as expected.";
    }

    @Override
    protected void setup() {
        final ComboBox cb1 = new ComboBox();
        cb1.setWidth("260px");
        // cb.focus();
        PopupView pv1 = new PopupView("<u>1. expected (click)</u>", cb1);
        getLayout().addComponent(pv1);

        final ComboBox cb2 = new ComboBox();
        cb2.setWidth("260px");
        PopupView pv2 = new PopupView("<u>2. focused (click)</u>", cb2);
        pv2.addPopupVisibilityListener(new PopupVisibilityListener() {

            @Override
            public void popupVisibilityChange(PopupVisibilityEvent event) {
                cb2.focus();
            }
        });
        getLayout().addComponent(pv2);

    }

}
