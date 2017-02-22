/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.data;

import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import com.vaadin.data.util.BeanUtil;
import com.vaadin.data.validator.BeanValidator;

/**
 * @author Vaadin Ltd
 * @see Binder
 * @see HasValue
 *
 * @since 8.0
 */
public class BeanValidationBinder<BEAN> extends Binder<BEAN> {

    private final Class<BEAN> beanType;

    private RequiredFieldConfigurator requiredConfigurator = RequiredFieldConfigurator.DEFAULT;

    /**
     * Creates a new binder that uses reflection based on the provided bean type
     * to resolve bean properties. It assumes that JSR-303 bean validation
     * implementation is present on the classpath. If there is no such
     * implementation available then {@link Binder} class should be used instead
     * (this constructor will throw an exception). Otherwise
     * {@link BeanValidator} is added to each binding that is defined using a
     * property name.
     *
     * @param beanType
     *            the bean type to use, not <code>null</code>
     */
    public BeanValidationBinder(Class<BEAN> beanType) {
        super(beanType);
        if (!BeanUtil.checkBeanValidationAvailable()) {
            throw new IllegalStateException(
                    BeanValidationBinder.class.getSimpleName()
                            + " cannot be used because a JSR-303 Bean Validation "
                            + "implementation not found on the classpath. Use "
                            + Binder.class.getSimpleName() + " instead");
        }
        this.beanType = beanType;
    }

    /**
     * Sets a logic which allows to configure require indicator via
     * {@link HasValue#setRequiredIndicatorVisible(boolean)} based on property
     * descriptor.
     * <p>
     * Required indicator configuration will not be used at all if
     * {@code configurator} is null.
     * <p>
     * By default the {@link RequiredFieldConfigurator#DEFAULT} configurator is
     * used.
     * 
     * @param configurator
     *            required indicator configurator, may be {@code null}
     */
    public void setRequiredConfigurator(
            RequiredFieldConfigurator configurator) {
        requiredConfigurator = configurator;
    }

    /**
     * Gets field required indicator configuration logic.
     * 
     * @see #setRequiredConfigurator(RequiredFieldConfigurator)
     * 
     * @return required indicator configurator, may be {@code null}
     */
    public RequiredFieldConfigurator getRequiredConfigurator() {
        return requiredConfigurator;
    }

    @Override
    protected BindingBuilder<BEAN, ?> configureBinding(
            BindingBuilder<BEAN, ?> binding,
            PropertyDefinition<BEAN, ?> definition) {
        BeanValidator validator = new BeanValidator(beanType,
                definition.getName());
        if (requiredConfigurator != null) {
            configureRequired(binding, definition, validator);
        }
        return binding.withValidator(validator);
    }

    private void configureRequired(BindingBuilder<BEAN, ?> binding,
            PropertyDefinition<BEAN, ?> definition, BeanValidator validator) {
        assert requiredConfigurator != null;
        BeanDescriptor descriptor = validator.getJavaxBeanValidator()
                .getConstraintsForClass(beanType);
        PropertyDescriptor propertyDescriptor = descriptor
                .getConstraintsForProperty(definition.getName());
        if (propertyDescriptor == null) {
            return;
        }
        if (propertyDescriptor.getConstraintDescriptors().stream()
                .map(ConstraintDescriptor::getAnnotation)
                .anyMatch(requiredConfigurator)) {
            binding.getField().setRequiredIndicatorVisible(true);
        }
    }

}
