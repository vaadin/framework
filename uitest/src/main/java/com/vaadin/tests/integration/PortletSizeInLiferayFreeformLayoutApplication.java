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

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;

/**
 * On Liferay in a freeform layout, this application should get its height from
 * the height of the portlet container in the Liferay layout.
 *
 * See ticket #5521.
 */
public class PortletSizeInLiferayFreeformLayoutApplication
        extends LegacyApplication {
    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow("Portlet5521 Application");
        ((VerticalLayout) mainWindow.getContent()).setMargin(false);
        ((VerticalLayout) mainWindow.getContent()).setSizeFull();
        // ((VerticalLayout) mainWindow.getContent()).setHeight("200px");
        Label label = new Label("Hello Vaadin user");
        mainWindow.addComponent(label);
        for (int i = 0; i < 50; ++i) {
            mainWindow.addComponent(new Label("Label " + i));
        }
        mainWindow.setSizeFull();
        setMainWindow(mainWindow);
    }

}
