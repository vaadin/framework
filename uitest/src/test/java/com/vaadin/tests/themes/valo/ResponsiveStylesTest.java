package com.vaadin.tests.themes.valo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for the built-in reponsive ("RWD") styles in Valo.
 */
public class ResponsiveStylesTest extends MultiBrowserTest {

    /**
     * Use this parameter to test the collapsed menu state.
     */
    public static final String COLLAPSED_MENU_TEST_PARAM = "collapsed";

    private static final String MENU_STYLENAME = "valo-menu";
    private static final int NARROW_ELEMENT_INDEX = 0;
    private static final int NARROW_WIDTH = 112;
    private static final int WIDE_ELEMENT_INDEX = 1;
    private static final int WIDE_WIDTH = 146;

    private static final String TOGGLE_STYLENAME = "valo-menu-toggle";

    /**
     * Tests that valo-menu-responsive can be used in any element on the page,
     * not just as top-level component.
     *
     * @throws Exception
     */
    @Test
    public void testValoMenuResponsiveParentSize() throws Exception {
        openTestURL();
        waitUntilLoadingIndicatorNotVisible();

        List<WebElement> menus = findElements(
                com.vaadin.testbench.By.className(MENU_STYLENAME));

        WebElement narrowMenu = menus.get(NARROW_ELEMENT_INDEX);
        int narrowWidth = narrowMenu.getSize().width;
        assertThat(narrowWidth, equalTo(NARROW_WIDTH));

        WebElement wideMenu = menus.get(WIDE_ELEMENT_INDEX);
        int wideWidth = wideMenu.getSize().width;
        assertThat((double) wideWidth, closeTo(WIDE_WIDTH, 1));
        sleep(300);

        compareScreen("defaultMenuWidths");
    }

    /**
     * Tests that the valo-menu-hover style makes the menu appear on mouseover.
     *
     * @throws Exception
     */
    @Test
    public void testValoMenuResponsiveHover() throws Exception {
        openTestURL(COLLAPSED_MENU_TEST_PARAM);
        waitUntilLoadingIndicatorNotVisible();

        // Make sure mouse is not hovering the menu
        new Actions(getDriver()).moveToElement($(LabelElement.class).first())
                .moveByOffset(0, 300).perform();

        compareScreen("collapsedMenu");

        List<WebElement> toggles = findElements(
                com.vaadin.testbench.By.className(TOGGLE_STYLENAME));

        // Only one menu in the collapsed example
        WebElement toggle = toggles.get(0);

        Actions actions = new Actions(getDriver());
        actions.moveToElement(toggle);
        actions.perform();
        sleep(200);

        Parameters.setScreenshotComparisonTolerance(0.1);
        compareScreen("expandedMenu");
    }
}
