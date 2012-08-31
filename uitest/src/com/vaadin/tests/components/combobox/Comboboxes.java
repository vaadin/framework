package com.vaadin.tests.components.combobox;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;

public class Comboboxes extends ComponentTestCase<ComboBox> {

    private static final Object CAPTION = "caption";

    @Override
    protected Class<ComboBox> getTestClass() {
        return ComboBox.class;
    }

    @Override
    protected void initializeComponents() {
        ComboBox s;

        s = createSelect(null);
        s.setWidth(null);
        addTestComponent(s);

        s = createSelect("Undefined wide, empty select");
        s.setWidth(null);
        addTestComponent(s);

        s = createSelect("Undefined wide select with 5 items");
        s.setWidth(null);
        addItem(s, "The first item");
        addItem(s, "The second item");
        addItem(s, "The third item");
        addItem(s, "The fourth item");
        addItem(s, "The fifth item");
        addTestComponent(s);

        s = createSelect("Undefined wide select with 50 items");
        s.setWidth(null);
        populate(s, 50);
        addTestComponent(s);

        s = createSelect(null);
        s.setWidth("100px");
        addTestComponent(s);

        s = createSelect("100px wide, empty select");
        s.setWidth("100px");
        addTestComponent(s);

        s = createSelect("150px wide select with 5 items");
        s.setWidth("150px");
        addItem(s, "The first item");
        addItem(s, "The second item");
        addItem(s, "The third item");
        addItem(s, "The fourth item");
        addItem(s, "The fifth item");
        addTestComponent(s);

        s = createSelect("200px wide select with 50 items");
        s.setWidth("200px");
        populate(s, 50);
        addTestComponent(s);

        s = new PageLength0ComboBox();
        s.setImmediate(true);
        s.addContainerProperty(CAPTION, String.class, "");
        s.setItemCaptionPropertyId(CAPTION);
        s.setCaption("Pagelength 0");
        populate(s, 15);
        addTestComponent(s);
    }

    public class PageLength0ComboBox extends ComboBox {
        public PageLength0ComboBox() {
            super();
            pageLength = 0;
        }
    }

    private void populate(ComboBox s, int nr) {
        String text = " an item ";

        String caption = "";
        for (int i = 0; i < nr; i++) {
            if (i % 2 == 0) {
                caption += text;
            } else {
                caption += i;
            }

            addItem(s, caption);
        }

    }

    private void addItem(ComboBox s, String string) {
        Object id = s.addItem();
        s.getItem(id).getItemProperty(CAPTION).setValue(string);

    }

    private ComboBox createSelect(String caption) {
        ComboBox cb = new ComboBox();
        cb.setImmediate(true);
        cb.addContainerProperty(CAPTION, String.class, "");
        cb.setItemCaptionPropertyId(CAPTION);
        cb.setCaption(caption);

        return cb;
    }

    @Override
    protected String getDescription() {
        return "A generic test for ComboBoxes in different configurations";
    }

    @Override
    protected void createCustomActions(List<Component> actions) {
        actions.add(createIconSelect());
    }

    private Component createIconSelect() {

        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("<None>", null);
        options.put("16x16", "../runo/icons/16/user.png");
        options.put("32x32", "../runo/icons/32/attention.png");
        options.put("64x64", "../runo/icons/64/email-reply.png");

        return createSelectAction("Icon", options, "<None>",
                new Command<ComboBox, String>() {

                    @Override
                    public void execute(ComboBox c, String value, Object data) {
                        for (Object id : c.getItemIds()) {
                            if (value == null) {
                                c.setItemIcon(id, null);
                            } else {
                                c.setItemIcon(id, new ThemeResource(value + "?"
                                        + new Date().getTime()));
                            }
                        }
                    }
                });
    }
}
