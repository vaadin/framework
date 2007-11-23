package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Set;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

abstract class IOptionGroupBase extends Composite implements Paintable,
        ClickListener, ChangeListener, KeyboardListener {

    public static final String CLASSNAME_OPTION = "i-select-option";

    ApplicationConnection client;

    String id;

    protected Set selectedKeys;

    private boolean immediate;

    private boolean multiselect;

    private boolean disabled;

    private boolean readonly;

    private int cols = 0;

    private int rows = 0;

    private boolean nullSelectionAllowed = true;

    private boolean nullSelectionItemAvailable = false;

    /**
     * Widget holding the different options (e.g. ListBox or Panel for radio
     * buttons) (optional, fallbacks to container Panel)
     */
    protected Widget optionsContainer;

    /**
     * Panel containing the component
     */
    private Panel container;

    private ITextField newItemField;

    private IButton newItemButton;

    public IOptionGroupBase(String classname) {
        container = new FlowPanel();
        initWidget(container);
        optionsContainer = container;
        container.setStyleName(classname);
        immediate = false;
        multiselect = false;
    }

    /*
     * Call this if you wish to specify your own container for the option
     * elements (e.g. SELECT)
     */
    public IOptionGroupBase(Widget w, String classname) {
        this(classname);
        optionsContainer = w;
        container.add(optionsContainer);
    }

    protected boolean isImmediate() {
        return immediate;
    }

    protected boolean isMultiselect() {
        return multiselect;
    }

    protected boolean isDisabled() {
        return disabled;
    }

    protected boolean isReadonly() {
        return readonly;
    }

    protected boolean isNullSelectionAllowed() {
        return nullSelectionAllowed;
    }

    protected boolean isNullSelectionItemAvailable() {
        return nullSelectionItemAvailable;
    }

    /**
     * @return "cols" specified in uidl, 0 if not specified
     */
    protected int getColumns() {
        return cols;
    }

    /**
     * @return "rows" specified in uidl, 0 if not specified
     */

    protected int getRows() {
        return rows;
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        id = uidl.getId();

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        selectedKeys = uidl.getStringArrayVariableAsSet("selected");

        readonly = uidl.getBooleanAttribute("readonly");
        disabled = uidl.getBooleanAttribute("disabled");
        multiselect = "multi".equals(uidl.getStringAttribute("selectmode"));
        immediate = uidl.getBooleanAttribute("immediate");
        nullSelectionAllowed = uidl.getBooleanAttribute("nullselect");
        nullSelectionItemAvailable = uidl.getBooleanAttribute("nullselectitem");

        cols = uidl.getIntAttribute("cols");
        rows = uidl.getIntAttribute("rows");

        UIDL ops = uidl.getChildUIDL(0);

        if (getColumns() > 0) {
            container.setWidth(getColumns() + "em");
            if (container != optionsContainer) {
                optionsContainer.setWidth("100%");
            }
        }

        buildOptions(ops);

        if (uidl.getBooleanAttribute("allownewitem")) {
            if (newItemField == null) {
                newItemButton = new IButton();
                newItemButton.setText("+");
                newItemButton.setWidth("1.5em");
                newItemButton.addClickListener(this);
                newItemField = new ITextField();
                newItemField.addKeyboardListener(this);
                // newItemField.setColumns(16);
                if (getColumns() > 0) {
                    newItemField.setWidth((getColumns() - 2) + "em");
                }
            }
            newItemField.setEnabled(!disabled && !readonly);
            newItemButton.setEnabled(!disabled && !readonly);

            if (newItemField == null || newItemField.getParent() != container) {
                container.add(newItemField);
                container.add(newItemButton);
            }
        } else if (newItemField != null) {
            container.remove(newItemField);
            container.remove(newItemButton);
        }

    }

    public void onClick(Widget sender) {
        if (sender == newItemButton && !newItemField.getText().equals("")) {
            client.updateVariable(id, "newitem", newItemField.getText(), true);
            newItemField.setText("");
        }
    }

    public void onChange(Widget sender) {
        if (multiselect) {
            client
                    .updateVariable(id, "selected", getSelectedItems(),
                            immediate);
        } else {
            client.updateVariable(id, "selected", new String[] { ""
                    + getSelectedItem() }, immediate);
        }
    }

    public void onKeyPress(Widget sender, char keyCode, int modifiers) {
        if (sender == newItemField && keyCode == KeyboardListener.KEY_ENTER) {
            newItemButton.click();
        }
    }

    public void onKeyUp(Widget sender, char keyCode, int modifiers) {
        // Ignore, subclasses may override
    }

    public void onKeyDown(Widget sender, char keyCode, int modifiers) {
        // Ignore, subclasses may override
    }

    protected abstract void buildOptions(UIDL uidl);

    protected abstract Object[] getSelectedItems();

    protected Object getSelectedItem() {
        Object[] sel = getSelectedItems();
        if (sel.length > 0) {
            return sel[0];
        } else {
            return null;
        }
    }

}
