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
package com.vaadin.tests.components.embedded;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Image;
import com.vaadin.v7.ui.themes.Reindeer;

/**
 * Tests that {@link Embedded} uses correct theme when the theme is set with
 * {@link #setTheme(String)}, and also updates correctly if theme is changed
 * later. {@link Image} is used as the baseline for correct behavior.
 *
 * @author Vaadin Ltd
 */
public class EmbeddedThemeResource extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        setTheme("tests-components");

        addButton("Toggle theme", event -> {
            if (Reindeer.THEME_NAME.equals(getTheme())) {
                setTheme("tests-components");
            } else {
                setTheme(Reindeer.THEME_NAME);
            }
        });

        // let's show a simple themeresource
        ThemeResource logoResource = new ThemeResource("images/logo.png");
        Embedded embedded = new Embedded("embedded:", logoResource);
        Image image = new Image("image:", logoResource);

        addComponents(embedded, image);
    }

    @Override
    protected String getTestDescription() {
        return "Tests that Embedded updates correctly when using setTheme(String)";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15194;
    }
}
