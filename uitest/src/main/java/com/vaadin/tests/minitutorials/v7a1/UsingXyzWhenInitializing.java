/* 
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Using%20URI%20or%20
 * parameters%20or%20screen%20size%20when%20initializing%20an%20application
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class UsingXyzWhenInitializing extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        String name = request.getParameter("name");
        if (name == null) {
            name = "Unknown";
        }

        layout.addComponent(new Label("Hello " + name));

        String pathInfo = request.getPathInfo();
        if ("/viewSource".equals(pathInfo)) {
            layout.addComponent(new Label("This is the source"));
        } else {
            layout.addComponent(new Label("Welcome to my application"));
        }

        WebBrowser browser = getPage().getWebBrowser();
        String resolution = "Your browser window on startup was "
                + browser.getScreenWidth() + "x" + browser.getScreenHeight();
        if (browser.getScreenWidth() > 1024) {
            layout.addComponent(new Label(
                    "The is the large version of the application. "
                            + resolution));
        } else {
            layout.addComponent(new Label(
                    "This is the small version of the application. "
                            + resolution));
        }
    }

}
