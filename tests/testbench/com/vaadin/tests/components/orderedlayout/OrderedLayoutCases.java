package com.vaadin.tests.components.orderedlayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;

public class OrderedLayoutCases extends AbstractTestRoot {
    private static final String[] dimensionValues = { "-1px", "5px", "350px",
            "800px", "100%", "50%" };

    private static class SampleChild extends VerticalLayout {
        public SampleChild() {
            setStyleName("sampleChild");
            addComponent(createSimpleSelector("Child width",
                    new ValueChangeListener() {
                        public void valueChange(ValueChangeEvent event) {
                            setWidth(event.getProperty().getValue().toString());
                        }
                    }, dimensionValues));
            addComponent(createSimpleSelector("Child height",
                    new ValueChangeListener() {
                        public void valueChange(ValueChangeEvent event) {
                            setHeight(event.getProperty().getValue().toString());
                        }
                    }, dimensionValues));
            addComponent(createSimpleSelector("Caption",
                    new ValueChangeListener() {
                        public void valueChange(ValueChangeEvent event) {
                            String value = event.getProperty().getValue()
                                    .toString();
                            if (value.length() == 0) {
                                setCaption(null);
                            } else if (value.equals("Long")) {
                                setCaption("A rather long caption just to see what happens");
                            } else {
                                setCaption(value);
                            }
                        }
                    }, "", "Short", "Long"));

            addComponent(createSimpleSelector("Expand ratio",
                    new ValueChangeListener() {
                        public void valueChange(ValueChangeEvent event) {
                            AbstractOrderedLayout parent = (AbstractOrderedLayout) getParent();
                            if (parent == null) {
                                return;
                            }
                            String value = event.getProperty().getValue()
                                    .toString();
                            parent.setExpandRatio(SampleChild.this,
                                    Float.parseFloat(value));
                        }
                    }, "0", "1", "2"));

            // Why is Alignment not an enum? Now we have to use reflection just
            // to get the different values as hardcoding is never an option! ;)
            List<String> alignmentValues = new ArrayList<String>();
            Field[] fields = Alignment.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == Alignment.class) {
                    alignmentValues.add(field.getName());
                }
            }
            addComponent(createSimpleSelector("Alignment",
                    new ValueChangeListener() {
                        public void valueChange(ValueChangeEvent event) {
                            String value = event.getProperty().getValue()
                                    .toString();
                            AlignmentHandler parent = (AlignmentHandler) getParent();
                            if (parent == null) {
                                return;
                            }
                            try {
                                Field field = Alignment.class
                                        .getDeclaredField(value);
                                Alignment alignment = (Alignment) field
                                        .get(null);
                                parent.setComponentAlignment(SampleChild.this,
                                        alignment);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, alignmentValues, "TOP_LEFT")); // Sorry for not using
                                                      // more reflection magic
                                                      // just to find the
                                                      // default value...

        }
    }

    private AbstractOrderedLayout currentLayout;
    private HorizontalLayout sizeBar;

    @Override
    protected void setup(WrappedRequest request) {
        TestUtils
                .injectCSS(
                        getRoot(),
                        ".sampleChild, .theLayout {border: 1px solid black;}"
                                + ".theLayout > div > div:first-child {background: aqua;}"
                                + ".theLayout > div > div:first-child + div {background: yellow;}"
                                + ".theLayout > div > div:first-child + div + div {background: lightgrey;}");

        currentLayout = new HorizontalLayout();
        for (int i = 0; i < 3; i++) {
            currentLayout.addComponent(new SampleChild());
        }

        sizeBar = new HorizontalLayout();
        sizeBar.setSpacing(true);

        sizeBar.addComponent(createSimpleSelector("Layout width",
                new ValueChangeListener() {
                    public void valueChange(ValueChangeEvent event) {
                        currentLayout.setWidth(event.getProperty().getValue()
                                .toString());
                    }
                }, dimensionValues));
        sizeBar.addComponent(createSimpleSelector("Layout height",
                new ValueChangeListener() {
                    public void valueChange(ValueChangeEvent event) {
                        currentLayout.setHeight(event.getProperty().getValue()
                                .toString());
                    }
                }, dimensionValues));
        sizeBar.addComponent(createSimpleSelector("Spacing",
                new ValueChangeListener() {
                    public void valueChange(ValueChangeEvent event) {
                        currentLayout.setSpacing(Boolean.parseBoolean(event
                                .getProperty().getValue().toString()));
                    }
                }, "false", "true"));
        sizeBar.addComponent(createSimpleSelector("Margin",
                new ValueChangeListener() {
                    public void valueChange(ValueChangeEvent event) {
                        currentLayout.setMargin(Boolean.parseBoolean(event
                                .getProperty().getValue().toString()));
                    }
                }, "false", "true"));
        sizeBar.addComponent(createSimpleSelector("Direction",
                new ValueChangeListener() {
                    public void valueChange(ValueChangeEvent event) {
                        Object value = event.getProperty().getValue();

                        AbstractOrderedLayout newLayout;
                        if (value.equals("Horizontal")) {
                            newLayout = new HorizontalLayout();
                        } else {
                            newLayout = new VerticalLayout();
                        }

                        while (currentLayout.getComponentCount() > 0) {
                            newLayout.addComponent(currentLayout
                                    .getComponent(0));
                        }
                        newLayout.setStyleName("theLayout");

                        newLayout.setHeight(currentLayout.getHeight(),
                                currentLayout.getHeightUnits());
                        newLayout.setWidth(currentLayout.getWidth(),
                                currentLayout.getWidthUnits());

                        newLayout.setMargin(currentLayout.getMargin());
                        newLayout.setSpacing(currentLayout.isSpacing());

                        getLayout().replaceComponent(currentLayout, newLayout);
                        getLayout().setExpandRatio(newLayout, 1);
                        currentLayout = newLayout;
                    }
                }, "Horizontal", "Vertical"));

        HorizontalLayout caseBar = new HorizontalLayout();
        caseBar.addComponent(new Button("Undefined without relative",
                new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        // width: 350px to middle child
                        setChildState(1, 0, 2);
                    }
                }));
        caseBar.addComponent(new Button("Undefined with relative",
                new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        // width: 100% to middle child
                        setChildState(1, 0, 4);
                    }
                }));
        caseBar.addComponent(new Button("Fixed with overflow",
                new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        // layout width: 350px
                        setState(sizeBar, 0, 2);
                        // layout margin enabled
                        setState(sizeBar, 3, 1);
                    }
                }));
        caseBar.addComponent(new Button("Fixed with extra space",
                new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        // Layout width: 800px
                        setState(sizeBar, 0, 3);
                        // layout margin enabled
                        setState(sizeBar, 3, 1);
                        // width: 350px to middle child
                        setChildState(1, 0, 2);
                    }
                }));

        caseBar.addComponent(new Button("Expand with alignment",
                new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        // Layout width: 800px
                        setState(sizeBar, 0, 3);
                        // Layout height: 350px
                        setState(sizeBar, 1, 2);
                        // Expand: 1 to middle child
                        setChildState(1, 3, 1);
                        // Align bottom left to middle child
                        setChildState(1, 4, 6);
                    }
                }));

        caseBar.addComponent(new Button("Multiple expands",
                new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        // Layout width: 800px
                        setState(sizeBar, 0, 3);
                        // Layout height: 350px
                        setState(sizeBar, 1, 2);
                        // Width 350px to middle child
                        setChildState(1, 0, 2);
                        // Apply to left and middle child
                        for (int i = 0; i < 2; i++) {
                            // Expand: 1
                            setChildState(i, 3, 1);
                            // Align: middle center
                            setChildState(i, 4, 5);
                        }
                    }
                }));

        caseBar.addComponent(new Button("Fixed + relative height",
                new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        // Layout height: 100%
                        setState(sizeBar, 1, 4);
                        // Height: 350px to left child
                        setChildState(0, 1, 2);
                        // Height: 100% to middle child
                        setChildState(1, 1, 4);
                        // Alignment: bottom left to right child
                        setChildState(2, 4, 7);
                    }
                }));

        caseBar.addComponent(new Button("Undefined + relative height",
                new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        // Height: 350px to left child
                        setChildState(0, 1, 2);
                        // Height: 100% to middle child
                        setChildState(1, 1, 4);
                        // Alignment: bottom left to right child
                        setChildState(2, 4, 7);
                    }
                }));

        caseBar.setSpacing(true);

        addComponent(caseBar);
        addComponent(sizeBar);
        addComponent(currentLayout);

        getLayout().setSpacing(true);
        getContent().setSizeFull();
        getLayout().setSizeFull();
        getLayout().setExpandRatio(currentLayout, 1);
    }

    private void resetState() {
        for (int i = 0; i < sizeBar.getComponentCount(); i++) {
            setState(sizeBar, i, 0);
        }
        for (int i = 0; i < 3; i++) {
            // Child width and height -> -1px
            SampleChild child = (SampleChild) currentLayout.getComponent(i);
            for (int j = 0; j < child.getComponentCount(); j++) {
                if (j == 4) {
                    setState(child, j, 1);
                } else {
                    setState(child, j, 0);
                }
            }
        }
    }

    private void setChildState(int childIndex, int selectIndex, int valueIndex) {
        Component child = currentLayout.getComponent(childIndex);
        setState(child, selectIndex, valueIndex);
    }

    private static void setState(Component container, int selectIndex, int value) {
        NativeSelect select = (NativeSelect) ((AbstractOrderedLayout) container)
                .getComponent(selectIndex);
        select.setValue(new ArrayList<Object>(select.getItemIds()).get(value));
    }

    private static NativeSelect createSimpleSelector(String caption,
            ValueChangeListener listener, String... values) {
        return createSimpleSelector(caption, listener, Arrays.asList(values),
                values[0]);
    }

    private static NativeSelect createSimpleSelector(String caption,
            ValueChangeListener listener, List<String> values,
            String defaultValue) {
        NativeSelect selector = new NativeSelect(caption, values);
        selector.setNullSelectionAllowed(false);
        selector.setImmediate(true);
        selector.addListener(listener);
        selector.setValue(defaultValue);
        return selector;
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    protected String getTestDescription() {
        return "Tester application for exploring how Horizontal/VerticalLayout reacts to various settings ";
    }

}
