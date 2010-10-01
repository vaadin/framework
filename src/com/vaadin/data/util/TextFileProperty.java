/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.LinkedList;

import com.vaadin.data.Property;

/**
 * Property implementation for wrapping a text file.
 * 
 * Supports reading and writing of a File from/to String. ValueChangeNotifiers
 * are supported, but only fire when setValue(Object) is explicitly called.
 */
@SuppressWarnings("serial")
public class TextFileProperty implements Property, Property.ValueChangeNotifier {

    private File file;
    private boolean readonly;
    private Charset charset = null;
    private LinkedList<Property.ValueChangeListener> valueChangeListeners = null;

    /**
     * Wrap given file with property interface.
     * 
     * Setting the file to null works, but getValue() will return null.
     * 
     * @param file
     *            File to be wrapped.
     */
    public TextFileProperty(File file) {
        this.file = file;
    }

    /**
     * Wrap the given file with the property interface and specify character
     * set.
     * 
     * Setting the file to null works, but getValue() will return null.
     * 
     * @param file
     *            File to be wrapped.
     * @param charset
     *            Charset to be used for reading and writing the file.
     */
    public TextFileProperty(File file, Charset charset) {
        this.file = file;
        this.charset = charset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property#getType()
     */
    public Class<?> getType() {
        return String.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property#getValue()
     */
    public Object getValue() {
        if (file == null) {
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = charset == null ? new InputStreamReader(fis)
                    : new InputStreamReader(fis, charset);
            BufferedReader r = new BufferedReader(isr);
            StringBuilder b = new StringBuilder();
            char buf[] = new char[8 * 1024];
            int len;
            while ((len = r.read(buf)) != -1) {
                b.append(buf, 0, len);
            }
            r.close();
            isr.close();
            fis.close();
            return b.toString();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property#isReadOnly()
     */
    public boolean isReadOnly() {
        return file == null || readonly || !file.canWrite();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property#setReadOnly(boolean)
     */
    public void setReadOnly(boolean newStatus) {
        readonly = newStatus;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property#setValue(java.lang.Object)
     */
    public void setValue(Object newValue) throws ReadOnlyException {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }
        if (file == null) {
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = charset == null ? new OutputStreamWriter(
                    fos) : new OutputStreamWriter(fos, charset);
            BufferedWriter w = new BufferedWriter(osw);
            w.append(newValue.toString());
            w.flush();
            w.close();
            osw.close();
            fos.close();
            fireValueChange();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (String) getValue();
    }

    /* Events */

    /**
     * An <code>Event</code> object specifying the TextFileProperty whose value
     * has changed.
     */
    private class ValueChangeEvent extends java.util.EventObject implements
            Property.ValueChangeEvent {

        /**
         * Constructs a new value change event for this object.
         * 
         * @param source
         *            the source object of the event.
         */
        protected ValueChangeEvent(TextFileProperty source) {
            super(source);
        }

        /**
         * Gets the Property whose read-only state has changed.
         * 
         * @return source the Property of the event.
         */
        public Property getProperty() {
            return (Property) getSource();
        }
    }

    /**
     * Removes a previously registered value change listener.
     * 
     * @param listener
     *            the listener to be removed.
     */
    public void removeListener(Property.ValueChangeListener listener) {
        if (valueChangeListeners != null) {
            valueChangeListeners.remove(listener);
        }
    }

    /**
     * Registers a new value change listener for this TextFileProperty.
     * 
     * @param listener
     *            the new Listener to be registered
     */
    public void addListener(Property.ValueChangeListener listener) {
        if (valueChangeListeners == null) {
            valueChangeListeners = new LinkedList<Property.ValueChangeListener>();
        }
        valueChangeListeners.add(listener);
    }

    /**
     * Sends a value change event to all registered listeners.
     */
    private void fireValueChange() {
        if (valueChangeListeners != null) {
            final Object[] l = valueChangeListeners.toArray();
            final ValueChangeEvent event = new ValueChangeEvent(this);
            for (int i = 0; i < l.length; i++) {
                ((ValueChangeListener) l[i]).valueChange(event);
            }
        }
    }

}
