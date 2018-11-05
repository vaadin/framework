package com.vaadin.tests.widgetset.client;

/**
 * Dummy state bean used just to check that nothing breaks when generating code
 * to serialize beans with properties in abstract superclasses
 */
public class BeanWithAbstractSuperclass extends AbstractSuperclassForBean {
    public String propertyInsubclass;
}
