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
package com.vaadin.tests.components.popupview;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;

/*
 * Used by PopupViewInEmbedded.html
 */
public class PopupViewInEmbeddedApplication extends TestBase {

    @Override
    protected void setup() {
        PopupView pop = new PopupView("Click me!",
                new Label("I popped up, woohoo!"));
        addComponent(pop);
    }

    @Override
    protected String getDescription() {
        return "Clicking on the popup link should pop up the popup on top of the link,"
                + " even though the application has been embedded inside a div.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7110;
    }

}
