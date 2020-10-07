package com.vaadin.tests.tb3;

import java.io.IOException;

import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.vaadin.tests.tb3.LimitedTB3TestsComponentsNoGrid.LimitedTB3TestsComponentsNoGridSuite;

/**
 * Test consisting of all TB3 tests within package com.vaadin.tests.components
 * except those within package com.vaadin.tests.components.grid (classes
 * extending AbstractTB3Test).
 *
 * @author Vaadin Ltd
 */
@RunWith(LimitedTB3TestsComponentsNoGridSuite.class)
public class LimitedTB3TestsComponentsNoGrid {

    public static class LimitedTB3TestsComponentsNoGridSuite
            extends TB3TestSuite {

        public LimitedTB3TestsComponentsNoGridSuite(Class<?> klass)
                throws InitializationError, IOException {
            super(klass, AbstractTB3Test.class, "com.vaadin.tests.components",
                    new String[] { "com.vaadin.tests.components.grid" });
        }

    }

}
