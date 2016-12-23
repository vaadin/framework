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
package com.vaadin.tests.components.checkbox;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.tests.components.abstractlisting.AbstractMultiSelectTestUI;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.IconGenerator;

/**
 * Test UI for CheckBoxGroup component
 *
 * @author Vaadin Ltd
 */
public class CheckBoxGroupTestUI
        extends AbstractMultiSelectTestUI<CheckBoxGroup<Object>> {

    private static final IconGenerator<Object> DEFAULT_ICON_GENERATOR = item -> "Item 2"
            .equals(item) ? ICON_16_HELP_PNG_CACHEABLE : null;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Class<CheckBoxGroup<Object>> getTestClass() {
        return (Class) CheckBoxGroup.class;
    }

    @Override
    protected CheckBoxGroup<Object> constructComponent() {
        CheckBoxGroup<Object> checkBoxGroup = super.constructComponent();
        checkBoxGroup.setItemIconGenerator(DEFAULT_ICON_GENERATOR);
        checkBoxGroup.setItemEnabledProvider(item -> !"Item 10".equals(item));
        return checkBoxGroup;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createItemIconGenerator();
    }

    private void createItemIconGenerator() {
        createBooleanAction("Use Item Icon Generator", "Item Generator", false,
                this::useItemIconProvider);
    }

    private void useItemIconProvider(CheckBoxGroup<Object> group,
            boolean activate, Object data) {
        if (activate) {
            group.setItemIconGenerator(
                    item -> VaadinIcons.values()[getIndex(item) + 1]);
        } else {
            group.setItemIconGenerator(DEFAULT_ICON_GENERATOR);
        }
        group.getDataProvider().refreshAll();
    }

    private int getIndex(Object item) {
        int index = item.toString().indexOf(' ');
        if (index < 0) {
            return 0;
        }
        String postfix = item.toString().substring(index + 1);
        index = postfix.indexOf(' ');
        if (index >= 0) {
            postfix = postfix.substring(0, index);
        }
        try {
            return Integer.parseInt(postfix);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
