/* 
@VaadinApache2LicenseForJavaFiles@
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
import java.util.Map;
import java.util.logging.Logger;

import com.vaadin.Application;
import com.vaadin.data.Buffered;
import com.vaadin.data.Property;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.terminal.AbstractErrorMessage;
import com.vaadin.terminal.CompositeErrorMessage;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.AbstractFieldState;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;

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
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public abstract class AbstractField<T> extends AbstractComponent implements
        Field<T>, Property.ReadOnlyStatusChangeListener,
        Property.ReadOnlyStatusChangeNotifier, Action.ShortcutNotifier {

    /* Private members */

    private static final Logger logger = Logger.getLogger(AbstractField.class
            .getName());

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
     * Auto commit mode.
     */
    private boolean writeThroughMode = true;

    /**
     * Reads the value from data-source, when it is not modified.
     */
    private boolean readThroughMode = true;

    /**
     * Is the field modified but not committed.
     */
    private boolean modified = false;

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
     * The tab order number of this field.
     */
    private int tabIndex = 0;

    /**
     * Required field.
     */
    private boolean required = false;

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

    /* Component basics */

    /*
     * Paints the field. Don't add a JavaDoc comment here, we use the default
     * documentation from the implemented interface.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {

        // The tab ordering number
        if (getTabIndex() != 0) {
            target.addAttribute("tabindex", getTabIndex());
        }

        // If the field is modified, but not committed, set modified attribute
        if (isModified()) {
            target.addAttribute("modified", true);
        }

        // Adds the required attribute
        if (!isReadOnly() && isRequired()) {
            target.addAttribute(AbstractComponentConnector.ATTRIBUTE_REQUIRED,
                    true);
        }

        // Hide the error indicator if needed
        if (shouldHideErrors()) {
            target.addAttribute(
                    AbstractComponentConnector.ATTRIBUTE_HIDEERRORS, true);
        }
    }

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
        return isRequired() && isEmpty() && getComponentError() == null
                && getErrorMessage() != null;
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
    public boolean isInvalidCommitted() {
        return invalidCommitted;
    }

    /**
     * Sets if the invalid data should be committed to datasource.
     * 
     * @see com.vaadin.data.BufferedValidatable#setInvalidCommitted(boolean)
     */
    public void setInvalidCommitted(boolean isCommitted) {
        invalidCommitted = isCommitted;
    }

    /*
     * Saves the current value to the data source Don't add a JavaDoc comment
     * here, we use the default documentation from the implemented interface.
     */
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

        boolean repaintNeeded = false;

        // The abstract field is not modified anymore
        if (isModified()) {
            setModified(false);
            repaintNeeded = true;
        }

        // If successful, remove set the buffering state to be ok
        if (getCurrentBufferedSourceException() != null) {
            setCurrentBufferedSourceException(null);
        }

        if (valueWasModifiedByDataSourceDuringCommit) {
            valueWasModifiedByDataSourceDuringCommit = false;
            fireValueChange(false);
        } else if (repaintNeeded) {
            requestRepaint();
        }

    }

    /*
     * Updates the value from the data source. Don't add a JavaDoc comment here,
     * we use the default documentation from the implemented interface.
     */
    public void discard() throws Buffered.SourceException {
        if (dataSource != null) {

            // Gets the correct value from datasource
            T newFieldValue;
            try {

                // Discards buffer by overwriting from datasource
                newFieldValue = convertFromDataSource(getDataSourceValue());

                // If successful, remove set the buffering state to be ok
                if (getCurrentBufferedSourceException() != null) {
                    setCurrentBufferedSourceException(null);
                }
            } catch (final Throwable e) {
                // FIXME: What should really be done here if conversion fails?

                // Sets the buffering state
                currentBufferedSourceException = new Buffered.SourceException(
                        this, e);
                requestRepaint();

                // Throws the source exception
                throw currentBufferedSourceException;
            }

            final boolean wasModified = isModified();
            setModified(false);

            // If the new value differs from the previous one
            if (!equals(newFieldValue, getInternalValue())) {
                setInternalValue(newFieldValue);
                fireValueChange(false);
            } else if (wasModified) {
                // If the value did not change, but the modification status did
                requestRepaint();
            }
        }
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
        if (dataSource == null || !isReadThrough() || isModified()) {
            return getInternalValue();
        }

        // There is no buffered value so use whatever the data model provides
        return convertFromDataSource(getDataSourceValue());
    }

    /*
     * Has the field been modified since the last commit()? Don't add a JavaDoc
     * comment here, we use the default documentation from the implemented
     * interface.
     */
    public boolean isModified() {
        return modified;
    }

    private void setModified(boolean modified) {
        this.modified = modified;
    }

    /*
     * Tests if the field is in write-through mode. Don't add a JavaDoc comment
     * here, we use the default documentation from the implemented interface.
     */
    public boolean isWriteThrough() {
        return writeThroughMode;
    }

    /**
     * Sets the field's write-through mode to the specified status. When
     * switching the write-through mode on, a {@link #commit()} will be
     * performed.
     * 
     * @see #setBuffered(boolean) for an easier way to control read through and
     *      write through modes
     * 
     * @param writeThrough
     *            Boolean value to indicate if the object should be in
     *            write-through mode after the call.
     * @throws SourceException
     *             If the operation fails because of an exception is thrown by
     *             the data source.
     * @throws InvalidValueException
     *             If the implicit commit operation fails because of a
     *             validation error.
     * @deprecated Use {@link #setBuffered(boolean)} instead. Note that
     *             setReadThrough(true), setWriteThrough(true) equals
     *             setBuffered(false)
     */
    @Deprecated
    public void setWriteThrough(boolean writeThrough)
            throws Buffered.SourceException, InvalidValueException {
        if (writeThroughMode == writeThrough) {
            return;
        }
        writeThroughMode = writeThrough;
        if (writeThroughMode) {
            commit();
        }
    }

    /*
     * Tests if the field is in read-through mode. Don't add a JavaDoc comment
     * here, we use the default documentation from the implemented interface.
     */
    public boolean isReadThrough() {
        return readThroughMode;
    }

    /**
     * Sets the field's read-through mode to the specified status. When
     * switching read-through mode on, the object's value is updated from the
     * data source.
     * 
     * @see #setBuffered(boolean) for an easier way to control read through and
     *      write through modes
     * 
     * @param readThrough
     *            Boolean value to indicate if the object should be in
     *            read-through mode after the call.
     * 
     * @throws SourceException
     *             If the operation fails because of an exception is thrown by
     *             the data source. The cause is included in the exception.
     * @deprecated Use {@link #setBuffered(boolean)} instead. Note that
     *             setReadThrough(true), setWriteThrough(true) equals
     *             setBuffered(false)
     */
    @Deprecated
    public void setReadThrough(boolean readThrough)
            throws Buffered.SourceException {
        if (readThroughMode == readThrough) {
            return;
        }
        readThroughMode = readThrough;
        if (!isModified() && readThroughMode && getPropertyDataSource() != null) {
            setInternalValue(convertFromDataSource(getDataSourceValue()));
            fireValueChange(false);
        }
    }

    /**
     * Sets the buffered mode of this Field.
     * <p>
     * When the field is in buffered mode, changes will not be committed to the
     * property data source until {@link #commit()} is called.
     * </p>
     * <p>
     * Changing buffered mode will change the read through and write through
     * state for the field.
     * </p>
     * <p>
     * Mixing calls to {@link #setBuffered(boolean)} and
     * {@link #setReadThrough(boolean)} or {@link #setWriteThrough(boolean)} is
     * generally a bad idea.
     * </p>
     * 
     * @param buffered
     *            true if buffered mode should be turned on, false otherwise
     */
    public void setBuffered(boolean buffered) {
        setReadThrough(!buffered);
        setWriteThrough(!buffered);
    }

    /**
     * Checks the buffered mode of this Field.
     * <p>
     * This method only returns true if both read and write buffering is used.
     * 
     * @return true if buffered mode is on, false otherwise
     */
    public boolean isBuffered() {
        return !isReadThrough() && !isWriteThrough();
    }

    /* Property interface implementation */

    /**
     * Returns the (field) value converted to a String using toString().
     * 
     * @see java.lang.Object#toString()
     * @deprecated Instead use {@link #getValue()} to get the value of the
     *             field, {@link #getConvertedValue()} to get the field value
     *             converted to the data model type or
     *             {@link #getPropertyDataSource()} .getValue() to get the value
     *             of the data source.
     */
    @Deprecated
    @Override
    public String toString() {
        logger.warning("You are using AbstractField.toString() to get the value for a "
                + getClass().getSimpleName()
                + ". This is not recommended and will not be supported in future versions.");
        final Object value = getFieldValue();
        if (value == null) {
            return null;
        }
        return value.toString();
    }

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
    public void setValue(Object newFieldValue)
            throws Property.ReadOnlyException, Converter.ConversionException {
        // This check is needed as long as setValue accepts Object instead of T
        if (newFieldValue != null) {
            if (!getType().isAssignableFrom(newFieldValue.getClass())) {
                throw new Converter.ConversionException("Value of type "
                        + newFieldValue.getClass() + " cannot be assigned to "
                        + getClass().getName());
            }
        }
        setValue((T) newFieldValue, false);
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

        if (!equals(newFieldValue, getInternalValue())) {

            // Read only fields can not be changed
            if (isReadOnly()) {
                throw new Property.ReadOnlyException();
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
            // In write through mode , try to commit
            if (isWriteThrough() && dataSource != null
                    && (isInvalidCommitted() || isValid())) {
                try {

                    // Commits the value to datasource
                    committingValueToDataSource = true;
                    getPropertyDataSource().setValue(
                            convertToDataSource(newFieldValue));

                    // The buffer is now unmodified
                    setModified(false);

                } catch (final Throwable e) {

                    // Sets the buffering state
                    currentBufferedSourceException = new Buffered.SourceException(
                            this, e);
                    requestRepaint();

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

    private static boolean equals(Object value1, Object value2) {
        if (value1 == null) {
            return value2 == null;
        }
        return value1.equals(value2);
    }

    /* External data source */

    /**
     * Gets the current data source of the field, if any.
     * 
     * @return the current data source as a Property, or <code>null</code> if
     *         none defined.
     */
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
    public void setPropertyDataSource(Property newDataSource) {

        // Saves the old value
        final Object oldValue = getInternalValue();

        // Stops listening the old data source changes
        if (dataSource != null
                && Property.ValueChangeNotifier.class
                        .isAssignableFrom(dataSource.getClass())) {
            ((Property.ValueChangeNotifier) dataSource).removeListener(this);
        }
        if (dataSource != null
                && Property.ReadOnlyStatusChangeNotifier.class
                        .isAssignableFrom(dataSource.getClass())) {
            ((Property.ReadOnlyStatusChangeNotifier) dataSource)
                    .removeListener(this);
        }

        // Sets the new data source
        dataSource = newDataSource;
        getState().setPropertyReadOnly(
                dataSource == null ? false : dataSource.isReadOnly());

        // Check if the current converter is compatible.
        if (newDataSource != null
                && (getConverter() == null || !newDataSource.getType()
                        .isAssignableFrom(getConverter().getModelType()))) {
            // Changing from e.g. Number -> Double should set a new converter,
            // changing from Double -> Number can keep the old one (Property
            // accepts Number)

            // Set a new converter if there is a new data source and
            // there is no old converter or the old is incompatible.
            setConverter(newDataSource.getType());
        }
        // Gets the value from source
        try {
            if (dataSource != null) {
                T fieldValue = convertFromDataSource(getDataSourceValue());
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
        }

        // Listens the new data source if possible
        if (dataSource instanceof Property.ValueChangeNotifier) {
            ((Property.ValueChangeNotifier) dataSource).addListener(this);
        }
        if (dataSource instanceof Property.ReadOnlyStatusChangeNotifier) {
            ((Property.ReadOnlyStatusChangeNotifier) dataSource)
                    .addListener(this);
        }

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
        Converter<T, ?> converter = null;

        Application app = Application.getCurrentApplication();
        if (app != null) {
            ConverterFactory factory = app.getConverterFactory();
            converter = (Converter<T, ?>) factory.createConverter(getType(),
                    datamodelType);
        }
        setConverter(converter);
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
    @SuppressWarnings("unchecked")
    private T convertFromDataSource(Object newValue)
            throws Converter.ConversionException {
        if (converter != null) {
            return converter.convertToPresentation(newValue, getLocale());
        }
        if (newValue == null) {
            return null;
        }

        if (getType().isAssignableFrom(newValue.getClass())) {
            return (T) newValue;
        } else {
            throw new Converter.ConversionException(
                    "Unable to convert value of type "
                            + newValue.getClass().getName()
                            + " to "
                            + getType()
                            + ". No converter is set and the types are not compatible.");
        }
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
    private Object convertToDataSource(T fieldValue)
            throws Converter.ConversionException {
        if (converter != null) {
            /*
             * If there is a converter, always use it. It must convert or throw
             * an exception.
             */
            try {
                return converter.convertToModel(fieldValue, getLocale());
            } catch (com.vaadin.data.util.converter.Converter.ConversionException e) {
                throw new Converter.ConversionException(
                        getConversionError(converter.getModelType()), e);
            }
        }

        if (fieldValue == null) {
            // Null should always be passed through the converter but if there
            // is no converter we can safely return null
            return null;
        }

        // check that the value class is compatible with the data source type
        // (if data source set) or field type
        Class<?> type;
        if (getPropertyDataSource() != null) {
            type = getPropertyDataSource().getType();
        } else {
            type = getType();
        }

        if (type.isAssignableFrom(fieldValue.getClass())) {
            return fieldValue;
        } else {
            throw new Converter.ConversionException(getConversionError(type));
        }
    }

    /**
     * Returns the conversion error with {0} replaced by the data source type.
     * 
     * @param dataSourceType
     *            The type of the data source
     * @return The value conversion error string with parameters replaced.
     */
    protected String getConversionError(Class<?> dataSourceType) {
        if (dataSourceType == null) {
            return getConversionError();
        } else {
            return getConversionError().replace("{0}",
                    dataSourceType.getSimpleName());
        }
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
        return convertToDataSource(getFieldValue());
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
        setValue(convertFromDataSource(value));
    }

    /* Validation */

    /**
     * Adds a new validator for the field's value. All validators added to a
     * field are checked each time the its value changes.
     * 
     * @param validator
     *            the new validator to be added.
     */
    public void addValidator(Validator validator) {
        if (validators == null) {
            validators = new LinkedList<Validator>();
        }
        validators.add(validator);
        requestRepaint();
    }

    /**
     * Gets the validators of the field.
     * 
     * @return the Unmodifiable collection that holds all validators for the
     *         field.
     */
    public Collection<Validator> getValidators() {
        if (validators == null || validators.isEmpty()) {
            return null;
        }
        return Collections.unmodifiableCollection(validators);
    }

    /**
     * Removes the validator from the field.
     * 
     * @param validator
     *            the validator to remove.
     */
    public void removeValidator(Validator validator) {
        if (validators != null) {
            validators.remove(validator);
        }
        requestRepaint();
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
                        getLocale());
            } catch (Exception e) {
                throw new InvalidValueException(
                        getConversionError(getConverter().getModelType()));
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
    public void addListener(Property.ValueChangeListener listener) {
        addListener(AbstractField.ValueChangeEvent.class, listener,
                VALUE_CHANGE_METHOD);
    }

    /*
     * Removes a value change listener from the field. Don't add a JavaDoc
     * comment here, we use the default documentation from the implemented
     * interface.
     */
    public void removeListener(Property.ValueChangeListener listener) {
        removeListener(AbstractField.ValueChangeEvent.class, listener,
                VALUE_CHANGE_METHOD);
    }

    /**
     * Emits the value change event. The value contained in the field is
     * validated before the event is created.
     */
    protected void fireValueChange(boolean repaintIsNotNeeded) {
        fireEvent(new AbstractField.ValueChangeEvent(this));
        if (!repaintIsNotNeeded) {
            requestRepaint();
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
    public void readOnlyStatusChange(Property.ReadOnlyStatusChangeEvent event) {
        getState().setPropertyReadOnly(event.getProperty().isReadOnly());
        requestRepaint();
    }

    /**
     * An <code>Event</code> object specifying the Property whose read-only
     * status has changed.
     * 
     * @author Vaadin Ltd.
     * @version
     * @VERSION@
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
        public Property getProperty() {
            return (Property) getSource();
        }
    }

    /*
     * Adds a read-only status change listener for the field. Don't add a
     * JavaDoc comment here, we use the default documentation from the
     * implemented interface.
     */
    public void addListener(Property.ReadOnlyStatusChangeListener listener) {
        addListener(Property.ReadOnlyStatusChangeEvent.class, listener,
                READ_ONLY_STATUS_CHANGE_METHOD);
    }

    /*
     * Removes a read-only status change listener from the field. Don't add a
     * JavaDoc comment here, we use the default documentation from the
     * implemented interface.
     */
    public void removeListener(Property.ReadOnlyStatusChangeListener listener) {
        removeListener(Property.ReadOnlyStatusChangeEvent.class, listener,
                READ_ONLY_STATUS_CHANGE_METHOD);
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
    public void valueChange(Property.ValueChangeEvent event) {
        if (isReadThrough()) {
            if (committingValueToDataSource) {
                boolean propertyNotifiesOfTheBufferedValue = equals(event
                        .getProperty().getValue(), getInternalValue());
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
        setInternalValue(convertFromDataSource(event.getProperty().getValue()));
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);
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
    public int getTabIndex() {
        return tabIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component.Focusable#setTabIndex(int)
     */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
        requestRepaint();
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
        if (validators != null && !validators.isEmpty()) {
            requestRepaint();
        }
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
     * @return <code>true</code> if the field is required .otherwise
     *         <code>false</code>.
     */
    public boolean isRequired() {
        return required;
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
    public void setRequired(boolean required) {
        this.required = required;
        requestRepaint();
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
    public void setRequiredError(String requiredMessage) {
        requiredError = requiredMessage;
        requestRepaint();
    }

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
     * replaced by the simple name of the data source type.
     * 
     * @param valueConversionError
     *            Message to be shown when conversion of the value fails
     */
    public void setConversionError(String valueConversionError) {
        this.conversionError = valueConversionError;
        requestRepaint();
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
            requestRepaint();
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
        requestRepaint();
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
        requestRepaint();
    }

    @Override
    public AbstractFieldState getState() {
        return (AbstractFieldState) super.getState();
    }

    @Override
    protected AbstractFieldState createState() {
        return new AbstractFieldState();
    }
}
