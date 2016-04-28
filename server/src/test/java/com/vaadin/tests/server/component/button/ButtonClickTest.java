package com.vaadin.tests.server.component.button;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.UI;

/**
 * Tests the public click() method.
 */
public class ButtonClickTest {
    private boolean clicked = false;

    @Test
    public void clickDetachedButton() {
        Button b = new Button();
        final ObjectProperty<Integer> counter = new ObjectProperty<Integer>(0);
        b.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                counter.setValue(counter.getValue() + 1);
            }
        });

        b.click();
        Assert.assertEquals(Integer.valueOf(1), counter.getValue());
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

    @Test
    public void testClickReadOnly() {
        Button b = getButton();
        b.setReadOnly(true);
        b.click();
        Assert.assertFalse("Read only button fires click events", clicked);
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
        b.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent ev) {
                clicked = true;
            }
        });
    }
}
