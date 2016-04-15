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

package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.minitutorials.v7a3.Refresher.RefreshEvent;
import com.vaadin.tests.minitutorials.v7a3.Refresher.RefreshListener;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

@Widgetset(TestingWidgetSet.NAME)
public class RefresherTestUI extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Refresher refresher = new Refresher(this);
        refresher.setInterval(1000);
        refresher.addRefreshListener(new RefreshListener() {
            @Override
            public void refresh(RefreshEvent event) {
                System.out.println("Got refresh");
            }
        });
        addComponent(new Button("Remove refresher", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                removeExtension(refresher);
            }
        }));
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
