package com.vaadin.tests.server;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * @author Vaadin Ltd
 *
 */
public class DeprecatedTest {

    @Test
    public void allTypesAreDeprecated() throws URISyntaxException {
        AtomicInteger count = new AtomicInteger(0);

        File testRoot = new File(DeprecatedTest.class.getResource("/").toURI());

        new ClasspathHelper()
                .getVaadinClassesFromClasspath(
                        entry -> entry.contains("compatibility-server")
                                && !testRoot.equals(new File(entry)))
                .forEach(cls -> {
                    count.incrementAndGet();
                    assertNotNull("Class " + cls
                            + " is in compatability package and it's not deprecated",
                            cls.getAnnotation(Deprecated.class));
                });
        assertNotEquals("Total number of checked classes", 0, count.get());
    }

}
