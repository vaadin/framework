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
package com.vaadin.tests.elements.slider;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Slider;

public class SliderGetHandle extends AbstractTestUI {

    public static final double INITIAL_VALUE = 10.0;

    @Override
    protected void setup(VaadinRequest request) {
        Slider sl1 = new Slider();
        Slider sl2 = new Slider();
        sl2.setValue(INITIAL_VALUE);
        sl1.setWidth("50px");
        sl2.setWidth("50px");
        addComponent(sl1);
        addComponent(sl2);
    }

}
