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
package com.vaadin.tests.elements.datefield;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PopupDateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldPopupSetValueTest extends MultiBrowserTest {

    LabelElement counter;
    PopupDateFieldElement dfPopup;
    DateFieldElement df;

    @Before
    public void init() {
        openTestURL();
        counter = $(LabelElement.class).id("counter");
        df = $(DateFieldElement.class).first();
    }

    @Test
    public void testGetValue() {
        String value = df.getValue();
        Assert.assertEquals("04/12/15", value);
    }

    @Test
    public void testSetValue() {
        Date date = DateFieldPopupSetValue.changedDate;
        String value = (new SimpleDateFormat("MM/dd/yy")).format(date);
        df.setValue(value);
        Assert.assertEquals("06/11/15", df.getValue());
    }

    @Test
    public void testValueChanged() {
        Date date = DateFieldPopupSetValue.changedDate;
        String value = (new SimpleDateFormat("MM/dd/yy")).format(date);
        df.setValue(value);
        counter.waitForVaadin();
        Assert.assertEquals("1", counter.getText());
    }
}
