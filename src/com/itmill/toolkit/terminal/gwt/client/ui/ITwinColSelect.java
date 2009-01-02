/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ITwinColSelect extends IOptionGroupBase {

    private static final String CLASSNAME = "i-select-twincol";

    private static final int VISIBLE_COUNT = 10;

    private static final int DEFAULT_COLUMN_COUNT = 10;

    private final ListBox options;

    private final ListBox selections;

    private final IButton add;

    private final IButton remove;

    private FlowPanel buttons;

    private Panel panel;

    private boolean widthSet = false;

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
        buttons = new FlowPanel();
        buttons.setStyleName(CLASSNAME + "-buttons");
        add = new IButton();
        add.setText(">>");
        add.addClickListener(this);
        remove = new IButton();
        remove.setText("<<");
        remove.addClickListener(this);
        panel = ((Panel) optionsContainer);
        panel.add(options);
        buttons.add(add);
        final HTML br = new HTML("<span/>");
        br.setStyleName(CLASSNAME + "-deco");
        buttons.add(br);
        buttons.add(remove);
        panel.add(buttons);
        panel.add(selections);
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

        int cols = -1;
        if (getColumns() > 0) {
            cols = getColumns();
        } else if (!widthSet) {
            cols = DEFAULT_COLUMN_COUNT;
        }

        if (cols >= 0) {
            options.setWidth(cols + "em");
            selections.setWidth(cols + "em");
            buttons.setWidth("3.5em");
            optionsContainer.setWidth((2 * cols + 4) + "em");
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

    public void setHeight(String height) {
        super.setHeight(height);
        if ("".equals(height)) {
            options.setHeight("");
            selections.setHeight("");
        } else {
            setFullHeightInternals();
        }
    }

    private void setFullHeightInternals() {
        options.setHeight("100%");
        selections.setHeight("100%");
    }

    public void setWidth(String width) {
        super.setWidth(width);
        if (!"".equals(width) && width != null) {
            setRelativeInternalWidths();
        }
    }

    private void setRelativeInternalWidths() {
        DOM.setStyleAttribute(getElement(), "position", "relative");
        buttons.setWidth("15%");
        options.setWidth("42%");
        selections.setWidth("42%");
        widthSet = true;
    }

    protected void setTabIndex(int tabIndex) {
        options.setTabIndex(tabIndex);
        selections.setTabIndex(tabIndex);
        add.setTabIndex(tabIndex);
        remove.setTabIndex(tabIndex);
    }

    public void focus() {
        options.setFocus(true);
    }
}
