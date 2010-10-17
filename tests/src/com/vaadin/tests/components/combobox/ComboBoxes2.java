package com.vaadin.tests.components.combobox;

import java.util.LinkedHashMap;

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

        LinkedHashMap<String, Resource> options = new LinkedHashMap<String, Resource>();
        options.put("-", null);
        options.put("16x16", ICON_16_USER_PNG_UNCACHEABLE);
        options.put("32x32", ICON_32_ATTENTION_PNG_UNCACHEABLE);
        options.put("64x64", ICON_64_EMAIL_REPLY_PNG_UNCACHEABLE);

        createSelectAction("Icon", category, options, "-",
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
