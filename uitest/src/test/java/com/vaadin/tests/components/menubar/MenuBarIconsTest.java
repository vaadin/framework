package com.vaadin.tests.components.menubar;

import com.vaadin.server.FontAwesome;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assume;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MenuBarIconsTest extends SingleBrowserTest {

    @Test
    public void fontIconsRendered() {
        openTestURL();
        MenuBarElement menu = $(MenuBarElement.class).id("fontIcon");
        WebElement moreItem = menu
                .findElements(By.className("v-menubar-menuitem")).get(3);

        assertFontIcon(FontAwesome.MAIL_REPLY_ALL,
                menu.findElement(By.vaadin("#Main")));
        WebElement hasSubElement = menu.findElement(By.id("fontIcon-3"));
        assertFontIcon(FontAwesome.SUBWAY, hasSubElement);
        assertFontIcon(FontAwesome.ANGELLIST,
                menu.findElement(By.id("fontIcon-5")));

        hasSubElement.click();

        assertFontIcon(FontAwesome.AMBULANCE,
                hasSubElement.findElement(By.id("fontIcon-4")));
        // Close sub menu
        hasSubElement.click();

        assertFontIcon(FontAwesome.MOTORCYCLE, moreItem);

        moreItem.click();
        WebElement filler5 = moreItem.findElement(By.id("fontIcon-10"));
        assertFontIcon(FontAwesome.ANGELLIST, filler5);

    }

    @Test
    public void imageIconsRendered() {
        Assume.assumeFalse(
                "PhantomJS uses different font which shifts index of the 'More' item",
                BrowserUtil.isPhantomJS(getDesiredCapabilities()));

        openTestURL();
        MenuBarElement menu = $(MenuBarElement.class).id("image");
        WebElement moreItem = menu
                .findElements(By.className("v-menubar-menuitem")).get(4);

        String image = "/tests-valo/img/email-reply.png";
        assertImage(image, menu.findElement(By.vaadin("#Main")));
        WebElement hasSubElement = menu.findElement(By.vaadin("#Has sub"));
        assertImage(image, hasSubElement);
        assertImage(image, menu.findElement(By.vaadin("#Filler 0")));

        hasSubElement.click();

        assertImage(image, hasSubElement.findElement(By.vaadin("#Sub item")));
        // Close sub menu
        hasSubElement.click();

        assertImage(image, moreItem);

        moreItem.click();
        waitForElementPresent(By.className("v-menubar-submenu"));
        WebElement filler5 = moreItem.findElement(By.vaadin("#Filler 5"));
        assertImage(image, filler5);

    }

    private void assertImage(String image, WebElement menuItem) {
        WebElement imageElement = menuItem.findElement(By.className("v-icon"));
        assertTrue(imageElement.getAttribute("src").endsWith(image));
    }

    private void assertFontIcon(FontAwesome expected, WebElement menuItem) {
        WebElement mainIcon = menuItem.findElement(By.className("v-icon"));

        assertEquals(expected.getCodepoint(),
                mainIcon.getText().codePointAt(0));

    }
}
