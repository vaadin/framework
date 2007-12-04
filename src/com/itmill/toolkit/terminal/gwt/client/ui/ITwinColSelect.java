/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ITwinColSelect extends IOptionGroupBase {

    private static final String CLASSNAME = "i-select-twincol";

    private static final int VISIBLE_COUNT = 10;

    private static final String DEFAULT_WIDTH = "10em";

    private final ListBox options;

    private final ListBox selections;

    private final IButton add;

    private final IButton remove;

    public ITwinColSelect() {
        super(CLASSNAME);
        options = new ListBox();
        options.addClickListener(this);
        selections = new ListBox();
        selections.addClickListener(this);
        options.setVisibleItemCount(VISIBLE_COUNT);
        selections.setVisibleItemCount(VISIBLE_COUNT);
        options.setStyleName(CLASSNAME + "-options");
        selections.setStyleName(CLASSNAME + "-selections");
        final Panel buttons = new FlowPanel();
        buttons.setStyleName(CLASSNAME + "-buttons");
        add = new IButton();
        add.setText(">>");
        add.addClickListener(this);
        remove = new IButton();
        remove.setText("<<");
        remove.addClickListener(this);
        final Panel p = ((Panel) optionsContainer);
        p.add(options);
        buttons.add(add);
        final HTML br = new HTML("<span/>");
        br.setStyleName(CLASSNAME + "-deco");
        buttons.add(br);
        buttons.add(remove);
        p.add(buttons);
        p.add(selections);
    }

    protected void buildOptions(UIDL uidl) {
        final boolean enabled = !isDisabled() && !isReadonly();
        options.setMultipleSelect(isMultiselect());
        selections.setMultipleSelect(isMultiselect());
        options.setEnabled(enabled);
        selections.setEnabled(enabled);
        add.setEnabled(enabled);
        remove.setEnabled(enabled);
        options.clear();
        selections.clear();
        for (final Iterator i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL optionUidl = (UIDL) i.next();
            if (optionUidl.hasAttribute("selected")) {
                selections.addItem(optionUidl.getStringAttribute("caption"),
                        optionUidl.getStringAttribute("key"));
            } else {
                options.addItem(optionUidl.getStringAttribute("caption"),
                        optionUidl.getStringAttribute("key"));
            }
        }
        optionsContainer.setWidth(null);
        if (getColumns() > 0) {
            options.setWidth(getColumns() + "em");
            selections.setWidth(getColumns() + "em");
        } else {
            options.setWidth(DEFAULT_WIDTH);
            selections.setWidth(DEFAULT_WIDTH);
        }
        if (getRows() > 0) {
            options.setVisibleItemCount(getRows());
            selections.setVisibleItemCount(getRows());
        }
    }

    protected Object[] getSelectedItems() {
        final Vector selectedItemKeys = new Vector();
        for (int i = 0; i < selections.getItemCount(); i++) {
            selectedItemKeys.add(selections.getValue(i));
        }
        return selectedItemKeys.toArray();
    }

    private boolean[] getItemsToAdd() {
        final boolean[] selectedIndexes = new boolean[options.getItemCount()];
        for (int i = 0; i < options.getItemCount(); i++) {
            if (options.isItemSelected(i)) {
                selectedIndexes[i] = true;
            } else {
                selectedIndexes[i] = false;
            }
        }
        return selectedIndexes;
    }

    private boolean[] getItemsToRemove() {
        final boolean[] selectedIndexes = new boolean[selections.getItemCount()];
        for (int i = 0; i < selections.getItemCount(); i++) {
            if (selections.isItemSelected(i)) {
                selectedIndexes[i] = true;
            } else {
                selectedIndexes[i] = false;
            }
        }
        return selectedIndexes;
    }

    public void onClick(Widget sender) {
        super.onClick(sender);
        if (sender == add) {
            final boolean[] sel = getItemsToAdd();
            for (int i = 0; i < sel.length; i++) {
                if (sel[i]) {
                    final int optionIndex = i
                            - (sel.length - options.getItemCount());
                    selectedKeys.add(options.getValue(optionIndex));

                    // Move selection to another column
                    final String text = options.getItemText(optionIndex);
                    final String value = options.getValue(optionIndex);
                    selections.addItem(text, value);
                    selections.setItemSelected(selections.getItemCount() - 1,
                            true);
                    options.removeItem(optionIndex);
                }
            }
            client.updateVariable(id, "selected", selectedKeys.toArray(),
                    isImmediate());

        } else if (sender == remove) {
            final boolean[] sel = getItemsToRemove();
            for (int i = 0; i < sel.length; i++) {
                if (sel[i]) {
                    final int selectionIndex = i
                            - (sel.length - selections.getItemCount());
                    selectedKeys.remove(selections.getValue(selectionIndex));

                    // Move selection to another column
                    final String text = selections.getItemText(selectionIndex);
                    final String value = selections.getValue(selectionIndex);
                    options.addItem(text, value);
                    options.setItemSelected(options.getItemCount() - 1, true);
                    selections.removeItem(selectionIndex);
                }
            }
            client.updateVariable(id, "selected", selectedKeys.toArray(),
                    isImmediate());
        } else if (sender == options) {
            // unselect all in other list, to avoid mistakes (i.e wrong button)
            final int c = selections.getItemCount();
            for (int i = 0; i < c; i++) {
                selections.setItemSelected(i, false);
            }
        } else if (sender == selections) {
            // unselect all in other list, to avoid mistakes (i.e wrong button)
            final int c = options.getItemCount();
            for (int i = 0; i < c; i++) {
                options.setItemSelected(i, false);
            }
        }
    }

}
