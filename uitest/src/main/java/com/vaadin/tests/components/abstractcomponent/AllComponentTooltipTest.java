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
package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.VaadinClasses;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.LegacyWindow;

public class AllComponentTooltipTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        GridLayout layout = new GridLayout(5, 5);
        setContent(layout);
        for (Class<? extends Component> cls : VaadinClasses.getComponents()) {
            try {
                AbstractComponent c = (AbstractComponent) cls.newInstance();
                if (c instanceof LegacyWindow) {
                    continue;
                }

                c.setId(cls.getName());
                c.setCaption(cls.getName());
                c.setDescription(cls.getName());
                c.setWidth("100px");
                c.setHeight("100px");
                layout.addComponent(c);
                System.out.println("Added " + cls.getName());
            } catch (Exception e) {
                System.err.println("Could not instatiate " + cls.getName());
            }
        }
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
