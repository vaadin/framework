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
@SuppressWarnings("serial")
@ClientWidget(value = VRichTextArea.class, loadStyle = LoadStyle.LAZY)
public class RichTextArea extends TextField {

    /**
     * Constructs an empty <code>RichTextArea</code> with no caption.
     */
    public RichTextArea() {
        super();
    }

    /**
     * 
     * Constructs an empty <code>RichTextArea</code> with the given caption.
     * 
     * @param caption
     *            the caption for the editor.
     */
    public RichTextArea(String caption) {
        super(caption);
    }

    /**
     * Constructs a new <code>RichTextArea</code> that's bound to the specified
     * <code>Property</code> and has no caption.
     * 
     * @param dataSource
     *            the data source for the editor value
     */
    public RichTextArea(Property dataSource) {
        super(dataSource);
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
        super(caption, dataSource);
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
        super(caption, value);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute("richtext", true);
        super.paintContent(target);
    }

    /**
     * RichTextArea does not support input prompt.
     */
    @Override
    public void setInputPrompt(String inputPrompt) {
        throw new UnsupportedOperationException(
                "RichTextArea does not support inputPrompt");
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

}
