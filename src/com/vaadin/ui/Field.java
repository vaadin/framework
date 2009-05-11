/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.data.BufferedValidatable;
import com.vaadin.data.Property;
import com.vaadin.ui.Component.Focusable;

/**
 * @author IT Mill Ltd.
 * 
 */
public interface Field extends Component, BufferedValidatable, Property,
        Property.ValueChangeNotifier, Property.ValueChangeListener,
        Property.Editor, Focusable {

    /**
     * Sets the Caption.
     * 
     * @param caption
     */
    void setCaption(String caption);

    String getDescription();

    /**
     * Sets the Description.
     * 
     * @param caption
     */
    void setDescription(String caption);

    /**
     * Is this field required.
     * 
     * Required fields must filled by the user.
     * 
     * @return <code>true</code> if the field is required,otherwise
     *         <code>false</code>.
     * @since 3.1
     */
    public boolean isRequired();

    /**
     * Sets the field required. Required fields must filled by the user.
     * 
     * @param required
     *            Is the field required.
     * @since 3.1
     */
    public void setRequired(boolean required);

    /**
     * Sets the error message to be displayed if a required field is empty.
     * 
     * @param requiredMessage
     *            Error message.
     * @since 5.2.6
     */
    public void setRequiredError(String requiredMessage);

    /**
     * Gets the error message that is to be displayed if a required field is
     * empty.
     * 
     * @return Error message.
     * @since 5.2.6
     */
    public String getRequiredError();

    /**
     * An <code>Event</code> object specifying the Field whose value has been
     * changed.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    @SuppressWarnings("serial")
    public class ValueChangeEvent extends Component.Event implements
            Property.ValueChangeEvent {

        /**
         * Constructs a new event object with the specified source field object.
         * 
         * @param source
         *            the field that caused the event.
         */
        public ValueChangeEvent(Field source) {
            super(source);
        }

        /**
         * Gets the Property which triggered the event.
         * 
         * @return the Source Property of the event.
         */
        public Property getProperty() {
            return (Property) getSource();
        }
    }
}
