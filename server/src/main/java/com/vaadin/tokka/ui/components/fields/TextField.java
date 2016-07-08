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

package com.vaadin.tokka.ui.components.fields;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.data.Property;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

/**
 * A component for editing textual data that fits on a single line. For a
 * multi-line textarea, see the {@link TextArea} component.
 * 
 * @author Vaadin Ltd.
 */
@SuppressWarnings("serial")
public class TextField extends AbstractTextField {

    /**
     * Constructs an empty <code>TextField</code> with no caption.
     */
    public TextField() {
        clear();
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

    /**
     * Clears the value of this TextField.
     */
    public void clear() {
        setValue("");
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        Attributes attr = design.attributes();
        if (attr.hasKey("value")) {
            String text = DesignAttributeHandler.readAttribute("value", attr,
                    String.class);
            getState(false).text = text;
        }
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        AbstractTextField def = (AbstractTextField) designContext
                .getDefaultInstance(this);
        Attributes attr = design.attributes();
        DesignAttributeHandler.writeAttribute("value", attr, getValue(),
                def.getValue(), String.class);
    }
}
