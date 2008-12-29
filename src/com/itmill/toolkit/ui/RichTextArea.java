/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * A simple RichTextEditor to edit HTML format text.
 * 
 * Note, that using {@link TextField#setMaxLength(int)} method in
 * {@link RichTextArea} may produce unexpected results as formatting is counted
 * into length of field.
 */
public class RichTextArea extends TextField {

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute("richtext", true);
        super.paintContent(target);
    }

}
