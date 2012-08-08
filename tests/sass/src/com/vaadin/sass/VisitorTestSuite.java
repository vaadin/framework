package com.vaadin.sass;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.vaadin.sass.testcases.visitor.MixinVisitorTest;
import com.vaadin.sass.testcases.visitor.NestedPropertiesVisitorTest;

@RunWith(Suite.class)
@SuiteClasses({ NestedPropertiesVisitorTest.class, MixinVisitorTest.class })
public class VisitorTestSuite {

}
