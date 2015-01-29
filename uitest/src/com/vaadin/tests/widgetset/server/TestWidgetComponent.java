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

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.annotations.Widgetset;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.client.TestWidgetConnector;
import com.vaadin.tests.widgetset.client.TestWidgetConnector.TestWidgetState;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.UI;

/**
 * Testing component that shows any widget class inside the
 * com.vaadin.tests.widgetset.client package.
 */
public class TestWidgetComponent extends AbstractComponent {
    private static final String targetPackage = TestWidgetConnector.class
            .getPackage().getName();

    public TestWidgetComponent(Class<? extends Widget> widgetClass) {
        String className = widgetClass.getCanonicalName();
        if (!className.startsWith(targetPackage)) {
            throw new IllegalArgumentException(
                    "Widget class must be inside the " + targetPackage
                            + " package");
        }

        getState().widgetClass = className;
        setSizeFull();
    }

    @Override
    public void attach() {
        super.attach();

        Class<? extends UI> uiClass = getUI().getClass();

        Widgetset widgetset = uiClass.getAnnotation(Widgetset.class);
        if (widgetset == null
                || !widgetset.value().equals(TestingWidgetSet.NAME)) {
            throw new IllegalStateException("You must add @"
                    + Widgetset.class.getSimpleName() + "("
                    + TestingWidgetSet.class.getSimpleName() + ".NAME) to "
                    + uiClass.getSimpleName());
        }
    }

    @Override
    protected TestWidgetState getState() {
        return (TestWidgetState) super.getState();
    }

}
