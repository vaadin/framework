package com.vaadin.tests.design.designroot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class DesignRootTest {
    @Test
    public void designAnnotationWithoutFilename() {
        DesignWithEmptyAnnotation d = new DesignWithEmptyAnnotation();
        assertNotNull(d.ok);
        assertNotNull(d.CaNCEL);
        assertEquals("original", d.preInitializedField.getValue());
    }

    @Test
    public void designAnnotationWithFilename() {
        DesignWithAnnotation d = new DesignWithAnnotation();
        assertNotNull(d.ok);
        assertNotNull(d.cancel);
        assertEquals("original", d.preInitializedField.getValue());
    }

    @Test
    public void extendedDesignAnnotationWithoutFilename() {
        DesignWithEmptyAnnotation d = new ExtendedDesignWithEmptyAnnotation();
        assertNotNull(d.ok);
        assertNotNull(d.CaNCEL);
        assertEquals("original", d.preInitializedField.getValue());
    }

    @Test
    public void extendedDesignAnnotationWithFilename() {
        DesignWithAnnotation d = new ExtendedDesignWithAnnotation();
        assertNotNull(d.ok);
        assertNotNull(d.cancel);
        assertEquals("original", d.preInitializedField.getValue());
    }

}
