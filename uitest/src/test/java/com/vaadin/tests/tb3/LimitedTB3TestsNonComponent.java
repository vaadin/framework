package com.vaadin.tests.tb3;

import java.io.IOException;

import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.vaadin.tests.tb3.LimitedTB3TestsNonComponent.LimitedTB3TestsNonComponentSuite;

/**
 * Test consisting of all TB3 tests except integration tests and component tests
 * (classes extending AbstractTB3Test, excludes packages
 * com.vaadin.test.integration and com.vaadin.tests.components).
 *
 * @author Vaadin Ltd
 */
@RunWith(LimitedTB3TestsNonComponentSuite.class)
public class LimitedTB3TestsNonComponent {

    public static class LimitedTB3TestsNonComponentSuite extends TB3TestSuite {

        public LimitedTB3TestsNonComponentSuite(Class<?> klass)
                throws InitializationError, IOException {
            super(klass, AbstractTB3Test.class, "com.vaadin.tests",
                    new String[] { "com.vaadin.tests.integration",
                            "com.vaadin.tests.components" });
        }

    }

}
