package com.vaadin.tests.server.component.button;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UI;

/**
 * Tests the public click() method.
 */
public class ButtonClickTest {
    private boolean clicked = false;

    @Test
    public void clickDetachedButton() {
        Button b = new Button();
        AtomicInteger counter = new AtomicInteger(0);
        b.addClickListener((ClickEvent event) -> {
            counter.incrementAndGet();
        });

        b.click();
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testClick() {
        getButton().click();
        Assert.assertTrue("Button doesn't fire clicks", clicked);
    }

    @Test
    public void testClickDisabled() {
        Button b = getButton();
        b.setEnabled(false);
        b.click();
        Assert.assertFalse("Disabled button fires click events", clicked);
    }

    private Button getButton() {
        Button b = new Button();
        UI ui = createUI();
        b.setParent(ui);
        addClickListener(b);
        return b;
    }

    private UI createUI() {
        UI ui = new UI() {

            @Override
            protected void init(VaadinRequest request) {
            }
        };
        return ui;
    }

    private void addClickListener(Button b) {
        clicked = false;
        b.addClickListener((ClickEvent ev) -> {
            clicked = true;
        });
    }
}
