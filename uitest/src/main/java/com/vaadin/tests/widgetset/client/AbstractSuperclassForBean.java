package com.vaadin.tests.widgetset.client;

import java.io.Serializable;

/**
 * Dummy state bean used just to check that nothing breaks when generating code
 * to serialize beans with properties in abstract superclasses
 */
public abstract class AbstractSuperclassForBean implements Serializable {
    public String propertyInAbstractSuperclass;
}
