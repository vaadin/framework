/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.label;

/**
 * Content modes defining how the client should interpret a Label's value.
 * 
 * @sine 7.0
 */
public enum ContentMode {
    /**
     * Content mode, where the label contains only plain text. The getValue()
     * result is coded to XML when painting.
     */
    TEXT,

    /**
     * Content mode, where the label contains preformatted text.
     */
    PREFORMATTED,

    /**
     * Content mode, where the label contains XHTML.
     */
    XHTML,

    /**
     * Content mode, where the label contains well-formed or well-balanced XML.
     * Each of the root elements must have their default namespace specified.
     * 
     * @deprecated Use {@link #XHTML}
     */
    @Deprecated
    XML,

    /**
     * Content mode, where the label contains RAW output. Output is not required
     * to comply to with XML. In Web Adapter output is inserted inside the
     * resulting HTML document as-is. This is useful for some specific purposes
     * where possibly broken HTML content needs to be shown, but in most cases
     * XHTML mode should be preferred.
     * 
     * @deprecated Use {@link #XHTML}, {@link #TEXT} or {@link #PREFORMATTED}.
     */
    @Deprecated
    RAW;
}
