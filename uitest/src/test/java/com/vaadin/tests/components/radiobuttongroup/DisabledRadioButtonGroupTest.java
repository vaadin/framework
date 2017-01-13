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
package com.vaadin.tests.components.radiobuttongroup;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class DisabledRadioButtonGroupTest extends MultiBrowserTest {

    @Test
    public void initialDataInDisabledCheckBoxGroup() {
        openTestURL();
        List<String> options = $(RadioButtonGroupElement.class).first()
                .getOptions();
        Assert.assertEquals(3, options.size());
        Assert.assertEquals("a", options.get(0));
        Assert.assertEquals("b", options.get(1));
        Assert.assertEquals("c", options.get(2));
    }

}
