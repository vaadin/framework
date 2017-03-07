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
package com.vaadin.tests.urifragments;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SettingNullFragment extends AbstractReindeerTestUI {

    protected static final String BUTTON_FRAG_1_ID = "buttonFrag1";
    protected static final String BUTTON_NULL_FRAGMENT_ID = "buttonNullFragment";

    protected static final String FRAG_1_URI = "FRAG1";
    protected static final String NULL_FRAGMENT_URI = "";

    @Override
    protected void setup(VaadinRequest request) {
        Button button1 = new Button("Set Fragment");
        button1.setId(BUTTON_FRAG_1_ID);

        Button button2 = new Button("Set Null Fragment");
        button2.setId(BUTTON_NULL_FRAGMENT_ID);

        button1.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Page.getCurrent().setUriFragment(FRAG_1_URI);
            }
        });

        button2.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Page.getCurrent().setUriFragment(null);
            }
        });

        getLayout().addComponent(button1);
        getLayout().addComponent(button2);
    }

    @Override
    protected String getTestDescription() {
        return "Setting null as URI fragment should remove (clear) old fragment in the browser";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11312;
    }
}
