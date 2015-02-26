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
package com.vaadin.tests.components.grid.basicfeatures.escalator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.client.WidgetUtil;
import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;

@SuppressWarnings("boxing")
public class EscalatorSpacerTest extends EscalatorBasicClientFeaturesTest {

    //@formatter:off
    // separate strings made so that eclipse can show the concatenated string by hovering the mouse over the constant
    
    // translate3d(0px, 40px, 123px);
    // translate3d(24px, 15.251px, 0);
    // translate(0, 40px);
    private final static String TRANSLATE_VALUE_REGEX = 
            "translate(?:3d|)" // "translate" or "translate3d"
            + "\\(" // literal "("
                + ".+?, " // the x argument, uninteresting
                + "(" // start capturing the y argument
                    + "[0-9]+" // the integer part of the value
                    + "(?:" // start of the subpixel part of the value
                        + "\\.[0-9]" // if we have a period, there must be at least one number after it
                        + "[0-9]*" // any amount of accuracy afterwards is fine
                    + ")?" // the subpixel part is optional
                + ")"
                + "(?:px)?" // we don't care if the values are suffixed by "px" or not.
                + "(?:, .*?)?" // the possible z argument, uninteresting (translate doesn't have one, translate3d does)
                + "\\)" // literal ")"
                + ";?"; // optional ending semicolon
    
    // 40px;
    // 12.34px
    private final static String TOP_VALUE_REGEX =
            "(" // capture the pixel value
                + "[0-9]+" // the pixel argument
                + "(?:" // start of the subpixel part of the value
                    + "\\.[0-9]" // if we have a period, there must be at least one number after it
                    + "[0-9]*" // any amount of accuracy afterwards is fine
                + ")?" // the subpixel part is optional
            + ")"
            + "(?:px)?" // optional "px" string
            + ";?"; // optional semicolon
    //@formatter:on

    private final static Pattern TRANSFORM_CSS_PATTERN = Pattern
            .compile("transform: (.*?);"); // also matches "-webkit-transform";
    private final static Pattern TOP_CSS_PATTERN = Pattern
            .compile("top: (.*?);");

    private final static Pattern TRANSLATE_VALUE_PATTERN = Pattern
            .compile(TRANSLATE_VALUE_REGEX);
    private final static Pattern TOP_VALUE_PATTERN = Pattern
            .compile(TOP_VALUE_REGEX);

    @Before
    public void before() {
        openTestURL();
        populate();
    }

    @Test
    public void openVisibleSpacer() {
        assertNull("No spacers should be shown at the start", getSpacer(1));
        selectMenuPath(FEATURES, SPACERS, ROW_1, SET_100PX);
        assertNotNull("Spacer should be shown after setting it", getSpacer(1));
    }

    @Test
    public void closeVisibleSpacer() {
        selectMenuPath(FEATURES, SPACERS, ROW_1, SET_100PX);
        selectMenuPath(FEATURES, SPACERS, ROW_1, REMOVE);
        assertNull("Spacer should not exist after removing it", getSpacer(1));
    }

    @Test
    public void spacerPushesVisibleRowsDown() {
        double oldTop = getElementTop(getBodyRow(2));
        selectMenuPath(FEATURES, SPACERS, ROW_1, SET_100PX);
        double newTop = getElementTop(getBodyRow(2));

        assertGreater("Row below a spacer was not pushed down", newTop, oldTop);
    }

    @Test
    public void addingRowAboveSpacerPushesItDown() {
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, REMOVE_ALL_ROWS);
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, ADD_ONE_ROW_TO_BEGINNING);

        selectMenuPath(FEATURES, SPACERS, ROW_1, SET_100PX);
        double oldTop = getElementTop(getSpacer(1));
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, ADD_ONE_ROW_TO_BEGINNING);
        double newTop = getElementTop(getSpacer(1));

        assertGreater("Spacer should've been pushed down", newTop, oldTop);
    }

    @Test
    public void addingRowBelowSpacerDoesNotPushItDown() {
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, REMOVE_ALL_ROWS);
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, ADD_ONE_ROW_TO_BEGINNING);

        selectMenuPath(FEATURES, SPACERS, ROW_1, SET_100PX);
        double oldTop = getElementTop(getSpacer(1));
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, ADD_ONE_ROW_TO_END);
        double newTop = getElementTop(getSpacer(1));

        assertEquals("Spacer should've not been pushed down", newTop, oldTop,
                WidgetUtil.PIXEL_EPSILON);
    }

    @Test
    public void addingRowBelowSpacerIsActuallyRenderedBelowWhenEscalatorIsEmpty() {
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, REMOVE_ALL_ROWS);
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, ADD_ONE_ROW_TO_BEGINNING);

        selectMenuPath(FEATURES, SPACERS, ROW_1, SET_100PX);
        double spacerTop = getElementTop(getSpacer(1));
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, ADD_ONE_ROW_TO_END);
        double rowTop = getElementTop(getBodyRow(2));

        assertEquals("Next row should've been rendered below the spacer",
                spacerTop + 100, rowTop, WidgetUtil.PIXEL_EPSILON);
    }

    private static double getElementTop(WebElement element) {
        /*
         * we need to parse the style attribute, since using getCssValue gets a
         * normalized value that is harder to parse.
         */
        String style = element.getAttribute("style");

        String transform = getTransformFromStyle(style);
        if (transform != null) {
            return getTranslateYValue(transform);
        }

        String top = getTopFromStyle(style);
        if (top != null) {
            return getTopValue(top);
        }

        throw new IllegalArgumentException(
                "Could not parse the top position from the CSS \"" + style
                        + "\"");
    }

    private static String getTransformFromStyle(String style) {
        return getFromStyle(TRANSFORM_CSS_PATTERN, style);
    }

    private static String getTopFromStyle(String style) {
        return getFromStyle(TOP_CSS_PATTERN, style);
    }

    private static String getFromStyle(Pattern pattern, String style) {
        Matcher matcher = pattern.matcher(style);
        if (matcher.find()) {
            assertEquals("wrong amount of groups matched in " + style, 1,
                    matcher.groupCount());
            return matcher.group(1);
        } else {
            return null;
        }
    }

    private static double getTranslateYValue(String translate) {
        return getValueFromCss(TRANSLATE_VALUE_PATTERN, translate);
    }

    private static double getTopValue(String top) {
        return getValueFromCss(TOP_VALUE_PATTERN, top);
    }

    private static double getValueFromCss(Pattern pattern, String css) {
        Matcher matcher = pattern.matcher(css);
        assertTrue("no matches for " + css + " against "
                + TRANSLATE_VALUE_PATTERN, matcher.find());
        assertEquals("wrong amount of groups matched in " + css, 1,
                matcher.groupCount());
        return Double.parseDouble(matcher.group(1));
    }
}
