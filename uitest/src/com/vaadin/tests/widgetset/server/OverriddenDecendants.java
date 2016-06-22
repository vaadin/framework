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
package com.vaadin.tests.widgetset.server;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.TextArea;

/**
 * UI for testing that @DelegateToWidget works on derived widget states.
 *
 * @author Vaadin Ltd
 */
@Widgetset(TestingWidgetSet.NAME)
public class OverriddenDecendants extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        TextArea normalTextArea = new TextArea();
        normalTextArea.setRows(10);
        normalTextArea.setWordwrap(true);

        getLayout().addComponent(normalTextArea);

        // @DelegateToWidget will not work with overridden state in connector
        SuperTextArea superTextArea = new SuperTextArea();
        superTextArea.setRows(10);
        superTextArea.setWordwrap(true);

        getLayout().addComponent(superTextArea);

        // @DelegateToWidget will not work with overridden state in connector
        ExtraSuperTextArea extraSuperTextArea = new ExtraSuperTextArea();
        extraSuperTextArea.setRows(10);
        extraSuperTextArea.setWordwrap(true);

        getLayout().addComponent(extraSuperTextArea);
    }

    @Override
    protected String getTestDescription() {
        return "@DelegateToWidget does not work for widget descendants with overridden getState";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14059;
    }

}
