package com.vaadin.tests.layouts;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.HasValue;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.AbstractLegacyComponent;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.PasswordField;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

public class CaptionsInLayoutsWaiAria extends TestBase {

    private static final Object CAPTION = "CAPTION";
    private static final Object CLASS = "C";
    private static final Object WIDTH = "W";

    private NativeSelect layoutSelect;
    private Layout layout;
    private VerticalLayout verticalLayout;
    private HorizontalLayout horizontalLayout;
    private GridLayout gridLayout;
    private FormLayout formLayout;
    private List<AbstractComponent> components = new ArrayList<>();
    private CssLayout cssLayout;
    private HorizontalLayout layoutParent = new HorizontalLayout();

    @Override
    protected void setup() {
        // setTheme("tests-tickets");
        addComponent(createLayoutSelect());
        addComponent(toggleRequired());
        // addComponent(toggleCaptions());
        // addComponent(toggleError());
        addComponent(toggleIcon());
        addComponent(toggleReadOnly());
        addComponent(toggleInvalid());
        addComponent(toggleEnabled());
        addComponent(addCaptionText());
        // layoutParent.addComponent(new
        // NativeButton("Button right of layout"));
        addComponent(layoutParent);
        // addComponent(new NativeButton("Button below layout"));
        createComponents();
        layoutSelect.setValue(layoutSelect.getItemIds().iterator().next());
    }

    private Component addCaptionText() {
        Button b = new Button("Add caption text");
        b.addClickListener(event -> prependCaptions("a"));
        return b;
    }

    protected void prependCaptions(String prepend) {
        for (Component c : components) {
            c.setCaption(prepend + c.getCaption());
        }
    }

    private Component toggleRequired() {
        CheckBox requiredToggle = new CheckBox();
        requiredToggle.setCaption("Required");
        requiredToggle
                .addValueChangeListener(event -> setRequired(event.getValue()));
        return requiredToggle;
    }

    private Component toggleIcon() {
        CheckBox iconToggle = new CheckBox();
        iconToggle.setCaption("Icons");
        iconToggle.addValueChangeListener(event -> setIcon(event.getValue()));
        return iconToggle;
    }

    private Component toggleReadOnly() {
        CheckBox readOnlyToggle = new CheckBox();
        readOnlyToggle.setCaption("Read only");
        readOnlyToggle
                .addValueChangeListener(event -> setReadOnly(event.getValue()));

        return readOnlyToggle;
    }

    private Component toggleEnabled() {
        CheckBox enabledToggle = new CheckBox();
        enabledToggle.setValue(true);
        enabledToggle.setCaption("Enabled");
        enabledToggle
                .addValueChangeListener(event -> setEnabled(event.getValue()));

        return enabledToggle;
    }

    private Component toggleInvalid() {
        CheckBox invalid = new CheckBox("Invalid");
        invalid.addValueChangeListener(event -> setInvalid(event.getValue()));

        return invalid;
    }

    protected void setInvalid(boolean value) {
        UserError userError = null;
        if (value) {
            userError = new UserError(
                    "Der eingegebene Wert ist nicht zulässig!");
        }

        for (AbstractComponent c : components) {
            c.setComponentError(userError);
        }
    }

    protected void setRequired(boolean value) {
        for (AbstractComponent c : components) {
            if (c instanceof HasValue) {
                ((HasValue) c).setRequiredIndicatorVisible(value);
            } else if (c instanceof Field) {
                ((Field) c).setRequired(value);
            }
        }

    }

    protected void setIcon(boolean value) {
        for (Component c : components) {
            if (!value) {
                c.setIcon(null);
            } else {
                c.setIcon(new ThemeResource("../runo/icons/16/ok.png"));
            }
        }

    }

    protected void setReadOnly(boolean value) {
        for (Component c : components) {
            if (c instanceof HasValue) {
                ((HasValue<String>) c).setReadOnly(value);
            } else if (c instanceof AbstractLegacyComponent) {
                ((AbstractLegacyComponent) c).setReadOnly(value);
            }
        }
    }

    protected void setEnabled(boolean value) {
        for (Component c : components) {
            c.setEnabled(value);
        }
    }

    @SuppressWarnings("unused")
    private Component toggleError() {
        CheckBox errorToggle = new CheckBox();
        errorToggle.setCaption("Error");
        errorToggle.addValueChangeListener(event -> setError(event.getValue()));
        return errorToggle;
    }

    protected void setError(boolean value) {
        for (AbstractComponent c : components) {
            if (value) {
                c.setComponentError(new UserError("error"));
            } else {
                c.setComponentError(null);

            }
        }

    }

    private void createComponents() {
        components.add(new TextField("Default TextBox"));
        components.add(new TextArea("Default TextArea."));
        // components.add(new RichTextArea("Default RichtTextArea"));
        components.add(new PasswordField("Default Password"));
        components.add(new TestDateField("Default DateField"));

        // PopupDateField popupDateField = new
        // PopupDateField("Default DateField");
        // popupDateField.setTextFieldEnabled(false);
        // components.add(popupDateField);

        components.add(new CheckBox("Default CheckBox"));

        ComboBox<String> comboBox = new ComboBox<>("Default ComboBox");
        comboBox.setItems("Item1");
        components.add(comboBox);

        OptionGroup radioGroup = new OptionGroup("Single Items");
        radioGroup.addItem("Single Item 1");
        radioGroup.addItem("Single Item 2");
        radioGroup.setMultiSelect(false);
        components.add(radioGroup);

        OptionGroup checkGroup = new OptionGroup("Multi Items");
        checkGroup.addItem("Multi Item 1");
        checkGroup.addItem("Multi Item 2");
        checkGroup.setMultiSelect(true);
        components.add(checkGroup);

        // Tree tree = new Tree();
        // tree.setCaption("tree");
        // tree.addItem("single item");
        // components.add(tree);
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
            for (String width : new String[] { "auto" }) {
                Object id = layoutSelect.addItem();
                Item i = layoutSelect.getItem(id);
                i.getItemProperty(CAPTION)
                        .setValue(cls.getSimpleName() + ", " + width);
                i.getItemProperty(CLASS).setValue(cls);
                i.getItemProperty(WIDTH).setValue(width);
            }

        }
        layoutSelect.setImmediate(true);
        layoutSelect.addValueChangeListener(event -> {
            Item i = layoutSelect.getItem(event.getProperty().getValue());

            setLayout(getLayout((String) i.getItemProperty(CAPTION).getValue(),
                    (Class<? extends Layout>) i.getItemProperty(CLASS)
                            .getValue(),
                    (String) i.getItemProperty(WIDTH).getValue()));
        });

        return layoutSelect;
    }

    @Override
    protected String getDescription() {
        return "Tests what happens when the caption changes in various layouts. Behavior should be consistent.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5424;
    }

}
