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

package com.vaadin.tests;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Root;
import com.vaadin.ui.VerticalLayout;

public class TestForWindowOpen extends CustomComponent {

    public TestForWindowOpen() {

        final VerticalLayout main = new VerticalLayout();
        setCompositionRoot(main);

        main.addComponent(new Button("Open in this window",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        final ExternalResource r = new ExternalResource(
                                "http://www.google.com");
                        Root.getCurrent().getPage().open(r);

                    }

                }));

        main.addComponent(new Button("Open in target \"mytarget\"",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        final ExternalResource r = new ExternalResource(
                                "http://www.google.com");
                        Root.getCurrent().getPage().open(r, "mytarget");

                    }

                }));

        main.addComponent(new Button("Open in target \"secondtarget\"",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        final ExternalResource r = new ExternalResource(
                                "http://www.google.com");
                        Root.getCurrent().getPage().open(r, "secondtarget");

                    }

                }));

    }

}
