/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import java.util.Collection;

import org.jsoup.nodes.Element;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.label.LabelState;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignFormatter;

/**
 * Label component for showing non-editable short texts.
 * <p>
 * The label content can be set to the modes specified by {@link ContentMode}.
 * If content mode is set to HTML, any HTML content is allowed.
 *
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class Label extends AbstractComponent {

    /**
     * Creates an empty Label.
     */
    public Label() {
        this("");
    }

    /**
     * Creates a new instance with text content mode and the given text.
     *
     * @param text
     *            the text to set
     */
    public Label(String text) {
        this(text, ContentMode.TEXT);
    }

    /**
     * Creates a new instance with the given text and content mode.
     *
     * @param text
     *            the text to set
     * @param contentMode
     *            the content mode to use
     * @since 8.0
     */
    public Label(String text, ContentMode contentMode) {
        setValue(text);
        setContentMode(contentMode);
    }

    @Override
    protected LabelState getState() {
        return (LabelState) super.getState();
    }

    @Override
    protected LabelState getState(boolean markAsDirty) {
        return (LabelState) super.getState(markAsDirty);
    }

    /**
     * Gets the content mode of the label.
     *
     * @return the content mode of the label
     *
     * @see ContentMode
     * @since 8.0
     */
    public ContentMode getContentMode() {
        return getState(false).contentMode;
    }

    /**
     * Sets the content mode of the label.
     *
     * @param contentMode
     *            the content mode to set
     *
     * @see ContentMode
     * @since 8.0
     */
    public void setContentMode(ContentMode contentMode) {
        if (contentMode == null) {
            throw new IllegalArgumentException("Content mode can not be null");
        }

        getState().contentMode = contentMode;
    }

    /**
     * Sets the text to be shown in the label.
     *
     * @param value
     *            the text to show in the label, null is converted to an empty
     *            string
     */
    public void setValue(String value) {
        if (value == null) {
            getState().text = "";
        } else {
            getState().text = value;
        }
    }

    /**
     * Gets the text shown in the label.
     *
     * @return the text shown in the label, not null
     */
    public String getValue() {
        return getState(false).text;
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        String innerHtml = design.html();
        boolean plainText = design.hasAttr(DESIGN_ATTR_PLAIN_TEXT);
        if (plainText) {
            setContentMode(ContentMode.TEXT);
        } else {
            setContentMode(ContentMode.HTML);
        }
        if (innerHtml != null && !"".equals(innerHtml)) {
            if (plainText) {
                innerHtml = DesignFormatter.decodeFromTextNode(innerHtml);
            }
            setValue(innerHtml);
        }
    }

    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> result = super.getCustomAttributes();
        result.add("value");
        result.add("content-mode");
        result.add("plain-text");
        return result;
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        String content = getValue();
        if (content != null) {
            switch (getContentMode()) {
            case TEXT:
            case PREFORMATTED: {
                // FIXME This attribute is not enough to be able to restore the
                // content mode in readDesign. The content mode should instead
                // be written directly in the attribute and restored in
                // readDesign. See ticket #19435
                design.attr(DESIGN_ATTR_PLAIN_TEXT, true);
                String encodeForTextNode = DesignFormatter
                        .encodeForTextNode(content);
                if (encodeForTextNode != null) {
                    design.html(encodeForTextNode);
                }
            }
                break;
            case HTML:
                design.html(content);
                break;
            }
        }
    }

}
