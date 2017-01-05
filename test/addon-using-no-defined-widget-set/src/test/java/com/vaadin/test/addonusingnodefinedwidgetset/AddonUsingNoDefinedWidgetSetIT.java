package com.vaadin.test.addonusingnodefinedwidgetset;

import org.junit.Test;

import com.vaadin.test.defaultwidgetset.AbstractWidgetSetIT;

public class AddonUsingNoDefinedWidgetSetIT extends AbstractWidgetSetIT {

    @Test
    public void appStartsUserCanInteract() {
        testAppStartsUserCanInteract("AppWidgetset");
        assertNoUnknownComponentShown();
    }
}