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
package com.vaadin.tests.components.label;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

public class HundredPercentWideLabelResize extends TestBase {

    @Override
    protected String getDescription() {
        return "100% wide label re-wrap should cause re-layout; forceLayout fixes this.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2514;
    }

    @Override
    protected void setup() {
        getLayout().setWidth("500px");
        Label text = new Label(
                "This is a fairly long text that will wrap if the width of the layout is narrow enough. Directly below the text is a Button - however, when the layout changes size, the Label re-wraps w/o moving the button, causing eiter clipping or a big space.");
        text.setWidth("100%");
        getLayout().addComponent(text);

        getLayout().addComponent(
                new Button("toggle width", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (getLayout().getWidth() == 500) {
                            getLayout().setWidth("100px");
                        } else {
                            getLayout().setWidth("500px");
                        }

                    }

                }));
    }

}
