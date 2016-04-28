package com.vaadin.tests.themes.valo;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.ModalWindow;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ModalWindowTest extends SingleBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return ModalWindow.class;
    }

    @Test
    public void modalAnimationsAreDisabled() {
        openTestURL("theme=tests-valo-disabled-animations");

        openModalWindow();

        WebElement modalityCurtain = findElement(By
                .className("v-window-modalitycurtain"));

        assertThat(modalityCurtain.getCssValue("-webkit-animation-name"),
                is("none"));
    }

    private void openModalWindow() {
        $(ButtonElement.class).get(1).click();
    }
}