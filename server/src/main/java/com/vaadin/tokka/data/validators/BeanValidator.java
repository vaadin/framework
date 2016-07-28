/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.tokka.data.validators;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator.Context;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;

import com.vaadin.tokka.data.Validator;
import com.vaadin.tokka.data.util.Result;

/**
 * A {@code Validator} using the JSR-303 (javax.validation) annotation-based
 * bean validation mechanism. Values passed to this validator are compared
 * against the constraints, if any, specified by annotations on the
 * corresponding bean property.
 * <p>
 * Note that a JSR-303 implementation (e.g. Hibernate Validator or Apache Bean
 * Validation - formerly agimatec validation) must be present on the project
 * classpath when using bean validation.
 * 
 * @author Petri Hakala
 * @author Vaadin Ltd.
 * 
 * @since
 */
public class BeanValidator implements Validator<Object> {

    private static final long serialVersionUID = 1L;
    private static ValidatorFactory factory;

    private String propertyName;
    private Class<?> beanType;
    private Locale locale;

    /**
     * Creates a new JSR-303 {@code BeanValidator} that validates values of the
     * specified property. Localizes validation messages using the
     * {@linkplain Locale#getDefault() default locale}.
     * 
     * @param beanType
     *            the bean type declaring the property, not null
     * @param propertyName
     *            the property to validate, not null
     */
    public BeanValidator(Class<?> beanType, String propertyName) {
        this(beanType, propertyName, Locale.getDefault());
    }

    /**
     * Creates a new JSR-303 {@code BeanValidator} that validates values of the
     * specified property. Localizes validation messages using the given locale.
     * 
     * @param beanType
     *            the bean class declaring the property, not null
     * @param propertyName
     *            the property to validate, not null
     * @param locale
     *            the locale to use, not null
     */
    public BeanValidator(Class<?> beanType, String propertyName,
            Locale locale) {
        Objects.requireNonNull(beanType, "bean class cannot be null");
        Objects.requireNonNull(propertyName, "property name cannot be null");
        this.beanType = beanType;
        this.propertyName = propertyName;
        setLocale(locale);
    }

    /**
     * Validates the given value as if it were the value of the bean property
     * configured for this validator. Returns {@code Result.ok} if there are no
     * JSR-303 constraint violations, a {@code Result.error} of chained
     * constraint violation messages otherwise.
     * <p>
     * Null values are accepted unless the property has an {@code @NotNull}
     * annotation or equivalent.
     */
    @Override
    public Result<Object> apply(final Object value) {
        Set<? extends ConstraintViolation<?>> violations = getJavaxBeanValidator()
                .validateValue(beanType, propertyName, value);

        return violations.stream()
                .map(v -> Result.error(getMessage(v)))
                .reduce(Result.ok(value), Result::append);
    }

    /**
     * Sets the locale used for validation error messages. Revalidation is not
     * automatically triggered by setting the locale.
     * 
     * @param locale
     *            the locale to use for error messages, not null
     */
    public void setLocale(Locale locale) {
        Objects.requireNonNull(locale, "locale cannot be null");
        this.locale = locale;
    }

    /**
     * Returns the locale used for validation error messages.
     * 
     * @return the locale used for error messages
     */
    public Locale getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return String.format("%s[%s.%s]", getClass().getSimpleName(),
                beanType.getSimpleName(), propertyName);
    }

    /**
     * Returns the underlying JSR-303 bean validator factory used. A factory is
     * created using {@link Validation} if necessary.
     * 
     * @return the validator factory to use
     */
    protected static ValidatorFactory getJavaxBeanValidatorFactory() {
        if (factory == null) {
            factory = Validation.buildDefaultValidatorFactory();
        }
        return factory;
    }

    /**
     * Returns a shared JSR-303 validator instance to use.
     * 
     * @return the validator to use
     */
    protected javax.validation.Validator getJavaxBeanValidator() {
        return getJavaxBeanValidatorFactory().getValidator();
    }

    /**
     * Returns the interpolated error message for the given constraint violation
     * using the locale specified for this validator.
     * 
     * @param v
     *            the constraint violation
     * @return the localized error message
     */
    protected String getMessage(ConstraintViolation<?> v) {
        return getJavaxBeanValidatorFactory().getMessageInterpolator()
                .interpolate(v.getMessageTemplate(), createContext(v), locale);
    }

    /**
     * Creates a simple message interpolation context based on the given
     * constraint violation.
     * 
     * @param v
     *            the constraint violation
     * @return the message interpolation context
     */
    protected Context createContext(ConstraintViolation<?> v) {
        return new Context() {
            @Override
            public ConstraintDescriptor<?> getConstraintDescriptor() {
                return v.getConstraintDescriptor();
            }

            @Override
            public Object getValidatedValue() {
                return v.getInvalidValue();
            }
        };
    }
}
