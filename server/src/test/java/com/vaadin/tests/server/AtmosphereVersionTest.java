package com.vaadin.tests.server;

import junit.framework.TestCase;

import org.atmosphere.util.Version;

import com.vaadin.server.Constants;

public class AtmosphereVersionTest extends TestCase {
    /**
     * Test that the atmosphere version constant matches the version on our
     * classpath
     */
    public void testAtmosphereVersion() {
        assertEquals(Constants.REQUIRED_ATMOSPHERE_RUNTIME_VERSION,
                Version.getRawVersion());
    }
}
