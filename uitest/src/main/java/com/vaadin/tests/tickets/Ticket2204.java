package com.vaadin.tests.tickets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.AbstractSplitPanel;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class Ticket2204 extends LegacyApplication {

    private final List<RichTextArea> textAreas = new ArrayList<RichTextArea>();
    private TabSheet ts;
    private final Map<Component, Component> containerToComponent = new HashMap<Component, Component>();
    private RichTextArea rta;
    private final List<Class<? extends Component>> classes = new ArrayList<Class<? extends Component>>();
    protected RichTextArea formTextArea;

    @Override
    public void init() {
        classes.add(VerticalLayout.class);
        classes.add(HorizontalLayout.class);
        classes.add(GridLayout.class);
        classes.add(Accordion.class);
        classes.add(TabSheet.class);
        classes.add(Panel.class);
        classes.add(VerticalSplitPanel.class);
        classes.add(HorizontalSplitPanel.class);
        classes.add(Form.class);

        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((AbstractOrderedLayout) w.getContent());
    }

    private void createUI(AbstractOrderedLayout layout) {
        ts = new TabSheet();
        layout.addComponent(ts);

        for (Class<? extends Component> c : classes) {
            ts.addTab(createComponent(c), c.getSimpleName(), null);
        }
        rta = new RichTextArea();
        rta.setVisible(false);
        ts.addTab(rta, "Hidden rta", null);

        Button b = new Button("Show area", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                showHide();
            }
        });

        layout.addComponent(b);

        b = new Button("Show tab", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                showTab();
            }
        });

        layout.addComponent(b);

    }

    protected void showTab() {
        rta.setVisible(!rta.isVisible());

    }

    protected void showHide() {
        Component c = containerToComponent.get(ts.getSelectedTab());
        c.setVisible(!c.isVisible());
    }

    private Component createComponent(Class<? extends Component> c) {
        RichTextArea textArea = new RichTextArea();
        textArea.setVisible(false);
        textArea.setCaption("This is the textArea");
        textArea.setWidth("200px");
        textArea.setHeight("100px");
        textAreas.add(textArea);
        Component cc = null;

        try {
            cc = c.newInstance();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        // if (c == OrderedLayout.class) {
        // cc = new VerticalLayout();
        // } else
        if (c == Accordion.class) {
            // Label l = new Label("Filler");
            // l.setCaption("Filler label");
            // cc.addComponent(l);
        }

        if (c == Form.class) {
            Form f = (Form) cc;
            f.setFormFieldFactory(new FormFieldFactory() {

                @Override
                public Field<?> createField(Item item, Object propertyId,
                        Component uiContext) {
                    formTextArea = new RichTextArea();
                    formTextArea.setVisible(false);
                    return formTextArea;
                }

            });
            f.setItemDataSource(new BeanItem<Object>(new Object() {
                private int a;

                @SuppressWarnings("unused")
                public int getA() {
                    return a;
                }

                @SuppressWarnings("unused")
                public void setA(int a) {
                    this.a = a;
                }
            }));
            containerToComponent.put(f, formTextArea);
            return f;
        }
        containerToComponent.put(cc, textArea);
        if (cc instanceof ComponentContainer) {
            ((ComponentContainer) cc).addComponent(textArea);
        }

        if (AbstractSplitPanel.class.isAssignableFrom(c)) {
            AbstractSplitPanel sp = (AbstractSplitPanel) cc;
            sp.setWidth("300px");
            sp.setHeight("300px");
            sp.addComponent(new Label("Label"));
            textArea.setSizeFull();
        }
        if (c == Panel.class) {
            VerticalLayout layout = new VerticalLayout();
            layout.setMargin(true);
            ((Panel) cc).setContent(layout);
            containerToComponent.put(cc, layout);
            layout.setVisible(false);
            textArea.setVisible(true);
            return cc;
        }

        return cc;
    }
}
