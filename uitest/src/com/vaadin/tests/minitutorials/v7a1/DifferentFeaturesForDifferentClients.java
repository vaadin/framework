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

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Creating%20an%20application
 * %20with%20different%20features%20for%20different%20clients
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class DifferentFeaturesForDifferentClients extends UIProvider {

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        // could also use browser version etc.
        if (event.getRequest().getHeader("user-agent").contains("mobile")) {
            return TouchUI.class;
        } else {
            return DefaultUI.class;
        }
    }

    // Must override as default implementation isn't allowed to
    // instantiate our non-public classes
    @Override
    public UI createInstance(UICreateEvent event) {
        try {
            return event.getUIClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class DefaultUI extends UI {
    @Override
    protected void init(VaadinRequest request) {
        setContent(new Label("This browser does not support touch events"));
    }
}

class TouchUI extends UI {
    @Override
    protected void init(VaadinRequest request) {
        WebBrowser webBrowser = getPage().getWebBrowser();
        String screenSize = "" + webBrowser.getScreenWidth() + "x"
                + webBrowser.getScreenHeight();
        setContent(new Label("Using a touch enabled device with screen size"
                + screenSize));
    }
}
