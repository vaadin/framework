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
package com.vaadin.tests.widgetset.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ValueMap;
import com.vaadin.client.debug.internal.DebugButton;
import com.vaadin.client.debug.internal.Icon;
import com.vaadin.client.debug.internal.Section;
import com.vaadin.client.debug.internal.VDebugWindow;

public class TestingWidgetsetEntryPoint implements EntryPoint {

    @Override
    public void onModuleLoad() {
        if (Location.getPath().contains("PreserveCustomDebugSectionOpen")) {
            addDummyDebugWindowSection();
        }
    }

    private void addDummyDebugWindowSection() {
        VDebugWindow.get().addSection(new Section() {
            private final DebugButton tabButton = new DebugButton(Icon.ERROR,
                    "Dummy debug window section");
            private final Label controls = new Label("");
            private final Label contents = new Label(
                    "Dummy debug window section");

            @Override
            public DebugButton getTabButton() {
                return tabButton;
            }

            @Override
            public Widget getControls() {
                return controls;
            }

            @Override
            public Widget getContent() {
                return contents;
            }

            @Override
            public void show() {
                // nop
            }

            @Override
            public void hide() {
                // nop
            }

            @Override
            public void meta(ApplicationConnection ac, ValueMap meta) {
                // nop
            }

            @Override
            public void uidl(ApplicationConnection ac, ValueMap uidl) {
                // nop
            }
        });
    }

}
