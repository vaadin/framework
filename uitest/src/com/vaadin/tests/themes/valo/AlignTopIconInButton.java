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
package com.vaadin.tests.themes.valo;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Test UI for image icon in button with 'icon-align-top' style.
 * 
 * @author Vaadin Ltd
 */
@Theme("valo")
public class AlignTopIconInButton extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Button button = new Button();
        button.setIcon(new ThemeResource("../runo/icons/16/document.png"));
        addComponent(button);
        button.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_TOP);
        button.setCaption("caption");
    }

    @Override
    protected Integer getTicketNumber() {
        return 15140;
    }

    @Override
    protected String getTestDescription() {
        return "Icon in the button with 'icon-align-top' style is not "
                + "centered when image is used.";
    }
}
