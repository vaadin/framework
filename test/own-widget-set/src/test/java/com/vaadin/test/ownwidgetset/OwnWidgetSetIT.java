package com.vaadin.test.ownwidgetset;

import org.junit.Test;

import com.vaadin.test.defaultwidgetset.AbstractWidgetSetIT;

public class OwnWidgetSetIT extends AbstractWidgetSetIT {

    @Test
    public void appStartsUserCanInteract() {
        testAppStartsUserCanInteract(
                "com.vaadin.test.ownwidgetset.OwnWidgetSet");
    }
}