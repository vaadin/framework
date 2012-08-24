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
package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.VaadinClasses;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;

public class AllComponentTooltipTest extends AbstractTestUI {

    @Override
    protected void setup(WrappedRequest request) {
        setContent(new GridLayout(5, 5));
        for (Class<? extends Component> cls : VaadinClasses.getComponents()) {
            try {
                AbstractComponent c = (AbstractComponent) cls.newInstance();
                if (c instanceof LegacyWindow) {
                    continue;
                }

                c.setDebugId(cls.getName());
                c.setCaption(cls.getName());
                c.setDescription(cls.getName());
                c.setWidth("100px");
                c.setHeight("100px");
                getContent().addComponent(c);
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
