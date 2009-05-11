/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ITooltip;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class IListSelect extends IOptionGroupBase {

    public static final String CLASSNAME = "i-select";

    private static final int VISIBLE_COUNT = 10;

    protected TooltipListBox select;

    private int lastSelectedIndex = -1;

    public IListSelect() {
        super(new TooltipListBox(true), CLASSNAME);
        select = (TooltipListBox) optionsContainer;
        select.setSelect(this);
        select.addChangeListener(this);
        select.addClickListener(this);
        select.setStyleName(CLASSNAME + "-select");
        select.setVisibleItemCount(VISIBLE_COUNT);
    }

    @Override
    protected void buildOptions(UIDL uidl) {
        select.setClient(client);
        select.setMultipleSelect(isMultiselect());
        select.setEnabled(!isDisabled() && !isReadonly());
        select.clear();
        if (!isMultiselect() && isNullSelectionAllowed()
                && !isNullSelectionItemAvailable()) {
            // can't unselect last item in singleselect mode
            select.addItem("", null);
        }
        for (final Iterator i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL optionUidl = (UIDL) i.next();
            select.addItem(optionUidl.getStringAttribute("caption"), optionUidl
                    .getStringAttribute("key"));
            if (optionUidl.hasAttribute("selected")) {
                select.setItemSelected(select.getItemCount() - 1, true);
            }
        }
        if (getRows() > 0) {
            select.setVisibleItemCount(getRows());
        }
    }

    @Override
    protected Object[] getSelectedItems() {
        final Vector selectedItemKeys = new Vector();
        for (int i = 0; i < select.getItemCount(); i++) {
            if (select.isItemSelected(i)) {
                selectedItemKeys.add(select.getValue(i));
            }
        }
        return selectedItemKeys.toArray();
    }

    @Override
    public void onChange(Widget sender) {
        final int si = select.getSelectedIndex();
        if (si == -1 && !isNullSelectionAllowed()) {
            select.setSelectedIndex(lastSelectedIndex);
        } else {
            lastSelectedIndex = si;
            if (isMultiselect()) {
                client.updateVariable(id, "selected", getSelectedItems(),
                        isImmediate());
            } else {
                client.updateVariable(id, "selected", new String[] { ""
                        + getSelectedItem() }, isImmediate());
            }
        }
    }

    @Override
    public void setHeight(String height) {
        select.setHeight(height);
        super.setHeight(height);
    }

    @Override
    public void setWidth(String width) {
        select.setWidth(width);
        super.setWidth(width);
    }

    @Override
    protected void setTabIndex(int tabIndex) {
        ((TooltipListBox) optionsContainer).setTabIndex(tabIndex);
    }

    public void focus() {
        select.setFocus(true);
    }

}

/**
 * Extended ListBox to listen tooltip events and forward them to generic
 * handler.
 */
class TooltipListBox extends ListBox {
    private ApplicationConnection client;
    private Paintable pntbl;

    TooltipListBox(boolean isMultiselect) {
        super(isMultiselect);
        sinkEvents(ITooltip.TOOLTIP_EVENTS);
    }

    public void setClient(ApplicationConnection client) {
        this.client = client;
    }

    public void setSelect(Paintable s) {
        pntbl = s;
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (client != null) {
            client.handleTooltipEvent(event, pntbl);
        }
    }
}