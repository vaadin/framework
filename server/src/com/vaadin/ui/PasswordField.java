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

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.data.Property;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

/**
 * A field that is used to enter secret text information like passwords. The
 * entered text is not displayed on the screen.
 */
public class PasswordField extends AbstractTextField {

    /**
     * Constructs an empty PasswordField.
     */
    public PasswordField() {
        setValue("");
    }

    /**
     * Constructs a PasswordField with given property data source.
     * 
     * @param dataSource
     *            the property data source for the field
     */
    public PasswordField(Property dataSource) {
        setPropertyDataSource(dataSource);
    }

    /**
     * Constructs a PasswordField with given caption and property data source.
     * 
     * @param caption
     *            the caption for the field
     * @param dataSource
     *            the property data source for the field
     */
    public PasswordField(String caption, Property dataSource) {
        this(dataSource);
        setCaption(caption);
    }

    /**
     * Constructs a PasswordField with given value and caption.
     * 
     * @param caption
     *            the caption for the field
     * @param value
     *            the value for the field
     */
    public PasswordField(String caption, String value) {
        setValue(value);
        setCaption(caption);
    }

    /**
     * Constructs a PasswordField with given caption.
     * 
     * @param caption
     *            the caption for the field
     */
    public PasswordField(String caption) {
        this();
        setCaption(caption);
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
        Attributes attr = design.attributes();
        if (attr.hasKey("value")) {
            setValue(DesignAttributeHandler.readAttribute("value", attr,
                    String.class));
        }
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
        AbstractTextField def = (AbstractTextField) designContext
                .getDefaultInstance(this);
        Attributes attr = design.attributes();
        DesignAttributeHandler.writeAttribute("value", attr, getValue(),
                def.getValue(), String.class);
    }

    @Override
    public void clear() {
        setValue("");
    }

}
