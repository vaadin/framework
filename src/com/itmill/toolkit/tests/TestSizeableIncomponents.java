/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.AbstractComponent;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.ComponentContainer;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class TestSizeableIncomponents extends Application {

    private IndexedContainer cont;
    private ComboBox select;
    private Button prev;
    private Button next;
    private Panel testPanel;

    public void init() {

        initComponentList();

        Window w = new Window();
        setMainWindow(w);
        w.setTheme("demo");

        final ExpandLayout main = new ExpandLayout();
        w.setLayout(main);

        select = new ComboBox();
        select.setImmediate(true);
        select.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
        select.setWidth("400px");

        prev = new Button("<<-|");
        prev.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                Object cur = select.getValue();
                Testable prev = (Testable) cont.prevItemId(cur);
                if (prev == null) {
                    getMainWindow().showNotification("No more test cases");
                } else {
                    getMainWindow().showNotification(
                            "Selected test:" + prev.getTestableName());
                    select.setValue(prev);
                    select.requestRepaint();
                }
            }
        });
        next = new Button("|->>");
        next.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                Object cur = select.getValue();
                Testable next = (Testable) cont.nextItemId(cur);
                if (next == null) {
                    getMainWindow().showNotification("No more test cases");
                } else {
                    getMainWindow().showNotification(
                            "Selected test:" + next.getTestableName());
                    select.setValue(next);
                    select.requestRepaint();
                }
            }
        });

        OrderedLayout controllers = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        controllers.addComponent(prev);
        controllers.addComponent(select);
        controllers.addComponent(next);
        main.addComponent(controllers);

        select.setContainerDataSource(cont);
        select.addListener(new ComboBox.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                Testable t = (Testable) select.getValue();
                if (t != null) {
                    testPanel.removeAllComponents();
                    try {
                        Component c = t.getComponent();
                        if (c != null) {
                            testPanel.addComponent(c);
                        }
                    } catch (InstantiationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        testPanel = new Panel();
        testPanel.setSizeFull();
        testPanel.setLayout(new ExpandLayout());
        testPanel.setStyleName("testable");
        main.addComponent(testPanel);
        main.expand(testPanel);

    }

    private void initComponentList() {
        cont = new IndexedContainer();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        URL dir = cl.getResource("com/itmill/toolkit/ui");

        String[] list2 = (new File(dir.getFile())).list();
        for (int i = 0; i < list2.length; i++) {
            String f = list2[i];
            if (f.endsWith(".class") && (f.indexOf("CustomComponent") == -1)
                    && (f.indexOf("Window") == -1)) {
                f = f.replaceAll(".class", "");
                String className = "com.itmill.toolkit.ui." + f;
                Class c;
                try {
                    c = Class.forName(className);
                    Object o = c.newInstance();
                    if (o instanceof Component) {
                        Testable t = new Testable(c);
                        cont.addItem(t);
                        t = new Testable(c);
                        t.addConfiguration(new Configuration("100px*100px") {
                            void configure(Component c) {
                                c.setWidth(60);
                                c.setHeight(60);
                            }
                        });
                        t = new Testable(c);
                        t.addConfiguration(new Configuration("Width 50em") {
                            void configure(Component c) {
                                c.setWidth("50em");
                            }
                        });
                        cont.addItem(t);
                        t = new Testable(c);
                        t.addConfiguration(new Configuration("Height 7cm") {
                            void configure(Component c) {
                                c.setHeight("7cm");
                            }
                        });
                        cont.addItem(t);
                        t = new Testable(c) {
                            public Component getComponent()
                                    throws InstantiationException,
                                    IllegalAccessException {

                                Component c = super.getComponent();

                                Panel p = new Panel(
                                        "Wrapper panel (400px*400px)");
                                p.setLayout(new ExpandLayout());
                                p.setWidth("400px");
                                p.setHeight("400px");
                                p.addComponent(c);
                                p.addStyleName("testablew");
                                p.addStyleName("testable");
                                return p;
                            }

                        };
                        t.addConfiguration(new Configuration("100%*100%") {
                            void configure(Component c) {
                                c.setSizeFull();
                            }

                        });
                        cont.addItem(t);
                    }

                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                } catch (InstantiationException e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                }
            }
        }

    }

    class Testable {

        private Class classToTest;
        private ArrayList configurations = new ArrayList();

        Testable(Class c) {
            classToTest = c;
        }

        public void addConfiguration(Configuration conf) {
            configurations.add(conf);
        }

        public String getTestableName() {
            StringBuffer sb = new StringBuffer();
            sb.append(classToTest.getName().replaceAll(
                    "com.itmill.toolkit.ui.", ""));
            sb.append("[");
            for (Iterator i = configurations.iterator(); i.hasNext();) {
                sb.append(((Configuration) i.next()).getDescription());
                if (i.hasNext()) {
                    sb.append(",");
                }
            }
            sb.append("]");

            return sb.toString();
        }

        /**
         * Instantiates and populates component with test data to be ready for
         * testing.
         * 
         * @return
         * @throws InstantiationException
         * @throws IllegalAccessException
         */
        public Component getComponent() throws InstantiationException,
                IllegalAccessException {
            Component c = (Component) classToTest.newInstance();

            if (c instanceof Button) {
                ((AbstractComponent) c).setCaption("test");
            }
            if (AbstractSelect.class.isAssignableFrom(c.getClass())) {
                if (c instanceof Table) {
                    Table new_name = (Table) c;
                    new_name
                            .setContainerDataSource(TestForTablesInitialColumnWidthLogicRendering
                                    .getTestTable(5, 100)
                                    .getContainerDataSource());

                } else {
                    AbstractSelect new_name = (AbstractSelect) c;
                    Container cont = TestForTablesInitialColumnWidthLogicRendering
                            .getTestTable(2, 8).getContainerDataSource();
                    new_name.setContainerDataSource(cont);
                    new_name.setItemCaptionPropertyId(cont
                            .getContainerPropertyIds().iterator().next());

                }
            } else if (c instanceof ComponentContainer) {
                ComponentContainer new_name = (ComponentContainer) c;
                new_name
                        .addComponent(new Label("component 1 in test container"));
                new_name.addComponent(new Button("component 2"));
            } else if (c instanceof Embedded) {
                Embedded em = (Embedded) c;
                em.setSource(new ThemeResource("test.png"));
            } else if (c instanceof Label) {
                ((Label) c).setValue("Test label");
            }

            for (Iterator i = configurations.iterator(); i.hasNext();) {
                Configuration conf = (Configuration) i.next();
                conf.configure(c);
            }
            return c;
        }

        public String toString() {
            return getTestableName();
        }
    }

    public abstract class Configuration {

        private String description = "";

        Configuration(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        abstract void configure(Component c);

        public String toString() {
            return getDescription();
        }

    }
}
