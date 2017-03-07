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
package com.vaadin.tests.components.image;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Image;

public class ImageAltText extends TestBase {

    @Override
    protected void setup() {
        final Image image = new Image("Caption",
                new ThemeResource("../runo/icons/64/ok.png"));
        image.setDebugId("image");
        image.setAlternateText("Original alt text");
        addComponent(image);

        Button changeAltTexts = new Button("Change alt text",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        image.setAlternateText("New alt text!");
                    }
                });
        addComponent(changeAltTexts);
    }

    @Override
    protected String getDescription() {
        return "Test alternative text of image";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
