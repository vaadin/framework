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
package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;

public class PopUpWidth extends TestBase {

    @Override
    protected void setup() {

        addComponent(createComboBox("Do not touch this"));
        addComponent(createComboBox(
                "Browse this (check that width does not change)"));
    }

    private ComboBox<Integer> createComboBox(String caption) {
        ComboBox<Integer> cb = new ComboBox<>(caption);
        List<Integer> items = new ArrayList<>();
        for (int i = 1; i < 200 + 1; i++) {
            items.add(i);
        }
        cb.setItems(items);
        cb.setItemIconGenerator(
                item -> new ThemeResource("../runo/icons/16/users.png"));
        cb.setItemCaptionGenerator(item -> "Item " + item);
        return cb;
    }

    @Override
    protected String getDescription() {
        return "Check that width of popup or combobox does not change when paging.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7013;
    }

}
