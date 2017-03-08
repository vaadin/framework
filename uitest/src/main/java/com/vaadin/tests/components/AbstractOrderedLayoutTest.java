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
package com.vaadin.tests.components;

import java.util.LinkedHashMap;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;

public abstract class AbstractOrderedLayoutTest<T extends AbstractOrderedLayout>
        extends AbstractLayoutTest<T> implements LayoutClickListener {

    private Command<T, Boolean> layoutClickListenerCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            if (value) {
                c.addLayoutClickListener(AbstractOrderedLayoutTest.this);
            } else {

            }

        }
    };

    private Command<T, Integer> setComponentExpandRatio = new Command<T, Integer>() {

        @Override
        public void execute(T c, Integer value, Object ratio) {
            Component child = getComponentAtIndex(c, value);
            c.setExpandRatio(child, (Float) ratio);
        }
    };

    @Override
    protected void createActions() {
        super.createActions();

        createLayoutClickListenerAction(CATEGORY_LISTENERS);
        createChangeComponentExpandRatioAction(CATEGORY_LAYOUT_FEATURES);
        // Set a root style so we can see the component. Can be overridden by
        // setting the style name in the UI
        for (T c : getTestComponents()) {
            c.setStyleName("background-lightblue");
        }
    }

    private void createLayoutClickListenerAction(String category) {
        createBooleanAction("Layout click listener", category, false,
                layoutClickListenerCommand);
    }

    private void createChangeComponentExpandRatioAction(String category) {
        String expandRatioCategory = "Component expand ratio";
        createCategory(expandRatioCategory, category);

        LinkedHashMap<String, Float> options = new LinkedHashMap<>();
        options.put("0", 0f);
        options.put("0.5", 0.5f);
        for (float f = 1; f <= 5; f++) {
            options.put(String.valueOf(f), f);
        }

        for (int i = 0; i < 20; i++) {
            String componentExpandRatioCategory = "Component " + i
                    + " expand ratio";
            createCategory(componentExpandRatioCategory, expandRatioCategory);

            for (String option : options.keySet()) {
                createClickAction(option, componentExpandRatioCategory,
                        setComponentExpandRatio, Integer.valueOf(i),
                        options.get(option));
            }

        }

    }

    @Override
    public void layoutClick(LayoutClickEvent event) {
        log(event.getClass().getSimpleName() + ": button="
                + event.getButtonName() + ", childComponent="
                + event.getChildComponent().getClass().getSimpleName()
                + ", relativeX=" + event.getRelativeX() + ", relativeY="
                + event.getRelativeY());

    }
}
