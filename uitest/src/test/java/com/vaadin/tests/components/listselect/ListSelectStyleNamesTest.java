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
package com.vaadin.tests.components.listselect;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ListSelectStyleNamesTest extends SingleBrowserTest {

    private NativeSelectElement nativeSelect;
    private TestBenchElement nativeSelectSelect;
    private ListSelectElement listSelect;
    private TestBenchElement listSelectSelect;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        nativeSelect = $(NativeSelectElement.class).first();
        nativeSelectSelect = (TestBenchElement) nativeSelect
                .findElement(By.xpath("select"));

        listSelect = $(ListSelectElement.class).first();
        listSelectSelect = (TestBenchElement) listSelect
                .findElement(By.xpath("select"));

    }

    @Test
    public void correctInitialStyleNames() {
        assertStyleNames(nativeSelect, "v-select", "v-widget", "custominitial",
                "v-select-custominitial");
        assertStyleNames(nativeSelectSelect, "v-select-select");
        assertStyleNames(listSelect, "v-select", "v-widget", "custominitial",
                "v-select-custominitial");
        assertStyleNames(listSelectSelect, "v-select-select");
    }

    @Test
    public void addStyleName() {
        $(ButtonElement.class).id("add").click();
        assertStyleNames(nativeSelect, "v-select", "v-widget", "custominitial",
                "v-select-custominitial", "new", "v-select-new");
        assertStyleNames(nativeSelectSelect, "v-select-select");
        assertStyleNames(listSelect, "v-select", "v-widget", "custominitial",
                "v-select-custominitial", "new", "v-select-new");
        assertStyleNames(listSelectSelect, "v-select-select");
    }

    @Test
    public void changePrimaryStyleName() {
        $(ButtonElement.class).id("add").click();
        $(ButtonElement.class).id("changeprimary").click();
        assertStyleNames(nativeSelect, "newprimary", "v-widget",
                "custominitial", "newprimary-custominitial", "new",
                "newprimary-new");
        assertStyleNames(nativeSelectSelect, "newprimary");
        assertStyleNames(listSelect, "newprimary", "v-widget", "custominitial",
                "newprimary-custominitial", "new", "newprimary-new");
        assertStyleNames(listSelectSelect, "newprimary-select");

    }

    private void assertStyleNames(TestBenchElement element,
            String... styleNames) {
        Assert.assertEquals(new HashSet<>(Arrays.asList(styleNames)),
                element.getClassNames());
    }
}
