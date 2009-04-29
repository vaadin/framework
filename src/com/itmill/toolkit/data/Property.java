/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.data;

import java.io.Serializable;

/**
 * <p>
 * The <code>Property</code> is a simple data object that contains one typed
 * value. This interface contains methods to inspect and modify the stored value
 * and its type, and the object's read-only state.
 * </p>
 * 
 * <p>
 * The <code>Property</code> also defines the events
 * <code>ReadOnlyStatusChangeEvent</code> and <code>ValueChangeEvent</code>, and
 * the associated <code>listener</code> and <code>notifier</code> interfaces.
 * </p>
 * 
 * <p>
 * The <code>Property.Viewer</code> interface should be used to attach the
 * Property to an external data source. This way the value in the data source
 * can be inspected using the <code>Property</code> interface.
 * </p>
 * 
 * <p>
 * The <code>Property.editor</code> interface should be implemented if the value
 * needs to be changed through the implementing class.
 * </p>
 * 
 * @author IT Mill Ltd
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface Property extends Serializable {

    /**
     * Gets the value stored in the Property. The returned object is compatible
     * with the class returned by getType().
     * 
     * @return the value stored in the Property
     */
    public Object getValue();

    /**
     * Sets the value of the Property.
     * <p>
     * Implementing this functionality is optional. If the functionality is
     * missing, one should declare the Property to be in read-only mode and
     * throw <code>Property.ReadOnlyException</code> in this function.
     * </p>
     * Note : It is not required, but highly recommended to support setting the
     * value also as a <code>String</code> in addition to the native type of the
     * Property (as given by the <code>getType</code> method). If the
     * <code>String</code> conversion fails or is unsupported, the method should
     * throw <code>Property.ConversionException</code>. The string conversion
     * should at least understand the format returned by the
     * <code>toString</code> method of the Property.
     * 
     * @param newValue
     *            New value of the Property. This should be assignable to the
     *            type returned by getType, but also String type should be
     *            supported
     * 
     * @throws Property.ReadOnlyException
     *             if the object is in read-only mode
     * @throws Property.ConversionException
     *             if newValue can't be converted into the Property's native
     *             type directly or through String
     */
    public void setValue(Object newValue) throws Property.ReadOnlyException,
            Property.ConversionException;

    /**
     * Returns the value of the Property in human readable textual format. The
     * return value should be assignable to the <code>setValue</code> method if
     * the Property is not in read-only mode.
     * 
     * @return <code>String</code> representation of the value stored in the
     *         Property
     */
    public String toString();

    /**
     * Returns the type of the Property. The methods <code>getValue</code> and
     * <code>setValue</code> must be compatible with this type: one must be able
     * to safely cast the value returned from <code>getValue</code> to the given
     * type and pass any variable assignable to this type as an argument to
     * <code>setValue</code>.
     * 
     * @return type of the Property
     */
    public Class<?> getType();

    /**
     * Tests if the Property is in read-only mode. In read-only mode calls to
     * the method <code>setValue</code> will throw
     * <code>ReadOnlyException</code> and will not modify the value of the
     * Property.
     * 
     * @return <code>true</code> if the Property is in read-only mode,
     *         <code>false</code> if it's not
     */
    public boolean isReadOnly();

    /**
     * Sets the Property's read-only mode to the specified status.
     * 
     * This functionality is optional, but all properties must implement the
     * <code>isReadOnly</code> mode query correctly.
     * 
     * @param newStatus
     *            new read-only status of the Property
     */
    public void setReadOnly(boolean newStatus);

    /**
     * <code>Exception</code> object that signals that a requested Property
     * modification failed because it's in read-only mode.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    @SuppressWarnings("serial")
    public class ReadOnlyException extends RuntimeException {

        /**
         * Constructs a new <code>ReadOnlyException</code> without a detail
         * message.
         */
        public ReadOnlyException() {
        }

        /**
         * Constructs a new <code>ReadOnlyException</code> with the specified
         * detail message.
         * 
         * @param msg
         *            the detail message
         */
        public ReadOnlyException(String msg) {
            super(msg);
        }
    }

    /**
     * An exception that signals that the value passed to the
     * <code>setValue</code> method couldn't be converted to the native type of
     * the Property.
     * 
     * @author IT Mill Ltd
     * @version
     * @VERSION@
     * @since 3.0
     */
    @SuppressWarnings("serial")
    public class ConversionException extends RuntimeException {

        /**
         * Constructs a new <code>ConversionException</code> without a detail
         * message.
         */
        public ConversionException() {
        }

        /**
         * Constructs a new <code>ConversionException</code> with the specified
         * detail message.
         * 
         * @param msg
         *            the detail message
         */
        public ConversionException(String msg) {
            super(msg);
        }

        /**
         * Constructs a new <code>ConversionException</code> from another
         * exception.
         * 
         * @param cause
         *            The cause of the the conversion failure
         */
        public ConversionException(Throwable cause) {
            super(cause.toString());
        }
    }

    /**
     * Interface implemented by the viewer classes capable of using a Property
     * as a data source.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface Viewer extends Serializable {

        /**
         * Sets the Property that serves as the data source of the viewer.
         * 
         * @param newDataSource
         *            the new data source Property
         */
        public void setPropertyDataSource(Property newDataSource);

        /**
         * Gets the Property serving as the data source of the viewer.
         * 
         * @return the Property serving as the viewers data source
         */
        public Property getPropertyDataSource();
    }

    /**
     * Interface implemented by the editor classes capable of editing the
     * Property.
     * <p>
     * Implementing this interface means that the Property serving as the data
     * source of the editor can be modified through the editor. It does not
     * restrict the editor from editing the Property internally, though if the
     * Property is in a read-only mode, attempts to modify it will result in the
     * <code>ReadOnlyException</code> being thrown.
     * </p>
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface Editor extends Property.Viewer, Serializable {

    }

    /* Value change event */

    /**
     * An <code>Event</code> object specifying the Property whose value has been
     * changed.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface ValueChangeEvent extends Serializable {

        /**
         * Retrieves the Property that has been modified.
         * 
         * @return source Property of the event
         */
        public Property getProperty();
    }

    /**
     * The <code>listener</code> interface for receiving
     * <code>ValueChangeEvent</code> objects.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface ValueChangeListener extends Serializable {

        /**
         * Notifies this listener that the Property's value has changed.
         * 
         * @param event
         *            value change event object
         */
        public void valueChange(Property.ValueChangeEvent event);
    }

    /**
     * The interface for adding and removing <code>ValueChangeEvent</code>
     * listeners. If a Property wishes to allow other objects to receive
     * <code>ValueChangeEvent</code> generated by it, it must implement this
     * interface.
     * <p>
     * Note : The general Java convention is not to explicitly declare that a
     * class generates events, but to directly define the
     * <code>addListener</code> and <code>removeListener</code> methods. That
     * way the caller of these methods has no real way of finding out if the
     * class really will send the events, or if it just defines the methods to
     * be able to implement an interface.
     * </p>
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface ValueChangeNotifier extends Serializable {

        /**
         * Registers a new value change listener for this Property.
         * 
         * @param listener
         *            the new Listener to be registered
         */
        public void addListener(Property.ValueChangeListener listener);

        /**
         * Removes a previously registered value change listener.
         * 
         * @param listener
         *            listener to be removed
         */
        public void removeListener(Property.ValueChangeListener listener);
    }

    /* ReadOnly Status change event */

    /**
     * An <code>Event</code> object specifying the Property whose read-only
     * status has been changed.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface ReadOnlyStatusChangeEvent extends Serializable {

        /**
         * Property whose read-only state has changed.
         * 
         * @return source Property of the event.
         */
        public Property getProperty();
    }

    /**
     * The listener interface for receiving
     * <code>ReadOnlyStatusChangeEvent</code> objects.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface ReadOnlyStatusChangeListener extends Serializable {

        /**
         * Notifies this listener that a Property's read-only status has
         * changed.
         * 
         * @param event
         *            Read-only status change event object
         */
        public void readOnlyStatusChange(
                Property.ReadOnlyStatusChangeEvent event);
    }

    /**
     * The interface for adding and removing
     * <code>ReadOnlyStatusChangeEvent</code> listeners. If a Property wishes to
     * allow other objects to receive <code>ReadOnlyStatusChangeEvent</code>
     * generated by it, it must implement this interface.
     * <p>
     * Note : The general Java convention is not to explicitly declare that a
     * class generates events, but to directly define the
     * <code>addListener</code> and <code>removeListener</code> methods. That
     * way the caller of these methods has no real way of finding out if the
     * class really will send the events, or if it just defines the methods to
     * be able to implement an interface.
     * </p>
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface ReadOnlyStatusChangeNotifier extends Serializable {

        /**
         * Registers a new read-only status change listener for this Property.
         * 
         * @param listener
         *            the new Listener to be registered
         */
        public void addListener(Property.ReadOnlyStatusChangeListener listener);

        /**
         * Removes a previously registered read-only status change listener.
         * 
         * @param listener
         *            listener to be removed
         */
        public void removeListener(
                Property.ReadOnlyStatusChangeListener listener);
    }
}
