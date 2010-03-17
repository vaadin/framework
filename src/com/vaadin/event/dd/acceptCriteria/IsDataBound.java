/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;

/**
 * A simple accept criterion which ensures that {@link Transferable} contains an
 * {@link Item} identifiers. In other words the criterion check that drag is
 * coming from a {@link Container} like {@link Tree} or {@link Table}. TODO
 * Javadoc
 * <p>
 * Note! class is singleton, use {@link #get()} method to get the instance.
 * 
 * @since 6.3
 * 
 */
public final class IsDataBound extends ContainsDataFlavor {
    private static final long serialVersionUID = 1952366107184656946L;
    private static IsDataBound singleton = new IsDataBound();

    private IsDataBound() {
        super("itemId");
    }

    public static IsDataBound get() {
        return singleton;
    }

    @Override
    public boolean accepts(DragAndDropEvent dragEvent) {
        if (dragEvent.getTransferable() instanceof DataBoundTransferable) {
            return ((DataBoundTransferable) dragEvent.getTransferable())
                    .getItemId() != null;
        }
        return false;
    }
}