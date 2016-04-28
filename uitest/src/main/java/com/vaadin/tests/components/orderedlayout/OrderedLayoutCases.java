package com.vaadin.tests.components.orderedlayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
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

@Theme("tests-components")
public class OrderedLayoutCases extends AbstractTestUI {

    private static final String[] dimensionValues = { "-1px", "5px", "350px",
            "800px", "100%", "50%" };

    private static class SampleChild extends VerticalLayout {
        public SampleChild(int i) {
            addStyleName("sampleChild");
            addStyleName("sampleChild" + i);
            addComponent(createSimpleSelector("Child width",
                    new ValueChangeListener() {
                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            setWidth(event.getProperty().getValue().toString());
                        }
                    }, dimensionValues));
            addComponent(createSimpleSelector("Child height",
                    new ValueChangeListener() {
                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            setHeight(event.getProperty().getValue().toString());
                        }
                    }, dimensionValues));
            addComponent(createSimpleSelector("Caption",
                    new ValueChangeListener() {
                        @Override
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
                        @Override
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
                        @Override
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
    protected void setup(VaadinRequest request) {
        TestUtils.injectCSS(getUI(),
                ".sampleChild, .theLayout {border: 1px solid black;}"
                        + ".sampleChild1 {background: aqua;}"
                        + ".sampleChild2 {background: yellow;}"
                        + ".sampleChild3 {background: lightgrey;}");

        currentLayout = new HorizontalLayout();
        for (int i = 0; i < 3; i++) {
            currentLayout.addComponent(new SampleChild(i + 1));
        }

        sizeBar = new HorizontalLayout();
        sizeBar.setSpacing(true);

        sizeBar.addComponent(createSimpleSelector("Layout width",
                new ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        currentLayout.setWidth(event.getProperty().getValue()
                                .toString());
                    }
                }, dimensionValues));
        sizeBar.addComponent(createSimpleSelector("Layout height",
                new ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        currentLayout.setHeight(event.getProperty().getValue()
                                .toString());
                    }
                }, dimensionValues));
        sizeBar.addComponent(createSimpleSelector("Spacing",
                new ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        currentLayout.setSpacing(Boolean.parseBoolean(event
                                .getProperty().getValue().toString()));
                    }
                }, "false", "true"));
        sizeBar.addComponent(createSimpleSelector("Margin",
                new ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        currentLayout.setMargin(Boolean.parseBoolean(event
                                .getProperty().getValue().toString()));
                    }
                }, "false", "true"));
        sizeBar.addComponent(createSimpleSelector("Direction",
                new ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        Object value = event.getProperty().getValue();

                        AbstractOrderedLayout newLayout;
                        if (value.equals("Horizontal")) {
                            newLayout = new HorizontalLayout();
                        } else {
                            newLayout = new VerticalLayout();
                        }

                        while (currentLayout.getComponentCount() > 0) {
                            Component child = currentLayout.getComponent(0);
                            Alignment alignment = currentLayout
                                    .getComponentAlignment(child);
                            float expRatio = currentLayout
                                    .getExpandRatio(child);
                            newLayout.addComponent(child);
                            newLayout.setExpandRatio(child, expRatio);
                            newLayout.setComponentAlignment(child, alignment);

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
                    @Override
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        setState(sizeBar, 2, 1);
                        // width: 350px to middle child
                        setChildState(1, 0, 2);
                        // middle center allign to middle child
                        setChildState(1, 4, 5);
                        // long captions to right child
                        setChildState(2, 2, 2);
                    }
                }));
        caseBar.addComponent(new Button("Undefined with relative",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        // width: 100% to middle child
                        setChildState(1, 0, 4);
                    }
                }));
        caseBar.addComponent(new Button("Fixed with overflow",
                new ClickListener() {
                    @Override
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
                    @Override
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        // Layout width: 800px
                        setState(sizeBar, 0, 3);
                        // layout margin enabled
                        setState(sizeBar, 3, 1);
                        // width: 350px to middle child
                        setChildState(1, 0, 2);
                        // short caption for middle child
                        setChildState(1, 2, 1);
                        // top center align for middle child
                        setChildState(1, 4, 2);
                    }
                }));

        caseBar.addComponent(new Button("Expand with alignment",
                new ClickListener() {
                    @Override
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
                        // Long caption to middle child
                        setChildState(1, 2, 2);
                    }
                }));

        caseBar.addComponent(new Button("Multiple expands",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        // Layout width: 800px
                        setState(sizeBar, 0, 3);
                        // Layout height: 350px
                        setState(sizeBar, 1, 2);
                        // Long caption to left child
                        setChildState(0, 2, 2);
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
                    @Override
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        // Layout height: 100%
                        setState(sizeBar, 1, 4);
                        // Height: 350px to left child
                        setChildState(0, 1, 2);
                        // Height: 100% to middle child
                        setChildState(1, 1, 4);
                        // Short caption to middle child
                        setChildState(1, 2, 1);
                        // Alignment: bottom left to right child
                        setChildState(2, 4, 7);
                    }
                }));

        caseBar.addComponent(new Button("Undefined + relative height",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        // Height: 350px to left child
                        setChildState(0, 1, 2);
                        // Short caption to left child
                        setChildState(0, 2, 1);
                        // Height: 100% to middle child
                        setChildState(1, 1, 4);
                    }
                }));

        caseBar.addComponent(new Button("Undefined + alignments",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        // Height: 350px to left child
                        setChildState(0, 1, 2);
                        // Short caption to left child
                        setChildState(0, 2, 1);
                        // Alignment: bottom left to right child
                        setChildState(2, 4, 7);
                    }
                }));
        caseBar.addComponent(new Button("Relative child without expand",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        resetState();
                        // Width 800px
                        setState(sizeBar, 0, 3);
                        // First child 100% wide
                        setChildState(0, 0, 4);
                        // Second child expand 1
                        setChildState(1, 3, 1);
                    }
                }));
        /*
         * Hidden for not to avoid changing screenshots, functionality is still
         * available by adding case=9 to the query string...
         */
        caseBar.getComponent(9).setVisible(false);

        caseBar.setSpacing(true);

        addComponent(caseBar);
        addComponent(sizeBar);
        addComponent(currentLayout);

        getLayout().setSpacing(true);
        getContent().setSizeFull();
        getLayout().setSizeFull();
        getLayout().setExpandRatio(currentLayout, 1);

        String caseParameter = request.getParameter("case");
        if (caseParameter != null) {
            int caseIndex = Integer.parseInt(caseParameter);
            Button button = (Button) caseBar.getComponent(caseIndex);
            button.click();
        }
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
