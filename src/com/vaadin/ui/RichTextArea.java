/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * A simple RichTextArea to edit HTML format text.
 * 
 * Note, that using {@link TextField#setMaxLength(int)} method in
 * {@link RichTextArea} may produce unexpected results as formatting is counted
 * into length of field.
 */
@SuppressWarnings("serial")
public class RichTextArea extends TextField {

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

}
