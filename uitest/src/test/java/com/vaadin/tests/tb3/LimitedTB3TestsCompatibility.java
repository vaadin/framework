package com.vaadin.tests.tb3;

import java.io.IOException;

import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.vaadin.tests.tb3.LimitedTB3TestsCompatibility.LimitedTB3TestsCompatibilitySuite;

/**
 * Test consisting of all TB3 tests within the compatibility package
 * com.vaadin.v7.tests (classes extending AbstractTB3Test).
 *
 * @author Vaadin Ltd
 */
@RunWith(LimitedTB3TestsCompatibilitySuite.class)
public class LimitedTB3TestsCompatibility {

    public static class LimitedTB3TestsCompatibilitySuite extends TB3TestSuite {

        public LimitedTB3TestsCompatibilitySuite(Class<?> klass)
                throws InitializationError, IOException {
            super(klass, AbstractTB3Test.class, "com.vaadin.v7.tests",
                    new String[] {});
        }

    }

}
