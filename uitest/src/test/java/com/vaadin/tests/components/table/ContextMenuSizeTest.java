package com.vaadin.tests.components.table;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for context menu position and size.
 *
 * @author Vaadin Ltd
 */
public class ContextMenuSizeTest extends MultiBrowserTest {

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
        WebElement target = findElement(By.className("v-table-cell-wrapper"));

        // Make sure target is visible.
        target.click();

        Actions actions = new Actions(getDriver());
        actions.moveToElement(target, getXOffset(target, 1),
                getYOffset(target, 1)).contextClick();
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
