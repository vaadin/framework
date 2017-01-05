package com.vaadin.test.defaultwidgetset;

import org.junit.Test;

public abstract class DefaultWidgetSetIT extends AbstractWidgetSetIT {

    @Test
    public void appStartsUserCanInteract() {
        testAppStartsUserCanInteract("com.vaadin.DefaultWidgetSet");
    }
}