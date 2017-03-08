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
package com.vaadin.tests.components.uitest.components;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Embedded;

public class EmbeddedCssTest {

    private int debugIdCounter = 0;

    public EmbeddedCssTest(TestSampler parent) {
        Embedded e = new Embedded("Embedded with a caption",
                new ThemeResource(parent.ICON_URL));
        e.setId("embedded" + debugIdCounter);
        parent.addComponent(e);

        e = new Embedded(null, new ThemeResource(parent.ICON_URL));
        e.setId("embedded" + debugIdCounter);
        parent.addComponent(e);

        BrowserFrame eBrowser = new BrowserFrame();
        eBrowser.setCaption("A embedded browser");
        eBrowser.setSource(null);
        eBrowser.setHeight("150px");
        eBrowser.setWidth("300px");
        eBrowser.setId("embedded" + debugIdCounter);
        parent.addComponent(eBrowser);
    }

}
