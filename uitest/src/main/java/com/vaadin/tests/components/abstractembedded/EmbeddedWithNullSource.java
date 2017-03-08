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
package com.vaadin.tests.components.abstractembedded;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractEmbedded;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Flash;
import com.vaadin.ui.Image;

public class EmbeddedWithNullSource extends TestBase {

    @Override
    protected void setup() {
        AbstractEmbedded e;

        e = new Image("Image w/o alt text");
        addComponent(e);

        e = new Image("Image w/ alt text");
        e.setAlternateText("Image");
        addComponent(e);

        e = new Flash("Flash w/o alt text");
        addComponent(e);

        e = new Flash("Flash w/ alt text");
        e.setAlternateText("Flash");
        addComponent(e);

        e = new BrowserFrame("BrowserFrame w/o alt text");
        addComponent(e);

        e = new BrowserFrame("BrowserFrame w/ alt text");
        e.setAlternateText("BrowserFrame");
        addComponent(e);

    }

    @Override
    protected String getDescription() {
        return "Image without a source causes a client-side NPE";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10101;
    }

}
