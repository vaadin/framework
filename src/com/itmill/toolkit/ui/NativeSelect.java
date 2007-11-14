package com.itmill.toolkit.ui;

import java.util.Collection;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * This is a simple drop-down select without, for instance, support for
 * multiselect, new items, lazyloading, and other advanced features. Sometimes
 * "native" select without all the bells-and-whistles of the ComboBox is a
 * better choice.
 */
public class NativeSelect extends AbstractSelect {

    public NativeSelect() {
        super();
    }

    public NativeSelect(String caption, Collection options) {
        super(caption, options);
    }

    public NativeSelect(String caption, Container dataSource) {
        super(caption, dataSource);
    }

    public NativeSelect(String caption) {
        super(caption);
    }

    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute("type", "native");
        super.paintContent(target);
    }

    public void setMultiSelect(boolean multiSelect)
            throws UnsupportedOperationException {
        if (multiSelect == true) {
            throw new UnsupportedOperationException("Multiselect not supported");
        }
    }

    public void setNewItemsAllowed(boolean allowNewOptions)
            throws UnsupportedOperationException {
        if (allowNewOptions == true) {
            throw new UnsupportedOperationException(
                    "newItemsAllowed not supported");
        }
    }

}
