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
package com.vaadin.tests.components.optiongroup;

import java.util.Collection;
import java.util.LinkedHashMap;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.select.AbstractSelectTestCase;
import com.vaadin.v7.ui.OptionGroup;

public class OptionGroups extends AbstractSelectTestCase<OptionGroup> {

    @Override
    protected Class<OptionGroup> getTestClass() {
        return OptionGroup.class;
    }

    @Override
    protected void createActions() {
        super.createActions();

        createDisabledItemsMultiToggle("Disabled items");
        createBooleanAction("HTML content allowed", CATEGORY_STATE, false,
                new Command<OptionGroup, Boolean>() {
                    @Override
                    public void execute(OptionGroup og, Boolean value,
                            Object data) {
                        og.setHtmlContentAllowed(value.booleanValue());
                    }
                });
        createIconToggle("Item icons");
    }

    private void createIconToggle(String string) {
        LinkedHashMap<String, ThemeResource> options = new LinkedHashMap<>();
        options.put("-", null);
        options.put("16x16", ICON_16_USER_PNG_CACHEABLE);
        options.put("32x32", ICON_32_ATTENTION_PNG_CACHEABLE);
        options.put("64x64", ICON_64_EMAIL_REPLY_PNG_CACHEABLE);

        createSelectAction(string, CATEGORY_DECORATIONS, options,
                options.keySet().iterator().next(),
                new Command<OptionGroup, ThemeResource>() {
                    @Override
                    public void execute(OptionGroup c, ThemeResource icon,
                            Object data) {
                        Collection<?> itemIds = c.getItemIds();
                        for (Object itemId : itemIds) {
                            c.setItemIcon(itemId, icon);
                        }
                    }
                });
    }

    private void createDisabledItemsMultiToggle(String category) {
        for (Object id : getComponent().getItemIds()) {
            createBooleanAction(id.toString() + " - enabled", category, true,
                    enabledItemCommand, id);
        }
    }

    private Command<OptionGroup, Boolean> enabledItemCommand = new Command<OptionGroup, Boolean>() {

        @Override
        public void execute(OptionGroup c, Boolean value, Object data) {
            c.setItemEnabled(data, value);

        }
    };

}
