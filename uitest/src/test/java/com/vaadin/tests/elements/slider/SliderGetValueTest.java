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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.SliderElement;
import com.vaadin.tests.elements.ComponentElementGetValue;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class SliderGetValueTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return ComponentElementGetValue.class;
    }

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void checkSlider() {
        SliderElement pb = $(SliderElement.class).get(0);
        String expected = "" + ComponentElementGetValue.TEST_SLIDER_VALUE;
        String actual = pb.getValue();
        Assert.assertEquals(expected, actual);
    }
}
