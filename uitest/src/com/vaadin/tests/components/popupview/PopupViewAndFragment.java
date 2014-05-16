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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;

public class PopupViewAndFragment extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final PopupView pw = new PopupView("Open", new Label("Oh, hi"));
        addComponent(pw);

        final Button button = new Button("Open and change fragment",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        pw.setPopupVisible(true);
                        getPage().setUriFragment(
                                String.valueOf(System.currentTimeMillis()));
                    }
                });
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Changing frament should not automatically close PopupView";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10530;
    }

}
