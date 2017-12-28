/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.tests.components;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ConnectorResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.embedded.EmbeddedPdf;
import com.vaadin.ui.Button;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class MenuBarDownloadBrowserOpenerUI extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {

        ConnectorResource downloadResource = new ClassResource(
                new EmbeddedPdf().getClass(), "test.pdf");
        ExternalResource openResource = new ExternalResource(
                "https://vaadin.com");

        MenuBar menuBar = new MenuBar();
        MenuItem download = menuBar.addItem("Download");
        MenuItem saveAsNoLog = download.addItem("Save as without logging...");
        MenuItem saveAsLog = download.addItem("Save as with logging...",
                item -> {
                    log("Download triggered");
                });
        FileDownloader fd = new FileDownloader(downloadResource);
        fd.extend(saveAsNoLog);
        FileDownloader fd2 = new FileDownloader(downloadResource);
        fd2.extend(saveAsLog);

        MenuItem open = menuBar.addItem("Open");
        MenuItem openNoLog = open.addItem("Open without logging...");
        MenuItem openLog = open.addItem("Open with logging...", item -> {
            log("Open triggered");
        });

        BrowserWindowOpener bwo = new BrowserWindowOpener(openResource);
        bwo.extend(openNoLog);
        BrowserWindowOpener bwo2 = new BrowserWindowOpener(openResource);
        bwo2.extend(openLog);

        addComponent(menuBar);

        addComponent(new Button("Remove downloaders and openers", event -> {
            fd.remove();
            fd2.remove();
            bwo.remove();
            bwo2.remove();
        }));
    }

}
