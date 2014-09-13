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

package com.vaadin.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.vaadin.data.Buffered;
import com.vaadin.data.Property;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.LegacyPropertyHelper;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.util.converter.ConverterUtil;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.CompositeErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.AbstractFieldState;
import com.vaadin.shared.util.SharedUtil;

/**
 * <p>
 * Abstract field component for implementing buffered property editors. The
 * field may hold an internal value, or it may be connected to any data source
 * that implements the {@link com.vaadin.data.Property}interface.
 * <code>AbstractField</code> implements that interface itself, too, so
 * accessing the Property value represented by it is straightforward.
 * </p>
 * 
 * <p>
 * AbstractField also provides the {@link com.vaadin.data.Buffered} interface
 * for buffering the data source value. By default the Field is in write
 * through-mode and {@link #setWriteThrough(boolean)}should be called to enable
 * buffering.
 * </p>
 * 
 * <p>
 * The class also supports {@link com.vaadin.data.Validator validators} to make
 * sure the value contained in the field is valid.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public abstract class AbstractField<T> extends AbstractComponent implements
        Field<T>, Property.ReadOnlyStatusChangeListener,
        Property.ReadOnlyStatusChangeNotifier, Action.ShortcutNotifier {

    /* Private members */

    /**
     * Value of the abstract field.
     */
    private T value;

    /**
     * A converter used to convert from the data model type to the field type
     * and vice versa.
     */
    private Converter<T, Object> converter = null;
    /**
     * Connected data-source.
     */
    private Property<?> dataSource = null;

    /**
     * The list of validators.
     */
    private LinkedList<Validator> validators = null;

    /**
     * True if field is in buffered mode, false otherwise
     */
    private boolean buffered;

    /**
     * Flag to indicate that the field is currently committing its value to the
     * datasource.
     */
    private boolean committingValueToDataSource = false;

    /**
     * Current source exception.
     */
    private Buffered.SourceException currentBufferedSourceException = null;

    /**
     * Are the invalid values allowed in fields ?
     */
    private boolean invalidAllowed = true;

    /**
     * Are the invalid values committed ?
     */
    private boolean invalidCommitted = false;

    /**
     * The error message for the exception that is thrown when the field is
     * required but empty.
     */
    private String requiredError = "";

    /**
     * The error message that is shown when the field value cannot be converted.
     */
    private String conversionError = "Could not convert value to {0}";

    /**
     * Is automatic validation enabled.
     */
    private boolean validationVisible = true;

    private boolean valueWasModifiedByDataSourceDuringCommit;

    /**
     * Whether this field is currently registered as listening to events from
     * its data source.
     * 
     * @see #setPropertyDataSource(Property)
     * @see #addPropertyListeners()
     * @see #removePropertyListeners()
     */
    private boolean isListeningToPropertyEvents = false;

    /**
     * The locale used when setting the value.
     */
    private Locale valueLocale = null;

    /* Component basics */

    /*
     * Paints the field. Don't add a JavaDoc comment here, we use the default
     * documentation from the implemented interface.
     */

    /**
     * Returns true if the error indicator be hidden when painting the component
     * even when there are errors.
     * 
     * This is a mostly internal method, but can be overridden in subclasses
     * e.g. if the error indicator should also be shown for empty fields in some
     * cases.
     * 
     * @return true to hide the error indicator, false to use the normal logic
     *         to show it when there are errors
     */
    protected boolean shouldHideErrors() {
        // getErrorMessage() can still return something else than null based on
        // validation etc.
        return isRequired() && isEmpty() && getComponentError() == null;
    }

    /**
     * Returns the type of the Field. The methods <code>getValue</code> and
     * <code>setValue</code> must be compatible with this type: one must be able
     * to safely cast the value returned from <code>getValue</code> to the given
     * type and pass any variable assignable to this type as an argument to
     * <code>setValue</code>.
     * 
     * @return the type of the Field
     */
    @Override
    public abstract Class<? extends T> getType();

    /**
     * The abstract field is read only also if the data source is in read only
     * mode.
     */
    @Override
    public boolean isReadOnly() {
        return super.isReadOnly()
                || (dataSource != null && dataSource.isReadOnly());
    }

    /**
     * Changes the readonly state and throw read-only status change events.
     * 
     * @see com.vaadin.ui.Component#setReadOnly(boolean)
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        fireReadOnlyStatusChange();
    }

    /**
     * Tests if the invalid data is committed to datasource.
     * 
     * @see com.vaadin.data.BufferedValidatable#isInvalidCommitted()
     */
    @Override
    public boolean isInvalidCommitted() {
        return invalidCommitted;
    }

    /**
     * Sets if the invalid data should be committed to datasource.
     * 
     * @see com.vaadin.data.BufferedValidatable#setInvalidCommitted(boolean)
     */
    @Override
    public void setInvalidCommitted(boolean isCommitted) {
        invalidCommitted = isCommitted;
    }

    /*
     * Saves the current value to the data source Don't add a JavaDoc comment
     * here, we use the default documentation from the implemented interface.
     */
    @Override
    public void commit() throws Buffered.SourceException, InvalidValueException {
        if (dataSource != null && !dataSource.isReadOnly()) {
            if ((isInvalidCommitted() || isValid())) {
                try {

                    // Commits the value to datasource.
                    valueWasModifiedByDataSourceDuringCommit = false;
                    committingValueToDataSource = true;
                    getPropertyDataSource().setValue(getConvertedValue());
                } catch (final Throwable e) {

                    // Sets the buffering state.
                    SourceException sourceException = new Buffered.SourceException(
                            this, e);
                    setCurrentBufferedSourceException(sourceException);

                    // Throws the source exception.
                    throw sourceException;
                } finally {
                    committingValueToDataSource = false;
                }
            } else {
                /* An invalid value and we don't allow them, throw the exception */
                validate();
            }
        }

        // The abstract field is not modified anymore
        if (isModified()) {
            setModified(false);
        }

        // If successful, remove set the buffering state to be ok
        if (getCurrentBufferedSourceException() != null) {
            setCurrentBufferedSourceException(null);
        }

        if (valueWasModifiedByDataSourceDuringCommit) {
            valueWasModifiedByDataSourceDuringCommit = false;
            fireValueChange(false);
        }

    }

    /*
     * Updates the value from the data source. Don't add a JavaDoc comment here,
     * we use the default documentation from the implemented interface.
     */
    @Override
    public void discard() throws Buffered.SourceException {
        updateValueFromDataSource();
    }

    /**
     * Gets the value from the data source. This is only here because of clarity
     * in the code that handles both the data model value and the field value.
     * 
     * @return The value of the property data source
     */
    private Object getDataSourceValue() {
        return dataSource.getValue();
    }

    /**
     * Returns the field value. This is always identical to {@link #getValue()}
     * and only here because of clarity in the code that handles both the data
     * model value and the field value.
     * 
     * @return The value of the field
     */
    private T getFieldValue() {
        // Give the value from abstract buffers if the field if possible
        if (dataSource == null || isBuffered() || isModified()) {
            return getInternalValue();
        }

        // There is no buffered value so use whatever the data model provides
        return convertFromModel(getDataSourceValue());
    }

    /*
     * Has the field been modified since the last commit()? Don't add a JavaDoc
     * comment here, we use the default documentation from the implemented
     * interface.
     */
    @Override
    public boolean isModified() {
        return getState(false).modified;
    }

    private void setModified(boolean modified) {
        getState().modified = modified;
    }

    /**
     * Sets the buffered mode of this Field.
     * <p>
     * When the field is in buffered mode, changes will not be committed to the
     * property data source until {@link #commit()} is called.
     * </p>
     * <p>
     * Setting buffered mode from true to false will commit any pending changes.
     * </p>
     * <p>
     * 
     * </p>
     * 
     * @since 7.0.0
     * @param buffered
     *            true if buffered mode should be turned on, false otherwise
     */
    @Override
    public void setBuffered(boolean buffered) {
        if (this.buffered == buffered) {
            return;
        }
        this.buffered = buffered;
        if (!buffered) {
            commit();
        }
    }

    /**
     * Checks the buffered mode of this Field.
     * 
     * @return true if buffered mode is on, false otherwise
     */
    @Override
    public boolean isBuffered() {
        return buffered;
    }

    /**
     * Returns a string representation of this object. The returned string
     * representation depends on if the legacy Property toString mode is enabled
     * or disabled.
     * <p>
     * If legacy Property toString mode is enabled, returns the value of this
     * <code>Field</code> converted to a String.
     * </p>
     * <p>
     * If legacy Property toString mode is disabled, the string representation
     * has no special meaning
     * </p>
     * 
     * @see LegacyPropertyHelper#isLegacyToStringEnabled()
     * 
     * @return A string representation of the value value stored in the Property
     *         or a string representation of the Property object.
     * @deprecated As of 7.0. Use {@link #getValue()} to get the value of the
     *             field, {@link #getConvertedValue()} to get the field value
     *             converted to the data model type or
     *             {@link #getPropertyDataSource()} .getValue() to get the value
     *             of the data source.
     */
    @Deprecated
    @Override
    public String toString() {
        if (!LegacyPropertyHelper.isLegacyToStringEnabled()) {
            return super.toString();
        } else {
            return LegacyPropertyHelper.legacyPropertyToString(this);
        }
    }

    /* Property interface implementation */

    /**
     * Gets the current value of the field.
     * 
     * <p>
     * This is the visible, modified and possible invalid value the user have
     * entered to the field.
     * </p>
     * 
     * <p>
     * Note that the object returned is compatible with getType(). For example,
     * if the type is String, this returns Strings even when the underlying
     * datasource is of some other type. In order to access the converted value,
     * use {@link #getConvertedValue()} and to access the value of the property
     * data source, use {@link Property#getValue()} for the property data
     * source.
     * </p>
     * 
     * <p>
     * Since Vaadin 7.0, no implicit conversions between other data types and
     * String are performed, but a converter is used if set.
     * </p>
     * 
     * @return the current value of the field.
     */
    @Override
    public T getValue() {
        return getFieldValue();
    }

    /**
     * Sets the value of the field.
     * 
     * @param newFieldValue
     *            the New value of the field.
     * @throws Property.ReadOnlyException
     */
    @Override
    public void setValue(T newFieldValue) throws Property.ReadOnlyException,
            Converter.ConversionException {
        setValue(newFieldValue, false);
    }

    /**
     * Sets the value of the field.
     * 
     * @param newFieldValue
     *            the New value of the field.
     * @param repaintIsNotNeeded
     *            True iff caller is sure that repaint is not needed.
     * @throws Property.ReadOnlyException
     */
    protected void setValue(T newFieldValue, boolean repaintIsNotNeeded)
            throws Property.ReadOnlyException, Converter.ConversionException,
            InvalidValueException {

        if (!SharedUtil.equals(newFieldValue, getInternalValue())) {

            // Read only fields can not be changed
            if (isReadOnly()) {
                throw new Property.ReadOnlyException();
            }
            try {
                T doubleConvertedFieldValue = convertFromModel(convertToModel(newFieldValue));
                if (!SharedUtil
                        .equals(newFieldValue, doubleConvertedFieldValue)) {
                    newFieldValue = doubleConvertedFieldValue;
                    repaintIsNotNeeded = false;
                }
            } catch (Throwable t) {
                // Ignore exceptions in the conversion at this stage. Any
                // conversion error will be handled later by validate().
            }

            // Repaint is needed even when the client thinks that it knows the
            // new state if validity of the component may change
            if (repaintIsNotNeeded
                    && (isRequired() || getValidators() != null || getConverter() != null)) {
                repaintIsNotNeeded = false;
            }

            if (!isInvalidAllowed()) {
                /*
                 * If invalid values are not allowed the value must be validated
                 * before it is set. If validation fails, the
                 * InvalidValueException is thrown and the internal value is not
                 * updated.
                 */
                validate(newFieldValue);
            }

            // Changes the value
            setInternalValue(newFieldValue);
            setModified(dataSource != null);

            valueWasModifiedByDataSourceDuringCommit = false;
            // In not buffering, try to commit
            if (!isBuffered() && dataSource != null
                    && (isInvalidCommitted() || isValid())) {
                try {

                    // Commits the value to datasource
                    committingValueToDataSource = true;
                    getPropertyDataSource().setValue(
                            convertToModel(newFieldValue));

                    // The buffer is now unmodified
                    setModified(false);

                } catch (final Throwable e) {

                    // Sets the buffering state
                    currentBufferedSourceException = new Buffered.SourceException(
                            this, e);
                    markAsDirty();

                    // Throws the source exception
                    throw currentBufferedSourceException;
                } finally {
                    committingValueToDataSource = false;
                }
            }

            // If successful, remove set the buffering state to be ok
            if (getCurrentBufferedSourceException() != null) {
                setCurrentBufferedSourceException(null);
            }

            if (valueWasModifiedByDataSourceDuringCommit) {
                /*
                 * Value was modified by datasource. Force repaint even if
                 * repaint was not requested.
                 */
                valueWasModifiedByDataSourceDuringCommit = repaintIsNotNeeded = false;
            }

            // Fires the value change
            fireValueChange(repaintIsNotNeeded);

        }
    }

    @Deprecated
    static boolean equals(Object value1, Object value2) {
        return SharedUtil.equals(value1, value2);
    }

    /* External data source */

    /**
     * Gets the current data source of the field, if any.
     * 
     * @return the current data source as a Property, or <code>null</code> if
     *         none defined.
     */
    @Override
    public Property getPropertyDataSource() {
        return dataSource;
    }

    /**
     * <p>
     * Sets the specified Property as the data source for the field. All
     * uncommitted changes are replaced with a value from the new data source.
     * </p>
     * 
     * <p>
     * If the datasource has any validators, the same validators are added to
     * the field. Because the default behavior of the field is to allow invalid
     * values, but not to allow committing them, this only adds visual error
     * messages to fields and do not allow committing them as long as the value
     * is invalid. After the value is valid, the error message is not shown and
     * the commit can be done normally.
     * </p>
     * 
     * <p>
     * If the data source implements
     * {@link com.vaadin.data.Property.ValueChangeNotifier} and/or
     * {@link com.vaadin.data.Property.ReadOnlyStatusChangeNotifier}, the field
     * registers itself as a listener and updates itself according to the events
     * it receives. To avoid memory leaks caused by references to a field no
     * longer in use, the listener registrations are removed on
     * {@link AbstractField#detach() detach} and re-added on
     * {@link AbstractField#attach() attach}.
     * </p>
     * 
     * <p>
     * Note: before 6.5 we actually called discard() method in the beginning of
     * the method. This was removed to simplify implementation, avoid excess
     * calls to backing property and to avoid odd value change events that were
     * previously fired (developer expects 0-1 value change events if this
     * method is called). Some complex field implementations might now need to
     * override this method to do housekeeping similar to discard().
     * </p>
     * 
     * @param newDataSource
     *            the new data source Property.
     */
    @Override
    public void setPropertyDataSource(Property newDataSource) {

        // Saves the old value
        final Object oldValue = getInternalValue();

        // Stop listening to the old data source
        removePropertyListeners();

        // Sets the new data source
        dataSource = newDataSource;
        getState().propertyReadOnly = dataSource == null ? false : dataSource
                .isReadOnly();

        // Check if the current converter is compatible.
        if (newDataSource != null
                && !ConverterUtil.canConverterPossiblyHandle(getConverter(),
                        getType(), newDataSource.getType())) {
            // There is no converter set or there is no way the current
            // converter can be compatible.
            setConverter(newDataSource.getType());
        }
        // Gets the value from source. This requires that a valid converter has
        // been set.
        try {
            if (dataSource != null) {
                T fieldValue = convertFromModel(getDataSourceValue());
                setInternalValue(fieldValue);
            }
            setModified(false);
            if (getCurrentBufferedSourceException() != null) {
                setCurrentBufferedSourceException(null);
            }
        } catch (final Throwable e) {
            setCurrentBufferedSourceException(new Buffered.SourceException(
                    this, e));
            setModified(true);
            throw getCurrentBufferedSourceException();
        }

        // Listen to new data source if possible
        addPropertyListeners();

        // Copy the validators from the data source
        if (dataSource instanceof Validatable) {
            final Collection<Validator> validators = ((Validatable) dataSource)
                    .getValidators();
            if (validators != null) {
                for (final Iterator<Validator> i = validators.iterator(); i
                        .hasNext();) {
                    addValidator(i.next());
                }
            }
        }

        // Fires value change if the value has changed
        T value = getInternalValue();
        if ((value != oldValue)
                && ((value != null && !value.equals(oldValue)) || value == null)) {
            fireValueChange(false);
        }
    }

    /**
     * Retrieves a converter for the field from the converter factory defined
     * for the application. Clears the converter if no application reference is
     * available or if the factory returns null.
     * 
     * @param datamodelType
     *            The type of the data model that we want to be able to convert
     *            from
     */
    public void setConverter(Class<?> datamodelType) {
        Converter<T, ?> c = (Converter<T, ?>) ConverterUtil.getConverter(
                getType(), datamodelType, getSession());
        setConverter(c);
    }

    /**
     * Convert the given value from the data source type to the UI type.
     * 
     * @param newValue
     *            The data source value to convert.
     * @return The converted value that is compatible with the UI type or the
     *         original value if its type is compatible and no converter is set.
     * @throws Converter.ConversionException
     *             if there is no converter and the type is not compatible with
     *             the data source type.
     */
    private T convertFromModel(Object newValue) {
        return convertFromModel(newValue, getLocale());
    }

    /**
     * Convert the given value from the data source type to the UI type.
     * 
     * @param newValue
     *            The data source value to convert.
     * @return The converted value that is compatible with the UI type or the
     *         original value if its type is compatible and no converter is set.
     * @throws Converter.ConversionException
     *             if there is no converter and the type is not compatible with
     *             the data source type.
     */
    private T convertFromModel(Object newValue, Locale locale) {
        return ConverterUtil.convertFromModel(newValue, getType(),
                getConverter(), locale);
    }

    /**
     * Convert the given value from the UI type to the data source type.
     * 
     * @param fieldValue
     *            The value to convert. Typically returned by
     *            {@link #getFieldValue()}
     * @return The converted value that is compatible with the data source type.
     * @throws Converter.ConversionException
     *             if there is no converter and the type is not compatible with
     *             the data source type.
     */
    private Object convertToModel(T fieldValue)
            throws Converter.ConversionException {
        return convertToModel(fieldValue, getLocale());
    }

    /**
     * Convert the given value from the UI type to the data source type.
     * 
     * @param fieldValue
     *            The value to convert. Typically returned by
     *            {@link #getFieldValue()}
     * @param locale
     *            The locale to use for the conversion
     * @return The converted value that is compatible with the data source type.
     * @throws Converter.ConversionException
     *             if there is no converter and the type is not compatible with
     *             the data source type.
     */
    private Object convertToModel(T fieldValue, Locale locale)
            throws Converter.ConversionException {
        Class<?> modelType = getModelType();
        try {
            return ConverterUtil.convertToModel(fieldValue,
                    (Class<Object>) modelType, getConverter(), locale);
        } catch (ConversionException e) {
            throw new ConversionException(getConversionError(modelType, e), e);
        }
    }

    /**
     * Retrieves the type of the currently used data model. If the field has no
     * data source then the model type of the converter is used.
     * 
     * @since 7.1
     * @return The type of the currently used data model or null if no data
     *         source or converter is set.
     */
    protected Class<?> getModelType() {
        Property<?> pd = getPropertyDataSource();
        if (pd != null) {
            return pd.getType();
        } else if (getConverter() != null) {
            return getConverter().getModelType();
        }
        return null;
    }

    /**
     * Returns the conversion error with {0} replaced by the data source type
     * and {1} replaced by the exception (localized) message.
     * 
     * @since 7.1
     * @param dataSourceType
     *            the type of the data source
     * @param e
     *            a conversion exception which can provide additional
     *            information
     * @return The value conversion error string with parameters replaced.
     */
    protected String getConversionError(Class<?> dataSourceType,
            ConversionException e) {
        String conversionError = getConversionError();

        if (conversionError != null) {
            if (dataSourceType != null) {
                conversionError = conversionError.replace("{0}",
                        dataSourceType.getSimpleName());
            }
            if (e != null) {
                conversionError = conversionError.replace("{1}",
                        e.getLocalizedMessage());
            }
        }
        return conversionError;
    }

    /**
     * Returns the current value (as returned by {@link #getValue()}) converted
     * to the data source type.
     * <p>
     * This returns the same as {@link AbstractField#getValue()} if no converter
     * has been set. The value is not necessarily the same as the data source
     * value e.g. if the field is in buffered mode and has been modified.
     * </p>
     * 
     * @return The converted value that is compatible with the data source type
     */
    public Object getConvertedValue() {
        return convertToModel(getFieldValue());
    }

    /**
     * Sets the value of the field using a value of the data source type. The
     * value given is converted to the field type and then assigned to the
     * field. This will update the property data source in the same way as when
     * {@link #setValue(Object)} is called.
     * 
     * @param value
     *            The value to set. Must be the same type as the data source.
     */
    public void setConvertedValue(Object value) {
        setValue(convertFromModel(value));
    }

    /* Validation */

    /**
     * Adds a new validator for the field's value. All validators added to a
     * field are checked each time the its value changes.
     * 
     * @param validator
     *            the new validator to be added.
     */
    @Override
    public void addValidator(Validator validator) {
        if (validators == null) {
            validators = new LinkedList<Validator>();
        }
        validators.add(validator);
        markAsDirty();
    }

    /**
     * Gets the validators of the field.
     * 
     * @return An unmodifiable collection that holds all validators for the
     *         field.
     */
    @Override
    public Collection<Validator> getValidators() {
        if (validators == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableCollection(validators);
        }
    }

    /**
     * Removes the validator from the field.
     * 
     * @param validator
     *            the validator to remove.
     */
    @Override
    public void removeValidator(Validator validator) {
        if (validators != null) {
            validators.remove(validator);
        }
        markAsDirty();
    }

    /**
     * Removes all validators from the field.
     */
    @Override
    public void removeAllValidators() {
        if (validators != null) {
            validators.clear();
        }
        markAsDirty();
    }

    /**
     * Tests the current value against registered validators if the field is not
     * empty. If the field is empty it is considered valid if it is not required
     * and invalid otherwise. Validators are never checked for empty fields.
     * 
     * In most cases, {@link #validate()} should be used instead of
     * {@link #isValid()} to also get the error message.
     * 
     * @return <code>true</code> if all registered validators claim that the
     *         current value is valid or if the field is empty and not required,
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean isValid() {

        try {
            validate();
            return true;
        } catch (InvalidValueException e) {
            return false;
        }
    }

    /**
     * Checks the validity of the Field.
     * 
     * A field is invalid if it is set as required (using
     * {@link #setRequired(boolean)} and is empty, if one or several of the
     * validators added to the field indicate it is invalid or if the value
     * cannot be converted provided a converter has been set.
     * 
     * The "required" validation is a built-in validation feature. If the field
     * is required and empty this method throws an EmptyValueException with the
     * error message set using {@link #setRequiredError(String)}.
     * 
     * @see com.vaadin.data.Validatable#validate()
     */
    @Override
    public void validate() throws Validator.InvalidValueException {

        if (isRequired() && isEmpty()) {
            throw new Validator.EmptyValueException(requiredError);
        }
        validate(getFieldValue());
    }

    /**
     * Validates that the given value pass the validators for the field.
     * <p>
     * This method does not check the requiredness of the field.
     * 
     * @param fieldValue
     *            The value to check
     * @throws Validator.InvalidValueException
     *             if one or several validators fail
     */
    protected void validate(T fieldValue)
            throws Validator.InvalidValueException {

        Object valueToValidate = fieldValue;

        // If there is a converter we start by converting the value as we want
        // to validate the converted value
        if (getConverter() != null) {
            try {
                valueToValidate = getConverter().convertToModel(fieldValue,
                        getModelType(), getLocale());
            } catch (ConversionException e) {
                throw new InvalidValueException(getConversionError(
                        getConverter().getModelType(), e));
            }
        }

        List<InvalidValueException> validationExceptions = new ArrayList<InvalidValueException>();
        if (validators != null) {
            // Gets all the validation errors
            for (Validator v : validators) {
                try {
                    v.validate(valueToValidate);
                } catch (final Validator.InvalidValueException e) {
                    validationExceptions.add(e);
                }
            }
        }

        // If there were no errors
        if (validationExceptions.isEmpty()) {
            return;
        }

        // If only one error occurred, throw it forwards
        if (validationExceptions.size() == 1) {
            throw validationExceptions.get(0);
        }

        InvalidValueException[] exceptionArray = validationExceptions
                .toArray(new InvalidValueException[validationExceptions.size()]);

        // Create a composite validator and include all exceptions
        throw new Validator.InvalidValueException(null, exceptionArray);
    }

    /**
     * Fields allow invalid values by default. In most cases this is wanted,
     * because the field otherwise visually forget the user input immediately.
     * 
     * @return true iff the invalid values are allowed.
     * @see com.vaadin.data.Validatable#isInvalidAllowed()
     */
    @Override
    public boolean isInvalidAllowed() {
        return invalidAllowed;
    }

    /**
     * Fields allow invalid values by default. In most cases this is wanted,
     * because the field otherwise visually forget the user input immediately.
     * <p>
     * In common setting where the user wants to assure the correctness of the
     * datasource, but allow temporarily invalid contents in the field, the user
     * should add the validators to datasource, that should not allow invalid
     * values. The validators are automatically copied to the field when the
     * datasource is set.
     * </p>
     * 
     * @see com.vaadin.data.Validatable#setInvalidAllowed(boolean)
     */
    @Override
    public void setInvalidAllowed(boolean invalidAllowed)
            throws UnsupportedOperationException {
        this.invalidAllowed = invalidAllowed;
    }

    /**
     * Error messages shown by the fields are composites of the error message
     * thrown by the superclasses (that is the component error message),
     * validation errors and buffered source errors.
     * 
     * @see com.vaadin.ui.AbstractComponent#getErrorMessage()
     */
    @Override
    public ErrorMessage getErrorMessage() {

        /*
         * Check validation errors only if automatic validation is enabled.
         * Empty, required fields will generate a validation error containing
         * the requiredError string. For these fields the exclamation mark will
         * be hidden but the error must still be sent to the client.
         */
        Validator.InvalidValueException validationError = null;
        if (isValidationVisible()) {
            try {
                validate();
            } catch (Validator.InvalidValueException e) {
                if (!e.isInvisible()) {
                    validationError = e;
                }
            }
        }

        // Check if there are any systems errors
        final ErrorMessage superError = super.getErrorMessage();

        // Return if there are no errors at all
        if (superError == null && validationError == null
                && getCurrentBufferedSourceException() == null) {
            return null;
        }

        // Throw combination of the error types
        return new CompositeErrorMessage(
                new ErrorMessage[] {
                        superError,
                        AbstractErrorMessage
                                .getErrorMessageForException(validationError),
                        AbstractErrorMessage
                                .getErrorMessageForException(getCurrentBufferedSourceException()) });

    }

    /* Value change events */

    private static final Method VALUE_CHANGE_METHOD;

    static {
        try {
            VALUE_CHANGE_METHOD = Property.ValueChangeListener.class
                    .getDeclaredMethod("valueChange",
                            new Class[] { Property.ValueChangeEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in AbstractField");
        }
    }

    /*
     * Adds a value change listener for the field. Don't add a JavaDoc comment
     * here, we use the default documentation from the implemented interface.
     */
    @Override
    public void addValueChangeListener(Property.ValueChangeListener listener) {
        addListener(AbstractField.ValueChangeEvent.class, listener,
                VALUE_CHANGE_METHOD);
        // ensure "automatic immediate handling" works
        markAsDirty();
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addValueChangeListener(com.vaadin.data.Property.ValueChangeListener)}
     **/
    @Override
    @Deprecated
    public void addListener(Property.ValueChangeListener listener) {
        addValueChangeListener(listener);
    }

    /*
     * Removes a value change listener from the field. Don't add a JavaDoc
     * comment here, we use the default documentation from the implemented
     * interface.
     */
    @Override
    public void removeValueChangeListener(Property.ValueChangeListener listener) {
        removeListener(AbstractField.ValueChangeEvent.class, listener,
                VALUE_CHANGE_METHOD);
        // ensure "automatic immediate handling" works
        markAsDirty();
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeValueChangeListener(com.vaadin.data.Property.ValueChangeListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(Property.ValueChangeListener listener) {
        removeValueChangeListener(listener);
    }

    /**
     * Emits the value change event. The value contained in the field is
     * validated before the event is created.
     */
    protected void fireValueChange(boolean repaintIsNotNeeded) {
        fireEvent(new AbstractField.ValueChangeEvent(this));
        if (!repaintIsNotNeeded) {
            markAsDirty();
        }
    }

    /* Read-only status change events */

    private static final Method READ_ONLY_STATUS_CHANGE_METHOD;

    static {
        try {
            READ_ONLY_STATUS_CHANGE_METHOD = Property.ReadOnlyStatusChangeListener.class
                    .getDeclaredMethod(
                            "readOnlyStatusChange",
                            new Class[] { Property.ReadOnlyStatusChangeEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in AbstractField");
        }
    }

    /**
     * React to read only status changes of the property by requesting a
     * repaint.
     * 
     * @see Property.ReadOnlyStatusChangeListener
     */
    @Override
    public void readOnlyStatusChange(Property.ReadOnlyStatusChangeEvent event) {
        getState().propertyReadOnly = event.getProperty().isReadOnly();
    }

    /**
     * An <code>Event</code> object specifying the Property whose read-only
     * status has changed.
     * 
     * @author Vaadin Ltd.
     * @since 3.0
     */
    public static class ReadOnlyStatusChangeEvent extends Component.Event
            implements Property.ReadOnlyStatusChangeEvent, Serializable {

        /**
         * New instance of text change event.
         * 
         * @param source
         *            the Source of the event.
         */
        public ReadOnlyStatusChangeEvent(AbstractField source) {
            super(source);
        }

        /**
         * Property where the event occurred.
         * 
         * @return the Source of the event.
         */
        @Override
        public Property getProperty() {
            return (Property) getSource();
        }
    }

    /*
     * Adds a read-only status change listener for the field. Don't add a
     * JavaDoc comment here, we use the default documentation from the
     * implemented interface.
     */
    @Override
    public void addReadOnlyStatusChangeListener(
            Property.ReadOnlyStatusChangeListener listener) {
        addListener(Property.ReadOnlyStatusChangeEvent.class, listener,
                READ_ONLY_STATUS_CHANGE_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addReadOnlyStatusChangeListener(com.vaadin.data.Property.ReadOnlyStatusChangeListener)}
     **/
    @Override
    @Deprecated
    public void addListener(Property.ReadOnlyStatusChangeListener listener) {
        addReadOnlyStatusChangeListener(listener);
    }

    /*
     * Removes a read-only status change listener from the field. Don't add a
     * JavaDoc comment here, we use the default documentation from the
     * implemented interface.
     */
    @Override
    public void removeReadOnlyStatusChangeListener(
            Property.ReadOnlyStatusChangeListener listener) {
        removeListener(Property.ReadOnlyStatusChangeEvent.class, listener,
                READ_ONLY_STATUS_CHANGE_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeReadOnlyStatusChangeListener(com.vaadin.data.Property.ReadOnlyStatusChangeListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(Property.ReadOnlyStatusChangeListener listener) {
        removeReadOnlyStatusChangeListener(listener);
    }

    /**
     * Emits the read-only status change event. The value contained in the field
     * is validated before the event is created.
     */
    protected void fireReadOnlyStatusChange() {
        fireEvent(new AbstractField.ReadOnlyStatusChangeEvent(this));
    }

    /**
     * This method listens to data source value changes and passes the changes
     * forwards.
     * 
     * Changes are not forwarded to the listeners of the field during internal
     * operations of the field to avoid duplicate notifications.
     * 
     * @param event
     *            the value change event telling the data source contents have
     *            changed.
     */
    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        if (!isBuffered()) {
            if (committingValueToDataSource) {
                boolean propertyNotifiesOfTheBufferedValue = SharedUtil.equals(
                        event.getProperty().getValue(), getInternalValue());
                if (!propertyNotifiesOfTheBufferedValue) {
                    /*
                     * Property (or chained property like PropertyFormatter) now
                     * reports different value than the one the field has just
                     * committed to it. In this case we respect the property
                     * value.
                     * 
                     * Still, we don't fire value change yet, but instead
                     * postpone it until "commit" is done. See setValue(Object,
                     * boolean) and commit().
                     */
                    readValueFromProperty(event);
                    valueWasModifiedByDataSourceDuringCommit = true;
                }
            } else if (!isModified()) {
                readValueFromProperty(event);
                fireValueChange(false);
            }
        }
    }

    private void readValueFromProperty(Property.ValueChangeEvent event) {
        setInternalValue(convertFromModel(event.getProperty().getValue()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focus() {
        super.focus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component.Focusable#getTabIndex()
     */
    @Override
    public int getTabIndex() {
        return getState(false).tabIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component.Focusable#setTabIndex(int)
     */
    @Override
    public void setTabIndex(int tabIndex) {
        getState().tabIndex = tabIndex;
    }

    /**
     * Returns the internal field value, which might not match the data source
     * value e.g. if the field has been modified and is not in write-through
     * mode.
     * 
     * This method can be overridden by subclasses together with
     * {@link #setInternalValue(Object)} to compute internal field value at
     * runtime. When doing so, typically also {@link #isModified()} needs to be
     * overridden and care should be taken in the management of the empty state
     * and buffering support.
     * 
     * @return internal field value
     */
    protected T getInternalValue() {
        return value;
    }

    /**
     * Sets the internal field value. This is purely used by AbstractField to
     * change the internal Field value. It does not trigger valuechange events.
     * It can be overridden by the inheriting classes to update all dependent
     * variables.
     * 
     * Subclasses can also override {@link #getInternalValue()} if necessary.
     * 
     * @param newValue
     *            the new value to be set.
     */
    protected void setInternalValue(T newValue) {
        value = newValue;
        valueLocale = getLocale();
        if (validators != null && !validators.isEmpty()) {
            markAsDirty();
        }
    }

    /**
     * Notifies the component that it is connected to an application.
     * 
     * @see com.vaadin.ui.Component#attach()
     */
    @Override
    public void attach() {
        super.attach();

        localeMightHaveChanged();
        if (!isListeningToPropertyEvents) {
            addPropertyListeners();
            if (!isModified() && !isBuffered()) {
                // Update value from data source
                updateValueFromDataSource();
            }
        }
    }

    @Override
    public void setLocale(Locale locale) {
        super.setLocale(locale);
        localeMightHaveChanged();
    }

    private void localeMightHaveChanged() {
        if (!SharedUtil.equals(valueLocale, getLocale())) {
            // The locale HAS actually changed

            if (dataSource != null && !isModified()) {
                // When we have a data source and the internal value is directly
                // read from that we want to update the value
                T newInternalValue = convertFromModel(getPropertyDataSource()
                        .getValue());
                if (!SharedUtil.equals(newInternalValue, getInternalValue())) {
                    setInternalValue(newInternalValue);
                    fireValueChange(false);
                }
            } else if (dataSource == null && converter != null) {
                /*
                 * No data source but a converter has been set. The same issues
                 * as above but we cannot use propertyDataSource. Convert the
                 * current value back to a model value using the old locale and
                 * then convert back using the new locale. If this does not
                 * match the field value we need to set the converted value
                 * again.
                 */
                Object convertedValue = convertToModel(getInternalValue(),
                        valueLocale);
                T newinternalValue = convertFromModel(convertedValue);
                if (!SharedUtil.equals(getInternalValue(), newinternalValue)) {
                    setInternalValue(newinternalValue);
                    fireValueChange(false);
                }
            }
        }
    }

    @Override
    public void detach() {
        super.detach();
        // Stop listening to data source events on detach to avoid a potential
        // memory leak. See #6155.
        removePropertyListeners();
    }

    /**
     * Is this field required. Required fields must filled by the user.
     * 
     * If the field is required, it is visually indicated in the user interface.
     * Furthermore, setting field to be required implicitly adds "non-empty"
     * validator and thus isValid() == false or any isEmpty() fields. In those
     * cases validation errors are not painted as it is obvious that the user
     * must fill in the required fields.
     * 
     * On the other hand, for the non-required fields isValid() == true if the
     * field isEmpty() regardless of any attached validators.
     * 
     * 
     * @return <code>true</code> if the field is required, otherwise
     *         <code>false</code>.
     */
    @Override
    public boolean isRequired() {
        return getState(false).required;
    }

    /**
     * Sets the field required. Required fields must filled by the user.
     * 
     * If the field is required, it is visually indicated in the user interface.
     * Furthermore, setting field to be required implicitly adds "non-empty"
     * validator and thus isValid() == false or any isEmpty() fields. In those
     * cases validation errors are not painted as it is obvious that the user
     * must fill in the required fields.
     * 
     * On the other hand, for the non-required fields isValid() == true if the
     * field isEmpty() regardless of any attached validators.
     * 
     * @param required
     *            Is the field required.
     */
    @Override
    public void setRequired(boolean required) {
        getState().required = required;
    }

    /**
     * Set the error that is show if this field is required, but empty. When
     * setting requiredMessage to be "" or null, no error pop-up or exclamation
     * mark is shown for a empty required field. This faults to "". Even in
     * those cases isValid() returns false for empty required fields.
     * 
     * @param requiredMessage
     *            Message to be shown when this field is required, but empty.
     */
    @Override
    public void setRequiredError(String requiredMessage) {
        requiredError = requiredMessage;
        markAsDirty();
    }

    @Override
    public String getRequiredError() {
        return requiredError;
    }

    /**
     * Gets the error that is shown if the field value cannot be converted to
     * the data source type.
     * 
     * @return The error that is shown if conversion of the field value fails
     */
    public String getConversionError() {
        return conversionError;
    }

    /**
     * Sets the error that is shown if the field value cannot be converted to
     * the data source type. If {0} is present in the message, it will be
     * replaced by the simple name of the data source type. If {1} is present in
     * the message, it will be replaced by the ConversionException message.
     * 
     * @param valueConversionError
     *            Message to be shown when conversion of the value fails
     */
    public void setConversionError(String valueConversionError) {
        this.conversionError = valueConversionError;
        markAsDirty();
    }

    /**
     * Is the field empty?
     * 
     * In general, "empty" state is same as null. As an exception, TextField
     * also treats empty string as "empty".
     */
    protected boolean isEmpty() {
        return (getFieldValue() == null);
    }

    /**
     * Is automatic, visible validation enabled?
     * 
     * If automatic validation is enabled, any validators connected to this
     * component are evaluated while painting the component and potential error
     * messages are sent to client. If the automatic validation is turned off,
     * isValid() and validate() methods still work, but one must show the
     * validation in their own code.
     * 
     * @return True, if automatic validation is enabled.
     */
    public boolean isValidationVisible() {
        return validationVisible;
    }

    /**
     * Enable or disable automatic, visible validation.
     * 
     * If automatic validation is enabled, any validators connected to this
     * component are evaluated while painting the component and potential error
     * messages are sent to client. If the automatic validation is turned off,
     * isValid() and validate() methods still work, but one must show the
     * validation in their own code.
     * 
     * @param validateAutomatically
     *            True, if automatic validation is enabled.
     */
    public void setValidationVisible(boolean validateAutomatically) {
        if (validationVisible != validateAutomatically) {
            markAsDirty();
            validationVisible = validateAutomatically;
        }
    }

    /**
     * Sets the current buffered source exception.
     * 
     * @param currentBufferedSourceException
     */
    public void setCurrentBufferedSourceException(
            Buffered.SourceException currentBufferedSourceException) {
        this.currentBufferedSourceException = currentBufferedSourceException;
        markAsDirty();
    }

    /**
     * Gets the current buffered source exception.
     * 
     * @return The current source exception
     */
    protected Buffered.SourceException getCurrentBufferedSourceException() {
        return currentBufferedSourceException;
    }

    /**
     * A ready-made {@link ShortcutListener} that focuses the given
     * {@link Focusable} (usually a {@link Field}) when the keyboard shortcut is
     * invoked.
     * 
     */
    public static class FocusShortcut extends ShortcutListener {
        protected Focusable focusable;

        /**
         * Creates a keyboard shortcut for focusing the given {@link Focusable}
         * using the shorthand notation defined in {@link ShortcutAction}.
         * 
         * @param focusable
         *            to focused when the shortcut is invoked
         * @param shorthandCaption
         *            caption with keycode and modifiers indicated
         */
        public FocusShortcut(Focusable focusable, String shorthandCaption) {
            super(shorthandCaption);
            this.focusable = focusable;
        }

        /**
         * Creates a keyboard shortcut for focusing the given {@link Focusable}.
         * 
         * @param focusable
         *            to focused when the shortcut is invoked
         * @param keyCode
         *            keycode that invokes the shortcut
         * @param modifiers
         *            modifiers required to invoke the shortcut
         */
        public FocusShortcut(Focusable focusable, int keyCode, int... modifiers) {
            super(null, keyCode, modifiers);
            this.focusable = focusable;
        }

        /**
         * Creates a keyboard shortcut for focusing the given {@link Focusable}.
         * 
         * @param focusable
         *            to focused when the shortcut is invoked
         * @param keyCode
         *            keycode that invokes the shortcut
         */
        public FocusShortcut(Focusable focusable, int keyCode) {
            this(focusable, keyCode, null);
        }

        @Override
        public void handleAction(Object sender, Object target) {
            focusable.focus();
        }
    }

    private void updateValueFromDataSource() {
        if (dataSource != null) {

            // Gets the correct value from datasource
            T newFieldValue;
            try {

                // Discards buffer by overwriting from datasource
                newFieldValue = convertFromModel(getDataSourceValue());

                // If successful, remove set the buffering state to be ok
                if (getCurrentBufferedSourceException() != null) {
                    setCurrentBufferedSourceException(null);
                }
            } catch (final Throwable e) {
                // FIXME: What should really be done here if conversion fails?

                // Sets the buffering state
                currentBufferedSourceException = new Buffered.SourceException(
                        this, e);
                markAsDirty();

                // Throws the source exception
                throw currentBufferedSourceException;
            }

            final boolean wasModified = isModified();
            setModified(false);

            // If the new value differs from the previous one
            if (!SharedUtil.equals(newFieldValue, getInternalValue())) {
                setInternalValue(newFieldValue);
                fireValueChange(false);
            } else if (wasModified) {
                // If the value did not change, but the modification status did
                markAsDirty();
            }
        }
    }

    /**
     * Gets the converter used to convert the property data source value to the
     * field value.
     * 
     * @return The converter or null if none is set.
     */
    public Converter<T, Object> getConverter() {
        return converter;
    }

    /**
     * Sets the converter used to convert the field value to property data
     * source type. The converter must have a presentation type that matches the
     * field type.
     * 
     * @param converter
     *            The new converter to use.
     */
    public void setConverter(Converter<T, ?> converter) {
        this.converter = (Converter<T, Object>) converter;
        markAsDirty();
    }

    @Override
    protected AbstractFieldState getState() {
        return (AbstractFieldState) super.getState();
    }

    @Override
    protected AbstractFieldState getState(boolean markAsDirty) {
        return (AbstractFieldState) super.getState(markAsDirty);
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        // Hide the error indicator if needed
        getState().hideErrors = shouldHideErrors();
    }

    /**
     * Registers this as an event listener for events sent by the data source
     * (if any). Does nothing if
     * <code>isListeningToPropertyEvents == true</code>.
     */
    private void addPropertyListeners() {
        if (!isListeningToPropertyEvents) {
            if (dataSource instanceof Property.ValueChangeNotifier) {
                ((Property.ValueChangeNotifier) dataSource).addListener(this);
            }
            if (dataSource instanceof Property.ReadOnlyStatusChangeNotifier) {
                ((Property.ReadOnlyStatusChangeNotifier) dataSource)
                        .addListener(this);
            }
            isListeningToPropertyEvents = true;
        }
    }

    /**
     * Stops listening to events sent by the data source (if any). Does nothing
     * if <code>isListeningToPropertyEvents == false</code>.
     */
    private void removePropertyListeners() {
        if (isListeningToPropertyEvents) {
            if (dataSource instanceof Property.ValueChangeNotifier) {
                ((Property.ValueChangeNotifier) dataSource)
                        .removeListener(this);
            }
            if (dataSource instanceof Property.ReadOnlyStatusChangeNotifier) {
                ((Property.ReadOnlyStatusChangeNotifier) dataSource)
                        .removeListener(this);
            }
            isListeningToPropertyEvents = false;
        }
    }
}
