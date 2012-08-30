package com.vaadin.tests.components.select;

import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;
import com.vaadin.ui.NativeSelect;

public class NativeSelects extends ComponentTestCase<NativeSelect> {

    NativeSelect label[] = new NativeSelect[20];

    @Override
    protected Class<NativeSelect> getTestClass() {
        return NativeSelect.class;
    }

    @Override
    protected void initializeComponents() {

        NativeSelect s;

        s = createNativeSelect(null);
        s.setWidth(null);
        addTestComponent(s);

        s = createNativeSelect("Undefined wide, empty select");
        s.setWidth(null);
        addTestComponent(s);

        s = createNativeSelect("Undefined wide select with 5 items");
        s.setWidth(null);
        addItem(s, "The first item");
        addItem(s, "The second item");
        addItem(s, "The third item");
        addItem(s, "The fourth item");
        addItem(s, "The fifth item");
        addTestComponent(s);

        s = createNativeSelect("Undefined wide select with 50 items");
        s.setWidth(null);
        populate(s, 50);
        addTestComponent(s);

        s = createNativeSelect(null);
        s.setWidth("100px");
        addTestComponent(s);

        s = createNativeSelect("100px wide, empty select");
        s.setWidth("100px");
        addTestComponent(s);

        s = createNativeSelect("150px wide select with 5 items");
        s.setWidth("150px");
        addItem(s, "The first item");
        addItem(s, "The second item");
        addItem(s, "The third item");
        addItem(s, "The fourth item");
        addItem(s, "The fifth item");
        addTestComponent(s);

        s = createNativeSelect("200px wide select with 50 items");
        s.setWidth("200px");
        populate(s, 50);
        addTestComponent(s);

    }

    private void populate(NativeSelect s, int nr) {
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

    private void addItem(NativeSelect s, String string) {
        Object id = s.addItem();
        s.getItem(id).getItemProperty(CAPTION).setValue(string);

    }

    private NativeSelect createNativeSelect(String caption) {
        NativeSelect s = new NativeSelect();

        s.addContainerProperty(CAPTION, String.class, "");
        s.setItemCaptionPropertyId(CAPTION);
        s.setCaption(caption);
        s.setNullSelectionAllowed(false);
        return s;
    }

    @Override
    protected String getDescription() {
        return "A generic test for Labels in different configurations";
    }

    @Override
    protected void createCustomActions(List<Component> actions) {
        actions.add(createBooleanAction("Null selection allowed", false,
                new Command<NativeSelect, Boolean>() {

                    @Override
                    public void execute(NativeSelect c, Boolean value,
                            Object data) {
                        c.setNullSelectionAllowed(value);
                    }
                }));
    }

}
