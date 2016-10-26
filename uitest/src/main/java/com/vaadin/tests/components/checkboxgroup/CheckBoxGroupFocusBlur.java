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
package com.vaadin.tests.components.checkboxgroup;

import java.util.stream.IntStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.CheckBoxGroup;

/**
 * @author Vaadin Ltd
 *
 */
public class CheckBoxGroupFocusBlur extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        CheckBoxGroup<Integer> group = new CheckBoxGroup<>();
        group.setItems(IntStream.range(1, 10).mapToObj(Integer::valueOf)
                .toArray(Integer[]::new));
        addComponent(group);

        group.addFocusListener(event -> log("Focus Event"));
        group.addBlurListener(event -> log("Blur Event"));
    }

}
