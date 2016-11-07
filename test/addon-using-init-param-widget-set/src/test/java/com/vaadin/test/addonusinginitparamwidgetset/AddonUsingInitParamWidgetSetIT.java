package com.vaadin.test.addonusinginitparamwidgetset;

import org.junit.Test;

import com.vaadin.test.defaultwidgetset.AbstractWidgetSetIT;

public class AddonUsingInitParamWidgetSetIT extends AbstractWidgetSetIT {

    @Test
    public void appStartsUserCanInteract() {
        testAppStartsUserCanInteract("com.vaadin.DefaultWidgetSet", true);
        assertHasDebugMessage(
                "does not contain an implementation for com.vaadin.addon.contextmenu.ContextMenu");
    }

}
