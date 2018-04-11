package com.vaadin.tests.layouts.layouttester;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

/**
 *
 * Base class for Layout tests.
 */
public abstract class BaseLayoutTestUI extends AbstractReindeerTestUI {
    protected static final String FOLDER_16_PNG = "../icons/runo/16/folder.png";
    protected static final String CALENDAR_32_PNG = "../runo/icons/16/calendar.png";
    protected static final String LOCK_16_PNG = "../runo/icons/16/lock.png";
    protected static final String GLOBE_16_PNG = "../runo/icons/16/globe.png";
    public Alignment[] alignments = { Alignment.TOP_CENTER, Alignment.TOP_LEFT,
            Alignment.TOP_RIGHT, Alignment.BOTTOM_CENTER, Alignment.BOTTOM_LEFT,
            Alignment.BOTTOM_RIGHT, Alignment.MIDDLE_CENTER,
            Alignment.MIDDLE_LEFT, Alignment.MIDDLE_RIGHT };

    public final String[] CAPTIONS = { "", "VeryLongOneWordCaption",
            "Very long caption of 50 approximately symbols aaaaaaaaaaaa aaaaaa aaa " };
    Resource[] ICONS = { new ThemeResource(CALENDAR_32_PNG),
            new ThemeResource(LOCK_16_PNG), new ThemeResource(GLOBE_16_PNG) };
    public AbstractComponent[] components = new AbstractComponent[alignments.length];

    protected AbstractOrderedLayout l1;
    protected AbstractOrderedLayout l2;
    protected Class<? extends AbstractLayout> layoutClass;
    protected VerticalLayout mainLayout = new VerticalLayout();

    public BaseLayoutTestUI(Class<? extends AbstractLayout> layoutClass) {
        super();
        fillComponents();
        this.layoutClass = layoutClass;

    }

    protected void init() {
        try {
            l1 = (AbstractOrderedLayout) layoutClass.newInstance();
            l2 = (AbstractOrderedLayout) layoutClass.newInstance();
            l1.setMargin(false);
            l1.setSpacing(false);
            l2.setMargin(false);
            l2.setSpacing(false);
        } catch (InstantiationException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        if (layoutClass.equals(HorizontalLayout.class)) {
            setLayoutMeasures(l1, l2, "600px", "400px");
        } else if (layoutClass.equals(VerticalLayout.class)) {
            setLayoutMeasures(l1, l2, "400px", "400px");
        } else {
            setDefaultForVertical(l1, l2);
        }
    }

    private void fillComponents() {
        for (int i = 0; i < components.length; i++) {
            String name = "Field" + i;
            TextField field = new TextField();
            field.setValue(name);
            components[i] = field;
        }
    }

    protected AbstractLayout createLabelsFields(
            Class<? extends AbstractComponent> compType) {
        return createLabelsFields(compType, false, null);
    }

    protected void getLayoutForLayoutSizing(final String compType) {

        l2.setSpacing(false);
        l2.setMargin(false);

        final AbstractComponent c1 = getTestTable();
        c1.setSizeFull();
        final AbstractComponent c2 = getTestTable();
        c2.setSizeFull();

        class SetSizeButton extends Button {
            SetSizeButton(final String size) {
                super();
                setCaption("Set size " + size);
                addClickListener(event -> {
                    if (compType == "layout") {
                        l2.setHeight(size);
                        l2.setWidth(size);
                    } else if (compType == "component") {
                        c2.setHeight(size);
                        c2.setWidth(size);
                    }
                });
            }
        }
        Button btn1 = new SetSizeButton("350px");
        Button btn2 = new SetSizeButton("-1px");
        Button btn3 = new SetSizeButton("75%");
        Button btn4 = new SetSizeButton("100%");

        Label spacer = new Label(
                "<div style='height: 1px'></div><hr /><div style='height: 1px'></div>",
                ContentMode.HTML);
        spacer.setWidth("100%");

        l1.addComponent(btn1);
        l1.addComponent(btn2);
        l1.addComponent(btn3);
        l1.addComponent(btn4);
        l2.addComponent(c1);
        l2.addComponent(spacer);
        l2.addComponent(c2);
        l2.setExpandRatio(c1, 0.5f);
        l2.setExpandRatio(c2, 0.5f);

        btn2.addClickListener(event -> {
            Label newLabel = new Label("--- NEW LABEL ---");
            newLabel.setSizeUndefined();
            l2.addComponent(newLabel);
        });
    }

    protected Table getTestTable() {
        Table t = new Table();
        t.setPageLength(5);
        t.addContainerProperty("test", String.class, null);
        t.addItem(new Object[] { "qwertyuiop asdfghjköäxccvbnm,m,." }, 1);
        t.addItem(new Object[] { "YGVYTCTCTRXRXRXRX" }, 2);
        return t;
    }

    protected AbstractLayout createLabelsFields(
            Class<? extends AbstractComponent> compType, boolean useIcon,
            String ErrorMessage) {
        AbstractOrderedLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(false);
        mainLayout.setMargin(false);
        AbstractLayout curLayout = null;
        try {
            curLayout = layoutClass.newInstance();
            if (curLayout instanceof AbstractOrderedLayout) {
                ((AbstractOrderedLayout) curLayout).setMargin(false);
                ((AbstractOrderedLayout) curLayout).setSpacing(false);
            }
        } catch (InstantiationException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        final Component[] components = new Component[CAPTIONS.length];

        for (int i = 0; i < components.length; i++) {
            AbstractComponent comp = null;
            try {
                comp = compType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            components[i] = comp;
            comp.setCaption(CAPTIONS[i]);
            if (useIcon) {
                comp.setIcon(ICONS[i]);
            }
            if (ErrorMessage != null) {
                if (ErrorMessage.isEmpty()) {
                    comp.setComponentError(new UserError(null));
                } else {
                    comp.setComponentError(new UserError(ErrorMessage));
                }
            }
            // if component is a tab sheet add two tabs for it
            if (comp instanceof TabSheet) {
                comp.setSizeUndefined();
                TabSheet tab = (TabSheet) comp;
                tab.addTab(new UndefWideLabel("TAB1"), "TAB1",
                        new ThemeResource(GLOBE_16_PNG));
                tab.addTab(new UndefWideLabel("TAB2"), "TAB2", null);
            }
            curLayout.addComponent(comp);
            mainLayout.addComponent(curLayout);
        }
        return mainLayout;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        mainLayout.setMargin(false);
        mainLayout.setSpacing(false);
        mainLayout.addComponent(l1);
        mainLayout.addComponent(l2);
        addComponent(mainLayout);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return null;
    }

    protected void setLayoutMeasures(AbstractOrderedLayout l1,
            AbstractOrderedLayout l2, String w, String h) {
        l1.setWidth(w);
        l1.setHeight(h);
        l2.setWidth(h);
        l2.setHeight(w);
    }

    protected void setDefaultForVertical(AbstractOrderedLayout l1,
            AbstractOrderedLayout l2) {
        l1.setWidth("800px");
        l1.setHeight("600px");
        l2.setWidth("800px");
        l2.setHeight("600px");
    }

    protected void setDefaultForHorizontal(AbstractOrderedLayout l1,
            AbstractOrderedLayout l2) {
        l1.setWidth("600px");
        l1.setHeight("600px");
        l2.setWidth("600px");
        l2.setHeight("600px");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
