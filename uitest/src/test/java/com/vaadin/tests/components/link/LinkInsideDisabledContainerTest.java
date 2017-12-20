package com.vaadin.tests.components.link;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        assertEquals(1, clicksOnLink());

        clickLink();
        assertEquals(2, clicksOnLink());

    }

    @Test
    public void clickOnEnabledLinkInDisabledContainerShouldNotPerformAction()
            throws InterruptedException {

        disableContainer();

        clickLink();

        assertFalse(isLinkEnabled());
        assertEquals(0, clicksOnLink());
    }

    @Test
    public void linkShouldMaintainDisabledStatusWhenTogglingContainerEnabledStatus()
            throws InterruptedException {

        toggleLinkEnabledStatus();
        clickLink();
        assertFalse(isLinkEnabled());
        assertEquals(0, clicksOnLink());

        disableContainer();
        clickLink();
        assertFalse(isLinkEnabled());
        assertEquals(0, clicksOnLink());

        enableContainer();
        clickLink();
        assertFalse(isLinkEnabled());
        assertEquals(0, clicksOnLink());
    }

    @Test
    public void linkShouldMaintainEnabledStatusWhenTogglingContainerEnabledStatus()
            throws InterruptedException {

        clickLink();
        assertTrue(isLinkEnabled());
        assertEquals(1, clicksOnLink());

        disableContainer();
        clickLink();
        assertFalse(isLinkEnabled());
        assertEquals(1, clicksOnLink());

        enableContainer();
        clickLink();
        assertTrue(isLinkEnabled());
        assertEquals(2, clicksOnLink());
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

    private int clicksOnLink() {
        if (!getLogs().isEmpty()) {
            Matcher m = CLICK_MATCHER.matcher(getLogRow(0));
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            }
        }
        return 0;
    }
}
