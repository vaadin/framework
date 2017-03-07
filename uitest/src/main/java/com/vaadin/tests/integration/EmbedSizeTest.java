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
package com.vaadin.tests.integration;

import com.vaadin.server.Page;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.LegacyWindow;

public class EmbedSizeTest extends TestBase {

    private Log log = new Log(10);

    @Override
    protected void setup() {
        LegacyWindow mainWindow = getMainWindow();
        mainWindow.setSizeUndefined();
        mainWindow.getContent().setSizeUndefined();

        CheckBox lazyCheckBox = new CheckBox("Lazy resize");
        lazyCheckBox.addValueChangeListener(event -> {
            boolean resizeLazy = event.getValue();
            getMainWindow().setResizeLazy(resizeLazy);
            log.log("Resize lazy: " + resizeLazy);
        });
        lazyCheckBox.setValue(Boolean.FALSE);
        addComponent(lazyCheckBox);

        addComponent(log);
        mainWindow.addListener(new Page.BrowserWindowResizeListener() {
            @Override
            public void browserWindowResized(BrowserWindowResizeEvent event) {
                log.log("Resize event: " + event.getWidth() + " x "
                        + event.getHeight());
            }
        });
    }

    @Override
    protected String getDescription() {
        return "Resizing the browser window should send consistent resize events to the server even when the application is embedded";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7923);
    }

}
