package com.vaadin.tests.layouts.layouttester;

import java.lang.reflect.Method;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
public class LayoutTesterApplication extends AbstractTestCase {
    Button nextButton = new Button("Next");
    private int layoutIndex = -1;

    static final String[] layoutGetters = new String[] { "getCaptionsTests",
            "getIconsTests", "getRequiredErrorIndicatorsTests",
            "getAlignmentTests", "getExpandRatiosTests",
            "getMarginSpacingTests", "getComponentAddReplaceMoveTests",
            "getComponentSizingTests", "getLayoutSizingTests" };
    public static final String NEXT_BUTTON_ID = "nextButton";
    private static final String LAYOUT_SELECT_ID = "layoutSelect";

    private LegacyWindow mainWindow;
    private NativeSelect layoutSelector;

    @Override
    public void init() {
        nextButton.setId(NEXT_BUTTON_ID);
        mainWindow = new LegacyWindow("LayoutTesterApplication");
        setMainWindow(mainWindow);
        nextLayout();

        nextButton.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = -1577298910202253538L;

            @Override
            public void buttonClick(ClickEvent event) {
                nextLayout();
            }
        });
    }

    private void nextLayout() {
        try {
            mainWindow.removeAllComponents();
            HorizontalLayout vlo = new HorizontalLayout();
            vlo.setSpacing(true);
            ++layoutIndex;
            if (layoutIndex >= layoutGetters.length) {
                layoutIndex = 0;
            }
            mainWindow.addComponent(vlo);
            vlo.addComponent(nextButton);
            vlo.addComponent(getLayoutTypeSelect());
            vlo.addComponent(new UndefWideLabel(getLayoutGetterMethod(
                    layoutGetters[layoutIndex]).getName()));

            Layout lo = null;
            if (layoutSelector.getValue() == VerticalLayout.class) {
                lo = getVerticalTestLayout(layoutIndex);
            } else if (layoutSelector.getValue() == HorizontalLayout.class) {
                lo = getHorizontalTestLayout(layoutIndex);
            } else if (layoutSelector.getValue() == GridLayout.class) {
                lo = getGridTestLayout(layoutIndex);
            }
            if (lo != null) {
                lo.addStyleName(Reindeer.LAYOUT_BLUE);
                mainWindow.addComponent(lo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Method getLayoutGetterMethod(String method) {
        try {
            return AbstractLayoutTests.class.getDeclaredMethod(method);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Layout getVerticalTestLayout(int index) throws Exception {
        VerticalLayoutTests vlotest = new VerticalLayoutTests(this);
        return (Layout) getLayoutGetterMethod(layoutGetters[index]).invoke(
                vlotest, (Object[]) null);
    }

    public Layout getHorizontalTestLayout(int index) throws Exception {
        HorizontalLayoutTests hlotest = new HorizontalLayoutTests(this);
        return (Layout) getLayoutGetterMethod(layoutGetters[index]).invoke(
                hlotest, (Object[]) null);
    }

    public Layout getGridTestLayout(int index) throws Exception {
        GridLayoutTests hlotest = new GridLayoutTests(this);
        return (Layout) getLayoutGetterMethod(layoutGetters[index]).invoke(
                hlotest, (Object[]) null);
    }

    private NativeSelect getLayoutTypeSelect() {
        if (layoutSelector == null) {
            layoutSelector = new NativeSelect();
            layoutSelector.setId(LAYOUT_SELECT_ID);
            layoutSelector.addItem(VerticalLayout.class);
            layoutSelector.addItem(HorizontalLayout.class);
            layoutSelector.addItem(GridLayout.class);
            layoutSelector.setNullSelectionAllowed(false);
            layoutSelector.setImmediate(true);
            layoutSelector.select(VerticalLayout.class);
            layoutSelector.addListener(new Property.ValueChangeListener() {
                private static final long serialVersionUID = -605319614765838359L;

                @Override
                public void valueChange(ValueChangeEvent event) {
                    layoutIndex = -1;
                    nextLayout();
                }
            });
        }
        return layoutSelector;
    }

    @Override
    protected String getDescription() {
        return "Test application for VerticalLayout, HorizontalLayout, and GridLayout";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5334;
    }

}
