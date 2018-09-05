/*
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.v7.ui;

import org.jsoup.nodes.Element;

import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignFormatter;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.shared.ui.textarea.TextAreaState;

/**
 * A text field that supports multi line editing.
 *
 * @deprecated As of 8.0 replaced by {@link com.vaadin.ui.TextArea} based on the
 *             new data binding API
 */
@Deprecated
public class TextArea extends AbstractTextField {

    /**
     * Constructs an empty TextArea.
     */
    public TextArea() {
        setValue("");
    }

    /**
     * Constructs an empty TextArea with given caption.
     *
     * @param caption
     *            the caption for the field.
     */
    public TextArea(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a TextArea with given property data source.
     *
     * @param dataSource
     *            the data source for the field
     */
    public TextArea(Property dataSource) {
        this();
        setPropertyDataSource(dataSource);
    }

    /**
     * Constructs a TextArea with given caption and property data source.
     *
     * @param caption
     *            the caption for the field
     * @param dataSource
     *            the data source for the field
     */
    public TextArea(String caption, Property dataSource) {
        this(dataSource);
        setCaption(caption);
    }

    /**
     * Constructs a TextArea with given caption and value.
     *
     * @param caption
     *            the caption for the field
     * @param value
     *            the value for the field
     */
    public TextArea(String caption, String value) {
        this(caption);
        setValue(value);

    }

    @Override
    protected TextAreaState getState() {
        return (TextAreaState) super.getState();
    }

    @Override
    protected TextAreaState getState(boolean markAsDirty) {
        return (TextAreaState) super.getState(markAsDirty);
    }

    /**
     * Sets the number of rows in the text area.
     *
     * @param rows
     *            the number of rows for this text area.
     */
    public void setRows(int rows) {
        if (rows < 0) {
            rows = 0;
        }
        getState().rows = rows;
    }

    /**
     * Gets the number of rows in the text area.
     *
     * @return number of explicitly set rows.
     */
    public int getRows() {
        return getState(false).rows;
    }

    /**
     * Sets the text area's word-wrap mode on or off.
     *
     * @param wordwrap
     *            the boolean value specifying if the text area should be in
     *            word-wrap mode.
     */
    public void setWordwrap(boolean wordwrap) {
        getState().wordwrap = wordwrap;
    }

    /**
     * Tests if the text area is in word-wrap mode.
     *
     * @return <code>true</code> if the component is in word-wrap mode,
     *         <code>false</code> if not.
     */
    public boolean isWordwrap() {
        return getState(false).wordwrap;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractField#readDesign(org.jsoup.nodes.Element ,
     * com.vaadin.ui.declarative.DesignContext)
     */
    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        setValue(DesignFormatter.decodeFromTextNode(design.html()), false,
                true);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractTextField#writeDesign(org.jsoup.nodes.Element
     * , com.vaadin.ui.declarative.DesignContext)
     */
    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        design.html(DesignFormatter.encodeForTextNode(getValue()));
    }

    @Override
    public void clear() {
        setValue("");
    }

}
