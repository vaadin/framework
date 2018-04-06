package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.SingleBrowserTest;

@RunLocally(Browser.PHANTOMJS)
public class ComboBoxCaptionAndIconUpdateTest extends SingleBrowserTest {

    @Test
    public void testInitialData() {
        openTestURL();

        assertDisplayValues("fi.gif", "Commit 1");
    }

    @Test
    public void testChangeIconProvider() {
        openTestURL();
        changeIconGenerator();

        assertDisplayValues("m.gif", "Commit 1");
    }

    @Test
    public void testChangeCaptionProvider() {
        openTestURL();
        changeCaptionGenerator();

        assertDisplayValues("fi.gif", "Commit ID 1");
    }

    @Test
    public void testItemAndCaptionProvider() {
        openTestURL();
        changeCaptionGenerator();
        changeIconGenerator();

        assertDisplayValues("m.gif", "Commit ID 1");
    }

    @Test
    public void testEditCaption() {
        openTestURL();
        changeIconGenerator();
        changeCaptionGenerator();
        
        clickButton("editMsg");
        assertDisplayValues("m.gif", "Edited message");
    }

    @Test
    public void testEditIcon() {
        openTestURL();
        changeIconGenerator();
        changeCaptionGenerator();

        clickButton("editIcon");
        assertDisplayValues("fi.gif", "Commit ID 1");
    }

    @Test
    public void testEditIconAndCaption() {
        openTestURL();
        changeIconGenerator();
        changeCaptionGenerator();

        clickButton("editAll");
        assertDisplayValues("fi.gif", "Edited message and icon");

    }

    private void assertDisplayValues(String iconName, String caption) {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        String iconURL = comboBox.findElement(By.tagName("img"))
                .getAttribute("src");
        assertTrue("Icon URL did not end with " + iconName,
                iconURL.endsWith(iconName));
        assertEquals("Caption did not match", caption, comboBox.getValue());
    }

    private void changeIconGenerator() {
        clickButton("icon");
    }

    private void changeCaptionGenerator() {
        clickButton("caption");
    }

    private void clickButton(String id) {
        $(ButtonElement.class).id(id).click();
    }
}
