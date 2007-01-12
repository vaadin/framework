/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Interfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license.pdf. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.data;

/** Property is a simple data object that contains one typed value. This
 * interface contains methods to inspect and modify the stored value and its
 * type, and the object's read-only state.  
 * 
 * Property also defines the events ReadOnlyStatusChangeEvent and
 * ValueChangeEvent, and the associated listener and notifier interfaces.
 *
 * The Property.Viewer interface should be used to attach the Property to
 * an external data source. This way the value in the data source can be
 * inspected using the Property interface.
 * 
 * The Property.editor interface should be implemented if the value needs to
 * be changed through the implementing class.
 * 
 * @author  IT Mill Ltd
 * @version @VERSION@
 * @since 3.0
 */
public interface Property {
    
    /** Gets the value stored in the Property.
     * 
     * @return the value stored in the Property
     */
    public Object getValue();
    
    /** Sets the value of the Property.
     *
     * Implementing this functionality is optional. If the functionality
     * is missing, one should declare the Property to be in read-only mode
     * and throw Property.ReadOnlyException in this function.
     *
     * It is not required, but highly recommended to support setting
     * the value also as a <code>String</code> in addition to the native
     * type of the Property (as given by the <code>getType</code> method).
     * If the <code>String</code> conversion fails or is unsupported, the
     * method should throw </code>Property.ConversionException</code>. The
     * string conversion should at least understand the format returned by
     * the <code>toString()</code> method of the Property.
     *
     * @param newValue New value of the Property. This should be assignable
     * to the type returned by <code>getType</code>, but also String type
     * should be supported
     *
     * @throws Property.ReadOnlyException if the object is in read-only
     * mode
     * @throws Property.ConversionException if <code>newValue</code> can't
     * be converted into the Property's native type directly or through
     * String
     */
    public void setValue(Object newValue)
    throws Property.ReadOnlyException, Property.ConversionException;
    
    /** Returns the value of the Property in human readable textual format.
     * The return value should be assignable to the <code>setValue</code>
     * method if the Property is not in read-only mode.
     * 
     * @return <code>String</code> representation of the value stored in the
     * Property
     */
    public String toString();
    
    /** Returns the type of the Property. The methods <code>getValue</code>
     * and <code>setValue</code> must be compatible with this type: one
     * must be able to safely cast the value returned from
     * <code>getValue</code> to the given type and pass any variable
     * assignable to this type as an argument to <code>setValue</code>.
     * 
     * @return type of the Property
     */
    public Class getType();
    
    /** Tests if the Property is in read-only mode. In read-only mode calls
     * to the method <code>setValue</code> will throw
     * <code>ReadOnlyException</code>s and will not modify the value of the
     * Property.
     *
     * @return <code>true</code> if the Property is in read-only mode,
     * <code>false</code> if it's not
     */
    public boolean isReadOnly();
    
    /** Sets the Property's read-only mode to the specified status.
     * 
     * This functionality is optional, but all properties must implement
     * the <code>isReadOnly()</code> mode query correctly.
     * 
     * @param newStatus new read-only status of the Property
     */	
    public void setReadOnly(boolean newStatus);
    
    /** <code>Exception</code> object that signals that a requested
     * Property modification failed because it's in read-only mode.
     * @author IT Mill Ltd.
     * @version @VERSION@
     * @since 3.0
     */
    public class ReadOnlyException extends RuntimeException {
        
	   /**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3257571702287119410L;

    /** Constructs a new <code>ReadOnlyException</code> without a detail
         * message.
         */
        public ReadOnlyException() {
        }
        
        /** Constructs a new <code>ReadOnlyException</code> with the
         * specified detail message.
         * 
         * @param msg the detail message
         */
        public ReadOnlyException(String msg) {
            super(msg);
        }
    }
    
    /** An exception that signals that the value passed to the
     * <code>setValue()</code> method couldn't be converted to the native
     * type of the Property.
     * @author IT Mill Ltd
     * @version @VERSION@
     * @since 3.0
     */
    public class ConversionException extends RuntimeException {
        
        /**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3257571706666366008L;

        /** Constructs a new <code>ConversionException</code> without a
         * detail message.
         */
        public ConversionException() {
        }
        
        /** Constructs a new <code>ConversionException</code> with the
         * specified detail message.
         * 
         * @param msg the detail message
         */
        public ConversionException(String msg) {
            super(msg);
        }
        
        /** Constructs a new <code>ConversionException</code> from another
         * exception.
         * 
         * @param cause The cause of the the conversion failure
         */
        public ConversionException(Throwable cause) {
            super(cause.toString());
        }
    }
    
    /** Interface implemented by the viewer classes capable of using a
     * Property as a data source.
     * @author IT Mill Ltd.
     * @version @VERSION@
     * @since 3.0
     */
    public interface Viewer {
        
        /** Set the Property that serves as the data source of the viewer.
         * 
         * @param newDataSource the new data source Property
         */
        public void setPropertyDataSource(Property newDataSource);
        
        /** Get the Property serving as the data source of the viewer.
         * 
         * @return the Property serving as the viewers data source
         */
        public Property getPropertyDataSource();
    }
    
    /** Interface implemented by the editor classes capable of editing the
     * Property. Implementing this interface means that the Property serving
     * as the data source of the editor can be modified through the editor.
     * It does not restrict the editor from editing the Property internally,
     * though if the Property is in a read-only mode, attempts to modify it
     * will result in the <code>ReadOnlyException</code> being thrown.
     * @author IT Mill Ltd.
     * @version @VERSION@
     * @since 3.0
     */
    public interface Editor extends Property.Viewer {
        
    }
    
    /* Value change event ******************************************* */
    
    /** An <code>Event</code> object specifying the Property whose value
     * has been changed.
     * @author IT Mill Ltd.
     * @version @VERSION@
     * @since 3.0
     */
    public interface ValueChangeEvent {
        
        /** Retrieves the Property that has been modified.
         * 
         * @return source Property of the event
         */
        public Property getProperty();
    }
    
    /** The listener interface for receiving ValueChangeEvent objects.
     * @author IT Mill Ltd.
     * @version @VERSION@
     * @since 3.0
     **/
    public interface ValueChangeListener {
        
        /** Notifies this listener that the Property's value has changed.
         * 
         * @param event value change event object
         */
        public void valueChange(Property.ValueChangeEvent event);
    }
    
    /** The interface for adding and removing <code>ValueChangeEvent</code>
     * listeners. If a Property wishes to allow other objects to receive
     * <code>ValueChangeEvent</code>s generated by it, it must implement
     * this interface.
     * 
     * Note that the general Java convention is not to explicitly declare
     * that a class generates events, but to directly define the
     * <code>addListener</code> and <code>removeListener</code> methods.
     * That way the caller of these methods has no real way of finding out
     * if the class really will send the events, or if it just defines the
     * methods to be able to implement an interface.
     * @author IT Mill Ltd.
     * @version @VERSION@
     * @since 3.0
     */
    public interface ValueChangeNotifier {
        
        /** Registers a new value change listener for this Property.
         * 
         * @param listener the new Listener to be registered
         */
        public void addListener(Property.ValueChangeListener listener);
        
        /** Removes a previously registered value change listener.
         * 
         * @param listener listener to be removed
         */
        public void removeListener(Property.ValueChangeListener listener);
    }
    
    /* ReadOnly Status change event ***************************************** */
    
    /** An <code>Event</code> object specifying the Property whose read-only
     * status has been changed.
     * @author IT Mill Ltd.
     * @version @VERSION@
     * @since 3.0
     */
    public interface ReadOnlyStatusChangeEvent {
        
        /** Property whose read-only state has changed.
         * 
         * @return source Property of the event.
         */
        public Property getProperty();
    }
    
    /** The listener interface for receiving ReadOnlyStatusChangeEvent
     * objects. 
     * @author IT Mill Ltd.
     * @version @VERSION@
     * @since 3.0
     * */
    public interface ReadOnlyStatusChangeListener {
        
        /** Notifies this listener that a Property's read-only status has
         * changed.
         * 
         * @param event Read-only status change event object
         */
        public void readOnlyStatusChange(
        Property.ReadOnlyStatusChangeEvent event);
    }
    
    /** The interface for adding and removing
     * <code>ReadOnlyStatusChangeEvent</code> listeners. If a Property
     * wishes to allow other objects to receive
     * <code>ReadOnlyStatusChangeEvent</code>s generated by it, it must
     * implement this interface.
     * 
     * Note that the general Java convention is not to explicitly declare
     * that a class generates events, but to directly define the
     * <code>addListener</code> and <code>removeListener</code> methods.
     * That way the caller of these methods has no real way of finding out
     * if the class really will send the events, or if it just defines the
     * methods to be able to implement an interface.
     * @author IT Mill Ltd.
     * @version @VERSION@
     * @since 3.0
     */
    public interface ReadOnlyStatusChangeNotifier {
        
        /** Registers a new read-only status change listener for this
         * Property.
         * 
         * @param listener the new Listener to be registered
         */
        public void addListener(Property.ReadOnlyStatusChangeListener listener);
        
        /** Remove a previously registered read-only status change listener.
         * 
         * @param listener listener to be removed
         */
        public void removeListener(
        Property.ReadOnlyStatusChangeListener listener);
    }
}
