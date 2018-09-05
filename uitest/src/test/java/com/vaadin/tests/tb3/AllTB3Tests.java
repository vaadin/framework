package com.vaadin.tests.tb3;

import java.io.IOException;

import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.vaadin.tests.tb3.AllTB3Tests.AllTB3TestsSuite;

/**
 * Test consisting of all TB3 tests except integration tests (classes extending
 * AbstractTB3Test, excludes package com.vaadin.test.integration).
 *
 * @author Vaadin Ltd
 */
@RunWith(AllTB3TestsSuite.class)
public class AllTB3Tests {

    public static class AllTB3TestsSuite extends TB3TestSuite {

        public AllTB3TestsSuite(Class<?> klass)
                throws InitializationError, IOException {
            super(klass, AbstractTB3Test.class, "com.vaadin.tests",
                    new String[] { "com.vaadin.tests.integration" });
        }

    }

}
