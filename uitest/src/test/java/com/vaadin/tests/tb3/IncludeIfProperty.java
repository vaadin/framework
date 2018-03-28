package com.vaadin.tests.tb3;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to control inclusion of a test into a test suite.
 * <p>
 * The test will be included in the suite only if the given System property
 * {@code property} has the given {@code value}.
 * <p>
 * Used by {@link TB3TestLocator}
 *
 * @since
 * @author Vaadin Ltd
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface IncludeIfProperty {

    String property();

    String value();

}
