package com.vaadin.tests.tb3;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker interface for a TB3+ test class which will exclude the test from any
 * test suite which automatically scans for test classes. Mostly useful for long
 * tests which should not be run in every build.
 *
 * @since 7.1.10
 * @author Vaadin Ltd
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ExcludeFromSuite {

}
