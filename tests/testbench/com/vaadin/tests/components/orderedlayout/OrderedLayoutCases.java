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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;

public class OrderedLayoutCases extends AbstractTestRoot {
    private static final String[] dimensionValues = { "-1px", "5px", "300px",
            "800px", "100%", "50%" };

    private static class SampleChild extends VerticalLayout {
        public SampleChild() {
            setStyleName("showBorders");
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

    @Override
    protected void setup(WrappedRequest request) {
        TestUtils.injectCSS(getRoot(),
                ".showBorders {border: 1px solid black};");

        currentLayout = new HorizontalLayout();
        for (int i = 0; i < 3; i++) {
            currentLayout.addComponent(new SampleChild());
        }

        HorizontalLayout sizeBar = new HorizontalLayout();
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
                        newLayout.setStyleName("showBorders");

                        newLayout.setHeight(currentLayout.getHeight(),
                                currentLayout.getHeightUnits());
                        newLayout.setWidth(currentLayout.getWidth(),
                                currentLayout.getWidthUnits());

                        getLayout().replaceComponent(currentLayout, newLayout);
                        currentLayout = newLayout;
                    }
                }, "Horizontal", "Vertical"));

        addComponent(sizeBar);
        addComponent(currentLayout);

        getLayout().setSpacing(true);
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
    protected String getTestDescription() {
        return "Tester application for exploring how Horizontal/VerticalLayout reacts to various settings ";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
