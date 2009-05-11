/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasFocus;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.UIDL;

public class IOptionGroup extends IOptionGroupBase {

    public static final String CLASSNAME = "i-select-optiongroup";

    private final Panel panel;

    private final Map optionsToKeys;

    public IOptionGroup() {
        super(CLASSNAME);
        panel = (Panel) optionsContainer;
        optionsToKeys = new HashMap();
    }

    /*
     * Return true if no elements were changed, false otherwise.
     */
    @Override
    protected void buildOptions(UIDL uidl) {
        panel.clear();
        for (final Iterator it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL opUidl = (UIDL) it.next();
            CheckBox op;
            if (isMultiselect()) {
                op = new ICheckBox();
                op.setText(opUidl.getStringAttribute("caption"));
            } else {
                op = new RadioButton(id, opUidl.getStringAttribute("caption"));
                op.setStyleName("i-radiobutton");
            }
            op.addStyleName(CLASSNAME_OPTION);
            op.setChecked(opUidl.getBooleanAttribute("selected"));
            op.setEnabled(!opUidl.getBooleanAttribute("disabled")
                    && !isReadonly() && !isDisabled());
            op.addClickListener(this);
            optionsToKeys.put(op, opUidl.getStringAttribute("key"));
            panel.add(op);
        }
    }

    @Override
    protected Object[] getSelectedItems() {
        return selectedKeys.toArray();
    }

    @Override
    public void onClick(Widget sender) {
        super.onClick(sender);
        if (sender instanceof CheckBox) {
            final boolean selected = ((CheckBox) sender).isChecked();
            final String key = (String) optionsToKeys.get(sender);
            if (!isMultiselect()) {
                selectedKeys.clear();
            }
            if (selected) {
                selectedKeys.add(key);
            } else {
                selectedKeys.remove(key);
            }
            client.updateVariable(id, "selected", getSelectedItems(),
                    isImmediate());
        }
    }

    @Override
    protected void setTabIndex(int tabIndex) {
        for (Iterator iterator = panel.iterator(); iterator.hasNext();) {
            if (isMultiselect()) {
                ICheckBox cb = (ICheckBox) iterator.next();
                cb.setTabIndex(tabIndex);
            } else {
                RadioButton rb = (RadioButton) iterator.next();
                rb.setTabIndex(tabIndex);
            }
        }
    }

    public void focus() {
        Iterator<Widget> iterator = panel.iterator();
        if (iterator.hasNext()) {
            ((HasFocus) iterator.next()).setFocus(true);
        }
    }

}
