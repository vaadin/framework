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

import com.vaadin.server.ClassResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;

public class EmbeddedPdf extends TestBase {

    @Override
    protected String getDescription() {
        return "The embedded flash should have the movie parameter set to \"someRandomValue\" and an allowFullScreen parameter set to \"true\".";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3367;
    }

    @Override
    public void setup() {
        final Embedded player = new Embedded();
        player.setType(Embedded.TYPE_BROWSER);
        player.setWidth("400px");
        player.setHeight("300px");
        player.setSource(new ClassResource(getClass(), "test.pdf"));
        addComponent(player);

        addComponent(new Button("Remove pdf", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                removeComponent(player);
            }
        }));

        player.getUI().addWindow(new Window("Testwindow"));
    }

}
