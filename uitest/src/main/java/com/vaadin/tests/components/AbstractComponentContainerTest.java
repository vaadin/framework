package com.vaadin.tests.components;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents.ComponentAttachEvent;
import com.vaadin.ui.HasComponents.ComponentAttachListener;
import com.vaadin.ui.HasComponents.ComponentDetachEvent;
import com.vaadin.ui.HasComponents.ComponentDetachListener;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalSplitPanel;

public abstract class AbstractComponentContainerTest<T extends AbstractComponentContainer>
        extends AbstractComponentTest<T> implements ComponentAttachListener,
        ComponentDetachListener {

    private String CATEGORY_COMPONENT_CONTAINER_FEATURES = "Component container features";
    private Command<T, ComponentSize> addButtonCommand = new Command<T, ComponentSize>() {

        @Override
        public void execute(T c, ComponentSize size, Object data) {
            Button b = new Button("A button");
            c.addComponent(b);
            size.apply(b);
        }
    };

    private Command<T, ComponentSize> addNativeButtonCommand = new Command<T, ComponentSize>() {

        @Override
        public void execute(T c, ComponentSize size, Object data) {
            NativeButton b = new NativeButton("Native button");
            c.addComponent(b);
            size.apply(b);
        }
    };

    private Command<T, ComponentSize> addTextAreaCommand = new Command<T, ComponentSize>() {
        @Override
        public void execute(T c, ComponentSize size, Object data) {
            TextArea ta = new TextArea();
            c.addComponent(ta);
            size.apply(ta);
        }
    };

    private Command<T, ComponentSize> addRichTextAreaCommand = new Command<T, ComponentSize>() {
        @Override
        public void execute(T c, ComponentSize size, Object data) {
            RichTextArea ta = new RichTextArea();
            c.addComponent(ta);
            size.apply(ta);
        }
    };

    private Command<T, ComponentSize> addTextFieldCommand = new Command<T, ComponentSize>() {
        @Override
        public void execute(T c, ComponentSize size, Object data) {
            TextField tf = new TextField();
            c.addComponent(tf);
            size.apply(tf);
        }
    };

    private Command<T, ComponentSize> addInlineDateFieldCommand = new Command<T, ComponentSize>() {
        @Override
        public void execute(T c, ComponentSize size, Object data) {
            InlineDateField tf = new InlineDateField();
            c.addComponent(tf);
            size.apply(tf);
        }
    };
    private Command<T, ComponentSize> addPopupDateFieldCommand = new Command<T, ComponentSize>() {
        @Override
        public void execute(T c, ComponentSize size, Object data) {
            PopupDateField tf = new PopupDateField();
            c.addComponent(tf);
            size.apply(tf);
        }
    };

    private Command<T, ComponentSize> addVerticalSplitPanelCommand = new Command<T, ComponentSize>() {
        @Override
        public void execute(T c, ComponentSize size, Object data) {
            VerticalSplitPanel vsp = new VerticalSplitPanel();
            c.addComponent(vsp);
            size.apply(vsp);
        }
    };

    private Command<T, ComponentSize> addHorizontalSplitPanelCommand = new Command<T, ComponentSize>() {
        @Override
        public void execute(T c, ComponentSize size, Object data) {
            HorizontalSplitPanel vsp = new HorizontalSplitPanel();
            c.addComponent(vsp);
            size.apply(vsp);
        }
    };

    private Command<T, ComponentSize> addTabSheetCommand = new Command<T, ComponentSize>() {
        @Override
        public void execute(T c, ComponentSize size, Object data) {
            TabSheet ts = createTabSheet();
            c.addComponent(ts);
            size.apply(ts);
        }
    };

    private Command<T, ComponentSize> addTableCommand = new Command<T, ComponentSize>() {

        @Override
        public void execute(T c, ComponentSize size, Object data) {
            Table t = createTable();
            c.addComponent(t);
            size.apply(t);
        }
    };
    private Command<T, Object> removeAllComponentsCommand = new Command<T, Object>() {
        @Override
        public void execute(T c, Object value, Object data) {
            c.removeAllComponents();
        }
    };
    private Command<T, Integer> removeComponentByIndexCommand = new Command<T, Integer>() {

        @Override
        public void execute(T c, Integer value, Object data) {
            Component child = getComponentAtIndex(c, value);
            c.removeComponent(child);

        }
    };
    private Command<T, Boolean> componentAttachListenerCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            if (value) {
                c.addListener((ComponentAttachListener) AbstractComponentContainerTest.this);
            } else {
                c.removeListener((ComponentAttachListener) AbstractComponentContainerTest.this);
            }
        }
    };

    private Command<T, Boolean> componentDetachListenerCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            if (value) {
                c.addListener((ComponentDetachListener) AbstractComponentContainerTest.this);
            } else {
                c.removeListener((ComponentDetachListener) AbstractComponentContainerTest.this);
            }
        }
    };

    private Command<T, Integer> setComponentHeight = new Command<T, Integer>() {

        @Override
        public void execute(T c, Integer value, Object data) {
            Component child = getComponentAtIndex(c, value);
            child.setHeight((String) data);

        }
    };

    private Command<T, Integer> setComponentWidth = new Command<T, Integer>() {

        @Override
        public void execute(T c, Integer value, Object data) {
            Component child = getComponentAtIndex(c, value);
            child.setWidth((String) data);

        }
    };

    protected static class ComponentSize {
        private String width, height;

        public ComponentSize(String width, String height) {
            this.width = width;
            this.height = height;
        }

        public void apply(Component target) {
            target.setWidth(width);
            target.setHeight(height);
        }

        public String getWidth() {
            return width;
        }

        public String getHeight() {
            return height;
        }

        @Override
        public String toString() {
            String s = "";
            s += width == null ? "auto" : width;
            s += " x ";
            s += height == null ? "auto" : height;
            return s;
        }
    }

    @Override
    protected void createActions() {
        super.createActions();

        createAddComponentActions(CATEGORY_COMPONENT_CONTAINER_FEATURES);
        createRemoveComponentActions(CATEGORY_COMPONENT_CONTAINER_FEATURES);
        createChangeComponentSizeActions(CATEGORY_COMPONENT_CONTAINER_FEATURES);
        createComponentAttachListener(CATEGORY_LISTENERS);
        createComponentDetachListener(CATEGORY_LISTENERS);
    }

    protected Component getComponentAtIndex(T container, int value) {
        Iterator<Component> iter = container.getComponentIterator();
        for (int i = 0; i < value; i++) {
            iter.next();
        }

        return iter.next();
    }

    protected Table createTable() {
        Table t = new Table();
        t.addContainerProperty("property 1", String.class, "");
        t.addContainerProperty("property 2", String.class, "");
        t.addContainerProperty("property 3", String.class, "");
        for (int i = 1; i < 10; i++) {
            t.addItem(new Object[] { "row/col " + i + "/1",
                    "row/col " + i + "/2", "row/col " + i + "/3" },
                    String.valueOf(i));
        }
        return t;
    }

    protected TabSheet createTabSheet() {
        TabSheet ts = new TabSheet();
        Table t = createTable();
        t.setSizeFull();
        ts.addTab(t, "Size full Table", ICON_16_USER_PNG_UNCACHEABLE);
        ts.addTab(new Button("A button"), "Button", null);
        return ts;
    }

    private void createComponentAttachListener(String category) {
        createBooleanAction("Component attach listener", category, false,
                componentAttachListenerCommand);

    }

    private void createComponentDetachListener(String category) {
        createBooleanAction("Component detach listener", category, false,
                componentDetachListenerCommand);

    }

    private void createRemoveComponentActions(String category) {
        String subCategory = "Remove component";
        String byIndexCategory = "By index";

        createCategory(subCategory, category);
        createCategory(byIndexCategory, subCategory);
        createClickAction("Remove all components", subCategory,
                removeAllComponentsCommand, null);
        for (int i = 0; i < 20; i++) {
            createClickAction("Remove component " + i, byIndexCategory,
                    removeComponentByIndexCommand, Integer.valueOf(i));
        }

    }

    private void createAddComponentActions(String category) {
        String subCategory = "Add component";
        createCategory(subCategory, category);

        LinkedHashMap<String, Command<T, ComponentSize>> addCommands = new LinkedHashMap<String, AbstractComponentTestCase.Command<T, ComponentSize>>();
        addCommands.put("Button", addButtonCommand);
        addCommands.put("NativeButton", addNativeButtonCommand);
        addCommands.put("TextField", addTextFieldCommand);
        addCommands.put("TextArea", addTextAreaCommand);
        addCommands.put("RichTextArea", addRichTextAreaCommand);
        addCommands.put("TabSheet", addTabSheetCommand);
        addCommands.put("Table", addTableCommand);
        addCommands.put("InlineDateField", addInlineDateFieldCommand);
        addCommands.put("PopupDateField", addPopupDateFieldCommand);
        addCommands.put("VerticalSplitPanel", addVerticalSplitPanelCommand);
        addCommands.put("HorizontalSplitPanel", addHorizontalSplitPanelCommand);

        HashSet<String> noVerticalSize = new HashSet<String>();
        noVerticalSize.add("TextField");
        noVerticalSize.add("Button");

        // addCommands.put("AbsoluteLayout", addAbsoluteLayoutCommand);
        // addCommands.put("HorizontalLayout", addHorizontalLayoutCommand);
        // addCommands.put("VerticalLayout", addVerticalLayoutCommand);

        ComponentSize[] sizes = new ComponentSize[] {
                new ComponentSize(null, null),
                new ComponentSize("200px", null),
                new ComponentSize("100%", null),
                new ComponentSize(null, "200px"),
                new ComponentSize(null, "100%"),
                new ComponentSize("300px", "300px"),
                new ComponentSize("100%", "100%"),

        };

        for (String componentCategory : addCommands.keySet()) {
            createCategory(componentCategory, subCategory);

            for (ComponentSize size : sizes) {
                if (size.getHeight() != null
                        && noVerticalSize.contains(componentCategory)) {
                    continue;
                }
                createClickAction(size.toString(), componentCategory,
                        addCommands.get(componentCategory), size);
            }
        }

    }

    private void createChangeComponentSizeActions(String category) {
        String widthCategory = "Change component width";
        createCategory(widthCategory, category);
        String heightCategory = "Change component height";
        createCategory(heightCategory, category);

        String[] options = new String[] { "100px", "200px", "50%", "100%" };
        for (int i = 0; i < 20; i++) {
            String componentWidthCategory = "Component " + i + " width";
            String componentHeightCategory = "Component " + i + " height";
            createCategory(componentWidthCategory, widthCategory);
            createCategory(componentHeightCategory, heightCategory);

            createClickAction("auto", componentHeightCategory,
                    setComponentHeight, Integer.valueOf(i), null);
            createClickAction("auto", componentWidthCategory,
                    setComponentWidth, Integer.valueOf(i), null);
            for (String option : options) {
                createClickAction(option, componentHeightCategory,
                        setComponentHeight, Integer.valueOf(i), option);
                createClickAction(option, componentWidthCategory,
                        setComponentWidth, Integer.valueOf(i), option);
            }

        }

    }

    @Override
    public void componentDetachedFromContainer(ComponentDetachEvent event) {
        log(event.getClass().getSimpleName() + ": "
                + event.getDetachedComponent().getClass().getSimpleName()
                + " detached from "
                + event.getContainer().getClass().getSimpleName());
    }

    @Override
    public void componentAttachedToContainer(ComponentAttachEvent event) {
        log(event.getClass().getSimpleName() + ": "
                + event.getAttachedComponent().getClass().getSimpleName()
                + " attached to "
                + event.getContainer().getClass().getSimpleName());

    }

}
