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

import java.util.stream.IntStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeSelect;

/**
 * @author Vaadin Ltd
 *
 */
public class NativeSelectEmptySelection extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        NativeSelect<String> select = new NativeSelect<>();
        select.setItems(IntStream.range(1, 50)
                .mapToObj(index -> String.valueOf(index)));
        select.setEmptySelectionCaption("empty");
        addComponent(select);

        Button update = new Button("Update Empty Caption to 'updated'",
                event -> select.setEmptySelectionCaption("updated"));

        Button disallow = new Button("Disallow empty selection item",
                event -> select.setEmptySelectionAllowed(false));

        Button enable = new Button("Allow empty selection item",
                event -> select.setEmptySelectionAllowed(true));
        addComponents(update, disallow, enable);
    }

}
