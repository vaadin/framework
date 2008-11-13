package com.itmill.toolkit.tests.tickets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.Accordion;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.ComponentContainer;
import com.itmill.toolkit.ui.CoordinateLayout;
import com.itmill.toolkit.ui.Field;
import com.itmill.toolkit.ui.FieldFactory;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.RichTextArea;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class Ticket2204 extends Application {

    private List<RichTextArea> textAreas = new ArrayList<RichTextArea>();
    private TabSheet ts;
    private Map<Component, Component> containerToComponent = new HashMap<Component, Component>();
    private RichTextArea rta;
    private List<Class<? extends Component>> classes = new ArrayList<Class<? extends Component>>();
    protected RichTextArea formTextArea;

    public void init() {
        classes.add(OrderedLayout.class);
        classes.add(GridLayout.class);
        classes.add(Accordion.class);
        classes.add(TabSheet.class);
        classes.add(Panel.class);
        classes.add(CoordinateLayout.class);
        classes.add(SplitPanel.class);
        classes.add(Form.class);

        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((OrderedLayout) w.getLayout());
    }

    private void createUI(OrderedLayout layout) {
        ts = new TabSheet();
        layout.addComponent(ts);

        for (Class c : classes) {
            ts.addTab(createComponent(c), c.getSimpleName(), null);
        }
        rta = new RichTextArea();
        rta.setVisible(false);
        ts.addTab(rta, "Hidden rta", null);

        Button b = new Button("Show area", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                showHide();
            }
        });

        layout.addComponent(b);

        b = new Button("Show tab", new ClickListener() {

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

    private Component createComponent(Class c) {
        RichTextArea textArea = new RichTextArea();
        textArea.setVisible(false);
        textArea.setCaption("This is the textArea");
        textArea.setWidth("200px");
        textArea.setHeight("100px");
        textAreas.add(textArea);
        Component cc = null;

        try {
            cc = (Component) c.newInstance();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        // if (c == OrderedLayout.class) {
        // cc = new OrderedLayout();
        // } else
        if (c == Accordion.class) {
            // Label l = new Label("Filler");
            // l.setCaption("Filler label");
            // cc.addComponent(l);
        }
        if (c == CoordinateLayout.class) {
            ((Sizeable) cc).setHeight("100px");
        }

        if (c == Form.class) {
            Form f = (Form) cc;
            f.setFieldFactory(new FieldFactory() {

                public Field createField(Class type, Component uiContext) {
                    return createField();
                }

                public Field createField(Property property, Component uiContext) {
                    return createField();
                }

                public Field createField(Item item, Object propertyId,
                        Component uiContext) {
                    return createField();
                }

                private Field createField() {
                    formTextArea = new RichTextArea();
                    formTextArea.setVisible(false);
                    return formTextArea;
                }

                public Field createField(Container container, Object itemId,
                        Object propertyId, Component uiContext) {
                    return createField();
                }

            });
            f.setItemDataSource(new BeanItem(new Object() {
                private int a;

                public int getA() {
                    return a;
                }

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

        if (c == SplitPanel.class) {
            SplitPanel sp = (SplitPanel) cc;
            sp.setWidth("300px");
            sp.setHeight("300px");
            sp.addComponent(new Label("Label"));
        }
        if (c == Panel.class) {
            Layout layout = ((Panel) cc).getLayout();
            containerToComponent.put(cc, layout);
            layout.setVisible(false);
            textArea.setVisible(true);
            return cc;
        }

        return cc;
    }
}
