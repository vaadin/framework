package com.vaadin.tests.design.designroot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.vaadin.tests.server.component.customcomponent.MyPrefilledCustomComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class CustomComponentDesignRootTest {

    @Test
    public void customComponentReadVerticalLayoutDesign() {
        CustomComponentDesignRootForVerticalLayout r = new CustomComponentDesignRootForVerticalLayout();
        // Composition root, should be VerticalLayout
        Component compositionRoot = r.iterator().next();
        assertNotNull(compositionRoot);
        assertEquals(VerticalLayout.class, compositionRoot.getClass());
        assertNotNull(r.ok);
        assertNotNull(r.cancel);
        assertEquals("original", r.preInitializedField.getValue());
    }

    @Test
    public void customComponentReadCustomComponentDesign() {
        CustomComponentDesignRootForMyCustomComponent r = new CustomComponentDesignRootForMyCustomComponent();
        // Composition root, should be MyPrefilledCustomComponent
        Component compositionRoot = r.iterator().next();
        assertNotNull(compositionRoot);
        assertEquals(MyPrefilledCustomComponent.class,
                compositionRoot.getClass());

    }

}
