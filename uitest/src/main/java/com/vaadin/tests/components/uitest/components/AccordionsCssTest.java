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
package com.vaadin.tests.components.uitest.components;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.themes.ChameleonTheme;
import com.vaadin.v7.ui.themes.Runo;

public class AccordionsCssTest {

    private TestSampler parent;
    private int debugIdCounter = 0;

    public AccordionsCssTest(TestSampler parent) {
        this.parent = parent;

        Accordion def = createAccordionWith("Def Accordion", null);
        parent.addComponent(def);

        Accordion light = createAccordionWith("Light Accordion",
                Runo.ACCORDION_LIGHT);
        parent.addComponent(light);

        Accordion opaque = createAccordionWith("Oppaque Accordion",
                ChameleonTheme.ACCORDION_OPAQUE);
        parent.addComponent(opaque);

    }

    private Accordion createAccordionWith(String caption, String styleName) {
        Accordion acc = new Accordion();
        acc.setId("accordion" + debugIdCounter++);
        acc.setCaption(caption);
        acc.setComponentError(new UserError("A error message..."));

        if (styleName != null) {
            acc.addStyleName(styleName);
        }

        Label l1 = new Label("There are no previously saved actions.");
        Label l2 = new Label("There are no saved notes.");
        Label l3 = new Label("There are currently no issues.");

        acc.addTab(l1, "Actions", new ThemeResource(parent.ICON_URL));
        acc.addTab(l2, "Notes", new ThemeResource(parent.ICON_URL));
        acc.addTab(l3, "Issues", new ThemeResource(parent.ICON_URL));

        acc.getTab(l2).setEnabled(false);

        return acc;
    }

}
