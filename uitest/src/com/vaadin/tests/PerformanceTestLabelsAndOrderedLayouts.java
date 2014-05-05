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

package com.vaadin.tests;

import java.util.Date;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class PerformanceTestLabelsAndOrderedLayouts extends CustomComponent {
    private final AbstractOrderedLayout main;

    private final AbstractOrderedLayout testContainer;

    private Date startTime;

    private final Label result;

    private static final String DESCRIPTION = "Simple test that renders n labels into ordered layout.";

    private static final int INITIAL_COMPONENTS = 1000;

    public PerformanceTestLabelsAndOrderedLayouts() {
        main = new VerticalLayout();
        setCompositionRoot(main);
        addInfo();

        result = new Label();
        main.addComponent(result);

        main.addComponent(new Button("click when rendered",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        endTest();
                    }
                }));

        main.addComponent(new Button(
                "Click for layout repaint (cached components)",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        testContainer.markAsDirty();
                    }
                }));

        testContainer = new VerticalLayout();

        for (int i = 0; i < INITIAL_COMPONENTS; i++) {
            Label l = new Label("foo" + i);
            testContainer.addComponent(l);
        }

        main.addComponent(testContainer);
        startTest();
    }

    public void startTest() {
        startTime = new Date();
    }

    public void endTest() {
        final long millis = (new Date()).getTime() - startTime.getTime();
        final Float f = new Float(millis / 1000.0);
        result.setValue("Test completed in " + f + " seconds");
    }

    private void addInfo() {
        main.addComponent(new Label(DESCRIPTION, ContentMode.HTML));
    }

}
