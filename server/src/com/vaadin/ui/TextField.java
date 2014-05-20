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

import com.vaadin.data.Property;

/**
 * <p>
 * A text editor component that can be bound to any bindable Property. The text
 * editor supports both multiline and single line modes, default is one-line
 * mode.
 * </p>
 * 
 * <p>
 * Since <code>TextField</code> extends <code>AbstractField</code> it implements
 * the {@link com.vaadin.data.Buffered} interface. A <code>TextField</code> is
 * in write-through mode by default, so
 * {@link com.vaadin.ui.AbstractField#setWriteThrough(boolean)} must be called
 * to enable buffering.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class TextField extends AbstractTextField {

    /**
     * Constructs an empty <code>TextField</code> with no caption.
     */
    public TextField() {
        setValue("");
    }

    /**
     * Constructs an empty <code>TextField</code> with given caption.
     * 
     * @param caption
     *            the caption <code>String</code> for the editor.
     */
    public TextField(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a new <code>TextField</code> that's bound to the specified
     * <code>Property</code> and has no caption.
     * 
     * @param dataSource
     *            the Property to be edited with this editor.
     */
    public TextField(Property dataSource) {
        setPropertyDataSource(dataSource);
    }

    /**
     * Constructs a new <code>TextField</code> that's bound to the specified
     * <code>Property</code> and has the given caption <code>String</code>.
     * 
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param dataSource
     *            the Property to be edited with this editor.
     */
    public TextField(String caption, Property dataSource) {
        this(dataSource);
        setCaption(caption);
    }

    /**
     * Constructs a new <code>TextField</code> with the given caption and
     * initial text contents. The editor constructed this way will not be bound
     * to a Property unless
     * {@link com.vaadin.data.Property.Viewer#setPropertyDataSource(Property)}
     * is called to bind it.
     * 
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param value
     *            the initial text content of the editor.
     */
    public TextField(String caption, String value) {
        setValue(value);
        setCaption(caption);
    }

}
