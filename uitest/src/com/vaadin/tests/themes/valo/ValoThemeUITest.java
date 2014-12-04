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
package com.vaadin.tests.themes.valo;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.CssLayoutElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TreeElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ValoThemeUITest extends MultiBrowserTest {

    @Test
    public void labels() throws Exception {
        openTestURL("test");
        open("Labels");
        compareScreen("labels");
    }

    @Test
    public void buttonsLinks() throws Exception {
        openTestURL("test");
        open("Buttons & Links", "Buttons");
        compareScreen("buttonsLinks_with_disabled");
    }

    @Test
    public void textFields() throws Exception {
        openTestURL("test");
        open("Text Fields <span class=\"valo-menu-badge\">123</span>",
                "Text Fields");
        compareScreen("textFields");
    }

    @Test
    public void common() throws Exception {
        openTestURL("test");
        open("Common UI Elements");
        compareScreen("common");
    }

    @Test
    public void datefields() throws Exception {
        openTestURL("test");
        open("Date Fields");
        // Note that this can look broken in IE9 because of some browser
        // rendering issue... The problem seems to be in the customized
        // horizontal layout in the test app
        compareScreen("datefields-with-range");
    }

    @Test
    public void comboboxes() throws Exception {
        openTestURL("test");
        open("Combo Boxes");
        compareScreen("comboboxes");
    }

    @Test
    public void selects() throws Exception {
        openTestURL("test");
        open("Selects");
        compareScreen("selects");
    }

    @Test
    public void checkboxes() throws Exception {
        openTestURL("test");
        open("Check Boxes & Option Groups", "Check Boxes");
        compareScreen("checkboxes_with_disabled");
    }

    @Test
    public void sliders() throws Exception {
        openTestURL("test");
        open("Sliders & Progress Bars", "Sliders");
        compareScreen("sliders");
    }

    @Test
    public void colorpickers() throws Exception {
        openTestURL("test");
        open("Color Pickers");
        compareScreen("colorpickers");
    }

    @Test
    public void menubars() throws Exception {
        openTestURL("test");
        open("Menu Bars");
        compareScreen("menubars");
    }

    @Test
    public void trees() throws Exception {
        openTestURL("test");
        open("Trees");
        selectTreeNodeByCaption("Quid securi");
        compareScreen("trees");
    }

    private void selectTreeNodeByCaption(String string) {
        WebElement e = $(TreeElement.class).first().findElement(
                By.xpath("//div[@class='v-tree-node-caption']//span[text()='"
                        + string + "']"));
        e.click();
    }

    @Test
    public void tables() throws Exception {
        openTestURL("test");
        open("Tables");
        check("Components in Cells");
        compareScreen("tables");
    }

    @Test
    public void treeTables() throws Exception {
        openTestURL("test");
        open("Tables");
        check("Hierarchical");
        check("Footer");
        compareScreen("treetables");
    }

    @Test
    public void dragging() throws Exception {
        openTestURL("test");
        open("Drag and Drop", "Dragging Components");
        compareScreen("dragging");
    }

    @Test
    public void panels() throws Exception {
        openTestURL("test");
        open("Panels", "Panels & Layout panels");
        compareScreen("panels");
    }

    @Test
    public void splitpanels() throws Exception {
        openTestURL("test");
        open("Split Panels");
        compareScreen("splitpanels");
    }

    @Test
    public void tabs() throws Exception {
        openTestURL("test");
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        compareScreen("tabs");

    }

    @Test
    public void tabsClosable() throws Exception {
        openTestURL("test");
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Closable");
        check("Disable tabs");
        check("Overflow");
        compareScreen("tabs-closable-disabled");
    }

    @Test
    public void tabsClosableUnframed() throws Exception {
        openTestURL("test");
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Closable");
        // Framed option is checked by default so we are actually unchecking
        check("Framed");
        check("Overflow");
        compareScreen("tabs-closable-unframed");
    }

    @Test
    public void tabsAlignRight() throws Exception {
        openTestURL("test");
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Right-aligned tabs");
        compareScreen("tabs-align-right");
    }

    /**
     * workaround for http://dev.vaadin.com/ticket/13763
     */
    private void check(String caption) {
        WebElement cb = $(CheckBoxElement.class).caption(caption).first()
                .findElement(By.xpath("input"));
        if (BrowserUtil.isChrome(getDesiredCapabilities())) {
            testBenchElement(cb).click(0, 0);
        } else {
            cb.click();
        }
    }

    @Test
    public void tabsAlignCenter() throws Exception {
        openTestURL("test");
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Centered tabs");
        compareScreen("tabs-align-center");
    }

    @Test
    public void tabsIconsOnTop() throws Exception {
        openTestURL("test");
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Icons on top");
        compareScreen("tabs-icons-on-top");
    }

    @Test
    public void tabsEqualCompactPadded() throws Exception {
        openTestURL("test");
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Equal-width tabs");
        check("Padded tabbar");
        check("Compact");
        compareScreen("tabs-equal-compact-padded");
    }

    @Test
    public void accordions() throws Exception {
        openTestURL("test");
        open("Accordions");
        compareScreen("accordions");
    }

    @Test
    public void popupviews() throws Exception {
        openTestURL("test");
        open("Popup Views");
        scrollTo(500, 0);
        compareScreen("popupviews");
    }

    @Test
    public void calendar() throws Exception {
        openTestURL("test");
        scrollTo(500, 0);
        open("Calendar");

        compareScreen("calendar");
    }

    @Test
    public void forms() throws Exception {
        openTestURL("test");
        scrollTo(500, 0);
        open("Forms");
        compareScreen("forms");
    }

    private void open(String link) {
        open(link, link);
    }

    private void open(String link, String caption) {
        open(link, caption, 10);
    }

    // FIXME: Remove this once click works properly on IE...
    private void open(String link, String caption, int tries) {
        if (tries <= 0) {
            throw new RuntimeException(
                    "Tried many times but was not able to click the link...");
        }

        $(ButtonElement.class).caption(link).first().click();
        CssLayoutElement content = wrap(CssLayoutElement.class,
                findElement(By.className("valo-content")));
        LabelElement captionElem = content.$(LabelElement.class).first();
        if (!captionElem.getText().equals(caption)) {
            // IE ... why you fail clicks
            System.err.println("Extra click needed on '" + link
                    + "' on remote " + getDesiredCapabilities() + " "
                    + getRemoteControlName());

            open(link, caption, tries - 1);
        } else {
            // Done opening, scroll left panel to the top again for consistency
            scrollTo(0, 0);
        }
    }

    private void scrollTo(int top, int left) {
        CssLayoutElement testMenu = $(CssLayoutElement.class).id("testMenu");

        testBenchElement(testMenu).scroll(top);
        testBenchElement(testMenu).scrollLeft(left);
    }

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Override
    protected boolean usePersistentHoverForIE() {
        return false;
    }

}
