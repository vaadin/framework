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

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;

public class EmbeddedClickListenerRelativeCoordinates extends TestBase {

    @Override
    protected void setup() {
        Embedded e = new Embedded("Embedded caption",
                new ThemeResource("../runo/icons/64/ok.png"));
        final Label xLabel = new Label();
        xLabel.setId("x");
        final Label yLabel = new Label();
        yLabel.setId("y");
        e.addClickListener(new ClickListener() {

            @Override
            public void click(ClickEvent event) {
                xLabel.setValue("" + event.getRelativeX());
                yLabel.setValue("" + event.getRelativeY());
            }
        });
        addComponent(e);
        addComponent(xLabel);
        addComponent(yLabel);
    }

    @Override
    protected String getDescription() {
        return "Click the image to get coordinates relative to the top-left corder of the embedded image.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
