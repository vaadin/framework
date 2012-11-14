package com.vaadin.tests.layouts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LayoutPerformanceTests extends TestBase {
    private static final String[] widths = { null, "100%", "200px" };

    private enum ContainerType {
        SIMPLE_WRAPS {
            @Override
            public ComponentContainer buildLayout(int depth, int leafs,
                    SampleType leafType, boolean fullHeight) {
                if (depth == 0) {
                    return buildInnerLayout(leafs, leafType, fullHeight);
                }

                AbstractOrderedLayout layout = createOrderedLayout(depth,
                        fullHeight);
                layout.addComponent(buildLayout(depth - 1, leafs, leafType,
                        fullHeight));
                return layout;
            }
        },
        BORDER_LAYOUT {
            @Override
            public ComponentContainer buildLayout(int depth, int leafs,
                    SampleType leafType, boolean fullHeight) {
                if (depth == 0) {
                    return buildInnerLayout(leafs, leafType, fullHeight);
                }

                AbstractOrderedLayout layout = createOrderedLayout(depth,
                        fullHeight);
                Component content = leafType.createContent();
                content.setSizeUndefined();
                layout.addComponent(content);
                layout.addComponent(buildLayout(depth - 1, leafs, leafType,
                        fullHeight));
                layout.setExpandRatio(layout.getComponent(1), 1);
                return layout;
            }
        },
        FRACTAL {
            @Override
            public ComponentContainer buildLayout(int depth, int leafs,
                    SampleType leafType, boolean fullHeight) {
                if (depth == 0) {
                    return buildInnerLayout(leafs, leafType, fullHeight);
                }

                AbstractOrderedLayout layout = createOrderedLayout(depth,
                        fullHeight);
                layout.addComponent(buildLayout(depth - 1, leafs, leafType,
                        fullHeight));
                layout.addComponent(buildLayout(depth - 1, leafs, leafType,
                        fullHeight));
                layout.setExpandRatio(layout.getComponent(0), 1);
                layout.setExpandRatio(layout.getComponent(1), 2);
                return layout;
            }
        };
        public abstract ComponentContainer buildLayout(int depth, int leafs,
                SampleType leafType, boolean fullHeight);

        protected AbstractOrderedLayout createOrderedLayout(int depth,
                boolean fullHeight) {
            AbstractOrderedLayout layout = (depth % 2) == 0 ? new VerticalLayout()
                    : new HorizontalLayout();
            layout.setWidth("100%");
            if (fullHeight) {
                layout.setHeight("100%");
            } else {
                layout.setHeight(null);
            }

            return layout;
        }

        public ComponentContainer buildInnerLayout(int leafs,
                SampleType leafType, boolean fullHeight) {
            VerticalLayout layout = new VerticalLayout();
            if (fullHeight) {
                layout.setHeight("100%");
                layout.setWidth("100%");
            }
            for (int i = 0; i < leafs; i++) {
                Component leaf = leafType.createContent();
                if (leaf.getWidth() <= 0) {
                    leaf.setWidth(widths[i % 3]);
                }
                layout.addComponent(leaf);
            }
            return layout;
        }
    }

    private enum SampleType {
        SHORT_LABEL {
            @Override
            public Component createContent() {
                return new Label("Short label");
            }
        },
        LONG_LABEL {
            @Override
            public Component createContent() {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 100; i++) {
                    builder.append("A rather long text. ");
                }
                return new Label(builder.toString());
            }
        },
        BUTTON {
            @Override
            public Component createContent() {
                return new Button("Text");
            }
        },
        TEXT_FIELD {
            @Override
            public Component createContent() {
                return new TextField("Field label");
            }
        },
        HORIZONTAL_LAYOUT {
            @Override
            public Component createContent() {
                HorizontalLayout layout = new HorizontalLayout();
                layout.addComponent(new Label("Left"));
                layout.addComponent(new Label("Right"));
                layout.setComponentAlignment(layout.getComponent(1),
                        Alignment.BOTTOM_RIGHT);

                return layout;
            }
        },
        WRAPPED_PANEL {
            @Override
            public Component createContent() {
                HorizontalLayout horizontal = new HorizontalLayout();
                horizontal.setWidth("100%");
                horizontal.setHeight(null);
                horizontal.setMargin(true);

                VerticalLayout left = new VerticalLayout();
                left.setWidth("100%");
                left.addComponent(new Label("Text 1"));
                left.addComponent(new Label("Text 2"));
                left.addComponent(new Label("Text 3"));
                horizontal.addComponent(left);

                VerticalLayout right = new VerticalLayout();
                right.setWidth("100%");
                right.addComponent(new Label("Text 1"));
                right.addComponent(new Label("Text 2"));
                right.addComponent(new Label("Text 3"));
                horizontal.addComponent(right);

                Panel panel = new Panel(horizontal);
                panel.setCaption("Panel caption");
                panel.setWidth("100%");
                panel.setHeight(null);

                return panel;
            }
        };
        public abstract Component createContent();
    }

    private Component testLayout = new Label("");

    private final CheckBox wrapInPanel = new CheckBox("Wrap in Panel");
    private final NativeSelect containerSelector = new NativeSelect(
            "Wrapping structure", EnumSet.allOf(ContainerType.class));
    @SuppressWarnings("boxing")
    private final NativeSelect levels = new NativeSelect("Wrapping depth",
            Arrays.asList(0, 1, 2, 3, 4, 5, 10, 15, 20, 25));
    private final NativeSelect leafSelector = new NativeSelect("Leaf type",
            EnumSet.allOf(SampleType.class));
    @SuppressWarnings("boxing")
    private final NativeSelect childAmount = new NativeSelect("Leaf count",
            Arrays.asList(1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048,
                    4096, 8192, 16384));

    @Override
    protected void setup() {
        HorizontalLayout controls = new HorizontalLayout();
        controls.setSpacing(true);

        controls.addComponent(wrapInPanel);
        controls.addComponent(containerSelector);
        controls.addComponent(levels);
        controls.addComponent(leafSelector);
        controls.addComponent(childAmount);

        controls.addComponent(new Button("Clear", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setTestLayout(new Label(""));
            }
        }));

        controls.addComponent(new Button("Apply", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                SampleType leafType = (SampleType) leafSelector.getValue();
                if (leafType == null) {
                    return;
                }

                ContainerType containerType = (ContainerType) containerSelector
                        .getValue();
                if (containerType == null) {
                    return;
                }

                boolean wrapped = wrapInPanel.booleanValue();
                ComponentContainer container = containerType.buildLayout(
                        ((Number) levels.getValue()).intValue(),
                        ((Number) childAmount.getValue()).intValue(), leafType,
                        !wrapped);
                if (wrapped) {
                    Panel panel = new Panel(container);
                    panel.setSizeFull();
                    setTestLayout(panel);
                } else {
                    setTestLayout(container);
                }
            }
        }));

        for (Iterator<Component> i = controls.getComponentIterator(); i
                .hasNext();) {
            Component component = i.next();
            if (component instanceof NativeSelect) {
                NativeSelect nativeSelect = (NativeSelect) component;
                nativeSelect.setNullSelectionAllowed(false);
                nativeSelect.setValue(new ArrayList<Object>(nativeSelect
                        .getItemIds()).get(0));
            }
            controls.setComponentAlignment(component, Alignment.BOTTOM_LEFT);
        }

        VerticalLayout layout = getLayout();
        layout.addComponent(controls);
        layout.addComponent(testLayout);
        layout.setExpandRatio(testLayout, 1);
        layout.setSizeFull();
    }

    public void setTestLayout(Component testLayout) {
        getLayout().replaceComponent(this.testLayout, testLayout);
        getLayout().setExpandRatio(testLayout, 1);
        this.testLayout = testLayout;
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
