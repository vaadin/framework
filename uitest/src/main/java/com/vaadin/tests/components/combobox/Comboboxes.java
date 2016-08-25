package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;

public class Comboboxes extends ComponentTestCase<ComboBox> {

    private static class StringBean {
        private String value;

        public StringBean(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    protected Class<ComboBox> getTestClass() {
        return ComboBox.class;
    }

    @Override
    protected void initializeComponents() {
        ComboBox<String> s1 = createSelect(null);
        s1.setWidth(null);
        addTestComponent(s1);

        ComboBox<String> s2 = createSelect("Undefined wide, empty select");
        s2.setWidth(null);
        addTestComponent(s2);

        ComboBox<String> s3 = createSelect(
                "Undefined wide select with 5 items");
        s3.setWidth(null);
        s3.setItems("The first item", "The second item", "The third item",
                "The fourth item", "The fifth item");
        addTestComponent(s3);

        ComboBox<StringBean> s4 = new ComboBox<>(
                "Undefined wide select with 50 items");
        s4.setWidth(null);
        populate(s4, 50);
        s4.setItemCaptionProvider(StringBean::getValue);
        s4.setScrollToSelectedItem(true);
        addTestComponent(s4);

        ComboBox<String> s5 = createSelect(null);
        s5.setWidth("100px");
        addTestComponent(s5);

        ComboBox<String> s6 = createSelect("100px wide, empty select");
        s6.setWidth("100px");
        addTestComponent(s6);

        ComboBox<String> s7 = createSelect("150px wide select with 5 items");
        s7.setWidth("150px");
        s7.setItems("The first item", "The second item", "The third item",
                "The fourth item", "The fifth item");
        addTestComponent(s7);

        ComboBox<StringBean> s8 = new ComboBox<>(
                "200px wide select with 50 items");
        s8.setWidth("200px");
        populate(s8, 50);
        s8.setItemCaptionProvider(StringBean::getValue);
        addTestComponent(s8);

        ComboBox<StringBean> s9 = new PageLength0ComboBox();
        s9.setImmediate(true);
        s9.setCaption("Pagelength 0");
        populate(s9, 15);
        s9.setItemCaptionProvider(StringBean::getValue);
        addTestComponent(s9);
    }

    public class PageLength0ComboBox extends ComboBox<StringBean> {
        public PageLength0ComboBox() {
            super();
            setPageLength(0);
        }
    }

    private void populate(ComboBox<StringBean> s, int nr) {
        List<StringBean> beans = new ArrayList<>();
        String text = " an item ";

        String caption = "";
        for (int i = 0; i < nr; i++) {
            if (i % 2 == 0) {
                caption += text;
            } else {
                caption += i;
            }

            beans.add(new StringBean(caption));
        }
        s.setItems(beans);
    }

    private ComboBox<String> createSelect(String caption) {
        return new ComboBox<>(caption);
    }

    @Override
    protected String getTestDescription() {
        return "A generic test for ComboBoxes in different configurations";
    }

    @Override
    protected void createCustomActions(List<Component> actions) {
        actions.add(createIconSelect());
    }

    @SuppressWarnings("rawtypes")
    private Component createIconSelect() {

        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("<None>", null);
        options.put("16x16", "../runo/icons/16/user.png");
        options.put("32x32", "../runo/icons/32/attention.png");
        options.put("64x64", "../runo/icons/64/email-reply.png");

        return createSelectAction("Icon", options, "<None>",
                new Command<ComboBox, String>() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void execute(ComboBox c, String value, Object data) {
                        if (value == null) {
                            c.setItemIconProvider(item -> null);
                        } else {
                            c.setItemIconProvider(item -> new ThemeResource(
                                    value + "?" + new Date().getTime()));
                        }
                    }
                });
    }
}
