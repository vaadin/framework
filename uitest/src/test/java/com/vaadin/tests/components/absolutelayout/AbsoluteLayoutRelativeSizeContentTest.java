/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.components.absolutelayout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.IsCloseTo.closeTo;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests how AbsoluteLayout handles relative sized contents.
 * 
 * @author Vaadin Ltd
 */
public class AbsoluteLayoutRelativeSizeContentTest extends MultiBrowserTest {

    @Override
    @Before
    public void setup() throws Exception {
        super.setup();
        openTestURL();

        waitForElementPresent(By.id("comparison-table"));
    };

    @Test
    public void testFullAgainstComparison() {
        WebElement comparison = findElement(By.id("comparison-table"));
        WebElement full = findElement(By.id("full-table"));

        assertThat("Full table should be as wide as comparison table",
                full.getSize().width, is(comparison.getSize().width));
        assertThat("Full table should be as high as comparison table",
                full.getSize().height, is(comparison.getSize().height));
    }

    @Test
    public void testHalfAgainstComparison() {
        WebElement comparison = findElement(By.id("comparison-table"));
        WebElement half = findElement(By.id("half-table"));

        assertThat(
                "Half-sized table should be half as wide as comparison table",
                half.getSize().width, is(comparison.getSize().width / 2));
        assertThat(
                "Half-sized table should be half as high as comparison table",
                half.getSize().height, is(comparison.getSize().height / 2));
    }

    @Test
    public void testHalfWithTinyAgainstComparison() {
        WebElement comparison = findElement(By.id("comparison-table"));
        WebElement half = findElement(By.id("halfwithtiny-table"));

        assertThat(
                "Half-sized table should be half as wide as comparison table even if there are other components in the layout",
                half.getSize().width, is(comparison.getSize().width / 2));
        assertThat(
                "Half-sized table should be half as high as comparison table even if there are other components in the layout",
                half.getSize().height, is(comparison.getSize().height / 2));
    }

    @Test
    public void testHalfAgainstFullLayout() {
        WebElement layout = findElement(By.id("halfinfull-layout"));
        WebElement half = findElement(By.id("halfinfull-table"));

        assertThat("Half-sized table should be half as wide as full layout",
                (double) half.getSize().width,
                closeTo(((double) layout.getSize().width) / 2, 0.5));
        assertThat("Half-sized table should be half as high as full layout",
                (double) half.getSize().height,
                closeTo(((double) layout.getSize().height) / 2, 0.5));
    }

    @Test
    public void testFullOnFixedWithSetLocation() {
        WebElement outer = findElement(By.id("fullonfixed-outer"));
        WebElement inner = findElement(By.id("fullonfixed-inner"));

        assertThat(
                "Inner layout should be as wide as outer layout minus left position",
                inner.getSize().width, is(outer.getSize().width - 100));
        assertThat(
                "Inner layout should be as high as outer layout minus top position",
                inner.getSize().height, is(outer.getSize().height - 50));
    }

    @Test
    public void testFullOnFullWithSetLocation() {
        WebElement outer = findElement(By.id("fullonfull-outer"));
        WebElement inner = findElement(By.id("fullonfull-inner"));

        assertThat(
                "Inner layout should be as wide as outer layout minus left position",
                inner.getSize().width, is(outer.getSize().width - 100));
        assertThat(
                "Inner layout should be as high as outer layout minus top position",
                inner.getSize().height, is(outer.getSize().height - 50));
    }
}
