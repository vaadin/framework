/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.data.Property;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.richtextarea.VRichTextArea;
import com.vaadin.ui.ClientWidget.LoadStyle;

/**
 * A simple RichTextArea to edit HTML format text.
 * 
 * Note, that using {@link TextField#setMaxLength(int)} method in
 * {@link RichTextArea} may produce unexpected results as formatting is counted
 * into length of field.
 */
@SuppressWarnings({ "serial", "unchecked" })
@ClientWidget(value = VRichTextArea.class, loadStyle = LoadStyle.LAZY)
public class RichTextArea extends AbstractTextField {

    private boolean selectAll;

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
        super.paintContent(target);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        // IE6 cannot support multi-classname selectors properly
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
        requestRepaint();
    }

}
