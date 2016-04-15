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
import java.util.Map;

import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class PerformanceTestBasicComponentRendering extends CustomComponent {

    private final VerticalLayout main;

    private final VerticalLayout testContainer;

    private Date startTime;

    private final Label result;

    private static final String DESCRIPTION = "Rendering lots of differend components to stress rendering performance. Visits server after render (due table cache row fetch) and prints client round trip time to a label. More exact render time can be checked from clients debug dialog.";

    private static final int INITIAL_COMPONENTS = 10;

    public PerformanceTestBasicComponentRendering() {

        main = new VerticalLayout();
        setCompositionRoot(main);
        addInfo();

        result = new Label();
        main.addComponent(result);

        testContainer = new VerticalLayout();

        final Table t = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(5, 200);

        Table t2 = new Table("Test Table with 199 rows rendered initially") {
            @Override
            public void changeVariables(Object source,
                    Map<String, Object> variables) {
                super.changeVariables(source, variables);
                // end timing on cache row request
                endTest();
            }
        };
        t2.setPageLength(199); // render almost all rows at once
        t2.setContainerDataSource(t.getContainerDataSource());

        testContainer.addComponent(t2);

        for (int i = 0; i < INITIAL_COMPONENTS; i++) {
            ComboBox cb = new ComboBox("Combobox " + i);
            for (int j = 0; j < INITIAL_COMPONENTS; j++) {
                cb.addItem("option " + i + " " + j);
            }
            testContainer.addComponent(cb);

            TextField tf = new TextField("TextField " + i);
            tf.setDescription("DESC SDKJSDF");
            tf.setComponentError(new UserError("dsfjklsdf"));
            testContainer.addComponent(tf);

            testContainer.addComponent(new DateField("DateField" + i));

            testContainer.addComponent(new Button("Button" + i));

            TabSheet ts = new TabSheet();

            for (int j = 0; j < INITIAL_COMPONENTS; j++) {
                Label tab = new Label("Tab content " + i + " " + j);
                tab.setCaption("Tab " + i + " " + j);
                ts.addTab(tab);
            }
            testContainer.addComponent(ts);

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
