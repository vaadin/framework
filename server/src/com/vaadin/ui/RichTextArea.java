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

import java.util.Map;

import com.vaadin.data.Property;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;

/**
 * A simple RichTextArea to edit HTML format text.
 * 
 * Note, that using {@link TextField#setMaxLength(int)} method in
 * {@link RichTextArea} may produce unexpected results as formatting is counted
 * into length of field.
 */
public class RichTextArea extends AbstractField<String> implements
        LegacyComponent {

    /**
     * Null representation.
     */
    private String nullRepresentation = "null";

    /**
     * Is setting to null from non-null value allowed by setting with null
     * representation .
     */
    private boolean nullSettingAllowed = false;

    /**
     * Temporary flag that indicates all content will be selected after the next
     * paint. Reset to false after painted.
     */
    private boolean selectAll = false;

    /**
     * Constructs an empty <code>RichTextArea</code> with no caption.
     */
    public RichTextArea() {
        setValue("");
    }

    /**
     * 
     * Constructs an empty <code>RichTextArea</code> with the given caption.
     * 
     * @param caption
     *            the caption for the editor.
     */
    public RichTextArea(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a new <code>RichTextArea</code> that's bound to the specified
     * <code>Property</code> and has no caption.
     * 
     * @param dataSource
     *            the data source for the editor value
     */
    public RichTextArea(Property dataSource) {
        setPropertyDataSource(dataSource);
    }

    /**
     * Constructs a new <code>RichTextArea</code> that's bound to the specified
     * <code>Property</code> and has the given caption.
     * 
     * @param caption
     *            the caption for the editor.
     * @param dataSource
     *            the data source for the editor value
     */
    public RichTextArea(String caption, Property dataSource) {
        this(dataSource);
        setCaption(caption);
    }

    /**
     * Constructs a new <code>RichTextArea</code> with the given caption and
     * initial text contents.
     * 
     * @param caption
     *            the caption for the editor.
     * @param value
     *            the initial text content of the editor.
     */
    public RichTextArea(String caption, String value) {
        setValue(value);
        setCaption(caption);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        if (selectAll) {
            target.addAttribute("selectAll", true);
            selectAll = false;
        }

        // Adds the content as variable
        String value = getValue();
        if (value == null) {
            value = getNullRepresentation();
        }
        if (value == null) {
            throw new IllegalStateException(
                    "Null values are not allowed if the null-representation is null");
        }
        target.addVariable(this, "text", value);

    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        // IE6 cannot support multi-classname selectors properly
        // TODO Can be optimized now that support for I6 is dropped
        if (readOnly) {
            addStyleName("v-richtextarea-readonly");
        } else {
            removeStyleName("v-richtextarea-readonly");
        }
    }

    /**
     * Selects all text in the rich text area. As a side effect, focuses the
     * rich text area.
     * 
     * @since 6.5
     */
    public void selectAll() {
        /*
         * Set selection range functionality is currently being
         * planned/developed for GWT RTA. Only selecting all is currently
         * supported. Consider moving selectAll and other selection related
         * functions to AbstractTextField at that point to share the
         * implementation. Some third party components extending
         * AbstractTextField might however not want to support them.
         */
        selectAll = true;
        focus();
        markAsDirty();
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        // Sets the text
        if (variables.containsKey("text") && !isReadOnly()) {

            // Only do the setting if the string representation of the value
            // has been updated
            String newValue = (String) variables.get("text");

            final String oldValue = getValue();
            if (newValue != null
                    && (oldValue == null || isNullSettingAllowed())
                    && newValue.equals(getNullRepresentation())) {
                newValue = null;
            }
            if (newValue != oldValue
                    && (newValue == null || !newValue.equals(oldValue))) {
                boolean wasModified = isModified();
                setValue(newValue, true);

                // If the modified status changes,
                // repaint is needed after all.
                if (wasModified != isModified()) {
                    markAsDirty();
                }
            }
        }

    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    /**
     * Gets the null-string representation.
     * 
     * <p>
     * The null-valued strings are represented on the user interface by
     * replacing the null value with this string. If the null representation is
     * set null (not 'null' string), painting null value throws exception.
     * </p>
     * 
     * <p>
     * The default value is string 'null'.
     * </p>
     * 
     * @return the String Textual representation for null strings.
     * @see TextField#isNullSettingAllowed()
     */
    public String getNullRepresentation() {
        return nullRepresentation;
    }

    /**
     * Is setting nulls with null-string representation allowed.
     * 
     * <p>
     * If this property is true, writing null-representation string to text
     * field always sets the field value to real null. If this property is
     * false, null setting is not made, but the null values are maintained.
     * Maintenance of null-values is made by only converting the textfield
     * contents to real null, if the text field matches the null-string
     * representation and the current value of the field is null.
     * </p>
     * 
     * <p>
     * By default this setting is false
     * </p>
     * 
     * @return boolean Should the null-string represenation be always converted
     *         to null-values.
     * @see TextField#getNullRepresentation()
     */
    public boolean isNullSettingAllowed() {
        return nullSettingAllowed;
    }

    /**
     * Sets the null-string representation.
     * 
     * <p>
     * The null-valued strings are represented on the user interface by
     * replacing the null value with this string. If the null representation is
     * set null (not 'null' string), painting null value throws exception.
     * </p>
     * 
     * <p>
     * The default value is string 'null'
     * </p>
     * 
     * @param nullRepresentation
     *            Textual representation for null strings.
     * @see TextField#setNullSettingAllowed(boolean)
     */
    public void setNullRepresentation(String nullRepresentation) {
        this.nullRepresentation = nullRepresentation;
    }

    /**
     * Sets the null conversion mode.
     * 
     * <p>
     * If this property is true, writing null-representation string to text
     * field always sets the field value to real null. If this property is
     * false, null setting is not made, but the null values are maintained.
     * Maintenance of null-values is made by only converting the textfield
     * contents to real null, if the text field matches the null-string
     * representation and the current value of the field is null.
     * </p>
     * 
     * <p>
     * By default this setting is false.
     * </p>
     * 
     * @param nullSettingAllowed
     *            Should the null-string represenation be always converted to
     *            null-values.
     * @see TextField#getNullRepresentation()
     */
    public void setNullSettingAllowed(boolean nullSettingAllowed) {
        this.nullSettingAllowed = nullSettingAllowed;
    }

    @Override
    protected boolean isEmpty() {
        return super.isEmpty() || getValue().length() == 0;
    }

}
