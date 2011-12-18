/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.validator;

import java.util.Collection;
import java.util.Locale;

import javax.validation.constraints.NotNull;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;

/**
 * Vaadin {@link Form} using the JSR-303 (javax.validation) declarative bean
 * validation on its fields.
 * 
 * Most validation is performed using {@link BeanValidationValidator}. In
 * addition, fields are automatically marked as required when necessary based on
 * the {@link NotNull} annotations.
 * 
 * If the item is a {@link BeanItem}, the exact type of the bean is used in
 * setting up validators. Otherwise, validation will only be performed based on
 * beanClass, and behavior is undefined for subclasses that have fields not
 * present in the superclass.
 * 
 * Note that a JSR-303 implementation (e.g. Hibernate Validator or agimatec
 * validation) must be present on the project classpath when using bean
 * validation.
 * 
 * @since 7.0
 * 
 * @author Petri Hakala
 * @author Henri Sara
 */
public class BeanValidationForm<T> extends Form {

    private static final long serialVersionUID = 1L;

    private Class<T> beanClass;
    private Locale locale;

    /**
     * Creates a form that performs validation on beans of the type given by
     * beanClass.
     * 
     * Full validation of sub-types of beanClass is performed if the item used
     * is a {@link BeanItem}. Otherwise, the class given to this constructor
     * determines which properties are validated.
     * 
     * @param beanClass
     *            base class of beans for the form
     */
    public BeanValidationForm(Class<T> beanClass) {
        if (beanClass == null) {
            throw new IllegalArgumentException("Bean class cannot be null");
        }
        this.beanClass = beanClass;
    }

    @Override
    public void addField(Object propertyId, Field field) {
        Item item = getItemDataSource();
        Class<? extends T> beanClass = this.beanClass;
        if (item instanceof BeanItem) {
            beanClass = (Class<? extends T>) ((BeanItem<T>) item).getBean()
                    .getClass();
        }
        BeanValidationValidator validator = BeanValidationValidator
                .addValidator(field, propertyId, beanClass);
        if (locale != null) {
            validator.setLocale(locale);
        }
        super.addField(propertyId, field);
    }

    /**
     * Sets the item data source for the form. If the new data source is a
     * {@link BeanItem}, its bean must be assignable to the bean class of the
     * form.
     * 
     * {@inheritDoc}
     */
    @Override
    public void setItemDataSource(Item newDataSource) {
        if ((newDataSource instanceof BeanItem)
                && !beanClass.isAssignableFrom(((BeanItem) newDataSource)
                        .getBean().getClass())) {
            throw new IllegalArgumentException("Bean must be of type "
                    + beanClass.getName());
        }
        super.setItemDataSource(newDataSource);
    }

    /**
     * Sets the item data source for the form. If the new data source is a
     * {@link BeanItem}, its bean must be assignable to the bean class of the
     * form.
     * 
     * {@inheritDoc}
     */
    @Override
    public void setItemDataSource(Item newDataSource, Collection propertyIds) {
        if ((newDataSource instanceof BeanItem)
                && !beanClass.isAssignableFrom(((BeanItem) newDataSource)
                        .getBean().getClass())) {
            throw new IllegalArgumentException("Bean must be of type "
                    + beanClass.getName());
        }
        super.setItemDataSource(newDataSource, propertyIds);
    }

    /**
     * Returns the base class of beans supported by this form.
     * 
     * @return bean class
     */
    public Class<T> getBeanClass() {
        return beanClass;
    }
}