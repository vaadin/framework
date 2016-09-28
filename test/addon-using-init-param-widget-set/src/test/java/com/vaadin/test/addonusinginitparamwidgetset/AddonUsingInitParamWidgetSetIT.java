package com.vaadin.test.addonusinginitparamwidgetset;

import org.junit.Test;

import com.vaadin.test.defaultwidgetset.AbstractWidgetSetIT;

public class AddonUsingInitParamWidgetSetIT extends AbstractWidgetSetIT {

    @Test
    public void appStartsUserCanInteract() {
        testAppStartsUserCanInteract("com.vaadin.DefaultWidgetSet");
        assertUnknownComponentShown("com.vaadin.addon.contextmenu.ContextMenu");
    }

}