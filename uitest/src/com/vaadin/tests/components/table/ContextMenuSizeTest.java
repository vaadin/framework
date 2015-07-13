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
package com.vaadin.tests.components.table;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for context menu position and size.
 * 
 * @author Vaadin Ltd
 */
public class ContextMenuSizeTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingContextMenu();
    }

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
    }

    private void resizeViewPortHeightTo(int windowHeight) {
        // viewport width doesn't matter, let's use a magic number of 500.
        testBench().resizeViewPortTo(500, windowHeight);
    }

    private int getContextMenuY() {
        int y = openContextMenu().getLocation().getY();

        closeContextMenu();

        return y;
    }

    private int getContextMenuHeight() {
        int height = openContextMenu().getSize().getHeight();

        closeContextMenu();

        return height;
    }

    private WebElement openContextMenu() {
        Actions actions = new Actions(getDriver());
        actions.contextClick(findElement(By.className("v-table-cell-wrapper")));
        actions.perform();
        return findElement(By.className("v-contextmenu"));
    }

    private void closeContextMenu() {
        findElement(By.className("v-app")).click();
    }

    @Test
    public void contextMenuIsResizedToFitWindow() {
        int initialHeight = getContextMenuHeight();

        resizeViewPortHeightTo(initialHeight - 10);
        assertThat(getContextMenuHeight(), lessThan(initialHeight));

        resizeViewPortHeightTo(initialHeight + 100);
        assertThat(getContextMenuHeight(), is(initialHeight));
    }

    @Test
    public void contextMenuPositionIsChangedAfterResize() {
        int height = getContextMenuHeight();
        int y = getContextMenuY();

        resizeViewPortHeightTo(y + height - 10);

        assertThat(getContextMenuY(), lessThan(y));
        assertThat(getContextMenuHeight(), is(height));
    }

}
