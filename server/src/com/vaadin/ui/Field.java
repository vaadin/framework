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

import com.vaadin.data.BufferedValidatable;
import com.vaadin.data.Property;
import com.vaadin.ui.Component.Focusable;

/**
 * Field interface is implemented by all classes (field components) that have a
 * value that the user can change through the user interface.
 *
 * Field components are built upon the framework defined in the Field interface
 * and the {@link com.vaadin.AbstractField} base class.
 *
 * The Field interface inherits the {@link com.vaadin.ui.Component}
 * superinterface and also the {@link com.vaadin.ui.Property} interface to have
 * a value for the field.
 *
 *
 * @author Vaadin Ltd.
 *
 * @param T
 *            the type of values in the field, which might not be the same type
 *            as that of the data source if converters are used
 *
 * @author IT Mill Ltd.
 */
public interface Field<T> extends Component, BufferedValidatable, Property<T>,
        Property.ValueChangeNotifier, Property.ValueChangeListener,
        Property.Editor, Focusable {

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
     * @author Vaadin Ltd.
     * @since 3.0
     */
    @SuppressWarnings("serial")
    public static class ValueChangeEvent extends Component.Event implements
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
        @Override
        public Property getProperty() {
            return (Property) getSource();
        }
    }
}
