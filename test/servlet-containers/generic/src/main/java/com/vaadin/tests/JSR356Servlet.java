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
package com.vaadin.tests;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.integration.ServletIntegrationWebsocketUI;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * Created by elmot on 06-02-2017.
 */
@WebServlet(urlPatterns = "/run-jsr356/*", name = "IntegrationUIProvider-Jsr356", asyncSupported = false, initParams = {
        @WebInitParam(name = "org.atmosphere.cpr.asyncSupport", value = "org.atmosphere.container.JSR356AsyncSupport")})
@VaadinServletConfiguration(ui = ServletIntegrationWebsocketUI.class, productionMode = false)
public class JSR356Servlet extends VaadinServlet {

}
