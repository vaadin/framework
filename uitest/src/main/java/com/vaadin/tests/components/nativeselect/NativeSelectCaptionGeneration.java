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
package com.vaadin.tests.components.nativeselect;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

public class NativeSelectCaptionGeneration extends UI {

    @DesignRoot
    public static class TestComponent extends VerticalLayout {

        HorizontalLayout buttons;
        NativeSelect<String> nativeSelect;

        public TestComponent() {
            Design.read(this);

            // Store the declarative with to string fallback
            ItemCaptionGenerator<String> declarative = nativeSelect
                    .getItemCaptionGenerator();

            buttons.addComponents(
                    new Button("toString",
                            e -> nativeSelect
                                    .setItemCaptionGenerator(String::toString)),
                    new Button("Only number",
                            e -> nativeSelect.setItemCaptionGenerator(
                                    str -> str.substring(7))),
                    new Button("Declarative", e -> nativeSelect
                            .setItemCaptionGenerator(declarative)));
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        setContent(new TestComponent());
    }

}
