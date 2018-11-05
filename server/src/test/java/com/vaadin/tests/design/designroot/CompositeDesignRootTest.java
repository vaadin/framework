package com.vaadin.tests.design.designroot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.vaadin.tests.server.component.composite.MyPrefilledComposite;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class CompositeDesignRootTest {

    @Test
    public void compositeReadVerticalLayoutDesign() {
        CompositeDesignRootForVerticalLayout r = new CompositeDesignRootForVerticalLayout();
        // Composition root, should be VerticalLayout
        Component compositionRoot = r.iterator().next();
        assertNotNull(compositionRoot);
        assertEquals(VerticalLayout.class, compositionRoot.getClass());
        assertNotNull(r.ok);
        assertNotNull(r.cancel);
        assertEquals("original", r.preInitializedField.getValue());
    }

    @Test
    public void compositeReadCompositeDesign() {
        CompositeDesignRootForMyComposite r = new CompositeDesignRootForMyComposite();
        // Composition root, should be MyPrefilledcomposite
        Component compositionRoot = r.iterator().next();
        assertNotNull(compositionRoot);
        assertEquals(MyPrefilledComposite.class, compositionRoot.getClass());

    }

}
