package com.vaadin.tests.components.grid.basicfeatures.escalator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;

public class EscalatorColumnFreezingTest
        extends EscalatorBasicClientFeaturesTest {

    private static final Pattern TRANSFORM_PATTERN = Pattern.compile(// @formatter:off
            // any start of the string
            ".*"

            // non-capturing group for "webkitTransform: " or "transform: "
            + "(?:webkitT|t)ransform: "

            // non-capturing group for "translate" or "translate3d"
            + "translate(?:3d)?"

            // capturing the digits in e.g "(100px,"
            + "\\((\\d+)px,"

            // any end of the string
            + ".*", Pattern.CASE_INSENSITIVE);

            // @formatter:on

    private static final Pattern LEFT_PATTERN = Pattern
            .compile(".*left: (\\d+)px.*", Pattern.CASE_INSENSITIVE);

    private static final int NO_FREEZE = -1;

    @Test
    public void testNoFreeze() {
        openTestURL();
        populate();

        WebElement bodyCell = getBodyCell(0, 0);
        assertFalse(isFrozen(bodyCell));
        assertEquals(NO_FREEZE, getFrozenScrollCompensation(bodyCell));
    }

    @Test
    public void testOneFreeze() {
        openTestURL();
        populate();

        selectMenuPath(FEATURES, FROZEN_COLUMNS, FREEZE_1_COLUMN);
        int scrollPx = 60;
        scrollHorizontallyTo(scrollPx);

        WebElement bodyCell = getBodyCell(0, 0);
        assertTrue(isFrozen(bodyCell));
        assertEquals(scrollPx, getFrozenScrollCompensation(bodyCell));
    }

    @Test
    public void testFreezeToggle() {
        openTestURL();
        populate();

        selectMenuPath(FEATURES, FROZEN_COLUMNS, FREEZE_1_COLUMN);
        scrollHorizontallyTo(100);
        selectMenuPath(FEATURES, FROZEN_COLUMNS, FREEZE_0_COLUMNS);

        WebElement bodyCell = getBodyCell(0, 0);
        assertFalse(isFrozen(bodyCell));
        assertEquals(NO_FREEZE, getFrozenScrollCompensation(bodyCell));
    }

    private static boolean isFrozen(WebElement cell) {
        return cell.getAttribute("class").contains("frozen");
    }

    private static int getFrozenScrollCompensation(WebElement cell) {
        String styleAttribute = cell.getAttribute("style");
        Matcher transformMatcher = TRANSFORM_PATTERN.matcher(styleAttribute);
        Matcher leftMatcher = LEFT_PATTERN.matcher(styleAttribute);

        if (transformMatcher.find()) {
            return Integer.parseInt(transformMatcher.group(1));
        } else if (leftMatcher.find()) {
            return Integer.parseInt(leftMatcher.group(1));
        } else {
            return NO_FREEZE;
        }
    }
}
