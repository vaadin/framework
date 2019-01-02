package com.vaadin.tests.components.menubar;

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertEquals;

public class MenuBarChangeFromEventListenerTest extends MultiBrowserTest {

    @Test
    public void eventFired() {
        openTestURL();

        findElement(By.className("v-menubar-menuitem")).click();
        assertLogRow(0, 1, MenuBarChangeFromEventListener.MENU_CLICKED);
        findElement(By.id("textField")).click();

        findElement(By.className("v-menubar-menuitem")).click();
        sleep(300);
        assertLogRow(1, 2, MenuBarChangeFromEventListener.MENU_CLICKED_BLUR);
        assertLogRow(0, 3, MenuBarChangeFromEventListener.MENU_CLICKED);
    }

    private void assertLogRow(int index, int expentedRowNo,
            String expectedValueWithoutRowNo) {
        assertEquals(expentedRowNo + ". " + expectedValueWithoutRowNo,
                getLogRow(index));
    }

}
