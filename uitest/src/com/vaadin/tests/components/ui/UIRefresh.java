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

package com.vaadin.tests.components.ui;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

@PreserveOnRefresh
public class UIRefresh extends AbstractTestUI {

    public static final String REINIT_ID = "reinit";

    @Override
    protected void setup(VaadinRequest request) {
    }

    @Override
    protected void refresh(VaadinRequest request) {
        Label l = new Label("Reinit!");
        l.setId(REINIT_ID);
        addComponent(l);
    }

    @Override
    public String getTestDescription() {
        return "UI reinit after refresh";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(12191);
    }
}
