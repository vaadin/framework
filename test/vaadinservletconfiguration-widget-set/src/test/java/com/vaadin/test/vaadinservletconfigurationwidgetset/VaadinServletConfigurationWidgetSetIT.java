package com.vaadin.test.vaadinservletconfigurationwidgetset;

import org.junit.Test;

import com.vaadin.test.defaultwidgetset.AbstractWidgetSetIT;

public class VaadinServletConfigurationWidgetSetIT extends AbstractWidgetSetIT {

    @Test
    public void appStartsUserCanInteract() {
        testAppStartsUserCanInteract(
                "com.vaadin.test.vaadinservletconfigurationwidgetset.VaadinServletConfigurationWidgetSet");
    }
}
