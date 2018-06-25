package com.vaadin.server;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link DefaultDeploymentConfiguration}
 *
 * @author Vaadin Ltd
 * @since 7.2
 */
public class DefaultDeploymentConfigurationTest {

    @Test
    public void testGetSystemPropertyForDefaultPackage()
            throws ClassNotFoundException {
        Class<?> clazz = Class.forName("ClassInDefaultPackage");
        String value = "value";
        String prop = "prop";
        System.setProperty(prop, value);
        DefaultDeploymentConfiguration config = new DefaultDeploymentConfiguration(
                clazz, new Properties());
        Assert.assertEquals(value, config.getSystemProperty(prop));
    }

    @Test
    public void testGetSystemProperty() throws ClassNotFoundException {
        String value = "value";
        String prop = "prop";
        System.setProperty(
                DefaultDeploymentConfigurationTest.class.getPackage().getName()
                        + '.' + prop,
                value);
        DefaultDeploymentConfiguration config = new DefaultDeploymentConfiguration(
                DefaultDeploymentConfigurationTest.class, new Properties());
        Assert.assertEquals(value, config.getSystemProperty(prop));
    }
}
