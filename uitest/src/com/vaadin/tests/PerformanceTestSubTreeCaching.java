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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class PerformanceTestSubTreeCaching extends CustomComponent {

    private final VerticalLayout main;

    private final VerticalLayout testContainer;

    private Date startTime;

    private final Label result;

    private static final String DESCRIPTION = "Hypothesis: Toolkit 4 has major architechtural problem when adding "
            + "small incrementall updates to a container which has either a lot or "
            + "some very slow components in it. Toolkit 5 has 'subtree caching' and a"
            + " small amount of logic in containers, so CommunicationManager can assure"
            + " that client do not need information about unchanged components it contains."
            + " Completing test ought to be much faster with Toolkit 5.";

    private static final int INITIAL_COMPONENTS = 40;

    public PerformanceTestSubTreeCaching() {
        main = new VerticalLayout();
        setCompositionRoot(main);
        addInfo();

        Button b = new Button("start test", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                startTest();
            }
        });
        b.setDescription("Push this button to start test. A test label will be rendered above existing components.");
        main.addComponent(b);
        b = new Button("end test", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                endTest();
            }
        });
        b.setDescription("Push this button as soon as test componenet is rendered.");
        main.addComponent(b);

        result = new Label();
        main.addComponent(result);

        testContainer = new VerticalLayout();
        populateContainer(testContainer, INITIAL_COMPONENTS);
        main.addComponent(testContainer);
    }

    public void startTest() {
        startTime = new Date();
        testContainer.addComponentAsFirst(new Label("Simplel Test Component"));
    }

    public void endTest() {
        final long millis = (new Date()).getTime() - startTime.getTime();
        final Float f = new Float(millis / 1000.0);
        result.setValue("Test completed in " + f + " seconds");
    }

    /**
     * Adds n Table components to given container
     * 
     * @param testContainer2
     */
    private void populateContainer(VerticalLayout container, int n) {
        for (int i = 0; i < n; i++) {
            // array_type array_element = [i];
            final Table t = TestForTablesInitialColumnWidthLogicRendering
                    .getTestTable(5, 100);
            container.addComponent(t);
        }
    }

    private void addInfo() {
        main.addComponent(new Label(DESCRIPTION, ContentMode.HTML));
    }

}
