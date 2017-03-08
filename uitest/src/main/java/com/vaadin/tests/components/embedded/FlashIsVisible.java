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
import com.vaadin.ui.Embedded;

/**
 * Tests the legacy flash support in Embedded
 */
@Deprecated
public class FlashIsVisible extends TestBase {

    @Override
    protected void setup() {
        Embedded player = new Embedded();
        player.setType(Embedded.TYPE_OBJECT);
        player.setMimeType("application/x-shockwave-flash");
        player.setWidth("400px");
        player.setHeight("300px");
        player.setSource(new ClassResource(
                com.vaadin.tests.components.flash.FlashIsVisible.class,
                "simple.swf"));
        addComponent(player);
    }

    @Override
    protected String getDescription() {
        return "Flash plugin should load and be visible on all browsers";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6501;
    }

}
