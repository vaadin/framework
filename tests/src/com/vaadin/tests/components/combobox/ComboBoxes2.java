package com.vaadin.tests.components.combobox;

import com.vaadin.terminal.Resource;
import com.vaadin.tests.components.select.AbstractSelectTestCase;
import com.vaadin.ui.ComboBox;

public class ComboBoxes2 extends AbstractSelectTestCase<ComboBox> {

    @Override
    protected Class<ComboBox> getTestClass() {
        return ComboBox.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createItemIconSelect(CATEGORY_DATA_SOURCE);
    }

    private void createItemIconSelect(String category) {

        createSelectAction("Icon", category, createIconOptions(false), "-",
                new Command<ComboBox, Resource>() {

                    public void execute(ComboBox c, Resource value, Object data) {
                        for (Object id : c.getItemIds()) {
                            if (value == null) {
                                c.setItemIcon(id, null);
                            } else {
                                c.setItemIcon(id, value);
                            }
                        }
                    }
                });
    }

}
