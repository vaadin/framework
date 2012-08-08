package com.vaadin.sass;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CssTestSuite.class, ScssTestSuite.class, VisitorTestSuite.class })
public class AllTests {

}
