package com.vaadin.tests.tb3;

import java.io.IOException;

import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.vaadin.tests.tb3.LimitedTB3TestsComponentsGridOnly.LimitedTB3TestsComponentsGridOnlySuite;

/**
 * Test consisting of all TB3 tests within package
 * com.vaadin.tests.components.grid (classes extending AbstractTB3Test).
 *
 * @author Vaadin Ltd
 */
@RunWith(LimitedTB3TestsComponentsGridOnlySuite.class)
public class LimitedTB3TestsComponentsGridOnly {

    public static class LimitedTB3TestsComponentsGridOnlySuite
            extends TB3TestSuite {

        public LimitedTB3TestsComponentsGridOnlySuite(Class<?> klass)
                throws InitializationError, IOException {
            super(klass, AbstractTB3Test.class,
                    "com.vaadin.tests.components.grid", new String[] {});
        }

    }

}
