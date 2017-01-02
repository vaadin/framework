package com.vaadin.tests.components.menubar;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.server.FontAwesome;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class MenuBarFontIconTest extends SingleBrowserTest {

    @Test
    public void fontIconsRendered() {
        openTestURL();
        MenuBarElement menu = $(MenuBarElement.class).first();
        WebElement moreItem = menu
                .findElements(By.className("v-menubar-menuitem")).get(3);

        assertIcon(FontAwesome.MAIL_REPLY_ALL,
                menu.findElement(By.vaadin("#Main")));
        WebElement hasSubElement = menu.findElement(By.vaadin("#Has sub"));
        assertIcon(FontAwesome.SUBWAY, hasSubElement);
        assertIcon(FontAwesome.ANGELLIST,
                menu.findElement(By.vaadin("#Filler 0")));

        hasSubElement.click();

        assertIcon(FontAwesome.AMBULANCE,
                hasSubElement.findElement(By.vaadin("#Sub item")));

        assertIcon(FontAwesome.MOTORCYCLE, moreItem);

        moreItem.click();
        WebElement filler5 = moreItem.findElement(By.vaadin("#Filler 5"));
        assertIcon(FontAwesome.ANGELLIST, filler5);

    }

    private void assertIcon(FontAwesome expected, WebElement menuItem) {
        WebElement mainIcon = menuItem.findElement(By.className("v-icon"));

        Assert.assertEquals(expected.getCodepoint(),
                mainIcon.getText().codePointAt(0));

    }
}
