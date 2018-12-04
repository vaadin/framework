package com.vaadin.tests.components.upload;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public abstract class UploadImmediateButtonWidthTest extends MultiBrowserTest {

    protected abstract String getTheme();

    protected double getButtonWidth(String id) {
        WebElement upload = driver.findElement(By.id(id));
        WebElement button = upload.findElement(By.className("v-button"));

        return button.getSize().getWidth();
    }

    @Override
    protected Class<?> getUIClass() {
        return UploadImmediateButtonWidth.class;
    }

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL(String.format("theme=%s", getTheme()));
    }

    @Test
    public void immediateButtonWithPixelWidth() {
        assertThat(getButtonWidth("upload1"), is(300.0));
    }

    @Test
    public void immediateButtonWithPercentageWidth() {
        assertThat(getButtonWidth("upload2"), is(250.0));
    }
}
