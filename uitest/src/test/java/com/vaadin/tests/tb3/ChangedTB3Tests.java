package com.vaadin.tests.tb3;

import java.io.IOException;

import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.vaadin.tests.tb3.ChangedTB3Tests.ChangedTB3TestsSuite;

/**
 * Test suite that runs tests from test classes which have changes in the
 * current workspace. If there are no changes in the workspace, it will run the
 * changes to test classes introduced in the HEAD commit.
 *
 * @author Vaadin Ltd
 */
@RunWith(ChangedTB3TestsSuite.class)
public class ChangedTB3Tests {
    public static class ChangedTB3TestsSuite extends TB3TestSuite {
        public ChangedTB3TestsSuite(Class<?> klass)
                throws InitializationError, IOException {
            super(klass, AbstractTB3Test.class, "com.vaadin.tests",
                    new String[] { "com.vaadin.tests.integration" },
                    new ChangedTB3TestLocator());

        }
    }
}
