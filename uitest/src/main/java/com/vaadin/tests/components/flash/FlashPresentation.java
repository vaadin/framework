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
package com.vaadin.tests.components.flash;

import com.vaadin.server.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Flash;

public class FlashPresentation extends TestBase {

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
        Flash player = new Flash();
        player.setWidth("400px");
        player.setHeight("300px");
        String url = "http://www.youtube.com/v/qQ9N742QB4g&autoplay=1";
        player.setSource(new ExternalResource(url));
        player.setParameter("movie", "someRandomValue");
        player.setParameter("allowFullScreen", "true");
        player.setAlternateText("Flash alternative text");

        addComponent(player);
    }

}
