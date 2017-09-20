/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.tests.components.flash;

import com.vaadin.server.ClassResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Flash;

public class FlashExpansion extends TestBase {

    Flash player = new Flash();

    @Override
    protected void setup() {

        player.setWidth("400px");
        player.setHeight("300px");
        player.setSource(new ClassResource("simple.swf"));
        addComponent(player);
        Button button = new Button("click", e -> player.setSizeFull());
        addComponent(button);
    }

    @Override
    protected String getDescription() {
        return "Flash object should expand according to percentile sizes";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4035;
    }

}
