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
package com.vaadin.tests.components;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WebBrowser;

public abstract class AbstractTestCase extends LegacyApplication {

    protected abstract String getDescription();

    protected abstract Integer getTicketNumber();

    protected WebBrowser getBrowser() {
        WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
        return webBrowser;

    }
}
