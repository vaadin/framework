package com.vaadin.test.addonusingownwidgetset;

import org.junit.Test;

import com.vaadin.test.defaultwidgetset.AbstractWidgetSetIT;

public class AddonUsingOwnWidgetSetIT extends AbstractWidgetSetIT {

    @Test
    public void appStartsUserCanInteract() {
        testAppStartsUserCanInteract(
                "com.vaadin.test.addonusingownwidgetset.AddonUsingOwnWidgetSet");
        assertNoUnknownComponentShown();
    }
}
