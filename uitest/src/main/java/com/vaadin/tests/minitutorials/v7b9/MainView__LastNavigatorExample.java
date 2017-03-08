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
package com.vaadin.tests.minitutorials.v7b9;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class MainView__LastNavigatorExample extends Panel implements View {

    public static final String NAME = "";

    public MainView__LastNavigatorExample(final Navigator navigator) {

        Link lnk = new Link("Settings",
                new ExternalResource("#!" + SettingsView.NAME));
        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(lnk);
        setContent(vl);

    }

    @Override
    public void enter(ViewChangeEvent event) {

    }
}
