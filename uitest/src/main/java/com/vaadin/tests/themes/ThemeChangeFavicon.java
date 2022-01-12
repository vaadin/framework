/*
 * Copyright 2000-2022 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4);
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.themes;

import java.util.Arrays;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class ThemeChangeFavicon extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        for (final String theme : Arrays.asList("valo", "reindeer")) {
            addComponent(new Button(theme, new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    setTheme(theme);
                }
            }));
        }
    }

    @Override
    public String getDescription() {
        return "UI for testing that the favicon changes when changing themes";
    }

}
