/* *************************************************************************
 
 Millstone(TM) 
 Open Sourced User Interface Library for
 Internet Development with Java

 Millstone is a registered trademark of IT Mill Ltd
 Copyright (C) 2000-2005 IT Mill Ltd
 
 *************************************************************************

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 license version 2.1 as published by the Free Software Foundation.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:  +358 2 4802 7181
 20540, Turku                          email: info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for MillStone information and releases: www.millstone.org

 ********************************************************************** */

package com.itmill.tk.ui;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import com.itmill.tk.data.Buffered;
import com.itmill.tk.data.Property;
import com.itmill.tk.data.Validatable;
import com.itmill.tk.data.Validator;
import com.itmill.tk.terminal.ErrorMessage;
import com.itmill.tk.terminal.PaintException;
import com.itmill.tk.terminal.PaintTarget;

/**
 * <p>
 * Abstract field component for implementing buffered property editors. The
 * field may hold an internal value, or it may be connected to any data source
 * that implements the {@link com.itmill.tk.data.Property}interface.
 * <code>AbstractField</code> implements that interface itself, too, so
 * accessing the Property value represented by it is straightforward.
 * </p>
 * 
 * <p>
 * AbstractField also provides the {@link com.itmill.tk.data.Buffered}
 * interface for buffering the data source value. By default the Field is in
 * write through-mode and {@link #setWriteThrough(boolean)}should be called to
 * enable buffering.
 * </p>
 * 
 * <p>
 * The class also supports {@link com.itmill.tk.data.Validator validators}
 * to make sure the value contained in the field is valid.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public abstract class AbstractField extends AbstractComponent implements Field,
        Property.ReadOnlyStatusChangeNotifier {

    /* Private members ************************************************* */

    private boolean delayedFocus;

    /** Value of the datafield */
    private Object value;

    /** Connected data-source. */
    private Property dataSource = null;

    /** The list of validators. */
    private LinkedList validators = null;

    /** Auto commit mode. */
    private boolean writeTroughMode = true;

    /** Read the value from data-source, when it is not modified. */
    private boolean readTroughMode = true;

    /** Is the field modified but not committed. */
    private boolean modified = false;

    /** Current source exception */
    private Buffered.SourceException currentBufferedSourceException = null;

    /** Are the invalid values alloved in fields ? */
    private boolean invalidAllowed = true;

    /** Are the invalid values committed */
    private boolean invalidCommitted = false;

    /** The tab order number of this field */
    private int tabIndex = 0;

    /** Unique focusable id */
    private long focusableId = -1;

    /** Required field */
    private boolean required = false;

    /* Component basics ************************************************ */

    public AbstractField() {
        this.focusableId = Window.getNewFocusableId(this);
    }

    /*
     * Paint the field. Don't add a JavaDoc comment here, we use the default
     * documentation from the implemented interface.
     */
    public void paintContent(PaintTarget target) throws PaintException {

        // Focus control id
        if (this.focusableId > 0) {
            target.addAttribute("focusid", this.focusableId);
        }

        // The tab ordering number
        if (this.tabIndex > 0)
            target.addAttribute("tabindex", this.tabIndex);

        // If the field is modified, but not committed, set modified attribute
        if (isModified())
            target.addAttribute("modified", true);

        // Add required attribute
        if (isRequired())
            target.addAttribute("required", true);

    }

    /*
     * Gets the field type Don't add a JavaDoc comment here, we use the default
     * documentation from the implemented interface.
     */
    public abstract Class getType();

    /**
     * The abstract field is read only also if the data source is in readonly
     * mode.
     */
    public boolean isReadOnly() {
        return super.isReadOnly()
                || (dataSource != null && dataSource.isReadOnly());
    }

    /**
     * Change the readonly state and throw read-only status change events.
     * 
     * @see com.itmill.tk.ui.Component#setReadOnly(boolean)
     */
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        fireReadOnlyStatusChange();
    }

    /* Buffering ******************************************************* */

    public boolean isInvalidCommitted() {
        return invalidCommitted;
    }

    public void setInvalidCommitted(boolean isCommitted) {
        this.invalidCommitted = isCommitted;
    }

    /*
     * Save the current value to the data source Don't add a JavaDoc comment
     * here, we use the default documentation from the implemented interface.
     */
    public void commit() throws Buffered.SourceException {
        if (dataSource != null && (isInvalidCommitted() || isValid())
                && !dataSource.isReadOnly()) {
            Object newValue = getValue();
            try {

                // Commit the value to datasource
                dataSource.setValue(newValue);

            } catch (Throwable e) {

                // Set the buffering state
                currentBufferedSourceException = new Buffered.SourceException(
                        this, e);
                requestRepaint();

                // Throw the source exception
                throw currentBufferedSourceException;
            }
        }

        boolean repaintNeeded = false;

        // The abstract field is not modified anymore
        if (modified) {
            modified = false;
            repaintNeeded = true;
        }

        // If successful, remove set the buffering state to be ok
        if (currentBufferedSourceException != null) {
            currentBufferedSourceException = null;
            repaintNeeded = true;
        }

        if (repaintNeeded)
            requestRepaint();
    }

    /*
     * Update the value from the data source. Don't add a JavaDoc comment here,
     * we use the default documentation from the implemented interface.
     */
    public void discard() throws Buffered.SourceException {
        if (dataSource != null) {

            // Get the correct value from datasource
            Object newValue;
            try {

                // Discard buffer by overwriting from datasource
                newValue = dataSource.getValue();

                // If successful, remove set the buffering state to be ok
                if (currentBufferedSourceException != null) {
                    currentBufferedSourceException = null;
                    requestRepaint();
                }
            } catch (Throwable e) {

                // Set the buffering state
                currentBufferedSourceException = new Buffered.SourceException(
                        this, e);
                requestRepaint();

                // Throw the source exception
                throw currentBufferedSourceException;
            }

            boolean wasModified = isModified();
            modified = false;

            // If the new value differs from the previous one
            if ((newValue == null && value != null)
                    || (newValue != null && !newValue.equals(value))) {
                setInternalValue(newValue);
                fireValueChange();
            }

            // If the value did not change, but the modification status did
            else if (wasModified) {
                requestRepaint();
            }
        }
    }

    /*
     * Has the field been modified since the last commit()? Don't add a JavaDoc
     * comment here, we use the default documentation from the implemented
     * interface.
     */
    public boolean isModified() {
        return modified;
    }

    /*
     * Test if the field is in write-through mode. Don't add a JavaDoc comment
     * here, we use the default documentation from the implemented interface.
     */
    public boolean isWriteThrough() {
        return writeTroughMode;
    }

    /*
     * Set the field's write-through mode to the specified status Don't add a
     * JavaDoc comment here, we use the default documentation from the
     * implemented interface.
     */
    public void setWriteThrough(boolean writeTrough)
            throws Buffered.SourceException {
        if (writeTroughMode == writeTrough)
            return;
        writeTroughMode = writeTrough;
        if (writeTroughMode)
            commit();
    }

    /*
     * Test if the field is in read-through mode. Don't add a JavaDoc comment
     * here, we use the default documentation from the implemented interface.
     */
    public boolean isReadThrough() {
        return readTroughMode;
    }

    /*
     * Set the field's read-through mode to the specified status Don't add a
     * JavaDoc comment here, we use the default documentation from the
     * implemented interface.
     */
    public void setReadThrough(boolean readTrough)
            throws Buffered.SourceException {
        if (readTroughMode == readTrough)
            return;
        readTroughMode = readTrough;
        if (!isModified() && readTroughMode && dataSource != null) {
            setInternalValue(dataSource.getValue());
            fireValueChange();
        }
    }

    /* Property interface implementation ******************************* */

    /**
     * Returns the value of the Property in human readable textual format.
     * 
     * @return <code>String</code> representation of the value stored in the
     *         Property
     */
    public String toString() {
        Object value = getValue();
        if (value == null)
            return null;
        return getValue().toString();
    }

    /**
     * Gets the current value of the field. This is the visible, modified and
     * possible invalid value the user have entered to the field. In the
     * read-through mode, the abstract buffer is also updated and validation is
     * performed.
     * 
     * @return the current value of the field
     */
    public Object getValue() {

        // Give the value from abstract buffers if the field if possible
        if (dataSource == null || !isReadThrough() || isModified())
            return value;

        Object newValue = dataSource.getValue();
        if ((newValue == null && value != null)
                || (newValue != null && !newValue.equals(value))) {
            setInternalValue(newValue);
            fireValueChange();
        }

        return newValue;
    }

    /**
     * Set the value of the field.
     * 
     * @param newValue
     *            New value of the field.
     */
    public void setValue(Object newValue) throws Property.ReadOnlyException,
            Property.ConversionException {

        if ((newValue == null && value != null)
                || (newValue != null && !newValue.equals(value))) {

            // Read only fields can not be changed
            if (isReadOnly())
                throw new Property.ReadOnlyException();

            // If invalid values are not allowed, the value must be checked
            if (!isInvalidAllowed()) {
                Collection v = getValidators();
                if (v != null)
                    for (Iterator i = v.iterator(); i.hasNext();)
                        ((Validator) i.next()).validate(newValue);
            }

            // Change the value
            setInternalValue(newValue);
            modified = dataSource != null;

            // In write trough mode , try to commit
            if (isWriteThrough() && dataSource != null
                    && (isInvalidCommitted() || isValid())) {
                try {

                    // Commit the value to datasource
                    dataSource.setValue(newValue);

                    // The buffer is now unmodified
                    modified = false;

                } catch (Throwable e) {

                    // Set the buffering state
                    currentBufferedSourceException = new Buffered.SourceException(
                            this, e);
                    requestRepaint();

                    // Throw the source exception
                    throw currentBufferedSourceException;
                }
            }

            // If successful, remove set the buffering state to be ok
            if (currentBufferedSourceException != null) {
                currentBufferedSourceException = null;
                requestRepaint();
            }

            // Fire value change
            fireValueChange();
        }
    }

    /* External data source ******************************************** */

    /**
     * Gets the current data source of the field, if any.
     * 
     * @return The current data source as a Property, or <code>null</code> if
     *         none defined.
     */
    public Property getPropertyDataSource() {
        return dataSource;
    }

    /**
     * <p>
     * Sets the specified Property as the data source for the field. All
     * uncommitted changes to the field are discarded and the value is refreshed
     * from the new data source.
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
     * @param newDataSource
     *            the new data source Property
     */
    public void setPropertyDataSource(Property newDataSource) {

        // Save the old value
        Object oldValue = value;

        // Discard all changes to old datasource
        try {
            discard();
        } catch (Buffered.SourceException ignored) {
        }

        // Stop listening the old data source changes
        if (dataSource != null
                && Property.ValueChangeNotifier.class
                        .isAssignableFrom(dataSource.getClass()))
            ((Property.ValueChangeNotifier) dataSource).removeListener(this);

        // Set the new data source
        dataSource = newDataSource;

        // Get the value from source
        try {
            if (dataSource != null)
                setInternalValue(dataSource.getValue());
            modified = false;
        } catch (Throwable e) {
            currentBufferedSourceException = new Buffered.SourceException(this,
                    e);
            modified = true;
        }

        // Listen the new data source if possible
        if (dataSource instanceof Property.ValueChangeNotifier)
            ((Property.ValueChangeNotifier) dataSource).addListener(this);

        // Copy the validators from the data source
        if (dataSource instanceof Validatable) {
            Collection validators = ((Validatable) dataSource).getValidators();
            if (validators != null)
                for (Iterator i = validators.iterator(); i.hasNext();)
                    addValidator((Validator) i.next());
        }

        // Fire value change if the value has changed
        if ((value != oldValue)
                && ((value != null && !value.equals(oldValue)) || value == null))
            fireValueChange();
    }

    /* Validation ****************************************************** */

    /**
     * Adds a new validator for the field's value. All validators added to a
     * field are checked each time the its value changes.
     * 
     * @param validator
     *            the new validator to be added
     */
    public void addValidator(Validator validator) {
        if (validators == null)
            validators = new LinkedList();
        validators.add(validator);
    }

    /**
     * Gets the validators of the field.
     * 
     * @return Unmodifiable collection that holds all validators for the field.
     */
    public Collection getValidators() {
        if (validators == null || validators.isEmpty())
            return null;
        return Collections.unmodifiableCollection(validators);
    }

    /**
     * Removes a validator from the field.
     * 
     * @param validator
     *            the validator to remove
     */
    public void removeValidator(Validator validator) {
        if (validators != null)
            validators.remove(validator);
    }

    /**
     * Tests the current value against all registered validators.
     * 
     * @return <code>true</code> if all registered validators claim that the
     *         current value is valid, <code>false</code> otherwise
     */
    public boolean isValid() {

        if (validators == null)
            return true;

        Object value = getValue();
        for (Iterator i = validators.iterator(); i.hasNext();)
            if (!((Validator) i.next()).isValid(value))
                return false;

        return true;
    }

    public void validate() throws Validator.InvalidValueException {

        // If there is no validator, there can not be any errors
        if (validators == null)
            return;

        // Initialize temps
        Validator.InvalidValueException firstError = null;
        LinkedList errors = null;
        Object value = getValue();

        // Get all the validation errors
        for (Iterator i = validators.iterator(); i.hasNext();)
            try {
                ((Validator) i.next()).validate(value);
            } catch (Validator.InvalidValueException e) {
                if (firstError == null)
                    firstError = e;
                else {
                    if (errors == null) {
                        errors = new LinkedList();
                        errors.add(firstError);
                    }
                    errors.add(e);
                }
            }

        // If there were no error
        if (firstError == null)
            return;

        // If only one error occurred, throw it forwards
        if (errors == null)
            throw firstError;

        // Create composite validator
        Validator.InvalidValueException[] exceptions = new Validator.InvalidValueException[errors
                .size()];
        int index = 0;
        for (Iterator i = errors.iterator(); i.hasNext();)
            exceptions[index++] = (Validator.InvalidValueException) i.next();

        throw new Validator.InvalidValueException(null, exceptions);
    }

    /**
     * Fields allow invalid values by default. In most cases this is wanted,
     * because the field otherwise visually forget the user input immediately.
     * 
     * @see com.itmill.tk.data.Validatable#isInvalidAllowed()
     * 
     * @return true iff the invalid values are allowed.
     */
    public boolean isInvalidAllowed() {
        return invalidAllowed;
    }

    /**
     * Fields allow invalid values by default. In most cases this is wanted,
     * because the field otherwise visually forget the user input immediately.
     * In common setting where the user wants to assure the correctness of the
     * datasource, but allow temporarily invalid contents in the field, the user
     * should add the validators to datasource, that should not allow invalid
     * values. The validators are automatically copied to the field when the
     * datasource is set.
     * 
     * @see com.itmill.tk.data.Validatable#setInvalidAllowed(boolean)
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
     * @see com.itmill.tk.ui.AbstractComponent#getErrorMessage()
     */
    public ErrorMessage getErrorMessage() {
        ErrorMessage superError = super.getErrorMessage();
        return superError;
        /*
         * TODO: Check the logic of this ErrorMessage validationError = null;
         * try { validate(); } catch (Validator.InvalidValueException e) {
         * validationError = e; }
         * 
         * if (superError == null && validationError == null &&
         * currentBufferedSourceException == null) return null; // Throw
         * combination of the error types return new CompositeErrorMessage( new
         * ErrorMessage[] { superError, validationError,
         * currentBufferedSourceException });
         */

    }

    /* Value change events ****************************************** */

    private static final Method VALUE_CHANGE_METHOD;

    static {
        try {
            VALUE_CHANGE_METHOD = Property.ValueChangeListener.class
                    .getDeclaredMethod("valueChange",
                            new Class[] { Property.ValueChangeEvent.class });
        } catch (java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException();
        }
    }

    /*
     * Add a value change listener for the field. Don't add a JavaDoc comment
     * here, we use the default documentation from the implemented interface.
     */
    public void addListener(Property.ValueChangeListener listener) {
        addListener(AbstractField.ValueChangeEvent.class, listener,
                VALUE_CHANGE_METHOD);
    }

    /*
     * Remove a value change listener from the field. Don't add a JavaDoc
     * comment here, we use the default documentation from the implemented
     * interface.
     */
    public void removeListener(Property.ValueChangeListener listener) {
        removeListener(AbstractField.ValueChangeEvent.class, listener,
                VALUE_CHANGE_METHOD);
    }

    /**
     * Emit a value change event. The value contained in the field is validated
     * before the event is created.
     */
    protected void fireValueChange() {
        fireEvent(new AbstractField.ValueChangeEvent(this));
        requestRepaint();
    }

    /* Read-only status change events *************************************** */

    private static final Method READ_ONLY_STATUS_CHANGE_METHOD;

    static {
        try {
            READ_ONLY_STATUS_CHANGE_METHOD = Property.ReadOnlyStatusChangeListener.class
                    .getDeclaredMethod(
                            "readOnlyStatusChange",
                            new Class[] { Property.ReadOnlyStatusChangeEvent.class });
        } catch (java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException();
        }
    }

    /**
     * An <code>Event</code> object specifying the Property whose read-only
     * status has changed.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public class ReadOnlyStatusChangeEvent extends Component.Event implements
            Property.ReadOnlyStatusChangeEvent {

        /**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3258688823264161846L;

        /**
         * New instance of text change event
         * 
         * @param source
         *            Source of the event.
         */
        public ReadOnlyStatusChangeEvent(AbstractField source) {
            super(source);
        }

        /**
         * Property where the event occurred
         * 
         * @return Source of the event.
         */
        public Property getProperty() {
            return (Property) getSource();
        }
    }

    /*
     * Add a read-only status change listener for the field. Don't add a JavaDoc
     * comment here, we use the default documentation from the implemented
     * interface.
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
     * Emit a read-only status change event. The value contained in the field is
     * validated before the event is created.
     */
    protected void fireReadOnlyStatusChange() {
        fireEvent(new AbstractField.ReadOnlyStatusChangeEvent(this));
    }

    /**
     * This method listens to data source value changes and passes the changes
     * forwards.
     * 
     * @param event
     *            the value change event telling the data source contents have
     *            changed
     */
    public void valueChange(Property.ValueChangeEvent event) {
        if (isReadThrough() || !isModified())
            fireValueChange();
    }

    /** Ask the terminal to place the cursor to this field. */
    public void focus() {
        Window w = getWindow();
        if (w != null) {
            w.setFocusedComponent(this);
        } else {
            this.delayedFocus = true;
        }
    }

    /**
     * Create abstract field by the type of the property.
     * 
     * <p>
     * This returns most suitable field type for editing property of given type
     * </p>
     * 
     * @param propertyType
     *            Type of the property, that needs to be edited.
     */
    public static AbstractField constructField(Class propertyType) {

        // Null typed properties can not be edited
        if (propertyType == null)
            return null;

        // Date field
        if (Date.class.isAssignableFrom(propertyType)) {
            return new DateField();
        }

        // Boolean field
        if (Boolean.class.isAssignableFrom(propertyType)) {
            Button button = new Button("");
            button.setSwitchMode(true);
            button.setImmediate(false);
            return button;
        }

        // Text field is used by default
        return new TextField();
    }

    /**
     * Get the tab index of this field. The tab index property is used to
     * specify the natural tab ordering of fields.
     * 
     * @return Tab index of this field. Negative value means unspecified.
     */
    public int getTabIndex() {
        return tabIndex;
    }

    /**
     * Get the tab index of this field. The tab index property is used to
     * specify the natural tab ordering of fields.
     * 
     * @param tabIndex
     *            The tab order of this component. Negative value means
     *            unspecified.
     */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    /**
     * Set the internal field value. This is purely used by AbstractField to
     * change the internal Field value. It does not trigger any events. It can
     * be overriden by the inheriting classes to update all dependent variables.
     * 
     * @param newValue
     *            The new value to be set.
     */
    protected void setInternalValue(Object newValue) {
        this.value = newValue;
    }

    /**
     * @see com.itmill.tk.ui.Component.Focusable#getFocusableId()
     */
    public long getFocusableId() {
        return this.focusableId;
    }

    /**
     * @see com.itmill.tk.ui.Component#attach()
     */
    public void attach() {
        super.attach();
        if (this.delayedFocus) {
            this.delayedFocus = false;
            this.focus();
        }
    }

    /**
     * Is this field required.
     * 
     * Required fields must filled by the user.
     * 
     * @return true if the
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Set the field required. Required fields must filled by the user.
     * 
     * @param required
     *            Is the field required
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Free used resources.
     */
    public void finalize() throws Throwable {
        if (focusableId > -1) {
            Window.removeFocusableId(focusableId);
        }
        super.finalize();
    }
}