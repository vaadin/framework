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
package com.vaadin.tests.components.customcomponent;

import java.util.Date;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;

public class EmbeddedInCustomComponent extends TestBase {

    @Override
    protected void setup() {
        HorizontalLayout hl = new HorizontalLayout();

        hl.addComponent(wrap("An uncached image",
                "ok.png?random=" + new Date().getTime()));
        hl.addComponent(wrap("A cached image", "cancel.png"));
        addComponent(hl);
    }

    private CustomComponent wrap(String caption, String themeImage) {
        Embedded image = new Embedded(caption,
                new ThemeResource("../runo/icons/64/" + themeImage));
        CustomComponent cc = new CustomComponent(image);
        return cc;
    }

    @Override
    protected String getDescription() {
        return "Two embedded images are shown below, side-by-side. The first one has a random url so it is not cached, the second one is cached.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6304;
    }

}
