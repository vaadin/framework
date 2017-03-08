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
package com.vaadin.tests.components.select;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.AbstractLegacyComponent;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.ListSelect;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TwinColSelect;

public class OptionGroupBaseSelects
        extends ComponentTestCase<HorizontalLayout> {

    private HorizontalLayout layout;

    @Override
    protected Class<HorizontalLayout> getTestClass() {
        return HorizontalLayout.class;
    }

    @Override
    protected void initializeComponents() {

        CheckBox cb = new CheckBox("Switch Selects ReadOnly", false);
        cb.addValueChangeListener(event -> {
            for (Component c : layout) {
                if (c instanceof AbstractSelect) {
                    AbstractLegacyComponent legacyComponent = (AbstractLegacyComponent) c;
                    legacyComponent.setReadOnly(!legacyComponent.isReadOnly());
                }
            }
        });
        CheckBox cb2 = new CheckBox("Switch Selects Enabled", true);
        cb2.addValueChangeListener(event -> {
            for (Component c : layout) {
                if (c instanceof AbstractSelect) {
                    boolean enabled = !c.isEnabled();
                    c.setEnabled(enabled);
                    c.setCaption(c.getCaption().replace(
                            (enabled ? "disabled" : "enabled"),
                            (enabled ? "enabled" : "disabled")));
                }
            }
        });
        HorizontalLayout cbs = new HorizontalLayout();
        cbs.setSpacing(true);
        cbs.addComponent(cb);
        cbs.addComponent(cb2);
        addComponent(cbs);

        layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.addComponent(
                createSelect(new ListSelect("List Select, enabled"), true));
        layout.addComponent(
                createSelect(new ListSelect("List Select, disabled"), false));

        layout.addComponent(
                createSelect(new NativeSelect("Native Select, enabled"), true));
        layout.addComponent(createSelect(
                new NativeSelect("Native Select, disabled"), false));

        layout.addComponent(
                createSelect(new OptionGroup("Option Group, enabled"), true));
        layout.addComponent(
                createSelect(new OptionGroup("Option Group, disabled"), false));

        layout.addComponent(createSelect(
                new TwinColSelect("Twin Column Select, enabled"), true));
        layout.addComponent(createSelect(
                new TwinColSelect("Twin Column Select, disabled"), false));

        addTestComponent(layout);

    }

    private AbstractSelect createSelect(AbstractSelect select,
            boolean enabled) {
        select.addContainerProperty(CAPTION, String.class, null);
        for (int i = 0; i < 10; i++) {
            select.addItem("" + i).getItemProperty(CAPTION)
                    .setValue("Item " + i);
            if (select instanceof OptionGroup && i % 2 == 1) {
                ((OptionGroup) select).setItemEnabled("" + i, false);
            }
        }
        select.setEnabled(enabled);
        select.setImmediate(true);
        return select;
    }
}
