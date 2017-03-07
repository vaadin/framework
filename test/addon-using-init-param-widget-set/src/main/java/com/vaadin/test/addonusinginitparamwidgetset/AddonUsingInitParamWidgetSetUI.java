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
package com.vaadin.test.addonusinginitparamwidgetset;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.contextmenu.ContextMenu;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.test.widgetset.AbstractTestWidgetSetUI;
import com.vaadin.ui.AbstractComponent;

public class AddonUsingInitParamWidgetSetUI extends AbstractTestWidgetSetUI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        super.init(vaadinRequest);
        new ContextMenu((AbstractComponent) getContent(), true);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true, initParams = @WebInitParam(name = "widgetset", value = "com.vaadin.DefaultWidgetSet"))
    @VaadinServletConfiguration(ui = AddonUsingInitParamWidgetSetUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
