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
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Embedded;

public class EmbeddedTooltip extends TestBase {

    @Override
    protected String getDescription() {
        return "The tooltip for an Embedded image should be visible also when hovering the image";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2853;
    }

    @Override
    protected void setup() {
        Embedded e = new Embedded("Embedded caption",
                new ThemeResource("../runo/icons/64/ok.png"));
        e.setDescription(
                "Embedded tooltip, only shown on caption, not on the image");
        addComponent(e);

    }
}
