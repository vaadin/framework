package com.vaadin.tests.components.link;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LinkElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class LinkInsideDisabledContainerTest extends MultiBrowserTest {

    private static final Pattern CLICK_MATCHER = Pattern
            .compile(LinkInsideDisabledContainer.CLICK_COUNT_TEXT + "(\\d+)");

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void clickOnEnabledLinkInEnabledContainerShouldPerformAction()
            throws InterruptedException {

        clickLink();
        assertTrue(isLinkEnabled());
        assertThat(clicksOnLink(), is(1));

        clickLink();
        assertThat(clicksOnLink(), is(2));

    }

    @Test
    public void clickOnEnabledLinkInDisabledContainerShouldNotPerformAction()
            throws InterruptedException {

        disableContainer();

        clickLink();

        assertFalse(isLinkEnabled());
        assertThat(clicksOnLink(), is(0));
    }

    @Test
    public void linkShouldMaintainDisabledStatusWhenTogglingContainerEnabledStatus()
            throws InterruptedException {

        toggleLinkEnabledStatus();
        clickLink();
        assertFalse(isLinkEnabled());
        assertThat(clicksOnLink(), is(0));

        disableContainer();
        clickLink();
        assertFalse(isLinkEnabled());
        assertThat(clicksOnLink(), is(0));

        enableContainer();
        clickLink();
        assertFalse(isLinkEnabled());
        assertThat(clicksOnLink(), is(0));

    }

    @Test
    public void linkShouldMaintainEnabledStatusWhenTogglingContainerEnabledStatus()
            throws InterruptedException {

        clickLink();
        assertTrue(isLinkEnabled());
        assertThat(clicksOnLink(), is(1));

        disableContainer();
        clickLink();
        assertFalse(isLinkEnabled());
        assertThat(clicksOnLink(), is(1));

        enableContainer();
        clickLink();
        assertTrue(isLinkEnabled());
        assertThat(clicksOnLink(), is(2));

    }

    private void disableContainer() {
        VerticalLayoutElement container = $(VerticalLayoutElement.class)
                .id("testContainer");
        if (container.isEnabled()) {
            toggleContainerEnabledStatus();
        }
    }

    private void enableContainer() {
        VerticalLayoutElement container = $(VerticalLayoutElement.class)
                .id("testContainer");
        if (!container.isEnabled()) {
            toggleContainerEnabledStatus();
        }
    }

    private void toggleContainerEnabledStatus() {
        ButtonElement button = $(ButtonElement.class).get(1);
        button.click();
    }

    private void toggleLinkEnabledStatus() {
        ButtonElement button = $(ButtonElement.class).get(0);
        button.click();
    }

    protected void clickLink() throws InterruptedException {
        findElement(By.tagName("A")).click();
        sleep(250);
    }

    private boolean isLinkEnabled() {
        return $(LinkElement.class).first().isEnabled();
    }

    private Integer clicksOnLink() {

        if (!getLogs().isEmpty()) {
            Matcher m = CLICK_MATCHER.matcher(getLogRow(0));
            if (m.find()) {
                return Integer.valueOf(m.group(1));
            }
        }
        return 0;
    }
}
