package com.vaadin.tests.layouts;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class CaptionsInLayouts extends AbstractTestUI {

    private static final Object CAPTION = "CAPTION";
    private static final Object CLASS = "C";
    private static final Object WIDTH = "W";

    private NativeSelect layoutSelect;
    private Layout layout;
    private VerticalLayout verticalLayout;
    private HorizontalLayout horizontalLayout;
    private GridLayout gridLayout;
    private FormLayout formLayout;
    private List<AbstractField<?>> components = new ArrayList<AbstractField<?>>();
    private CssLayout cssLayout;
    private HorizontalLayout layoutParent = new HorizontalLayout();

    @Override
    protected void setup(VaadinRequest request) {
        // setTheme("tests-tickets");
        addComponent(createLayoutSelect());
        addComponent(toggleRequired());
        // addComponent(toggleCaptions());
        addComponent(toggleError());
        addComponent(toggleIcon());
        addComponent(addCaptionText());
        layoutParent.addComponent(new NativeButton("Button right of layout"));
        addComponent(layoutParent);
        addComponent(new NativeButton("Button below layout"));
        createComponents();
        layoutSelect.setValue(layoutSelect.getItemIds().iterator().next());
    }

    private Component addCaptionText() {
        Button b = new Button("Add caption text");
        b.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                prependCaptions("a");
            }
        });
        return b;
    }

    protected void prependCaptions(String prepend) {
        for (AbstractField<?> c : components) {
            c.setCaption(prepend + c.getCaption());
        }

    }

    private Component toggleRequired() {
        CheckBox requiredToggle = new CheckBox();
        requiredToggle.setImmediate(true);
        requiredToggle.setCaption("Required");
        requiredToggle.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                setRequired((Boolean) event.getProperty().getValue());
            }
        });
        return requiredToggle;
    }

    private Component toggleIcon() {
        CheckBox iconToggle = new CheckBox();
        iconToggle.setImmediate(true);
        iconToggle.setCaption("Icons");
        iconToggle.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                setIcon((Boolean) event.getProperty().getValue());
            }
        });
        return iconToggle;
    }

    protected void setRequired(boolean value) {
        for (AbstractField<?> c : components) {
            c.setRequired(value);
        }

    }

    protected void setIcon(boolean value) {
        for (AbstractField<?> c : components) {
            if (!value) {
                c.setIcon(null);
            } else {
                c.setIcon(new ThemeResource("../runo/icons/16/ok.png"));
            }
        }

    }

    private Component toggleError() {
        CheckBox errorToggle = new CheckBox();
        errorToggle.setImmediate(true);
        errorToggle.setCaption("Error");
        errorToggle.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                setError((Boolean) event.getProperty().getValue());
            }
        });
        return errorToggle;
    }

    protected void setError(boolean value) {
        for (AbstractField<?> c : components) {
            if (value) {
                c.setComponentError(new UserError("error"));
            } else {
                c.setComponentError(null);

            }
        }

    }

    private void createComponents() {
        TextField tfUndefWide = new TextField(
                "Undefined wide text field with a very long caption, longer than the field and the layout. Lorem ipsum dolor sit amet.");
        TextField tf100pxWide = new TextField(
                "100 px wide text field with a very long caption, longer than 100px.");
        tf100pxWide.setWidth("100px");

        TextField tf500pxWide = new TextField(
                "500 px wide text field with a very long caption, longer than 500px. Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        tf500pxWide.setWidth("500px");

        components.add(tfUndefWide);
        components.add(tf100pxWide);
        components.add(tf500pxWide);

    }

    private void setLayout(Layout newLayout) {
        if (layout == null) {
            layoutParent.addComponent(newLayout, 0);
        } else {
            layoutParent.replaceComponent(layout, newLayout);
        }
        layout = newLayout;

        for (Component c : components) {
            if (c.getParent() != layout) {
                layout.addComponent(c);
            }
        }

    }

    private Layout getLayout(String caption,
            Class<? extends Layout> layoutClass, String width) {
        Layout l;
        if (layoutClass == VerticalLayout.class) {
            if (verticalLayout == null) {
                verticalLayout = new VerticalLayout();
                verticalLayout.setStyleName("borders");
            }
            l = verticalLayout;
        } else if (layoutClass == HorizontalLayout.class) {
            if (horizontalLayout == null) {
                horizontalLayout = new HorizontalLayout();
                horizontalLayout.setStyleName("borders");
            }
            l = horizontalLayout;
        } else if (layoutClass == GridLayout.class) {
            if (gridLayout == null) {
                gridLayout = new GridLayout();
                gridLayout.setStyleName("borders");
            }
            l = gridLayout;
        } else if (layoutClass == CssLayout.class) {
            if (cssLayout == null) {
                cssLayout = new CssLayout();
                cssLayout.setStyleName("borders");
            }
            l = cssLayout;
        } else if (layoutClass == FormLayout.class) {
            if (formLayout == null) {
                formLayout = new FormLayout();
                formLayout.setStyleName("borders");
            }
            l = formLayout;
        } else {
            return null;
        }

        l.setCaption(caption);
        if (width.equals("auto")) {
            width = null;
        }

        l.setWidth(width);

        // addComponent(l);

        return l;
    }

    private Component createLayoutSelect() {
        layoutSelect = new NativeSelect("Layout");
        layoutSelect.addContainerProperty(CAPTION, String.class, "");
        layoutSelect.addContainerProperty(CLASS, Class.class, "");
        layoutSelect.addContainerProperty(WIDTH, String.class, "");
        layoutSelect.setItemCaptionPropertyId(CAPTION);
        layoutSelect.setNullSelectionAllowed(false);

        for (Class<?> cls : new Class[] { HorizontalLayout.class,
                VerticalLayout.class, GridLayout.class, CssLayout.class,
                FormLayout.class }) {
            for (String width : new String[] { "400px", "auto" }) {
                Object id = layoutSelect.addItem();
                Item i = layoutSelect.getItem(id);
                i.getItemProperty(CAPTION).setValue(
                        cls.getSimpleName() + ", " + width);
                i.getItemProperty(CLASS).setValue(cls);
                i.getItemProperty(WIDTH).setValue(width);
            }

        }
        layoutSelect.setImmediate(true);
        layoutSelect.addListener(new ValueChangeListener() {

            @Override
            @SuppressWarnings("unchecked")
            public void valueChange(ValueChangeEvent event) {
                Item i = layoutSelect.getItem(event.getProperty().getValue());

                setLayout(getLayout((String) i.getItemProperty(CAPTION)
                        .getValue(), (Class<? extends Layout>) i
                        .getItemProperty(CLASS).getValue(), (String) i
                        .getItemProperty(WIDTH).getValue()));
            }
        });

        return layoutSelect;
    }

    @Override
    protected String getTestDescription() {
        return "Tests what happens when the caption changes in various layouts. Behavior should be consistent.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5424;
    }

}
