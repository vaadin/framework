/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.shared.ui.label;

/**
 * Content modes defining how the client should interpret a Label's value.
 * 
 * @since 7.0.0
 */
public enum ContentMode {
    /**
     * Content mode, where the label contains only plain text.
     */
    TEXT,

    /**
     * Content mode, where the label contains pre formatted text. In this mode
     * newlines are preserved when rendered on the screen.
     */
    PREFORMATTED,

    /**
     * Content mode, where the label contains XHTML. Care should be taken to
     * ensure
     */
    XHTML,

    /**
     * Content mode, where the label contains well-formed or well-balanced XML.
     * This is handled in the same way as {@link #XHTML}.
     * 
     * @deprecated Use {@link #XHTML} instead
     */
    @Deprecated
    XML,

    /**
     * Legacy content mode, where the label contains RAW output. This is handled
     * in exactly the same way as {@link #XHTML}.
     * 
     * @deprecated Use {@link #XHTML} instead
     */
    @Deprecated
    RAW;
}
