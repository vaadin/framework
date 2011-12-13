package com.vaadin.data.validator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator.Context;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Field;

/**
 * Vaadin {@link Validator} using the JSR-303 (javax.validation)
 * annotation-based bean validation.
 * 
 * The annotations of the fields of the beans are used to determine the
 * validation to perform.
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
public class BeanValidationValidator implements Validator {

    private static final long serialVersionUID = 1L;
    private static ValidatorFactory factory = Validation
            .buildDefaultValidatorFactory();

    private transient javax.validation.Validator validator;
    private String propertyName;
    private Class<?> beanClass;
    private MethodProperty method;
    private Locale locale;

    /**
     * Creates a Vaadin {@link Validator} utilizing JSR-303 bean validation.
     * 
     * @param beanClass
     *            bean class based on which the validation should be performed
     * @param propertyName
     *            property to validate
     */
    public BeanValidationValidator(Class<?> beanClass, String propertyName) {
        this.beanClass = beanClass;
        this.propertyName = propertyName;
        validator = factory.getValidator();
        try {
            method = new MethodProperty(beanClass.newInstance(), propertyName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Class '" + beanClass
                    + "' must contain default constructor");
        }
        locale = Locale.getDefault();
    }

    /**
     * Apply a bean validation validator to a field based on a bean class and
     * the identifier of the property the field displays. The field is also
     * marked as required if the bean field has the {@link NotNull} annotation.
     * <p>
     * No actual Vaadin validator is added in case no or only {@link NotNull}
     * validation is used (required is practically same as NotNull validation).
     * 
     * @param field
     *            the {@link Field} component to which to add a validator
     * @param propertyId
     *            the property ID of the field of the bean that this field
     *            displays
     * @param beanClass
     *            the class of the bean with the bean validation annotations
     * @return the created validator
     */
    public static BeanValidationValidator addValidator(Field field,
            Object propertyId, Class<?> beanClass) {
        BeanValidationValidator validator = new BeanValidationValidator(
                beanClass, String.valueOf(propertyId));
        PropertyDescriptor constraintsForProperty = validator.validator
                .getConstraintsForClass(beanClass).getConstraintsForProperty(
                        propertyId.toString());
        if (constraintsForProperty != null) {
            int nonNotNullValidators = constraintsForProperty
                    .getConstraintDescriptors().size();
            if (validator.isRequired()) {
                field.setRequired(true);
                field.setRequiredError(validator.getRequiredMessage());
                nonNotNullValidators--;
            }
            if (nonNotNullValidators > 0) {
                field.addValidator(validator);
            }
        }
        return validator;
    }

    public boolean isValid(Object value) {
        try {
            validate(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the property has been marked as required (has the
     * {@link NotNull} annotation.
     * 
     * @return true if the field is marked as not null
     */
    public boolean isRequired() {
        PropertyDescriptor desc = validator.getConstraintsForClass(beanClass)
                .getConstraintsForProperty(propertyName);
        if (desc != null) {
            Iterator<ConstraintDescriptor<?>> it = desc
                    .getConstraintDescriptors().iterator();
            while (it.hasNext()) {
                final ConstraintDescriptor<?> d = it.next();
                Annotation a = d.getAnnotation();
                if (a instanceof NotNull) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public String getRequiredMessage() {
        return getErrorMessage(null, NotNull.class);
    }

    public void validate(final Object value) throws InvalidValueException {
        Object convertedValue = value;
        try {
            convertedValue = convertValue(value);
        } catch (Exception e) {
            String msg = getErrorMessage(value);
            if (msg != null) {
                throw new InvalidValueException(msg);
            } else {
                // there should be always some constraints if conversion is
                // needed
                // for example if String -> Integer then Digits annotation
                throw new InvalidValueException("Conversion exception");
            }
        }
        Set<?> violations = validator.validateValue(beanClass, propertyName,
                convertedValue);
        if (violations.size() > 0) {
            final Object finalValue = convertedValue;
            List<String> exceptions = new ArrayList<String>();
            for (Object v : violations) {
                final ConstraintViolation<?> violation = (ConstraintViolation<?>) v;
                String msg = factory.getMessageInterpolator().interpolate(
                        violation.getMessageTemplate(), new Context() {

                            public ConstraintDescriptor<?> getConstraintDescriptor() {
                                return violation.getConstraintDescriptor();
                            }

                            public Object getValidatedValue() {
                                return finalValue;
                            }

                        }, locale);
                exceptions.add(msg);
            }
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < exceptions.size(); i++) {
                if (i != 0) {
                    b.append("<br/>");
                }
                b.append(exceptions.get(i));
            }
            throw new InvalidValueException(b.toString());
        }
    }

    /**
     * Convert the value the way {@link MethodProperty} does: if the bean field
     * is assignable from the value, return the value directly. Otherwise, try
     * to find a constructor for bean field type that takes a String and call it
     * with value.toString() .
     * 
     * @param value
     *            the value to convert
     * @return converted value, assignable to the field of the bean
     * @throws {@link ConversionException} if no suitable conversion found or
     *         the target type constructor from string threw an exception
     */
    private Object convertValue(Object value)
            throws Property.ConversionException {
        // Try to assign the compatible value directly
        if (value == null
                || method.getType().isAssignableFrom(value.getClass())) {
            return value;
        } else {
            try {
                // Gets the string constructor
                final Constructor constr = method.getType().getConstructor(
                        new Class[] { String.class });

                return constr.newInstance(new Object[] { value.toString() });

            } catch (final java.lang.Exception e) {
                throw new Property.ConversionException(e);
            }
        }
    }

    private String getErrorMessage(final Object value,
            Class<? extends Annotation>... an) {
        BeanDescriptor beanDesc = validator.getConstraintsForClass(beanClass);
        PropertyDescriptor desc = beanDesc
                .getConstraintsForProperty(propertyName);
        if (desc == null) {
            // validate() reports a conversion error in this case
            return null;
        }
        Iterator<ConstraintDescriptor<?>> it = desc.getConstraintDescriptors()
                .iterator();
        List<String> exceptions = new ArrayList<String>();
        while (it.hasNext()) {
            final ConstraintDescriptor<?> d = it.next();
            Annotation a = d.getAnnotation();
            boolean skip = false;
            if (an != null && an.length > 0) {
                skip = true;
                for (Class<? extends Annotation> t : an) {
                    if (t == a.annotationType()) {
                        skip = false;
                        break;
                    }
                }
            }
            if (!skip) {
                String messageTemplate = null;
                try {
                    Method m = a.getClass().getMethod("message");
                    messageTemplate = (String) m.invoke(a);
                } catch (Exception ex) {
                    throw new InvalidValueException(
                            "Annotation must have message attribute");
                }
                String msg = factory.getMessageInterpolator().interpolate(
                        messageTemplate, new Context() {

                            public Object getValidatedValue() {
                                return value;
                            }

                            public ConstraintDescriptor<?> getConstraintDescriptor() {
                                return d;
                            }
                        }, locale);
                exceptions.add(msg);
            }
        }
        if (exceptions.size() > 0) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < exceptions.size(); i++) {
                if (i != 0) {
                    b.append("<br/>");
                }
                b.append(exceptions.get(i));
            }
            return b.toString();
        }
        return null;
    }

    /**
     * Sets the locale used for validation error messages.
     * 
     * Revalidation is not automatically triggered by setting the locale.
     * 
     * @param locale
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Gets the locale used for validation error messages.
     * 
     * @return
     */
    public Locale getLocale() {
        return locale;
    }

    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        validator = factory.getValidator();
    }
}