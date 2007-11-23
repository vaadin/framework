/**
 * 
 */
package com.itmill.toolkit.ui;

import java.util.Collection;

import com.itmill.toolkit.data.Container;

/**
 * A filtering dropdown single-select with newItemsAllowed. Items are filtered
 * based on user input, and loaded dynamically ("lazy-loading") from the server.
 * You can turn off newItemsAllowed and change filtering mode (and also turn it
 * off), but you can not turn on multi-select mode.
 * 
 */
public class ComboBox extends Select {
    public ComboBox() {
        setMultiSelect(false);
        setNewItemsAllowed(true);
    }

    public ComboBox(String caption, Collection options) {
        super(caption, options);
        setMultiSelect(false);
        setNewItemsAllowed(true);
    }

    public ComboBox(String caption, Container dataSource) {
        super(caption, dataSource);
        setMultiSelect(false);
        setNewItemsAllowed(true);
    }

    public ComboBox(String caption) {
        super(caption);
        setMultiSelect(false);
        setNewItemsAllowed(true);
    }

    public void setMultiSelect(boolean multiSelect) {
        if (multiSelect && !isMultiSelect()) {
            throw new UnsupportedOperationException("Multiselect not supported");
        }
        super.setMultiSelect(multiSelect);
    }

}
