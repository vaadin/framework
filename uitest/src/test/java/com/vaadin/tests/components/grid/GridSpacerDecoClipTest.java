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
package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.minitutorials.v7_5.ShowingExtraDataForRows;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for "Grid detail row outline overflows" (#17826)
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridSpacerDecoClipTest extends MultiBrowserTest {

    private static final String SPACER_CSS_CLASS_DECO = "v-grid-spacer-deco";

    @Override
    protected Class<?> getUIClass() {
        return ShowingExtraDataForRows.class;
    }

    @Test
    public void testNewSpacerClip() {
        openTestURL();

        GridElement gridElement = $(GridElement.class).first();
        gridElement.scrollToRow(999);
        GridElement.GridRowElement nextToLastRow = gridElement.getRow(998);
        nextToLastRow.doubleClick();

        TestBenchElement deco = getSpacerDeco(0);
        System.out.println("Lower deco.clip = " + deco.getCssValue("clip"));
        GridElement.GridRowElement nearToBottomRow = gridElement.getRow(993);
        nearToBottomRow.doubleClick();
        deco = getSpacerDeco(0);
        System.out.println("Lower deco.clip = " + deco.getCssValue("clip"));
        Assert.assertNotEquals(
                "Spacer deco clipping is not updated after opening another spacer",
                "auto", deco.getCssValue("clip"));
    }

    @Test
    public void testRemovedSpacerClip() throws InterruptedException {
        openTestURL();

        GridElement gridElement = $(GridElement.class).first();
        gridElement.scrollToRow(999);
        GridElement.GridRowElement lastRow = gridElement.getRow(999);
        lastRow.doubleClick(); // Open lowest Row Details

        TestBenchElement deco = getSpacerDeco(0);
        System.out.println("deco.rect = " + deco.getCssValue("clip"));

        GridElement.GridRowElement nearToBottomRow = gridElement.getRow(993);

        nearToBottomRow.doubleClick(); // Open upper Row Details, lower Row
                                       // Details goes out of visible range
        Thread.sleep(500);
        nearToBottomRow.doubleClick(); // Close upper Row Details, lower Row
                                       // Details goes back to visible range

        deco = getSpacerDeco(0);
        String clip = deco.getCssValue("clip");
        System.out.println("deco.rect = " + clip);

        Assert.assertTrue(
                "Part of lower Row Details is visible, its deco clip height should be positive, but it is negative",
                clip.indexOf('-') < 0);
    }

    private TestBenchElement getSpacerDeco(int index) {
        SearchContext context = getContext();
        return (TestBenchElement) context.findElements(
                By.className(SPACER_CSS_CLASS_DECO)).get(index);
    }
}
