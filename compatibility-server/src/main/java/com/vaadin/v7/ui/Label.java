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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;

import org.jsoup.nodes.Element;

import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignFormatter;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.ConverterUtil;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.shared.ui.label.LabelState;

/**
 * Label component for showing non-editable short texts.
 *
 * The label content can be set to the modes specified by {@link ContentMode}
 *
 * <p>
 * The contents of the label may contain simple formatting:
 * <ul>
 * <li><b>&lt;b></b> Bold
 * <li><b>&lt;i></b> Italic
 * <li><b>&lt;u></b> Underlined
 * <li><b>&lt;br/></b> Linebreak
 * <li><b>&lt;ul>&lt;li>item 1&lt;/li>&lt;li>item 2&lt;/li>&lt;/ul></b> List of
 * items
 * </ul>
 * The <b>b</b>,<b>i</b>,<b>u</b> and <b>li</b> tags can contain all the tags in
 * the list recursively.
 * </p>
 *
 * @author Vaadin Ltd.
 * @since 3.0
 *
 * @deprecated As of 8.0, replaced by {@link com.vaadin.ui.Label} that removes
 *             data binding support
 */
@SuppressWarnings("serial")
@Deprecated
public class Label extends AbstractLegacyComponent implements Property<String>,
        Property.Viewer, Property.ValueChangeListener,
        Property.ValueChangeNotifier, Comparable<Label> {

    /**
     * @deprecated As of 7.0, use {@link ContentMode#TEXT} instead
     */
    @Deprecated
    public static final ContentMode CONTENT_TEXT = ContentMode.TEXT;

    /**
     * @deprecated As of 7.0, use {@link ContentMode#PREFORMATTED} instead
     */
    @Deprecated
    public static final ContentMode CONTENT_PREFORMATTED = ContentMode.PREFORMATTED;

    /**
     * @deprecated As of 7.0, use {@link ContentMode#HTML} instead
     */
    @Deprecated
    public static final ContentMode CONTENT_XHTML = ContentMode.HTML;

    /**
     * @deprecated As of 7.0, use {@link ContentMode#XML} instead
     */
    @Deprecated
    public static final ContentMode CONTENT_XML = ContentMode.XML;

    /**
     * @deprecated As of 7.0, use {@link ContentMode#RAW} instead
     */
    @Deprecated
    public static final ContentMode CONTENT_RAW = ContentMode.RAW;

    /**
     * @deprecated As of 7.0, use {@link ContentMode#TEXT} instead
     */
    @Deprecated
    public static final ContentMode CONTENT_DEFAULT = ContentMode.TEXT;

    /**
     * A converter used to convert from the data model type to the field type
     * and vice versa. Label type is always String.
     */
    private Converter<String, Object> converter = null;

    private Property<String> dataSource = null;

    /**
     * Creates an empty Label.
     */
    public Label() {
        this("");
    }

    /**
     * Creates a new instance of Label with text-contents.
     *
     * @param content
     */
    public Label(String content) {
        this(content, ContentMode.TEXT);
    }

    /**
     * Creates a new instance of Label with text-contents read from given
     * datasource.
     *
     * @param contentSource
     */
    public Label(Property contentSource) {
        this(contentSource, ContentMode.TEXT);
    }

    /**
     * Creates a new instance of Label with text-contents.
     *
     * @param content
     * @param contentMode
     */
    public Label(String content, ContentMode contentMode) {
        setValue(content);
        setContentMode(contentMode);
        setWidth(100, Unit.PERCENTAGE);
    }

    /**
     * Creates a new instance of Label with text-contents read from given
     * datasource.
     *
     * @param contentSource
     * @param contentMode
     */
    public Label(Property contentSource, ContentMode contentMode) {
        setPropertyDataSource(contentSource);
        setContentMode(contentMode);
        setWidth(100, Unit.PERCENTAGE);
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
     * Gets the value of the label.
     * <p>
     * The value of the label is the text that is shown to the end user.
     * Depending on the {@link ContentMode} it is plain text or markup.
     * </p>
     *
     * @return the value of the label.
     */
    @Override
    public String getValue() {
        if (getPropertyDataSource() == null) {
            // Use internal value if we are running without a data source
            return getState(false).text;
        }
        return getDataSourceValue();
    }

    /**
     * Returns the current value of the data source converted using the current
     * locale.
     *
     * @return
     */
    private String getDataSourceValue() {
        return ConverterUtil.convertFromModel(
                getPropertyDataSource().getValue(), String.class,
                getConverter(), getLocale());
    }

    /**
     * Set the value of the label. Value of the label is the XML contents of the
     * label. Since Vaadin 7.2, changing the value of Label instance with that
     * method will fire ValueChangeEvent.
     *
     * @param newStringValue
     *            the New value of the label.
     */
    @Override
    public void setValue(String newStringValue) {
        if (getPropertyDataSource() == null) {

            LabelState state = getState(false);
            String oldTextValue = state.text;
            if (!SharedUtil.equals(oldTextValue, newStringValue)) {
                getState().text = newStringValue;
                fireValueChange();
            }
        } else {
            throw new IllegalStateException(
                    "Label is only a Property.Viewer and cannot update its data source");
        }
    }

    /**
     * Gets the type of the Property.
     *
     * @see Property#getType()
     */
    @Override
    public Class<String> getType() {
        return String.class;
    }

    /**
     * Gets the viewing data-source property.
     *
     * @return the data source property.
     * @see Property.Viewer#getPropertyDataSource()
     */
    @Override
    public Property getPropertyDataSource() {
        return dataSource;
    }

    /**
     * Sets the property as data-source for viewing. Since Vaadin 7.2 a
     * ValueChangeEvent is fired if the new value is different from previous.
     *
     * @param newDataSource
     *            the new data source Property
     * @see Property.Viewer#setPropertyDataSource(Property)
     */
    @Override
    public void setPropertyDataSource(Property newDataSource) {
        // Stops listening the old data source changes
        if (dataSource != null && Property.ValueChangeNotifier.class
                .isAssignableFrom(dataSource.getClass())) {
            ((Property.ValueChangeNotifier) dataSource).removeListener(this);
        }

        // Check if the current converter is compatible.
        if (newDataSource != null
                && !ConverterUtil.canConverterPossiblyHandle(getConverter(),
                        getType(), newDataSource.getType())) {
            // There is no converter set or there is no way the current
            // converter can be compatible.
            Converter<String, ?> c = ConverterUtil.getConverter(String.class,
                    newDataSource.getType(), getSession());
            setConverter(c);
        }

        dataSource = newDataSource;
        if (dataSource != null) {
            // Update the value from the data source. If data source was set to
            // null, retain the old value
            updateValueFromDataSource();
        }

        // Listens the new data source if possible
        if (dataSource != null && Property.ValueChangeNotifier.class
                .isAssignableFrom(dataSource.getClass())) {
            ((Property.ValueChangeNotifier) dataSource).addListener(this);
        }
        markAsDirty();
    }

    /**
     * Gets the content mode of the Label.
     *
     * @return the Content mode of the label.
     *
     * @see ContentMode
     */
    public ContentMode getContentMode() {
        return getState(false).contentMode;
    }

    /**
     * Sets the content mode of the Label.
     *
     * @param contentMode
     *            the New content mode of the label.
     *
     * @see ContentMode
     */
    public void setContentMode(ContentMode contentMode) {
        if (contentMode == null) {
            throw new IllegalArgumentException("Content mode can not be null");
        }

        getState().contentMode = contentMode;
    }

    /* Value change events */

    private static final Method VALUE_CHANGE_METHOD;

    static {
        try {
            VALUE_CHANGE_METHOD = Property.ValueChangeListener.class
                    .getDeclaredMethod("valueChange",
                            new Class[] { Property.ValueChangeEvent.class });
        } catch (final NoSuchMethodException e) {
            // This should never happen
            throw new RuntimeException(
                    "Internal error finding methods in Label");
        }
    }

    /**
     * Value change event.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    @Deprecated
    public static class ValueChangeEvent extends Component.Event
            implements Property.ValueChangeEvent {

        /**
         * New instance of text change event.
         *
         * @param source
         *            the Source of the event.
         */
        public ValueChangeEvent(Label source) {
            super(source);
        }

        /**
         * Gets the Property that has been modified.
         *
         * @see Property.ValueChangeEvent#getProperty()
         */
        @Override
        public Property getProperty() {
            return (Property) getSource();
        }
    }

    /**
     * Adds the value change listener.
     *
     * @param listener
     *            the Listener to be added.
     * @see Property.ValueChangeNotifier#addListener(Property.ValueChangeListener)
     */
    @Override
    public void addValueChangeListener(Property.ValueChangeListener listener) {
        addListener(Label.ValueChangeEvent.class, listener,
                VALUE_CHANGE_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addValueChangeListener(Property.ValueChangeListener)}
     */
    @Override
    @Deprecated
    public void addListener(Property.ValueChangeListener listener) {
        addValueChangeListener(listener);
    }

    /**
     * Removes the value change listener.
     *
     * @param listener
     *            the Listener to be removed.
     * @see Property.ValueChangeNotifier#removeListener(Property.ValueChangeListener)
     */
    @Override
    public void removeValueChangeListener(
            Property.ValueChangeListener listener) {
        removeListener(Label.ValueChangeEvent.class, listener,
                VALUE_CHANGE_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeValueChangeListener(Property.ValueChangeListener)}
     */
    @Override
    @Deprecated
    public void removeListener(Property.ValueChangeListener listener) {
        removeValueChangeListener(listener);
    }

    /**
     * Emits the options change event.
     */
    protected void fireValueChange() {
        // Set the error message
        fireEvent(new Label.ValueChangeEvent(this));
    }

    /**
     * Listens the value change events from data source.
     *
     * @see Property.ValueChangeListener#valueChange(Property.ValueChangeEvent)
     */
    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        updateValueFromDataSource();
    }

    private void updateValueFromDataSource() {
        // Update the internal value from the data source
        String newConvertedValue = getDataSourceValue();
        if (!SharedUtil.equals(newConvertedValue, getState(false).text)) {
            getState().text = newConvertedValue;
            fireValueChange();
        }
    }

    @Override
    public void attach() {
        super.attach();
        localeMightHaveChanged();
    }

    @Override
    public void setLocale(Locale locale) {
        super.setLocale(locale);
        localeMightHaveChanged();
    }

    private void localeMightHaveChanged() {
        if (getPropertyDataSource() != null) {
            updateValueFromDataSource();
        }
    }

    private String getComparableValue() {
        String stringValue = getValue();
        if (stringValue == null) {
            stringValue = "";
        }

        if (getContentMode() == ContentMode.HTML
                || getContentMode() == ContentMode.XML) {
            return stripTags(stringValue);
        } else {
            return stringValue;
        }

    }

    /**
     * Compares the Label to other objects.
     *
     * <p>
     * Labels can be compared to other labels for sorting label contents. This
     * is especially handy for sorting table columns.
     * </p>
     *
     * <p>
     * In RAW, PREFORMATTED and TEXT modes, the label contents are compared as
     * is. In XML, UIDL and HTML modes, only CDATA is compared and tags ignored.
     * If the other object is not a Label, its toString() return value is used
     * in comparison.
     * </p>
     *
     * @param other
     *            the Other object to compare to.
     * @return a negative integer, zero, or a positive integer as this object is
     *         less than, equal to, or greater than the specified object.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Label other) {

        String thisValue = getComparableValue();
        String otherValue = other.getComparableValue();

        return thisValue.compareTo(otherValue);
    }

    /**
     * Strips the tags from the XML.
     *
     * @param xml
     *            the String containing a XML snippet.
     * @return the original XML without tags.
     */
    private String stripTags(String xml) {

        final StringBuilder res = new StringBuilder();

        int processed = 0;
        final int xmlLen = xml.length();
        while (processed < xmlLen) {
            int next = xml.indexOf('<', processed);
            if (next < 0) {
                next = xmlLen;
            }
            res.append(xml.substring(processed, next));
            if (processed < xmlLen) {
                next = xml.indexOf('>', processed);
                if (next < 0) {
                    next = xmlLen;
                }
                processed = next + 1;
            }
        }

        return res.toString();
    }

    /**
     * Gets the converter used to convert the property data source value to the
     * label value.
     *
     * @return The converter or null if none is set.
     */
    public Converter<String, Object> getConverter() {
        return converter;
    }

    /**
     * Sets the converter used to convert the label value to the property data
     * source type. The converter must have a presentation type of String.
     *
     * @param converter
     *            The new converter to use.
     */
    public void setConverter(Converter<String, ?> converter) {
        this.converter = (Converter<String, Object>) converter;
        markAsDirty();
    }

    // LegacyPropertyHelper has been removed in Vaadin 8

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractComponent#readDesign(org.jsoup.nodes .Element,
     * com.vaadin.ui.declarative.DesignContext)
     */
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

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractComponent#getCustomAttributes()
     */
    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> result = super.getCustomAttributes();
        result.add("value");
        result.add("content-mode");
        result.add("plain-text");
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractComponent#writeDesign(org.jsoup.nodes.Element
     * , com.vaadin.ui.declarative.DesignContext)
     */
    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        String content = getValue();
        if (content != null) {
            switch (getContentMode()) {
            case TEXT:
            case PREFORMATTED:
            case XML:
            case RAW: {
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
            default:
                throw new IllegalStateException(
                        "ContentMode " + getContentMode()
                                + " is not supported by writeDesign");
            }
        }
    }
}
