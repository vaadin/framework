/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * A filtering dropdown single-select. Suitable for newItemsAllowed, but it's
 * turned of by default to avoid mistakes. Items are filtered based on user
 * input, and loaded dynamically ("lazy-loading") from the server. You can turn
 * on newItemsAllowed and change filtering mode (and also turn it off), but you
 * can not turn on multi-select mode.
 * 
 */
@SuppressWarnings("serial")
public class ComboBox extends Select {

    private String inputPrompt = null;

    public ComboBox() {
        setMultiSelect(false);
        setNewItemsAllowed(false);
    }

    public ComboBox(String caption, Collection options) {
        super(caption, options);
        setMultiSelect(false);
        setNewItemsAllowed(false);
    }

    public ComboBox(String caption, Container dataSource) {
        super(caption, dataSource);
        setMultiSelect(false);
        setNewItemsAllowed(false);
    }

    public ComboBox(String caption) {
        super(caption);
        setMultiSelect(false);
        setNewItemsAllowed(false);
    }

    @Override
    public void setMultiSelect(boolean multiSelect) {
        if (multiSelect && !isMultiSelect()) {
            throw new UnsupportedOperationException("Multiselect not supported");
        }
        super.setMultiSelect(multiSelect);
    }

    /**
     * Gets the current input prompt.
     * 
     * @see #setInputPrompt(String)
     * @return the current input prompt, or null if not enabled
     */
    public String getInputPrompt() {
        return inputPrompt;
    }

    /**
     * Sets the input prompt - a textual prompt that is displayed when the
     * select would otherwise be empty, to prompt the user for input.
     * 
     * @param inputPrompt
     *            the desired input prompt, or null to disable
     */
    public void setInputPrompt(String inputPrompt) {
        this.inputPrompt = inputPrompt;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        if (inputPrompt != null) {
            target.addAttribute("prompt", inputPrompt);
        }
        super.paintContent(target);
    }

}
