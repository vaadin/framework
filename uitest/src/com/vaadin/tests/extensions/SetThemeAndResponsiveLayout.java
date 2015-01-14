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
package com.vaadin.tests.extensions;

import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class SetThemeAndResponsiveLayout extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setSizeFull();
        CssLayout responsiveLayout = new CssLayout();
        responsiveLayout.addStyleName("width-and-height");
        responsiveLayout.setSizeFull();
        setContent(responsiveLayout);
        responsiveLayout
                .addComponent(new Label(
                        "First set the theme using the button and then resize the browser window in both dimensions to see the background color change."));
        Button setThemeButton = new Button("Set theme");
        setThemeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setTheme("tests-responsive");
            }
        });
        responsiveLayout.addComponent(setThemeButton);
        Responsive.makeResponsive(responsiveLayout);
    }

    @Override
    protected String getTestDescription() {
        return "This test verifies that responsive works also when theme is set using setTheme method";
    };

    @Override
    protected Integer getTicketNumber() {
        return 15281;
    };

}
