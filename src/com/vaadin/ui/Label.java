/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.lang.reflect.Method;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

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
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
// TODO generics for interface Property
public class Label extends AbstractComponent implements Property,
        Property.Viewer, Property.ValueChangeListener,
        Property.ValueChangeNotifier, Comparable<Object> {

    /**
     * Content modes defining how the client should interpret a Label's value.
     * 
     * @sine 7.0
     */
    public enum ContentMode {
        /**
         * Content mode, where the label contains only plain text. The
         * getValue() result is coded to XML when painting.
         */
        TEXT(null) {
            @Override
            public void paintText(String text, PaintTarget target)
                    throws PaintException {
                target.addText(text);
            }
        },

        /**
         * Content mode, where the label contains preformatted text.
         */
        PREFORMATTED("pre") {
            @Override
            public void paintText(String text, PaintTarget target)
                    throws PaintException {
                target.startTag("pre");
                target.addText(text);
                target.endTag("pre");
            }
        },

        /**
         * Content mode, where the label contains XHTML. Contents is then
         * enclosed in DIV elements having namespace of
         * "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd".
         */
        XHTML("xhtml") {
            @Override
            public void paintText(String text, PaintTarget target)
                    throws PaintException {
                target.startTag("data");
                target.addXMLSection("div", text,
                        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
                target.endTag("data");
            }
        },

        /**
         * Content mode, where the label contains well-formed or well-balanced
         * XML. Each of the root elements must have their default namespace
         * specified.
         */
        XML("xml") {
            @Override
            public void paintText(String text, PaintTarget target)
                    throws PaintException {
                target.addXMLSection("data", text, null);
            }
        },

        /**
         * Content mode, where the label contains RAW output. Output is not
         * required to comply to with XML. In Web Adapter output is inserted
         * inside the resulting HTML document as-is. This is useful for some
         * specific purposes where possibly broken HTML content needs to be
         * shown, but in most cases XHTML mode should be preferred.
         */
        RAW("raw") {
            @Override
            public void paintText(String text, PaintTarget target)
                    throws PaintException {
                target.startTag("data");
                target.addAttribute("escape", false);
                target.addText(text);
                target.endTag("data");
            }
        };

        private final String uidlName;

        /**
         * The default content mode is text
         */
        public static ContentMode DEFAULT = TEXT;

        private ContentMode(String uidlName) {
            this.uidlName = uidlName;
        }

        /**
         * Gets the name representing this content mode in UIDL messages
         * 
         * @return the UIDL name of this content mode
         */
        public String getUidlName() {
            return uidlName;
        }

        /**
         * Adds the text value to a {@link PaintTarget} according to this
         * content mode
         * 
         * @param text
         *            the text to add
         * @param target
         *            the paint target to add the value to
         * @throws PaintException
         *             if the paint operation failed
         */
        public abstract void paintText(String text, PaintTarget target)
                throws PaintException;
    }

    /**
     * @deprecated From 7.0, use {@link ContentMode#TEXT} instead
     */
    @Deprecated
    public static final ContentMode CONTENT_TEXT = ContentMode.TEXT;

    /**
     * @deprecated From 7.0, use {@link ContentMode#PREFORMATTED} instead
     */
    @Deprecated
    public static final ContentMode CONTENT_PREFORMATTED = ContentMode.PREFORMATTED;

    /**
     * @deprecated From 7.0, use {@link ContentMode#XHTML} instead
     */
    @Deprecated
    public static final ContentMode CONTENT_XHTML = ContentMode.XHTML;

    /**
     * @deprecated From 7.0, use {@link ContentMode#XML} instead
     */
    @Deprecated
    public static final ContentMode CONTENT_XML = ContentMode.XML;

    /**
     * @deprecated From 7.0, use {@link ContentMode#RAW} instead
     */
    @Deprecated
    public static final ContentMode CONTENT_RAW = ContentMode.RAW;

    /**
     * @deprecated From 7.0, use {@link ContentMode#DEFAULT} instead
     */
    @Deprecated
    public static final ContentMode CONTENT_DEFAULT = ContentMode.DEFAULT;

    private static final String DATASOURCE_MUST_BE_SET = "Datasource must be set";

    private Property dataSource;

    private ContentMode contentMode = ContentMode.DEFAULT;

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
        this(content, ContentMode.DEFAULT);
    }

    /**
     * Creates a new instance of Label with text-contents read from given
     * datasource.
     * 
     * @param contentSource
     */
    public Label(Property contentSource) {
        this(contentSource, ContentMode.DEFAULT);
    }

    /**
     * Creates a new instance of Label with text-contents.
     * 
     * @param content
     * @param contentMode
     */
    public Label(String content, ContentMode contentMode) {
        this(new ObjectProperty<String>(content, String.class), contentMode);
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
        if (contentMode != ContentMode.DEFAULT) {
            setContentMode(contentMode);
        }
        setWidth(100, UNITS_PERCENTAGE);
    }

    /**
     * Paints the content of this component.
     * 
     * @param target
     *            the Paint Event.
     * @throws PaintException
     *             if the Paint Operation fails.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        String uidlName = contentMode.getUidlName();
        if (uidlName != null) {
            target.addAttribute("mode", uidlName);
        }
        contentMode.paintText(getStringValue(), target);

    }

    /**
     * Gets the value of the label. Value of the label is the XML contents of
     * the label.
     * 
     * @return the Value of the label.
     */
    public Object getValue() {
        if (dataSource == null) {
            throw new IllegalStateException(DATASOURCE_MUST_BE_SET);
        }
        return dataSource.getValue();
    }

    /**
     * Set the value of the label. Value of the label is the XML contents of the
     * label.
     * 
     * @param newValue
     *            the New value of the label.
     */
    public void setValue(Object newValue) {
        if (dataSource == null) {
            throw new IllegalStateException(DATASOURCE_MUST_BE_SET);
        }
        dataSource.setValue(newValue);
    }

    /**
     * @see java.lang.Object#toString()
     * @deprecated use the data source value or {@link #getStringValue()}
     *             instead
     */
    @Deprecated
    @Override
    public String toString() {
        throw new UnsupportedOperationException(
                "Use Property.getValue() instead of Label.toString()");
    }

    /**
     * Returns the value of the <code>Property</code> in human readable textual
     * format.
     * 
     * This method exists to help migration from previous Vaadin versions by
     * providing a simple replacement for {@link #toString()}. However, it is
     * normally better to use the value of the label directly.
     * 
     * @return String representation of the value stored in the Property
     * @since 7.0
     */
    public String getStringValue() {
        if (dataSource == null) {
            throw new IllegalStateException(DATASOURCE_MUST_BE_SET);
        }
        Object value = dataSource.getValue();
        return (null != value) ? value.toString() : null;
    }

    /**
     * Gets the type of the Property.
     * 
     * @see com.vaadin.data.Property#getType()
     */
    public Class getType() {
        if (dataSource == null) {
            throw new IllegalStateException(DATASOURCE_MUST_BE_SET);
        }
        return dataSource.getType();
    }

    /**
     * Gets the viewing data-source property.
     * 
     * @return the data source property.
     * @see com.vaadin.data.Property.Viewer#getPropertyDataSource()
     */
    public Property getPropertyDataSource() {
        return dataSource;
    }

    /**
     * Sets the property as data-source for viewing.
     * 
     * @param newDataSource
     *            the new data source Property
     * @see com.vaadin.data.Property.Viewer#setPropertyDataSource(com.vaadin.data.Property)
     */
    public void setPropertyDataSource(Property newDataSource) {
        // Stops listening the old data source changes
        if (dataSource != null
                && Property.ValueChangeNotifier.class
                        .isAssignableFrom(dataSource.getClass())) {
            ((Property.ValueChangeNotifier) dataSource).removeListener(this);
        }

        // Sets the new data source
        dataSource = newDataSource;

        // Listens the new data source if possible
        if (dataSource != null
                && Property.ValueChangeNotifier.class
                        .isAssignableFrom(dataSource.getClass())) {
            ((Property.ValueChangeNotifier) dataSource).addListener(this);
        }
        requestRepaint();
    }

    /**
     * Gets the content mode of the Label.
     * 
     * @return the Content mode of the label.
     * 
     * @see ContentMode
     */
    public ContentMode getContentMode() {
        return contentMode;
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
        if (contentMode != this.contentMode) {
            this.contentMode = contentMode;
            requestRepaint();
        }
    }

    /* Value change events */

    private static final Method VALUE_CHANGE_METHOD;

    static {
        try {
            VALUE_CHANGE_METHOD = Property.ValueChangeListener.class
                    .getDeclaredMethod("valueChange",
                            new Class[] { Property.ValueChangeEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in Label");
        }
    }

    /**
     * Value change event
     * 
     * @author Vaadin Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public static class ValueChangeEvent extends Component.Event implements
            Property.ValueChangeEvent {

        /**
         * New instance of text change event
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
         * @see com.vaadin.data.Property.ValueChangeEvent#getProperty()
         */
        public Property getProperty() {
            return (Property) getSource();
        }
    }

    /**
     * Adds the value change listener.
     * 
     * @param listener
     *            the Listener to be added.
     * @see com.vaadin.data.Property.ValueChangeNotifier#addListener(com.vaadin.data.Property.ValueChangeListener)
     */
    public void addListener(Property.ValueChangeListener listener) {
        addListener(Label.ValueChangeEvent.class, listener, VALUE_CHANGE_METHOD);
    }

    /**
     * Removes the value change listener.
     * 
     * @param listener
     *            the Listener to be removed.
     * @see com.vaadin.data.Property.ValueChangeNotifier#removeListener(com.vaadin.data.Property.ValueChangeListener)
     */
    public void removeListener(Property.ValueChangeListener listener) {
        removeListener(Label.ValueChangeEvent.class, listener,
                VALUE_CHANGE_METHOD);
    }

    /**
     * Emits the options change event.
     */
    protected void fireValueChange() {
        // Set the error message
        fireEvent(new Label.ValueChangeEvent(this));
        requestRepaint();
    }

    /**
     * Listens the value change events from data source.
     * 
     * @see com.vaadin.data.Property.ValueChangeListener#valueChange(Property.ValueChangeEvent)
     */
    public void valueChange(Property.ValueChangeEvent event) {
        fireValueChange();
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
     * is. In XML, UIDL and XHTML modes, only CDATA is compared and tags
     * ignored. If the other object is not a Label, its toString() return value
     * is used in comparison.
     * </p>
     * 
     * @param other
     *            the Other object to compare to.
     * @return a negative integer, zero, or a positive integer as this object is
     *         less than, equal to, or greater than the specified object.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object other) {

        String thisValue;
        String otherValue;

        if (contentMode == ContentMode.XML || contentMode == ContentMode.XHTML) {
            thisValue = stripTags(getStringValue());
        } else {
            thisValue = getStringValue();
        }

        if (other instanceof Label
                && (((Label) other).getContentMode() == ContentMode.XML || ((Label) other)
                        .getContentMode() == ContentMode.XHTML)) {
            otherValue = stripTags(((Label) other).getStringValue());
        } else {
            // TODO not a good idea - and might assume that Field.toString()
            // returns a string representation of the value
            otherValue = other.toString();
        }

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

        final StringBuffer res = new StringBuffer();

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

}
