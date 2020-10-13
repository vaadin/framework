package com.vaadin.tests.themes.valo;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.CssLayoutElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.v7.testbench.elements.TreeElement;

public class ValoThemeUITest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL("test");
        // During the URL open process, we have already verified that the UI
        // is correct, so that in this case, we can assume everything is fine
        // as long as the UI is not totally broken
        Parameters.setScreenshotComparisonTolerance(0.1);
    }

    @Test
    public void labels() throws Exception {
        open("Labels");
        compareScreen("labels");
    }

    @Test
    public void buttonsLinks() throws Exception {
        open("Buttons & Links", "Buttons");
        compareScreen("buttonsLinks_with_disabled");
    }

    @Test
    public void textFields() throws Exception {
        open("Text Fields <span class=\"valo-menu-badge\">123</span>",
                "Text Fields");
        compareScreen("textFields");
    }

    @Test
    public void common() throws Exception {
        open("Common UI Elements");
        compareScreen("common");
    }

    @Test
    public void datefields() throws Exception {
        open("Date Fields");
        // Note that this can look broken in IE9 because of some browser
        // rendering issue... The problem seems to be in the customized
        // horizontal layout in the test app
        compareScreen("datefields-localdate-with-range");
    }

    @Test
    public void comboboxes() throws Exception {
        open("Combo Boxes");
        compareScreen("comboboxes");
    }

    @Test
    public void selects() throws Exception {
        open("Selects");
        compareScreen("selects");
    }

    @Test
    public void checkboxes() throws Exception {
        open("Check Boxes & Option Groups", "Check Boxes");
        compareScreen("checkboxes_with_readonly");
    }

    @Test
    public void sliders() throws Exception {
        open("Sliders & Progress Bars", "Sliders");
        compareScreen("sliders");
    }

    @Test
    public void colorpickers() throws Exception {
        open("Color Pickers");
        compareScreen("colorpickers");
    }

    @Test
    public void menubars() throws Exception {
        open("Menu Bars");
        compareScreen("menubars");
    }

    @Test
    public void trees() throws Exception {
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
        open("Tables");
        check("Components in Cells");
        compareScreen("tables");
    }

    @Test
    public void treeTables() throws Exception {
        open("Tables");
        check("Hierarchical");
        check("Footer");
        compareScreen("treetables");
    }

    @Test
    public void dragging() throws Exception {
        open("Drag and Drop", "Dragging Components");
        compareScreen("dragging");
    }

    @Test
    public void panels() throws Exception {
        open("Panels", "Panels & Layout panels");
        compareScreen("panels");
    }

    @Test
    public void splitpanels() throws Exception {
        open("Split Panels");
        compareScreen("splitpanels");
    }

    @Test
    public void tabs() throws Exception {
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        sleep(200);
        compareScreen("tabs");

    }

    @Test
    public void tabsClosable() throws Exception {
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Closable");
        check("Disable tabs");
        check("Overflow");
        sleep(200);
        compareScreen("tabs-closable-disabled");
    }

    @Test
    public void tabsClosableUnframed() throws Exception {
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Closable");
        // Framed option is checked by default so we are actually unchecking
        check("Framed");
        check("Overflow");
        sleep(200);
        compareScreen("tabs-closable-unframed");
    }

    @Test
    public void tabsAlignRight() throws Exception {
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Right-aligned tabs");
        sleep(200);
        compareScreen("tabs-align-right");
    }

    /**
     * workaround for http://dev.vaadin.com/ticket/13763
     */
    private void check(String caption) {
        click($(CheckBoxElement.class).caption(caption).first());
    }

    @Test
    public void tabsAlignCenter() throws Exception {
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Centered tabs");
        sleep(200);
        compareScreen("tabs-align-center");
    }

    @Test
    public void tabsIconsOnTop() throws Exception {
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Icons on top");
        sleep(200);
        compareScreen("tabs-icons-on-top");
    }

    @Test
    public void tabsEqualCompactPadded() throws Exception {
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Equal-width tabs");
        check("Padded tabbar");
        check("Compact");

        compareScreen("tabs-equal-compact-padded");
    }

    @Test
    public void accordions() throws Exception {
        open("Accordions");

        // Screenshot test is very unstable here.
        // We are testing the label contains the correct text in this case.
        CssLayoutElement content = wrap(CssLayoutElement.class,
                findElement(By.className("valo-content")));
        LabelElement labelElem = content.$(LabelElement.class).get(1);
        String text = "Fabio vel iudice vincam, sunt in culpa qui officia. Ut "
                + "enim ad minim veniam, quis nostrud exercitation.";
        Assert.assertEquals(text, labelElem.getText());
    }

    @Test
    public void popupviews() throws Exception {
        open("Popup Views");
        scrollTo(500, 0);
        compareScreen("popupviews");
    }

    @Test
    public void calendar() throws Exception {
        scrollTo(500, 0);
        open("Calendar");

        compareScreen("calendar");
    }

    @Test
    public void forms() throws Exception {
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
        waitUntilLoadingIndicatorNotVisible();
        CssLayoutElement content = wrap(CssLayoutElement.class,
                findElement(By.className("valo-content")));
        LabelElement captionElem = content.$(LabelElement.class).first();
        if (!captionElem.getText().equals(caption)) {
            // IE ... why you fail clicks
            System.err.println("Extra click needed on '" + link + "' on remote "
                    + getDesiredCapabilities() + " " + getRemoteControlName());

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
        waitUntilLoadingIndicatorNotVisible();
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
