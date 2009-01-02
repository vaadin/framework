/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.util.Collection;

import com.itmill.toolkit.data.Container;

/**
 * A filtering dropdown single-select. Suitable for newItemsAllowed, but it's
 * turned of by default to avoid mistakes. Items are filtered based on user
 * input, and loaded dynamically ("lazy-loading") from the server. You can turn
 * on newItemsAllowed and change filtering mode (and also turn it off), but you
 * can not turn on multi-select mode.
 * 
 */
public class ComboBox extends Select {

    private String emptyText = null;

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

    /*- TODO enable and test this - client impl exists
    public String getEmptyText() {
        return emptyText;
    }

    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
    }

    public void paintContent(PaintTarget target) throws PaintException {
        if (emptyText != null) {
            target.addAttribute("emptytext", emptyText);
        }
        super.paintContent(target);
    }
    -*/

}
