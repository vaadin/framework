package com.vaadin.tests.tb3;

import java.io.IOException;

import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.vaadin.tests.tb3.AffectedTB3Tests.AffectedTB3TestSuite;

/**
 * Test suite that runs tests from test classes which have changes or have
 * similar package name compare the the changes files in the current workspace.
 * If there are no changes in the workspace, it will run the changes to test
 * classes introduced in the HEAD commit.
 * 
 * @author Vaadin Ltd
 */
@RunWith(AffectedTB3TestSuite.class)
public class AffectedTB3Tests {

    public static class AffectedTB3TestSuite extends TB3TestSuite {

        public AffectedTB3TestSuite(Class<?> klass) throws InitializationError,
                IOException {
            super(klass, AbstractTB3Test.class, "com.vaadin.tests",
                    new String[] { "com.vaadin.tests.integration" },
                    new AffectedTB3TestLocator());
        }
    }
}
