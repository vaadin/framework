package com.vaadin.tests.components.menubar;

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MenuBarChangeFromEventListenerTest extends MultiBrowserTest {

    @Test
    public void eventFired() {
        openTestURL();

        findElement(By.className("v-menubar-menuitem")).click();
        assertEquals(1,findElements(By.className("menuClickedLabel")).size());
        findElement(By.id("textField")).click();

        findElement(By.className("v-menubar-menuitem")).click();
        try {
            sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(1,findElements(By.className("blurListenerLabel")).size());
        assertEquals(2,findElements(By.className("menuClickedLabel")).size());
    }
}
