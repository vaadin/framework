package com.vaadin.tests.components.menubar;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.server.FontAwesome;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class MenuBarIconsTest extends SingleBrowserTest {

    @Test
    public void fontIconsRendered() {
        openTestURL();
        MenuBarElement menu = $(MenuBarElement.class).id("fontIcon");
        WebElement moreItem = menu
                .findElements(By.className("v-menubar-menuitem")).get(3);

        assertFontIcon(FontAwesome.MAIL_REPLY_ALL,
                menu.findElement(By.vaadin("#Main")));
        WebElement hasSubElement = menu.findElement(By.vaadin("#Has sub"));
        assertFontIcon(FontAwesome.SUBWAY, hasSubElement);
        assertFontIcon(FontAwesome.ANGELLIST,
                menu.findElement(By.vaadin("#Filler 0")));

        hasSubElement.click();

        assertFontIcon(FontAwesome.AMBULANCE,
                hasSubElement.findElement(By.vaadin("#Sub item")));
        // Close sub menu
        hasSubElement.click();

        assertFontIcon(FontAwesome.MOTORCYCLE, moreItem);

        moreItem.click();
        WebElement filler5 = moreItem.findElement(By.vaadin("#Filler 5"));
        assertFontIcon(FontAwesome.ANGELLIST, filler5);

    }

    @Test
    public void imageIconsRendered() {
        openTestURL();
        MenuBarElement menu = $(MenuBarElement.class).id("image");
        WebElement moreItem = menu
                .findElements(By.className("v-menubar-menuitem")).get(3);

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
        WebElement filler5 = moreItem.findElement(By.vaadin("#Filler 5"));
        assertImage(image, filler5);

    }

    private void assertImage(String image, WebElement menuItem) {
        WebElement imageElement = menuItem.findElement(By.className("v-icon"));
        Assert.assertTrue(imageElement.getAttribute("src").endsWith(image));
    }

    private void assertFontIcon(FontAwesome expected, WebElement menuItem) {
        WebElement mainIcon = menuItem.findElement(By.className("v-icon"));

        Assert.assertEquals(expected.getCodepoint(),
                mainIcon.getText().codePointAt(0));

    }
}
