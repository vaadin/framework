package com.vaadin.sass;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.vaadin.sass.testcases.scss.ControlDirectives;
import com.vaadin.sass.testcases.scss.Extends;
import com.vaadin.sass.testcases.scss.Functions;
import com.vaadin.sass.testcases.scss.Imports;
import com.vaadin.sass.testcases.scss.Mixins;
import com.vaadin.sass.testcases.scss.NestedProperties;
import com.vaadin.sass.testcases.scss.Nesting;
import com.vaadin.sass.testcases.scss.ParentImports;
import com.vaadin.sass.testcases.scss.ParentSelector;
import com.vaadin.sass.testcases.scss.Variables;
import com.vaadin.sass.tree.ImportNodeTest;

@RunWith(Suite.class)
@SuiteClasses({ ControlDirectives.class, Extends.class, Functions.class,
        ImportNodeTest.class, Imports.class, Mixins.class,
        NestedProperties.class, Nesting.class, ParentImports.class,
        Variables.class, ParentSelector.class })
public class ScssTestSuite {

}
