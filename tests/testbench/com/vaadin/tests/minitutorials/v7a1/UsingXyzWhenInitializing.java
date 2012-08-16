/* 
 * Copyright 2011 Vaadin Ltd.
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

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Using%20URI%20or%20
 * parameters%20or%20screen%20size%20when%20initializing%20an%20application
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class UsingXyzWhenInitializing extends Root {

    @Override
    protected void init(WrappedRequest request) {
        String name = request.getParameter("name");
        if (name == null) {
            name = "Unknown";
        }

        getContent().addComponent(new Label("Hello " + name));

        String pathInfo = request.getRequestPathInfo();
        if ("/viewSource".equals(pathInfo)) {
            getContent().addComponent(new Label("This is the source"));
        } else {
            getContent().addComponent(new Label("Welcome to my application"));
        }

        // WebBrowser browser = request.getBrowserDetails().getWebBrowser();
        // String resolution = "Your browser window on startup was "
        // + browser.getClientWidth() + "x" + browser.getClientHeight();
        // if (browser.getClientWidth() > 1024) {
        // getContent().addComponent(
        // new Label("The is the large version of the application. "
        // + resolution));
        // } else {
        // getContent().addComponent(
        // new Label("This is the small version of the application. "
        // + resolution));
        // }
    }

}
